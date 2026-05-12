package com.ecom.app.ui.routes.order

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.BuildConfig
import com.ecom.app.model.order.CheckoutResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.components.ScreenLoading
import com.ecom.app.ui.screens.order.CheckoutScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CheckoutRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    navigateCart: () -> Unit,
    navigatePaymentWeb: (String) -> Unit,
    navigateAddAddress: () -> Unit,
    navigateLogin: () -> Unit,
    navigateSignupOtp: (String) -> Unit
) {
    var checkoutResponse by remember {
        mutableStateOf<CheckoutResponse?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        try {
            isLoading = true

            val response = RetrofitClient.apiService.getCheckout()

            when (response.nextStep) {
                "checkout" -> {
                    checkoutResponse = response
                }

                "login_contact" -> {
                    navigateLogin()
                }

                "add_address" -> {
                    navigateAddAddress()
                }

                "signup_otp" -> {
                    navigateSignupOtp(response.contact.orEmpty())
                }

                "basket_detail" -> {
                    navigateCart()
                }

                else -> {
                    Log.e("CHECKOUT_GET", "unexpected next_step: ${response.nextStep}")
                }
            }

        } catch (e: Exception) {
            Log.e("CHECKOUT_GET", "failed: ${e.message}", e)
        } finally {
            isLoading = false
        }
    }

    if (isLoading || checkoutResponse == null) {
        ScreenLoading(message = "Preparing checkout...")
        return
    }

    CheckoutScreen(
        modifier = Modifier.padding(innerPadding),
        checkout = checkoutResponse,
        onAddAddressClick = navigateAddAddress,
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

                        else -> {
                            Log.e("CHECKOUT_PAY", "unexpected next_step: ${response.nextStep}")
                        }
                    }

                } catch (e: Exception) {
                    Log.e("CHECKOUT_PAY", "failed: ${e.message}", e)
                }
            }
        }
    )
}