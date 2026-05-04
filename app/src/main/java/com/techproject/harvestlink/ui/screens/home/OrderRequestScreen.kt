package com.techproject.harvestlink.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techproject.harvestlink.model.FarmerOrderRequest
import com.techproject.harvestlink.model.Produce
import com.techproject.harvestlink.ui.HarvestViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderRequestScreen(
    produce: Produce,
    harvestViewModel: HarvestViewModel,
    onBackClick: () -> Unit,
    onOrderSubmitted: () -> Unit
) {
    var quantity by remember { mutableStateOf("1") }
    var notes by remember { mutableStateOf("") }
    
    val pricePerUnit = produce.price
    val totalAmount = (quantity.toDoubleOrNull() ?: 0.0) * pricePerUnit

    val buyer = harvestViewModel.buyerProfile

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF8F3))
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Request Order",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B3D2F)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Produce Info
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                // Placeholder for image
                Box(modifier = Modifier.size(64.dp).background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = produce.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = "Ugx ${produce.price} per ${produce.unit}", color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Quantity (${produce.unit})", fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Additional Notes", fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("E.g. Delivery preferences...") }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Order Summary
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal")
                    Text("Ugx $totalAmount")
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Delivery Fee")
                    Text("Ugx 5000")
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", fontWeight = FontWeight.Bold)
                    Text("Ugx ${totalAmount + 5000}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { 
                val orderRequest = FarmerOrderRequest(
                    buyerName = buyer.name,
                    buyerLocation = buyer.deliveryAddress ?: "Not specified",
                    produceName = produce.name,
                    quantity = quantity.toIntOrNull() ?: 1,
                    offeredPricePerKg = produce.price.toInt(),
                    buyerNote = notes,
                    requestDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                    isResponded = false
                )
                //harvestViewModel.submitOrderRequest(orderRequest)
                onOrderSubmitted() 
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3D2F))
        ) {
            Text("Confirm Order Request", fontWeight = FontWeight.Bold)
        }
    }
}
