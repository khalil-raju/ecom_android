package com.ecom.app.model

import com.google.gson.annotations.SerializedName

data class Order(
    val id: Int,

    @SerializedName("order_token")
    val orderToken: String,

    @SerializedName("items_total_amt")
    val itemsTotalAmt: Double,

    @SerializedName("shipping_cost")
    val shippingCost: Double,

    @SerializedName("total_amount")
    val totalAmount: Double
)