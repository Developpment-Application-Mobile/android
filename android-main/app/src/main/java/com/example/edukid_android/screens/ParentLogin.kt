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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.example.edukid_android.R
import com.example.edukid_android.utils.ApiClient
import com.example.edukid_android.utils.PreferencesManager
import kotlinx.coroutines.launch

@Composable
fun ParentSignInScreen(
    navController: NavController,
    onForgotPasswordClick: () -> Unit = {},
    onLoginSuccess: (String, com.example.edukid_android.models.Parent) -> Unit = { _, _ -> }
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var rememberMe by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFAF7EE7).copy(alpha = 0.6f), Color(0xFF272052)
                    ), center = Offset(200f, 200f), radius = 400f
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Decorative elements
            DecorativeElementsSignIn()

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(100.dp))
                // Title
                Text(
                    text = "Welcome Back,\nParent!",
                    fontSize = 32.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    letterSpacing = 0.4.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Sign in to manage your child's learning journey",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    placeholder = {
                        Text(
                            text = "Email", color = Color.White.copy(alpha = 0.6f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    placeholder = {
                        Text(
                            text = "Password", color = Color.White.copy(alpha = 0.6f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(
                                text = if (passwordVisible) "👁️" else "👁️‍🗨️", fontSize = 18.sp
                            )
                        }
                    })

                Spacer(modifier = Modifier.height(12.dp))

                // Remember Me checkbox and Forgot password row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Remember Me checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { rememberMe = !rememberMe }
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color.White,
                                uncheckedColor = Color.White.copy(alpha = 0.7f),
                                checkmarkColor = Color(0xFF272052)
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Remember Me",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }

                    // Forgot password
                    Text(
                        text = "Forgot Password?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.clickable { onForgotPasswordClick() }
                    )
                }

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

                // Sign in button
                Button(
                    onClick = {
                        // Validate inputs
                        if (email.isBlank()) {
                            errorMessage = "Please enter your email"
                            return@Button
                        }
                        if (password.isBlank()) {
                            errorMessage = "Please enter your password"
                            return@Button
                        }

                        errorMessage = null
                        isLoading = true

                        coroutineScope.launch {
                            val result = ApiClient.login(email, password)
                            isLoading = false

                            result.onSuccess { loginResponse ->
                                val parent = loginResponse.parent.toParent()
                                
                                // Save credentials if Remember Me is checked
                                if (rememberMe) {
                                    PreferencesManager.saveAccessToken(context, loginResponse.accessToken)
                                    PreferencesManager.saveParentData(context, parent)
                                    PreferencesManager.setRememberMe(context, true)
                                } else {
                                    // Clear any previously stored credentials if Remember Me is unchecked
                                    PreferencesManager.clearAll(context)
                                }
                                
                                onLoginSuccess(loginResponse.accessToken, parent)
                            }.onFailure { exception ->
                                errorMessage = exception.message ?: "Login failed. Please try again."
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(100.dp),
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
                            text = "SIGN IN",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E2E2E),
                            letterSpacing = 0.4.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign up prompt
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account? ",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "Sign Up",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.clickable { navController.navigate("parentSignup") })

                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun BoxScope.DecorativeElementsSignIn() {
    // Education Book - Top Left (smaller)
    Image(
        painter = painterResource(id = R.drawable.education_book),
        contentDescription = "Education Book",
        modifier = Modifier
            .size(150.dp)
            .offset(x = (-50).dp, y = (-10).dp)
            .blur(1.dp),
        contentScale = ContentScale.Fit
    )

    // Coins - Top Right
    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(80.dp)
            .offset(x = 280.dp, y = 20.dp),
        contentScale = ContentScale.Fit
    )

    // Book Stacks - Bottom Right
    Image(
        painter = painterResource(id = R.drawable.book_stacks),
        contentDescription = "Book Stacks",
        modifier = Modifier
            .size(100.dp)
            .offset(x = 250.dp, y = 650.dp)
            .blur(2.dp),
        contentScale = ContentScale.Fit
    )

    // Coins - Bottom Left
    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(40.dp)
            .offset(x = 30.dp, y = 700.dp)
            .rotate(38.66f),
        contentScale = ContentScale.Fit
    )
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun ParentSignInScreenPreview() {

//    ParentSignInScreen(navController: NavController)
}