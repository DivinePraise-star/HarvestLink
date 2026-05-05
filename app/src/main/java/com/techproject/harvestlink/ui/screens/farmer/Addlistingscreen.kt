package com.techproject.harvestlink.ui.screens.farmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.model.FarmerListing
import com.techproject.harvestlink.model.ListingStatus

@Composable
fun AddListingScreen(
    farmerId: String,
    onBackClick: () -> Unit,
    onListingAdded: () -> Unit
) {
    var produceName by remember { mutableStateOf("") }
    var quantityAvailable by remember { mutableStateOf("") }
    var pricePerUnit by remember { mutableStateOf("") }

    var isSubmitting by remember { mutableStateOf(false) }
    var submitError by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Inline validation — only shown after the user has typed something
    val nameError = produceName.isNotBlank() && produceName.length < 2
    val quantityError = quantityAvailable.isNotBlank() && quantityAvailable.toIntOrNull() == null
    val priceError = pricePerUnit.isNotBlank() && pricePerUnit.toDoubleOrNull() == null

    val formValid = produceName.isNotBlank() &&
            quantityAvailable.toIntOrNull() != null &&
            pricePerUnit.toDoubleOrNull() != null &&
            !isSubmitting

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Listing added!") },
            text = { Text("Your $produceName listing is now live on the market.") },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false; onListingAdded() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3D2F))
                ) { Text("Done") }
            }
        )
    }

    // Submission runs here so it has a coroutine scope
    if (isSubmitting) {
        LaunchedEffect(Unit) {
            try {
                val newListing = FarmerListing(
                    id = "",
                    farmerId = farmerId,
                    produceName = produceName.trim(),
                    quantityAvailable = quantityAvailable.toInt(),
                    quantitySold = 0,
                    pricePerUnit = pricePerUnit.toDouble(),
                    status = ListingStatus.ACTIVE
                )
                MoreData.addFarmerListing(newListing)
                showSuccessDialog = true
            } catch (e: Exception) {
                submitError = e.localizedMessage ?: "Failed to post listing"
            } finally {
                isSubmitting = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF8F3))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header — matches the style of FarmerOrderRequestScreen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFF1B3D2F))
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(top = 48.dp, start = 16.dp)
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 24.dp, bottom = 20.dp)
                ) {
                    Text(
                        text = "New Listing",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Add produce to the market",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // Form body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-16).dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color(0xFFFBF8F3))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Produce details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1B3D2F)
                )

                // Produce name
                ListingTextField(
                    value = produceName,
                    onValueChange = { produceName = it },
                    label = "Produce name",
                    placeholder = "e.g. Tomatoes",
                    isError = nameError,
                    errorMessage = "Name must be at least 2 characters"
                )

                // Quantity and price side by side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ListingTextField(
                        value = quantityAvailable,
                        onValueChange = { quantityAvailable = it },
                        label = "Quantity (kg)",
                        placeholder = "e.g. 500",
                        keyboardType = KeyboardType.Number,
                        isError = quantityError,
                        errorMessage = "Whole number only",
                        modifier = Modifier.weight(1f)
                    )
                    ListingTextField(
                        value = pricePerUnit,
                        onValueChange = { pricePerUnit = it },
                        label = "Price/kg (Ugx)",
                        placeholder = "e.g. 2000",
                        keyboardType = KeyboardType.Number,
                        isError = priceError,
                        errorMessage = "Enter a valid price",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Live total value summary — only shows once the form is valid
                if (formValid) {
                    val qty = quantityAvailable.toIntOrNull() ?: 0
                    val price = pricePerUnit.toDoubleOrNull() ?: 0.0
                    val total = qty * price
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total value", fontSize = 12.sp, color = Color(0xFF2E7D32))
                                Text(
                                    text = "Ugx ${"%,.0f".format(total)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color(0xFF1B3D2F)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("At Ugx ${"%,.0f".format(price)}/kg", fontSize = 12.sp, color = Color(0xFF2E7D32))
                                Text("for $qty kg", fontSize = 12.sp, color = Color(0xFF2E7D32))
                            }
                        }
                    }
                }

                if (submitError != null) {
                    Text(text = submitError!!, color = Color.Red, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { isSubmitting = true; submitError = null },
                    enabled = formValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1B3D2F),
                        disabledContainerColor = Color(0xFF1B3D2F).copy(alpha = 0.4f)
                    )
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Post Listing", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ListingTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { Text(placeholder, fontSize = 13.sp) },
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1B3D2F),
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorBorderColor = Color.Red
            ),
            singleLine = true
        )
        if (isError) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}