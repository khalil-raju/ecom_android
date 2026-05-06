package com.ecom.app.model.account

import com.google.gson.annotations.SerializedName

data class Address(
    val id: Int? = null,

    @SerializedName("full_name")
    val fullName: String = "",

    val phone: String = "",

    @SerializedName("line_1")
    val line1: String = "",

    @SerializedName("line_2")
    val line2: String = "",

    val city: String = "",

    val state: String = "",

    @SerializedName("postal_code")
    val postalCode: String = "",

    val country: String = "India",

    val label: String = "Home",

    @SerializedName("is_default")
    val isDefault: Boolean = false
)