package com.ecom.app.util

object PaymentType {
    const val ONLINE = "ONLINE"
    const val COD = "COD"
    const val FREE = "FREE"
    const val RZP_ONLINE = "RZP_ONLINE"
}

object NextStep {
    const val INITIATE_RZP = "initiate_rzp_payment"
    const val FINALIZE_ORDER = "finalize_order"
    const val BASKET = "basket_detail"
}