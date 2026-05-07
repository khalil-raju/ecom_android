package com.ecom.app.ui.routes.account

import android.util.Log
import androidx.compose.runtime.*
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.navigations.OtpPurpose
import com.ecom.app.ui.screens.account.SignupPasswordScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SignupPasswordRoute(
    scope: CoroutineScope,
    contact: String,
    navigateHome: () -> Unit,
    navigateLogin: () -> Unit,
    navigateOtp: (String, OtpPurpose) -> Unit
) {
    var error by remember(contact) { mutableStateOf<String?>(null) }

    SignupPasswordScreen(
        contact = contact,
        error = error,
        onLogoClick = navigateHome,
        onLoginClick = navigateLogin,
        onContinue = { password, confirm ->
            scope.launch {
                try {
                    error = null

                    val csrfToken = RetrofitClient.getCsrfToken()
                    if (csrfToken == null) {
                        error = "Session not ready. Please try again."
                        return@launch
                    }

                    val response = RetrofitClient.apiService.signupPassword(
                        csrfToken = csrfToken,
                        password = password,
                        confirm = confirm
                    )

                    if (response.success && response.nextStep == "signup_otp") {
                        navigateOtp(contact, OtpPurpose.SIGNUP)
                    } else {
                        error = response.error ?: response.errorMsg ?: "Unable to continue."
                    }

                } catch (e: Exception) {
                    error = e.message ?: "Unable to continue."
                    Log.e("SIGNUP_PASSWORD", "failed: ${e.message}", e)
                }
            }
        }
    )
}