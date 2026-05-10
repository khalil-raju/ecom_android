package com.ecom.app.ui.routes.account

import android.util.Log
import androidx.compose.runtime.*
import com.ecom.app.model.account.AuthStepResponse
import com.ecom.app.model.account.ProfileResponse
import com.ecom.app.model.order.CheckoutResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.navigations.OtpPurpose
import com.ecom.app.ui.screens.account.OtpVerifyScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun OtpVerifyRoute(
    scope: CoroutineScope,
    contact: String,
    purpose: OtpPurpose,
    navigateHome: () -> Unit,
    onVerified: (AuthStepResponse) -> Unit,
    setCheckoutResponse: (CheckoutResponse) -> Unit,
    onProfileUpdated: (ProfileResponse) -> Unit
) {
    var error by remember(contact, purpose) {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(contact, purpose) {
        try {
            error = null

            val response = when (purpose) {
                OtpPurpose.LOGIN -> {
                    RetrofitClient.apiService.startLoginOtp()
                }

                OtpPurpose.SIGNUP -> {
                    RetrofitClient.apiService.startSignupOtp()
                }

                OtpPurpose.CHANGE_PHONE,
                OtpPurpose.CHANGE_EMAIL -> {
                    RetrofitClient.apiService.startChangeContactOtp()
                }
            }

            if (!response.success) {
                error = response.error ?: response.errorMsg ?: "Unable to send OTP"
            }

        } catch (e: Exception) {
            error = e.message ?: "Unable to send OTP"
            Log.e("OTP_START", "failed: ${e.message}", e)
        }
    }

    fun handleSubmitResponse(response: AuthStepResponse) {
        if (response.success) {
            scope.launch {
                if (
                    purpose == OtpPurpose.CHANGE_PHONE ||
                    purpose == OtpPurpose.CHANGE_EMAIL
                ) {
                    try {
                        val profile = RetrofitClient.apiService.getProfile()
                        onProfileUpdated(profile)
                    } catch (e: Exception) {
                        Log.e("OTP_PROFILE_REFRESH", "failed: ${e.message}", e)
                    }
                }

                onVerified(response)
                Log.d(
                    "OTP_SUBMIT_RESPONSE",
                    "success=${response.success}, authenticated=${response.authenticated}, nextStep=${response.nextStep}"
                )

                if (response.nextStep == "/orders/checkout/") {
                    val checkoutResponse = RetrofitClient.apiService.getCheckout()
                    if (checkoutResponse.success) {
                        setCheckoutResponse(checkoutResponse)
                    }
                }
            }
        } else {
            error = response.error ?: response.errorMsg ?: "OTP verification failed"
        }
    }

    OtpVerifyScreen(
        contact = contact,
        title = when (purpose) {
            OtpPurpose.LOGIN -> "Welcome"
            OtpPurpose.SIGNUP -> "Verify OTP"
            OtpPurpose.CHANGE_PHONE -> "Verify Phone"
            OtpPurpose.CHANGE_EMAIL -> "Verify Email"
        },
        buttonText = if (purpose == OtpPurpose.LOGIN) "Login" else "Verify",
        resendInitialSeconds = 60,
        error = error,
        onLogoClick = navigateHome,

        onVerifyOtp = { otp ->
            scope.launch {
                try {
                    error = null

                    val csrfToken = RetrofitClient.getCsrfToken()
                    if (csrfToken == null) {
                        error = "Session not ready"
                        return@launch
                    }

                    val response = when (purpose) {
                        OtpPurpose.LOGIN -> {
                            RetrofitClient.apiService.submitLoginOtp(
                                csrfToken = csrfToken,
                                otp = otp
                            )
                        }

                        OtpPurpose.SIGNUP -> {
                            RetrofitClient.apiService.submitSignupOtp(
                                csrfToken = csrfToken,
                                otp = otp
                            )
                        }

                        OtpPurpose.CHANGE_PHONE,
                        OtpPurpose.CHANGE_EMAIL -> {
                            RetrofitClient.apiService.submitChangeContactOtp(
                                csrfToken = csrfToken,
                                otp = otp
                            )
                        }
                    }

                    handleSubmitResponse(response)

                } catch (e: Exception) {
                    error = e.message ?: "OTP verification failed"
                    Log.e("OTP_SUBMIT", "failed: ${e.message}", e)
                }
            }
        },

        onResendOtp = {
            scope.launch {
                try {
                    error = null

                    val response = when (purpose) {
                        OtpPurpose.LOGIN -> {
                            RetrofitClient.apiService.resendLoginOtp()
                        }

                        OtpPurpose.SIGNUP -> {
                            RetrofitClient.apiService.resendSignupOtp()
                        }

                        OtpPurpose.CHANGE_PHONE,
                        OtpPurpose.CHANGE_EMAIL -> {
                            RetrofitClient.apiService.resendChangeContactOtp()
                        }
                    }

                    if (!response.success) {
                        error = response.error ?: response.errorMsg ?: "Unable to resend OTP"
                    }

                } catch (e: Exception) {
                    error = e.message ?: "Unable to resend OTP"
                    Log.e("OTP_RESEND", "failed: ${e.message}", e)
                }
            }
        }
    )
}