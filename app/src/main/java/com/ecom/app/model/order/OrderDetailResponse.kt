// model/OrderDetailResponse.kt
package com.ecom.app.model.order

import com.ecom.app.model.account.Address
import com.ecom.app.model.payment.Payment
import com.google.gson.annotations.SerializedName

data class OrderDetailResponse(
    val success: Boolean,

    val order: Order,

    @SerializedName("order_items")
    val orderItems: List<OrderItem>,

    val payment: Payment?,

    val invoice: OrderInvoice?
)
