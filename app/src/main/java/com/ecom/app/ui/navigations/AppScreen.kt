package com.ecom.app.ui.navigations

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.ecom.app.model.account.ProfileResponse
import com.ecom.app.model.basket.BasketResponse
import com.ecom.app.model.order.CheckoutResponse
import com.ecom.app.model.product.Product
import com.ecom.app.model.product.ProductDetailResponse
import com.ecom.app.ui.routes.account.AddAddressRoute
import com.ecom.app.ui.routes.account.ChangeContactRoute
import com.ecom.app.ui.routes.account.ChangeNameRoute
import com.ecom.app.ui.routes.account.ContactChangeType
import com.ecom.app.ui.routes.account.LoginContactRoute
import com.ecom.app.ui.routes.account.LoginPasswordRoute
import com.ecom.app.ui.routes.account.OtpVerifyRoute
import com.ecom.app.ui.routes.account.ProfileRoute
import com.ecom.app.ui.routes.account.SavedAddressesRoute
import com.ecom.app.ui.routes.basket.CartRoute
import com.ecom.app.ui.routes.order.CancelOrderRoute
import com.ecom.app.ui.routes.order.CheckoutRoute
import com.ecom.app.ui.routes.order.OrderDetailRoute
import com.ecom.app.ui.routes.order.OrderItemDetailRoute
import com.ecom.app.ui.routes.order.OrderItemHistoryRoute
import com.ecom.app.ui.routes.order.ReturnOrderItemRoute
import com.ecom.app.ui.routes.payment.PaymentWebRoute
import com.ecom.app.ui.routes.product.ProductDetailRoute
import com.ecom.app.ui.routes.product.ProductListRoute
import com.ecom.app.ui.routes.review.ReviewOrderItemRoute
import kotlinx.coroutines.CoroutineScope

enum class OtpPurpose {
    LOGIN,
    SIGNUP,
    CHANGE_PHONE,
    CHANGE_EMAIL
}

sealed interface AppScreen {
    fun isFullScreen(): Boolean {
        return this is AppScreen.PaymentWeb ||
                this is AppScreen.LoginContact ||
                this is AppScreen.LoginPassword ||
                this is AppScreen.OtpVerify
    }

    data object Home : AppScreen
    data class ProductDetail(val detail: ProductDetailResponse) : AppScreen
    data object Cart : AppScreen
    data object Checkout : AppScreen
    data object OrderItemHistory : AppScreen
    data class OrderItemDetail(val itemToken: String) : AppScreen
    data class OrderDetail(val orderToken: String) : AppScreen
    data class CancelOrder(val orderToken: String) : AppScreen
    data class ReturnOrderItem(val itemToken: String) : AppScreen
    data class ReviewOrderItem(val itemToken: String) : AppScreen
    data class PaymentWeb(val url: String) : AppScreen
    data object Profile : AppScreen
    data object ChangeName : AppScreen
    data object ChangeEmail : AppScreen
    data object ChangePhone : AppScreen
    data object LoginContact : AppScreen
    data class LoginPassword(val contact: String) : AppScreen
    data class OtpVerify(val contact: String, val purpose: OtpPurpose) : AppScreen
    data object SavedAddresses : AppScreen
    data class AddAddress(val addressId: Int? = null) : AppScreen
}

