// ui/routes/ReturnOrderItemRoute.kt
package com.ecom.app.ui.routes.order

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.model.order.ReturnOrderItemResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.screens.order.ReturnOrderItemScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ReturnOrderItemRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    itemToken: String,
    navigateBack: () -> Unit,
    navigateOrderItemDetail: (String) -> Unit
) {
    var returnOrderItemResponse by remember(itemToken) {
        mutableStateOf<ReturnOrderItemResponse?>(null)
    }

    var returnOrderItemError by remember(itemToken) {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(itemToken) {
        try {
            returnOrderItemError = null

            returnOrderItemResponse =
                RetrofitClient.apiService.getReturnOrderItem(itemToken)

        } catch (e: Exception) {
            returnOrderItemError =
                e.message ?: "Unable to load return order item."

            Log.e("RETURN_ITEM", "failed: ${e.message}", e)
        }
    }

    ReturnOrderItemScreen(
        modifier = Modifier.padding(innerPadding),
        response = returnOrderItemResponse,
        error = returnOrderItemError,

        onBack = navigateBack,

        onSubmitReturn = { returnReason, refundAccount ->
            scope.launch {
                try {
                    val csrfToken =
                        RetrofitClient.getCsrfToken() ?: return@launch

                    val response =
                        RetrofitClient.apiService.submitReturnOrderItem(
                            csrfToken = csrfToken,
                            itemToken = itemToken,
                            returnReason = returnReason,
                            refundAccount = refundAccount
                        )

                    if (
                        response.success &&
                        response.nextStep == "order_item_details"
                    ) {
                        returnOrderItemError = null

                        navigateOrderItemDetail(
                            response.itemToken ?: itemToken
                        )

                    } else {
                        returnOrderItemError =
                            response.error
                                ?: "Unable to submit return request."
                    }

                } catch (e: Exception) {
                    returnOrderItemError =
                        e.message
                            ?: "Unable to submit return request."

                    Log.e("RETURN_SUBMIT", "failed: ${e.message}", e)
                }
            }
        }
    )
}