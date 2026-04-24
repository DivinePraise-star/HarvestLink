package com.techproject.harvestlink.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.model.Produce
import com.techproject.harvestlink.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    onProduceClick: (Produce) -> Unit,
    onFilterClick: () -> Unit
) {
    var produceList by remember { mutableStateOf<List<Produce>>(emptyList()) }
    var farmers by remember { mutableStateOf<List<User.Farmer>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            produceList = MoreData.fetchProduce()
            farmers = MoreData.fetchFarmers()
        } finally {
            isLoading = false
        }
    }

    val filteredProduce = produceList.filter {
        it.name.contains(searchQuery, ignoreCase = true) || 
        it.category.contains(searchQuery, ignoreCase = true)
    }

    val filteredFarmers = farmers.filter {
        it.name.contains(searchQuery, ignoreCase = true) || 
        it.location.contains(searchQuery, ignoreCase = true)
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF1B3D2F))
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFBF8F3)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "Fresh from local farms",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B3D2F)
                )
            }

            // Search Bar and Filter Icon
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search produce or farms...") },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1B3D2F),
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onFilterClick,
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filter",
                            tint = Color(0xFF1B3D2F)
                        )
                    }
                }
            }

            // Featured Farmers Section
            if (filteredFarmers.isNotEmpty()) {
                item {
                    Column {
                        Text(
                            text = "Featured Farmers",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp),
                            color = Color(0xFF1B3D2F)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(filteredFarmers) { farmer ->
                                FarmerCard(
                                    farmer = farmer,
                                    modifier = Modifier.width(280.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Available Produce Section
            if (filteredProduce.isNotEmpty()) {
                item {
                    Text(
                        text = "Available Produce",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp),
                        color = Color(0xFF1B3D2F)
                    )
                }
                
                // Vertical Grid for Produce
                items(filteredProduce.windowed(2, 2, true)) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEach { produce ->
                            ProduceCard(
                                produce = produce,
                                modifier = Modifier.weight(1f),
                                onClick = { onProduceClick(produce) }
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            
            if (filteredFarmers.isEmpty() && filteredProduce.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("No results found for \"$searchQuery\"", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProduceCard(
    produce: Produce,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        when {
                            produce.category.contains("Vegetable", true) -> Color(0xFFE8F5E9)
                            produce.category.contains("Fruit", true) -> Color(0xFFFFF3E0)
                            produce.category.contains("Grain", true) -> Color(0xFFF3E5F5)
                            else -> Color(0xFFF5F5F5)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for Image
                Text(
                    text = produce.name.take(1),
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.Gray.copy(alpha = 0.3f)
                )
                
                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = produce.rating.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = produce.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    color = Color(0xFF1B3D2F)
                )
                Text(
                    text = "UGX ${produce.price}/${produce.unit}",
                    fontSize = 13.sp,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun FarmerCard(
    farmer: User.Farmer,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                Text(farmer.name.take(1), fontWeight = FontWeight.Bold, color = Color.Gray)
                if (farmer.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .align(Alignment.BottomEnd)
                            .background(Color.White, CircleShape)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF4CAF50), CircleShape)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = farmer.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    color = Color(0xFF1B3D2F)
                )
                Text(
                    text = "📍 ${farmer.location}",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${farmer.rating}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${farmer.salesCompleted} sales",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
