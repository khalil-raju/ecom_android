package com.ecom.app.ui.routes.account

import android.util.Log
import androidx.compose.runtime.*
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.screens.account.SignupContactScreen
import com.ecom.app.ui.components.ScreenLoading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SignupContactRoute(
    scope: CoroutineScope,
    navigateHome: () -> Unit,
    navigateLoginPassword: (String) -> Unit,
    navigateSignupPassword: (String) -> Unit,
    navigateLogin: () -> Unit
) {
    var error by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    if (isSubmitting) {
        ScreenLoading(message = "Checking account...")
        return
    }

    SignupContactScreen(
        error = error,
        onLogoClick = navigateHome,
        onLoginClick = navigateLogin,
        onContinue = { contact, consent ->
            scope.launch {
                try {
                    isSubmitting = true
                    error = null

                    val csrfToken = RetrofitClient.getCsrfToken()
                    if (csrfToken == null) {
                        error = "Session not ready. Please try again."
                        return@launch
                    }

                    val response = RetrofitClient.apiService.signupContact(
                        csrfToken = csrfToken,
                        contact = contact,
                        consentPpTc = if (consent) "on" else null
                    )

                    if (response.success) {
                        when (response.nextStep) {
                            "signup_password" -> {
                                navigateSignupPassword(response.contact ?: contact)
                            }

                            "login_password" -> {
                                navigateLoginPassword(response.contact ?: contact)
                            }

                            "product_list",
                            "products:product_list",
                            "home" -> {
                                navigateHome()
                            }

                            else -> {
                                error = "Unexpected next step: ${response.nextStep}"
                            }
                        }
                    } else {
                        error = response.errorMsg ?: "Signup failed"
                    }

                } catch (e: Exception) {
                    error = e.message ?: "Signup failed"
                    Log.e("SIGNUP_CONTACT", "failed: ${e.message}", e)
                } finally {
                    isSubmitting = false
                }
            }
        }
    )
}
