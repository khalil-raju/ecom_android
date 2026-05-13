package com.ecom.app.ui.routes.order

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import com.ecom.app.ui.components.ScreenLoading
import com.ecom.app.ui.screens.order.OrderItemHistoryScreen
import com.ecom.app.model.order.OrderItemHistoryResponse
import com.ecom.app.network.RetrofitClient


@Composable
fun OrderItemHistoryRoute(
    innerPadding: PaddingValues,
    navigateLogin: () -> Unit,
    navigateOrderItemDetail: (String) -> Unit
) {
    var orderItemHistoryResponse by remember {
        mutableStateOf<OrderItemHistoryResponse?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var showLoginRequired by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        try {
            isLoading = true

            val response = RetrofitClient.apiService.getOrderItemHistory()

            orderItemHistoryResponse = response

        } catch (e: Exception) {
            Log.e("ORDER_ITEM_HISTORY", "failed: ${e.message}", e)

            showLoginRequired = true

        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        ScreenLoading(message = "Loading orders...")
        return
    }

    if (showLoginRequired) {

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Please login to see your order history.",
                    fontSize = 16.sp
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = navigateLogin
                ) {
                    Text("Login")
                }
            }
        }

        return
    }

    if (orderItemHistoryResponse == null) {
        return
    }

    OrderItemHistoryScreen(
        modifier = Modifier.padding(innerPadding),
        response = orderItemHistoryResponse,

        onItemClick = { item ->
            navigateOrderItemDetail(item.itemToken)
        }
    )
}