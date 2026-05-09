package com.ecom.app.ui.layouts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ecom.app.ui.components.BottomMenu
import com.ecom.app.ui.components.TopMenu
import com.ecom.app.ui.navigations.AppScreen

@Composable
fun AppScaffold(
    isSearchDrawerOpen: Boolean,
    drawerContentType: DrawerContentType,
    onCloseDrawer: () -> Unit,

    currentScreen: AppScreen,

    onBackClick: () -> Unit,
    onLogoClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSearchSubmit: (String) -> Unit,

    onHomeClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onWishlistClick: () -> Unit,
    cartCount: Int,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,

    content: @Composable (PaddingValues) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                if (!currentScreen.isFullScreen()) {
                    TopMenu(
                        showBackButton = currentScreen != AppScreen.Home,
                        onBackClick = onBackClick,
                        onSearchClick = onSearchClick,
                        onLogoClick = onLogoClick
                    )
                }
            },
            bottomBar = {
                if (!currentScreen.isFullScreen()) {
                    BottomMenu(
                        currentScreen = currentScreen,
                        cartCount = cartCount,
                        onHomeClick = onHomeClick,
                        onOrdersClick = onOrdersClick,
                        onWishlistClick = onWishlistClick,
                        onCartClick = onCartClick,
                        onProfileClick = onProfileClick
                    )
                }
            }
        ) { innerPadding ->
            content(innerPadding)
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterEnd
        ) {
            AnimatedVisibility(
                visible = isSearchDrawerOpen,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(durationMillis = 420)
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(durationMillis = 360)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(320.dp)
                        .background(Color.White)
                ) {
                    AppDrawer(
                        drawerContentType = drawerContentType,
                        onClose = onCloseDrawer,
                        onSearchSubmit = onSearchSubmit
                    )
                }
            }
        }
    }
}