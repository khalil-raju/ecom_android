package com.ecom.app.ui.screens.product

import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import com.ecom.app.model.product.ProductLite
import com.ecom.app.ui.components.HeroBanner
import com.ecom.app.ui.components.ProductCard
import com.ecom.app.ui.components.ScreenLoading
import kotlinx.coroutines.launch


@Composable
fun ProductListScreen(
    products: List<ProductLite>,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    hasMore: Boolean,
    modifier: Modifier = Modifier,
    onLoadMore: () -> Unit,
    onProductClick: (ProductLite) -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var isHeroLoaded by remember { mutableStateOf(false) }

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
            ScreenLoading(message = "Loading...")
        }
        return
    }


    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (isHeroLoaded) 1f else 0f)
        ) {
            item {
                HeroBanner(
                    onHeroLoaded = {
                        isHeroLoaded = true
                        scope.launch {
                            listState.scrollToItem(0)
                        }
                    }
                )
            }

            item {
                Spacer(Modifier.height(32.dp))
            }

            items(products) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product) }
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

        if (!isHeroLoaded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                ScreenLoading(message = "Loading...")
            }
        }
    }
}