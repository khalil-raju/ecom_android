package com.ecom.app.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.ecom.app.model.account.ProfileResponse

private data class ProfileRowItem(
    val title: String,
    val actionText: String? = null,
    val onClick: () -> Unit = {}
)

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    profile: ProfileResponse?,
    error: String?,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val user = profile?.user

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onBack() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Profile",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (error != null) {
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier.padding(12.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ProfileSection(
                    title = "Personal Information",
                    items = listOf(
                        ProfileRowItem(
                            title = "Name: ${user?.firstName?.ifBlank { "NoName" } ?: "NoName"} ${user?.lastName ?: ""}",
                            actionText = "Change Name"
                        ),
                        ProfileRowItem(
                            title = "Phone: ${user?.phone?.ifBlank { "Not added" } ?: "Not added"}",
                            actionText = "Change Phone"
                        ),
                        ProfileRowItem(
                            title = "Email: ${user?.email?.ifBlank { "Not added" } ?: "Not added"}",
                            actionText = "Change Email"
                        ),
                        ProfileRowItem(title = "Change Password")
                    )
                )
            }

            item {
                ProfileSection(
                    title = "Saved Addresses",
                    items = listOf(
                        ProfileRowItem("Saved Addresses"),
                        ProfileRowItem("Add Address")
                    )
                )
            }

            item {
                ProfileSection(
                    title = "Basket",
                    items = listOf(
                        ProfileRowItem("View Wishlist"),
                        ProfileRowItem("View Saved Items"),
                        ProfileRowItem("View Shopping Bag")
                    )
                )
            }

            item {
                ProfileSection(
                    title = "Order History",
                    items = listOf(
                        ProfileRowItem("View Orders")
                    )
                )
            }

            item {
                ProfileSection(
                    title = "Wallet",
                    items = listOf(
                        ProfileRowItem("Wallet Detail")
                    )
                )
            }

            item {
                ProfileSection(
                    title = "Logout",
                    items = listOf(
                        ProfileRowItem(
                            title = "Logout",
                            onClick = onLogout
                        )
                    ),
                    isDanger = true
                )
            }
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    items: List<ProfileRowItem>,
    isDanger: Boolean = false
) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { item.onClick() }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.title,
                            color = if (isDanger) Color.Red else Color.Black,
                            fontSize = 15.sp
                        )

                        item.actionText?.let {
                            Text(
                                text = it,
                                color = Color.Blue,
                                fontSize = 14.sp
                            )
                        }
                    }

                    if (index != items.lastIndex) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
