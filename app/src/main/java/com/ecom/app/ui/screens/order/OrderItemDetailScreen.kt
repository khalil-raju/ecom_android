package com.ecom.app.ui.screens.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ecom.app.BuildConfig
import com.ecom.app.R
import com.ecom.app.model.order.OrderItem
import com.ecom.app.model.order.OrderItemDetailResponse
import com.ecom.app.ui.components.ScreenHeader

private fun fullUrl(path: String?): String? {
    return path?.let {
        if (it.startsWith("http")) it
        else BuildConfig.BASE_URL.trimEnd('/') + it
    }
}

@Composable
fun OrderItemDetailScreen(
    modifier: Modifier = Modifier,
    response: OrderItemDetailResponse?,
    onOrderDetailClick: (String) -> Unit,
    onReturnItemClick: (String) -> Unit = {},
    onReviewItemClick: (String) -> Unit = {},
    onTrackItemClick: (String) -> Unit = {},
    onSupportClick: (String) -> Unit = {},
    onProductClick: (variantId: Int, slug: String) -> Unit
) {
    val item = response?.orderItem

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {

        ScreenHeader(
            title = "Item Details",
            subtitle = "View and manage delivery item"
        )

        if (item == null) {
            EmptyState()
            return@Column
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ItemTopSection(
                    item = item,
                    onProductClick = onProductClick
                )
            }

            item {
                ItemInfoCard(
                    item = item,
                    onOrderDetailClick = onOrderDetailClick
                )
            }

            item {
                PriceDetailsCard(item = item)
            }

            item {
                ReviewItemCard(
                    itemToken = item.itemToken,
                    canUserReview = item.canUserReview,
                    alreadyReviewed = item.alreadyReviewed,
                    onReviewItemClick = onReviewItemClick
                )
            }

            item {
                ReturnItemCard(
                    itemToken = item.itemToken,
                    canUserReturn = item.canUserReturn,
                    onReturnItemClick = onReturnItemClick
                )
            }

            item {
                HelpCard(
                    itemToken = item.itemToken,
                    onSupportClick = onSupportClick
                )
            }

            item {
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun OrderItemDetailHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "Back",
            tint = Color.Black,
            modifier = Modifier
                .size(28.dp)
                .clickable { onBack() }
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = "Order Item",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ItemTopSection(
    item: OrderItem,
    onProductClick: (variantId: Int, slug: String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = fullUrl(item.variantImageUrl),
                contentDescription = item.variantName,
                modifier = Modifier
                    .size(width = 120.dp, height = 160.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(10.dp))
                    .clickable {
                        val variantId = item.variantId
                        val slug = item.variantSlug
                        if (variantId != null && !slug.isNullOrBlank()) {
                            onProductClick(variantId, slug)
                        }
                    },
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.variantName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = buildProductMeta(item),
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "₹${formatAmount(item.totalAmt)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Qty: ${item.quantity}",
                    fontSize = 15.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
private fun ItemInfoCard(
    item: OrderItem,
    onOrderDetailClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            DetailRow(
                label = "Status",
                value = item.statusSummary.orEmpty(),
                valueColor = statusColor(item.statusSummary.orEmpty())
            )

            DetailRow("Order ID", item.orderId.orEmpty())

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        item.orderToken?.let { onOrderDetailClick(it) }
                    }
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "View Order Details ›",
                    color = Color(0xFF0A73E8),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun PriceDetailsCard(item: OrderItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Price Details", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(16.dp))

            DetailRow("Price", "₹${formatAmount(item.price)}")
            DetailRow("Qty", item.quantity.toString())

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            DetailRow(
                label = "Total",
                value = "₹${formatAmount(item.totalAmt)}",
                bold = true
            )
        }
    }
}
@Composable
private fun ReviewItemCard(
    itemToken: String?,
    canUserReview: Boolean?,
    alreadyReviewed: Boolean?,
    onReviewItemClick: (String) -> Unit
) {
    if (itemToken.isNullOrBlank()) return
    if (canUserReview != true && alreadyReviewed != true) return

    val title = if (alreadyReviewed == true) {
        "View Review"
    } else {
        "Review Item"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onReviewItemClick(itemToken) },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        ActionRow(title = title)
    }
}

@Composable
private fun ReturnItemCard(
    itemToken: String?,
    canUserReturn: Boolean?,
    onReturnItemClick: (String) -> Unit
) {
    if (canUserReturn != true || itemToken.isNullOrBlank()) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onReturnItemClick(itemToken) },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        ActionRow(title = "Return Item")
    }
}

@Composable
private fun HelpCard(
    itemToken: String?,
    onSupportClick: (String) -> Unit
) {
    if (itemToken.isNullOrBlank()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Need Help?", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(14.dp))

            OutlinedButton(
                onClick = { onSupportClick(itemToken) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Contact Support")
            }
        }
    }
}

@Composable
private fun ActionRow(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "›",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = Color.Black,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = Color.DarkGray,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )

        Text(
            text = value,
            fontSize = 15.sp,
            color = valueColor,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Order item not found.", fontSize = 16.sp)
    }
}

private fun buildProductMeta(item: OrderItem): String {
    val parts = mutableListOf<String>()

    item.variantSize?.takeIf { it.isNotBlank() }?.let {
        parts.add("Size: $it")
    }

    return parts.joinToString("  •  ")
}

private fun statusColor(status: String): Color {
    return when {
        status.contains("cancel", ignoreCase = true) -> Color.Red
        status.contains("deliver", ignoreCase = true) -> Color(0xFF0B8F3A)
        status.contains("return", ignoreCase = true) -> Color(0xFFE67E22)
        else -> Color.DarkGray
    }
}

private fun formatAmount(value: Double): String {
    return if (value % 1.0 == 0.0) value.toInt().toString()
    else "%.2f".format(value)
}
