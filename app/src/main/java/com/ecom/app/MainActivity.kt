package com.ecom.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.ecom.app.model.account.ProfileResponse
import com.ecom.app.model.basket.BasketResponse
import com.ecom.app.model.order.CheckoutResponse
import com.ecom.app.model.product.Product
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.components.SideMenuCategory
import com.ecom.app.ui.components.SideMenuCategoryChild
import com.ecom.app.ui.layouts.AppScaffold
import com.ecom.app.ui.layouts.DrawerContentType
import com.ecom.app.ui.navigations.AppRouter
import com.ecom.app.ui.navigations.AppScreen
import com.ecom.app.util.openExternalUrl
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
            var drawerContentType by remember { mutableStateOf(DrawerContentType.SIDE_MENU) }

            var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }
            var backScreenStack by remember { mutableStateOf<List<AppScreen>>(emptyList()) }

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

            fun clearBackStackAndReplaceScreenTo(screen: AppScreen) {
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

            var products by remember { mutableStateOf<List<Product>>(emptyList()) }
            var parentCategories by remember { mutableStateOf<List<SideMenuCategory>>(emptyList()) }

            var cartCount by remember { mutableIntStateOf(0) }
            var isAuthenticated by remember { mutableStateOf(false) }

            var profileResponse by remember { mutableStateOf<ProfileResponse?>(null) }
            var profileError by remember { mutableStateOf<String?>(null) }

            var basketResponse by remember { mutableStateOf<BasketResponse?>(null) }
            var checkoutResponse by remember { mutableStateOf<CheckoutResponse?>(null) }

            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
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
                    val categoryResponse = RetrofitClient.apiService.getCategoryMenu()

                    parentCategories = categoryResponse.categories.map { cat ->
                        SideMenuCategory(
                            name = cat.name,
                            slug = cat.slug,
                            children = cat.children.map { child ->
                                SideMenuCategoryChild(
                                    name = child.name,
                                    slug = child.slug
                                )
                            }
                        )
                    }
                } catch (e: Exception) {
                    Log.e("INIT_CATEGORIES", "failed: ${e.message}", e)
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
                drawerState = drawerState,
                drawerContentType = drawerContentType,
                currentScreen = currentScreen,
                isAuthenticated = isAuthenticated,
                cartCount = cartCount,
                parentCategories = parentCategories,

                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                },

                onMenuClick = {
                    drawerContentType = DrawerContentType.SIDE_MENU
                    scope.launch { drawerState.open() }
                },

                onSearchClick = {
                    drawerContentType = DrawerContentType.SEARCH_MENU
                    scope.launch { drawerState.open() }
                },

                onLogoClick = {
                    clearBackStackAndReplaceScreenTo(AppScreen.Home)
                },

                onProfileClick = {
                    scope.launch {
                        try {
                            profileError = null

                            val response = RetrofitClient.apiService.getProfile()
                            profileResponse = response
                            isAuthenticated = response.success && response.authenticated

                            setScreenTo(
                                if (isAuthenticated) AppScreen.Profile
                                else AppScreen.LoginContact
                            )
                        } catch (e: Exception) {
                            isAuthenticated = false
                            setScreenTo(AppScreen.LoginContact)
                        }
                    }
                },

                onCartClick = {
                    setScreenTo(AppScreen.Cart)
                },

                onHomeClick = {
                    clearBackStackAndReplaceScreenTo(AppScreen.Home)
                },

                onLoginClick = {
                    setScreenTo(AppScreen.LoginContact)
                },

                onSignupClick = {
                    setScreenTo(AppScreen.SignupContact)
                },

                onCategoryClick = { parentSlug, childSlug ->
                    scope.launch {
                        try {
                            val response = if (childSlug.isNullOrBlank()) {
                                RetrofitClient.apiService.getProductsByParentCategory(parentSlug)
                            } else {
                                RetrofitClient.apiService.getProductsByChildCategory(
                                    parentSlug = parentSlug,
                                    childSlug = childSlug
                                )
                            }

                            products = response.products
                            clearBackStackAndReplaceScreenTo(AppScreen.Home)

                        } catch (e: Exception) {
                            Log.e("CATEGORY_PRODUCTS", "failed: ${e.message}", e)
                        }
                    }
                },

                onOrdersClick = {
                    setScreenTo(AppScreen.OrderItemHistory)
                },

                onWalletClick = {
                    setScreenTo(AppScreen.Wallet)
                },

                onWishlistClick = {
                    setScreenTo(AppScreen.Cart)
                },

                onLogoutClick = {
                    scope.launch {
                        try {
                            RetrofitClient.apiService.logout()
                            isAuthenticated = false
                            profileResponse = null
                            clearBackStackAndReplaceScreenTo(AppScreen.LoginContact)
                        } catch (e: Exception) {
                            Log.e("SIDE_MENU_LOGOUT", "failed: ${e.message}", e)
                        }
                    }
                },

                onTermsAndConditionsClick = {
                    openExternalUrl(
                        context,
                        "${BuildConfig.BASE_URL.trimEnd('/')}/about-us/terms-and-conditions/"
                    )
                },

                onPrivacyPolicyClick = {
                    openExternalUrl(
                        context,
                        "${BuildConfig.BASE_URL.trimEnd('/')}/about-us/privacy-policy/"
                    )
                },

                onReturnPolicyClick = {
                    openExternalUrl(
                        context,
                        "${BuildConfig.BASE_URL.trimEnd('/')}/about-us/returns-and-shipping-policy/"
                    )
                },

                onContactUsClick = {
                    openExternalUrl(
                        context,
                        "${BuildConfig.BASE_URL.trimEnd('/')}/about-us/contact-us/"
                    )
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