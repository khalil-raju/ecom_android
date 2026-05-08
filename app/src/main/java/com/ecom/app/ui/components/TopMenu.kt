package com.ecom.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ecom.app.BuildConfig
import com.ecom.app.R

@Composable
fun TopMenu(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onLogoClick: () -> Unit
) {

    val logoUrl = BuildConfig.LOGO_FULL_IMAGE

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .background(Color.Black)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // LEFT
        Box(
            modifier = Modifier.width(40.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (showBackButton) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBackClick() }
                )
            }
        }

        // CENTER LOGO
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = logoUrl,
                contentDescription = "BrandLogo",
                modifier = Modifier
                    .width(160.dp)
                    .clickable { onLogoClick() },
                contentScale = ContentScale.Fit
            )
        }

        // RIGHT SEARCH
        Box(
            modifier = Modifier.width(40.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(22.dp)
                    .clickable { onSearchClick() }
            )
        }
    }
}