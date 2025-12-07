package com.example.edukid_android.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.clip
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
import com.example.edukid_android.R
import com.example.edukid_android.models.Child
import com.example.edukid_android.models.Parent
import com.example.edukid_android.utils.ApiClient
import com.example.edukid_android.utils.PreferencesManager
import com.example.edukid_android.utils.getAvatarResource
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class ParentProfile(
    val name: String, val email: String, val password: String
)

@Composable
fun ParentProfileScreen(
    parentProfile: ParentProfile,
    children: List<Child> = emptyList(),
    onBackClick: () -> Unit = {},
    onUpdateProfile: (String, String, String) -> Unit = { _, _, _ -> },
    onDeleteChild: (Child) -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf(parentProfile.name) }
    var email by remember { mutableStateOf(parentProfile.email) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Child?>(null) }
    var isUpdating by remember { mutableStateOf(false) }

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
            DecorativeElementsProfile()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with back button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick, modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.2f), shape = CircleShape
                                )
                        ) {
                            Text(
                                text = "←", fontSize = 24.sp, color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = "Profile Settings",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.4.sp
                        )
                    }

                    // Logout button
                    IconButton(
                        onClick = onLogoutClick, modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color(0xFFFF5252).copy(alpha = 0.3f), shape = CircleShape
                            )
                    ) {
                        Text(
                            text = "🚪", fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Profile Info Section
                Text(
                    text = "Personal Information",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Name field
                        Text(
                            text = "Full Name",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF2E2E2E),
                                unfocusedTextColor = Color(0xFF2E2E2E),
                                focusedBorderColor = Color(0xFFAF7EE7),
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                cursorColor = Color(0xFFAF7EE7)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email field
                        Text(
                            text = "Email Address",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF2E2E2E),
                                unfocusedTextColor = Color(0xFF2E2E2E),
                                focusedBorderColor = Color(0xFFAF7EE7),
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                cursorColor = Color(0xFFAF7EE7)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Change Password Section
                Text(
                    text = "Change Password",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Current Password
                        Text(
                            text = "Current Password",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF2E2E2E),
                                unfocusedTextColor = Color(0xFF2E2E2E),
                                focusedBorderColor = Color(0xFFAF7EE7),
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                cursorColor = Color(0xFFAF7EE7)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Text(
                                        text = if (passwordVisible) "👁️" else "👁️‍🗨️",
                                        fontSize = 18.sp
                                    )
                                }
                            })

                        Spacer(modifier = Modifier.height(16.dp))

                        // New Password
                        Text(
                            text = "New Password",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF2E2E2E),
                                unfocusedTextColor = Color(0xFF2E2E2E),
                                focusedBorderColor = Color(0xFFAF7EE7),
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                cursorColor = Color(0xFFAF7EE7)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                    Text(
                                        text = if (newPasswordVisible) "👁️" else "👁️‍🗨️",
                                        fontSize = 18.sp
                                    )
                                }
                            })

                        Spacer(modifier = Modifier.height(16.dp))

                        // Confirm Password
                        Text(
                            text = "Confirm New Password",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF2E2E2E),
                                unfocusedTextColor = Color(0xFF2E2E2E),
                                focusedBorderColor = Color(0xFFAF7EE7),
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                cursorColor = Color(0xFFAF7EE7)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = {
                                    confirmPasswordVisible = !confirmPasswordVisible
                                }) {
                                    Text(
                                        text = if (confirmPasswordVisible) "👁️" else "👁️‍🗨️",
                                        fontSize = 18.sp
                                    )
                                }
                            })
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Manage Children Section
                Text(
                    text = "Manage Children",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (children.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.95f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.avatar_3),
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No children added yet",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2E2E2E)
                            )
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        children.forEach { child ->
                            ChildManagementCard(
                                child = child, onDeleteClick = { showDeleteDialog = child })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Update Profile Button
                Button(
                    onClick = {
                        isUpdating = true
                        val passwordToUpdate =
                            if (newPassword.isNotBlank() && newPassword == confirmPassword) newPassword else currentPassword
                        onUpdateProfile(name, email, passwordToUpdate)
                        isUpdating = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(100.dp),
                    enabled = !isUpdating
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF2E2E2E),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "UPDATE PROFILE",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E2E2E),
                            letterSpacing = 0.4.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Delete Confirmation Dialog
    showDeleteDialog?.let { child ->
        AlertDialog(
            onDismissRequest = { },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚠️", fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Delete Child Profile?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E2E2E),
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to delete ${child.name}'s profile? This action cannot be undone and all quiz data will be lost.",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteChild(child)
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5252)
                    ), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Delete",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { },
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp, Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF666666)
                    )
                }
            })
    }
}

