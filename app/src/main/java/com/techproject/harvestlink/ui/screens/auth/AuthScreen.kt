package com.techproject.harvestlink.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.techproject.harvestlink.ui.HarvestViewModel


@Composable
fun AuthScreen(
    harvestViewModel: HarvestViewModel
) {
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
            harvestViewModel.toggleBeginner()
        }
    }
}

enum class AuthState {
    SPLASH,
    ONBOARDING,
    WELCOME,
    SIGN_IN,
    SIGN_UP,
    FORGOT_PASSWORD,
    AUTHENTICATED
}