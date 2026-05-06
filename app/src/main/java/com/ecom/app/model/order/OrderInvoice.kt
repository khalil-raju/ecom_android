package com.ecom.app.model.order

import com.google.gson.annotations.SerializedName

data class OrderInvoice(
    @SerializedName("pdf_url")
    val pdfUrl: String?,

    @SerializedName("credit_notes")
    val creditNotes: List<OrderCreditNote>
)