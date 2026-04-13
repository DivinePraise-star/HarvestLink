package com.techproject.harvestlink

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterScreen(
    onDismiss: () -> Unit,
    onApply: () -> Unit
) {
    var priceRange by remember { mutableStateOf(0f..500f) }
    var distanceRange by remember { mutableStateOf(0f..50f) }
    val categories = listOf("Vegetables", "Fruits", "Grains", "Dairy", "Organic")
    val selectedCategories = remember { mutableStateListOf<String>() }

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
                selectedCategories.clear()
                priceRange = 0f..500f
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
                        selected = selectedCategories.contains(category),
                        onClick = {
                            if (selectedCategories.contains(category)) {
                                selectedCategories.remove(category)
                            } else {
                                selectedCategories.add(category)
                            }
                        },
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
                onValueChange = { priceRange = it },
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
