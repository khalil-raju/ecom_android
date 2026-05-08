package com.ecom.app.ui.layouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ecom.app.ui.components.BottomMenu
import com.ecom.app.ui.components.SideMenuCategory
import com.ecom.app.ui.components.TopMenu
import com.ecom.app.ui.navigations.AppScreen

@Composable
fun AppScaffold(
    /* Drawer */
    drawerState: DrawerState,
    drawerContentType: DrawerContentType,
    onCloseDrawer: () -> Unit,

    /* AppScreen */
    currentScreen: AppScreen,

    /* Top Menu */
    onBackClick: () -> Unit,
    onLogoClick: () -> Unit,
    onSearchClick: () -> Unit,

    /* Bottom Menu */
    onHomeClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onWishlistClick: () -> Unit,
    cartCount: Int,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,

    content: @Composable (PaddingValues) -> Unit
) {
    Box(modifier = Modifier.safeDrawingPadding()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                AppDrawer(
                    drawerContentType = drawerContentType,
                    onClose = onCloseDrawer
                )
            }
        ) {
            Scaffold(
                topBar = {
                    if (!currentScreen.isFullScreen()) {
                        val showBackButton = currentScreen != AppScreen.Home
                        TopMenu(
                            showBackButton = showBackButton,
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
                            onCategoriesClick = onCategoriesClick,
                            onWishlistClick = onWishlistClick,
                            onCartClick = onCartClick,
                            onProfileClick = onProfileClick
                        )
                    }
                }
            ) { innerPadding ->
                content(innerPadding)
            }
        }
    }
}