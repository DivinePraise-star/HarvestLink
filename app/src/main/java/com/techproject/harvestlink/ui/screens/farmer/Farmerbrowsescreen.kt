package com.techproject.harvestlink.ui.screens.farmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.model.Produce

private val categories = listOf("All", "Vegetables", "Fruits", "Grains", "Legumes", "Tubers")

@Composable
fun FarmerBrowseScreen() {
    var produceList by remember { mutableStateOf<List<Produce>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        isLoading = true
        error = null
        try {
            produceList = MoreData.fetchProduce()
        } catch (e: Exception) {
            error = e.localizedMessage ?: "Failed to load market listings"
        } finally {
            isLoading = false
        }
    }

    // Filter the list based on search and category
    val filtered = produceList.filter { produce ->
        val matchesSearch = searchQuery.isBlank() ||
                produce.name.contains(searchQuery, ignoreCase = true) ||
                produce.farmerName.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" ||
                produce.category.equals(selectedCategory, ignoreCase = true)
        matchesSearch && matchesCategory
    }

    // Market stats derived from the full unfiltered list
    val avgPrice = if (produceList.isEmpty()) 0.0
    else produceList.sumOf { it.price } / produceList.size
    val highestPrice = produceList.maxOfOrNull { it.price } ?: 0.0
    val lowestPrice = produceList.minOfOrNull { it.price } ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF8F3))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Market Browse",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B3D2F)
        )
        Text(
            text = "See what's being listed across the market",
            fontSize = 13.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search produce or farmer...", fontSize = 14.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1B3D2F),
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF1B3D2F))
                }
            }

            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: $error", color = Color.Red)
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {

                    // Market overview stat cards
                    item {
                        Text(
                            text = "Market overview",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1B3D2F)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MarketStatCard(
                                label = "Avg price/kg",
                                value = "Ugx ${"%,.0f".format(avgPrice)}",
                                backgroundColor = Color(0xFFE8F5E9),
                                textColor = Color(0xFF2E7D32),
                                modifier = Modifier.weight(1f)
                            )
                            MarketStatCard(
                                label = "Highest/kg",
                                value = "Ugx ${"%,.0f".format(highestPrice)}",
                                backgroundColor = Color(0xFFFFF3E0),
                                textColor = Color(0xFFE65100),
                                modifier = Modifier.weight(1f)
                            )
                            MarketStatCard(
                                label = "Lowest/kg",
                                value = "Ugx ${"%,.0f".format(lowestPrice)}",
                                backgroundColor = Color(0xFFE3F2FD),
                                textColor = Color(0xFF1565C0),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Category filter chips
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(categories) { category ->
                                val isSelected = category == selectedCategory
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCategory = category },
                                    label = { Text(category, fontSize = 13.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF1B3D2F),
                                        selectedLabelColor = Color.White,
                                        containerColor = Color.White,
                                        labelColor = Color(0xFF1B3D2F)
                                    )
                                )
                            }
                        }
                    }

                    // Results count
                    item {
                        Text(
                            text = "${filtered.size} listing${if (filtered.size != 1) "s" else ""} found",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }

                    // Empty state
                    if (filtered.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No listings match your search",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        items(filtered) { produce ->
                            MarketProduceCard(produce = produce)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarketStatCard(
    label: String,
    value: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = label, fontSize = 11.sp, color = textColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun MarketProduceCard(produce: Produce) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Produce initial avatar
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = produce.name.first().toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color(0xFF1B3D2F)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = produce.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1B3D2F)
                )
                Text(
                    text = "by ${produce.farmerName}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${produce.availableQuantity.toInt()} kg available",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Ugx ${"%,.0f".format(produce.price)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1B3D2F)
                )
                Text(
                    text = "per ${produce.unit}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                if (produce.category.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = Color(0xFFF1F8E9),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = produce.category,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = Color(0xFF33691E)
                        )
                    }
                }
            }
        }
    }
}