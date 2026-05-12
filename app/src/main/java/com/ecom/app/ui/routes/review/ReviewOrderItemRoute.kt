package com.ecom.app.ui.routes.review

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecom.app.model.review.ReviewOrderItemResponse
import com.ecom.app.network.RetrofitClient
import com.ecom.app.ui.components.ScreenLoading
import com.ecom.app.ui.screens.review.ReviewOrderItemScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ReviewOrderItemRoute(
    innerPadding: PaddingValues,
    scope: CoroutineScope,
    itemToken: String,
    navigateOrderItemDetail: (String) -> Unit
) {
    var reviewOrderItemResponse by remember(itemToken) {
        mutableStateOf<ReviewOrderItemResponse?>(null)
    }

    var isLoading by remember(itemToken) {
        mutableStateOf(true)
    }

    var reviewOrderItemError by remember(itemToken) {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(itemToken) {
        try {
            isLoading = true
            reviewOrderItemError = null

            val response = RetrofitClient.apiService.getReviewOrderItem(itemToken)
            reviewOrderItemResponse = response

        } catch (e: Exception) {
            reviewOrderItemError = e.message ?: "Unable to load review item."
            Log.e("REVIEW_ITEM", "failed: ${e.message}", e)
        } finally {
            isLoading = false
        }
    }

    if (isLoading || reviewOrderItemResponse == null) {
        ScreenLoading(message = "Loading review...")
        return
    }

    ReviewOrderItemScreen(
        modifier = Modifier.padding(innerPadding),
        response = reviewOrderItemResponse,
        error = reviewOrderItemError,
        onSubmitReview = { rating, review ->
            scope.launch {
                try {
                    reviewOrderItemError = null

                    val csrfToken = RetrofitClient.getCsrfToken() ?: return@launch

                    val response = RetrofitClient.apiService.submitReviewOrderItem(
                        csrfToken = csrfToken,
                        itemToken = itemToken,
                        rating = rating,
                        review = review
                    )

                    if (
                        response.success &&
                        response.nextStep == "order_item_details"
                    ) {
                        val token = response.orderItem?.itemToken ?: itemToken
                        navigateOrderItemDetail(token)
                    } else {
                        reviewOrderItemError =
                            response.errorMsg ?: "Unable to submit review."
                    }

                } catch (e: Exception) {
                    reviewOrderItemError = e.message ?: "Unable to submit review."
                    Log.e("REVIEW_SUBMIT", "failed: ${e.message}", e)
                }
            }
        }
    )
}