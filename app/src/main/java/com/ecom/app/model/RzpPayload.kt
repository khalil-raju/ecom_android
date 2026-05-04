package com.ecom.app.model

import com.google.gson.annotations.SerializedName

data class RzpPayload(

    val key: String,

    @SerializedName("rzp_order_id")
    val rzpOrderId: String,

    @SerializedName("amount_minor")
    val amountMinor: Int,

    val currency: String,

    @SerializedName("store_name")
    val storeName: String,

    val description: String,

    @SerializedName("user_email")
    val userEmail: String,

    @SerializedName("user_phone")
    val userPhone: String,

    @SerializedName("verify_url")
    val verifyUrl: String,

    @SerializedName("finalize_url")
    val finalizeUrl: String
)