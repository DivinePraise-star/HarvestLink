package com.techproject.harvestlink.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.techproject.harvestlink.ui.screens.AppDestinations
import com.techproject.harvestlink.ui.screens.ProfileScreen
import com.techproject.harvestlink.ui.screens.auth.AuthScreen
import com.techproject.harvestlink.ui.screens.chat.ChatDetails
import com.techproject.harvestlink.ui.screens.chat.ChatList
import com.techproject.harvestlink.ui.screens.farmer.FarmerDashboardScreen
import com.techproject.harvestlink.ui.screens.farmer.FarmerOrderRequestScreen
import com.techproject.harvestlink.ui.screens.home.BrowseScreen
import com.techproject.harvestlink.ui.screens.home.FilterScreen
import com.techproject.harvestlink.ui.screens.home.HomeScreen
import com.techproject.harvestlink.ui.screens.home.ProduceDetailScreen
import com.techproject.harvestlink.ui.screens.order.TrackOrderScreen

@Composable
fun HarvestLinkApp() {
    val navController = rememberNavController()
    val harvestViewModel: HarvestViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if(harvestViewModel.homeUiState.beginner){
        AuthScreen(
            harvestViewModel = harvestViewModel
        )
    }else{
        Scaffold(
            bottomBar = {
                if(harvestViewModel.homeUiState.showNavBar){
                    BottomNavBar(navController,currentRoute )
                }
            }
        ) { paddingValues ->
            MainScreen(
                navController = navController,
                harvestViewModel = harvestViewModel,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }

}

@Composable
fun MainScreen(
    navController: NavHostController,
    harvestViewModel: HarvestViewModel,
    modifier: Modifier = Modifier
){
    NavHost(
        navController = navController,
        startDestination = AppDestinations.HOME.name,
        modifier = modifier
    ){
        composable(route = AppDestinations.HOME.name) {
            if(harvestViewModel.homeUiState.isFarmer){
                FarmerDashboardScreen(
                    onOrderRequestClick = {
                        navController.navigate("FarmerOrderRequestScreen/${it.id}")
                        harvestViewModel.toggleNavBar()
                    }
                )
            }else{
                HomeScreen (
                    harvestViewModel = harvestViewModel,
                    onProduceClick = {
                        navController.navigate("ProduceDetailScreen")
                        harvestViewModel.updateCurrentProduce(it)
                    },
                    onFilterClick = {navController.navigate("FilterScreen")},
                    onNavigateToOrders = { navController.navigate(AppDestinations.ORDERS.name) },
                    onNavigateToMessages = { navController.navigate(AppDestinations.MESSAGES.name) },
                    onSeeAllClick = { navController.navigate(AppDestinations.BROWSE.name) }
                )
            }
        }
        composable(route = AppDestinations.BROWSE.name){
            if(harvestViewModel.homeUiState.isFarmer){
                PlaceholderScreen("Welcome to Browse (Farmer)")
            }else{
                BrowseScreen(
                    onProduceClick = { 
                        harvestViewModel.updateCurrentProduce(it)
                        navController.navigate("ProduceDetailScreen")
                    },
                    onFilterClick = { navController.navigate("FilterScreen") }
                )
            }
        }
        composable(route = AppDestinations.ORDERS.name){
            if(harvestViewModel.homeUiState.isFarmer){
                FarmerDashboardScreen(
                    onOrderRequestClick = {
                        navController.navigate("FarmerOrderRequestScreen/${it.id}")
                        harvestViewModel.toggleNavBar()
                    }
                )
            }else{ TrackOrderScreen() }
        }
        composable(route = AppDestinations.MESSAGES.name){
            ChatList(navController)
        }
        composable(
            route = "${AppDestinations.MESSAGES.name}/{conversationId}/{recipientId}",
            arguments =listOf(
                navArgument("conversationId"){
                    type = NavType.StringType
                },
                navArgument("recipientId"){
                    type = NavType.StringType
                },
            )
        ){
            ChatDetails(
                onClick = {navController.popBackStack()}
            )
        }
        composable(route = AppDestinations.PROFILE.name){
            ProfileScreen(harvestViewModel = harvestViewModel)
        }
        composable(route = "FilterScreen") {
            FilterScreen (
                harvestViewModel = harvestViewModel,
                onDismiss = { navController.popBackStack() },
                onApply = { navController.popBackStack() }
            )
        }
        composable(route = "ProduceDetailScreen") {
            val produce = harvestViewModel.homeUiState.currentProduce
            if (produce != null) {
                ProduceDetailScreen(
                    produce = produce,
                    onBackClick = { navController.popBackStack() }
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No produce selected.")
                }
            }
        }
        composable(route = "FarmerOrderRequestScreen/{requestId}") { entry ->
            val requestId = entry.arguments?.getString("requestId")
            FarmerOrderRequestScreen(
                requestId = requestId,
                onBackClick = {
                    navController.popBackStack()
                    harvestViewModel.toggleNavBar()
                })
        }
    }
}


@Composable
fun ScreenHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .fillMaxWidth()
    )
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Welcome to $name",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun BottomNavBar(
    navController: NavHostController,
    currentRoute: String?
) {
    NavigationBar {
        AppDestinations.entries.forEach {
            NavigationBarItem(
                icon = {
                    Icon(
                        it.icon,
                        contentDescription = it.label
                    )
                },
                label = { Text(it.label) },
                selected = it.name == currentRoute,
                onClick = { navController.navigate(it.name) }
            )
        }
    }
}
