package com.ecom.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.ecom.app.model.Address
import com.ecom.app.model.CheckoutResponse
import com.ecom.app.model.Order
import com.ecom.app.model.OrderItem

private fun fullUrl(path: String?): String? {
    return path?.let {
        if (it.startsWith("http")) it
        else BuildConfig.BASE_URL.trimEnd('/') + it
    }
}

@Composable
fun CheckoutScreen(
    modifier: Modifier = Modifier,
    checkout: CheckoutResponse?,
    onBack: () -> Unit,
    onAddAddressClick: () -> Unit = {},
    onProceedToPayment: (
        shippingAddressId: Int?,
        billingAddressId: Int?,
        useWallet: Boolean,
        paymentMethod: String
    ) -> Unit
) {
    val addresses = checkout?.allAddress.orEmpty()
    var shippingAddress by remember(checkout) {
        mutableStateOf(checkout?.selectedAddress ?: addresses.firstOrNull())
    }
    var billingAddress by remember(checkout) {
        mutableStateOf(checkout?.selectedAddress ?: addresses.firstOrNull())
    }

    var useWallet by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf("ONLINE") }

    val order = checkout?.order
    val orderItems = checkout?.orderItems.orEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        CheckoutHeader(onBack = onBack)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                SectionTitle("Shipping Address")
                AddressCard(address = shippingAddress)
            }

            item {
                SectionTitle("Billing Address")
                AddressCard(address = billingAddress)
            }

            item {
                AddAddressCard(onClick = onAddAddressClick)
            }

            item {
                SectionTitle("Order Items")
                OrderItemsCard(
                    orderItems = orderItems,
                    order = order
                )
            }

            item {
                SectionTitle("Payment Method")
                PaymentMethodCard(
                    walletBalance = checkout?.walletBalance,
                    useWallet = useWallet,
                    onWalletChange = { useWallet = it },
                    paymentMethod = paymentMethod,
                    onPaymentMethodChange = { paymentMethod = it }
                )
            }
        }

        CheckoutFooter(
            total = order?.totalAmount ?: 0.0,
            onProceed = {
                onProceedToPayment(
                    shippingAddress?.id,
                    billingAddress?.id,
                    useWallet,
                    paymentMethod
                )
            }
        )
    }
}

@Composable
private fun CheckoutHeader(onBack: () -> Unit) {
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
            text = "Checkout",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun AddressCard(address: Address?) {
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
            CircleIcon("⌖")

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color(0xFFDADADA),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = address?.label?.ifBlank { "Address" } ?: "Address",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${address?.fullName ?: ""} • ${address?.phone ?: ""}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Text("⌄", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = buildAddressText(address),
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

private fun buildAddressText(address: Address?): String {
    if (address == null) return ""

    return listOfNotNull(
        address.addressLine1,
        address.addressLine2,
        "${address.city}, ${address.state} - ${address.postalCode}"
    )
        .filter { it.isNotBlank() }
        .joinToString("\n")
}

@Composable
private fun AddAddressCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleIcon("+")

            Spacer(modifier = Modifier.width(18.dp))

            Text(
                text = "Add new address",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Text("›", fontSize = 34.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CircleIcon(text: String) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color(0xFFF0F0F0)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 26.sp)
    }
}

@Composable
private fun OrderItemsCard(
    orderItems: List<OrderItem>,
    order: Order?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Item", modifier = Modifier.weight(2.1f), fontSize = 14.sp)
                Text("Price", modifier = Modifier.weight(0.9f), fontSize = 14.sp)
                Text("Qty", modifier = Modifier.weight(0.6f), fontSize = 14.sp)
                Text("Subtotal", modifier = Modifier.weight(1f), fontSize = 14.sp)
            }

            HorizontalDivider()

            orderItems.forEachIndexed { index, item ->
                OrderItemRow(item = item)

                if (index != orderItems.lastIndex) {
                    HorizontalDivider()
                }
            }

            HorizontalDivider()

            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                SummaryRow("Items Total", order?.itemsTotalAmt ?: 0.0)
                SummaryRow("Shipping", order?.shippingCost ?: 0.0)

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = Color(0xFFDADADA)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Grand Total",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "₹${formatAmount(order?.totalAmount ?: 0.0)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderItemRow(item: OrderItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(2.1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = fullUrl(item.variantImageUrl),
                contentDescription = item.variantName,
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = item.variantName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Size: ${item.variantSize ?: ""}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "₹${formatAmount(item.price)}",
            modifier = Modifier.weight(0.9f),
            fontSize = 14.sp
        )

        Text(
            text = item.quantity.toString(),
            modifier = Modifier.weight(0.6f),
            fontSize = 14.sp
        )

        Text(
            text = "₹${formatAmount(item.totalAmt)}",
            modifier = Modifier.weight(1f),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 16.sp)
        Text("₹${formatAmount(value)}", fontSize = 16.sp)
    }
}

@Composable
private fun PaymentMethodCard(
    walletBalance: Double?,
    useWallet: Boolean,
    onWalletChange: (Boolean) -> Unit,
    paymentMethod: String,
    onPaymentMethodChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (walletBalance != null && walletBalance > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onWalletChange(!useWallet) }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = useWallet,
                        onCheckedChange = onWalletChange
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    CircleIcon("▣")

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = "Use Wallet (Balance: ₹${formatAmount(walletBalance)})",
                        fontSize = 16.sp
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
            }

            PaymentOption(
                selected = paymentMethod == "ONLINE",
                text = "Pay Now (UPI, Cards, Netbanking)",
                icon = "▤",
                onClick = { onPaymentMethodChange("ONLINE") }
            )

            PaymentOption(
                selected = paymentMethod == "COD",
                text = "Cash On Delivery (COD)",
                icon = "₹",
                onClick = { onPaymentMethodChange("COD") }
            )
        }
    }
}

@Composable
private fun PaymentOption(
    selected: Boolean,
    text: String,
    icon: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )

        Spacer(modifier = Modifier.width(12.dp))

        CircleIcon(icon)

        Spacer(modifier = Modifier.width(14.dp))

        Text(text = text, fontSize = 16.sp)
    }
}

@Composable
private fun CheckoutFooter(
    total: Double,
    onProceed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 18.dp)
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Grand Total: ₹$total",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onProceed,
            modifier = Modifier
                .width(260.dp)
                .height(46.dp),
            border = BorderStroke(3.dp, Color.Black),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Proceed to Payment",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun formatAmount(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        "%.2f".format(value)
    }
}