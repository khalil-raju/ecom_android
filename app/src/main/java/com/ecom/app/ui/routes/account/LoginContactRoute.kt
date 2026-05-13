package com.ecom.app.ui.routes.account

import android.util.Log
import androidx.compose.runtime.*
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.components.ScreenLoading
import com.ecom.app.ui.screens.account.LoginContactScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginContactRoute(
    scope: CoroutineScope,
    navigateHome: () -> Unit,
    navigateLoginPassword: (String) -> Unit,
    navigateSignup: () -> Unit,
    navigateGuestCheckout: (() -> Unit)? = null
) {
    var loginContactError by remember {
        mutableStateOf<String?>(null)
    }

    var isSubmitting by remember {
        mutableStateOf(false)
    }

    if (isSubmitting) {
        ScreenLoading(message = "Checking account...")
        return
    }

    LoginContactScreen(
        onLogoClick = navigateHome,
        error = loginContactError,

        onContinue = { contact ->
            scope.launch {
                try {
                    isSubmitting = true
                    loginContactError = null

                    val csrfToken = RetrofitClient.getCsrfToken()
                    if (csrfToken == null) {
                        loginContactError = "Session not ready. Please try again."
                        return@launch
                    }

                    val response = RetrofitClient.apiService.loginContact(
                        csrfToken = csrfToken,
                        contact = contact
                    )

                    if (response.success) {
                        when (response.nextStep) {
                            "login_password" -> {
                                navigateLoginPassword(response.contact ?: contact)
                            }

                            "product_list",
                            "products:product_list",
                            "home" -> {
                                navigateHome()
                            }

                            else -> {
                                loginContactError =
                                    "Unexpected next step: ${response.nextStep}"
                            }
                        }
                    } else {
                        loginContactError = response.errorMsg ?: "Login failed"
                    }

                } catch (e: Exception) {
                    loginContactError = e.message ?: "Login failed"
                    Log.e("LOGIN_CONTACT", "failed: ${e.message}", e)
                } finally {
                    isSubmitting = false
                }
            }
        },

        onSignupClick = navigateSignup,
        onGuestCheckoutClick = navigateGuestCheckout
    )
}
