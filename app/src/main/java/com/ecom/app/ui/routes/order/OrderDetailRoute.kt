package com.ecom.app.ui.routes.order

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.BuildConfig
import com.ecom.app.model.order.OrderDetailResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.components.ScreenLoading
import com.ecom.app.ui.screens.order.OrderDetailScreen
import com.ecom.app.util.downloadFileAndOpen
import kotlinx.coroutines.CoroutineScope

private fun fullUrl(path: String?): String? {
    return path?.let {
        if (it.startsWith("http")) it
        else BuildConfig.BASE_URL.trimEnd('/') + it
    }
}

@Composable
fun OrderDetailRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    context: Context,
    orderToken: String,
    navigateBack: () -> Unit,
    navigateCancelOrder: (String) -> Unit
) {
    var orderDetailResponse by remember(orderToken) {
        mutableStateOf<OrderDetailResponse?>(null)
    }

    var isLoading by remember(orderToken) {
        mutableStateOf(true)
    }

    LaunchedEffect(orderToken) {
        try {
            isLoading = true

            val response = RetrofitClient.apiService.getOrderDetail(orderToken)
            orderDetailResponse = response

        } catch (e: Exception) {
            Log.e("ORDER_DETAIL", "failed: ${e.message}", e)
        } finally {
            isLoading = false
        }
    }

    if (isLoading || orderDetailResponse == null) {
        ScreenLoading(message = "Loading order...")
        return
    }

    OrderDetailScreen(
        modifier = Modifier.padding(innerPadding),
        response = orderDetailResponse,
        onBack = navigateBack,
        onCancelOrderClick = {
            navigateCancelOrder(orderToken)
        },
        onInvoiceClick = {
            val invoiceUrl = orderDetailResponse?.invoice?.pdfUrl ?: return@OrderDetailScreen
            val url = fullUrl(invoiceUrl) ?: return@OrderDetailScreen

            downloadFileAndOpen(
                context = context,
                url = url,
                title = "Invoice",
                mimeType = "application/pdf"
            )
        }
    )
}