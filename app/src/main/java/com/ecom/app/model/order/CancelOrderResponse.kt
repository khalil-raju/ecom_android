// model/CancelOrderResponse.kt
package com.ecom.app.model.order

import com.google.gson.annotations.SerializedName

data class CancelOrderResponse(

    val success: Boolean,

    val order: CancelOrder?,

    @SerializedName("order_items")
    val orderItems: List<OrderItem> = emptyList(),

    @SerializedName("refund_required")
    val refundRequired: Boolean = false,

    val error: String? = null,

    @SerializedName("next_step")
    val nextStep: String? = null,

    @SerializedName("order_token")
    val orderToken: String? = null
)