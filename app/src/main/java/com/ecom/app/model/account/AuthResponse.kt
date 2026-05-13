package com.ecom.app.model.account

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val success: Boolean,
    val authenticated: Boolean,

    @SerializedName("next_step")
    val nextStep: String?,

    val contact: String?,

    @SerializedName("contact_type")
    val contactType: String?,

    @SerializedName("error_msg")
    val errorMsg: String?,

    @SerializedName("from_checkout")
    val fromCheckout: Boolean,

    @SerializedName("login_attempts_left")
    val loginAttemptsLeft: Int?,

    @SerializedName("otp_remaining_time")
    val otpRemainingTime: Int?
)
