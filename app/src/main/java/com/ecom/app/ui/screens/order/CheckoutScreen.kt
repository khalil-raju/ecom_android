package com.ecom.app.ui.screens.order

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ecom.app.BuildConfig
import com.ecom.app.R
import com.ecom.app.model.account.Address
import com.ecom.app.model.order.CheckoutResponse
import com.ecom.app.model.order.Order
import com.ecom.app.model.order.OrderItem
import com.ecom.app.ui.components.ScreenHeader

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

        ScreenHeader(
            title = "Checkout",
            subtitle = "Review your order and payment"
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                SectionTitle("Shipping Address")
                AddressDropdownCard(
                    selectedAddress = shippingAddress,
                    addresses = addresses,
                    onAddressSelected = { shippingAddress = it }
                )
            }

            item {
                SectionTitle("Billing Address")
                AddressDropdownCard(
                    selectedAddress = billingAddress,
                    addresses = addresses,
                    onAddressSelected = { billingAddress = it }
                )
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

            item {
                ProceedPaymentCard(
                    total = order?.totalAmount ?: 0.0,
                    paymentMethod = paymentMethod,
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

            item {
                Spacer(Modifier.height(12.dp))
            }
        }
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
private fun AddressDropdownCard(
    selectedAddress: Address?,
    addresses: List<Address>,
    onAddressSelected: (Address) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

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
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Color(0xFFDADADA),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { expanded = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedAddress?.label?.ifBlank { "Address" } ?: "Select Address",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = if (selectedAddress != null) {
                                    "${selectedAddress.fullName} • ${selectedAddress.phone}"
                                } else {
                                    "Choose from saved addresses"
                                },
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Text(
                            text = if (expanded) "⌃" else "⌄",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        addresses.forEach { address ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = address.label.ifBlank { "Address" },
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${address.fullName} • ${address.phone}",
                                            fontSize = 13.sp,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = buildAddressText(address).replace("\n", ", "),
                                            fontSize = 13.sp,
                                            color = Color.DarkGray
                                        )
                                    }
                                },
                                onClick = {
                                    onAddressSelected(address)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = buildAddressText(selectedAddress),
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
        address.line1,
        address.line2,
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
        Text(text = text, fontSize = 24.sp)
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
                Text(
                    text = "Item",
                    modifier = Modifier.weight(2.6f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Price",
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End
                )

                Text(
                    text = "Qty",
                    modifier = Modifier.weight(0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Subtotal",
                    modifier = Modifier.weight(1.2f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End
                )
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
                SummaryRow("Item(s) Total", order?.itemsTotalAmt ?: 0.0)
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
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(2.6f),
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

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = item.variantName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Size: ${item.variantSize ?: ""}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        Text(
            text = "₹${formatAmount(item.price)}",
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            textAlign = TextAlign.End
        )

        Text(
            text = item.quantity.toString(),
            modifier = Modifier.weight(0.7f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = "₹${formatAmount(item.totalAmt)}",
            modifier = Modifier.weight(1.2f),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End
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
private fun ProceedPaymentCard(
    total: Double,
    paymentMethod: String,
    onProceed: () -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Grand Total",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "₹${formatAmount(total)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onProceed,
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
                    text = if (paymentMethod == "COD") {
                        "Place Order"
                    } else {
                        "Proceed to Payment"
                    },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun formatAmount(value: Double): String {
    return "%.2f".format(value)
}