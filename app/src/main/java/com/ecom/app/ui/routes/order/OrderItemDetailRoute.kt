package com.ecom.app.ui.routes.order

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.model.order.OrderItemDetailResponse
import com.ecom.app.model.product.ProductDetailResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.components.ScreenLoading
import com.ecom.app.ui.screens.order.OrderItemDetailScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun OrderItemDetailRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    itemToken: String,
    navigateOrderDetail: (String) -> Unit,
    navigateReturnOrderItem: (String) -> Unit,
    navigateReviewOrderItem: (String) -> Unit,
    navigateProductDetail: (ProductDetailResponse) -> Unit
) {
    var orderItemDetailResponse by remember(itemToken) {
        mutableStateOf<OrderItemDetailResponse?>(null)
    }

    var isLoading by remember(itemToken) {
        mutableStateOf(true)
    }

    LaunchedEffect(itemToken) {
        try {
            isLoading = true

            val response = RetrofitClient.apiService.getOrderItemDetail(itemToken)
            orderItemDetailResponse = response

        } catch (e: Exception) {
            Log.e("ORDER_ITEM_DETAIL", "failed: ${e.message}", e)
        } finally {
            isLoading = false
        }
    }

    if (isLoading || orderItemDetailResponse == null) {
        ScreenLoading(message = "Loading order item...")
        return
    }

    OrderItemDetailScreen(
        modifier = Modifier.padding(innerPadding),
        response = orderItemDetailResponse,

        onOrderDetailClick = { orderToken ->
            navigateOrderDetail(orderToken)
        },

        onReturnItemClick = { token ->
            navigateReturnOrderItem(token)
        },

        onReviewItemClick = { token ->
            navigateReviewOrderItem(token)
        },

        onTrackItemClick = {
            // later
        },

        onSupportClick = {
            // later
        },

        onProductClick = { variantId, slug ->
            scope.launch {
                try {
                    val detail = RetrofitClient.apiService.getProductDetail(
                        variantId = variantId,
                        slug = slug
                    )

                    navigateProductDetail(detail)

                } catch (e: Exception) {
                    Log.e("ITEM_PRODUCT", "failed: ${e.message}", e)
                }
            }
        }
    )
}