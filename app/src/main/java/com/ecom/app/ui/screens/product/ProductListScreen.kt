package com.ecom.app.ui.screens.product

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ecom.app.model.product.Product
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
    }
}