package com.ecom.app.ui.routes.basket

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.ecom.app.model.basket.CartDetailResponse
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

@Composable
fun CartRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    setCartCount: (Int) -> Unit,
    navigateProductDetail: (ProductDetailResponse) -> Unit,
    navigateCheckout: () -> Unit,
) {
    var isInitialLoading by remember { mutableStateOf(true) }
    var isUpdatingCart by remember { mutableStateOf(false) }
    var cartDetailResponse by remember { mutableStateOf<CartDetailResponse?>(null) }

    fun refreshCart(showFullLoading: Boolean = false) {
        scope.launch {
            try {
                if (showFullLoading) {
                    isInitialLoading = true
                }

                val response = RetrofitClient.apiService.getCartDetail()
                cartDetailResponse = response
                setCartCount(response.cartCount)

            } catch (e: Exception) {
                Log.e("CART_REFRESH", "failed: ${e.message}", e)
            } finally {
                if (showFullLoading) {
                    isInitialLoading = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshCart(showFullLoading = true)
    }

    if (isInitialLoading || cartDetailResponse == null) {
        ScreenLoading(message = "Loading cart...")
        return
    }

    CartScreen(
        modifier = Modifier.padding(innerPadding),
        cartDetailResponse = cartDetailResponse,
        isLoading = isUpdatingCart,

        onNavigateToProduct = { variantId, slug ->
            scope.launch {
                try {
                    val detail = RetrofitClient.apiService.getProductDetail(
                        variantId = variantId,
                        slug = slug
                    )

                    navigateProductDetail(detail)

                } catch (e: Exception) {
                    Log.e("CART_PRODUCT", "failed: ${e.message}", e)
                }
            }
        },

        onQuantityChange = { basketItem, quantity ->
            scope.launch {
                try {
                    isUpdatingCart = true

                    val csrfToken = RetrofitClient.getCsrfToken() ?: return@launch
                    val variantId = basketItem.variantId ?: return@launch

                    val updateResponse = RetrofitClient.apiService.updateToCart(
                        csrfToken = csrfToken,
                        productId = basketItem.productId,
                        variantId = variantId,
                        quantity = quantity
                    )

                    setCartCount(updateResponse.cartCount)

                    val refreshedCart = RetrofitClient.apiService.getCartDetail()
                    cartDetailResponse = refreshedCart
                    setCartCount(refreshedCart.cartCount)

                } catch (e: Exception) {
                    Log.e("CART_QTY", "failed: ${e.message}", e)
                } finally {
                    isUpdatingCart = false
                }
            }
        },

        onCheckoutClick = {
            navigateCheckout()
        }
    )
}