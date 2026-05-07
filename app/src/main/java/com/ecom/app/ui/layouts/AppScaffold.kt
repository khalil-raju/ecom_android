package com.ecom.app.ui.layouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ecom.app.ui.components.SideMenuCategory
import com.ecom.app.ui.navigations.AppScreen

@Composable
fun AppScaffold(
    drawerState: DrawerState,
    drawerContentType: DrawerContentType,
    currentScreen: AppScreen,
    isAuthenticated: Boolean,
    cartCount: Int,
    parentCategories: List<SideMenuCategory>,
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
    onTermsAndConditionsClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onReturnPolicyClick: () -> Unit,
    onContactUsClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Box(modifier = Modifier.safeDrawingPadding()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                AppDrawer(
                    drawerContentType = drawerContentType,
                    isAuthenticated = isAuthenticated,
                    parentCategories = parentCategories,
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
                    onTermsClick = onTermsAndConditionsClick,
                    onPrivacyClick = onPrivacyPolicyClick,
                    onReturnsClick = onReturnPolicyClick,
                    onContactClick = onContactUsClick
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