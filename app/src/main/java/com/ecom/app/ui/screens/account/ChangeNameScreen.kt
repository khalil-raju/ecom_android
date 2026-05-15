package com.ecom.app.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.ecom.app.model.account.ChangeNameResponse
import com.ecom.app.ui.components.ScreenFooter
import com.ecom.app.ui.components.ScreenHeader

@Composable
fun ChangeNameScreen(
    modifier: Modifier = Modifier,
    response: ChangeNameResponse?,
    error: String?,
    onSubmit: (String, String) -> Unit
) {
    var firstName by remember(response) {
        mutableStateOf(response?.firstName.orEmpty())
    }

    var lastName by remember(response) {
        mutableStateOf(response?.lastName.orEmpty())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {

        ScreenHeader(
            title = "Change Name",
            subtitle = "Update your profile name"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("First Name")
                        },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Last Name")
                        },
                        singleLine = true
                    )

                    if (!error.isNullOrBlank()) {
                        Text(
                            text = error,
                            color = Color.Red,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = {
                            onSubmit(firstName, lastName)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "Save Name",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            ScreenFooter()
        }
    }
}