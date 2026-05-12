package com.ecom.app.ui.screens.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.ecom.app.model.order.ReturnOrderItemResponse
import com.ecom.app.ui.components.ScreenHeader

private fun fullUrl(path: String?): String? {
    return path?.let {
        if (it.startsWith("http")) it
        else BuildConfig.BASE_URL.trimEnd('/') + it
    }
}

@Composable
fun ReturnOrderItemScreen(
    modifier: Modifier = Modifier,
    response: ReturnOrderItemResponse?,
    error: String?,
    onSubmitReturn: (
        returnReason: String,
        refundAccount: String?
    ) -> Unit
) {
    val order = response?.order
    val item = response?.orderItem
    val payment = response?.payment

    var returnReason by remember { mutableStateOf("") }

    var refundAccount by remember(response?.refundRequired) {
        mutableStateOf(
            if (response?.refundRequired == true) "wallet_account" else null
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        ScreenHeader(
            title = "Return Item",
            subtitle = "Request a return for this item"
        )

        if (item == null) {
            EmptyState()
            return@Column
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OrderInfoCard(
                title = "Return ${item.variantName}",
                orderId = order?.orderId,
                placedAt = order?.placedAt,
                cancelledAt = order?.cancelledAt
            )

            ItemSummaryCard(item = item)

            if (
                payment != null &&
                payment.totalPaidAmt > 0
            ) {
                PaymentDetailsCard(
                    onlineMethod = payment.onlineMethod,
                    onlinePaidAmt = payment.onlinePaidAmt,
                    walletPaidAmt = payment.walletPaidAmt,
                    itemAfterWalletTotalAmt = item.afterWalletTotalAmt,
                    itemWalletTotalAmt = item.totalWalletAmt
                )
            }

            if (
                response.refundRequired &&
                payment != null &&
                payment.totalPaidAmt > 0
            ) {
                RefundAccountCard(
                    onlineMethod = payment.onlineMethod,
                    onlinePaidAmt = payment.onlinePaidAmt,
                    walletPaidAmt = payment.walletPaidAmt,
                    itemTotalAmt = item.totalAmt,
                    itemAfterWalletTotalAmt = item.afterWalletTotalAmt,
                    itemWalletTotalAmt = item.totalWalletAmt,
                    selected = refundAccount,
                    onSelected = { refundAccount = it }
                )
            }

            ReturnReasonCard(
                value = returnReason,
                onValueChange = { returnReason = it }
            )

            if (!error.isNullOrBlank()) {
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(90.dp))
        }

        ReturnFooter(
            enabled = item.canUserReturn,
            onClick = {
                onSubmitReturn(
                    returnReason.trim(),
                    refundAccount
                )
            }
        )
    }
}

@Composable
private fun OrderInfoCard(
    title: String,
    orderId: String?,
    placedAt: String?,
    cancelledAt: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            if (!orderId.isNullOrBlank()) {
                DetailRow("Order ID", orderId)
            }

            if (!placedAt.isNullOrBlank()) {
                DetailRow("Order Placed", placedAt)
            }

            if (!cancelledAt.isNullOrBlank()) {
                DetailRow("Order Cancelled", cancelledAt)
            }
        }
    }
}

@Composable
private fun ItemSummaryCard(item: OrderItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                AsyncImage(
                    model = fullUrl(item.variantImageUrl),
                    contentDescription = item.variantName,
                    modifier = Modifier
                        .size(width = 96.dp, height = 124.dp)
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.variantName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (item.variantSize.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Size: ${item.variantSize}",
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = item.statusSummary,
                        fontSize = 14.sp,
                        color = statusColor(item.statusSummary),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Item Summary",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(14.dp))

            DetailRow("Unit Price", "₹${formatAmount(item.price)}")
            DetailRow("Quantity", item.quantity.toString())

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
private fun PaymentDetailsCard(
    onlineMethod: String?,
    onlinePaidAmt: Double,
    walletPaidAmt: Double,
    itemAfterWalletTotalAmt: Double,
    itemWalletTotalAmt: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Payment Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(14.dp))

            if (itemAfterWalletTotalAmt > 0 && onlinePaidAmt > 0) {
                DetailRow(
                    label = onlineMethod ?: "Online Payment",
                    value = "₹${formatAmount(itemAfterWalletTotalAmt)}"
                )
            }

            if (itemWalletTotalAmt > 0 && walletPaidAmt > 0) {
                DetailRow(
                    label = "Wallet",
                    value = "₹${formatAmount(itemWalletTotalAmt)}"
                )
            }
        }
    }
}

@Composable
private fun RefundAccountCard(
    onlineMethod: String?,
    onlinePaidAmt: Double,
    walletPaidAmt: Double,
    itemTotalAmt: Double,
    itemAfterWalletTotalAmt: Double,
    itemWalletTotalAmt: Double,
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
                text = "Refund Options",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(14.dp))

            if (onlinePaidAmt > 0) {
                RefundOption(
                    selected = selected == "source_account",
                    title = "Refund to Source",
                    subtitle = buildString {
                        if (walletPaidAmt > 0) {
                            append("To ${onlineMethod ?: "source"}: ₹${formatAmount(itemAfterWalletTotalAmt)}")
                            append("\n")
                            append("To wallet: ₹${formatAmount(itemWalletTotalAmt)}")
                        } else {
                            append("₹${formatAmount(itemAfterWalletTotalAmt)}")
                        }
                    },
                    onClick = { onSelected("source_account") }
                )
            }

            RefundOption(
                selected = selected == "wallet_account",
                title = "Refund to Wallet",
                subtitle = "₹${formatAmount(itemTotalAmt)}",
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .selectable(
                selected = selected,
                onClick = onClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFD6D6D6))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun ReturnReasonCard(
    value: String,
    onValueChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Return Reason",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Please tell us why you are returning this item")
                },
                minLines = 4,
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}

@Composable
private fun ReturnFooter(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
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
                text = "Return",
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
            .padding(vertical = 7.dp),
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

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Return item not found.", fontSize = 16.sp)
    }
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