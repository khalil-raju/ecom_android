package com.ecom.app.model.account

import com.google.gson.annotations.SerializedName

data class ChangeNameResponse(
    val success: Boolean,

    @SerializedName("first_name")
    val firstName: String = "",

    @SerializedName("last_name")
    val lastName: String = "",

    @SerializedName("error_msg")
    val errorMsg: String? = null,

    @SerializedName("next_step")
    val nextStep: String? = null
)