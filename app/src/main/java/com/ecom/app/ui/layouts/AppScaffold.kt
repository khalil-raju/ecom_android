package com.ecom.app.ui.layouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ecom.app.ui.navigations.AppScreen

@Composable
fun AppScaffold(
    drawerState: DrawerState,
    drawerContentType: DrawerContentType,
    currentScreen: AppScreen,
    isAuthenticated: Boolean,
    cartCount: Int,
    onCloseDrawer: () -> Unit,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onLogoClick: () -> Unit,
    onProfileClick: () -> Unit,
    onCartClick: () -> Unit,
    onHomeClick: () -> Unit,
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
    onCategoryClick: (parentSlug: String, childSlug: String?) -> Unit,
    onOrdersClick: () -> Unit,
    onWalletClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onReturnsClick: () -> Unit,
    onContactClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Box(modifier = Modifier.safeDrawingPadding()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                AppDrawer(
                    drawerContentType = drawerContentType,
                    isAuthenticated = isAuthenticated,
                    onClose = onCloseDrawer,
                    onHomeClick = onHomeClick,
                    onLoginClick = onLoginClick,
                    onSignupClick = onSignupClick,
                    onCategoryClick = onCategoryClick,
                    onProfileClick = onProfileClick,
                    onOrdersClick = onOrdersClick,
                    onWalletClick = onWalletClick,
                    onWishlistClick = onWishlistClick,
                    onLogoutClick = onLogoutClick,
                    onTermsClick = onTermsClick,
                    onPrivacyClick = onPrivacyClick,
                    onReturnsClick = onReturnsClick,
                    onContactClick = onContactClick
                )
            }
        ) {
            Scaffold(
                topBar = {
                    AppTopBar(
                        currentScreen = currentScreen,
                        cartCount = cartCount,
                        onMenuClick = onMenuClick,
                        onSearchClick = onSearchClick,
                        onLogoClick = onLogoClick,
                        onProfileClick = onProfileClick,
                        onCartClick = onCartClick
                    )
                }
            ) { innerPadding ->
                content(innerPadding)
            }
        }
    }
}