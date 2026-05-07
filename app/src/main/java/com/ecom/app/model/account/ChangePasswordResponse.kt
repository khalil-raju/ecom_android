package com.ecom.app.model.account

import com.google.gson.annotations.SerializedName

data class ChangePasswordResponse(
    val success: Boolean,

    val heading: String? = null,

    @SerializedName("next_step")
    val nextStep: String? = null,

    @SerializedName("error_msg")
    val errorMsg: String? = null
)