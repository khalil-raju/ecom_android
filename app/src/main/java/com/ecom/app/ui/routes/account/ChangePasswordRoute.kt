package com.ecom.app.ui.routes.account

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.model.account.ChangePasswordResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.screens.account.ChangePasswordScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    navigateBack: () -> Unit,
    navigateProfile: () -> Unit
) {
    var response by remember {
        mutableStateOf<ChangePasswordResponse?>(null)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        try {
            response = RetrofitClient.apiService.getChangePassword()
        } catch (e: Exception) {
            Log.e("CHANGE_PASSWORD_GET", "failed: ${e.message}", e)
        }
    }

    ChangePasswordScreen(
        modifier = Modifier.padding(innerPadding),
        response = response,
        error = error,
        onBack = navigateBack,
        onSubmit = { password, confirm ->
            scope.launch {
                try {
                    error = null

                    val csrfToken = RetrofitClient.getCsrfToken() ?: return@launch

                    val result = RetrofitClient.apiService.submitChangePassword(
                        csrfToken = csrfToken,
                        newPassword = password,
                        confirmPassword = confirm
                    )

                    if (result.success) {
                        navigateProfile()
                    } else {
                        error = result.errorMsg ?: "Unable to change password."
                    }
                } catch (e: Exception) {
                    error = e.message ?: "Unable to change password."
                    Log.e("CHANGE_PASSWORD_POST", "failed: ${e.message}", e)
                }
            }
        }
    )
}