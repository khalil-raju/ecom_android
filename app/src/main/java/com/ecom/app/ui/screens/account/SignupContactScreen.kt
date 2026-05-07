package com.ecom.app.ui.screens.account

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

@Composable
fun SignupContactScreen(
    error: String?,
    onLogoClick: () -> Unit,
    onContinue: (contact: String, consent: Boolean) -> Unit,
    onLoginClick: () -> Unit
) {
    AuthShell(onLogoClick = onLogoClick) {
        var contact by remember { mutableStateOf("") }
        var consent by remember { mutableStateOf(false) }

        Text(
            text = "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(18.dp))

        OutlinedTextField(
            value = contact,
            onValueChange = { contact = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email or Phone") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        Row {
            Checkbox(
                checked = consent,
                onCheckedChange = { consent = it }
            )

            Text(
                text = "I agree to the Privacy Policy and Terms & Conditions",
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        if (!error.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = { onContinue(contact.trim(), consent) },
            modifier = Modifier.fillMaxWidth(),
            enabled = contact.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                disabledContainerColor = Color.DarkGray
            )
        ) {
            Text(
                text = "Continue",
                color = Color.White
            )
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onLoginClick) {
            Text("Already have an account? Login")
        }
    }
}