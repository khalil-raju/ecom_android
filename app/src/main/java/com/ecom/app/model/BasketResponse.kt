package com.ecom.app.model

import com.google.gson.annotations.SerializedName

data class BasketResponse(
    val success: Boolean,
    @SerializedName("cart_items")
    val cartItems: List<BasketItem>,
    @SerializedName("saved_items")
    val savedItems: List<BasketItem>,
    @SerializedName("wishlist_items")
    val wishlistItems: List<BasketItem>,
    @SerializedName("cart_total")
    val cartTotal: Double?,
    @SerializedName("cart_count")
    val cartCount: Int,
    @SerializedName("saved_count")
    val savedCount: Int,
    @SerializedName("wishlist_count")
    val wishlistCount: Int,
    @SerializedName("error_msg")
    val errorMsg: String?
)

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
    val subtotal: Double?
)