package com.ecom.app.ui.routes.account

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.model.account.ChangeContactResponse
import com.ecom.app.model.account.ChangeNameResponse
import com.ecom.app.model.account.ProfileResponse
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
    navigateBack: () -> Unit,
    navigateOtp: (String, OtpPurpose) -> Unit
) {
    var response by remember(type) { mutableStateOf<ChangeContactResponse?>(null) }
    var error by remember(type) { mutableStateOf<String?>(null) }

    LaunchedEffect(type) {
        response = when (type) {
            ContactChangeType.EMAIL -> RetrofitClient.apiService.getChangeEmail()
            ContactChangeType.PHONE -> RetrofitClient.apiService.getChangePhone()
        }
    }

    ChangeContactScreen(
        modifier = Modifier.padding(innerPadding),
        title = response?.heading ?: if (type == ContactChangeType.EMAIL) "Change Email" else "Change Phone",
        label = if (type == ContactChangeType.EMAIL) "New Email" else "New Phone Number",
        value = response?.value.orEmpty(),
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

                    if (result.success && result.nextStep == "change_contact_otp") {
                        navigateOtp(
                            result.contact ?: value,
                            if (type == ContactChangeType.EMAIL) OtpPurpose.CHANGE_EMAIL else OtpPurpose.CHANGE_PHONE
                        )
                    } else {
                        error = result.errorMsg ?: "Unable to continue."
                    }
                } catch (e: Exception) {
                    error = e.message ?: "Unable to continue."
                }
            }
        }
    )
}