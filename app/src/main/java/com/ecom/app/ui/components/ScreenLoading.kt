package com.ecom.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale

import coil.compose.AsyncImage
import com.ecom.app.BuildConfig

@Composable
fun ScreenLoading(
    message: String = "Loading..."
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_loading")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(850),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = BuildConfig.LOGO_FULL_IMAGE,
            contentDescription = "Loading",
            modifier = Modifier
                .width(170.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.height(18.dp))

        Text(
            text = message,
            fontSize = 15.sp,
            color = Color.DarkGray
        )
    }
}