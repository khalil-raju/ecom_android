// ui/routes/OrderItemHistoryRoute.kt
package com.ecom.app.ui.routes.order

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.model.order.OrderItemHistoryResponse
import com.ecom.app.network.RetrofitClient
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

    LaunchedEffect(Unit) {
        try {
            orderItemHistoryResponse =
                RetrofitClient.apiService.getOrderItemHistory()
        } catch (e: Exception) {
            Log.e("ORDER_ITEM_HISTORY", "failed: ${e.message}", e)
        }
    }

    OrderItemHistoryScreen(
        modifier = Modifier.padding(innerPadding),
        response = orderItemHistoryResponse,

        onBack = navigateBack,

        onItemClick = { item ->
            val token = item.itemToken ?: return@OrderItemHistoryScreen

            navigateOrderItemDetail(token)
        }
    )
}