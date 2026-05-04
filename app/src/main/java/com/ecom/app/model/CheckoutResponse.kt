package com.ecom.app.model

import com.google.gson.annotations.SerializedName

data class CheckoutResponse(
    val success: Boolean,

    @SerializedName("next_step")
    val nextStep: String?,

    @SerializedName("all_address")
    val allAddress: List<Address>,

    @SerializedName("selected_address")
    val selectedAddress: Address?,

    @SerializedName("wallet_balance")
    val walletBalance: Double?,

    val order: Order?,

    @SerializedName("order_items")
    val orderItems: List<OrderItem>
)
