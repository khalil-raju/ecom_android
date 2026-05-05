package com.ecom.app.model

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("order_token")
    val orderToken: String,

    @SerializedName("order_id")
    val orderId: String,

    val state: String,

    @SerializedName("placed_at")
    val placedAt: String?,

    @SerializedName("cancelled_at")
    val cancelledAt: String?,

    @SerializedName("items_total_base_amt")
    val itemsTotalBaseAmt: Double,

    @SerializedName("items_total_gst_amt")
    val itemsTotalGstAmt: Double,

    @SerializedName("items_total_amt")
    val itemsTotalAmt: Double,

    @SerializedName("shipping_cost")
    val shippingCost: Double,

    @SerializedName("total_amount")
    val totalAmount: Double,

    @SerializedName("can_user_cancel")
    val canUserCancel: Boolean
)