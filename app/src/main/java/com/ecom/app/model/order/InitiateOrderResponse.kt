package com.ecom.app.model.order

import com.google.gson.annotations.SerializedName

data class InitiateOrderResponse(
    val success: Boolean,

    @SerializedName("payment_type")
    val paymentType: String?,

    @SerializedName("next_step")
    val nextStep: String?,

    @SerializedName("order_token")
    val orderToken: String?,

    @SerializedName("error_msg")
    val errorMsg: String?
)