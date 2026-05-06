// model/ReturnOrderItemResponse.kt
package com.ecom.app.model.order

import com.google.gson.annotations.SerializedName

data class ReturnOrderItemResponse(
    val success: Boolean,

    val order: ReturnOrder? = null,

    @SerializedName("order_item")
    val orderItem: OrderItem? = null,

    @SerializedName("refund_required")
    val refundRequired: Boolean = false,

    val error: String? = null,

    @SerializedName("next_step")
    val nextStep: String? = null,

    @SerializedName("item_token")
    val itemToken: String? = null
)

data class ReturnOrder(
    @SerializedName("order_token")
    val orderToken: String,

    @SerializedName("order_id")
    val orderId: String,

    val state: String,

    @SerializedName("total_paid_amt")
    val totalPaidAmt: Double,

    @SerializedName("online_paid_amt")
    val onlinePaidAmt: Double,

    @SerializedName("wallet_paid_amt")
    val walletPaidAmt: Double
)