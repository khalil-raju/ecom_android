package com.ecom.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ecom.app.BuildConfig

@Composable
fun HeroBanner() {
    AsyncImage(
        model = BuildConfig.HERO_IMAGE,
        contentDescription = "Hero Banner",
        modifier = Modifier
            .fillMaxWidth(),
        contentScale = ContentScale.FillWidth
    )
}
