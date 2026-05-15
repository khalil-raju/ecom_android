package com.ecom.app.ui.screens.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecom.app.R
import com.ecom.app.model.account.Address
import com.ecom.app.ui.components.ScreenFooter
import com.ecom.app.ui.components.ScreenHeader

@Composable
fun SavedAddressesScreen(
    modifier: Modifier = Modifier,
    addresses: List<Address>,
    onAddAddress: () -> Unit,
    onEditAddress: (Int) -> Unit,
    onDeleteAddress: (Int) -> Unit,
    onSelectAddress: (Address) -> Unit = {}
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {

        ScreenHeader(
            title = "Saved Addresses",
            subtitle = "Manage your delivery addresses"
        )

        if (addresses.isEmpty()) {

            EmptyAddresses(
                onAddAddress = onAddAddress
            )

        } else {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(
                    vertical = 16.dp
                )
            ) {

                items(addresses) { address ->

                    AddressCard(
                        address = address,
                        onClick = {
                            onSelectAddress(address)
                        },
                        onEdit = {
                            address.id?.let(onEditAddress)
                        },
                        onDelete = {
                            address.id?.let(onDeleteAddress)
                        }
                    )
                }

                item {

                    Spacer(Modifier.height(6.dp))

                    AddAddressButton(
                        onClick = onAddAddress
                    )
                }

                item {
                    ScreenFooter()
                }
            }
        }
    }
}

@Composable
private fun SavedAddressesHeader(
    onBack: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "Back",
            tint = Color.Black,
            modifier = Modifier
                .size(28.dp)
                .clickable {
                    onBack()
                }
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = "Saved Addresses",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AddressCard(
    address: Address,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            1.dp,
            Color(0xFFE4E4E4)
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = address.fullName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (address.isDefault) {

                            Spacer(Modifier.width(8.dp))

                            Surface(
                                color = Color.Black,
                                shape = MaterialTheme.shapes.small
                            ) {

                                Text(
                                    text = "Default",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(
                                        horizontal = 8.dp,
                                        vertical = 3.dp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    Surface(
                        color = Color(0xFFF2F2F2),
                        shape = MaterialTheme.shapes.small
                    ) {

                        Text(
                            text = address.label,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 4.dp
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            AddressLine(address.line1)

            if (address.line2.isNotBlank()) {

                Spacer(Modifier.height(4.dp))

                AddressLine(address.line2)
            }

            Spacer(Modifier.height(4.dp))

            AddressLine(
                "${address.city}, ${address.state} - ${address.postalCode}"
            )

            Spacer(Modifier.height(4.dp))

            AddressLine(address.country)

            Spacer(Modifier.height(10.dp))

            Text(
                text = address.phone,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                OutlinedButton(
                    onClick = onEdit
                ) {

                    Text("Edit")
                }

                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    )
                ) {

                    Text("Delete")
                }
            }
        }
    }
}

@Composable
private fun AddressLine(
    text: String
) {

    Text(
        text = text,
        fontSize = 14.sp,
        color = Color.DarkGray,
        lineHeight = 20.sp
    )
}

@Composable
private fun AddAddressButton(
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        )
    ) {

        Text(
            text = "Add New Address",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmptyAddresses(
    onAddAddress: () -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "No saved addresses",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Add your delivery address to continue shopping.",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onAddAddress,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {

                Text("Add Address")
            }
        }
    }
}