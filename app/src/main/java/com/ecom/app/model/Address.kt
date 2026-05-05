package com.ecom.app.model

import com.google.gson.annotations.SerializedName

data class Address(
    val id: Int,
    val label: String,
    @SerializedName("full_name")
    val fullName: String,
    val phone: String,

    @SerializedName("line_1")
    val addressLine1: String,

    @SerializedName("line_2")
    val addressLine2: String?,

    val city: String,
    val state: String,

    @SerializedName("postal_code")
    val postalCode: String,

    val country: String,

    @SerializedName("is_default")
    val isDefault: Boolean
)
