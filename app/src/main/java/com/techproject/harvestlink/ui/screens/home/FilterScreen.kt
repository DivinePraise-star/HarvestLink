package com.techproject.harvestlink.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techproject.harvestlink.ui.HarvestViewModel

/**
 * 🔍 Search + filter combined (name + category)
 * 🧠 Use Filterable interface (classic Android way)
 * ⚡ Optimize with DiffUtil
 * 🧩 MVVM with LiveData / Flow filterin
 */


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterScreen(
    harvestViewModel: HarvestViewModel,
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {

    var distanceRange by remember { mutableStateOf(0f..50f) }
    val filterUiState = harvestViewModel.filterUiState
    val priceRange = filterUiState.priceRange
    // Replace static produceList with ViewModel-provided categories
    val categories = harvestViewModel.categories

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = {
                //REST
                distanceRange = 0f..50f
            }) {
                Text("Reset", color = Color.Red)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Category Filter
            Text(
                text = "Crop Category",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF1B3D2F)
            )
            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = filterUiState.categories.contains(category),
                        onClick = { harvestViewModel.toggleCategory(category) },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF1B3D2F),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Price Range Filter
            Text(
                text = "Price Range ($)",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF1B3D2F)
            )
            Text(
                text = "$${priceRange.start.toInt()} - $${priceRange.endInclusive.toInt()}",
                color = Color.Gray
            )
            RangeSlider(
                value = priceRange,
                onValueChange = { harvestViewModel.updatePriceRange(it) },
                valueRange = 0f..1000f,
                colors = SliderDefaults.colors(
                    activeTrackColor = Color(0xFF1B3D2F),
                    thumbColor = Color(0xFF1B3D2F)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Distance Filter
            Text(
                text = "Distance (km)",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF1B3D2F)
            )
            Text(
                text = "Within ${distanceRange.endInclusive.toInt()} km",
                color = Color.Gray
            )
            Slider(
                value = distanceRange.endInclusive,
                onValueChange = { distanceRange = 0f..it },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    activeTrackColor = Color(0xFF1B3D2F),
                    thumbColor = Color(0xFF1B3D2F)
                )
            )
        }

        // Apply Button
        Button(
            onClick = onApply,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3D2F))
        ) {
            Text("Apply Filters", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
