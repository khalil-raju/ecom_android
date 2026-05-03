package com.ecom.app.model

import com.google.gson.annotations.SerializedName

data class ProductDetailResponse(
    val product: ProductDetail,
    @SerializedName("active_variant_id")
    val activeVariantId: Int?,
    @SerializedName("active_variant")
    val activeVariant: ProductVariantDetail?,
    @SerializedName("in_wishlist")
    val inWishlist: Boolean,
    @SerializedName("og_image")
    val ogImage: String?
)

data class ProductDetail(
    val id: Int,
    val name: String,
    val description: String?,
    val brand: String?,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("is_featured")
    val isFeatured: Boolean,
    @SerializedName("is_new")
    val isNew: Boolean,
    val variants: List<ProductVariantDetail>
)

data class ProductVariantDetail(
    val id: Int,
    val color: String?,
    val size: String?,
    val price: String?,
    val stock: Int,
    val images: List<String?>,
    @SerializedName("is_default")
    val isDefault: Boolean
)