@Composable
fun AppRouter(
    innerPadding: PaddingValues,
    currentScreen: AppScreen,
    scope: CoroutineScope,
    context: Context,
    products: List<Product>,
    basketResponse: BasketResponse?,
    checkoutResponse: CheckoutResponse?,
    profileResponse: ProfileResponse?,
    profileError: String?,
    setScreen: (AppScreen) -> Unit,
    setCartCount: (Int) -> Unit,
    setBasketResponse: (BasketResponse) -> Unit,
    setCheckoutResponse: (CheckoutResponse) -> Unit,
    setAuthenticated: (Boolean) -> Unit,
    setProfileResponse: (ProfileResponse?) -> Unit
) {

    when (currentScreen) {
        AppScreen.Home -> ProductListRoute(
            innerPadding = innerPadding,
            scope = scope,
            products = products,
            navigateProductDetail = { setScreen(AppScreen.ProductDetail(it)) }
        )

        is AppScreen.ProductDetail -> ProductDetailRoute(
            innerPadding = innerPadding,
            detail = currentScreen.detail,
            navigateBack = { setScreen(AppScreen.Home) },
            onCartCountChange = setCartCount
        )

        AppScreen.Cart -> CartRoute(
            innerPadding = innerPadding,
            scope = scope,
            basketResponse = basketResponse,
            setBasketResponse = setBasketResponse,
            setCheckoutResponse = setCheckoutResponse,
            setCartCount = setCartCount,
            navigateHome = { setScreen(AppScreen.Home) },
            navigateProductDetail = { setScreen(AppScreen.ProductDetail(it)) },
            navigateCheckout = { setScreen(AppScreen.Checkout) }
        )

        AppScreen.Checkout -> CheckoutRoute(
            innerPadding = innerPadding,
            scope = scope,
            checkoutResponse = checkoutResponse,
            navigateCart = { setScreen(AppScreen.Cart) },
            navigatePaymentWeb = { setScreen(AppScreen.PaymentWeb(it)) },
            onAddAddressClick = { }
        )

        is AppScreen.PaymentWeb -> PaymentWebRoute(
            url = currentScreen.url,
            onPaymentSuccess = { setScreen(AppScreen.OrderDetail(it)) },
            onPaymentFailed = { setScreen(AppScreen.Cart) }
        )

        AppScreen.OrderItemHistory -> OrderItemHistoryRoute(
            innerPadding = innerPadding,
            navigateBack = { setScreen(AppScreen.Home) },
            navigateOrderItemDetail = { setScreen(AppScreen.OrderItemDetail(it)) }
        )

        is AppScreen.OrderItemDetail -> OrderItemDetailRoute(
            innerPadding = innerPadding,
            scope = scope,
            itemToken = currentScreen.itemToken,
            navigateBack = { setScreen(AppScreen.OrderItemHistory) },
            navigateOrderDetail = { setScreen(AppScreen.OrderDetail(it)) },
            navigateReturnOrderItem = { setScreen(AppScreen.ReturnOrderItem(it)) },
            navigateReviewOrderItem = { setScreen(AppScreen.ReviewOrderItem(it)) },
            navigateProductDetail = { setScreen(AppScreen.ProductDetail(it)) }
        )

        is AppScreen.OrderDetail -> OrderDetailRoute(
            innerPadding = innerPadding,
            scope = scope,
            context = context,
            orderToken = currentScreen.orderToken,
            navigateBack = { setScreen(AppScreen.OrderItemHistory) },
            navigateCancelOrder = { setScreen(AppScreen.CancelOrder(it)) }
        )

        is AppScreen.CancelOrder -> CancelOrderRoute(
            innerPadding = innerPadding,
            scope = scope,
            orderToken = currentScreen.orderToken,
            navigateBack = { setScreen(AppScreen.OrderDetail(currentScreen.orderToken)) },
            navigateOrderDetail = { setScreen(AppScreen.OrderDetail(it)) }
        )

        is AppScreen.ReturnOrderItem -> ReturnOrderItemRoute(
            innerPadding = innerPadding,
            scope = scope,
            itemToken = currentScreen.itemToken,
            navigateBack = { setScreen(AppScreen.OrderItemDetail(currentScreen.itemToken)) },
            navigateOrderItemDetail = { setScreen(AppScreen.OrderItemDetail(it)) }
        )

        is AppScreen.ReviewOrderItem -> ReviewOrderItemRoute(
            innerPadding = innerPadding,
            scope = scope,
            itemToken = currentScreen.itemToken,
            navigateBack = { setScreen(AppScreen.OrderItemDetail(currentScreen.itemToken)) },
            navigateOrderItemDetail = { setScreen(AppScreen.OrderItemDetail(it)) }
        )

        AppScreen.LoginContact -> LoginContactRoute(
            scope = scope,
            navigateHome = { setScreen(AppScreen.Home) },
            navigateLoginPassword = { setScreen(AppScreen.LoginPassword(it)) },
            navigateSignup = { }
        )

        is AppScreen.LoginPassword -> LoginPasswordRoute(
            scope = scope,
            contact = currentScreen.contact,
            navigateHome = { setScreen(AppScreen.Home) },
            onAuthenticated = {
                setAuthenticated(true)
                setScreen(AppScreen.Home)
            },
            onUnauthenticated = {
                setAuthenticated(false)
            },
            navigateOtpLogin = {
                setScreen(
                    AppScreen.OtpVerify(
                        contact = currentScreen.contact,
                        purpose = OtpPurpose.LOGIN
                    )
                )
            }
        )

        is AppScreen.OtpVerify -> OtpVerifyRoute(
            scope = scope,
            contact = currentScreen.contact,
            purpose = currentScreen.purpose,
            navigateHome = {
                setScreen(AppScreen.Home)
            },
            onVerified = {
                when (currentScreen.purpose) {
                    OtpPurpose.LOGIN,
                    OtpPurpose.SIGNUP -> {
                        setAuthenticated(true)
                        setScreen(AppScreen.Home)
                    }

                    OtpPurpose.CHANGE_PHONE,
                    OtpPurpose.CHANGE_EMAIL -> {
                        setScreen(AppScreen.Profile)
                    }
                }
            },
            onProfileUpdated = {
                setProfileResponse(it)
            }
        )

        AppScreen.Profile -> ProfileRoute(
            innerPadding = innerPadding,
            scope = scope,
            profileResponse = profileResponse,
            profileError = profileError,
            navigateBack = { setScreen(AppScreen.Home) },
            navigateSavedAddresses = {
                setScreen(AppScreen.SavedAddresses)
            },
            navigateAddAddress = {
                setScreen(AppScreen.AddAddress())
            },
            navigateChangeName = {
                setScreen(AppScreen.ChangeName)
            },
            navigateChangeEmail = {
                setScreen(AppScreen.ChangeEmail)
            },
            navigateChangePhone = {
                setScreen(AppScreen.ChangePhone)
            },
            onLoggedOut = {
                setAuthenticated(false)
                setProfileResponse(null)
                setScreen(AppScreen.LoginContact)
            }
        )

        AppScreen.ChangeName -> ChangeNameRoute(
            innerPadding = innerPadding,
            scope = scope,
            navigateBack = {
                setScreen(AppScreen.Profile)
            },
            navigateProfile = {
                setScreen(AppScreen.Profile)
            },
            onProfileUpdated = {
                setProfileResponse(it)
            }
        )

        AppScreen.ChangeEmail -> ChangeContactRoute(
            innerPadding = innerPadding,
            scope = scope,
            type = ContactChangeType.EMAIL,
            navigateBack = { setScreen(AppScreen.Profile) },
            navigateOtp = { contact, purpose ->
                setScreen(AppScreen.OtpVerify(contact, purpose))
            }
        )

        AppScreen.ChangePhone -> ChangeContactRoute(
            innerPadding = innerPadding,
            scope = scope,
            type = ContactChangeType.PHONE,
            navigateBack = { setScreen(AppScreen.Profile) },
            navigateOtp = { contact, purpose ->
                setScreen(AppScreen.OtpVerify(contact, purpose))
            }
        )

        is AppScreen.AddAddress -> {
            AddAddressRoute(
                innerPadding = innerPadding,
                scope = scope,
                addressId = currentScreen.addressId,
                navigateBack = {
                    setScreen(AppScreen.SavedAddresses)
                },
                navigateSavedAddresses = {
                    setScreen(AppScreen.SavedAddresses)
                }
            )
        }

        AppScreen.SavedAddresses -> {
            SavedAddressesRoute(
                innerPadding = innerPadding,
                scope = scope,
                navigateBack = {
                    setScreen(AppScreen.Profile)
                },
                navigateAddAddress = {
                    setScreen(AppScreen.AddAddress())
                },
                navigateEditAddress = { addressId ->
                    setScreen(AppScreen.AddAddress(addressId))
                }
            )
        }
    }
}