package com.ecom.app.model

import com.google.gson.annotations.SerializedName

data class RzpPaymentResponse(
    val success: Boolean,

    @SerializedName("payment_type")
    val paymentType: String,

    val razorpay: RzpPayload
)