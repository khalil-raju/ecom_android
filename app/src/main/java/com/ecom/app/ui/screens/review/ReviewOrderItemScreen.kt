package com.ecom.app.ui.screens.review

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ecom.app.BuildConfig
import com.ecom.app.R
import com.ecom.app.model.order.OrderItem
import com.ecom.app.model.review.ReviewOrderItemResponse
import com.ecom.app.ui.components.ScreenFooter
import com.ecom.app.ui.components.ScreenHeader

private fun fullUrl(path: String?): String? {
    return path?.let {
        if (it.startsWith("http")) it
        else BuildConfig.BASE_URL.trimEnd('/') + it
    }
}

@Composable
fun ReviewOrderItemScreen(
    modifier: Modifier = Modifier,
    response: ReviewOrderItemResponse?,
    error: String?,
    onSubmitReview: (
        rating: Int,
        review: String
    ) -> Unit
) {
    val item = response?.orderItem

    var rating by remember(response) {
        mutableIntStateOf(response?.review?.rating ?: 0)
    }

    var reviewText by remember(response) {
        mutableStateOf(response?.review?.review.orEmpty())
    }

    val alreadyReviewed = item?.alreadyReviewed == true

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {

        ScreenHeader(
            title = if (alreadyReviewed) "Your Review" else "Review Item",
            subtitle = if (alreadyReviewed) {
                "View your submitted feedback"
            } else {
                "Share your experience with this product"
            }
        )

        if (item == null) {
            EmptyState()
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            InfoBanner(alreadyReviewed = alreadyReviewed)

            ReviewItemTopCard(item = item)

            RatingCard(
                rating = rating,
                enabled = !alreadyReviewed,
                alreadyReviewed = alreadyReviewed,
                onRatingChange = { rating = it }
            )

            ReviewTextCard(
                value = reviewText,
                enabled = !alreadyReviewed,
                alreadyReviewed = alreadyReviewed,
                onValueChange = {
                    if (it.length <= 1000) reviewText = it
                }
            )

            if (!error.isNullOrBlank()) {
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (!alreadyReviewed) {
                ReviewFooter(
                    enabled = rating > 0,
                    onClick = {
                        onSubmitReview(
                            rating,
                            reviewText.trim()
                        )
                    }
                )
            }

            ScreenFooter()
        }
    }
}

@Composable
private fun InfoBanner(alreadyReviewed: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        border = BorderStroke(1.dp, Color(0xFFFFD166))
    ) {
        Text(
            text = if (alreadyReviewed) {
                "You have already reviewed this item."
            } else {
                "Share your experience with this product to help other shoppers."
            },
            modifier = Modifier.padding(16.dp),
            fontSize = 15.sp,
            color = Color.Black
        )
    }
}

@Composable
private fun ReviewItemTopCard(item: OrderItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = fullUrl(item.variantImageUrl),
                contentDescription = item.variantName,
                modifier = Modifier
                    .size(width = 116.dp, height = 150.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.variantName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = buildItemMeta(item),
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Qty: ${item.quantity}",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    text = "₹${formatAmount(item.totalAmt)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                item.statusSummary?.takeIf { it.isNotBlank() }?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = statusColor(it),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
@Composable
private fun RatingCard(
    rating: Int,
    enabled: Boolean,
    alreadyReviewed: Boolean,
    onRatingChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = if (alreadyReviewed) "Your Rating" else "Rate this product",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = when {
                    rating > 0 && alreadyReviewed -> "You rated this item $rating out of 5"
                    rating > 0 -> "$rating out of 5"
                    else -> "Tap to rate"
                },
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 1..5) {
                    Text(
                        text = if (i <= rating) "★" else "☆",
                        fontSize = 44.sp,
                        color = if (i <= rating) Color(0xFFFFB300) else Color.Gray,
                        modifier = Modifier.clickable(enabled = enabled) {
                            onRatingChange(i)
                        }
                    )
                }
            }
        }
    }
}
@Composable
private fun ReviewTextCard(
    value: String,
    enabled: Boolean,
    alreadyReviewed: Boolean,
    onValueChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = if (alreadyReviewed) "Your Review" else "Write your review",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = if (alreadyReviewed) {
                    "This is the review you submitted for this item."
                } else {
                    "Tell others about your experience with this product."
                },
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        if (alreadyReviewed) {
                            "No written review submitted."
                        } else {
                            "Write your review here..."
                        }
                    )
                },
                minLines = 5,
                shape = RoundedCornerShape(10.dp)
            )

            if (!alreadyReviewed) {
                Spacer(Modifier.height(6.dp))

                Text(
                    text = "${value.length}/1000",
                    modifier = Modifier.align(Alignment.End),
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun ReviewFooter(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White,
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.White
            )
        ) {
            Text(
                text = "Submit Review",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Review item not found.", fontSize = 16.sp)
    }
}

private fun buildItemMeta(item: OrderItem): String {
    val parts = mutableListOf<String>()

    item.variantSize?.takeIf { it.isNotBlank() }?.let {
        parts.add("Size: $it")
    }

    return parts.joinToString("  •  ")
}

private fun statusColor(status: String): Color {
    return when {
        status.contains("cancel", ignoreCase = true) -> Color.Red
        status.contains("deliver", ignoreCase = true) -> Color(0xFF0B8F3A)
        status.contains("return", ignoreCase = true) -> Color(0xFFE67E22)
        else -> Color.DarkGray
    }
}

private fun formatAmount(value: Double): String {
    return if (value % 1.0 == 0.0) value.toInt().toString()
    else "%.2f".format(value)
}