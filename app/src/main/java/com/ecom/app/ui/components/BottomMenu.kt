package com.ecom.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecom.app.R
import com.ecom.app.ui.navigations.AppScreen

@Composable
fun BottomMenu(
    currentScreen: AppScreen,
    cartCount: Int,
    onHomeClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        HorizontalDivider(color = Color(0xFFE5E5E5))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomMenuItem(
                selectedIcon = R.drawable.ic_home_selected,
                unselectedIcon = R.drawable.ic_home_unselected,
                label = "Home",
                selected = currentScreen == AppScreen.Home,
                onClick = onHomeClick,
                modifier = Modifier.weight(1f)
            )

            BottomMenuItem(
                selectedIcon = R.drawable.ic_categories_selected,
                unselectedIcon = R.drawable.ic_categories_unselected,
                label = "Categories",
                selected = false,
                onClick = onCategoriesClick,
                modifier = Modifier.weight(1f)
            )

            BottomMenuItem(
                selectedIcon = R.drawable.ic_wishlist_selected,
                unselectedIcon = R.drawable.ic_wishlist_unselected,
                label = "Wishlist",
                selected = false,
                onClick = onWishlistClick,
                modifier = Modifier.weight(1f)
            )

            BottomMenuItem(
                selectedIcon = R.drawable.ic_bag_selected,
                unselectedIcon = R.drawable.ic_bag_unselected,
                label = "Bag",
                selected = currentScreen == AppScreen.Cart,
                badgeCount = cartCount,
                onClick = onCartClick,
                modifier = Modifier.weight(1f)
            )

            BottomMenuItem(
                selectedIcon = R.drawable.ic_profile_selected,
                unselectedIcon = R.drawable.ic_profile_unselected,
                label = "Profile",
                selected = currentScreen == AppScreen.Profile,
                onClick = onProfileClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BottomMenuItem(
    @DrawableRes selectedIcon: Int,
    @DrawableRes unselectedIcon: Int,
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
    onClick: () -> Unit
) {
    val color = if (selected) Color.Black else Color(0xFF777777)

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() }
            .padding(top = 7.dp, bottom = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BadgedBox(
            badge = {
                if (badgeCount > 0) {
                    Badge(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ) {
                        Text(
                            text = cartBadgeText(badgeCount),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        ) {
            Icon(
                painter = painterResource(
                    id = if (selected) selectedIcon else unselectedIcon
                ),
                contentDescription = label,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.height(3.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            color = color,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

private fun cartBadgeText(count: Int): String {
    return if (count > 99) "99+" else count.toString()
}