// ui/routes/ProductListRoute.kt
package com.ecom.app.ui.routes.product

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ecom.app.model.product.Product
import com.ecom.app.model.product.ProductDetailResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.screens.product.ProductListScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ProductListRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    products: List<Product>,
    navigateProductDetail: (ProductDetailResponse) -> Unit
) {
    ProductListScreen(
        products = products,
        modifier = Modifier.padding(innerPadding),
        onProductClick = { product ->
            scope.launch {
                try {
                    val detail = RetrofitClient.apiService.getProductDetail(
                        variantId = product.variantId,
                        slug = product.slug
                    )

                    navigateProductDetail(detail)

                } catch (e: Exception) {
                    Log.e("PRODUCT_DETAIL", "failed: ${e.message}", e)
                }
            }
        }
    )
}