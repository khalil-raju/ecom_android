package com.ecom.app.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ecom.app.model.Product
import com.ecom.app.ui.components.HeroBanner
import com.ecom.app.ui.components.ProductCard


@Composable
fun ProductListScreen(
    products: List<Product>,
    modifier: Modifier = Modifier,
    onProductClick: (Product) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            HeroBanner()
        }

        items(products) { product ->
            ProductCard(
                product = product,
                onClick = {
                    onProductClick(product)
                }
            )
        }
    }
}