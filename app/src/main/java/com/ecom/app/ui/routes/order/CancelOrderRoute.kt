// ui/routes/CancelOrderRoute.kt
package com.ecom.app.ui.routes.order

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.model.order.CancelOrderResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.screens.order.CancelOrderScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CancelOrderRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    orderToken: String,
    navigateBack: () -> Unit,
    navigateOrderDetail: (String) -> Unit
) {
    var cancelOrderResponse by remember(orderToken) {
        mutableStateOf<CancelOrderResponse?>(null)
    }

    var cancelOrderError by remember(orderToken) {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(orderToken) {
        try {
            cancelOrderError = null
            cancelOrderResponse = RetrofitClient.apiService.getCancelOrder(orderToken)
        } catch (e: Exception) {
            cancelOrderError = e.message ?: "Unable to load cancel order."
            Log.e("CANCEL_ORDER", "failed: ${e.message}", e)
        }
    }

    CancelOrderScreen(
        modifier = Modifier.padding(innerPadding),
        response = cancelOrderResponse,
        error = cancelOrderError,
        onBack = navigateBack,
        onConfirmCancel = { reason, refundAccount ->
            scope.launch {
                try {
                    val csrfToken = RetrofitClient.getCsrfToken() ?: return@launch

                    val response = RetrofitClient.apiService.submitCancelOrder(
                        csrfToken = csrfToken,
                        orderToken = orderToken,
                        cancelReason = reason,
                        refundAccount = refundAccount
                    )

                    if (response.success && response.nextStep == "order_details") {
                        cancelOrderError = null
                        navigateOrderDetail(response.orderToken ?: orderToken)
                    } else {
                        cancelOrderError = response.error ?: "Unable to cancel order."
                    }
                } catch (e: Exception) {
                    cancelOrderError = e.message ?: "Unable to cancel order."
                    Log.e("CANCEL_SUBMIT", "failed: ${e.message}", e)
                }
            }
        }
    )
}