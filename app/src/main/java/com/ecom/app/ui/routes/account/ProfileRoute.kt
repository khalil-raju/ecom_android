package com.ecom.app.ui.routes.account

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.model.account.UserProfileResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.components.ScreenLoading
import com.ecom.app.ui.screens.account.ProfileScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ProfileRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    navigateLogin: () -> Unit,
    navigateSavedAddresses: () -> Unit,
    navigateAddAddress: () -> Unit,
    navigateChangeName: () -> Unit,
    navigateChangeEmail: () -> Unit,
    navigateChangePhone: () -> Unit,
    navigateChangePassword: () -> Unit,
    navigateOrderItemHistory: () -> Unit,
    navigateWallet: () -> Unit,
    navigateCart: () -> Unit,
    navigateWishlist: () -> Unit,
    onLoggedOut: () -> Unit
) {
    var profileResponse by remember {
        mutableStateOf<UserProfileResponse?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var profileError by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            profileError = null

            val response = RetrofitClient.apiService.getProfile()

            if (!response.authenticated) {
                navigateLogin()
                return@LaunchedEffect
            }

            profileResponse = response

        } catch (e: Exception) {
            profileError = e.message ?: "Unable to load profile."
            Log.e("PROFILE_GET", "failed: ${e.message}", e)
        } finally {
            isLoading = false
        }
    }

    if (isLoading || profileResponse == null) {
        ScreenLoading(message = "Loading profile...")
        return
    }

    ProfileScreen(
        modifier = Modifier.padding(innerPadding),
        profile = profileResponse,
        error = profileError,
        onSavedAddressesClick = navigateSavedAddresses,
        onAddAddressClick = navigateAddAddress,
        onChangeNameClick = navigateChangeName,
        onChangeEmailClick = navigateChangeEmail,
        onChangePhoneClick = navigateChangePhone,
        onChangePasswordClick = navigateChangePassword,
        onCartClick = navigateCart,
        onWishlistClick = navigateWishlist,
        onViewOrderClick = navigateOrderItemHistory,
        onViewWalletClick = navigateWallet,
        onLogout = {
            scope.launch {
                try {
                    val csrfToken = RetrofitClient.getCsrfToken() ?: return@launch

                    RetrofitClient.apiService.logout(
                        csrfToken = csrfToken
                    )

                    onLoggedOut()

                } catch (e: Exception) {
                    Log.e("PROFILE_LOGOUT", "failed: ${e.message}", e)
                }
            }
        }
    )
}