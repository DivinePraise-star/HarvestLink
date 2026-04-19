package com.techproject.harvestlink.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    BROWSE("Browse", Icons.Default.Search),
    ORDERS("Orders", Icons.Default.ShoppingBag),
    MESSAGES("Messages", Icons.Default.Mail),
    PROFILE("Profile", Icons.Default.AccountBox),
}