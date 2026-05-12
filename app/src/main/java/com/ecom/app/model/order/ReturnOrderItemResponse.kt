package com.ecom.app.model.order

import com.ecom.app.model.payment.Payment
import com.google.gson.annotations.SerializedName

data class ReturnOrderItemResponse(
    val success: Boolean,

    @SerializedName("error_msg")
    val errorMsg: String?,

    @SerializedName("next_step")
    val nextStep: String,

    val order: Order?,

    @SerializedName("order_item")
    val orderItem: OrderItem?,

    @SerializedName("refund_required")
    val refundRequired: Boolean,

    val payment: Payment?
)