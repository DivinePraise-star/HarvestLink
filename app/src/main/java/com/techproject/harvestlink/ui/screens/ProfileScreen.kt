package com.techproject.harvestlink.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.techproject.harvestlink.ui.HarvestViewModel


@Composable
fun ProfileScreen(
    harvestViewModel: HarvestViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (harvestViewModel.homeUiState.isFarmer) "Currently in Farmer View" else "Currently in Buyer View",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF1B3D2F)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { harvestViewModel.toggleFarmer() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3D2F)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(52.dp)
        ) {
            Text(
                text = if (harvestViewModel.homeUiState.isFarmer) "Switch to Buyer View" else "Switch to Farmer View"
            )
        }
    }
}

