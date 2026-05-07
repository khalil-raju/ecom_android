package com.ecom.app.ui.layouts

import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import com.ecom.app.ui.components.SearchMenu
import com.ecom.app.ui.components.SideMenu
import com.ecom.app.ui.components.SideMenuCategory

enum class DrawerContentType {
    SIDE_MENU,
    SEARCH_MENU
}

@Composable
fun AppDrawer(
    drawerContentType: DrawerContentType,
    isAuthenticated: Boolean,
    parentCategories: List<SideMenuCategory>,
    onClose: () -> Unit,
    onHomeClick: () -> Unit,
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit,
    onCategoryClick: (parentSlug: String, childSlug: String?) -> Unit,
    onProfileClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onWalletClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onReturnsClick: () -> Unit,
    onContactClick: () -> Unit
) {
    ModalDrawerSheet {
        when (drawerContentType) {
            DrawerContentType.SIDE_MENU -> {
                SideMenu(
                    parentCategories = parentCategories,
                    isAuthenticated = isAuthenticated,
                    onClose = onClose,
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

            DrawerContentType.SEARCH_MENU -> {
                SearchMenu(onClose = onClose)
            }
        }
    }
}