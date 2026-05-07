package com.ecom.app.network

import com.ecom.app.model.account.AddressFormResponse
import com.ecom.app.model.product.ProductListResponse
import com.ecom.app.model.product.SearchSuggestionResponse
import com.ecom.app.model.product.ProductDetailResponse
import com.ecom.app.model.basket.CartCountResponse
import com.ecom.app.model.account.AuthStepResponse
import com.ecom.app.model.account.ChangeContactResponse
import com.ecom.app.model.account.ChangeNameResponse
import com.ecom.app.model.account.ChangePasswordResponse
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
import com.ecom.app.model.product.CategoryMenuResponse
import com.ecom.app.model.review.ReviewOrderItemResponse

import retrofit2.http.Path
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.POST
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Field
import retrofit2.http.Header


interface ApiService {
    // ---------------- Login ----------------
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

    // ---------------- Logout ----------------
    @POST("accounts/logout/")
    suspend fun logout(
        @Query("format") format: String = "json"
    ): AuthStepResponse

    // ---------------- Signup ----------------
    @FormUrlEncoded
    @POST("accounts/signup/")
    suspend fun signupContact(
        @Header("X-CSRFToken")
        csrfToken: String,

        @Field("contact")
        contact: String,

        @Field("consent_pp_tc")
        consentPpTc: String? = null,

        @Query("format")
        format: String = "json"
    ): AuthStepResponse

    @GET("accounts/signup/password/")
    suspend fun getSignupPassword(
        @Query("format")
        format: String = "json"
    ): AuthStepResponse

    @FormUrlEncoded
    @POST("accounts/signup/password/")
    suspend fun signupPassword(
        @Header("X-CSRFToken")
        csrfToken: String,

        @Field("password")
        password: String,

        @Field("confirm")
        confirm: String,

        @Query("format")
        format: String = "json"
    ): AuthStepResponse

    // ---------------- OTP: Login ----------------
    @GET("accounts/login/otp/")
    suspend fun startLoginOtp(
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
    @POST("accounts/login/otp/")
    suspend fun submitLoginOtp(
        @Header("X-CSRFToken")
        csrfToken: String,

        @Field("otp")
        otp: String,

        @Query("format")
        format: String = "json"
    ): AuthStepResponse


    // ---------------- OTP: Signup ----------------
    @GET("accounts/signup/otp/")
    suspend fun startSignupOtp(
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
    @POST("accounts/signup/otp/")
    suspend fun submitSignupOtp(
        @Header("X-CSRFToken")
        csrfToken: String,

        @Field("otp")
        otp: String,

        @Query("format")
        format: String = "json"
    ): AuthStepResponse


    // ---------------- OTP: Verify Contact ----------------
    @GET("accounts/verify/contact/otp")
    suspend fun startVerifyContactOtp(
        @Query("contact_type")
        contactType: String,

        @Query("format")
        format: String = "json"
    ): AuthStepResponse

    @GET("accounts/verify/contact/otp")
    suspend fun resendVerifyContactOtp(
        @Query("contact_type")
        contactType: String,

        @Query("resend")
        resend: String = "1",

        @Query("format")
        format: String = "json"
    ): AuthStepResponse

    @FormUrlEncoded
    @POST("accounts/verify/contact/otp")
    suspend fun submitVerifyContactOtp(
        @Header("X-CSRFToken")
        csrfToken: String,

        @Field("otp")
        otp: String,

        @Query("format")
        format: String = "json"
    ): AuthStepResponse

    // ---------------- OTP: Change Contact ----------------
    @GET("accounts/change/contact/otp")
    suspend fun startChangeContactOtp(
        @Query("format") format: String = "json"
    ): AuthStepResponse

    @GET("accounts/change/contact/otp")
    suspend fun resendChangeContactOtp(
        @Query("resend") resend: String = "1",
        @Query("format") format: String = "json"
    ): AuthStepResponse

    @FormUrlEncoded
    @POST("accounts/change/contact/otp")
    suspend fun submitChangeContactOtp(
        @Header("X-CSRFToken") csrfToken: String,
        @Field("otp") otp: String,
        @Query("format") format: String = "json"
    ): AuthStepResponse

    // ---------------- Change Password ----------------
    @GET("accounts/change_password/")
    suspend fun getChangePassword(
        @Query("format") format: String = "json"
    ): ChangePasswordResponse

    @FormUrlEncoded
    @POST("accounts/change_password/")
    suspend fun submitChangePassword(
        @Header("X-CSRFToken") csrfToken: String,

        @Field("new_password") newPassword: String,

        @Field("confirm_password") confirmPassword: String,

        @Query("format") format: String = "json"
    ): ChangePasswordResponse

    // ---------------- Profile ----------------
    @GET("accounts/profile/")
    suspend fun getProfile(
        @Query("format") format: String = "json"
    ): ProfileResponse

    @GET("accounts/change_name/")
    suspend fun getChangeName(
        @Query("format")
        format: String = "json"
    ): ChangeNameResponse

    // ---------------- Change Name ----------------
    @FormUrlEncoded
    @POST("accounts/change_name/")
    suspend fun submitChangeName(
        @Header("X-CSRFToken")
        csrfToken: String,

        @Field("new_firstname")
        newFirstname: String,

        @Field("new_lastname")
        newLastname: String,

        @Query("format")
        format: String = "json"
    ): ChangeNameResponse

    // ---------------- Change Email ----------------
    @GET("accounts/change_email/")
    suspend fun getChangeEmail(
        @Query("format") format: String = "json"
    ): ChangeContactResponse

    @FormUrlEncoded
    @POST("accounts/change_email/")
    suspend fun submitChangeEmail(
        @Header("X-CSRFToken") csrfToken: String,
        @Field("new_email") newEmail: String,
        @Query("format") format: String = "json"
    ): ChangeContactResponse

    // ---------------- Change Phone ----------------
    @GET("accounts/change_phone/")
    suspend fun getChangePhone(
        @Query("format") format: String = "json"
    ): ChangeContactResponse

    @FormUrlEncoded
    @POST("accounts/change_phone/")
    suspend fun submitChangePhone(
        @Header("X-CSRFToken") csrfToken: String,
        @Field("new_phone") newPhone: String,
        @Query("format") format: String = "json"
    ): ChangeContactResponse

    // ---------------- Product ----------------
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

    @GET("products/category/menu/")
    suspend fun getCategoryMenu(
        @Query("format") format: String = "json"
    ): CategoryMenuResponse

    @GET("products/category/{parentSlug}/")
    suspend fun getProductsByParentCategory(
        @Path("parentSlug") parentSlug: String,
        @Query("format") format: String = "json"
    ): ProductListResponse

    @GET("products/category/{parentSlug}/{childSlug}/")
    suspend fun getProductsByChildCategory(
        @Path("parentSlug") parentSlug: String,
        @Path("childSlug") childSlug: String,
        @Query("format") format: String = "json"
    ): ProductListResponse

    // ---------------- Basket ----------------
    @GET("baskets/")
    suspend fun getBasket(
        @Query("format") format: String = "json"
    ): BasketResponse

    @FormUrlEncoded
    @POST("baskets/cart/update/")
    suspend fun addToCart(
        @Header("X-CSRFToken") csrfToken: String,
        @Field("product_id") productId: Int,
        @Field("variant_id") variantId: Int,
        @Field("quantity") quantity: Int = 1,
        @Field("minimal") minimal: String = "1"
    ): CartCountResponse

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

    // ---------------- Order ----------------
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

    // ---------------- Review ----------------
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