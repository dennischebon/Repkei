package com.example.ripkei.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "connection_logs")
data class ConnectionLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sourceIp: String,
    val destinationIp: String,
    val destinationPort: Int,
    val protocol: String,
    val appName: String?,
    val packageName: String?,
    val domainName: String? = null,
    val country: String? = null,
    val city: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val bytesSent: Long = 0,
    val bytesReceived: Long = 0,
    val isActive: Boolean = true,
    val duration: Long = 0
)
