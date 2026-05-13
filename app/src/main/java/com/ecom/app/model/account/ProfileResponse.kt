package com.ecom.app.model.account

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val username: String,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    val email: String,
    val phone: String,

    @SerializedName("is_email_verified")
    val isEmailVerified: Boolean,

    @SerializedName("is_phone_verified")
    val isPhoneVerified: Boolean
)

data class UserProfileResponse(
    val success: Boolean,
    val authenticated: Boolean,
    val user: User?
)

data class ChangeContactOtpResponse(
    val success: Boolean,
    val authenticated: Boolean,

    @SerializedName("next_step")
    val nextStep: String,

    val contact: String,

    @SerializedName("contact_type")
    val contactType: String,

    val error: String?
)

data class ChangeContactResponse(
    val success: Boolean,
    val contact: String,

    @SerializedName("contact_type")
    val contactType: String,

    @SerializedName("next_step")
    val nextStep: String?,

    @SerializedName("error_msg")
    val errorMsg: String?
)

data class ChangeNameResponse(
    val success: Boolean,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    @SerializedName("next_step")
    val nextStep: String?,

    @SerializedName("error_msg")
    val errorMsg: String?
)

data class ChangePasswordResponse(
    val success: Boolean,

    @SerializedName("next_step")
    val nextStep: String?,

    @SerializedName("error_msg")
    val errorMsg: String?
)