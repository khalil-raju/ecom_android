package com.ecom.app.network

import com.ecom.app.model.account.AddressFormResponse
import com.ecom.app.model.product.ProductListResponse
import com.ecom.app.model.product.SearchSuggestionResponse
import com.ecom.app.model.product.ProductDetailResponse
import com.ecom.app.model.basket.CartCountResponse
import com.ecom.app.model.account.AuthStepResponse
import com.ecom.app.model.account.PinCodeResponse
import com.ecom.app.model.account.ProfileResponse
import com.ecom.app.model.basket.BasketResponse
import com.ecom.app.model.order.CancelOrderResponse
import com.ecom.app.model.order.CheckoutResponse
import com.ecom.app.model.order.InitiateOrderResponse
import com.ecom.app.model.order.OrderDetailResponse
import com.ecom.app.model.order.OrderItemDetailResponse
import com.ecom.app.model.order.OrderItemHistoryResponse
import com.ecom.app.model.order.ReturnOrderItemResponse
import com.ecom.app.model.review.ReviewOrderItemResponse

import retrofit2.http.Path
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.POST
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Field
import retrofit2.http.Header


interface ApiService {

    @FormUrlEncoded
    @POST("accounts/login/")
    suspend fun loginContact(
        @Query("format") format: String = "json",
        @Header("X-CSRFToken") csrfToken: String,
        @Field("contact") contact: String
    ): AuthStepResponse

    @FormUrlEncoded
    @POST("accounts/login/password/")
    suspend fun loginPassword(
        @Query("format") format: String = "json",
        @Header("X-CSRFToken") csrfToken: String,
        @Field("password") password: String
    ): AuthStepResponse

    @POST("accounts/logout/")
    suspend fun logout(
        @Query("format") format: String = "json"
    ): AuthStepResponse

    @FormUrlEncoded
    @POST("accounts/login/otp/")
    suspend fun loginOtp(
        @Header("X-CSRFToken")
        csrfToken: String,

        @Field("otp")
        otp: String,

        @Query("format")
        format: String = "json"
    ): AuthStepResponse

    @GET("accounts/login/otp/")
    suspend fun resendLoginOtp(
        @Query("resend")
        resend: String = "1",

        @Query("format")
        format: String = "json"
    ): AuthStepResponse

    @FormUrlEncoded
    @POST("accounts/signup/otp/")
    suspend fun signupOtp(
        @Header("X-CSRFToken")
        csrfToken: String,

        @Field("otp")
        otp: String,

        @Query("format")
        format: String = "json"
    ): AuthStepResponse

    @GET("accounts/signup/otp/")
    suspend fun resendSignupOtp(
        @Query("resend")
        resend: String = "1",

        @Query("format")
        format: String = "json"
    ): AuthStepResponse

    @FormUrlEncoded
    @POST("accounts/verify/contact/otp")
    suspend fun verifyContactOtp(
        @Header("X-CSRFToken")
        csrfToken: String,

        @Field("otp")
        otp: String,

        @Query("format")
        format: String = "json"
    ): AuthStepResponse

    @GET("accounts/verify/contact/otp")
    suspend fun resendVerifyContactOtp(
        @Query("resend")
        resend: String = "1",

        @Query("format")
        format: String = "json"
    ): AuthStepResponse

    @GET("accounts/profile/")
    suspend fun getProfile(
        @Query("format") format: String = "json"
    ): ProfileResponse

    @GET("/")
    suspend fun getProducts(
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): ProductListResponse

    @GET("products/search/autocomplete/")
    suspend fun getSearchSuggestions(
        @Query("q") query: String
    ): SearchSuggestionResponse

    @GET("{variantId}/{slug}/")
    suspend fun getProductDetail(
        @Path("variantId") variantId: Int,
        @Path("slug") slug: String,
        @Query("format") format: String = "json"
    ): ProductDetailResponse

    @FormUrlEncoded
    @POST("baskets/cart/update/")
    suspend fun addToCart(
        @Header("X-CSRFToken") csrfToken: String,
        @Field("product_id") productId: Int,
        @Field("variant_id") variantId: Int,
        @Field("quantity") quantity: Int = 1,
        @Field("minimal") minimal: String = "1"
    ): CartCountResponse

    @GET("baskets/")
    suspend fun getBasket(
        @Query("format") format: String = "json"
    ): BasketResponse

    @FormUrlEncoded
    @POST("baskets/cart/update/")
    suspend fun updateCartQuantity(
        @Query("format") format: String = "json",
        @Header("X-CSRFToken") csrfToken: String,
        @Field("product_id") productId: Int,
        @Field("variant_id") variantId: Int,
        @Field("quantity") quantity: Int,
        @Field("minimal") minimal: String = "0"
    ): BasketResponse

    @GET("orders/checkout/")
    suspend fun getCheckout(
        @Query("format") format: String = "json"
    ): CheckoutResponse

    @FormUrlEncoded
    @POST("orders/initiate/order/{orderToken}/")
    suspend fun initiateOrder(
        @Path("orderToken") orderToken: String,
        @Query("format") format: String = "json",
        @Header("X-CSRFToken") csrfToken: String,
        @Field("shipping_address_id") shippingAddressId: Int?,
        @Field("billing_address_id") billingAddressId: Int?,
        @Field("use_wallet") useWallet: String,
        @Field("payment_method") paymentMethod: String
    ): InitiateOrderResponse

