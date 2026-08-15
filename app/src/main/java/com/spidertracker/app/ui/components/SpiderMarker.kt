package com.spidertracker.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spidertracker.app.domain.model.SpiderActivity

@Composable
fun SpiderMarker(activity: SpiderActivity) {
    val infiniteTransition = rememberInfiniteTransition(label = "spider")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .scale(scale)
            .rotate(if (activity == SpiderActivity.SWINGING) rotation * 2 else rotation),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "🕷️", fontSize = 32.sp)
    }
}
