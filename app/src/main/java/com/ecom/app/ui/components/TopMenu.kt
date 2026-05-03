package com.ecom.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import coil.compose.AsyncImage

import com.ecom.app.BuildConfig
import com.ecom.app.R
import kotlin.toString

@Composable
fun TopMenu(
    cartCount: Int = 0,
    onMenuClick: () -> Unit,
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_menu),
            contentDescription = "Menu",
            tint = Color.Unspecified,
            modifier = Modifier.size(28.dp).clickable { onMenuClick() }
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "Search",
            tint = Color.Unspecified,
            modifier = Modifier.size(26.dp).clickable { onSearchClick() }
        )

        AsyncImage(
            model = logoUrl,
            contentDescription = "BrandLogo",
            modifier = Modifier
                .width(150.dp)
                .height(42.dp)
                .clickable { onLogoClick() },
            contentScale = ContentScale.Fit
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_profile),
            contentDescription = "Profile",
            tint = Color.Unspecified,
            modifier = Modifier.size(27.dp)
        )

        BadgedBox(
            badge = {
                Badge(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ) {
                    Text(
                        text = cartCount.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bag),
                contentDescription = "Cart",
                tint = Color.Unspecified,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
