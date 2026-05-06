package com.ecom.app.model.account

import com.google.gson.annotations.SerializedName

data class ChangeContactResponse(
    val success: Boolean,
    val field: String? = null,
    val value: String? = null,
    val heading: String? = null,
    val contact: String? = null,

    @SerializedName("contact_type")
    val contactType: String? = null,

    @SerializedName("next_step")
    val nextStep: String? = null,

    @SerializedName("error_msg")
    val errorMsg: String? = null
)