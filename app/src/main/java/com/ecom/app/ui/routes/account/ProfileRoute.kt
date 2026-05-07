// ui/routes/ProfileRoute.kt
package com.ecom.app.ui.routes.account

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ecom.app.model.account.ProfileResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.screens.account.ProfileScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ProfileRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    profileResponse: ProfileResponse?,
    profileError: String?,
    navigateBack: () -> Unit,
    navigateSavedAddresses: () -> Unit,
    navigateAddAddress: () -> Unit,
    navigateChangeName: () -> Unit,
    navigateChangeEmail: () -> Unit,
    navigateChangePhone: () -> Unit,
    navigateChangePassword: () -> Unit,
    navigateOrderItemHistory: () -> Unit,
    navigateWallet: () -> Unit,
    navigateCart: () -> Unit,
    onLoggedOut: () -> Unit
) {
    ProfileScreen(
        modifier = Modifier.padding(innerPadding),
        profile = profileResponse,
        error = profileError,
        onBack = navigateBack,
        onSavedAddressesClick = navigateSavedAddresses,
        onAddAddressClick = navigateAddAddress,
        onChangeNameClick = navigateChangeName,
        onChangeEmailClick = navigateChangeEmail,
        onChangePhoneClick = navigateChangePhone,
        onChangePasswordClick = navigateChangePassword,
        onCartClick = navigateCart,
        onViewOrderClick = navigateOrderItemHistory,
        onViewWalletClick = navigateWallet,
        onLogout = {
            scope.launch {
                try {
                    RetrofitClient.apiService.logout()
                    onLoggedOut()
                } catch (e: Exception) {
                    Log.e("PROFILE_LOGOUT", "failed: ${e.message}", e)
                }
            }
        }
    )
}