package com.ecom.app.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecom.app.R
import com.ecom.app.ui.components.ScreenFooter
import com.ecom.app.ui.components.ScreenHeader


@Composable
fun ChangeContactScreen(
    modifier: Modifier = Modifier,
    title: String,
    label: String,
    value: String,
    error: String?,
    onSubmit: (String) -> Unit
) {
    var input by remember(value) { mutableStateOf(value) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {

        ScreenHeader(title = title)

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(label) },
                        singleLine = true
                    )

                    if (!error.isNullOrBlank()) {
                        Text(error, color = Color.Red, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { onSubmit(input.trim()) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            ScreenFooter()
        }
    }
}