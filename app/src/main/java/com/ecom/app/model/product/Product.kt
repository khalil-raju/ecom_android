package com.ecom.app.model.product

import com.google.gson.annotations.SerializedName

data class ProductLite(
    val id: Int,
    val name: String,

    @SerializedName("variant_id")
    val variantId: Int,

    val price: Double,
    val slug: String,
    val image: String?
)

data class Product(
    val id: Int,
    val name: String,
    val description: String?,
    val brand: String,

    @SerializedName("is_active")
    val isActive: Boolean,

    @SerializedName("is_featured")
    val isFeatured: Boolean,

    @SerializedName("is_new")
    val isNew: Boolean,

    val variants: List<Variant>
)

data class Variant(
    val id: Int,
    val name: String,
    val color: String,
    val size: String,
    val price: Double,
    val stock: Int,
    val images: List<String?>,

    @SerializedName("is_default")
    val isDefault: Boolean
)
