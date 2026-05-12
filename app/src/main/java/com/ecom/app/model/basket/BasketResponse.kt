package com.ecom.app.model.basket

import com.google.gson.annotations.SerializedName

data class BasketItem(
    @SerializedName("product_id")
    val productId: Int,

    @SerializedName("variant_id")
    val variantId: Int?,

    val name: String,
    val slug: String,
    val size: String,
    val color: String,

    val price: Double?,

    val quantity: Int,
    val stock: Int,

    val image: String?,

    val subtotal: Double
)

data class BasketCountsResponse(
    @SerializedName("cart_count")
    val cartCount: Int,

    @SerializedName("saved_count")
    val savedCount: Int,

    @SerializedName("wishlist_count")
    val wishlistCount: Int
)

data class CartDetailResponse(
    @SerializedName("cart_count")
    val cartCount: Int,

    @SerializedName("cart_total")
    val cartTotal: Double,

    @SerializedName("cart_items")
    val cartItems: List<BasketItem>,

    @SerializedName("error_msg")
    val errorMsg: String?
)

data class WishlistDetailResponse(
    @SerializedName("wishlist_count")
    val wishlistCount: Int,

    @SerializedName("wishlist_total")
    val wishlistTotal: Double,

    @SerializedName("wishlist_items")
    val wishlistItems: List<BasketItem>,

    @SerializedName("wishlist_ids")
    val wishlistIds: List<Int>,

    @SerializedName("error_msg")
    val errorMsg: String?
)

data class UpdateToCartResponse(
    val success: Boolean,

    @SerializedName("cart_count")
    val cartCount: Int,
)

data class RemoveFromCartResponse(
    val success: Boolean,

    @SerializedName("cart_count")
    val cartCount: Int,
)

data class AddToWishlistResponse(
    val success: Boolean,

    @SerializedName("wishlist_count")
    val wishlistCount: Int,
)

data class RemoveFromWishlistResponse(
    val success: Boolean,

    @SerializedName("wishlist_count")
    val wishlistCount: Int,
)
