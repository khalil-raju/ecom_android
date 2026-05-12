package com.ecom.app.model.product

data class ProductSearchResponse(
    val results: List<ProductLite>,
    val query: String
)

data class AutocompleteSearchSuggestionsResponse(
    val suggestions: List<AutocompleteSearchSuggestion>
)

data class AutocompleteSearchSuggestion(
    val name: String,
    val size: String
)
