package com.ecom.app.model.account

import com.google.gson.annotations.SerializedName

data class PinCodeResponse(
    val success: Boolean,

    val state: String? = null,

    val city: String? = null,

    @SerializedName("office_type")
    val officeType: String? = null,

    val error: String? = null
)
