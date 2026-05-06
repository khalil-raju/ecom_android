package com.ecom.app.model.account

import com.google.gson.annotations.SerializedName

data class AuthStepResponse(
    val success: Boolean,

    val authenticated: Boolean?,

    @SerializedName("next_step")
    val nextStep: String?,

    val contact: String?,

    @SerializedName("from_checkout")
    val fromCheckout: Boolean?,

    val error: String?,

    @SerializedName("error_msg")
    val errorMsg: String?,

    @SerializedName("login_attempts_left")
    val loginAttemptsLeft: Int?
)