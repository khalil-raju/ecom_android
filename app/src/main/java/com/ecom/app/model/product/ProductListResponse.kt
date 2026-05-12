package com.ecom.app.model.product

import com.google.gson.annotations.SerializedName

data class ProductListResponse(
    val products: List<ProductLite> = emptyList(),

    @SerializedName("has_more")
    val hasMore: Boolean = false,

    val limit: Int? = null,
    val offset: Int? = null,

    @SerializedName("category_view")
    val categoryView: String? = null
)