package com.ecom.app.model

import com.google.gson.annotations.SerializedName

data class OrderItem(

    val id: Int? = null,

    @SerializedName("item_token")
    val itemToken: String? = null,

    // -------- Product --------
    @SerializedName("variant_id")
    val variantId: Int?,

    @SerializedName("variant_slug")
    val variantSlug: String?,

    @SerializedName("variant_name")
    val variantName: String,

    @SerializedName("variant_size")
    val variantSize: String?,

    @SerializedName("variant_image_url")
    val variantImageUrl: String?,

    // -------- Pricing --------
    val price: Double,
    val quantity: Int,

    @SerializedName("total_amt")
    val totalAmt: Double,

    // -------- HISTORY --------
    @SerializedName("order_token")
    val orderToken: String? = null,

    @SerializedName("order_id")
    val orderId: String? = null,

    @SerializedName("order_state")
    val orderState: String? = null,

    @SerializedName("can_show_details")
    val canShowDetails: Boolean? = null,

    @SerializedName("status_summary")
    val statusSummary: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    // -------- DETAIL --------
    @SerializedName("can_user_return")
    val canUserReturn: Boolean? = null,

    @SerializedName("can_user_review")
    val canUserReview: Boolean? = null,

    @SerializedName("can_user_track")
    val canUserTrack: Boolean? = null
)