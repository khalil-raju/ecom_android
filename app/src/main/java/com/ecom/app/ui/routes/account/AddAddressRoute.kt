package com.ecom.app.ui.routes.account

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.model.account.AddAddressResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.components.ScreenLoading
import com.ecom.app.ui.screens.account.AddAddressScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AddAddressRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    addressId: Int? = null,
    navigateSavedAddresses: () -> Unit,
    navigateCheckout: () -> Unit,
) {
    var response by remember(addressId) {
        mutableStateOf<AddAddressResponse?>(null)
    }

    var isLoading by remember(addressId) {
        mutableStateOf(true)
    }

    LaunchedEffect(addressId) {
        try {
            isLoading = true

            response = if (addressId != null) {
                RetrofitClient.apiService.getEditAddress(addressId)
            } else {
                RetrofitClient.apiService.getAddAddress()
            }

        } catch (e: Exception) {
            Log.e("ADDRESS_FORM_GET", "failed: ${e.message}", e)
        } finally {
            isLoading = false
        }
    }

    if (isLoading || response == null) {
        ScreenLoading(message = "Loading address...")
        return
    }

    AddAddressScreen(
        modifier = Modifier.padding(innerPadding),
        response = response,
        errorMsg = response?.errorMsg.orEmpty(),

        onFetchPincode = { pincode ->
            try {
                RetrofitClient.apiService.getPinCodeDetails(pincode)
            } catch (e: Exception) {
                Log.e("PINCODE_FETCH", "failed: ${e.message}", e)
                null
            }
        },

        onSubmit = { fullName, phone, line1, line2, postalCode, city, state, country, label, isDefault, consentPpTc ->
            scope.launch {
                try {
                    val csrfToken = RetrofitClient.getCsrfToken() ?: return@launch

                    val result = when {
                        addressId != null -> {
                            RetrofitClient.apiService.submitEditAddress(
                                addressId = addressId,
                                csrfToken = csrfToken,
                                fullName = fullName,
                                phone = phone,
                                line1 = line1,
                                line2 = line2,
                                postalCode = postalCode,
                                city = city,
                                state = state,
                                country = country,
                                label = label,
                                isDefault = if (isDefault) "on" else null
                            )
                        }

                        response?.isGuest == true -> {
                            RetrofitClient.apiService.submitAddAddress(
                                csrfToken = csrfToken,
                                fullName = fullName,
                                phone = phone,
                                line1 = line1,
                                line2 = line2,
                                postalCode = postalCode,
                                city = city,
                                state = state,
                                country = country,
                                label = label,
                                isDefault = if (isDefault) "on" else null,
                                consentPpTc = if (consentPpTc) "on" else null,
                                from = "checkout"
                            )
                        }

                        else -> {
                            RetrofitClient.apiService.submitAddAddress(
                                csrfToken = csrfToken,
                                fullName = fullName,
                                phone = phone,
                                line1 = line1,
                                line2 = line2,
                                postalCode = postalCode,
                                city = city,
                                state = state,
                                country = country,
                                label = label,
                                isDefault = if (isDefault) "on" else null,
                                consentPpTc = if (consentPpTc) "on" else null
                            )
                        }
                    }

                    response = result

                    if (result.success) {
                        when (result.nextStep) {
                            "checkout" -> {
                                navigateCheckout()
                            }

                            else -> {
                                navigateSavedAddresses()
                            }
                        }
                    }

                } catch (e: Exception) {
                    Log.e("ADDRESS_FORM_SUBMIT", "failed: ${e.message}", e)
                }
            }
        }
    )
}