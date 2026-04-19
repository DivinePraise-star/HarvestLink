package com.techproject.harvestlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.techproject.harvestlink.ui.theme.HarvestLinkTheme

// Authentication states
enum class AuthState {
    SPLASH,
    ONBOARDING,
    WELCOME,
    SIGN_IN,
    SIGN_UP,
    FORGOT_PASSWORD,
    AUTHENTICATED
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HarvestLinkTheme {
                HarvestLinkAppWithAuth()
            }
        }
    }
}

@Composable
fun HarvestLinkAppWithAuth() {
    var authState by remember { mutableStateOf(AuthState.SPLASH) }
    var userRole by rememberSaveable { mutableStateOf<String?>(null) }
    var hasSeenOnboarding by rememberSaveable { mutableStateOf(false) }

    when (authState) {
        AuthState.SPLASH -> {
            SplashScreen(
                onSplashComplete = {
                    if (hasSeenOnboarding) {
                        authState = AuthState.WELCOME
                    } else {
                        authState = AuthState.ONBOARDING
                    }
                }
            )
        }

        AuthState.ONBOARDING -> {
            OnboardingScreen(
                onFinish = {
                    hasSeenOnboarding = true
                    authState = AuthState.WELCOME
                }
            )
        }

        AuthState.WELCOME -> {
            WelcomeScreen(
                onSignInClick = { authState = AuthState.SIGN_IN },
                onSignUpClick = { authState = AuthState.SIGN_UP },
                onGuestClick = {
                    userRole = "buyer"
                    authState = AuthState.AUTHENTICATED
                }
            )
        }

        AuthState.SIGN_IN -> {
            SignInScreen(
                onBackClick = { authState = AuthState.WELCOME },
                onSignInSuccess = { role ->
                    userRole = role
                    authState = AuthState.AUTHENTICATED
                },
                onForgotPassword = { authState = AuthState.FORGOT_PASSWORD }
            )
        }

        AuthState.SIGN_UP -> {
            SignUpScreen(
                onBackClick = { authState = AuthState.WELCOME },
                onSignUpSuccess = { role ->
                    userRole = role
                    authState = AuthState.AUTHENTICATED
                }
            )
        }

        AuthState.FORGOT_PASSWORD -> {
            ForgotPasswordScreen(
                onBackClick = { authState = AuthState.SIGN_IN },
                onResetSent = { authState = AuthState.SIGN_IN }
            )
        }

        AuthState.AUTHENTICATED -> {
            // Launch the main app with the user's role
            MainAppContent(userRole = userRole)
        }
    }
}

@Composable
fun MainAppContent(userRole: String?) {
    var isFarmerMode by rememberSaveable { mutableStateOf(userRole == "farmer") }
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var selectedProduce by remember { mutableStateOf<Produce?>(null) }
    var selectedRequest by remember { mutableStateOf<FarmerOrderRequest?>(null) }
    var showFilters by remember { mutableStateOf(false) }

    BackHandler(enabled = selectedProduce != null || showFilters || selectedRequest != null) {
        when {
            showFilters -> showFilters = false
            selectedProduce != null -> selectedProduce = null
            selectedRequest != null -> selectedRequest = null
        }
    }

    when {
        showFilters -> FilterScreen(
            onDismiss = { showFilters = false },
            onApply = { showFilters = false }
        )
        selectedProduce != null -> ProduceDetailScreen(
            produce = selectedProduce!!,
            onBackClick = { selectedProduce = null }
        )
        selectedRequest != null -> FarmerOrderRequestScreen(
            request = selectedRequest!!,
            onBackClick = { selectedRequest = null },
            onConfirm = { selectedRequest = null },
            onDecline = { selectedRequest = null }
        )
        else -> NavigationSuiteScaffold(
            navigationSuiteItems = {
                AppDestinations.entries.forEach {
                    item(
                        icon = { Icon(it.icon, contentDescription = it.label) },
                        label = { Text(it.label) },
                        selected = it == currentDestination,
                        onClick = { currentDestination = it }
                    )
                }
            }
        ) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Surface(modifier = Modifier.padding(innerPadding)) {
                    if (isFarmerMode) {
                        // Farmer Mode Screens
                        when (currentDestination) {
                            AppDestinations.HOME -> FarmerDashboardScreen(
                                onOrderRequestClick = { selectedRequest = it }
                            )
                            AppDestinations.ORDERS -> FarmerDashboardScreen(
                                onOrderRequestClick = { selectedRequest = it }
                            )
                            AppDestinations.PROFILE -> ModeToggleScreen(
                                isFarmerMode = true,
                                onToggle = { isFarmerMode = false }
                            )
                            else -> PlaceholderScreen("${currentDestination.label} (Farmer)")
                        }
                    } else {
                        // Buyer Mode Screens
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
                            AppDestinations.PROFILE -> ModeToggleScreen(
                                isFarmerMode = false,
                                onToggle = { isFarmerMode = true }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModeToggleScreen(isFarmerMode: Boolean, onToggle: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isFarmerMode) "Currently in Farmer View" else "Currently in Buyer View",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF1B3D2F)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onToggle,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3D2F)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(52.dp)
        ) {
            Text(
                text = if (isFarmerMode) "Switch to Buyer View" else "Switch to Farmer View"
            )
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

enum class AppDestinations(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    BROWSE("Browse", Icons.Default.Search),
    ORDERS("Orders", Icons.Default.ShoppingBag),
    MESSAGES("Messages", Icons.Default.Mail),
    PROFILE("Profile", Icons.Default.AccountBox),
}
