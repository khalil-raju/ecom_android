package com.ecom.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import android.app.Activity
import androidx.compose.ui.platform.LocalContext

/*
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject
*/

import com.ecom.app.model.Product
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.components.SearchMenu
import com.ecom.app.ui.components.SideMenu
import com.ecom.app.ui.components.TopMenu
import com.ecom.app.ui.screens.ProductListScreen
import com.ecom.app.model.ProductDetailResponse
import com.ecom.app.ui.screens.ProductDetailScreen
import com.ecom.app.ui.screens.auth.LoginContactScreen
import com.ecom.app.ui.screens.auth.LoginPasswordScreen
import com.ecom.app.model.ProfileResponse
import com.ecom.app.ui.screens.CartScreen
import com.ecom.app.ui.screens.ProfileScreen
import com.ecom.app.model.BasketResponse
import com.ecom.app.model.CheckoutResponse
import com.ecom.app.model.OrderDetailResponse
import com.ecom.app.model.OrderItemDetailResponse
import com.ecom.app.model.OrderItemHistoryResponse
import com.ecom.app.ui.screens.CheckoutScreen
import com.ecom.app.ui.screens.OrderDetailScreen
import com.ecom.app.ui.screens.OrderItemDetailScreen
import com.ecom.app.ui.screens.OrderItemHistoryScreen
import com.ecom.app.ui.screens.PaymentWebViewScreen
import com.ecom.app.model.CancelOrderResponse
import com.ecom.app.model.ReturnOrderItemResponse
import com.ecom.app.model.ReviewOrderItemResponse
import com.ecom.app.ui.screens.CancelOrderScreen
import com.ecom.app.ui.screens.ReturnOrderItemScreen
import com.ecom.app.ui.screens.ReviewOrderItemScreen
import com.ecom.app.util.downloadFileAndOpen

import kotlinx.coroutines.launch


enum class DrawerContentType {
    SIDE_MENU,
    SEARCH_MENU
}

sealed class AppScreen {
    fun isFullScreen(): Boolean {
        return this is AppScreen.PaymentWeb ||
                this is AppScreen.LoginContact ||
                this is AppScreen.LoginPassword
    }

    data object Home : AppScreen()
    data class ProductDetail(val detail: ProductDetailResponse) : AppScreen()
    data object LoginContact : AppScreen()
    data class LoginPassword(val contact: String) : AppScreen()
    data object Profile : AppScreen()
    data object Cart : AppScreen()
    data object Checkout : AppScreen()
    data class PaymentWeb(val url: String) : AppScreen()
    data object OrderItemHistory : AppScreen()
    data object OrderItemDetail : AppScreen()
    data object OrderDetail : AppScreen()
    data object CancelOrder : AppScreen()
    data object ReturnOrderItem : AppScreen()
    data object ReviewOrderItem : AppScreen()
}

private fun fullUrl(path: String?): String? {
    return path?.let {
        if (it.startsWith("http")) it
        else BuildConfig.BASE_URL.trimEnd('/') + it
    }
}

class MainActivity : ComponentActivity() /* , PaymentResultWithDataListener */ {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Checkout.preload(applicationContext) */

