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
import androidx.compose.material3.Text

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

import kotlinx.coroutines.launch


enum class DrawerContentType {
    SIDE_MENU,
    SEARCH_MENU
}

sealed class AppScreen {
    data object Home : AppScreen()
    data class ProductDetail(val detail: ProductDetailResponse) : AppScreen()
    data object LoginContact : AppScreen()
    data class LoginPassword(val contact: String) : AppScreen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                try {
                    val response = RetrofitClient.apiService.getProducts()
                    products = response.products
                } catch (e: Exception) {
                    e.printStackTrace()
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
                                        onClose = {
                                            scope.launch {
                                                drawerState.close()
                                            }
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
                                    currentScreen = AppScreen.LoginContact
                                }
                            )
                        }
                    ) { innerPadding ->

                        when (val screen = currentScreen) {
                            AppScreen.Home -> {
                                ProductListScreen(
                                    products = products,
                                    modifier = Modifier.padding(innerPadding),
                                    onProductClick = { product ->

                                        Log.d(
                                            "PRODUCT_CLICK",
                                            "Clicked: ${product.name}, ${product.variantId}, ${product.slug}"
                                        )

                                        scope.launch {
                                            try {
                                                val detail =
                                                    RetrofitClient.apiService.getProductDetail(
                                                        variantId = product.variantId,
                                                        slug = product.slug
                                                    )

                                                currentScreen = AppScreen.ProductDetail(detail)

                                                Log.d("PRODUCT_CLICK", "Detail loaded")
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                Log.e(
                                                    "PRODUCT_CLICK",
                                                    "Detail failed: ${e.message}",
                                                    e
                                                )
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

                            AppScreen.LoginContact -> {
                                LoginContactScreen(
                                    error = loginContactError,
                                    onContinue = { contact ->
                                        scope.launch {
                                            try {
                                                loginContactError = null

                                                val csrfToken = RetrofitClient.getCsrfToken()

                                                if (csrfToken == null) {
                                                    loginContactError = "Session not ready. Please try again."
                                                    return@launch
                                                }

                                                val response = RetrofitClient.apiService.loginContact(
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
                                                            loginContactError = "Unexpected next step: ${response.nextStep}"
                                                        }
                                                    }
                                                } else {
                                                    loginContactError =
                                                        response.error ?: response.errorMsg ?: "Login failed"
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

                                                val response = RetrofitClient.apiService.loginPassword(
                                                    csrfToken = csrfToken,
                                                    password = password
                                                )

                                                if (response.success) {
                                                    when (response.nextStep) {
                                                        "products:product_list",
                                                        "product_list",
                                                        "home" -> {
                                                            currentScreen = AppScreen.Home
                                                        }

                                                        else -> {
                                                            loginPasswordError =
                                                                "Unexpected next step: ${response.nextStep}"
                                                        }
                                                    }
                                                } else {
                                                    loginPasswordError =
                                                        response.error ?: response.errorMsg ?: "Login failed"

                                                    loginAttemptsLeft = response.loginAttemptsLeft
                                                }

                                            } catch (e: Exception) {
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
}
