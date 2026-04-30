package com.techproject.harvestlink.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.model.OrderStatus
import com.techproject.harvestlink.model.Produce
import com.techproject.harvestlink.ui.HarvestViewModel
import com.techproject.harvestlink.ui.screens.chat.ChatListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    harvestViewModel: HarvestViewModel,
    onProduceClick: (Produce) -> Unit,
    onFilterClick: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onSeeAllClick: () -> Unit,
) {
    val chatListViewModel: ChatListViewModel = viewModel(factory = ChatListViewModel.Factory)
    val chatState = chatListViewModel.chatListUiState.collectAsState().value
    val unreadMessagesCount = chatState.conversations.size // Assuming each conversation has unread

    val buyerProfile = harvestViewModel.buyerProfile

    val produceList = harvestViewModel.produceList
    val farmers = harvestViewModel.farmersList
    val activeOrdersCount = harvestViewModel.activeOrdersCount
    
    // Total notifications = active orders + messages + general notifications
    val notificationCount = activeOrdersCount + unreadMessagesCount + harvestViewModel.notificationCount

    val isLoading = harvestViewModel.produceLoading
    val loadError = harvestViewModel.produceError

    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    
    val userName = buyerProfile.name
    // val unreadMessagesCount = 9 // Removed duplicate

    val categories = harvestViewModel.categories

    val filteredProduce = produceList.filter {
        val matchesSearch = it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || it.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF8F3))
            .padding(horizontal = 16.dp)
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }
        if (loadError != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Could not load data: $loadError", color = Color.Red)
            }
            return@Column
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Top Bar: Location and Notifications
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
                onClick = onNavigateToNotifications,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape)
            ) {
                BadgedBox(
                    badge = {
                        if (notificationCount > 0) {
                            Badge {
                                Text(notificationCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Welcome, Section
            item {
                Column {
                    Text(
                        text = "Welcome back,",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "$userName!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B3D2F)
                    )
                }
            }

            // Dashboard Summary Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Active Orders Summary
                    Card(
                        modifier = Modifier.weight(1f).clickable { onNavigateToOrders() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Active Orders", fontSize = 12.sp, color = Color(0xFF2E7D32))
                            Text(text = activeOrdersCount.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B3D2F))
                        }
                    }

                    // Unread Messages Summary
                    Card(
                        modifier = Modifier.weight(1f).clickable { onNavigateToMessages() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Messages", fontSize = 12.sp, color = Color(0xFF1565C0))
                            Text(text = unreadMessagesCount.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B3D2F))
                        }
                    }
                }
            }

            // Fresh from local farms Title
            item {
                Text(
                    text = "Fresh from local farms",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B3D2F)
                )
            }

            // Search and Filter
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search produce...") },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
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
                        Icon(Icons.Default.Tune, contentDescription = "Filter", tint = Color(0xFF1B3D2F))
                    }
                }
            }

            // Categories
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF1B3D2F),
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = Color.Gray
                            ),
                            border = null
                        )
                    }
                }
            }

            // Available Produce
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Top Picks",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B3D2F)
                    )
                    TextButton(onClick = onSeeAllClick) {
                        Text("See all", color = Color(0xFF1B3D2F))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filteredProduce) { produce ->
                        ProduceCard(
                            produce = produce,
                            modifier = Modifier.width(160.dp),
                            onClick = { onProduceClick(produce) }
                        )
                    }
                }
            }

            // Featured Farmers
            item {
                Text(
                    text = "Nearby Farmers",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B3D2F),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    farmers.forEach { farmer ->
                        FarmerCard(farmer)
                    }
                }
            }
        }
    }
}
