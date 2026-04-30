package com.techproject.harvestlink.ui.screens.farmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.model.FarmerOrderRequest

@Composable
fun FarmerOrderRequestScreen(
    requestId: String?,
    onBackClick: () -> Unit,
    onConfirm: () -> Unit = {},
    onDecline: () -> Unit = {}
) {
    var request by remember { mutableStateOf<FarmerOrderRequest?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(requestId) {
        isLoading = true
        error = null
        try {
            val requests = MoreData.fetchFarmerOrderRequests()
            request = requests.find { it.id == requestId }
            if (request == null) error = "Request not found"
        } catch (e: Exception) {
            error = e.localizedMessage ?: "Unknown error"
        } finally {
            isLoading = false
        }
    }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showDeclineDialog by remember { mutableStateOf(false) }

    val totalValue = request?.let { it.quantity * it.offeredPricePerKg } ?: 0

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm this order?") },
            text = {
                Text(
                    "You're accepting ${request?.quantity ?: "?"} kg of ${request?.produceName ?: "?"} " +
                            "from ${request?.buyerName ?: "?"} at Ugx ${request?.offeredPricePerKg ?: "?"}/kg."
                )
            },
            confirmButton = {
                Button(
                    onClick = { showConfirmDialog = false; onConfirm() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3D2F))
                ) { Text("Yes, confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showDeclineDialog) {
        AlertDialog(
            onDismissRequest = { showDeclineDialog = false },
            title = { Text("Decline this request?") },
            text = { Text("The buyer will be notified that their request was not accepted.") },
            confirmButton = {
                Button(
                    onClick = { showDeclineDialog = false; onDecline() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) { Text("Decline") }
            },
            dismissButton = {
                TextButton(onClick = { showDeclineDialog = false }) { Text("Cancel") }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF8F3))
    ) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: $error", color = Color.Red)
                }
            }
            request == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Order request not found", color = Color.Red)
                }
            }
            else -> {
                val req = request!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
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
                                text = "Order Request",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = req.requestDate,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-16).dp)
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .background(Color(0xFFFBF8F3))
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Buyer card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE8F5E9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = req.buyerName.first().toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = Color(0xFF1B3D2F)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = req.buyerName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = Color(0xFF1B3D2F)
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(text = req.buyerLocation, fontSize = 13.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }

                        // Order details card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Order Details",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF1B3D2F),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                RequestDetailRow("Produce", req.produceName)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray)
                                RequestDetailRow("Quantity", "${req.quantity} kg")
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray)
                                RequestDetailRow("Offered Price", "Ugx ${"%,d".format(req.offeredPricePerKg)}/kg")
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Total Value", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1B3D2F))
                                    Text(
                                        text = "Ugx ${"%,d".format(totalValue)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color(0xFF1B3D2F)
                                    )
                                }
                            }
                        }

                        // Buyer note card
                        if (req.buyerNote.isNotBlank()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                                elevation = CardDefaults.cardElevation(0.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Note from buyer",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFFE65100)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = req.buyerNote, fontSize = 14.sp, color = Color(0xFF1B3D2F), lineHeight = 20.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Action buttons
                        Button(
                            onClick = { showConfirmDialog = true },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3D2F))
                        ) {
                            Text("Confirm Order", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = { showDeclineDialog = true },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828))
                        ) {
                            Text("Decline Request", fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RequestDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1B3D2F))
    }
}