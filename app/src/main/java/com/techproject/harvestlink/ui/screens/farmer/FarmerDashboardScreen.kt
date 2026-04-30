package com.techproject.harvestlink.ui.screens.farmer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
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
import com.techproject.harvestlink.model.FarmerListing
import com.techproject.harvestlink.model.FarmerOrderRequest
import com.techproject.harvestlink.model.ListingStatus

@Composable
fun FarmerDashboardScreen(
    onOrderRequestClick: (FarmerOrderRequest) -> Unit = {}
) {
    var listings by remember { mutableStateOf<List<FarmerListing>>(emptyList()) }
    var orderRequests by remember { mutableStateOf<List<FarmerOrderRequest>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        error = null
        try {
            listings = MoreData.fetchFarmerListings()
            orderRequests = MoreData.fetchFarmerOrderRequests()
        } catch (e: Exception) {
            error = e.localizedMessage ?: "Unknown error"
        } finally {
            isLoading = false
        }
    }

    val activeListings = listings.count { it.status == ListingStatus.ACTIVE }
    val pendingRequests = orderRequests.count { !it.isResponded }
    val totalEarnings = listings.filter { it.status == ListingStatus.SOLD }.sumOf { it.pricePerUnit * it.quantitySold }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF8F3))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF1B3D2F),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Kasese, Uganda",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1B3D2F)
                )
            }
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }
        if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $error", color = Color.Red)
            }
            return@Column
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Column {
                    Text(text = "Welcome back,", fontSize = 16.sp, color = Color.Gray)
                    Text(
                        text = "Ritah Patience!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B3D2F)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Active Listings", fontSize = 12.sp, color = Color(0xFF2E7D32))
                            Text(
                                text = activeListings.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B3D2F)
                            )
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "New Requests", fontSize = 12.sp, color = Color(0xFFE65100))
                            Text(
                                text = pendingRequests.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B3D2F)
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B3D2F))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Earnings",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Ugx ${"%,d".format(totalEarnings.toInt())}",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Listing", color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Incoming Requests",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B3D2F)
                )
            }
            items(orderRequests) { request ->
                OrderRequestCard(request = request, onClick = { onOrderRequestClick(request) })
            }
            item {
                Text(
                    text = "My Listings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B3D2F)
                )
            }
            items(listings) { listing ->
                FarmerListingCard(listing)
            }
        }
    }
}

@Composable
fun OrderRequestCard(request: FarmerOrderRequest, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = request.buyerName.first().toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1B3D2F)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = request.buyerName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1B3D2F))
                Text(text = "${request.quantity} kg of ${request.produceName}", fontSize = 13.sp, color = Color.Gray)
                Text(
                    text = "Ugx ${"%,d".format(request.offeredPricePerKg)}/kg · ${request.requestDate}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            if (!request.isResponded) {
                Surface(color = Color(0xFFFFF3E0), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = "NEW",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                }
            }
        }
    }
}

@Composable
fun FarmerListingCard(listing: FarmerListing) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(52.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFFBF8F3)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = listing.produceName.first().toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color(0xFF1B3D2F)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = listing.produceName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1B3D2F))
                Text(
                    text = "${listing.quantityAvailable} kg · Ugx ${"%,d".format(listing.pricePerUnit.toInt())}/kg",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
            ListingStatusBadge(listing.status)
        }
    }
}

@Composable
fun ListingStatusBadge(status: ListingStatus) {
    val (bg, fg) = when (status) {
        ListingStatus.ACTIVE -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        ListingStatus.PENDING -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        ListingStatus.SOLD -> Color(0xFFEEEEEE) to Color(0xFF616161)
    }
    Surface(color = bg, shape = RoundedCornerShape(8.dp)) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = fg
        )
    }
}