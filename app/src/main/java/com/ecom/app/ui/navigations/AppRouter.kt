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
import com.ecom.app.ui.routes.account.ChangePasswordRoute
import com.ecom.app.ui.routes.account.ContactChangeType
import com.ecom.app.ui.routes.account.LoginContactRoute
import com.ecom.app.ui.routes.account.LoginPasswordRoute
import com.ecom.app.ui.routes.account.OtpVerifyRoute
import com.ecom.app.ui.routes.account.ProfileRoute
import com.ecom.app.ui.routes.account.SavedAddressesRoute
import com.ecom.app.ui.routes.account.SignupContactRoute
import com.ecom.app.ui.routes.account.SignupPasswordRoute
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
import com.ecom.app.ui.routes.wallet.WalletRoute
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
                this is AppScreen.SignupContact ||
                this is AppScreen.SignupPassword ||
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
    data object ChangePassword : AppScreen
    data object LoginContact : AppScreen
    data class LoginPassword(val contact: String) : AppScreen
    data object SignupContact : AppScreen
    data class SignupPassword(val contact: String) : AppScreen
    data class OtpVerify(val contact: String, val purpose: OtpPurpose) : AppScreen
    data object SavedAddresses : AppScreen
    data class AddAddress(val addressId: Int? = null) : AppScreen
    data object Wallet : AppScreen
}
@Composable
fun AppRouter(
    innerPadding: PaddingValues,
    currentScreen: AppScreen,
    setScreenTo: (AppScreen) -> Unit,
    replaceScreenTo: (AppScreen) -> Unit,
    scope: CoroutineScope,
    context: Context,
    products: List<Product>,
    basketResponse: BasketResponse?,
    checkoutResponse: CheckoutResponse?,
    profileResponse: ProfileResponse?,
    profileError: String?,
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
            navigateProductDetail = { setScreenTo(AppScreen.ProductDetail(it)) }
        )

        is AppScreen.ProductDetail -> ProductDetailRoute(
            innerPadding = innerPadding,
            detail = currentScreen.detail,
            navigateBack = { replaceScreenTo(AppScreen.Home) },
            onCartCountChange = setCartCount
        )

        AppScreen.Cart -> CartRoute(
            innerPadding = innerPadding,
            scope = scope,
            basketResponse = basketResponse,
            setBasketResponse = setBasketResponse,
            setCheckoutResponse = setCheckoutResponse,
            setCartCount = setCartCount,
            navigateHome = { replaceScreenTo(AppScreen.Home) },
            navigateProductDetail = { setScreenTo(AppScreen.ProductDetail(it)) },
            navigateCheckout = { setScreenTo(AppScreen.Checkout) },
            navigateAddAddress = { setScreenTo(AppScreen.AddAddress()) },
            navigateSignupOtp = { contact ->
                setScreenTo(
                    AppScreen.OtpVerify(
                        contact = contact,
                        purpose = OtpPurpose.SIGNUP
                    )
                )
            }
        )

        AppScreen.Checkout -> CheckoutRoute(
            innerPadding = innerPadding,
            scope = scope,
            checkoutResponse = checkoutResponse,
            navigateCart = { replaceScreenTo(AppScreen.Cart) },
            navigatePaymentWeb = { setScreenTo(AppScreen.PaymentWeb(it)) },
            onAddAddressClick = { setScreenTo(AppScreen.AddAddress()) }
        )

        is AppScreen.PaymentWeb -> PaymentWebRoute(
            url = currentScreen.url,
            onPaymentSuccess = { replaceScreenTo(AppScreen.OrderDetail(it)) },
            onPaymentFailed = { replaceScreenTo(AppScreen.Cart) }
        )

        AppScreen.OrderItemHistory -> OrderItemHistoryRoute(
            innerPadding = innerPadding,
            navigateBack = { replaceScreenTo(AppScreen.Home) },
            navigateOrderItemDetail = { setScreenTo(AppScreen.OrderItemDetail(it)) }
        )

        is AppScreen.OrderItemDetail -> OrderItemDetailRoute(
            innerPadding = innerPadding,
            scope = scope,
            itemToken = currentScreen.itemToken,
            navigateBack = { replaceScreenTo(AppScreen.OrderItemHistory) },
            navigateOrderDetail = { setScreenTo(AppScreen.OrderDetail(it)) },
            navigateReturnOrderItem = { setScreenTo(AppScreen.ReturnOrderItem(it)) },
            navigateReviewOrderItem = { setScreenTo(AppScreen.ReviewOrderItem(it)) },
            navigateProductDetail = { setScreenTo(AppScreen.ProductDetail(it)) }
        )

        is AppScreen.OrderDetail -> OrderDetailRoute(
            innerPadding = innerPadding,
            scope = scope,
            context = context,
            orderToken = currentScreen.orderToken,
            navigateBack = { replaceScreenTo(AppScreen.OrderItemHistory) },
            navigateCancelOrder = { setScreenTo(AppScreen.CancelOrder(it)) }
        )

        is AppScreen.CancelOrder -> CancelOrderRoute(
            innerPadding = innerPadding,
            scope = scope,
            orderToken = currentScreen.orderToken,
            navigateBack = { replaceScreenTo(AppScreen.OrderDetail(currentScreen.orderToken)) },
            navigateOrderDetail = { replaceScreenTo(AppScreen.OrderDetail(it)) }
        )

        is AppScreen.ReturnOrderItem -> ReturnOrderItemRoute(
            innerPadding = innerPadding,
            scope = scope,
            itemToken = currentScreen.itemToken,
            navigateBack = { replaceScreenTo(AppScreen.OrderItemDetail(currentScreen.itemToken)) },
            navigateOrderItemDetail = { replaceScreenTo(AppScreen.OrderItemDetail(it)) }
        )

        is AppScreen.ReviewOrderItem -> ReviewOrderItemRoute(
            innerPadding = innerPadding,
            scope = scope,
            itemToken = currentScreen.itemToken,
            navigateBack = { replaceScreenTo(AppScreen.OrderItemDetail(currentScreen.itemToken)) },
            navigateOrderItemDetail = { replaceScreenTo(AppScreen.OrderItemDetail(it)) }
        )

        AppScreen.LoginContact -> LoginContactRoute(
            scope = scope,
            navigateHome = { replaceScreenTo(AppScreen.Home) },
            navigateLoginPassword = { setScreenTo(AppScreen.LoginPassword(it)) },
            navigateSignup = { replaceScreenTo(AppScreen.SignupContact) }
        )

        is AppScreen.LoginPassword -> LoginPasswordRoute(
            scope = scope,
            contact = currentScreen.contact,
            navigateHome = { replaceScreenTo(AppScreen.Home) },
            onAuthenticated = {
                setAuthenticated(true)
                replaceScreenTo(AppScreen.Home)
            },
            onUnauthenticated = { setAuthenticated(false) },
            navigateOtpLogin = {
                setScreenTo(
                    AppScreen.OtpVerify(
                        contact = currentScreen.contact,
                        purpose = OtpPurpose.LOGIN
                    )
                )
            }
        )

        AppScreen.SignupContact -> SignupContactRoute(
            scope = scope,
            navigateHome = { replaceScreenTo(AppScreen.Home) },
            navigateLoginPassword = { replaceScreenTo(AppScreen.LoginPassword(it)) },
            navigateSignupPassword = { setScreenTo(AppScreen.SignupPassword(it)) },
            navigateLogin = { replaceScreenTo(AppScreen.LoginContact) }
        )

        is AppScreen.SignupPassword -> SignupPasswordRoute(
            scope = scope,
            contact = currentScreen.contact,
            navigateHome = { replaceScreenTo(AppScreen.Home) },
            navigateLogin = { replaceScreenTo(AppScreen.LoginContact) },
            navigateOtp = { contact, purpose ->
                setScreenTo(AppScreen.OtpVerify(contact, purpose))
            }
        )

        is AppScreen.OtpVerify -> OtpVerifyRoute(
            scope = scope,
            contact = currentScreen.contact,
            purpose = currentScreen.purpose,
            navigateHome = { replaceScreenTo(AppScreen.Home) },
            onVerified = {
                when (currentScreen.purpose) {
                    OtpPurpose.LOGIN,
                    OtpPurpose.SIGNUP -> {
                        setAuthenticated(true)
                        replaceScreenTo(AppScreen.Home)
                    }

                    OtpPurpose.CHANGE_PHONE,
                    OtpPurpose.CHANGE_EMAIL -> {
                        replaceScreenTo(AppScreen.Profile)
                    }
                }
            },
            onProfileUpdated = { setProfileResponse(it) }
        )

        AppScreen.Profile -> ProfileRoute(
            innerPadding = innerPadding,
            scope = scope,
            profileResponse = profileResponse,
            profileError = profileError,
            navigateBack = { replaceScreenTo(AppScreen.Home) },
            navigateSavedAddresses = { setScreenTo(AppScreen.SavedAddresses) },
            navigateAddAddress = { setScreenTo(AppScreen.AddAddress()) },
            navigateChangeName = { setScreenTo(AppScreen.ChangeName) },
            navigateChangeEmail = { setScreenTo(AppScreen.ChangeEmail) },
            navigateChangePhone = { setScreenTo(AppScreen.ChangePhone) },
            navigateChangePassword = { setScreenTo(AppScreen.ChangePassword) },
            navigateCart = { setScreenTo(AppScreen.Cart) },
            navigateOrderItemHistory = { setScreenTo(AppScreen.OrderItemHistory) },
            navigateWallet = { setScreenTo(AppScreen.Wallet) },
            onLoggedOut = {
                setAuthenticated(false)
                setProfileResponse(null)
                replaceScreenTo(AppScreen.LoginContact)
            }
        )

        AppScreen.ChangeName -> ChangeNameRoute(
            innerPadding = innerPadding,
            scope = scope,
            navigateBack = { replaceScreenTo(AppScreen.Profile) },
            navigateProfile = { replaceScreenTo(AppScreen.Profile) },
            onProfileUpdated = { setProfileResponse(it) }
        )

        AppScreen.ChangeEmail -> ChangeContactRoute(
            innerPadding = innerPadding,
            scope = scope,
            type = ContactChangeType.EMAIL,
            navigateBack = { replaceScreenTo(AppScreen.Profile) },
            navigateOtp = { contact, purpose ->
                setScreenTo(AppScreen.OtpVerify(contact, purpose))
            }
        )

        AppScreen.ChangePhone -> ChangeContactRoute(
            innerPadding = innerPadding,
            scope = scope,
            type = ContactChangeType.PHONE,
            navigateBack = { replaceScreenTo(AppScreen.Profile) },
            navigateOtp = { contact, purpose ->
                setScreenTo(AppScreen.OtpVerify(contact, purpose))
            }
        )

        AppScreen.ChangePassword -> ChangePasswordRoute(
            innerPadding = innerPadding,
            scope = scope,
            navigateBack = { replaceScreenTo(AppScreen.Profile) },
            navigateProfile = { replaceScreenTo(AppScreen.Profile) }
        )

        is AppScreen.AddAddress -> AddAddressRoute(
            innerPadding = innerPadding,
            scope = scope,
            addressId = currentScreen.addressId,

            navigateBack = {
                replaceScreenTo(AppScreen.SavedAddresses)
            },

            navigateSavedAddresses = {
                replaceScreenTo(AppScreen.SavedAddresses)
            },

            navigateCheckout = {
                replaceScreenTo(AppScreen.Checkout)
            }
        )

        AppScreen.SavedAddresses -> SavedAddressesRoute(
            innerPadding = innerPadding,
            scope = scope,
            navigateBack = { replaceScreenTo(AppScreen.Profile) },
            navigateAddAddress = { setScreenTo(AppScreen.AddAddress()) },
            navigateEditAddress = { setScreenTo(AppScreen.AddAddress(it)) }
        )

        AppScreen.Wallet -> WalletRoute(
            innerPadding = innerPadding,
            scope = scope,
            navigateBack = { replaceScreenTo(AppScreen.Profile) }
        )
    }
}