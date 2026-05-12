package com.ecom.app.model.order

import com.ecom.app.model.account.Address
import com.google.gson.annotations.SerializedName

data class Order(
    val id: Int,

    @SerializedName("order_id")
    val orderId: String,

    @SerializedName("order_token")
    val orderToken: String,

    val state: String,

    @SerializedName("placed_at")
    val placedAt: String?,

    @SerializedName("cancelled_at")
    val cancelledAt: String?,

    @SerializedName("items_total_base_amt")
    val itemsTotalBaseAmt: Double,

    @SerializedName("items_total_gst_amt")
    val itemsTotalGstAmt: Double,

    @SerializedName("items_total_amt")
    val itemsTotalAmt: Double,

    @SerializedName("shipping_cost")
    val shippingCost: Double,

    @SerializedName("total_amount")
    val totalAmount: Double,

    @SerializedName("can_user_cancel")
    val canUserCancel: Boolean,

    @SerializedName("shipping_address")
    val shippingAddress: Address?,

    @SerializedName("billing_address")
    val billingAddress: Address?,
)

data class OrderItem(
    val id: Int,

    @SerializedName("item_token")
    val itemToken: String,

    @SerializedName("order_id")
    val orderId: String,

    @SerializedName("order_token")
    val orderToken: String,

    @SerializedName("product_name")
    val productName: String,

    @SerializedName("variant_name")
    val variantName: String,

    @SerializedName("variant_id")
    val variantId: Int?,

    @SerializedName("variant_slug")
    val variantSlug: String,

    @SerializedName("variant_size")
    val variantSize: String,

    @SerializedName("variant_image_url")
    val variantImageUrl: String?,

    val price: Double,

    val quantity: Int,

    @SerializedName("total_wallet_amt")
    val totalWalletAmt: Double,

    @SerializedName("after_wallet_total_amt")
    val afterWalletTotalAmt: Double,

    @SerializedName("total_amt")
    val totalAmt: Double,

    @SerializedName("status_summary")
    val statusSummary: String,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("can_user_return")
    val canUserReturn: Boolean,

    @SerializedName("can_user_review")
    val canUserReview: Boolean,

    @SerializedName("can_user_track")
    val canUserTrack: Boolean,

    @SerializedName("can_show_details")
    val canShowDetails: Boolean,

    @SerializedName("already_reviewed")
    val alreadyReviewed: Boolean,
)
