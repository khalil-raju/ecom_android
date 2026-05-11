package com.ecom.app.ui.routes.basket

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.ecom.app.model.basket.BasketResponse
import com.ecom.app.model.order.CheckoutResponse
import com.ecom.app.model.product.ProductDetailResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.screens.basket.CartScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.ecom.app.ui.components.ScreenLoading
import com.ecom.app.ui.screens.basket.WishlistScreen

@Composable
fun WishlistRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    navigateHome: () -> Unit,
    navigateProductDetail: (ProductDetailResponse) -> Unit,
    setBasketResponse: (BasketResponse) -> Unit,
    setCartCount: (Int) -> Unit
) {
    var basketResponse by remember {
        mutableStateOf<BasketResponse?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            val response = RetrofitClient.apiService.getBasket()
            Log.d("WISHLIST_GET","response: $response")
            basketResponse = response
            setBasketResponse(response)
            setCartCount(response.cartCount)
        } catch (e: Exception) {
            Log.e("WISHLIST_GET", "failed: ${e.message}", e)
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        ScreenLoading(message = "Loading wishlist...")
        return
    }

    WishlistScreen(
        modifier = Modifier.padding(innerPadding),
        items = basketResponse?.wishlistItems.orEmpty(),

        onProductClick = { item ->
            scope.launch {
                try {
                    val variantId = item.variantId ?: return@launch
                    val slug = item.slug

                    val detail = RetrofitClient.apiService.getProductDetail(
                        variantId = variantId,
                        slug = slug
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

                    RetrofitClient.apiService.removeFromWishlist(
                        csrfToken = csrfToken,
                        productId = item.productId,
                        minimal = "0"
                    )

                    val response = RetrofitClient.apiService.getBasket()
                    basketResponse = response
                    setBasketResponse(response)
                    setCartCount(response.cartCount)

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

                    val response = RetrofitClient.apiService.addToCart(
                        csrfToken = csrfToken,
                        productId = item.productId,
                        variantId = variantId
                    )

                    setCartCount(response.cartCount)

                    val basket = RetrofitClient.apiService.getBasket()
                    basketResponse = basket
                    setBasketResponse(basket)

                } catch (e: Exception) {
                    Log.e("WISHLIST_ADD_CART", "failed: ${e.message}", e)
                }
            }
        }
    )
}