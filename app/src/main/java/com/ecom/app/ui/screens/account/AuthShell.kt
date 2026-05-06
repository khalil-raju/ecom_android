package com.ecom.app.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ecom.app.BuildConfig

@Composable
fun AuthShell(
    onLogoClick: () -> Unit,
    content: @Composable () -> Unit
) {

    val logoUrl = BuildConfig.LOGO_FULL_IMAGE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 34.dp)
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        AsyncImage(
            model = logoUrl,
            contentDescription = "BrandLogo",
            modifier = Modifier
                .width(200.dp)
                .clickable { onLogoClick() },
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(42.dp))

        Card(
            modifier = Modifier.width(340.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF0F0F0)
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.Center
            ) {
                content()
            }
        }
    }
}
