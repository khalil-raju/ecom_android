package com.ecom.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.ecom.app.model.account.ProfileResponse
import com.ecom.app.model.basket.BasketResponse
import com.ecom.app.model.order.CheckoutResponse
import com.ecom.app.model.product.Product
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.layouts.AppScaffold
import com.ecom.app.ui.layouts.DrawerContentType
import com.ecom.app.ui.navigations.AppRouter
import com.ecom.app.ui.navigations.AppScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RetrofitClient.init(applicationContext)

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        setContent {
            var drawerContentType by remember {
                mutableStateOf(DrawerContentType.SEARCH_MENU)
            }

            var isSearchDrawerOpen by remember {
                mutableStateOf(false)
            }

            var currentScreen by remember {
                mutableStateOf<AppScreen>(AppScreen.Home)
            }

            var backScreenStack by remember {
                mutableStateOf<List<AppScreen>>(emptyList())
            }

            fun setScreenTo(screen: AppScreen) {
                if (screen != currentScreen) {
                    backScreenStack = backScreenStack + currentScreen
                    currentScreen = screen
                }
            }

            fun replaceScreenTo(screen: AppScreen) {
                backScreenStack = emptyList()
                currentScreen = screen
            }

            fun goBackScreen() {
                if (backScreenStack.isNotEmpty()) {
                    currentScreen = backScreenStack.last()
                    backScreenStack = backScreenStack.dropLast(1)
                }
            }

            BackHandler(enabled = backScreenStack.isNotEmpty()) {
                goBackScreen()
            }

            var products by remember {
                mutableStateOf<List<Product>>(emptyList())
            }

            var cartCount by remember {
                mutableIntStateOf(0)
            }

            var isAuthenticated by remember {
                mutableStateOf(false)
            }

            var profileResponse by remember {
                mutableStateOf<ProfileResponse?>(null)
            }

            var profileError by remember {
                mutableStateOf<String?>(null)
            }

            var basketResponse by remember {
                mutableStateOf<BasketResponse?>(null)
            }

            var checkoutResponse by remember {
                mutableStateOf<CheckoutResponse?>(null)
            }

            val scope = rememberCoroutineScope()
            val context = LocalContext.current

            LaunchedEffect(Unit) {
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

            AppScaffold(
                isSearchDrawerOpen = isSearchDrawerOpen,
                drawerContentType = drawerContentType,
                onCloseDrawer = {
                    isSearchDrawerOpen = false
                },

                currentScreen = currentScreen,

                onBackClick = {
                    goBackScreen()
                },

                onLogoClick = {
                    replaceScreenTo(AppScreen.Home)
                },

                onSearchClick = {
                    drawerContentType = DrawerContentType.SEARCH_MENU
                    isSearchDrawerOpen = true
                },

                onSearchSubmit = { query ->
                    scope.launch {
                        try {
                            val response = RetrofitClient.apiService.searchProducts(query)

                            products = response.products
                            isSearchDrawerOpen = false
                            replaceScreenTo(AppScreen.Home)

                        } catch (e: Exception) {
                            Log.e("SEARCH_PRODUCTS", "failed: ${e.message}", e)
                        }
                    }
                },

                onHomeClick = {
                    replaceScreenTo(AppScreen.Home)
                },

                onOrdersClick = {
                    setScreenTo(AppScreen.OrderItemHistory)
                },

                onWishlistClick = {
                    setScreenTo(AppScreen.Wishlist)
                },

                cartCount = cartCount,

                onCartClick = {
                    setScreenTo(AppScreen.Cart)
                },

                onProfileClick = {
                    scope.launch {
                        try {
                            profileError = null

                            val response = RetrofitClient.apiService.getProfile()
                            profileResponse = response
                            isAuthenticated = response.success && response.authenticated

                            setScreenTo(
                                if (isAuthenticated) {
                                    AppScreen.Profile
                                } else {
                                    AppScreen.LoginContact()
                                }
                            )

                        } catch (e: Exception) {
                            isAuthenticated = false
                            setScreenTo(AppScreen.LoginContact())
                        }
                    }
                }
            ) { innerPadding ->

                AppRouter(
                    innerPadding = innerPadding,
                    currentScreen = currentScreen,
                    scope = scope,
                    context = context,
                    products = products,
                    basketResponse = basketResponse,
                    checkoutResponse = checkoutResponse,
                    profileResponse = profileResponse,
                    profileError = profileError,
                    setScreenTo = { setScreenTo(it) },
                    replaceScreenTo = { replaceScreenTo(it) },
                    setCartCount = { cartCount = it },
                    setBasketResponse = { basketResponse = it },
                    setCheckoutResponse = { checkoutResponse = it },
                    setAuthenticated = { isAuthenticated = it },
                    setProfileResponse = { profileResponse = it }
                )
            }
        }
    }
}