package com.ecom.app.model

import com.google.gson.annotations.SerializedName

data class ProductListResponse(
    val products: List<Product>,
    @SerializedName("has_more")
    val hasMore: Boolean,
    val limit: Int,
    val offset: Int,
    @SerializedName("category_view")
    val categoryView: String?
)
