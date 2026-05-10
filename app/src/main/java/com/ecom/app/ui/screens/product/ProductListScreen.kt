package com.ecom.app.ui.screens.product

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import com.ecom.app.model.product.Product
import com.ecom.app.ui.components.HeroBanner
import com.ecom.app.ui.components.ProductCard


@Composable
fun ProductListScreen(
    products: List<Product>,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    hasMore: Boolean,
    modifier: Modifier = Modifier,
    onLoadMore: () -> Unit,
    onProductClick: (Product) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, products.size, hasMore) {

        snapshotFlow {
            val layoutInfo = listState.layoutInfo

            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItemIndex =
                layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            totalItems > 0 &&
                    lastVisibleItemIndex >= totalItems - 3
        }
            .distinctUntilChanged()
            .filter { it }
            .collect {

                if (hasMore && !isLoading && !isLoadingMore) {
                    onLoadMore()
                }
            }
    }

    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
    ) {
        item {
            HeroBanner()
        }

        item {
            Spacer(Modifier.height(32.dp))
        }

        items(products) { product ->
            ProductCard(
                product = product,
                onClick = {
                    onProductClick(product)
                }
            )
        }

        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}