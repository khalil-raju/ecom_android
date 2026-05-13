package com.ecom.app.model.account

import com.google.gson.annotations.SerializedName

data class Address(
    val id: Int?,

    @SerializedName("full_name")
    val fullName: String,

    val phone: String,

    @SerializedName("line_1")
    val line1: String,

    @SerializedName("line_2")
    val line2: String,

    val city: String,

    val state: String,

    @SerializedName("postal_code")
    val postalCode: String,

    val country: String,

    val label: String,

    @SerializedName("is_default")
    val isDefault: Boolean,
)

data class RegionState(
    val id: Int,

    val name: String,
)

data class AddAddressResponse(
    val success: Boolean,

    @SerializedName("is_guest")
    val isGuest: Boolean,

    @SerializedName("region_state")
    val regionStates: List<RegionState>?,

    @SerializedName("next_step")
    val nextStep: String?,

    val address: Address?,

    @SerializedName("error_msg")
    val errorMsg: Map<String, String>?,
)

data class PincodeDetailsResponse(
    val success: Boolean,

    val state: String,

    val city: String,

    @SerializedName("office_type")
    val officeType: String,

    val error: String,
)

data class SavedAddressResponse(
    val success: Boolean,

    val addresses: List<Address>,
)

data class DeleteAddressResponse(
    val success: Boolean,

    @SerializedName("next_step")
    val nextStep: String,
)
