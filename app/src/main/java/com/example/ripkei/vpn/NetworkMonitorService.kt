package com.example.ripkei.vpn

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import com.example.ripkei.domain.model.ConnectionLog
import com.example.ripkei.domain.repository.ConnectionRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.InetAddress
import java.nio.ByteBuffer
import javax.inject.Inject

@AndroidEntryPoint
class NetworkMonitorService : VpnService() {

    @Inject
    lateinit var connectionRepository: ConnectionRepository

    private var vpnInterface: ParcelFileDescriptor? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isRunning = false
    private lateinit var packetForwarder: PacketForwarder

    override fun onCreate() {
        super.onCreate()
        packetForwarder = PacketForwarder(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP") {
            stopVpn()
            return START_NOT_STICKY
        }
        
        if (!isRunning) {
            startVpn()
        }
        return START_STICKY
    }

    private fun startVpn() {
        isRunning = true
        val builder = Builder()
            .setSession("Ripkei Network Monitor")
            .addAddress("10.0.0.2", 32)
            .addRoute("0.0.0.0", 0)
            .setMtu(1500)
            .addDisallowedApplication(packageName) // Prevent loopback
        
        // In a real implementation, we would also add DNS servers
        // builder.addDnsServer("8.8.8.8")

        vpnInterface = builder.establish()
        
        if (vpnInterface != null) {
            serviceScope.launch {
                runVpnLoop()
            }
        }
    }

    private suspend fun runVpnLoop() {
        val inputStream = FileInputStream(vpnInterface?.fileDescriptor)
        val outputStream = FileOutputStream(vpnInterface?.fileDescriptor)
        val buffer = ByteBuffer.allocate(32767)

        while (isActive && isRunning) {
            val length = withContext(Dispatchers.IO) {
                try {
                    inputStream.read(buffer.array())
                } catch (e: Exception) {
                    -1
                }
            }

            if (length > 0) {
                // Parse packet header
                parsePacket(buffer.array(), length)
                
                // Forward the packet
                buffer.position(0)
                buffer.limit(length)
                packetForwarder.processPacket(buffer, length)
            } else if (length < 0) {
                break
            }
            buffer.clear()
        }
    }

    private fun parsePacket(packet: ByteArray, length: Int) {
        // Simplified IP packet parsing
        if (length < 20) return
        
        val version = (packet[0].toInt() shr 4) and 0x0F
        if (version != 4) return // Only IPv4 for now
        
        val protocol = packet[9].toInt() and 0xFF
        val srcIp = InetAddress.getByAddress(packet.sliceArray(12..15)).hostAddress
        val dstIp = InetAddress.getByAddress(packet.sliceArray(16..19)).hostAddress
        
        // Resolve App Name using UID if possible (Requires modern Android)
        // In a real implementation, we'd use ConnectionTracker or similar.
        val packageName = "com.example.app" 
        val appName = "System Process" 

        var dstPort = 0
        var protocolName = "Unknown"
        
        when (protocol) {
            6 -> { // TCP
                protocolName = "TCP"
                dstPort = ((packet[22].toInt() and 0xFF) shl 8) or (packet[23].toInt() and 0xFF)
            }
            17 -> { // UDP
                protocolName = "UDP"
                dstPort = ((packet[22].toInt() and 0xFF) shl 8) or (packet[23].toInt() and 0xFF)
            }
            else -> {
                protocolName = protocol.toString()
            }
        }

        if (dstIp != "10.0.0.2") {
            Log.d("RipkeiVPN", "Connection: $srcIp -> $dstIp:$dstPort ($protocolName)")
            // Save to database
            serviceScope.launch {
                connectionRepository.logConnection(
                    ConnectionLog(
                        sourceIp = srcIp ?: "",
                        destinationIp = dstIp ?: "",
                        destinationPort = dstPort,
                        protocol = protocolName,
                        appName = appName,
                        packageName = packageName,
                        bytesSent = length.toLong()
                    )
                )
            }
        }
    }

    private fun stopVpn() {
        isRunning = false
        vpnInterface?.close()
        vpnInterface = null
        serviceScope.cancel()
        stopSelf()
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }
}
