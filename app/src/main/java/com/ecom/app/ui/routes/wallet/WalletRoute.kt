package com.ecom.app.ui.routes.wallet

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.model.wallet.WalletResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.screens.wallet.WalletScreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun WalletRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    navigateBack: () -> Unit
) {
    var response by remember {
        mutableStateOf<WalletResponse?>(null)
    }

    LaunchedEffect(Unit) {
        try {
            response = RetrofitClient.apiService.getWalletDetail()
        } catch (e: Exception) {
            Log.e("WALLET_GET", "failed: ${e.message}", e)
        }
    }

    WalletScreen(
        modifier = Modifier.padding(innerPadding),
        response = response,
        onBack = navigateBack
    )
}