package com.ecom.app.ui.screens.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecom.app.R
import com.ecom.app.model.account.UserProfileResponse
import com.ecom.app.ui.components.ScreenFooter
import com.ecom.app.ui.components.ScreenHeader

private data class ProfileActionItem(
    val iconText: String,
    val title: String,
    val subtitle: String? = null,
    val actionText: String? = null,
    val verified: Boolean = false,
    val danger: Boolean = false,
    val onClick: () -> Unit = {}
)

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    profile: UserProfileResponse?,
    error: String?,
    onSavedAddressesClick: () -> Unit,
    onAddAddressClick: () -> Unit,
    onChangeNameClick: () -> Unit,
    onChangePhoneClick: () -> Unit,
    onChangeEmailClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onCartClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onViewOrderClick: () -> Unit,
    onViewWalletClick: () -> Unit,
    onLogout: () -> Unit
) {
    val user = profile?.user
    val fullName = listOf(
        user?.firstName.orEmpty(),
        user?.lastName.orEmpty()
    ).joinToString(" ").trim().ifBlank { "NoName" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {

        ScreenHeader(
            title = "My Profile",
            subtitle = "Manage your account and preferences"
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (!error.isNullOrBlank()) {
                item {
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item {
                ProfileHeroCard(
                    name = fullName,
                    email = user?.email?.takeIf { it.isNotBlank() } ?: "Email not added"
                )
            }

            item {
                ProfileSectionCard(
                    title = "Personal Information",
                    items = listOf(
                        ProfileActionItem(
                            iconText = "\uD83D\uDC64",
                            title = "Name",
                            subtitle = fullName,
                            actionText = "Change Name",
                            onClick = onChangeNameClick
                        ),
                        ProfileActionItem(
                            iconText = "\uD83D\uDCDE",
                            title = "Phone",
                            subtitle = user?.phone?.takeIf { it.isNotBlank() } ?: "Not added",
                            actionText = "Change Phone",
                            verified = user?.isPhoneVerified == true,
                            onClick = onChangePhoneClick
                        ),
                        ProfileActionItem(
                            iconText = "\uD83D\uDCE7",
                            title = "Email",
                            subtitle = user?.email?.takeIf { it.isNotBlank() } ?: "Not added",
                            actionText = "Change Email",
                            verified = user?.isEmailVerified == true,
                            onClick = onChangeEmailClick
                        ),
                        ProfileActionItem(
                            iconText = "\uD83D\uDD12",
                            title = "Change Password",
                            subtitle = "********",
                            onClick = onChangePasswordClick
                        )
                    )
                )
            }

            item {
                ProfileSectionCard(
                    title = "Addresses",
                    items = listOf(
                        ProfileActionItem(
                            iconText = "\uD83C\uDFE1",
                            title = "Saved Addresses",
                            subtitle = "View and manage your saved addresses",
                            onClick = onSavedAddressesClick
                        ),
                        ProfileActionItem(
                            iconText = "➕",
                            title = "Add Address",
                            subtitle = "Add a new delivery address",
                            onClick = onAddAddressClick
                        )
                    )
                )
            }

            item {
                ProfileSectionCard(
                    title = "Basket",
                    items = listOf(
                        ProfileActionItem(
                            "\uD83E\uDE76",
                            "View Wishlist",
                            "Items you saved for later",
                            onClick = onWishlistClick
                        ),
                        ProfileActionItem(
                            "\uD83D\uDECD\uFE0F",
                            "View Shopping Bag",
                            "Items in your shopping bag",
                            onClick = onCartClick
                        )
                    )
                )
            }

            item {
                ProfileSectionCard(
                    title = "Order & Wallet",
                    items = listOf(
                        ProfileActionItem(
                            "\uD83D\uDCE6",
                            "View Orders",
                            "Track and view your orders",
                            onClick = onViewOrderClick
                        ),
                        ProfileActionItem(
                            "\uD83D\uDCB0",
                            "Wallet",
                            "View balance and transactions",
                            onClick = onViewWalletClick
                        )
                    )
                )
            }

            item {
                ProfileSectionCard(
                    title = "Account",
                    items = listOf(
                        ProfileActionItem(
                            iconText = "\uD83D\uDEAA",
                            title = "Logout",
                            subtitle = "Sign out from your account",
                            danger = true,
                            onClick = onLogout
                        ),
                        ProfileActionItem(
                            iconText = "❌",
                            title = "Delete Account",
                            subtitle = "Permanently delete your account",
                            danger = true,
                            onClick = {}
                        )
                    )
                )
            }

            item {
                ScreenFooter()
            }
        }
    }
}

@Composable
private fun ProfileHeader(onBack: () -> Unit) {
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
                .clickable { onBack() },
            tint = Color.Black
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = "My Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2430)
            )

            Text(
                text = "Manage your account and preferences",
                fontSize = 13.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
private fun ProfileHeroCard(
    name: String,
    email: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0FF)),
        border = BorderStroke(1.dp, Color(0xFFE7E7FF))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E2FF)),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 34.sp)
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2430)
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = email,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Spacer(Modifier.height(8.dp))

                Surface(
                    color = Color(0xFFE3E4FF),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Member",
                        fontSize = 12.sp,
                        color = Color(0xFF2835C7),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileSectionCard(
    title: String?,
    items: List<ProfileActionItem>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE7E7E7))
    ) {
        Column {
            if (!title.isNullOrBlank()) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2430),
                    modifier = Modifier.padding(start = 18.dp, top = 18.dp, bottom = 4.dp)
                )
            }

            items.forEachIndexed { index, item ->
                ProfileRow(item = item)

                if (index != items.lastIndex) {
                    HorizontalDivider(color = Color(0xFFEDEDED))
                }
            }
        }
    }
}

@Composable
private fun ProfileRow(item: ProfileActionItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onClick() }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (item.danger) Color(0xFFFFEEEE) else Color(0xFFF0F0FF)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.iconText,
                fontSize = 21.sp,
                color = if (item.danger) Color.Red else Color(0xFF2835C7)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (item.danger) Color.Red else Color(0xFF1F2430)
            )

            if (!item.subtitle.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))

                Text(
                    text = item.subtitle,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (item.verified) {
                Spacer(Modifier.height(8.dp))
                VerifiedBadge()
            }
        }

        if (!item.actionText.isNullOrBlank()) {
            Text(
                text = item.actionText,
                fontSize = 14.sp,
                color = Color(0xFF0018D4),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )

            Spacer(Modifier.width(8.dp))
        }

        Text(
            text = "›",
            fontSize = 28.sp,
            color = if (item.danger) Color.Red else Color.Black
        )
    }
}

@Composable
private fun VerifiedBadge() {
    Surface(
        color = Color(0xFFE8F8EC),
        shape = RoundedCornerShape(18.dp)
    ) {
        Text(
            text = "✓ Verified",
            fontSize = 12.sp,
            color = Color(0xFF0A8F2E),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
        )
    }
}