package com.ecom.app.model

import com.google.gson.annotations.SerializedName
data class OrderItem(
    val id: Int,

    @SerializedName("product_name")
    val productName: String,

    @SerializedName("variant_name")
    val variantName: String,

    @SerializedName("variant_id")
    val variantId: Int,

    @SerializedName("variant_slug")
    val variantSlug: String,

    @SerializedName("variant_size")
    val variantSize: String?,

    @SerializedName("variant_image_url")
    val variantImageUrl: String?,

    val price: Double,
    val quantity: Int,

    @SerializedName("total_amt")
    val totalAmt: Double
)