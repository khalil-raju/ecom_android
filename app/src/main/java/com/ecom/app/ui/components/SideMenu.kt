package com.ecom.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SideMenuCategory(
    val name: String,
    val slug: String,
    val children: List<SideMenuCategoryChild> = emptyList()
)

data class SideMenuCategoryChild(
    val name: String,
    val slug: String
)

@Composable
fun SideMenu(
    parentCategories: List<SideMenuCategory>,
    isAuthenticated: Boolean,
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
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp)
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp, top = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "×",
                fontSize = 42.sp,
                modifier = Modifier.clickable { onClose() }
            )
        }

        MenuItem("Home") {
            onClose()
            onHomeClick()
        }

        parentCategories
            .filter { it.children.isNotEmpty() }
            .forEach { category ->
                ExpandableMenuGroup(
                    title = category.name,
                    items = buildList {
                        add("▹ All" to {
                            onClose()
                            onCategoryClick(category.slug, null)
                        })

                        category.children.forEach { child ->
                            add("▹ ${child.name}" to {
                                onClose()
                                onCategoryClick(category.slug, child.slug)
                            })
                        }
                    }
                )
            }

        if (isAuthenticated) {
            ExpandableMenuGroup(
                title = "My Account",
                items = listOf(
                    "▹ Profile" to {
                        onClose()
                        onProfileClick()
                    },
                    "▹ Orders" to {
                        onClose()
                        onOrdersClick()
                    },
                    "▹ Wallet" to {
                        onClose()
                        onWalletClick()
                    },
                    "▹ Wishlist" to {
                        onClose()
                        onWishlistClick()
                    },
                    "▹ Logout" to {
                        onClose()
                        onLogoutClick()
                    }
                )
            )
        } else {
            MenuItem("Login") {
                onClose()
                onLoginClick()
            }

            MenuItem("Sign Up") {
                onClose()
                onSignupClick()
            }
        }

        ExpandableMenuGroup(
            title = "About Us",
            items = listOf(
                "▹ Terms & Conditions" to {
                    onClose()
                    onTermsClick()
                },
                "▹ Privacy Policy" to {
                    onClose()
                    onPrivacyClick()
                },
                "▹ Returns & Shipping Policy" to {
                    onClose()
                    onReturnsClick()
                },
                "▹ Contact Us" to {
                    onClose()
                    onContactClick()
                }
            )
        )
    }
}

@Composable
private fun MenuItem(
    text: String,
    onClick: () -> Unit
) {
    Column {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 24.dp, vertical = 12.dp)
        )
        HorizontalDivider(thickness = 1.dp)
    }
}

@Composable
private fun ExpandableMenuGroup(
    title: String,
    items: List<Pair<String, () -> Unit>>
) {
    var open by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "$title ${if (open) "▾" else "▸"}",
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { open = !open }
                .padding(horizontal = 24.dp, vertical = 12.dp)
        )

        HorizontalDivider(thickness = 1.dp)

        if (open) {
            items.forEach { item ->
                Text(
                    text = item.first,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { item.second() }
                        .padding(horizontal = 42.dp, vertical = 10.dp)
                )
                HorizontalDivider(thickness = 1.dp)
            }
        }
    }
}
