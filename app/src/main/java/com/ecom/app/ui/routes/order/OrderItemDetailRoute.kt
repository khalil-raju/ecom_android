// ui/routes/OrderItemDetailRoute.kt
package com.ecom.app.ui.routes.order

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.model.product.ProductDetailResponse
import com.ecom.app.model.order.OrderItemDetailResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.screens.order.OrderItemDetailScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun OrderItemDetailRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    itemToken: String,
    navigateBack: () -> Unit,
    navigateOrderDetail: (String) -> Unit,
    navigateReturnOrderItem: (String) -> Unit,
    navigateReviewOrderItem: (String) -> Unit,
    navigateProductDetail: (ProductDetailResponse) -> Unit
) {
    var orderItemDetailResponse by remember(itemToken) {
        mutableStateOf<OrderItemDetailResponse?>(null)
    }

    LaunchedEffect(itemToken) {
        try {
            orderItemDetailResponse =
                RetrofitClient.apiService.getOrderItemDetail(itemToken)
        } catch (e: Exception) {
            Log.e("ORDER_ITEM_DETAIL", "failed: ${e.message}", e)
        }
    }

    OrderItemDetailScreen(
        modifier = Modifier.padding(innerPadding),
        response = orderItemDetailResponse,

        onBack = navigateBack,

        onOrderDetailClick = { orderToken ->
            navigateOrderDetail(orderToken)
        },

        onReturnItemClick = { itemToken ->
            navigateReturnOrderItem(itemToken)
        },

        onReviewItemClick = { itemToken ->
            navigateReviewOrderItem(itemToken)
        },

        onTrackItemClick = { itemToken ->
            // later
        },

        onSupportClick = { itemToken ->
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