    @GET("orders/order/details/{orderToken}/")
    suspend fun getOrderDetail(
        @Path("orderToken") orderToken: String,
        @Query("format") format: String = "json"
    ): OrderDetailResponse

    @GET("orders/order/item/history/")
    suspend fun getOrderItemHistory(
        @Query("format") format: String = "json"
    ): OrderItemHistoryResponse

    @GET("orders/order/item/details/{itemToken}/")
    suspend fun getOrderItemDetail(
        @Path("itemToken") itemToken: String,
        @Query("format") format: String = "json"
    ): OrderItemDetailResponse

    @GET("orders/cancel/order/{order_token}/")
    suspend fun getCancelOrder(
        @Path("order_token") orderToken: String,
        @Query("format") format: String = "json"
    ): CancelOrderResponse

    @FormUrlEncoded
    @POST("orders/cancel/order/{order_token}/")
    suspend fun submitCancelOrder(
        @Path("order_token") orderToken: String,

        @Field("cancel_reason")
        cancelReason: String,

        @Field("refund_account")
        refundAccount: String? = null,

        @Header("X-CSRFToken")
        csrfToken: String,

        @Query("format") format: String = "json"
    ): CancelOrderResponse

    @GET("orders/return/order/item/{itemToken}/")
    suspend fun getReturnOrderItem(
        @Path("itemToken") itemToken: String,
        @Query("format") format: String = "json"
    ): ReturnOrderItemResponse

    @FormUrlEncoded
    @POST("orders/return/order/item/{itemToken}/")
    suspend fun submitReturnOrderItem(
        @Path("itemToken") itemToken: String,

        @Field("return_reason")
        returnReason: String,

        @Field("refund_account")
        refundAccount: String? = null,

        @Header("X-CSRFToken")
        csrfToken: String,

        @Query("format")
        format: String = "json"
    ): ReturnOrderItemResponse

    @GET("reviews/rate/{itemToken}/")
    suspend fun getReviewOrderItem(
        @Path("itemToken") itemToken: String,
        @Query("format") format: String = "json"
    ): ReviewOrderItemResponse

    @FormUrlEncoded
    @POST("reviews/rate/{itemToken}/")
    suspend fun submitReviewOrderItem(
        @Path("itemToken") itemToken: String,
        @Field("rating")
        rating: Int,
        @Field("review")
        review: String? = null,
        @Header("X-CSRFToken")
        csrfToken: String,
        @Query("format") format: String = "json"
    ): ReviewOrderItemResponse

    // ---------------- Address ----------------

    @GET("accounts/add-address/")
    suspend fun getAddAddress(
        @Query("format") format: String = "json",
        @Query("from") from: String? = null
    ): AddressFormResponse


    @FormUrlEncoded
    @POST("accounts/add-address/")
    suspend fun submitAddAddress(
        @Header("X-CSRFToken")
        csrfToken: String,

        @Field("full_name")
        fullName: String,

        @Field("phone")
        phone: String,

        @Field("line_1")
        line1: String,

        @Field("line_2")
        line2: String? = "",

        @Field("postal_code")
        postalCode: String,

        @Field("city")
        city: String,

        @Field("state")
        state: String,

        @Field("country")
        country: String = "India",

        @Field("label")
        label: String = "Home",

        @Field("is_default")
        isDefault: String? = null,

        @Field("consent_pp_tc")
        consentPpTc: String? = null,

        @Query("format")
        format: String = "json",

        @Query("from")
        from: String? = null
    ): AddressFormResponse


    @GET("accounts/edit-address/{addressId}/")
    suspend fun getEditAddress(
        @Path("addressId")
        addressId: Int,

        @Query("format")
        format: String = "json"
    ): AddressFormResponse


    @FormUrlEncoded
    @POST("accounts/edit-address/{addressId}/")
    suspend fun submitEditAddress(
        @Path("addressId")
        addressId: Int,

        @Header("X-CSRFToken")
        csrfToken: String,

        @Field("full_name")
        fullName: String,

        @Field("phone")
        phone: String,

        @Field("line_1")
        line1: String,

        @Field("line_2")
        line2: String? = "",

        @Field("postal_code")
        postalCode: String,

        @Field("city")
        city: String,

        @Field("state")
        state: String,

        @Field("country")
        country: String = "India",

        @Field("label")
        label: String = "Home",

        @Field("is_default")
        isDefault: String? = null,

        @Query("format")
        format: String = "json"
    ): AddressFormResponse


    @GET("accounts/saved-address/")
    suspend fun getSavedAddresses(
        @Query("format")
        format: String = "json"
    ): AddressFormResponse


    @POST("accounts/delete-address/{addressId}/")
    suspend fun deleteAddress(
        @Path("addressId")
        addressId: Int,

        @Header("X-CSRFToken")
        csrfToken: String,

        @Query("format")
        format: String = "json"
    ): AddressFormResponse


    @GET("accounts/get/pincode/details/")
    suspend fun getPinCodeDetails(
        @Query("pincode")
        pincode: String
    ): PinCodeResponse

}