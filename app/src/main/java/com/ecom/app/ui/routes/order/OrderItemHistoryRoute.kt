package com.ecom.app.ui.routes.order

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecom.app.model.order.OrderItem
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.components.ScreenLoading
import com.ecom.app.ui.screens.order.OrderItemHistoryScreen
import kotlinx.coroutines.launch

@Composable
fun OrderItemHistoryRoute(
    innerPadding: PaddingValues,
    navigateLogin: () -> Unit,
    navigateOrderItemDetail: (String) -> Unit
) {
    val scope = rememberCoroutineScope()

    var orderItems by remember {
        mutableStateOf<List<OrderItem>>(emptyList())
    }

    var nextOffset by remember {
        mutableIntStateOf(0)
    }

    var hasMore by remember {
        mutableStateOf(true)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var isLoadingMore by remember {
        mutableStateOf(false)
    }

    var showLoginRequired by remember {
        mutableStateOf(false)
    }

    fun loadHistory(reset: Boolean = false) {
        if (isLoadingMore) return
        if (!reset && !hasMore) return

        scope.launch {
            try {
                if (reset) {
                    isLoading = true
                    showLoginRequired = false
                    orderItems = emptyList()
                    nextOffset = 0
                    hasMore = true
                } else {
                    isLoadingMore = true
                }

                val offsetToLoad = if (reset) 0 else nextOffset
                val limitToLoad = 10

                val response = RetrofitClient.apiService.getOrderItemHistory(
                    limit = limitToLoad,
                    offset = offsetToLoad
                )

                orderItems = if (reset) {
                    response.orderItems
                } else {
                    orderItems + response.orderItems
                }

                nextOffset = offsetToLoad + response.orderItems.size
                hasMore = response.hasMore

            } catch (e: Exception) {
                Log.e("ORDER_ITEM_HISTORY", "failed: ${e.message}", e)

                if (reset) {
                    showLoginRequired = true
                }

            } finally {
                isLoading = false
                isLoadingMore = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadHistory(reset = true)
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

    OrderItemHistoryScreen(
        modifier = Modifier.padding(innerPadding),
        items = orderItems,
        isLoadingMore = isLoadingMore,
        hasMore = hasMore,
        onLoadMore = {
            loadHistory(reset = false)
        },
        onItemClick = { item ->
            navigateOrderItemDetail(item.itemToken)
        }
    )
}