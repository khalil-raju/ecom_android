package com.ecom.app.ui.routes.wallet

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.model.wallet.WalletResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.components.ScreenLoading
import com.ecom.app.ui.screens.wallet.WalletScreen

@Composable
fun WalletRoute(
    innerPadding: PaddingValues
) {
    var walletResponse by remember {
        mutableStateOf<WalletResponse?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        try {
            isLoading = true

            val response = RetrofitClient.apiService.getWalletDetail()
            walletResponse = response

        } catch (e: Exception) {
            Log.e("WALLET_GET", "failed: ${e.message}", e)
        } finally {
            isLoading = false
        }
    }

    if (isLoading || walletResponse == null) {
        ScreenLoading(message = "Loading wallet...")
        return
    }

    WalletScreen(
        modifier = Modifier.padding(innerPadding),
        response = walletResponse
    )
}