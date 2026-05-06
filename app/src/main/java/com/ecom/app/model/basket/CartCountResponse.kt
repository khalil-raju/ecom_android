package com.ecom.app.model.basket

import com.google.gson.annotations.SerializedName

data class CartCountResponse(
    val success: Boolean,
    @SerializedName("cart_count")
    val cartCount: Int
)