package com.ecom.app.model

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    val success: Boolean,
    val user: ProfileUser
)

data class ProfileUser(
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