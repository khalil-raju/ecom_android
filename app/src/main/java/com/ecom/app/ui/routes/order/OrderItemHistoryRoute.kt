package com.ecom.app.ui.routes.order

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.model.order.OrderItemHistoryResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.components.ScreenLoading
import com.ecom.app.ui.screens.order.OrderItemHistoryScreen

@Composable
fun OrderItemHistoryRoute(
    innerPadding: PaddingValues,
    navigateBack: () -> Unit,
    navigateOrderItemDetail: (String) -> Unit
) {
    var orderItemHistoryResponse by remember {
        mutableStateOf<OrderItemHistoryResponse?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        try {
            isLoading = true

            val response = RetrofitClient.apiService.getOrderItemHistory()

            orderItemHistoryResponse = response

        } catch (e: Exception) {
            Log.e("ORDER_ITEM_HISTORY", "failed: ${e.message}", e)
        } finally {
            isLoading = false
        }
    }

    if (isLoading || orderItemHistoryResponse == null) {
        ScreenLoading(message = "Loading orders...")
        return
    }

    OrderItemHistoryScreen(
        modifier = Modifier.padding(innerPadding),
        response = orderItemHistoryResponse,

        onItemClick = { item ->
            val token = item.itemToken

            navigateOrderItemDetail(token)
        }
    )
}