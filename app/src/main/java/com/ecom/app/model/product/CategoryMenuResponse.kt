package com.ecom.app.model.product

data class CategoryMenuResponse(
    val success: Boolean,
    val categories: List<CategoryMenuItem> = emptyList()
)

data class CategoryMenuItem(
    val name: String,
    val slug: String,
    val children: List<CategoryMenuChild> = emptyList()
)

data class CategoryMenuChild(
    val name: String,
    val slug: String
)