        RetrofitClient.init(applicationContext)

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        setContent {
            var drawerContentType by remember { mutableStateOf(DrawerContentType.SIDE_MENU) }
            var products by remember { mutableStateOf<List<Product>>(emptyList()) }
            var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }
            var cartCount by remember { mutableIntStateOf(0) }
            var loginContactError by remember { mutableStateOf<String?>(null) }
            var loginPasswordError by remember { mutableStateOf<String?>(null) }
            var loginAttemptsLeft by remember { mutableStateOf<Int?>(null) }
            var isAuthenticated by remember { mutableStateOf(false) }
            var profileResponse by remember { mutableStateOf<ProfileResponse?>(null) }
            var profileError by remember { mutableStateOf<String?>(null) }
            var basketResponse by remember { mutableStateOf<BasketResponse?>(null) }
            var checkoutResponse by remember { mutableStateOf<CheckoutResponse?>(null) }
            var orderItemHistoryResponse by remember { mutableStateOf<OrderItemHistoryResponse?>(null) }
            var orderItemDetailResponse by remember { mutableStateOf<OrderItemDetailResponse?>(null) }
            var orderDetailResponse by remember { mutableStateOf<OrderDetailResponse?>(null) }
            var cancelOrderResponse by remember { mutableStateOf<CancelOrderResponse?>(null) }
            var cancelOrderError by remember { mutableStateOf<String?>(null) }
            var returnOrderItemResponse by remember { mutableStateOf<ReturnOrderItemResponse?>(null) }
            var returnOrderItemError by remember { mutableStateOf<String?>(null) }
            var reviewOrderItemResponse by remember { mutableStateOf<ReviewOrderItemResponse?>(null) }
            var reviewOrderItemError by remember { mutableStateOf<String?>(null) }

            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            val context = LocalContext.current

            LaunchedEffect(Unit) {

                currentScreen = AppScreen.OrderItemDetail
                orderItemDetailResponse = RetrofitClient.apiService.getOrderItemDetail("c62977e92dab46fc9287a4b7b90a0d76")

                try {
                    val productResponse = RetrofitClient.apiService.getProducts()
                    products = productResponse.products
                } catch (e: Exception) {
                    Log.e("INIT_PRODUCTS", "failed: ${e.message}", e)
                }

                try {
                    val basketResponseFromApi = RetrofitClient.apiService.getBasket()
                    basketResponse = basketResponseFromApi
                    cartCount = basketResponseFromApi.cartCount
                } catch (e: Exception) {
                    Log.e("INIT_BASKET", "failed: ${e.message}", e)
                }

                try {
                    val profile = RetrofitClient.apiService.getProfile()
                    profileResponse = profile
                    isAuthenticated = profile.success && profile.authenticated
                } catch (e: Exception) {
                    isAuthenticated = false
                    profileResponse = null
                    Log.e("INIT_AUTH", "failed: ${e.message}", e)
                }
            }

