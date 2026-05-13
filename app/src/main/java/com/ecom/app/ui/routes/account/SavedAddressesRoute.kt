package com.ecom.app.ui.routes.account

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.model.account.Address
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.screens.account.SavedAddressesScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SavedAddressesRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    navigateAddAddress: () -> Unit,
    navigateEditAddress: (Int) -> Unit = {}
) {
    var addresses by remember {
        mutableStateOf<List<Address>>(emptyList())
    }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.apiService.getSavedAddresses()
            addresses = response.addresses
        } catch (e: Exception) {
            Log.e("SAVED_ADDRESSES_GET", "failed: ${e.message}", e)
        }
    }

    SavedAddressesScreen(
        modifier = Modifier.padding(innerPadding),
        addresses = addresses,
        onAddAddress = navigateAddAddress,
        onEditAddress = navigateEditAddress,
        onDeleteAddress = { addressId ->
            scope.launch {
                try {
                    val csrfToken = RetrofitClient.getCsrfToken() ?: return@launch

                    val response = RetrofitClient.apiService.deleteAddress(
                        addressId = addressId,
                        csrfToken = csrfToken
                    )

                    if (response.success) {
                        addresses = addresses.filterNot { it.id == addressId }
                    }

                } catch (e: Exception) {
                    Log.e("ADDRESS_DELETE", "failed: ${e.message}", e)
                }
            }
        }
    )
}
