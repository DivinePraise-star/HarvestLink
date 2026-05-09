package com.techproject.harvestlink.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.techproject.harvestlink.ui.screens.home.FarmerProfileScreen
import com.techproject.harvestlink.ui.screens.home.FilterScreen
import com.techproject.harvestlink.ui.screens.home.HomeScreen
import com.techproject.harvestlink.ui.screens.home.NotificationScreen
import com.techproject.harvestlink.ui.screens.home.ProduceDetailScreen
import com.techproject.harvestlink.ui.screens.order.PlaceOrderScreen
import com.techproject.harvestlink.ui.screens.order.TrackOrderScreen
import kotlinx.coroutines.launch
import com.techproject.harvestlink.ui.screens.farmer.AddListingScreen
import com.techproject.harvestlink.ui.screens.farmer.FarmerBrowseScreen
import com.techproject.harvestlink.ui.screens.farmer.FarmerOrdersListScreen
import com.techproject.harvestlink.data.SupabaseService
import io.github.jan.supabase.auth.auth
@Composable
fun HarvestLinkApp() {
    val navController = rememberNavController()
    val harvestViewModel: HarvestViewModel = viewModel(factory = HarvestViewModel.Factory)

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
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val isAuthenticated = harvestViewModel.currentUserId.isNotBlank()
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
                    },
                    onNewListingClick = {
                        navController.navigate("AddListingScreen")
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
                    onNavigateToNotifications = { navController.navigate("NotificationScreen") },
                    onSeeAllClick = { navController.navigate(AppDestinations.BROWSE.name) }
                )
            }
        }
        composable(route = AppDestinations.BROWSE.name) {
            if (harvestViewModel.homeUiState.isFarmer) {
                FarmerBrowseScreen()  // ← swap in
            } else {
                BrowseScreen(
                    onProduceClick = {
                        harvestViewModel.updateCurrentProduce(it)
                        navController.navigate("ProduceDetailScreen")
                    },
                    onFilterClick = { navController.navigate("FilterScreen") }
                )
            }
        }
        composable(route = AppDestinations.ORDERS.name) {
            if (!isAuthenticated) {
                AuthRequiredScreen(
                    title = "Orders",
                    message = "Sign in to track and manage your orders.",
                    onSignInClick = { harvestViewModel.requireAuthentication() }
                )
            } else if (harvestViewModel.homeUiState.isFarmer) {
                FarmerOrdersListScreen(  // ← swap in
                    onOrderRequestClick = {
                        navController.navigate("FarmerOrderRequestScreen/${it.id}")
                        harvestViewModel.toggleNavBar()
                    }
                )
            } else {
                TrackOrderScreen(
                    isFarmer = false,
                    onContactFarmer = {
                        navController.navigate(AppDestinations.MESSAGES.name)
                    },
                    onReorder = { produceName ->
                        val produce = harvestViewModel.produceList.find { it.name == produceName }
                        if (produce != null) {
                            harvestViewModel.updateCurrentProduce(produce)
                            navController.navigate("OrderRequestScreen")
                        }
                    }
                )
            }
        }
        composable(route = AppDestinations.MESSAGES.name){
            if (!isAuthenticated) {
                AuthRequiredScreen(
                    title = "Messages",
                    message = "Sign in to view and send messages.",
                    onSignInClick = { harvestViewModel.requireAuthentication() }
                )
            } else {
                ChatList(navController)
            }
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
            if (!isAuthenticated) {
                AuthRequiredScreen(
                    title = "Messages",
                    message = "Sign in to view and send messages.",
                    onSignInClick = { harvestViewModel.requireAuthentication() }
                )
            } else {
                ChatDetails(
                    onClick = {navController.popBackStack()}
                )
            }
        }
        composable(route = AppDestinations.PROFILE.name){
            if (!isAuthenticated) {
                AuthRequiredScreen(
                    title = "Profile",
                    message = "Sign in to manage your profile.",
                    onSignInClick = { harvestViewModel.requireAuthentication() }
                )
            } else {
                ProfileScreen(harvestViewModel = harvestViewModel)
            }
        }
        composable(route = "FilterScreen") {
            FilterScreen (
                harvestViewModel = harvestViewModel,
                onDismiss = { navController.popBackStack() },
                onApply = { navController.popBackStack() }
            )
        }
        composable(route = "AddListingScreen") {
            AddListingScreen(
                farmerId = SupabaseService.client.auth.currentUserOrNull()?.id ?: "",
                onBackClick = { navController.popBackStack() },
                onListingAdded = { navController.popBackStack() }
            )
        }
        composable(route = "ProduceDetailScreen") {
            val produce = harvestViewModel.homeUiState.currentProduce
            if (produce != null) {
                ProduceDetailScreen(
                    produce = produce,
                    onBackClick = { navController.popBackStack() },
                    onViewProfileClick = { farmerId ->
                        val farmer = harvestViewModel.farmersList.find { it.id == farmerId }
                        if (farmer != null) {
                            harvestViewModel.updateCurrentFarmer(farmer)
                            navController.navigate("FarmerProfileScreen")
                        }
                    },
                    onMessage = {
                        if (!isAuthenticated) {
                            Toast.makeText(
                                context,
                                "Please sign in or sign up to message farmers.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            scope.launch {
                                val conversationId = harvestViewModel.getOrCreateConversation(produce.farmerId)
                                navController.navigate("${AppDestinations.MESSAGES.name}/$conversationId/${produce.farmerId}")
                            }
                        }
                    },
                    onRequest = {
                        if (!isAuthenticated) {
                            Toast.makeText(
                                context,
                                "Please sign in or sign up to request orders.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            navController.navigate("PlaceOrderScreen/${produce.farmerId}")
                        }
                    }
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No produce selected.")
                }
            }
        }
        composable(route = "FarmerProfileScreen") {
            val farmer = harvestViewModel.homeUiState.currentFarmer
            if (farmer != null) {
                FarmerProfileScreen(
                    farmer = farmer,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
        composable(route = "PlaceOrderScreen/{farmerId}"){
            val farmerId = it.arguments?.getString("farmerId")
            PlaceOrderScreen(
                harvestViewModel,
                farmerId = farmerId,
                onSuccess = {value ->
                    if(value > 0){
                        navController.navigate(AppDestinations.HOME.name)
                    }
                }
            )
        }
        composable(route = "NotificationScreen") {
            NotificationScreen(
                harvestViewModel = harvestViewModel,
                onBackClick = { navController.popBackStack() }
            )
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
fun AuthRequiredScreen(
    title: String,
    message: String,
    onSignInClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onSignInClick) {
                Text("Sign In or Sign Up")
            }
        }
    }
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
