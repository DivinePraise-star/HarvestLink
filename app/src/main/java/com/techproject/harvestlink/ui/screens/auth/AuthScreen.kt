package com.techproject.harvestlink.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.techproject.harvestlink.ui.AuthState
import com.techproject.harvestlink.ui.HarvestViewModel


@Composable
fun AuthScreen(
    harvestViewModel: HarvestViewModel
) {
    val authState = harvestViewModel.homeUiState.authState
    var userRole by rememberSaveable { mutableStateOf<String?>(null) }
    var hasSeenOnboarding by rememberSaveable { mutableStateOf(false) }

    when (authState) {
        AuthState.SPLASH -> {
            SplashScreen(
                onSplashComplete = {
                    if (hasSeenOnboarding) {
                        harvestViewModel.updateAuthState(AuthState.WELCOME)
                    } else {
                        harvestViewModel.updateAuthState(AuthState.ONBOARDING)
                    }
                }
            )
        }

        AuthState.ONBOARDING -> {
            OnboardingScreen(
                onFinish = {
                    hasSeenOnboarding = true
                    harvestViewModel.updateAuthState(AuthState.WELCOME)
                }
            )
        }

        AuthState.WELCOME -> {
            WelcomeScreen(
                onSignInClick = { harvestViewModel.updateAuthState(AuthState.SIGN_IN) },
                onSignUpClick = { harvestViewModel.updateAuthState(AuthState.SIGN_UP) },
                onGuestClick = {
                    harvestViewModel.enterGuestMode()
                }
            )
        }

        AuthState.SIGN_IN -> {
            SignInScreen(
                onBackClick = { harvestViewModel.updateAuthState(AuthState.WELCOME) },
                onSignInSuccess = { role ->
                    userRole = role
                    harvestViewModel.updateAuthState(AuthState.AUTHENTICATED)
                },
                onForgotPassword = { harvestViewModel.updateAuthState(AuthState.FORGOT_PASSWORD) },
                navigateToSignUp = { harvestViewModel.updateAuthState(AuthState.SIGN_UP) }
            )
        }

        AuthState.SIGN_UP -> {
            SignUpScreen(
                onBackClick = { harvestViewModel.updateAuthState(AuthState.WELCOME) },
                onSignUpSuccess = { role ->
                    userRole = role
                    harvestViewModel.updateAuthState(AuthState.AUTHENTICATED)
                },
                harvestViewModel = harvestViewModel
            )
        }

        AuthState.FORGOT_PASSWORD -> {
            ForgotPasswordScreen(
                onBackClick = { harvestViewModel.updateAuthState(AuthState.SIGN_IN) },
                onResetSent = { harvestViewModel.updateAuthState(AuthState.SIGN_IN) }
            )
        }

        AuthState.AUTHENTICATED -> {
            harvestViewModel.setRole(userRole == "farmer")
            harvestViewModel.toggleBeginner()
            // Ensure we are ready for next time
            harvestViewModel.updateAuthState(AuthState.SIGN_IN)
        }
    }
}
