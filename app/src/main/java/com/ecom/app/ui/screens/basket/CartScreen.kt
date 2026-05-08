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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CircularProgressIndicator
import coil.compose.AsyncImage
import com.ecom.app.BuildConfig
import com.ecom.app.R
import com.ecom.app.model.basket.BasketItem
import com.ecom.app.model.basket.BasketResponse

private fun fullUrl(path: String?): String? {
    return path?.let {
        if (it.startsWith("http")) it
        else BuildConfig.BASE_URL.trimEnd('/') + it
    }
}

@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    basket: BasketResponse?,
    isLoading: Boolean = false,
    onBack: () -> Unit,
    onNavigateToProduct: (Int, String) -> Unit,
    onQuantityChange: (BasketItem, Int) -> Unit,
    onCheckoutClick: () -> Unit
) {
    val cartItems = basket?.cartItems.orEmpty()
    val total = basket?.cartTotal ?: 0.0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        CartHeader(onBack = onBack)

        if (isLoading && basket == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (cartItems.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "🛍",
                        fontSize = 64.sp
                    )

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

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
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
                        onQuantityChange = onQuantityChange,
                    )
                }
            }
        }

        if(total > 0.0) {
            CartFooter(
                total = total,
                onCheckoutClick = onCheckoutClick
            )
        }
    }
}

@Composable
private fun CartHeader(onBack: () -> Unit) {
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
            text = "My Shopping Bag",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CartItemCard(
    item: BasketItem,
    onProductClick: (variantId: Int?, slug: String) -> Unit,
    onQuantityChange: (BasketItem, Int) -> Unit,
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onProductClick(item.variantId, item.slug)
                            }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Size: ${item.size}",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${item.price ?: ""}",
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
private fun QuantityControl(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color(0xFFDADADA),   // light gray like design
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "−",
            fontSize = 22.sp,
            modifier = Modifier.clickable { onQuantityChange(-1) }
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(quantity.toString(), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            "+",
            fontSize = 22.sp,
            modifier = Modifier.clickable { onQuantityChange(1) }
        )
    }
}

@Composable
private fun CartFooter(
    total: Double,
    onCheckoutClick: () -> Unit
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
            text = "Total: ₹$total",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onCheckoutClick,
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
                text = "Proceed to Checkout",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}