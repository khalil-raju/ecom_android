package com.ecom.app.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ecom.app.BuildConfig
import com.ecom.app.model.order.OrderItem
import com.ecom.app.ui.components.ScreenHeader

private fun fullUrl(path: String?): String? {
    return path?.let {
        if (it.startsWith("http")) it
        else BuildConfig.BASE_URL.trimEnd('/') + it
    }
}

@Composable
fun OrderItemHistoryScreen(
    modifier: Modifier = Modifier,
    items: List<OrderItem>,
    isLoadingMore: Boolean,
    hasMore: Boolean,
    onLoadMore: () -> Unit,
    onItemClick: (OrderItem) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        ScreenHeader(
            title = "My Orders",
            subtitle = "View your order history"
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            if (items.isEmpty()) {
                item {
                    EmptyCard()
                }
            } else {
                itemsIndexed(
                    items = items,
                    key = { _, item -> item.itemToken }
                ) { index, item ->

                    if (
                        index == items.lastIndex &&
                        hasMore &&
                        !isLoadingMore
                    ) {
                        LaunchedEffect(item.itemToken) {
                            onLoadMore()
                        }
                    }

                    OrderItemHistoryCard(
                        item = item,
                        onClick = {
                            if (item.canShowDetails) {
                                onItemClick(item)
                            }
                        }
                    )
                }

                if (isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderItemHistoryCard(
    item: OrderItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = fullUrl(item.variantImageUrl),
                contentDescription = item.variantName,
                modifier = Modifier
                    .size(width = 92.dp, height = 120.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.variantName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                item.orderId.takeIf { it.isNotBlank() }?.let {
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Order ID: $it",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = item.statusSummary,
                    fontSize = 16.sp,
                    color = statusColor(item.statusSummary),
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (item.canShowDetails) {
                Text(
                    text = "›",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun EmptyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "No orders found.",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Your ordered items will appear here.",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

private fun statusColor(status: String): Color {
    return when {
        status.contains("cancel", ignoreCase = true) -> Color.Red
        status.contains("delivered", ignoreCase = true) -> Color(0xFF0B8F3A)
        else -> Color.DarkGray
    }
}