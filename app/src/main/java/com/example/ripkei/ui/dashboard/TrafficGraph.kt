package com.example.ripkei.ui.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ripkei.ui.theme.CyberBlue
import com.example.ripkei.ui.theme.CyberPink
import kotlin.random.Random

@Composable
fun TrafficGraph() {
    val points = remember { mutableStateListOf<Float>() }
    var lastPoint by remember { mutableFloatStateOf(0.5f) }
    
    // Simulate real-time data
    LaunchedEffect(Unit) {
        while (true) {
            val next = (lastPoint + (Random.nextFloat() - 0.5f) * 0.2f).coerceIn(0.1f, 0.9f)
            points.add(next)
            if (points.size > 50) points.removeAt(0)
            lastPoint = next
            kotlinx.coroutines.delay(200)
        }
    }

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
    ) {
        if (points.size < 2) return@Canvas
        
        val path = Path()
        val stepX = size.width / 50f
        
        path.moveTo(0f, size.height * (1f - points[0]))
        
        points.forEachIndexed { index, point ->
            path.lineTo(index * stepX, size.height * (1f - point))
        }
        
        drawPath(
            path = path,
            color = CyberBlue,
            style = Stroke(width = 2.dp.toPx())
        )
        
        // Fill area
        val fillPath = Path().apply {
            addPath(path)
            lineTo(points.size * stepX, size.height)
            lineTo(0f, size.height)
            close()
        }
        
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(CyberBlue.copy(alpha = 0.3f), Color.Transparent)
            )
        )
    }
}