@Composable
fun ChildManagementCard(
    child: Child, onDeleteClick: () -> Unit
) {

    val context = LocalContext.current
    val avatarResId = remember(child.avatarEmoji) {
        val res = getAvatarResource(context, child.avatarEmoji)
        if (res != 0) res else getAvatarResource(context, "avatar_3")
    }
    print(avatarResId.toString()+ "fffffffffffffffffff")
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = Color(0xFFAF7EE7).copy(alpha = 0.2f), shape = CircleShape
                    ), contentAlignment = Alignment.Center
            ) {
                if (avatarResId != 0) {
                    Image(
                        painter = painterResource(id = avatarResId),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.avatar_3),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Child info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = child.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E2E)
                )
                Text(
                    text = "${child.age} years • Level ${child.level}",
                    fontSize = 13.sp,
                    color = Color(0xFF666666)
                )
            }

            // Delete button
            IconButton(
                onClick = onDeleteClick, modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = Color(0xFFFF5252).copy(alpha = 0.1f), shape = CircleShape
                    )
            ) {
                Text(
                    text = "🗑️", fontSize = 20.sp
                )
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun ParentProfileScreenPreview() {
    val sampleProfile = ParentProfile(
        name = "John Doe", email = "john.doe@example.com", password = "password123"
    )

    val sampleChildren = listOf(
        Child(
            id = "1", name = "Emma", age = 8, level = "3", avatarEmoji = "👧", Score = 200
        ), Child(
            id = "2", name = "Lucas", age = 6, level = "1", avatarEmoji = "👦", Score = 150
        )
    )

    ParentProfileScreen(
        parentProfile = sampleProfile, children = sampleChildren
    )
}

@Composable
fun ParentEditProfileScreen(
    initialParent: Parent? = null,
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onParentUpdated: (Parent) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentParent by remember { mutableStateOf<Parent?>(initialParent ?: PreferencesManager.getParentData(context)) }
    var showUpdateSuccess by remember { mutableStateOf(false) }
    LaunchedEffect(currentParent?.id) {
        val id = currentParent?.id
        if (!id.isNullOrBlank()) {
            val result = ApiClient.getParent(id)
            result.onSuccess { parentResponse ->
                val updated = parentResponse.toParent()
                PreferencesManager.saveParentData(context, updated)
                currentParent = updated
            }
        }
    }
    val profile = remember(currentParent) {
        ParentProfile(
            name = currentParent?.name ?: "",
            email = currentParent?.email ?: "",
            password = ""
        )
    }
    val children = currentParent?.children ?: emptyList()

    Box(modifier = Modifier.fillMaxSize()) {
        ParentProfileScreen(
            parentProfile = profile,
            children = children,
            onBackClick = onBackClick,
            onUpdateProfile = { name, email, password ->
                val id = currentParent?.id ?: return@ParentProfileScreen
                coroutineScope.launch {
                    val result = ApiClient.updateParent(id, name, email, password)
                    result.onSuccess { parentResponse ->
                        val updated = parentResponse.toParent()
                        PreferencesManager.saveParentData(context, updated)
                        currentParent = updated
                        onParentUpdated(updated)
                        showUpdateSuccess = true
                    }
                }
            },
            onDeleteChild = { child ->
                val parentId = currentParent?.id ?: return@ParentProfileScreen
                val childId = child.id ?: return@ParentProfileScreen
                coroutineScope.launch {
                    val result = ApiClient.deleteChild(parentId, childId)
                    result.onSuccess {
                        val parentResult = ApiClient.getParent(parentId)
                        parentResult.onSuccess { parentResponse ->
                            val updated = parentResponse.toParent()
                            PreferencesManager.saveParentData(context, updated)
                            currentParent = updated
                            onParentUpdated(updated)
                        }
                    }
                }
            },
            onLogoutClick = onLogoutClick
        )
        
        // Success Snackbar
        if (showUpdateSuccess) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                LaunchedEffect(showUpdateSuccess) {
                    delay(3000)
                    showUpdateSuccess = false
                }
                Snackbar(
                    containerColor = Color(0xFF43A047),
                    contentColor = Color.White
                ) {
                    Text(
                        text = "Profile updated successfully!",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}