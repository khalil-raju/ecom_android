package com.ecom.app.ui.routes.account

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.model.account.ChangeContactResponse
import com.ecom.app.ui.components.ScreenLoading
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.navigations.OtpPurpose
import com.ecom.app.ui.screens.account.ChangeContactScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class ContactChangeType {
    EMAIL,
    PHONE
}
@Composable
fun ChangeContactRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    type: ContactChangeType,
    navigateOtp: (String, OtpPurpose) -> Unit
) {
    var response by remember(type) {
        mutableStateOf<ChangeContactResponse?>(null)
    }

    var isLoading by remember(type) {
        mutableStateOf(true)
    }

    var error by remember(type) {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(type) {
        try {
            isLoading = true
            error = null

            response = when (type) {
                ContactChangeType.EMAIL -> RetrofitClient.apiService.getChangeEmail()
                ContactChangeType.PHONE -> RetrofitClient.apiService.getChangePhone()
            }

        } catch (e: Exception) {
            error = e.message ?: "Unable to load form."
            Log.e("CHANGE_CONTACT_GET", "failed: ${e.message}", e)
        } finally {
            isLoading = false
        }
    }

    if (isLoading || response == null) {
        ScreenLoading(
            message = when (type) {
                ContactChangeType.EMAIL -> "Loading email form..."
                ContactChangeType.PHONE -> "Loading phone form..."
            }
        )
        return
    }

    ChangeContactScreen(
        modifier = Modifier.padding(innerPadding),

        title = when (type) {
            ContactChangeType.EMAIL -> "Change Email"
            ContactChangeType.PHONE -> "Change Phone"
        },

        label = when (type) {
            ContactChangeType.EMAIL -> "New Email"
            ContactChangeType.PHONE -> "New Phone Number"
        },

        value = response?.contact.orEmpty(),
        error = error,

        onSubmit = { value ->
            scope.launch {
                try {
                    error = null

                    val csrfToken = RetrofitClient.getCsrfToken() ?: return@launch

                    val result = when (type) {
                        ContactChangeType.EMAIL -> RetrofitClient.apiService.submitChangeEmail(
                            csrfToken = csrfToken,
                            newEmail = value
                        )

                        ContactChangeType.PHONE -> RetrofitClient.apiService.submitChangePhone(
                            csrfToken = csrfToken,
                            newPhone = value
                        )
                    }

                    if (
                        result.success &&
                        result.nextStep == "change_contact_otp"
                    ) {
                        navigateOtp(
                            result.contact ?: value,
                            when (type) {
                                ContactChangeType.EMAIL -> OtpPurpose.CHANGE_EMAIL
                                ContactChangeType.PHONE -> OtpPurpose.CHANGE_PHONE
                            }
                        )
                    } else {
                        error = result.errorMsg ?: "Unable to continue."
                    }

                } catch (e: Exception) {
                    error = e.message ?: "Unable to continue."
                    Log.e("CHANGE_CONTACT_SUBMIT", "failed: ${e.message}", e)
                }
            }
        }
    )
}