package com.ecom.app.ui.screens.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.ecom.app.BuildConfig

@Composable
fun LoginContactScreen(
    onLogoClick: () -> Unit,
    onContinue: (String) -> Unit,
    onSignupClick: () -> Unit,
    onGuestCheckoutClick: (() -> Unit)? = null,
    error: String? = null
) {
    var contact by remember { mutableStateOf("") }

    val brandName = BuildConfig.BRAND_NAME

    AuthShell(
        onLogoClick = onLogoClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Login",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                modifier = Modifier.padding(bottom = 28.dp)
            )

            OutlinedTextField(
                value = contact,
                onValueChange = { contact = it },
                placeholder = {
                    Text("Email or phone number")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

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

            Button(
                onClick = { onContinue(contact) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Text(
                    text = "Continue",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            Row(
                modifier = Modifier.padding(top = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "New to $brandName? ",
                    color = Color.Gray
                )

                Text(
                    text = "Create an account",
                    color = Color.Blue,
                    modifier = Modifier.clickable { onSignupClick() }
                )
            }

            if (onGuestCheckoutClick != null) {
                Text(
                    text = "Continue checkout as guest",
                    color = Color.Blue,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(top = 22.dp)
                        .clickable { onGuestCheckoutClick() }
                )
            }
        }
    }
}
