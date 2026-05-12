package com.ecom.app.model.product

data class ProductCategoryResponse(
    val categories: List<ProductCategory>
)

data class ProductCategory(
    val name: String,
    val slug: String,
    val children: List<ProductCategoryChild>
)

data class ProductCategoryChild(
    val name: String,
    val slug: String
)
