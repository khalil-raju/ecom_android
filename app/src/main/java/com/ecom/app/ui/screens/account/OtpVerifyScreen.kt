package com.ecom.app.ui.screens.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun OtpVerifyScreen(
    contact: String,
    title: String = "Welcome",
    buttonText: String = "Login",
    resendInitialSeconds: Int = 60,
    error: String?,
    onLogoClick: () -> Unit,
    onVerifyOtp: (String) -> Unit,
    onResendOtp: () -> Unit
) {
    var otp by remember { mutableStateOf("") }
    var remainingSeconds by remember { mutableIntStateOf(resendInitialSeconds) }

    LaunchedEffect(remainingSeconds) {
        if (remainingSeconds > 0) {
            delay(1000)
            remainingSeconds -= 1
        }
    }

    AuthShell(
        onLogoClick = onLogoClick
    ) {
        Text(
            text = title,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(26.dp))

        Text(
            text = contact,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(22.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = {
                otp = it.filter { char -> char.isDigit() }.take(6)
            },
            placeholder = {
                Text("Enter 6-digit OTP")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        if (remainingSeconds > 0) {
            Text(
                text = "Resend OTP in $remainingSeconds sec",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            TextButton(
                onClick = {
                    remainingSeconds = resendInitialSeconds
                    onResendOtp()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Resend OTP")
            }
        }

        if (!error.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))

            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp
            )
        }

        Spacer(Modifier.height(26.dp))

        Button(
            onClick = {
                onVerifyOtp(otp)
            },
            enabled = otp.length == 6,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = androidx.compose.ui.graphics.Color.Black,
                contentColor = androidx.compose.ui.graphics.Color.White
            )
        ) {
            Text(
                text = buttonText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}