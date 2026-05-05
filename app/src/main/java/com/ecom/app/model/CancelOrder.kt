// model/CancelOrder.kt
package com.ecom.app.model

import com.google.gson.annotations.SerializedName

data class CancelOrder(

    @SerializedName("order_token")
    val orderToken: String,

    @SerializedName("order_id")
    val orderId: String,

    val state: String,

    @SerializedName("placed_at")
    val placedAt: String?,

    @SerializedName("total_amount")
    val totalAmount: Double,

    @SerializedName("can_user_cancel")
    val canUserCancel: Boolean,

    @SerializedName("total_paid_amt")
    val totalPaidAmt: Double,

    @SerializedName("online_paid_amt")
    val onlinePaidAmt: Double,

    @SerializedName("wallet_paid_amt")
    val walletPaidAmt: Double
)