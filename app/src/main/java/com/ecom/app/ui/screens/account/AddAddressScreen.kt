package com.ecom.app.ui.screens.account

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
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
import com.ecom.app.model.account.Address
import com.ecom.app.model.account.AddressFormResponse
import com.ecom.app.model.account.PinCodeResponse
import com.ecom.app.model.account.RegionState
import com.ecom.app.ui.components.ScreenHeader

@Composable
fun AddAddressScreen(
    modifier: Modifier = Modifier,
    response: AddressFormResponse?,
    errorMsg: Map<String, String> = emptyMap(),
    onFetchPincode: suspend (String) -> PinCodeResponse?,
    onSubmit: (
        fullName: String,
        phone: String,
        line1: String,
        line2: String,
        postalCode: String,
        city: String,
        state: String,
        country: String,
        label: String,
        isDefault: Boolean,
        consentPpTc: Boolean
    ) -> Unit
) {

    val address = response?.address
    val states = response?.states.orEmpty()
    val isGuest = response?.isGuest == true

    var fullName by remember(address) {
        mutableStateOf(address?.fullName.orEmpty())
    }

    var phone by remember(address) {
        mutableStateOf(address?.phone.orEmpty())
    }

    var line1 by remember(address) {
        mutableStateOf(address?.line1.orEmpty())
    }

    var line2 by remember(address) {
        mutableStateOf(address?.line2.orEmpty())
    }

    var postalCode by remember(address) {
        mutableStateOf(address?.postalCode.orEmpty())
    }

    var city by remember(address) {
        mutableStateOf(address?.city.orEmpty())
    }

    var state by remember(address) {
        mutableStateOf(address?.state.orEmpty())
    }

    var country by remember(address) {
        mutableStateOf(address?.country ?: "India")
    }

    var label by remember(address) {
        mutableStateOf(address?.label ?: "Home")
    }

    var isDefault by remember(address) {
        mutableStateOf(address?.isDefault == true)
    }

    var consentPpTc by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(postalCode) {
        if (postalCode.length == 6) {

            val result = onFetchPincode(postalCode)

            if (result?.success == true) {
                city = result.city.orEmpty()
                state = result.state.orEmpty()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {

        ScreenHeader(
            title = if (address != null) "Edit Address" else "Add Address",

            subtitle = if (address != null) {
                "Update your delivery details"
            } else {
                "Add new delivery details"
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            AddressCard {

                AddressTextField(
                    label = "Full Name",
                    value = fullName,
                    error = errorMsg["full_name"],
                    onValueChange = { fullName = it }
                )

                AddressTextField(
                    label = "Phone Number",
                    value = phone,
                    error = errorMsg["phone"],
                    onValueChange = { phone = it }
                )

                AddressTextField(
                    label = "Address",
                    value = line1,
                    error = errorMsg["line_1"],
                    onValueChange = { line1 = it }
                )

                AddressTextField(
                    label = "Address line 2 (optional)",
                    value = line2,
                    onValueChange = { line2 = it }
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Column(modifier = Modifier.weight(1f)) {

                        AddressTextField(
                            label = "Pin Code",
                            value = postalCode,
                            error = errorMsg["postal_code"],
                            onValueChange = {
                                postalCode = it.filter { char -> char.isDigit() }.take(6)
                            }
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {

                        AddressTextField(
                            label = "City",
                            value = city,
                            error = errorMsg["city"],
                            onValueChange = { city = it }
                        )
                    }
                }

                StateDropdown(
                    states = states,
                    selectedState = state,
                    error = errorMsg["state"],
                    onStateSelected = {
                        state = it
                    }
                )

                CountryDropdown(
                    selectedCountry = country
                )

                LabelSelector(
                    selectedLabel = label,
                    onSelected = {
                        label = it
                    }
                )

                DefaultAddressCheck(
                    checked = isDefault,
                    onCheckedChange = {
                        isDefault = it
                    }
                )

                if (isGuest) {
                    ConsentCheck(
                        checked = consentPpTc,
                        error = errorMsg["consent_pp_tc"],
                        onCheckedChange = {
                            consentPpTc = it
                        }
                    )
                }

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = {
                        onSubmit(
                            fullName,
                            phone,
                            line1,
                            line2,
                            postalCode,
                            city,
                            state,
                            country,
                            label,
                            isDefault,
                            consentPpTc
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Save Address",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AddressHeader(
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
                .clickable { onBack() }
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = "Address Form",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AddressCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            1.dp,
            Color(0xFFE5E5E5)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content
        )
    }
}

@Composable
private fun AddressTextField(
    label: String,
    value: String,
    error: String? = null,
    onValueChange: (String) -> Unit
) {

    Column {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = !error.isNullOrBlank()
        )

        if (!error.isNullOrBlank()) {

            Spacer(Modifier.height(4.dp))

            Text(
                text = error,
                color = Color.Red,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun StateDropdown(
    states: List<RegionState>,
    selectedState: String,
    error: String?,
    onStateSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "State / Region",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(6.dp))

        Box {
            OutlinedTextField(
                value = selectedState,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = true,
                isError = !error.isNullOrBlank(),
                trailingIcon = {
                    Text(
                        text = if (expanded) "▴" else "▾",
                        fontSize = 18.sp
                    )
                }
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        expanded = true
                    }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                states.forEach { state ->
                    DropdownMenuItem(
                        text = {
                            Text(state.name)
                        },
                        onClick = {
                            onStateSelected(state.name)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (!error.isNullOrBlank()) {
            Spacer(Modifier.height(4.dp))

            Text(
                text = error,
                color = Color.Red,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun CountryDropdown(
    selectedCountry: String
) {

    Column {

        Text(
            text = "Country",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(6.dp))

        OutlinedTextField(
            value = selectedCountry,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true
        )
    }
}

@Composable
private fun LabelSelector(
    selectedLabel: String,
    onSelected: (String) -> Unit
) {

    val labels = listOf(
        "Home",
        "Work",
        "Other"
    )

    Column {

        Text(
            text = "Address Label",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            labels.forEach { label ->

                FilterChip(
                    selected = selectedLabel == label,
                    onClick = {
                        onSelected(label)
                    },
                    label = {
                        Text(label)
                    }
                )
            }
        }
    }
}

@Composable
private fun DefaultAddressCheck(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )

        Text(
            text = "Set as default address",
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ConsentCheck(
    checked: Boolean,
    error: String?,
    onCheckedChange: (Boolean) -> Unit
) {

    Column {

        Row(
            verticalAlignment = Alignment.Top
        ) {

            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange
            )

            Text(
                text = "I agree to the Privacy Policy and Terms & Conditions",
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        if (!error.isNullOrBlank()) {

            Text(
                text = error,
                color = Color.Red,
                fontSize = 13.sp
            )
        }
    }
}