package com.ecom.app.ui.layouts

import androidx.compose.runtime.Composable
import com.ecom.app.ui.navigations.AppScreen
import com.ecom.app.ui.components.TopMenu

@Composable
fun AppTopBar(
    currentScreen: AppScreen,
    cartCount: Int,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onLogoClick: () -> Unit,
    onProfileClick: () -> Unit,
    onCartClick: () -> Unit
) {
    if (!currentScreen.isFullScreen()) {
        TopMenu(
            cartCount = cartCount,
            onMenuClick = onMenuClick,
            onSearchClick = onSearchClick,
            onLogoClick = onLogoClick,
            onProfileClick = onProfileClick,
            onCartClick = onCartClick
        )
    }
}