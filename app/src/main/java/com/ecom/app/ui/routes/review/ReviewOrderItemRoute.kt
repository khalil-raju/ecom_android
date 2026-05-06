// ui/routes/ReviewOrderItemRoute.kt
package com.ecom.app.ui.routes.review

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.model.review.ReviewOrderItemResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.screens.review.ReviewOrderItemScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ReviewOrderItemRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    itemToken: String,
    navigateBack: () -> Unit,
    navigateOrderItemDetail: (String) -> Unit
) {
    var reviewOrderItemResponse by remember(itemToken) {
        mutableStateOf<ReviewOrderItemResponse?>(null)
    }

    var reviewOrderItemError by remember(itemToken) {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(itemToken) {
        try {
            reviewOrderItemError = null
            reviewOrderItemResponse =
                RetrofitClient.apiService.getReviewOrderItem(itemToken)
        } catch (e: Exception) {
            reviewOrderItemError = e.message ?: "Unable to load review item."
            Log.e("REVIEW_ITEM", "failed: ${e.message}", e)
        }
    }

    ReviewOrderItemScreen(
        modifier = Modifier.padding(innerPadding),
        response = reviewOrderItemResponse,
        error = reviewOrderItemError,
        onBack = navigateBack,
        onSubmitReview = { rating, review ->
            scope.launch {
                try {
                    val csrfToken =
                        RetrofitClient.getCsrfToken() ?: return@launch

                    val response =
                        RetrofitClient.apiService.submitReviewOrderItem(
                            csrfToken = csrfToken,
                            itemToken = itemToken,
                            rating = rating,
                            review = review
                        )

                    if (
                        response.success &&
                        response.nextStep == "order_item_details"
                    ) {
                        reviewOrderItemError = null
                        navigateOrderItemDetail(response.itemToken ?: itemToken)
                    } else {
                        reviewOrderItemError =
                            response.error ?: "Unable to submit review."
                    }
                } catch (e: Exception) {
                    reviewOrderItemError =
                        e.message ?: "Unable to submit review."
                    Log.e("REVIEW_SUBMIT", "failed: ${e.message}", e)
                }
            }
        }
    )
}