package com.ecom.app.model.order

import com.google.gson.annotations.SerializedName

data class OrderItemHistoryResponse(
    val success: Boolean,

    val limit: Int? = null,

    val offset: Int? = null,

    @SerializedName("has_more")
    val hasMore: Boolean = false,

    @SerializedName("order_items")
    val orderItems: List<OrderItem> = emptyList()
)