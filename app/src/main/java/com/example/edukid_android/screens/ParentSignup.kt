package com.example.edukid_android.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edukid_android.R
import com.example.edukid_android.utils.ApiClient
import kotlinx.coroutines.launch

@Composable
fun ParentSignUpScreen(
    onSignUpClick: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onSignInClick: () -> Unit = {},
    onSignUpSuccess: (String, com.example.edukid_android.models.Parent) -> Unit = { _, _ -> }
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    fun validateEmail(mail: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFAF7EE7).copy(alpha = 0.6f),
                        Color(0xFF272052)
                    ),
                    center = androidx.compose.ui.geometry.Offset(200f, 200f),
                    radius = 400f
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Decorative elements
            DecorativeElementsSignUp()

            // Main content with scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(150.dp))
                // Title
                Text(
                    text = "Create Parent\nAccount",
                    fontSize = 32.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    letterSpacing = 0.4.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Join us to guide your child's learning adventure",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Full Name field
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { 
                        fullName = it
                        if (fullNameError != null) fullNameError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Full Name", color = Color.White.copy(alpha = 0.8f)) },
                    placeholder = {
                        Text(
                            text = "John Doe",
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    },
                    isError = fullNameError != null,
                    supportingText = {
                        if (fullNameError != null) {
                            Text(text = fullNameError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        errorLeadingIconColor = MaterialTheme.colorScheme.error,
                        errorSupportingTextColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        if (emailError != null) emailError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email", color = Color.White.copy(alpha = 0.8f)) },
                    placeholder = {
                        Text(
                            text = "example@mail.com",
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    },
                    isError = emailError != null,
                    supportingText = {
                        if (emailError != null) {
                            Text(text = emailError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        errorLeadingIconColor = MaterialTheme.colorScheme.error,
                        errorSupportingTextColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        if (passwordError != null) passwordError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password", color = Color.White.copy(alpha = 0.8f)) },
                    placeholder = {
                        Text(
                            text = "••••••••",
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    },
                    isError = passwordError != null,
                    supportingText = {
                        if (passwordError != null) {
                            Text(text = passwordError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        errorLeadingIconColor = MaterialTheme.colorScheme.error,
                        errorSupportingTextColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(
                                text = if (passwordVisible) "👁️" else "👁️‍🗨️",
                                fontSize = 18.sp
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password field
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { 
                        confirmPassword = it
                        if (confirmPasswordError != null) confirmPasswordError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Confirm Password", color = Color.White.copy(alpha = 0.8f)) },
                    placeholder = {
                        Text(
                            text = "••••••••",
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    },
                    isError = confirmPasswordError != null,
                    supportingText = {
                        if (confirmPasswordError != null) {
                            Text(text = confirmPasswordError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        errorLeadingIconColor = MaterialTheme.colorScheme.error,
                        errorSupportingTextColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Text(
                                text = if (confirmPasswordVisible) "👁️" else "👁️‍🗨️",
                                fontSize = 18.sp
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Error message display
                errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Sign up button
                Button(
                    onClick = {
                        // Validate inputs
                        var hasError = false
                        if (fullName.isBlank()) {
                            fullNameError = "Full name is required"
                            hasError = true
                        }
                        if (email.isBlank()) {
                            emailError = "Email is required"
                            hasError = true
                        } else if (!validateEmail(email)) {
                            emailError = "Invalid email format"
                            hasError = true
                        }
                        if (password.isBlank()) {
                            passwordError = "Password is required"
                            hasError = true
                        } else if (password.length < 6) {
                            passwordError = "Password must be at least 6 characters"
                            hasError = true
                        }
                        if (confirmPassword.isBlank()) {
                            confirmPasswordError = "Please confirm your password"
                            hasError = true
                        } else if (password != confirmPassword) {
                            confirmPasswordError = "Passwords do not match"
                            hasError = true
                        }

                        if (hasError) return@Button

                        errorMessage = null
                        isLoading = true

                        coroutineScope.launch {
                            val result = ApiClient.signUp(fullName, email, password)
                            isLoading = false

                            result.onSuccess { signUpResponse ->
                                val parent = signUpResponse.parent.toParent()
                                onSignUpSuccess(signUpResponse.accessToken, parent)
                                onSignUpClick(fullName, email, password, confirmPassword)
                            }.onFailure { exception ->
                                errorMessage = exception.message ?: "Sign up failed. Please try again."
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF272052)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 2.dp
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF2E2E2E),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "CREATE ACCOUNT",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E2E2E),
                            letterSpacing = 0.4.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign in prompt
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account? ",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "Sign In",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.clickable { onSignInClick() }
                    )
                }

                Spacer(modifier = Modifier.height(150.dp))
            }
        }
    }
}

@Composable
fun BoxScope.DecorativeElementsSignUp() {
    // Book and Globe - Top Center (smaller and blurred)
    Image(
        painter = painterResource(id = R.drawable.book_and_globe),
        contentDescription = "Book and Globe",
        modifier = Modifier
            .size(200.dp)
            .align(Alignment.TopCenter)
            .offset(x = 0.dp, y = (-20).dp)
            .blur(1.5.dp),
        contentScale = ContentScale.Fit
    )

    // Coins - Top Right
    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(70.dp)
            .offset(x = 290.dp, y = 40.dp)
            .graphicsLayer(scaleX = -1f),
        contentScale = ContentScale.Fit
    )

    // Coins - Top Left
    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(60.dp)
            .offset(x = 20.dp, y = 30.dp)
            .rotate(15f),
        contentScale = ContentScale.Fit
    )

    // Book Stacks - Bottom Right
    Image(
        painter = painterResource(id = R.drawable.book_stacks),
        contentDescription = "Book Stacks",
        modifier = Modifier
            .size(90.dp)
            .offset(x = 260.dp, y = 700.dp)
            .blur(2.dp),
        contentScale = ContentScale.Fit
    )

    // Coins - Bottom Left
    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(45.dp)
            .offset(x = 25.dp, y = 720.dp)
            .rotate(38.66f),
        contentScale = ContentScale.Fit
    )
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun ParentSignUpScreenPreview() {
//    ParentSignUpScreen()
}