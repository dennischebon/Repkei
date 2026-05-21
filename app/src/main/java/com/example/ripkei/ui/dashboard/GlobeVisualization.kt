package com.example.ripkei.ui.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ripkei.ui.theme.CyberBlue
import com.example.ripkei.ui.theme.CyberPink
import com.example.ripkei.ui.theme.GridColor
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GlobeVisualization() {
    val infiniteTransition = rememberInfiniteTransition(label = "globe")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2.5f

        // Draw grid
        for (i in 0 until 12) {
            val angle = (i * 30 + rotation) * (Math.PI / 180).toFloat()
            val x = center.x + radius * cos(angle.toDouble()).toFloat()
            val y = center.y + radius * sin(angle.toDouble()).toFloat()
            
            drawLine(
                color = GridColor,
                start = center,
                end = Offset(x, y),
                strokeWidth = 1f
            )
        }

        drawCircle(
            color = CyberBlue,
            radius = radius,
            center = center,
            style = Stroke(width = 2f)
        )
        
        // Draw some "connection nodes"
        val nodes = listOf(
            Offset(center.x + radius * 0.5f, center.y - radius * 0.3f),
            Offset(center.x - radius * 0.7f, center.y + radius * 0.2f),
            Offset(center.x + radius * 0.2f, center.y + radius * 0.6f)
        )
        
        nodes.forEach { node ->
            drawCircle(
                color = CyberPink,
                radius = 4.dp.toPx(),
                center = node
            )
            
            drawLine(
                color = CyberPink.copy(alpha = 0.5f),
                start = center,
                end = node,
                strokeWidth = 2f
            )
        }
    }
}
