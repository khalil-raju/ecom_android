// ui/screens/OrderDetailScreen.kt
package com.ecom.app.ui.screens.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ecom.app.BuildConfig
import com.ecom.app.R
import com.ecom.app.model.order.OrderDetailResponse
import com.ecom.app.model.order.OrderItem
import com.ecom.app.model.account.Address
import com.ecom.app.model.payment.Payment
import com.ecom.app.ui.components.ScreenHeader
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
    onInvoiceClick: () -> Unit,
    onSupportClick: () -> Unit = {},
    onCancelOrderClick: () -> Unit = {}
) {
    val order = response?.order
    val items = response?.orderItems.orEmpty()
    val payment = response?.payment
    val shippingAddress = order?.shippingAddress

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {

        ScreenHeader(
            title = "Order Details",
            subtitle = "View and manage order"
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OrderInfoCard(
                    orderId = order?.orderId.orEmpty(),
                    placedAt = order?.placedAt,
                    cancelledAt = order?.cancelledAt
                )
            }

            item {
                AddressCard(address = shippingAddress)
            }

            item {
                ItemsCard(
                    items = items,
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
                InvoiceCard(
                    invoiceUrl = response?.invoice?.pdfUrl,
                    onInvoiceClick = onInvoiceClick
                )
            }

            item {
                CancelOrderCard(
                    canUserCancel = order?.canUserCancel,
                    onCancelOrderClick = onCancelOrderClick
                )
            }

            item {
                HelpCard(
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
private fun OrderInfoCard(
    orderId: String,
    placedAt: String?,
    cancelledAt: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Order Details",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(18.dp))

            InfoRow("Order ID:", orderId)

            if (!placedAt.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                InfoRow("Order Placed:", formatDateOnly(placedAt))
            }

            if (!cancelledAt.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                InfoRow("Order Cancelled:", formatDateOnly(cancelledAt))
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 17.sp,
            color = Color.Black,
            modifier = Modifier.width(150.dp)
        )

        Text(
            text = value,
            fontSize = 17.sp,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ItemsCard(
    items: List<OrderItem>,
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
        modifier = Modifier.fillMaxWidth(), // ✅ FIX: full width
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.Top
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Delivery Address",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = address?.fullName.orEmpty(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = buildAddressLines(address),
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )

                address?.phone?.takeIf { it.isNotBlank() }?.let {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
private fun InvoiceCard(
    invoiceUrl: String?,
    onInvoiceClick: () -> Unit
) {
    if (invoiceUrl.isNullOrBlank()) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onInvoiceClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Download Invoice",
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
}

@Composable
private fun CancelOrderCard(
    canUserCancel: Boolean?,
    onCancelOrderClick: () -> Unit
) {

    if (canUserCancel != true) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCancelOrderClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cancel Order",
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
}

@Composable
private fun HelpCard(
    onSupportClick: () -> Unit
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
            }
        }
    }
}

private fun buildAddressLines(address: Address?): String {
    if (address == null) return ""

    val line1 = listOfNotNull(
        address.line1,
        address.line2
    ).joinToString(", ")

    val line2 = listOfNotNull(
        address.city,
        address.state
    ).joinToString(", ")

    val line3 = listOfNotNull(
        address.postalCode,
        address.country
    ).joinToString(", ")

    return listOf(line1, line2, line3)
        .filter { it.isNotBlank() }
        .joinToString("\n")
}

private fun buildPaymentText(payment: Payment?): String {
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

private fun formatDateOnly(value: String?): String {
    if (value.isNullOrBlank()) return ""

    return try {
        val cleaned = value.replace(Regex("\\.\\d+"), "")
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH)
        val output = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

        val date = input.parse(cleaned)
        if (date != null) output.format(date) else value
    } catch (e: Exception) {
        value
    }
}