package com.ecom.app.model

import com.google.gson.annotations.SerializedName

data class OrderPayment(
    @SerializedName("online_method")
    val onlineMethod: String?,

    @SerializedName("online_paid_amt")
    val onlinePaidAmt: Double,

    @SerializedName("wallet_paid_amt")
    val walletPaidAmt: Double,

    @SerializedName("cod_paid_amt")
    val codPaidAmt: Double,

    @SerializedName("total_due_amt")
    val totalDueAmt: Double
)