package com.ecom.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.ecom.app.model.CancelOrderResponse
import com.ecom.app.model.OrderItem

private fun fullUrl(path: String?): String? {
    return path?.let {
        if (it.startsWith("http")) it
        else BuildConfig.BASE_URL.trimEnd('/') + it
    }
}

@Composable
fun CancelOrderScreen(
    modifier: Modifier = Modifier,
    response: CancelOrderResponse?,
    error: String?,
    onBack: () -> Unit,
    onConfirmCancel: (
        cancelReason: String,
        refundAccount: String?
    ) -> Unit
) {
    val order = response?.order
    val items = response?.orderItems.orEmpty()

    val reasons = listOf(
        "Changed my mind",
        "Ordered by mistake",
        "Found a better price elsewhere",
        "Delivery is taking too long",
        "Item / Price related issue",
        "Other"
    )

    var selectedReason by remember { mutableStateOf("") }
    var otherReason by remember { mutableStateOf("") }
    var refundAccount by remember {
        mutableStateOf(
            if (response?.refundRequired == true) "wallet_account" else null
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        CancelOrderHeader(onBack = onBack)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OrderSummaryCard(
                    orderId = order?.orderId.orEmpty(),
                    totalAmount = order?.totalAmount ?: 0.0,
                    itemCount = items.sumOf { it.quantity }
                )
            }

            item {
                CancelItemsCard(items = items)
            }

            if (response?.refundRequired == true) {
                item {
                    RefundAccountCard(
                        onlinePaidAmt = order?.onlinePaidAmt ?: 0.0,
                        walletPaidAmt = order?.walletPaidAmt ?: 0.0,
                        totalPaidAmt = order?.totalPaidAmt ?: 0.0,
                        selected = refundAccount,
                        onSelected = { refundAccount = it }
                    )
                }
            }

            item {
                CancelReasonCard(
                    reasons = reasons,
                    selectedReason = selectedReason,
                    otherReason = otherReason,
                    onReasonSelected = { selectedReason = it },
                    onOtherReasonChange = { otherReason = it }
                )
            }

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
                Spacer(Modifier.height(90.dp))
            }
        }

        CancelFooter(
            enabled = order?.canUserCancel == true,
            onCancelClick = {
                val reason = if (selectedReason == "Other") {
                    otherReason.trim()
                } else {
                    selectedReason.trim()
                }

                onConfirmCancel(reason, refundAccount)
            }
        )
    }
}

@Composable
private fun CancelOrderHeader(onBack: () -> Unit) {
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
            text = "Cancel Order",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun OrderSummaryCard(
    orderId: String,
    totalAmount: Double,
    itemCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Order Summary",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(14.dp))

            DetailRow("Order ID", orderId)
            DetailRow("Items", itemCount.toString())
            DetailRow("Total Amount", "₹${formatAmount(totalAmount)}", bold = true)
        }
    }
}

@Composable
private fun CancelItemsCard(items: List<OrderItem>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Items to Cancel",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(14.dp))

            items.forEachIndexed { index, item ->
                CancelItemRow(item = item)

                if (index != items.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))
                }
            }
        }
    }
}

@Composable
private fun CancelItemRow(item: OrderItem) {
    Row(verticalAlignment = Alignment.Top) {
        AsyncImage(
            model = fullUrl(item.variantImageUrl),
            contentDescription = item.variantName,
            modifier = Modifier
                .size(width = 86.dp, height = 112.dp)
                .background(Color(0xFFF0F0F0), RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.variantName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = buildItemMeta(item),
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "₹${formatAmount(item.totalAmt)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RefundAccountCard(
    onlinePaidAmt: Double,
    walletPaidAmt: Double,
    totalPaidAmt: Double,
    selected: String?,
    onSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Refund Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            if (onlinePaidAmt > 0) {
                RefundOption(
                    selected = selected == "source_account",
                    title = "Refund to Source",
                    subtitle = buildString {
                        if (walletPaidAmt > 0) {
                            append("To source: ₹${formatAmount(onlinePaidAmt)}")
                            append("\n")
                            append("To wallet: ₹${formatAmount(walletPaidAmt)}")
                        } else {
                            append("₹${formatAmount(onlinePaidAmt)}")
                        }
                    },
                    onClick = { onSelected("source_account") }
                )
            }

            RefundOption(
                selected = selected == "wallet_account",
                title = "Refund to Wallet",
                subtitle = "₹${formatAmount(totalPaidAmt)}",
                onClick = { onSelected("wallet_account") }
            )
        }
    }
}

@Composable
private fun RefundOption(
    selected: Boolean,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Spacer(Modifier.width(10.dp))

        Column {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, fontSize = 13.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun CancelReasonCard(
    reasons: List<String>,
    selectedReason: String,
    otherReason: String,
    onReasonSelected: (String) -> Unit,
    onOtherReasonChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Why are you cancelling?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            reasons.forEach { reason ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedReason == reason,
                            onClick = { onReasonSelected(reason) }
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedReason == reason,
                        onClick = { onReasonSelected(reason) }
                    )

                    Spacer(Modifier.width(10.dp))

                    Text(reason, fontSize = 16.sp)
                }
            }

            if (selectedReason == "Other") {
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = otherReason,
                    onValueChange = onOtherReasonChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Reason") },
                    minLines = 3,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }
    }
}

@Composable
private fun CancelFooter(
    enabled: Boolean,
    onCancelClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onCancelClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD32F2F),
                contentColor = Color.White,
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.White
            )
        ) {
            Text(
                text = "Cancel Order",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
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
            color = Color.Black,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}

private fun buildItemMeta(item: OrderItem): String {
    val parts = mutableListOf<String>()

    item.variantSize?.takeIf { it.isNotBlank() }?.let {
        parts.add("Size: $it")
    }

    parts.add("Qty: ${item.quantity}")

    return parts.joinToString("  •  ")
}

private fun formatAmount(value: Double): String {
    return if (value % 1.0 == 0.0) value.toInt().toString()
    else "%.2f".format(value)
}