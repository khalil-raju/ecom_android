package com.ecom.app.ui.screens.wallet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecom.app.R
import com.ecom.app.model.wallet.WalletResponse
import com.ecom.app.model.wallet.WalletTxn
import com.ecom.app.ui.components.ScreenHeader

@Composable
fun WalletScreen(
    modifier: Modifier = Modifier,
    response: WalletResponse?,
) {
    val txns = response?.txns.orEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {

        ScreenHeader(
            title = "My Wallet",
            subtitle = "View your balance & transaction details"
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                BalanceCard(balance = response?.walletBalance ?: 0.0)
            }

            item {
                HowWalletWorksCard()
            }

            item {
                Text(
                    text = "Recent Wallet Activity",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (txns.isEmpty()) {
                item {
                    EmptyTxnCard()
                }
            } else {
                items(txns) { txn ->
                    WalletTxnCard(txn = txn)
                }
            }

            item {
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun WalletHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(14.dp),
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

        Spacer(Modifier.width(18.dp))

        Text(
            text = "My Wallet",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BalanceCard(balance: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFFAF1)),
        border = BorderStroke(1.dp, Color(0xFFD8EBDD))
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text(
                text = "Wallet Balance",
                fontSize = 16.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "₹${formatAmount(balance)}",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0A8F2E)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Available for future orders",
                fontSize = 15.sp,
                color = Color.DarkGray
            )

            Spacer(Modifier.height(14.dp))

            Surface(
                color = Color.White,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFFDDEDDD))
            ) {
                Text(
                    text = "✓ 100% Secure • Only from refunds",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    fontSize = 13.sp,
                    color = Color(0xFF1B5E20),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun HowWalletWorksCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "How Wallet Works",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0A7A2A)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Your wallet balance comes from refunds for returned or cancelled orders. You can use this balance automatically during checkout.",
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun WalletTxnCard(txn: WalletTxn) {
    val isCredit = txn.txnType.equals("DEPOSIT", ignoreCase = true)
    val amountColor = if (isCredit) Color(0xFF0A8F2E) else Color(0xFFD32F2F)
    val prefix = if (isCredit) "+" else "-"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isCredit) Color(0xFFE4F8E8) else Color(0xFFFFE6E8)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isCredit) "↓" else "↑",
                    fontSize = 26.sp,
                    color = amountColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isCredit) "Refund Received" else "Used for Order",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = txn.notes.ifBlank { txn.txnTypeDisplay },
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )

                txn.createdAt?.let {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = it.take(10),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Text(
                text = "$prefix ₹${formatAmount(txn.amount)}",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}

@Composable
private fun EmptyTxnCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Text(
            text = "No wallet activity yet.",
            modifier = Modifier.padding(20.dp),
            fontSize = 15.sp,
            color = Color.Gray
        )
    }
}

private fun formatAmount(value: Double): String {
    return if (value % 1.0 == 0.0) value.toInt().toString()
    else "%.2f".format(value)
}