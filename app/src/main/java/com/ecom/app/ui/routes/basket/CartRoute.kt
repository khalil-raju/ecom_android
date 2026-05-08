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

@Composable
fun CartRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    basketResponse: BasketResponse?,
    setBasketResponse: (BasketResponse) -> Unit,
    setCheckoutResponse: (CheckoutResponse) -> Unit,
    setCartCount: (Int) -> Unit,
    navigateHome: () -> Unit,
    navigateProductDetail: (ProductDetailResponse) -> Unit,
    navigateCheckout: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            isLoading = true

            val response = RetrofitClient.apiService.getBasket()
            setBasketResponse(response)
            setCartCount(response.cartCount)

        } catch (e: Exception) {
            Log.e("CART_REFRESH", "failed: ${e.message}", e)
        } finally {
            isLoading = false
        }
    }

    CartScreen(
        modifier = Modifier.padding(innerPadding),
        basket = basketResponse,
        isLoading = isLoading,
        onBack = navigateHome,
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
                    val csrfToken = RetrofitClient.getCsrfToken() ?: return@launch
                    val variantId = basketItem.variantId ?: return@launch

                    val response = RetrofitClient.apiService.updateCartQuantity(
                        csrfToken = csrfToken,
                        productId = basketItem.productId,
                        variantId = variantId,
                        quantity = quantity
                    )

                    setBasketResponse(response)
                    setCartCount(response.cartCount)
                } catch (e: Exception) {
                    Log.e("CART_QTY", "failed: ${e.message}", e)
                }
            }
        },
        onCheckoutClick = {
            scope.launch {
                try {
                    val response = RetrofitClient.apiService.getCheckout()
                    setCheckoutResponse(response)
                    navigateCheckout()
                } catch (e: Exception) {
                    Log.e("CHECKOUT", "failed: ${e.message}", e)
                }
            }
        }
    )
}