package com.ecom.app.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Int,
    val name: String,
    @SerializedName("variant_id")
    val variantId: Int,
    val price: String?,
    val slug: String,
    val image: String?
)