package com.spidertracker.app.presentation.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spidertracker.app.ui.theme.SpiderRed

@Composable
fun HomeScreen(
    onStartTracking: () -> Unit,
    onDemoMode: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Animated Web Background
        WebBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.95f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )

            Text(
                text = "🕷️",
                fontSize = 80.sp,
                modifier = Modifier.scale(scale)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SPIDER-MAN\nTRACKER",
                style = MaterialTheme.typography.headlineLarge,
                color = SpiderRed,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )

            Text(
                text = "Can you find him?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(64.dp))

            Button(
                onClick = onStartTracking,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SpiderRed)
            ) {
                Text("🕷️ START TRACKING", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onDemoMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
            ) {
                Text("🎮 DEMO MODE", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Just for fun — all sightings are fictional.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun WebBackground() {
    val color = SpiderRed.copy(alpha = 0.1f)
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = center
        val radius = size.minDimension / 1.5f
        
        // Draw radial lines
        for (i in 0 until 8) {
            val angle = (i * 45).toDouble()
            val x = center.x + radius * Math.cos(Math.toRadians(angle)).toFloat()
            val y = center.y + radius * Math.sin(Math.toRadians(angle)).toFloat()
            drawLine(
                color = color,
                start = center,
                end = androidx.compose.ui.geometry.Offset(x, y),
                strokeWidth = 2f
            )
        }
        
        // Draw concentric circles
        for (i in 1..4) {
            drawCircle(
                color = color,
                radius = radius * (i / 4f),
                style = Stroke(width = 2f)
            )
        }
    }
}
