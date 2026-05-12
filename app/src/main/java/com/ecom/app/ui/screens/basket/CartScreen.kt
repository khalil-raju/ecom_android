package com.ecom.app.ui.screens.basket

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import com.ecom.app.model.basket.CartDetailResponse
import com.ecom.app.ui.components.ScreenHeader

private fun fullUrl(path: String?): String? {
    return path?.let {
        if (it.startsWith("http")) it
        else BuildConfig.BASE_URL.trimEnd('/') + it
    }
}

@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    cartDetailResponse: CartDetailResponse?,
    isLoading: Boolean = false,
    onNavigateToProduct: (Int, String) -> Unit,
    onQuantityChange: (BasketItem, Int) -> Unit,
    onCheckoutClick: () -> Unit
) {
    val cartItems = cartDetailResponse?.cartItems.orEmpty()
    val total = cartDetailResponse?.cartTotal ?: 0.00

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {

        ScreenHeader(
            title = "My Shopping Bag",
        )

        if (isLoading && cartDetailResponse == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (cartItems.isEmpty()) {
            EmptyCartState()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(cartItems) { item ->
                    CartItemCard(
                        item = item,
                        onProductClick = { variantId, slug ->
                            if (variantId != null) {
                                onNavigateToProduct(variantId, slug)
                            }
                        },
                        onQuantityChange = onQuantityChange
                    )
                }

                item {
                    CartSummaryCard(
                        itemCount = cartItems.sumOf { it.quantity },
                        total = total,
                        onCheckoutClick = onCheckoutClick
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyCartState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🛍", fontSize = 64.sp)

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Your Shopping Bag is Empty",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Looks like you haven’t added anything yet.",
                fontSize = 15.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun CartItemCard(
    item: BasketItem,
    onProductClick: (variantId: Int?, slug: String) -> Unit,
    onQuantityChange: (BasketItem, Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = fullUrl(item.image),
                contentDescription = item.name,
                modifier = Modifier
                    .size(104.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(10.dp))
                    .clickable {
                        onProductClick(item.variantId, item.slug)
                    },
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        onProductClick(item.variantId, item.slug)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Size: ${item.size}",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Price: ${formatAmount(item.price)}",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${formatAmount(item.subtotal)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    QuantityControl(
                        quantity = item.quantity,
                        onQuantityChange = { delta ->
                            onQuantityChange(item, delta)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CartSummaryCard(
    itemCount: Int,
    total: Double,
    onCheckoutClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFF2F2F2)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "🛍", fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "$itemCount ${if (itemCount == 1) "item" else "items"}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = Color(0xFFEAEAEA))

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Subtotal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "₹${formatAmount(total)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onCheckoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Proceed to Checkout",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun QuantityControl(
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color(0xFFDADADA),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "−",
            fontSize = 22.sp,
            modifier = Modifier.clickable { onQuantityChange(-1) }
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = quantity.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = "+",
            fontSize = 22.sp,
            modifier = Modifier.clickable { onQuantityChange(1) }
        )
    }
}

private fun formatAmount(value: Double?): String {
    return "%.2f".format(value)
}