package com.techproject.harvestlink.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.techproject.harvestlink.R
import com.techproject.harvestlink.ui.HarvestViewModel
import com.techproject.harvestlink.util.hasInternetConnection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    harvestViewModel: HarvestViewModel
) {
    val isFarmer = harvestViewModel.homeUiState.isFarmer
    val buyer = harvestViewModel.buyerProfile
    val farmer = harvestViewModel.farmerProfile
    val context = LocalContext.current
    var isEditing by rememberSaveable { mutableStateOf(false) }
    
    // Form states
    var name by rememberSaveable(isFarmer, buyer, farmer) {
        mutableStateOf(if (isFarmer) farmer.name else buyer.name)
    }
    var email by rememberSaveable(isFarmer, buyer, farmer) {
        mutableStateOf(if (isFarmer) farmer.email else buyer.email)
    }
    var phoneNumber by rememberSaveable(isFarmer, buyer, farmer) {
        mutableStateOf(if (isFarmer) farmer.phoneNumber else buyer.phoneNumber)
    }
    var deliveryAddress by rememberSaveable(buyer) { mutableStateOf(buyer.deliveryAddress ?: "") }
    var preferredPaymentMethod by rememberSaveable(buyer) { mutableStateOf(buyer.preferredPaymentMethod ?: "") }
    var location by rememberSaveable(farmer) { mutableStateOf(farmer.location) }
    var farmName by rememberSaveable(farmer) { mutableStateOf(farmer.farmName ?: "") }

    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF8F3))
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isFarmer) "My Farmer Profile" else "My Profile",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B3D2F)
            )
            
            if (!isEditing) {
                IconButton(onClick = { isEditing = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = Color(0xFF1B3D2F))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Avatar placeholder
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9))
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = Color(0xFF1B3D2F)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isEditing) {
            if (isFarmer) {
                FarmerEditForm(
                    name = name,
                    onNameChange = { name = it },
                    email = email,
                    onEmailChange = { email = it },
                    phoneNumber = phoneNumber,
                    onPhoneNumberChange = { phoneNumber = it },
                    location = location,
                    onLocationChange = { location = it },
                    farmName = farmName,
                    onFarmNameChange = { farmName = it }
                )
            } else {
                ProfileEditForm(
                    name = name,
                    onNameChange = { name = it },
                    email = email,
                    onEmailChange = { email = it },
                    phoneNumber = phoneNumber,
                    onPhoneNumberChange = { phoneNumber = it },
                    deliveryAddress = deliveryAddress,
                    onDeliveryAddressChange = { deliveryAddress = it },
                    preferredPaymentMethod = preferredPaymentMethod,
                    onPreferredPaymentMethodChange = { preferredPaymentMethod = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (!hasInternetConnection(context)) {
                        Toast.makeText(
                            context,
                            "Internet connection required to update profile.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    if (isFarmer) {
                        val updatedFarmer = farmer.copy(
                            name = name,
                            email = email,
                            phoneNumber = phoneNumber,
                            location = location,
                            farmName = farmName.ifBlank { null }
                        )
                        harvestViewModel.updateFarmer(updatedFarmer)
                    } else {
                        val updatedBuyer = buyer.copy(
                            name = name,
                            email = email,
                            phoneNumber = phoneNumber,
                            deliveryAddress = deliveryAddress,
                            preferredPaymentMethod = preferredPaymentMethod
                        )
                        harvestViewModel.updateBuyer(updatedBuyer)
                    }
                    isEditing = false
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3D2F)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Changes")
            }
            
            TextButton(
                onClick = { isEditing = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", color = Color.Gray)
            }
        } else {
            if (isFarmer) {
                ProfileInfoSection(
                    label = "Full Name",
                    value = farmer.name.ifBlank { "N/A" }
                )
                ProfileInfoSection(
                    label = "Email Address",
                    value = farmer.email.ifBlank { "N/A" }
                )
                ProfileInfoSection(
                    label = "Phone Number",
                    value = farmer.phoneNumber.ifBlank { "N/A" }
                )
                ProfileInfoSection(
                    label = "Farm Name",
                    value = farmer.farmName?.ifBlank { "N/A" } ?: "N/A"
                )
                ProfileInfoSection(
                    label = "Location",
                    value = farmer.location.ifBlank { "N/A" }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Farm Stats", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        val ratingText = if (farmer.rating > 0) farmer.rating.toString() else "N/A"
                        val salesText = if (farmer.salesCompleted > 0) "${farmer.salesCompleted}" else "0"
                        Text(text = "Rating: $ratingText", fontSize = 14.sp)
                        Text(text = "Sales Completed: $salesText", fontSize = 14.sp)
                    }
                }
            } else {
                ProfileInfoSection(
                    label = "Full Name",
                    value = buyer.name.ifBlank { "N/A" }
                )
                ProfileInfoSection(
                    label = "Email Address",
                    value = buyer.email.ifBlank { "N/A" }
                )
                ProfileInfoSection(
                    label = "Phone Number",
                    value = buyer.phoneNumber.ifBlank { "N/A" }
                )
                ProfileInfoSection(
                    label = "Delivery Address",
                    value = buyer.deliveryAddress?.ifBlank { "N/A" } ?: "N/A"
                )
                ProfileInfoSection(
                    label = "Preferred Payment",
                    value = buyer.preferredPaymentMethod?.ifBlank { "N/A" } ?: "N/A"
                )

                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Order Stats", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        Text(text = "${buyer.orderHistoryCount} total orders completed", fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
        
        Spacer(modifier = Modifier.height(24.dp))

        // Actions
        Text(
            text = "Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B3D2F)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { harvestViewModel.logout() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1B3D2F))
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (!hasInternetConnection(context)) {
                    Toast.makeText(
                        context,
                        "Internet connection required to delete account.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }
                showDeleteDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEEBEE)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Delete Account", color = Color.Red)
        }
        
        Spacer(modifier = Modifier.height(48.dp))
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account?") },
            text = {
                Text(
                    text = stringResource(R.string.delete_account).trimIndent()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (!hasInternetConnection(context)) {
                            Toast.makeText(
                                context,
                                "Internet connection required to delete account.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@TextButton
                        }
                        harvestViewModel.deleteAccount {
                            harvestViewModel.logout()
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileInfoSection(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1B3D2F)
        )
    }
}

@Composable
fun ProfileEditForm(
    name: String, onNameChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    phoneNumber: String, onPhoneNumberChange: (String) -> Unit,
    deliveryAddress: String, onDeliveryAddressChange: (String) -> Unit,
    preferredPaymentMethod: String, onPreferredPaymentMethodChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = false
        )
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = deliveryAddress,
            onValueChange = onDeliveryAddressChange,
            label = { Text("Delivery Address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            minLines = 2
        )
        OutlinedTextField(
            value = preferredPaymentMethod,
            onValueChange = onPreferredPaymentMethodChange,
            label = { Text("Preferred Payment Method") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun FarmerEditForm(
    name: String, onNameChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    phoneNumber: String, onPhoneNumberChange: (String) -> Unit,
    location: String, onLocationChange: (String) -> Unit,
    farmName: String, onFarmNameChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = false
        )
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = farmName,
            onValueChange = onFarmNameChange,
            label = { Text("Farm Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = location,
            onValueChange = onLocationChange,
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
    }
}
