package com.ecom.app.network

import com.ecom.app.model.ProductListResponse
import com.ecom.app.model.SearchSuggestionResponse
import com.ecom.app.model.ProductDetailResponse
import com.ecom.app.model.CartCountResponse
import com.ecom.app.model.AuthStepResponse
import com.ecom.app.model.ProfileResponse
import com.ecom.app.model.BasketResponse
import com.ecom.app.model.CheckoutResponse
import com.ecom.app.model.InitiateOrderResponse
import com.ecom.app.model.RzpPaymentResponse

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

    @GET("payments/initiate/rzp/payment/{orderToken}/")
    suspend fun initiateRzpPayment(
        @Path("orderToken") orderToken: String,
        @Query("format") format: String = "json"
    ): RzpPaymentResponse
}