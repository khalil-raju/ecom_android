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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecom.app.R
import com.ecom.app.model.account.ChangePasswordResponse
import com.ecom.app.ui.components.ScreenHeader

@Composable
fun ChangePasswordScreen(
    modifier: Modifier = Modifier,
    response: ChangePasswordResponse?,
    error: String?,
    onBack: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    val minLengthOk = password.length >= 8
    val notAllNumeric = password.isNotBlank() && !password.all { it.isDigit() }
    val passwordsMatch = password.isNotBlank() && password == confirm

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {

        ScreenHeader(
            title = "Change Password",
            subtitle = "Keep your account secure"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("New Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = confirm,
                        onValueChange = { confirm = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Confirm Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )

                    PasswordRule("Minimum 8 characters", minLengthOk)
                    PasswordRule("Cannot be all numbers", notAllNumeric)
                    PasswordRule("Passwords must match", passwordsMatch)

                    if (!error.isNullOrBlank()) {
                        Text(error, color = Color.Red, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { onSubmit(password, confirm) },
                        enabled = minLengthOk && notAllNumeric && passwordsMatch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Save Password", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordRule(
    text: String,
    valid: Boolean
) {
    Text(
        text = "${if (valid) "✓" else "•"} $text",
        color = if (valid) Color(0xFF0A8F2E) else Color.Gray,
        fontSize = 13.sp
    )
}