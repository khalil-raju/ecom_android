package com.ecom.app.ui.routes.account

import android.util.Log
import androidx.compose.runtime.*
import com.ecom.app.model.account.AuthResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.components.ScreenLoading
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
    onVerified: (AuthResponse) -> Unit
) {
    var response by remember(contact, purpose) {
        mutableStateOf<AuthResponse?>(null)
    }

    var error by remember(contact, purpose) {
        mutableStateOf<String?>(null)
    }

    var isLoading by remember(contact, purpose) {
        mutableStateOf(true)
    }

    var isSubmitting by remember(contact, purpose) {
        mutableStateOf(false)
    }

    LaunchedEffect(contact, purpose) {
        try {
            isLoading = true
            error = null

            val result = when (purpose) {
                OtpPurpose.LOGIN -> RetrofitClient.apiService.startLoginOtp()
                OtpPurpose.SIGNUP -> RetrofitClient.apiService.startSignupOtp()
                OtpPurpose.CHANGE_PHONE,
                OtpPurpose.CHANGE_EMAIL -> RetrofitClient.apiService.startChangeContactOtp()
            }

            response = result

            if (!result.success) {
                error = result.errorMsg ?: "Unable to send OTP"
            }

        } catch (e: Exception) {
            error = e.message ?: "Unable to send OTP"
            Log.e("OTP_START", "failed: ${e.message}", e)
        } finally {
            isLoading = false
        }
    }

    if (isLoading || response == null) {
        ScreenLoading(message = "Sending OTP...")
        return
    }

    if (isSubmitting) {
        ScreenLoading(message = "Verifying OTP...")
        return
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

        resendInitialSeconds = response?.otpRemainingTime ?: 60,

        error = error,
        onLogoClick = navigateHome,

        onVerifyOtp = { otp ->
            scope.launch {
                try {
                    isSubmitting = true
                    error = null

                    val csrfToken = RetrofitClient.getCsrfToken()
                    if (csrfToken == null) {
                        error = "Session not ready"
                        return@launch
                    }

                    val result = when (purpose) {
                        OtpPurpose.LOGIN -> RetrofitClient.apiService.submitLoginOtp(
                            csrfToken = csrfToken,
                            otp = otp
                        )

                        OtpPurpose.SIGNUP -> RetrofitClient.apiService.submitSignupOtp(
                            csrfToken = csrfToken,
                            otp = otp
                        )

                        OtpPurpose.CHANGE_PHONE,
                        OtpPurpose.CHANGE_EMAIL -> RetrofitClient.apiService.submitChangeContactOtp(
                            csrfToken = csrfToken,
                            otp = otp
                        )
                    }

                    response = result

                    if (result.success) {
                        onVerified(result)
                    } else {
                        error = result.errorMsg ?: "OTP verification failed"
                    }

                } catch (e: Exception) {
                    error = e.message ?: "OTP verification failed"
                    Log.e("OTP_SUBMIT", "failed: ${e.message}", e)
                } finally {
                    isSubmitting = false
                }
            }
        },

        onResendOtp = {
            scope.launch {
                try {
                    error = null

                    val result = when (purpose) {
                        OtpPurpose.LOGIN -> RetrofitClient.apiService.resendLoginOtp()
                        OtpPurpose.SIGNUP -> RetrofitClient.apiService.resendSignupOtp()
                        OtpPurpose.CHANGE_PHONE,
                        OtpPurpose.CHANGE_EMAIL -> RetrofitClient.apiService.resendChangeContactOtp()
                    }

                    response = result

                    if (!result.success) {
                        error = result.errorMsg ?: "Unable to resend OTP"
                    }

                } catch (e: Exception) {
                    error = e.message ?: "Unable to resend OTP"
                    Log.e("OTP_RESEND", "failed: ${e.message}", e)
                }
            }
        }
    )
}