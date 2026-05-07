package com.ecom.app.ui.screens.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.ecom.app.ui.screens.account.AuthShell

@Composable
fun SignupPasswordScreen(
    contact: String,
    error: String?,
    onLogoClick: () -> Unit,
    onContinue: (password: String, confirm: String) -> Unit,
    onLoginClick: () -> Unit
) {
    AuthShell(onLogoClick = onLogoClick) {
        var password by remember { mutableStateOf("") }
        var confirm by remember { mutableStateOf("") }

        val minLengthOk = password.length >= 8
        val notAllNumeric = password.isNotBlank() && !password.all { it.isDigit() }
        val passwordsMatch = password.isNotBlank() && password == confirm

        Text(
            text = "Set Password",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(contact, fontSize = 14.sp)

        Spacer(Modifier.height(18.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(Modifier.height(10.dp))

        PasswordRule("Minimum 8 characters", minLengthOk)
        PasswordRule("Cannot be all numbers", notAllNumeric)
        PasswordRule("Passwords must match", passwordsMatch)

        if (!error.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = { onContinue(password, confirm) },
            modifier = Modifier.fillMaxWidth(),
            enabled = minLengthOk && notAllNumeric && passwordsMatch,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                disabledContainerColor = Color.Gray
            )

        ) {
            Text("Continue")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onLoginClick) {
            Text("Back to Login")
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
        color = if (valid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 13.sp
    )
}
