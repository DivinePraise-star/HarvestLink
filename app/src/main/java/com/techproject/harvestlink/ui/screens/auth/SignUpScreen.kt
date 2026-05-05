package com.techproject.harvestlink.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.ui.HarvestViewModel
import io.github.jan.supabase.auth.exception.AuthRestException
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onBackClick: () -> Unit,
    onSignUpSuccess: (String) -> Unit,
    harvestViewModel: HarvestViewModel? = null
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("buyer") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentStep by remember { mutableStateOf(1) } // 1: Basic Info, 2: Role & Location
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF1B3D2F)
                )
            }

            if (currentStep == 2) {
                TextButton(
                    onClick = { currentStep = 1 },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text("Back", color = Color.Gray)
                }
            }
        }

        // Progress indicator
        LinearProgressIndicator(
            progress = { if (currentStep == 1) 0.5f else 1f },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(4.dp),
            color = Color(0xFF1B3D2F),
            trackColor = Color.LightGray
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (currentStep == 1) "Create Account" else "Almost There!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B3D2F)
            )

            Text(
                text = if (currentStep == 1) "Join the HarvestLink community"
                else "Tell us more about yourself",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            when (currentStep) {
                1 -> {
                    // Step 1: Basic Information
                    BasicInfoStep(
                        fullName = fullName,
                        onFullNameChange = { fullName = it },
                        email = email,
                        onEmailChange = { email = it },
                        phoneNumber = phoneNumber,
                        onPhoneNumberChange = { phoneNumber = it },
                        password = password,
                        onPasswordChange = { password = it },
                        confirmPassword = confirmPassword,
                        onConfirmPasswordChange = { confirmPassword = it },
                        passwordVisible = passwordVisible,
                        onPasswordVisibleToggle = { passwordVisible = !passwordVisible },
                        confirmPasswordVisible = confirmPasswordVisible,
                        onConfirmPasswordVisibleToggle = { confirmPasswordVisible = !confirmPasswordVisible }
                    )
                }
                2 -> {
                    // Step 2: Role & Location
                    RoleLocationStep(
                        selectedRole = selectedRole,
                        onRoleChange = { selectedRole = it },
                        location = location,
                        onLocationChange = { location = it }
                    )
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Next/Complete button
            Button(
                onClick = {
                    when (currentStep) {
                        1 -> {
                            if (fullName.isBlank() || email.isBlank() ||
                                phoneNumber.isBlank() || password.isBlank() ||
                                confirmPassword.isBlank()) {
                                errorMessage = "Please fill in all fields"
                                return@Button
                            }
                            if (password != confirmPassword) {
                                errorMessage = "Passwords don't match"
                                return@Button
                            }
                            if (password.length < 6) {
                                errorMessage = "Password must be at least 6 characters"
                                return@Button
                            }
                            errorMessage = null
                            currentStep = 2
                        }
                        2 -> {
                            if (location.isBlank()) {
                                errorMessage = "Please enter your location"
                                return@Button
                            }

                            isLoading = true
                            errorMessage = null

                            scope.launch {
                                try{
                                    val response = MoreData.signUp(
                                        email = email,
                                        password = password,
                                        name = fullName,
                                        role = selectedRole,
                                        number = phoneNumber,
                                        location = location
                                    )
                                    if (response != null) {
                                        isLoading = false
                                        harvestViewModel?.saveCurrentSession(selectedRole == "farmer")
                                        onSignUpSuccess(selectedRole)
                                    }
                                }catch (e: AuthRestException) {
                                    isLoading = false
                                    Toast.makeText(context,"Auth Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }catch (e: Exception){
                                    isLoading = false
                                    Toast.makeText(context,"Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1B3D2F)
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = if (currentStep == 1) "Continue" else "Complete Registration",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Terms and conditions
            Text(
                text = "By continuing, you agree to our Terms of Service and Privacy Policy",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicInfoStep(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibleToggle: () -> Unit,
    confirmPasswordVisible: Boolean,
    onConfirmPasswordVisibleToggle: () -> Unit
) {
    Column {
        OutlinedTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            label = { Text("Full Name") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null)
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1B3D2F)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            label = { Text("Email") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1B3D2F)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            label = { Text("Phone Number") },
            leadingIcon = {
                Icon(Icons.Default.Phone, contentDescription = null)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1B3D2F)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            label = { Text("Password") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            trailingIcon = {
                IconButton(onClick = onPasswordVisibleToggle) {
                    Icon(
                        if (passwordVisible) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1B3D2F)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            label = { Text("Confirm Password") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            trailingIcon = {
                IconButton(onClick = onConfirmPasswordVisibleToggle) {
                    Icon(
                        if (confirmPasswordVisible) Icons.Default.VisibilityOff
                        else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (confirmPasswordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1B3D2F)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleLocationStep(
    selectedRole: String,
    onRoleChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit
) {
    Column {
        Text(
            text = "I am a:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1B3D2F),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterChip(
                selected = selectedRole == "buyer",
                onClick = { onRoleChange("buyer") },
                label = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🛒", fontSize = 24.sp)
                        Text("Buyer", fontWeight = FontWeight.Medium)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF1B3D2F),
                    selectedLabelColor = Color.White
                )
            )

            FilterChip(
                selected = selectedRole == "farmer",
                onClick = { onRoleChange("farmer") },
                label = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👨‍🌾", fontSize = 24.sp)
                        Text("Farmer", fontWeight = FontWeight.Medium)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF1B3D2F),
                    selectedLabelColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = location,
            onValueChange = onLocationChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            label = { Text("Your Location") },
            leadingIcon = {
                Icon(Icons.Default.LocationOn, contentDescription = null)
            },
            placeholder = { Text("e.g., Kampala, Uganda") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1B3D2F)
            )
        )
    }
}