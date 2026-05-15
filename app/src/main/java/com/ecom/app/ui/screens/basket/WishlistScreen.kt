package com.ecom.app.ui.screens.basket

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ecom.app.BuildConfig
import com.ecom.app.model.basket.BasketItem
import com.ecom.app.ui.components.ScreenFooter
import com.ecom.app.ui.components.ScreenHeader

private fun fullUrl(path: String?): String? {
    return path?.let {
        if (it.startsWith("http")) it
        else BuildConfig.BASE_URL.trimEnd('/') + it
    }
}

@Composable
fun WishlistScreen(
    modifier: Modifier = Modifier,
    items: List<BasketItem>,
    onProductClick: (BasketItem) -> Unit,
    onRemoveWishlist: (BasketItem) -> Unit,
    onAddToCart: (BasketItem) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ScreenHeader(
                title = "Wishlist",
                subtitle = "Items you saved for later"
            )
        }

        if (items.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Your wishlist is empty.",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        items(items) { item ->
            WishlistItemCard(
                item = item,
                onClick = { onProductClick(item) },
                onRemoveWishlist = { onRemoveWishlist(item) },
                onAddToCart = { onAddToCart(item) }
            )
        }

        item {
            ScreenFooter()
        }
    }
}

@Composable
private fun WishlistItemCard(
    item: BasketItem,
    onClick: () -> Unit,
    onRemoveWishlist: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = fullUrl(item.image),
                contentDescription = item.name,
                modifier = Modifier
                    .size(104.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                if (item.stock <= 0) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Out of stock",
                        fontSize = 13.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(48.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    OutlinedButton(
                        onClick = onRemoveWishlist,
                        modifier = Modifier.height(35.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
                        border = BorderStroke(1.dp, Color.Black),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Remove")
                    }

                    OutlinedButton(
                        onClick = onAddToCart,
                        enabled = item.stock > 0,
                        modifier = Modifier.height(35.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
                        border = BorderStroke(1.dp, Color.Black),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black,
                            disabledContainerColor = Color.LightGray
                        )
                    ) {
                        Text("Add to Bag")
                    }
                }
            }
        }
    }
}

private fun formatAmount(value: Double): String {
    return "%.2f".format(value)
}