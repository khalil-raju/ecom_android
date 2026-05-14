// ui/routes/PaymentWebViewRoute.kt
package com.ecom.app.ui.routes.payment

import androidx.compose.runtime.Composable
import com.ecom.app.ui.screens.payment.PaymentWebViewScreen

@Composable
fun PaymentWebViewRoute(
    url: String,
    onPaymentSuccess: (orderToken: String) -> Unit,
    onPaymentFailed: () -> Unit
) {
    PaymentWebViewScreen(
        url = url,
        onPaymentFinished = { success, orderToken ->
            if (success && !orderToken.isNullOrBlank()) {
                onPaymentSuccess(orderToken)
            } else {
                onPaymentFailed()
            }
        }
    )
}