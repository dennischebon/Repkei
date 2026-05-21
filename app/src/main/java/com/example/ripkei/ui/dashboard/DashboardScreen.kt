package com.example.ripkei.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ripkei.domain.model.ConnectionLog
import com.example.ripkei.ui.MainViewModel
import com.example.ripkei.ui.theme.CyberBlue
import com.example.ripkei.ui.theme.CyberPink
import com.example.ripkei.ui.theme.CyberGreen
import com.example.ripkei.ui.theme.SurfaceColor
import com.example.ripkei.ui.theme.GridColor

@Composable
fun DashboardScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val logs by viewModel.filteredLogs.collectAsState()
    val activeCount by viewModel.activeConnections.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "NETWORK INTELLIGENCE",
            style = MaterialTheme.typography.headlineMedium,
            color = CyberBlue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Button(
            onClick = { /* TODO: Toggle VPN */ },
            colors = ButtonDefaults.buttonColors(containerColor = CyberPink),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Text("TOGGLE MONITORING", color = Color.White)
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            placeholder = { Text("Search connections...", color = Color.Gray) },
            colors = TextFieldDefaults.colors(
                focusedTextColor = CyberBlue,
                unfocusedTextColor = CyberBlue,
                focusedContainerColor = SurfaceColor,
                unfocusedContainerColor = SurfaceColor,
                focusedIndicatorColor = CyberPink,
                unfocusedIndicatorColor = GridColor
            ),
            shape = RoundedCornerShape(8.dp)
        )
        
        DashboardStats(activeCount.size, logs.size)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        GlobeVisualization()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TrafficGraph()
        
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "TOP APPLICATIONS",
            style = MaterialTheme.typography.labelLarge,
            color = CyberPink,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        val topApps: Map<String, Int> by viewModel.topApps.collectAsState()
        topApps.forEach { (name, count) ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                Text(name, color = Color.LightGray, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text(count.toString(), color = CyberBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "LIVE CONNECTIONS",
            style = MaterialTheme.typography.labelLarge,
            color = CyberPink,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(logs) { log ->
                ConnectionItem(log, viewModel)
            }
        }
    }
}

@Composable
fun DashboardStats(active: Int, total: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard("ACTIVE", active.toString(), CyberBlue, Modifier.weight(1f))
        StatCard("TOTAL LOGS", total.toString(), CyberPink, Modifier.weight(1f))
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceColor)
            .padding(16.dp)
    ) {
        Column {
            Text(label, color = color.copy(alpha = 0.7f), fontSize = 10.sp)
            Text(value, color = color, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ConnectionItem(log: ConnectionLog, viewModel: MainViewModel) {
    val riskLevel = viewModel.getRiskLevel(log)
    val indicatorColor = when (riskLevel) {
        com.example.ripkei.domain.security.RiskLevel.HIGH -> CyberPink
        com.example.ripkei.domain.security.RiskLevel.MEDIUM -> Color.Yellow
        com.example.ripkei.domain.security.RiskLevel.LOW -> CyberGreen
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(indicatorColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.destinationIp,
                    color = CyberBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "${log.protocol} : ${log.destinationPort}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${log.bytesSent} B",
                    color = CyberPink,
                    fontSize = 12.sp
                )
                Text(
                    text = log.packageName ?: "Unknown",
                    color = Color.DarkGray,
                    fontSize = 10.sp
                )
            }
        }
    }
}
