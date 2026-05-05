package com.ecom.app.model

import com.google.gson.annotations.SerializedName

data class OrderCreditNote(
    @SerializedName("pdf_url")
    val pdfUrl: String?
)