package com.techproject.harvestlink.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techproject.harvestlink.model.Farmer

@Composable
fun FarmerProfileScreen(
    farmer: Farmer,
    onBackClick: () -> Unit
) {
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
                text = "Farmer Profile",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B3D2F)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = farmer.name.take(1),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B3D2F)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = farmer.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B3D2F)
                )

                Text(
                    text = farmer.farmName ?: "HarvestLink Partner",
                    color = Color.Gray,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoStat(label = "Rating", value = farmer.rating.toString(), icon = Icons.Default.Star)
                    InfoStat(label = "Sales", value = "${farmer.salesCompleted}+", icon = null)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Details",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B3D2F)
        )

        Spacer(modifier = Modifier.height(12.dp))

        DetailRow(label = "Location", value = farmer.location)
        DetailRow(label = "Phone", value = farmer.phoneNumber)
        DetailRow(label = "Email", value = farmer.email)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "About",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B3D2F)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This farmer has been part of HarvestLink for a while, providing fresh produce to the community. They are committed to quality and sustainable farming practices.",
            color = Color.Gray,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun InfoStat(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Text(text = label, color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.Medium, color = Color(0xFF1B3D2F))
    }
}
