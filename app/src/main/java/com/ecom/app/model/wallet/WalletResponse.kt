package com.ecom.app.model.wallet

import com.google.gson.annotations.SerializedName

data class WalletResponse(
    val success: Boolean,

    @SerializedName("wallet_balance")
    val walletBalance: Double = 0.0,

    val txns: List<WalletTxn> = emptyList()
)

data class WalletTxn(
    val id: Int,

    val amount: Double,

    @SerializedName("txn_type")
    val txnType: String,

    @SerializedName("txn_type_display")
    val txnTypeDisplay: String,

    val notes: String = "",

    @SerializedName("created_at")
    val createdAt: String? = null
)