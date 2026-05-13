package com.ecom.app.ui.routes.account

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.model.account.ChangeNameResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.components.ScreenLoading
import com.ecom.app.ui.screens.account.ChangeNameScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ChangeNameRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    navigateBack: () -> Unit,
    navigateProfile: () -> Unit
) {
    var response by remember {
        mutableStateOf<ChangeNameResponse?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            error = null

            response = RetrofitClient.apiService.getChangeName()

        } catch (e: Exception) {
            error = e.message ?: "Unable to load name form."
            Log.e("CHANGE_NAME_GET", "failed: ${e.message}", e)
        } finally {
            isLoading = false
        }
    }

    if (isLoading || response == null) {
        ScreenLoading(message = "Loading name form...")
        return
    }

    ChangeNameScreen(
        modifier = Modifier.padding(innerPadding),
        response = response,
        error = error,

        onSubmit = { firstName, lastName ->
            scope.launch {
                try {
                    error = null

                    val csrfToken = RetrofitClient.getCsrfToken() ?: return@launch

                    val result = RetrofitClient.apiService.submitChangeName(
                        csrfToken = csrfToken,
                        newFirstname = firstName,
                        newLastname = lastName
                    )

                    response = result

                    if (result.success && result.nextStep == "profile") {
                        navigateProfile()
                    } else {
                        error = result.errorMsg ?: "Unable to update name."
                    }

                } catch (e: Exception) {
                    error = e.message ?: "Unable to update name."
                    Log.e("CHANGE_NAME_SUBMIT", "failed: ${e.message}", e)
                }
            }
        }
    )
}