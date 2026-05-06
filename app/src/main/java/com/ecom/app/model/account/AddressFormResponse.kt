package com.ecom.app.model.account

import com.google.gson.annotations.SerializedName

data class AddressFormResponse(
    val success: Boolean,

    @SerializedName("is_guest")
    val isGuest: Boolean = false,

    val address: Address? = null,

    val addresses: List<Address> = emptyList(),

    val states: List<RegionState> = emptyList(),

    val labels: List<String> = emptyList(),

    @SerializedName("error_msg")
    val errorMsg: Map<String, String> = emptyMap(),

    @SerializedName("next_step")
    val nextStep: String? = null
)