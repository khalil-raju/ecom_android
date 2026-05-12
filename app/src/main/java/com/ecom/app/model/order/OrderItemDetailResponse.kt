package com.ecom.app.model.order

import com.google.gson.annotations.SerializedName

data class OrderItemDetailResponse(
    val success: Boolean,

    @SerializedName("order_item")
    val orderItem: OrderItem
)