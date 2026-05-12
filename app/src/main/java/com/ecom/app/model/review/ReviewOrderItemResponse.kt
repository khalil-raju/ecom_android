package com.ecom.app.model.review

import com.ecom.app.model.order.OrderItem
import com.google.gson.annotations.SerializedName

data class ReviewOrderItemResponse(
    val success: Boolean,

    @SerializedName("error_msg")
    val errorMsg: String?,

    @SerializedName("next_step")
    val nextStep: String,

    @SerializedName("order_item")
    val orderItem: OrderItem?,

    val review: OrderItemReview?
)

data class OrderItemReview(
    val rating: Int,
    val review: String,

    @SerializedName("created_at")
    val createdAt: String?
)