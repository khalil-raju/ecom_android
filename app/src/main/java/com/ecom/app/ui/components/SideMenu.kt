package com.ecom.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SideMenu(
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp)
            .padding(top = 16.dp)
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

        MenuItem("Home")
        ExpandableMenuGroup(
            title = "About Us",
            items = listOf(
                "▹ Terms & Conditions",
                "▹ Privacy Policy",
                "▹ Returns & Shipping Policy",
                "▹ Contact Us"
            )
        )

        MenuItem("Login")
        MenuItem("Sign Up")
    }
}

@Composable
private fun MenuItem(text: String) {
    Column {
        Text(
            text = text,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(horizontal = 24.dp, vertical = 12.dp)
        )
        HorizontalDivider(thickness = 1.dp)
    }
}

@Composable
private fun ExpandableMenuGroup(title: String, items: List<String>) {
    var open by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "$title ${if (open) "▾" else "▸"}",
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { open = !open }
                .padding(horizontal = 24.dp, vertical = 12.dp)
        )
        HorizontalDivider(thickness = 1.dp)

        if (open) {
            items.forEach {
                Text(
                    text = it,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(horizontal = 42.dp, vertical = 10.dp)
                )
                HorizontalDivider(thickness = 1.dp)
            }
        }
    }
}
