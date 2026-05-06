package com.ecom.app.ui.screens.product

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

import kotlinx.coroutines.launch

import coil.compose.AsyncImage

import com.ecom.app.R
import com.ecom.app.BuildConfig
import com.ecom.app.model.product.ProductDetailResponse
import com.ecom.app.model.product.ProductVariantDetail
import com.ecom.app.network.RetrofitClient


private fun fullUrl(path: String?): String? {
    return path?.let {
        if (it.startsWith("http")) it
        else BuildConfig.BASE_URL.trimEnd('/') + it
    }
}

@Composable
fun ProductDetailScreen(
    detail: ProductDetailResponse,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onCartCountChange: (Int) -> Unit
) {
    var selectedVariant by remember {
        mutableStateOf(detail.activeVariant ?: detail.product.variants.firstOrNull())
    }

    var selectedImage by remember(selectedVariant) {
        mutableStateOf(selectedVariant?.images?.firstOrNull())
    }

    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            DetailActionRow(onBack = onBack)

            ZoomableImage(
                images = selectedVariant?.images.orEmpty(),
                selectedImage = selectedImage,
                isOutOfStock = (selectedVariant?.stock ?: 0) <= 0,
                onImageChange = { selectedImage = it }
            )

            ThumbnailRow(
                images = selectedVariant?.images.orEmpty(),
                selectedImage = selectedImage,
                onImageClick = { selectedImage = it }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 22.dp)
            ) {
                Text(
                    text = detail.product.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = detail.product.description ?: "",
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "₹${selectedVariant?.price ?: ""}",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(26.dp))

                VariantButtons(
                    variants = detail.product.variants,
                    selectedVariant = selectedVariant,
                    onVariantClick = { variant ->
                        selectedVariant = variant
                    }
                )

                Spacer(modifier = Modifier.height(28.dp))

                AddToBagButton(
                    enabled = (selectedVariant?.stock ?: 0) > 0,
                    onClick = {
                        val variant = selectedVariant ?: return@AddToBagButton
                        val csrfToken = RetrofitClient.getCsrfToken() ?: return@AddToBagButton

                        scope.launch {
                            try {
                                val response = RetrofitClient.apiService.addToCart(
                                    csrfToken = csrfToken,
                                    productId = detail.product.id,
                                    variantId = variant.id
                                )

                                onCartCountChange(response.cartCount)

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun DetailActionRow(
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "Back",
            tint = Color.Black,
            modifier = Modifier
                .size(28.dp)
                .clickable { onBack() }
        )
    }
}

@Composable
fun ZoomableImage(
    images: List<String?>,
    selectedImage: String?,
    isOutOfStock: Boolean,
    onImageChange: (String?) -> Unit
) {
    var scale by remember(selectedImage) { mutableFloatStateOf(1f) }
    var offsetX by remember(selectedImage) { mutableFloatStateOf(0f) }
    var offsetY by remember(selectedImage) { mutableFloatStateOf(0f) }
    var dragAmount by remember { mutableFloatStateOf(0f) }

    val state = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 4f)

        if (scale > 1f) {
            offsetX += panChange.x
            offsetY += panChange.y
        }
    }

    fun swipe(direction: Int) {
        if (images.isEmpty()) return

        val currentIndex = images.indexOf(selectedImage).takeIf { it >= 0 } ?: 0
        val nextIndex = (currentIndex + direction + images.size) % images.size

        onImageChange(images[nextIndex])
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clipToBounds()
            .pointerInput(scale, selectedImage) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        dragAmount = 0f
                    },
                    onHorizontalDrag = { _, dragAmountChange ->
                        if (scale == 1f) {
                            dragAmount += dragAmountChange
                        }
                    },
                    onDragEnd = {
                        if (scale == 1f) {
                            when {
                                dragAmount < -120f -> swipe(1)   // next
                                dragAmount > 120f -> swipe(-1)   // previous
                            }
                        }
                        dragAmount = 0f
                    }
                )
            }
    ) {
        AsyncImage(
            model = fullUrl(selectedImage),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                    translationY = offsetY
                }
                .pointerInput(selectedImage) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (scale > 1f) {
                                scale = 1f
                                offsetX = 0f
                                offsetY = 0f
                            } else {
                                scale = 2f
                            }
                        }
                    )
                }
                .transformable(state),
            contentScale = ContentScale.Crop
        )

        if (isOutOfStock) {
            Text(
                text = "OUT OF STOCK",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-60).dp, y = 30.dp)
                    .rotate(-45f)
                    .background(Color.Red)
                    .padding(horizontal = 60.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun ThumbnailRow(
    images: List<String?>,
    selectedImage: String?,
    onImageClick: (String?) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        items(images) { image ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .size(72.dp)
                    .clickable { onImageClick(image) }
            ) {
                AsyncImage(
                    model = fullUrl(image),
                    contentDescription = "Product thumbnail",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (image == selectedImage) {
                    HorizontalDivider(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        thickness = 3.dp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun VariantButtons(
    variants: List<ProductVariantDetail>,
    selectedVariant: ProductVariantDetail?,
    onVariantClick: (ProductVariantDetail) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        items(variants) { variant ->
            val selected = variant.id == selectedVariant?.id

            OutlinedButton(
                onClick = {
                    onVariantClick(variant)
                },
                border = BorderStroke(2.dp, Color.Black),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selected) Color.Black else Color.White,
                    contentColor = if (selected) Color.White else Color.Black
                ),
                modifier = Modifier.size(66.dp)
            ) {
                Text(
                    text = variant.size ?: "",
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
private fun AddToBagButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        border = BorderStroke(2.dp, Color.Black),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Color.White,
            disabledContentColor = Color.Gray
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
    ) {
        Text(
            text = if (enabled) "Add to Bag" else "Out of Stock",
            fontSize = 24.sp
        )
    }
}