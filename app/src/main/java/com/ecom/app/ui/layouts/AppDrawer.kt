package com.ecom.app.ui.layouts

import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import com.ecom.app.ui.components.SearchMenu
import com.ecom.app.ui.components.SideMenu
import com.ecom.app.ui.components.SideMenuCategory

enum class DrawerContentType {
    SEARCH_MENU
}

@Composable
fun AppDrawer(
    drawerContentType: DrawerContentType,
    onClose: () -> Unit,
    onSearchSubmit: (String) -> Unit
) {
    when (drawerContentType) {
        DrawerContentType.SEARCH_MENU -> {
            SearchMenu(
                onClose = onClose,
                onSearchSubmit = onSearchSubmit
            )
        }
    }
}