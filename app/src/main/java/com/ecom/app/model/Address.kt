package com.ecom.app.model

import com.google.gson.annotations.SerializedName

data class Address(
    val id: Int,
    val label: String,
    @SerializedName("full_name")
    val fullName: String,
    val phone: String,

    @SerializedName("address_line_1")
    val addressLine1: String,

    @SerializedName("address_line_2")
    val addressLine2: String?,

    val city: String,
    val state: String,
    val pincode: String,
    val country: String,

    @SerializedName("is_default")
    val isDefault: Boolean
)