            Box(
                modifier = Modifier.safeDrawingPadding()
            ) {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            when (drawerContentType) {
                                DrawerContentType.SIDE_MENU -> {
                                    SideMenu(
                                        parentCategories = emptyList(), // replace with backend categories later
                                        isAuthenticated = isAuthenticated,
                                        onClose = {
                                            scope.launch { drawerState.close() }
                                        },
                                        onHomeClick = {
                                            currentScreen = AppScreen.Home
                                        },
                                        onLoginClick = {
                                            currentScreen = AppScreen.LoginContact
                                        },
                                        onSignupClick = {
                                            // later: currentScreen = AppScreen.SignupContact
                                        },
                                        onCategoryClick = { parentSlug, childSlug ->
                                            // later: load category product list
                                        },
                                        onProfileClick = {
                                            currentScreen = AppScreen.Profile
                                        },
                                        onOrdersClick = {
                                            scope.launch {
                                                try {
                                                    val response = RetrofitClient.apiService.getOrderItemHistory()
                                                    orderItemHistoryResponse = response
                                                    currentScreen = AppScreen.OrderItemHistory
                                                } catch (e: Exception) {
                                                    Log.e("ORDER_HISTORY", "failed: ${e.message}", e)
                                                }
                                            }
                                        },
                                        onWalletClick = {
                                            // later: currentScreen = AppScreen.Wallet
                                        },
                                        onWishlistClick = {
                                            currentScreen = AppScreen.Cart
                                        },
                                        onLogoutClick = {
                                            scope.launch {
                                                try {
                                                    RetrofitClient.apiService.logout()
                                                    isAuthenticated = false
                                                    profileResponse = null
                                                    currentScreen = AppScreen.LoginContact
                                                } catch (e: Exception) {
                                                    Log.e("SIDE_MENU_LOGOUT", "failed: ${e.message}", e)
                                                }
                                            }
                                        },
                                        onTermsClick = {
                                            // later: open web/static page
                                        },
                                        onPrivacyClick = {
                                            // later: open web/static page
                                        },
                                        onReturnsClick = {
                                            // later: open web/static page
                                        },
                                        onContactClick = {
                                            // later: open contact page
                                        }
                                    )
                                }

                                DrawerContentType.SEARCH_MENU -> {
                                    SearchMenu(
                                        onClose = {
                                            scope.launch {
                                                drawerState.close()
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            if (!currentScreen.isFullScreen()) {
                                TopMenu(
                                    cartCount = cartCount,
                                    onMenuClick = {
                                        drawerContentType = DrawerContentType.SIDE_MENU
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    },
                                    onSearchClick = {
                                        drawerContentType = DrawerContentType.SEARCH_MENU
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    },
                                    onLogoClick = {
                                        currentScreen = AppScreen.Home
                                    },
                                    onProfileClick = {
                                        scope.launch {
                                            try {
                                                profileError = null

                                                val response = RetrofitClient.apiService.getProfile()
                                                profileResponse = response
                                                isAuthenticated =
                                                    response.success && response.authenticated
                                                currentScreen = if (isAuthenticated) {
                                                    AppScreen.Profile
                                                } else {
                                                    AppScreen.LoginContact
                                                }

                                            } catch (e: Exception) {
                                                isAuthenticated = false
                                                currentScreen = AppScreen.LoginContact
                                            }
                                        }
                                    },
                                    onCartClick = {
                                        currentScreen = AppScreen.Cart
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->

                        when (val screen = currentScreen) {
                            AppScreen.Home -> {
                                ProductListScreen(
                                    products = products,
                                    modifier = Modifier.padding(innerPadding),
                                    onProductClick = { product ->

                                        scope.launch {
                                            try {
                                                val detail =
                                                    RetrofitClient.apiService.getProductDetail(
                                                        variantId = product.variantId,
                                                        slug = product.slug
                                                    )
                                                currentScreen = AppScreen.ProductDetail(detail)

                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                    }
                                )
                            }

                            is AppScreen.ProductDetail -> {
                                ProductDetailScreen(
                                    detail = screen.detail,
                                    modifier = Modifier.padding(innerPadding),
                                    onBack = {
                                        currentScreen = AppScreen.Home
                                    },
                                    onCartCountChange = { newCount ->
                                        cartCount = newCount
                                    }
                                )
                            }

                            AppScreen.Profile -> {
                                ProfileScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    profile = profileResponse,
                                    error = profileError,
                                    onBack = {
                                        currentScreen = AppScreen.Home
                                    },
                                    onLogout = {
                                        scope.launch {
                                            try {
                                                RetrofitClient.apiService.logout()

                                                isAuthenticated = false
                                                profileResponse = null
                                                currentScreen = AppScreen.LoginContact

                                            } catch (e: Exception) {
                                                // optional: show error
                                            }
                                        }
                                    }
                                )
                            }

                            AppScreen.Cart -> {
                                LaunchedEffect(Unit) {
                                    try {
                                        val response = RetrofitClient.apiService.getBasket()
                                        basketResponse = response
                                        cartCount = response.cartCount
                                    } catch (e: Exception) {

                                    }
                                }

                                CartScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    basket = basketResponse,
                                    onBack = {
                                        currentScreen = AppScreen.Home
                                    },
                                    onNavigateToProduct = { variantId, slug ->
                                        scope.launch {
                                            try {
                                                val detail =
                                                    RetrofitClient.apiService.getProductDetail(
                                                        variantId = variantId,
                                                        slug = slug
                                                    )

                                                currentScreen = AppScreen.ProductDetail(detail)
                                            } catch (e: Exception) {
                                                Log.e("CART_PRODUCT", "failed: ${e.message}", e)
                                            }
                                        }
                                    },
                                    onQuantityChange = { basketItem, quantity ->
                                        scope.launch {
                                            try {
                                                val csrfToken =
                                                    RetrofitClient.getCsrfToken() ?: return@launch
                                                val variantId =
                                                    basketItem.variantId ?: return@launch

                                                val response =
                                                    RetrofitClient.apiService.updateCartQuantity(
                                                        csrfToken = csrfToken,
                                                        productId = basketItem.productId,
                                                        variantId = variantId,
                                                        quantity = quantity
                                                    )

                                                basketResponse = response
                                                cartCount = response.cartCount
                                            } catch (e: Exception) {
                                                Log.e("CART_QTY", "failed: ${e.message}", e)
                                            }
                                        }
                                    },
                                    onCheckoutClick = {
                                        scope.launch {
                                            try {
                                                val response =
                                                    RetrofitClient.apiService.getCheckout()
                                                checkoutResponse = response
                                                currentScreen = AppScreen.Checkout
                                            } catch (e: Exception) {
                                                Log.e("CHECKOUT", "failed: ${e.message}", e)
                                            }
                                        }
                                    }
                                )
                            }

                            AppScreen.Checkout -> {
                                CheckoutScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    checkout = checkoutResponse,
                                    onBack = {
                                        currentScreen = AppScreen.Cart
                                    },
                                    onAddAddressClick = {
                                        // later: currentScreen = AppScreen.AddAddress
                                    },
                                    onProceedToPayment = { shippingAddressId, billingAddressId, useWallet, paymentMethod ->
                                        scope.launch {
                                            try {
                                                val csrfToken = RetrofitClient.getCsrfToken() ?: return@launch
                                                val orderToken = checkoutResponse?.order?.orderToken ?: return@launch

                                                val response = RetrofitClient.apiService.initiateOrder(
                                                    csrfToken = csrfToken,
                                                    orderToken = orderToken,
                                                    shippingAddressId = shippingAddressId,
                                                    billingAddressId = billingAddressId,
                                                    useWallet = if (useWallet) "1" else "0",
                                                    paymentMethod = paymentMethod
                                                )

                                                when (response.nextStep) {

                                                    "initiate_rzp_payment" -> {
                                                        val token = response.orderToken ?: return@launch

                                                        val paymentUrl =
                                                            "${BuildConfig.BASE_URL.trimEnd('/')}/payments/initiate/rzp/payment/$token/"

                                                        currentScreen = AppScreen.PaymentWeb(paymentUrl)
                                                    }

                                                    "initiate_cod_payment" -> {
                                                        val token = response.orderToken ?: return@launch

                                                        val paymentUrl =
                                                            "${BuildConfig.BASE_URL.trimEnd('/')}/payments/initiate/cod/payment/$token/"

                                                        currentScreen = AppScreen.PaymentWeb(paymentUrl)
                                                    }

                                                    "finalize_order" -> {
                                                        val token = response.orderToken ?: return@launch

                                                        val paymentUrl =
                                                            "${BuildConfig.BASE_URL.trimEnd('/')}/orders/finalize/order/$token"

                                                        currentScreen = AppScreen.PaymentWeb(paymentUrl)
                                                    }

                                                    "basket_detail" -> {
                                                        currentScreen = AppScreen.Cart
                                                    }
                                                }

                                            } catch (e: Exception) {
                                                Log.e("CHECKOUT_PAY", "failed: ${e.message}", e)
                                            }
                                        }
                                    }
                                )
                            }

                            is AppScreen.PaymentWeb -> {
                                println("RETROFIT COOKIE: ${RetrofitClient.getCookieHeader()}")
                                PaymentWebViewScreen(
                                    url = screen.url,
                                    onPaymentFinished = { success ->

                                        if (success) {
                                            currentScreen = AppScreen.Home // later → OrderSuccess
                                        } else {
                                            currentScreen = AppScreen.Cart
                                        }
                                    }
                                )
                            }

                            AppScreen.OrderItemHistory -> {
                                OrderItemHistoryScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    response = orderItemHistoryResponse,
                                    onBack = {
                                        currentScreen = AppScreen.Home
                                    },
                                    onItemClick = { item ->
                                        scope.launch {
                                            try {
                                                val token = item.itemToken ?: return@launch

                                                val response = RetrofitClient.apiService.getOrderItemDetail(token)
                                                orderItemDetailResponse = response

                                                currentScreen = AppScreen.OrderItemDetail
                                            } catch (e: Exception) {
                                                Log.e("ORDER_ITEM_DETAIL", "failed: ${e.message}", e)
                                            }
                                        }
                                    }
                                )
                            }

                            AppScreen.OrderItemDetail -> {
                                OrderItemDetailScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    response = orderItemDetailResponse,
                                    onBack = {
                                        currentScreen = AppScreen.OrderItemHistory
                                    },
                                    onOrderDetailClick = { orderToken ->
                                        scope.launch {
                                            try {
                                                val response = RetrofitClient.apiService.getOrderDetail(orderToken)
                                                orderDetailResponse = response
                                                currentScreen = AppScreen.OrderDetail
                                            } catch (e: Exception) {
                                                Log.e("ORDER_DETAIL", "failed: ${e.message}", e)
                                            }
                                        }
                                    },
                                    onReturnItemClick = { itemToken ->
                                        scope.launch {
                                            try {
                                                returnOrderItemError = null

                                                val response = RetrofitClient.apiService.getReturnOrderItem(itemToken)
                                                returnOrderItemResponse = response

                                                currentScreen = AppScreen.ReturnOrderItem
                                            } catch (e: Exception) {
                                                Log.e("RETURN_ITEM", "failed: ${e.message}", e)
                                            }
                                        }
                                    },
                                    onReviewItemClick = { itemToken ->
                                        scope.launch {
                                            try {
                                                reviewOrderItemError = null

                                                val response = RetrofitClient.apiService.getReviewOrderItem(itemToken)
                                                reviewOrderItemResponse = response

                                                currentScreen = AppScreen.ReviewOrderItem
                                            } catch (e: Exception) {
                                                Log.e("REVIEW_ITEM", "failed: ${e.message}", e)
                                            }
                                        }
                                    },
                                    onTrackItemClick = { itemToken ->
                                        // later: track item screen
                                    },
                                    onSupportClick = { itemToken ->
                                        // later: support screen
                                    },
                                    onProductClick = { variantId, slug ->
                                        scope.launch {
                                            try {
                                                val detail = RetrofitClient.apiService.getProductDetail(
                                                    variantId = variantId,
                                                    slug = slug
                                                )
                                                currentScreen = AppScreen.ProductDetail(detail)
                                            } catch (e: Exception) {
                                                Log.e("ITEM_PRODUCT", "failed: ${e.message}", e)
                                            }
                                        }
                                    }
                                )
                            }

                            AppScreen.OrderDetail -> {
                                OrderDetailScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    response = orderDetailResponse,
                                    onBack = {
                                        currentScreen = AppScreen.OrderItemDetail
                                    },
                                    onCancelOrderClick = {
                                        scope.launch {
                                            try {
                                                val orderToken = orderDetailResponse?.order?.orderToken ?: return@launch

                                                cancelOrderError = null
                                                val response = RetrofitClient.apiService.getCancelOrder(orderToken)

                                                cancelOrderResponse = response
                                                currentScreen = AppScreen.CancelOrder
                                            } catch (e: Exception) {
                                                Log.e("CANCEL_ORDER", "failed: ${e.message}", e)
                                            }
                                        }
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

                            AppScreen.CancelOrder -> {
                                CancelOrderScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    response = cancelOrderResponse,
                                    error = cancelOrderError,
                                    onBack = {
                                        currentScreen = AppScreen.OrderDetail
                                    },
                                    onConfirmCancel = { reason, refundAccount ->
                                        scope.launch {
                                            try {
                                                val csrfToken = RetrofitClient.getCsrfToken() ?: return@launch
                                                val orderToken = cancelOrderResponse?.order?.orderToken ?: return@launch

                                                val response = RetrofitClient.apiService.submitCancelOrder(
                                                    csrfToken = csrfToken,
                                                    orderToken = orderToken,
                                                    cancelReason = reason,
                                                    refundAccount = refundAccount
                                                )

                                                if (response.success && response.nextStep == "order_details") {
                                                    val detail = RetrofitClient.apiService.getOrderDetail(
                                                        response.orderToken ?: orderToken
                                                    )

                                                    orderDetailResponse = detail
                                                    cancelOrderError = null
                                                    currentScreen = AppScreen.OrderDetail
                                                } else {
                                                    cancelOrderError = response.error ?: "Unable to cancel order."
                                                }
                                            } catch (e: Exception) {
                                                cancelOrderError = e.message ?: "Unable to cancel order."
                                                Log.e("CANCEL_SUBMIT", "failed: ${e.message}", e)
                                            }
                                        }
                                    }
                                )
                            }

                            AppScreen.ReturnOrderItem -> {
                                ReturnOrderItemScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    response = returnOrderItemResponse,
                                    error = returnOrderItemError,
                                    onBack = {
                                        currentScreen = AppScreen.OrderItemDetail
                                    },
                                    onSubmitReturn = { returnReason, refundAccount ->
                                        scope.launch {
                                            try {
                                                val csrfToken = RetrofitClient.getCsrfToken() ?: return@launch
                                                val itemToken = returnOrderItemResponse?.orderItem?.itemToken ?: return@launch

                                                val response = RetrofitClient.apiService.submitReturnOrderItem(
                                                    csrfToken = csrfToken,
                                                    itemToken = itemToken,
                                                    returnReason = returnReason,
                                                    refundAccount = refundAccount
                                                )

                                                if (response.success && response.nextStep == "order_item_details") {
                                                    val detail = RetrofitClient.apiService.getOrderItemDetail(
                                                        response.itemToken ?: itemToken
                                                    )

                                                    orderItemDetailResponse = detail
                                                    returnOrderItemError = null
                                                    currentScreen = AppScreen.OrderItemDetail
                                                } else {
                                                    returnOrderItemError = response.error ?: "Unable to submit return request."
                                                }
                                            } catch (e: Exception) {
                                                returnOrderItemError = e.message ?: "Unable to submit return request."
                                                Log.e("RETURN_SUBMIT", "failed: ${e.message}", e)
                                            }
                                        }
                                    }
                                )
                            }

                            AppScreen.ReviewOrderItem -> {
                                ReviewOrderItemScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    response = reviewOrderItemResponse,
                                    error = reviewOrderItemError,
                                    onBack = {
                                        currentScreen = AppScreen.OrderItemDetail
                                    },
                                    onSubmitReview = { rating, review ->
                                        scope.launch {
                                            try {
                                                val csrfToken = RetrofitClient.getCsrfToken() ?: return@launch
                                                val itemToken = reviewOrderItemResponse?.item?.itemToken ?: return@launch

                                                val response = RetrofitClient.apiService.submitReviewOrderItem(
                                                    csrfToken = csrfToken,
                                                    itemToken = itemToken,
                                                    rating = rating,
                                                    review = review
                                                )

                                                if (response.success && response.nextStep == "order_item_details") {
                                                    val detail = RetrofitClient.apiService.getOrderItemDetail(
                                                        response.itemToken ?: itemToken
                                                    )

                                                    orderItemDetailResponse = detail
                                                    reviewOrderItemError = null
                                                    currentScreen = AppScreen.OrderItemDetail
                                                } else {
                                                    reviewOrderItemError = response.error ?: "Unable to submit review."
                                                }
                                            } catch (e: Exception) {
                                                reviewOrderItemError = e.message ?: "Unable to submit review."
                                                Log.e("REVIEW_SUBMIT", "failed: ${e.message}", e)
                                            }
                                        }
                                    }
                                )
                            }

                            AppScreen.LoginContact -> {
                                LoginContactScreen(
                                    onLogoClick = {
                                        currentScreen = AppScreen.Home
                                    },
                                    error = loginContactError,
                                    onContinue = { contact ->
                                        scope.launch {
                                            try {
                                                loginContactError = null

                                                val csrfToken = RetrofitClient.getCsrfToken()

                                                if (csrfToken == null) {
                                                    loginContactError =
                                                        "Session not ready. Please try again."
                                                    return@launch
                                                }

                                                val response =
                                                    RetrofitClient.apiService.loginContact(
                                                        csrfToken = csrfToken,
                                                        contact = contact
                                                    )

                                                if (response.success) {
                                                    when (response.nextStep) {
                                                        "login_password" -> {
                                                            currentScreen = AppScreen.LoginPassword(
                                                                response.contact ?: contact
                                                            )
                                                        }

                                                        "products:product_list",
                                                        "product_list",
                                                        "home" -> {
                                                            currentScreen = AppScreen.Home
                                                        }

                                                        else -> {
                                                            loginContactError =
                                                                "Unexpected next step: ${response.nextStep}"
                                                        }
                                                    }
                                                } else {
                                                    loginContactError =
                                                        response.error ?: response.errorMsg
                                                                ?: "Login failed"
                                                }
                                            } catch (e: Exception) {
                                                loginContactError = e.message ?: "Login failed"
                                            }
                                        }
                                    },
                                    onSignupClick = {
                                        // Signup screen next
                                    }
                                )
                            }

                            is AppScreen.LoginPassword -> {
                                LoginPasswordScreen(
                                    onLogoClick = {
                                        currentScreen = AppScreen.Home
                                    },
                                    contact = screen.contact,
                                    error = loginPasswordError,
                                    attemptsLeft = loginAttemptsLeft,
                                    onLogin = { password ->
                                        scope.launch {
                                            try {
                                                loginPasswordError = null

                                                val csrfToken = RetrofitClient.getCsrfToken()
                                                if (csrfToken == null) {
                                                    loginPasswordError = "Session not ready"
                                                    return@launch
                                                }

                                                val response =
                                                    RetrofitClient.apiService.loginPassword(
                                                        csrfToken = csrfToken,
                                                        password = password
                                                    )

                                                if (response.success && response.authenticated == true) {
                                                    isAuthenticated = true
                                                    currentScreen = AppScreen.Home
                                                } else {
                                                    isAuthenticated = false
                                                    loginPasswordError =
                                                        response.error ?: response.errorMsg
                                                                ?: "Login failed"

                                                    loginAttemptsLeft = response.loginAttemptsLeft
                                                }

                                            } catch (e: Exception) {
                                                isAuthenticated = false
                                                loginPasswordError = e.message ?: "Login failed"
                                            }
                                        }
                                    },
                                    onOtpLogin = {
                                        // later → AppScreen.LoginOtp
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /*
    private fun openRazorpay(payload: RzpPayload) {
        val checkout = Checkout()
        checkout.setKeyID(payload.key)

        val options = JSONObject().apply {
            put("name", payload.storeName)
            put("description", payload.description)
            put("currency", payload.currency)
            put("amount", payload.amountMinor)
            put("order_id", payload.rzpOrderId)

            put("prefill", JSONObject().apply {
                put("email", payload.userEmail)
                put("contact", payload.userPhone)
            })

            put("theme", JSONObject().apply {
                put("color", "#000000")
            })
        }

        checkout.open(this, options)
    }

    override fun onPaymentSuccess(
        razorpayPaymentId: String?,
        paymentData: PaymentData?
    ) {
        Log.d("RZP_SUCCESS", "paymentId=$razorpayPaymentId")
        Log.d("RZP_SUCCESS", "orderId=${paymentData?.orderId}")
        Log.d("RZP_SUCCESS", "signature=${paymentData?.signature}")

        // next: call verify_rzp_payment API
    }

    override fun onPaymentError(
        code: Int,
        response: String?,
        paymentData: PaymentData?
    ) {
        Log.e("RZP_ERROR", "code=$code response=$response")
    }

     */

}
