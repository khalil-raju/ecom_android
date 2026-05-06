package com.ecom.app.ui.routes.account

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.model.account.ChangeNameResponse
import com.ecom.app.model.account.ProfileResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.screens.account.ChangeNameScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ChangeNameRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    navigateBack: () -> Unit,
    navigateProfile: () -> Unit,
    onProfileUpdated: (ProfileResponse) -> Unit
) {

    var response by remember {
        mutableStateOf<ChangeNameResponse?>(null)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        try {
            response = RetrofitClient.apiService.getChangeName()
        } catch (e: Exception) {
            Log.e("CHANGE_NAME_GET", "failed: ${e.message}", e)
        }
    }

    ChangeNameScreen(
        modifier = Modifier.padding(innerPadding),
        response = response,
        error = error,
        onBack = navigateBack,

        onSubmit = { firstName, lastName ->
            scope.launch {
                try {
                    val csrfToken =
                        RetrofitClient.getCsrfToken() ?: return@launch

                    val result =
                        RetrofitClient.apiService.submitChangeName(
                            csrfToken = csrfToken,
                            newFirstname = firstName,
                            newLastname = lastName
                        )

                    if (result.success) {
                        val profile = RetrofitClient.apiService.getProfile()
                        onProfileUpdated(profile)
                        navigateProfile()
                    } else {
                        error = result.errorMsg
                    }

                } catch (e: Exception) {
                    error = e.message
                    Log.e("CHANGE_NAME_SUBMIT", "failed: ${e.message}", e)
                }
            }
        }
    )
}