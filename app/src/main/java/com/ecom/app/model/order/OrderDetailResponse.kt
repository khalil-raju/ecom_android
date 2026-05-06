// model/OrderDetailResponse.kt
package com.ecom.app.model.order

import com.ecom.app.model.account.Address
import com.google.gson.annotations.SerializedName

data class OrderDetailResponse(
    val success: Boolean,

    val order: Order,

    @SerializedName("shipping_address")
    val shippingAddress: Address?,

    @SerializedName("billing_address")
    val billingAddress: Address?,

    @SerializedName("order_items")
    val orderItems: List<OrderItem>,

    val payment: OrderPayment?,

    val invoice: OrderInvoice?
)
