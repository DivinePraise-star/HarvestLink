package com.techproject.harvestlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.techproject.harvestlink.ui.theme.HarvestLinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HarvestLinkTheme {
                HarvestLinkApp()
            }
        }
    }
}

@Composable
fun HarvestLinkApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var selectedProduce by remember { mutableStateOf<Produce?>(null) }
    var showFilters by remember { mutableStateOf(false) }

    // Simple back navigation logic
    BackHandler(enabled = selectedProduce != null || showFilters) {
        if (showFilters) {
            showFilters = false
        } else {
            selectedProduce = null
        }
    }

    if (showFilters) {
        FilterScreen(
            onDismiss = { showFilters = false },
            onApply = { showFilters = false }
        )
    } else if (selectedProduce != null) {
        ProduceDetailScreen(
            produce = selectedProduce!!,
            onBackClick = { selectedProduce = null }
        )
    } else {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                AppDestinations.entries.forEach {
                    item(
                        icon = {
                            Icon(
                                it.icon,
                                contentDescription = it.label
                            )
                        },
                        label = { Text(it.label) },
                        selected = it == currentDestination,
                        onClick = { currentDestination = it }
                    )
                }
            }
        ) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Surface(modifier = Modifier.padding(innerPadding)) {
                    when (currentDestination) {
                        AppDestinations.HOME -> HomeScreen(
                            onProduceClick = { selectedProduce = it },
                            onFilterClick = { showFilters = true },
                            onNavigateToOrders = { currentDestination = AppDestinations.ORDERS },
                            onNavigateToMessages = { currentDestination = AppDestinations.MESSAGES }
                        )
                        AppDestinations.BROWSE -> BrowseScreen(
                            onProduceClick = { selectedProduce = it },
                            onFilterClick = { showFilters = true }
                        )
                        AppDestinations.ORDERS -> OrdersScreen()
                        AppDestinations.MESSAGES -> MessagesScreen()
                        AppDestinations.PROFILE -> PlaceholderScreen("Profile Screen")
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Text(text = "Welcome to $name", modifier = Modifier.fillMaxSize().padding(16.dp))
}

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
