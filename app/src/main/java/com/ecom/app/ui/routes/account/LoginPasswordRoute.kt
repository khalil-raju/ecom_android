// ui/routes/LoginPasswordRoute.kt
package com.ecom.app.ui.routes.account

import android.util.Log
import androidx.compose.runtime.*
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.screens.account.LoginPasswordScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginPasswordRoute(
    scope: CoroutineScope,
    contact: String,
    navigateHome: () -> Unit,
    onAuthenticated: () -> Unit,
    onUnauthenticated: () -> Unit,
    navigateOtpLogin: () -> Unit
) {
    var loginPasswordError by remember(contact) {
        mutableStateOf<String?>(null)
    }

    var loginAttemptsLeft by remember(contact) {
        mutableStateOf<Int?>(null)
    }

    LoginPasswordScreen(
        onLogoClick = navigateHome,
        contact = contact,
        error = loginPasswordError,
        attemptsLeft = loginAttemptsLeft,
        onLogin = { password ->
            scope.launch {
                try {
                    loginPasswordError = null

                    val csrfToken = RetrofitClient.getCsrfToken()
                    if (csrfToken == null) {
                        loginPasswordError = "Session not ready"
                        return@launch
                    }

                    val response = RetrofitClient.apiService.loginPassword(
                        csrfToken = csrfToken,
                        password = password
                    )

                    if (response.success && response.authenticated == true) {
                        loginPasswordError = null
                        loginAttemptsLeft = null
                        onAuthenticated()
                    } else {
                        loginPasswordError =
                            response.error ?: response.errorMsg ?: "Login failed"

                        loginAttemptsLeft = response.loginAttemptsLeft
                        onUnauthenticated()
                    }

                } catch (e: Exception) {
                    loginPasswordError = e.message ?: "Login failed"
                    onUnauthenticated()
                    Log.e("LOGIN_PASSWORD", "failed: ${e.message}", e)
                }
            }
        },
        onOtpLogin = navigateOtpLogin
    )
}