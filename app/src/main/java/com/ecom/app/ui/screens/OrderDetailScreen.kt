// ui/screens/OrderDetailScreen.kt
package com.ecom.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ecom.app.BuildConfig
import com.ecom.app.R
import com.ecom.app.model.OrderDetailResponse
import com.ecom.app.model.OrderItem
import com.ecom.app.model.Address
import java.text.SimpleDateFormat
import java.util.Locale

private fun fullUrl(path: String?): String? {
    return path?.let {
        if (it.startsWith("http")) it
        else BuildConfig.BASE_URL.trimEnd('/') + it
    }
}

@Composable
fun OrderDetailScreen(
    modifier: Modifier = Modifier,
    response: OrderDetailResponse?,
    onBack: () -> Unit,
    onInvoiceClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onReturnClick: () -> Unit = {}
) {
    val order = response?.order
    val items = response?.orderItems.orEmpty()
    val payment = response?.payment
    val shippingAddress = response?.shippingAddress

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        OrderDetailHeader(onBack = onBack)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Order Details", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("Order ID: ${order?.orderId ?: ""}", fontSize = 15.sp, color = Color.DarkGray)
                Text("Placed on ${formatDate(order?.placedAt)}", fontSize = 14.sp, color = Color.Gray)
            }

            item {
                StatusCard(
                    state = order?.state.orEmpty(),
                    placedAt = order?.placedAt
                )
            }

            item {
                ItemsCard(
                    items = items,
                    onInvoiceClick = onInvoiceClick
                )
            }

            item {
                SummaryCard(
                    itemCount = items.sumOf { it.quantity },
                    baseAmount = order?.itemsTotalBaseAmt ?: 0.0,
                    gstAmount = order?.itemsTotalGstAmt ?: 0.0,
                    itemsTotal = order?.itemsTotalAmt ?: 0.0,
                    shipping = order?.shippingCost ?: 0.0,
                    total = order?.totalAmount ?: 0.0,
                    paymentText = buildPaymentText(payment)
                )
            }

            item {
                AddressCard(address = shippingAddress)
            }

            item {
                HelpCard(
                    onSupportClick = onSupportClick,
                    onReturnClick = onReturnClick
                )
            }

            item {
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun OrderDetailHeader(onBack: () -> Unit) {
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
            text = "Order Details",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatusCard(
    state: String,
    placedAt: String?
) {
    val delivered = state.equals("delivered", ignoreCase = true) ||
            state.equals("DELIVERED", ignoreCase = true)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(if (delivered) Color(0xFF0B8F3A) else Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (delivered) "Delivered" else state.ifBlank { "Order Placed" },
                    color = if (delivered) Color(0xFF0B8F3A) else Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (delivered) "Your order has been delivered successfully."
                    else "Placed on ${formatDate(placedAt)}",
                    color = Color.DarkGray,
                    fontSize = 15.sp
                )
            }

            Text("›", fontSize = 34.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ItemsCard(
    items: List<OrderItem>,
    onInvoiceClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Items (${items.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "View Invoice",
                    color = Color(0xFF0A73E8),
                    fontSize = 15.sp,
                    modifier = Modifier.clickable { onInvoiceClick() }
                )
            }

            Spacer(Modifier.height(16.dp))

            items.forEachIndexed { index, item ->
                OrderItemRow(item = item)

                if (index != items.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))
                }
            }
        }
    }
}

@Composable
private fun OrderItemRow(item: OrderItem) {
    Row(verticalAlignment = Alignment.Top) {
        AsyncImage(
            model = fullUrl(item.variantImageUrl),
            contentDescription = item.variantName,
            modifier = Modifier
                .size(120.dp)
                .background(Color(0xFFF1F1F1), RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.variantName,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Size: ${item.variantSize ?: ""}  •  Qty: ${item.quantity}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "₹${formatAmount(item.totalAmt)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SummaryCard(
    itemCount: Int,
    baseAmount: Double,
    gstAmount: Double,
    itemsTotal: Double,
    shipping: Double,
    total: Double,
    paymentText: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Order Summary", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(16.dp))

            SummaryRow("Price ($itemCount Item${if (itemCount == 1) "" else "s"})", baseAmount)
            SummaryRow("GST", gstAmount)
            SummaryRow("Items Total", itemsTotal)
            SummaryRow("Delivery Charge", shipping)

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Amount", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("₹${formatAmount(total)}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(paymentText, color = Color.Gray, fontSize = 15.sp)
                Text("₹${formatAmount(total)}", color = Color.Gray, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 16.sp)
        Text("₹${formatAmount(value)}", fontSize = 16.sp)
    }
}

@Composable
private fun AddressCard(address: Address?) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                Text("●", fontSize = 24.sp)
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Delivery Address", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(12.dp))

                Text(
                    text = address?.fullName.orEmpty(),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = buildAddress(address),
                    fontSize = 15.sp,
                    color = Color.DarkGray,
                    lineHeight = 22.sp
                )
            }

            Text("›", fontSize = 34.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun HelpCard(
    onSupportClick: () -> Unit,
    onReturnClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("Need Help?", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onSupportClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Contact Support")
                }

                OutlinedButton(
                    onClick = onReturnClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Request Return")
                }
            }
        }
    }
}

private fun buildAddress(address: Address?): String {
    if (address == null) return ""

    return listOf(
        address.addressLine1,
        address.addressLine2,
        "${address.city}, ${address.state} - ${address.pincode}",
        address.country
    )
        .filterNotNull()
        .filter { it.isNotBlank() }
        .joinToString("\n")
}

private fun buildPaymentText(payment: com.ecom.app.model.OrderPayment?): String {
    return when {
        payment == null -> ""
        payment.onlinePaidAmt > 0 -> "Paid via ${payment.onlineMethod ?: "Online"}"
        payment.walletPaidAmt > 0 -> "Paid via Wallet"
        payment.codPaidAmt > 0 -> "Paid via COD"
        payment.totalDueAmt > 0 -> "Amount Due"
        else -> "Paid"
    }
}

private fun formatAmount(value: Double): String {
    return if (value % 1.0 == 0.0) value.toInt().toString()
    else "%.2f".format(value)
}

private fun formatDate(value: String?): String {
    if (value.isNullOrBlank()) return ""

    return try {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH)
        val output = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
        val date = input.parse(value)
        if (date != null) output.format(date) else value
    } catch (e: Exception) {
        value
    }
}
