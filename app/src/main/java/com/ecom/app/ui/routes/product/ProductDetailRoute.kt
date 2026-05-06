// ui/routes/ProductDetailRoute.kt
package com.ecom.app.ui.routes.product

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ecom.app.model.product.ProductDetailResponse
import com.ecom.app.ui.screens.product.ProductDetailScreen

@Composable
fun ProductDetailRoute(
    innerPadding: PaddingValues,
    detail: ProductDetailResponse,
    navigateBack: () -> Unit,
    onCartCountChange: (Int) -> Unit
) {
    ProductDetailScreen(
        detail = detail,
        modifier = Modifier.padding(innerPadding),
        onBack = navigateBack,
        onCartCountChange = onCartCountChange
    )
}