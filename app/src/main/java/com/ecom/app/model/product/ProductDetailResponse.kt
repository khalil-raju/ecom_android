package com.ecom.app.model.product

import com.google.gson.annotations.SerializedName

data class ProductDetailResponse(
    val product: Product,

    @SerializedName("active_variant_id")
    val activeVariantId: Int?,

    @SerializedName("active_variant")
    val activeVariant: Variant?,

    @SerializedName("in_wishlist")
    val inWishlist: Boolean,

    @SerializedName("og_image")
    val ogImage: String?
)