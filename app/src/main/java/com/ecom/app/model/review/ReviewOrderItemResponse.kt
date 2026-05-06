package com.ecom.app.model.review

import com.ecom.app.model.order.OrderItem
import com.google.gson.annotations.SerializedName

data class ReviewOrderItemResponse(
    val success: Boolean,

    val item: OrderItem? = null,

    @SerializedName("already_reviewed")
    val alreadyReviewed: Boolean = false,

    val review: ReviewData? = null,

    @SerializedName("next_step")
    val nextStep: String? = null,

    @SerializedName("item_token")
    val itemToken: String? = null,

    val error: String? = null
)

data class ReviewData(
    val rating: Int,

    val review: String?
)