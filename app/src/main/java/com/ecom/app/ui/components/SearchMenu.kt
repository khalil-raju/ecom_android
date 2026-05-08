package com.ecom.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecom.app.R
import com.ecom.app.model.product.SearchSuggestion
import com.ecom.app.network.RetrofitClient
import kotlinx.coroutines.delay

@Composable
fun SearchMenu(
    onClose: () -> Unit

) {
    var query by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<SearchSuggestion>>(emptyList()) }

    LaunchedEffect(query) {
        if (query.trim().isEmpty()) {
            suggestions = emptyList()
            return@LaunchedEffect
        }

        delay(300)

        try {
            val response = RetrofitClient.apiService.getSearchSuggestions(query.trim())
            suggestions = response.suggestions
        } catch (e: Exception) {
            suggestions = emptyList()
            e.printStackTrace()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp)
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp, top = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "×",
                fontSize = 42.sp,
                modifier = Modifier.clickable { onClose() }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search...") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        // later: navigate to search results
                    }
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                tint = Color.Black,
                modifier = Modifier.size(26.dp)
            )
        }

        suggestions.forEach { suggestion ->
            Text(
                text = "${suggestion.name}${suggestion.size?.let { " - $it" } ?: ""}",
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        query = suggestion.name
                    }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            )
            HorizontalDivider()
        }
    }
}