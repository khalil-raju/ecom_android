package com.ecom.app.ui.routes.account

import android.util.Log
import androidx.compose.runtime.*
import com.ecom.app.model.account.AuthStepResponse
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
    navigateLoginPassword: (String) -> Unit,
    onVerified: () -> Unit
) {
    var error by remember(contact, purpose) {
        mutableStateOf<String?>(null)
    }

    fun handleResponse(response: AuthStepResponse) {
        if (response.success && response.authenticated == true) {
            onVerified()
        } else if (response.success) {
            onVerified()
        } else {
            error = response.error ?: response.errorMsg ?: "OTP verification failed"
        }
    }

    OtpVerifyScreen(
        contact = contact,
        title = when (purpose) {
            OtpPurpose.LOGIN -> "Welcome"
            OtpPurpose.SIGNUP -> "Verify OTP"
            OtpPurpose.VERIFY_PHONE -> "Verify Phone"
            OtpPurpose.VERIFY_EMAIL -> "Verify Email"
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
                            RetrofitClient.apiService.loginOtp(
                                csrfToken = csrfToken,
                                otp = otp
                            )
                        }

                        OtpPurpose.SIGNUP -> {
                            RetrofitClient.apiService.signupOtp(
                                csrfToken = csrfToken,
                                otp = otp
                            )
                        }

                        OtpPurpose.VERIFY_PHONE,
                        OtpPurpose.VERIFY_EMAIL -> {
                            RetrofitClient.apiService.verifyContactOtp(
                                csrfToken = csrfToken,
                                otp = otp
                            )
                        }
                    }

                    handleResponse(response)

                } catch (e: Exception) {
                    error = e.message ?: "OTP verification failed"
                    Log.e("OTP_VERIFY", "failed: ${e.message}", e)
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

                        OtpPurpose.VERIFY_PHONE,
                        OtpPurpose.VERIFY_EMAIL -> {
                            RetrofitClient.apiService.resendVerifyContactOtp()
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