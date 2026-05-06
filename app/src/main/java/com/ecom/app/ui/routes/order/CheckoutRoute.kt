package com.ecom.app.ui.routes.order

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ecom.app.BuildConfig
import com.ecom.app.model.order.CheckoutResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.screens.order.CheckoutScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CheckoutRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    checkoutResponse: CheckoutResponse?,
    navigateCart: () -> Unit,
    navigatePaymentWeb: (String) -> Unit,
    onAddAddressClick: () -> Unit = {}
) {
    CheckoutScreen(
        modifier = Modifier.padding(innerPadding),
        checkout = checkoutResponse,
        onBack = navigateCart,
        onAddAddressClick = onAddAddressClick,
        onProceedToPayment = { shippingAddressId, billingAddressId, useWallet, paymentMethod ->
            scope.launch {
                try {
                    val csrfToken = RetrofitClient.getCsrfToken() ?: return@launch
                    val orderToken = checkoutResponse?.order?.orderToken ?: return@launch

                    val response = RetrofitClient.apiService.initiateOrder(
                        csrfToken = csrfToken,
                        orderToken = orderToken,
                        shippingAddressId = shippingAddressId,
                        billingAddressId = billingAddressId,
                        useWallet = if (useWallet) "1" else "0",
                        paymentMethod = paymentMethod
                    )

                    val token = response.orderToken

                    when (response.nextStep) {
                        "initiate_rzp_payment" -> {
                            if (token.isNullOrBlank()) return@launch

                            navigatePaymentWeb(
                                "${BuildConfig.BASE_URL.trimEnd('/')}/payments/initiate/rzp/payment/$token/"
                            )
                        }

                        "initiate_cod_payment" -> {
                            if (token.isNullOrBlank()) return@launch

                            navigatePaymentWeb(
                                "${BuildConfig.BASE_URL.trimEnd('/')}/payments/initiate/cod/payment/$token/"
                            )
                        }

                        "finalize_order" -> {
                            if (token.isNullOrBlank()) return@launch

                            navigatePaymentWeb(
                                "${BuildConfig.BASE_URL.trimEnd('/')}/orders/finalize/order/$token"
                            )
                        }

                        "basket_detail" -> {
                            navigateCart()
                        }
                    }

                } catch (e: Exception) {
                    Log.e("CHECKOUT_PAY", "failed: ${e.message}", e)
                }
            }
        }
    )
}