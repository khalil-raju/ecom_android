package com.ecom.app.model

import com.google.gson.annotations.SerializedName

data class OrderItemHistoryResponse(
    val success: Boolean,

    @SerializedName("order_items")
    val orderItems: List<OrderItem>
)