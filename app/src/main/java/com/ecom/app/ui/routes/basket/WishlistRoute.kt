package com.ecom.app.ui.routes.basket

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.model.basket.WishlistDetailResponse
import com.ecom.app.model.product.ProductDetailResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.components.ScreenLoading
import com.ecom.app.ui.screens.basket.WishlistScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun WishlistRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    navigateProductDetail: (ProductDetailResponse) -> Unit,
    setCartCount: (Int) -> Unit
) {
    var wishlistDetailResponse by remember {
        mutableStateOf<WishlistDetailResponse?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    fun refreshWishlist() {
        scope.launch {
            try {
                isLoading = true

                val response = RetrofitClient.apiService.getWishlistDetail()
                wishlistDetailResponse = response

            } catch (e: Exception) {
                Log.e("WISHLIST_GET", "failed: ${e.message}", e)
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshWishlist()
    }

    if (isLoading || wishlistDetailResponse == null) {
        ScreenLoading(message = "Loading wishlist...")
        return
    }

    WishlistScreen(
        modifier = Modifier.padding(innerPadding),
        items = wishlistDetailResponse?.wishlistItems.orEmpty(),

        onProductClick = { item ->
            scope.launch {
                try {
                    val variantId = item.variantId ?: return@launch

                    val detail = RetrofitClient.apiService.getProductDetail(
                        variantId = variantId,
                        slug = item.slug
                    )

                    navigateProductDetail(detail)

                } catch (e: Exception) {
                    Log.e("WISHLIST_PRODUCT", "failed: ${e.message}", e)
                }
            }
        },

        onRemoveWishlist = { item ->
            scope.launch {
                try {
                    val csrfToken = RetrofitClient.getCsrfToken() ?: return@launch

                    val response = RetrofitClient.apiService.removeFromWishlist(
                        csrfToken = csrfToken,
                        productId = item.productId,
                        minimal = "1"
                    )

                    if (response.success) {
                        refreshWishlist()
                    }

                } catch (e: Exception) {
                    Log.e("WISHLIST_REMOVE", "failed: ${e.message}", e)
                }
            }
        },

        onAddToCart = { item ->
            scope.launch {
                try {
                    val csrfToken = RetrofitClient.getCsrfToken() ?: return@launch
                    val variantId = item.variantId ?: return@launch

                    val response = RetrofitClient.apiService.updateToCart(
                        csrfToken = csrfToken,
                        productId = item.productId,
                        variantId = variantId,
                        quantity = 1,
                        minimal = "1"
                    )

                    if (response.success) {
                        setCartCount(response.cartCount)
                        refreshWishlist()
                    }

                } catch (e: Exception) {
                    Log.e("WISHLIST_ADD_CART", "failed: ${e.message}", e)
                }
            }
        }
    )
}