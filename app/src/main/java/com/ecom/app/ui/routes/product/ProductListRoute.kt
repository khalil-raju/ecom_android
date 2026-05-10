package com.ecom.app.ui.routes.product

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
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
    navigateProductDetail: (ProductDetailResponse) -> Unit
) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var nextOffset by remember { mutableIntStateOf(0) }
    var hasMore by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingMore by remember { mutableStateOf(false) }

    fun loadProducts(reset: Boolean = false) {
        scope.launch {
            if (isLoading || isLoadingMore) return@launch
            if (!reset && !hasMore) return@launch

            try {
                if (reset) {
                    isLoading = true
                    products = emptyList()
                    nextOffset = 0
                    hasMore = true
                } else {
                    isLoadingMore = true
                }

                val offsetToLoad = if (reset) 0 else nextOffset
                val limitToLoad = 20

                val response = RetrofitClient.apiService.getProducts(
                    limit = limitToLoad,
                    offset = offsetToLoad
                )

                products = if (reset) {
                    response.products
                } else {
                    products + response.products
                }

                nextOffset = offsetToLoad + response.products.size
                hasMore = response.hasMore

            } catch (e: Exception) {
                Log.e("PRODUCT_LIST", "failed: ${e.message}", e)
            } finally {
                isLoading = false
                isLoadingMore = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadProducts(reset = true)
    }

    ProductListScreen(
        products = products,
        isLoading = isLoading,
        isLoadingMore = isLoadingMore,
        hasMore = hasMore,
        modifier = Modifier.padding(innerPadding),
        onLoadMore = {
            loadProducts(reset = false)
        },
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