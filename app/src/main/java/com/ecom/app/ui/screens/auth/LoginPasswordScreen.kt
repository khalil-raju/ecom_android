package com.ecom.app.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable

import com.ecom.app.R

@Composable
fun LoginPasswordScreen(
    onLogoClick: () -> Unit,
    contact: String,
    error: String? = null,
    attemptsLeft: Int? = null,
    onLogin: (String) -> Unit,
    onOtpLogin: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    AuthShell(
        onLogoClick = onLogoClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Welcome",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = contact,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Password input with eye toggle
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(end = 48.dp)
                )

                Icon(
                    painter = painterResource(
                        id = if (showPassword)
                            R.drawable.ic_eye_open
                        else
                            R.drawable.ic_eye_close
                    ),
                    contentDescription = "Toggle password",
                    tint = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                        .size(22.dp)
                        .clickable { showPassword = !showPassword }
                )
            }

            // Attempts left
            if (attemptsLeft != null) {
                Text(
                    text = "$attemptsLeft login attempts left",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                )
            }

            // Error
            if (error != null) {
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }

            // Login button
            Button(
                onClick = { onLogin(password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Text(
                    text = "Login",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            // OTP login link
            Text(
                text = "Forgot Password? Use OTP to login",
                color = Color.Blue,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 18.dp)
                    .clickable { onOtpLogin() }
            )
        }
    }
}
