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
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import com.ecom.app.R

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

        var showPassword by remember { mutableStateOf(false) }
        var showConfirm by remember { mutableStateOf(false) }

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
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                Icon(
                    painter = painterResource(
                        id = if (showPassword) R.drawable.ic_eye_open else R.drawable.ic_eye_close
                    ),
                    contentDescription = "Toggle password",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { showPassword = !showPassword }
                )
            }
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                Icon(
                    painter = painterResource(
                        id = if (showConfirm) R.drawable.ic_eye_open else R.drawable.ic_eye_close
                    ),
                    contentDescription = "Toggle confirm password",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { showConfirm = !showConfirm }
                )
            }
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
