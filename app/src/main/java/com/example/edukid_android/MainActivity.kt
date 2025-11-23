package com.example.edukid_android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.edukid_android.models.Child
import com.example.edukid_android.models.Parent
import com.example.edukid_android.models.Quiz
import com.example.edukid_android.screens.AddChildScreen
import com.example.edukid_android.screens.ChildProfileQRScreen
import com.example.edukid_android.screens.ChildQRLoginScreen
import com.example.edukid_android.screens.ForgotPasswordScreen
import com.example.edukid_android.screens.ResetPasswordScreen
import com.example.edukid_android.screens.GamesScreen
import com.example.edukid_android.screens.ImprovedChildHomeScreen
import com.example.edukid_android.screens.ParentDashboardScreen
import com.example.edukid_android.screens.ParentEditProfileScreen
import com.example.edukid_android.screens.ParentSignInScreen
import com.example.edukid_android.screens.ParentSignUpScreen
import com.example.edukid_android.screens.WelcomeScreen
import com.example.edukid_android.ui.theme.EduKid_androidTheme
import com.example.edukid_android.utils.PreferencesManager
import com.example.edukid_android.games.*

class MainActivity : ComponentActivity() {
    private var deepLinkTokenState = mutableStateOf<String?>(null)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Check initial intent for deep link
        deepLinkTokenState.value = extractTokenFromIntent(intent)

        setContent {
            EduKid_androidTheme {
                // Check for stored credentials on startup
                val initialToken = remember { PreferencesManager.getAccessToken(this@MainActivity) }
                val initialParent = remember { PreferencesManager.getParentData(this@MainActivity) }
                
                // Mutable state for current parent and access token
                var currentParent by remember { mutableStateOf<Parent?>(initialParent) }
                var accessToken by remember { mutableStateOf<String?>(initialToken) }
                var currentChild by remember { mutableStateOf<Child?>(null) }
                var currentQuiz by remember { mutableStateOf<Quiz?>(null) }

                val navController = rememberNavController()
                
                // Observe deep link token state
                val deepLinkToken by remember { deepLinkTokenState }
                
                // Navigate to reset password if deep link token is present
                LaunchedEffect(deepLinkToken) {
                    deepLinkToken?.let { token ->
                        navController.navigate("resetPassword/$token") {
                            // Clear back stack when navigating from deep link
                            popUpTo(0) { inclusive = true }
                        }
                        // Clear the token after navigation
                        deepLinkTokenState.value = null
                    }
                }
                
                NavHost(
                    navController = navController, 
                    startDestination = if (initialToken != null && initialParent != null) "parentDashboard" else "welcome"
                ) {
                    composable("welcome") { WelcomeScreen(navController) }
                    composable("parentLogin") { 
                        ParentSignInScreen(
                            navController = navController,
                            onSignUpClick = {
                                navController.navigate("parentSignup")
                            },
                            onForgotPasswordClick = {
                                navController.navigate("forgotPassword")
                            },
                            onLoginSuccess = { token, parent ->
                                // Store the access token and parent data
                                accessToken = token
                                currentParent = parent
                                // Navigate to parent dashboard
                                navController.navigate("parentDashboard") {
                                    // Clear the back stack so user can't go back to login
                                    popUpTo("welcome") { inclusive = false }
                                }
                            }
                        )
                    }
                    composable("forgotPassword") {
                        ForgotPasswordScreen(
                            navController = navController,
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable(
                        route = "resetPassword/{token}",
                        arguments = listOf(
                            navArgument("token") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val token = backStackEntry.arguments?.getString("token") ?: ""
                        ResetPasswordScreen(
                            navController = navController,
                            token = token,
                            onBackClick = {
                                navController.navigate("parentLogin") {
                                    popUpTo("forgotPassword") { inclusive = true }
                                }
                            },
                            onResetSuccess = {
                                navController.navigate("parentLogin") {
                                    popUpTo("forgotPassword") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("childLogin") { 
                        ChildQRLoginScreen(
                            navController = navController,
                            onQRScanned = { child ->
                                currentChild = child
                                navController.navigate("childHome") {
                                    popUpTo("childLogin") { inclusive = true }
                                }
                            },
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("childHome") { 
                        currentChild?.let { child ->
                            ImprovedChildHomeScreen(
                                navController = navController,
                                child = child,
                                onQuizClick = { quiz ->
                                    currentQuiz = quiz
                                    navController.navigate("quizPlay")
                                }
                            )
                        } ?: run {
                            // If no child data, navigate back to login
                            LaunchedEffect(Unit) {
                                navController.navigate("childLogin") {
                                    popUpTo("childHome") { inclusive = true }
                                }
                            }
                        }
                    }
                    composable("childGames") {
                        currentChild?.let { child ->
                            GamesScreen(
                                navController = navController,
                                child = child
                            )
                        } ?: run {
                            // If no child data, navigate back to login
                            LaunchedEffect(Unit) {
                                navController.navigate("childLogin") {
                                    popUpTo("childGames") { inclusive = true }
                                }
                            }
                        }
                    }
                    
                    // Game Routes
                    composable("game/number_match") {
                        NumberMatchGame(navController = navController)
                    }
                    composable("game/math_challenge") {
                        MathChallengeGame(navController = navController)
                    }
                    composable("game/word_builder") {
                        WordBuilderGame(navController = navController)
                    }
                    composable("game/vocabulary_quiz") {
                        VocabularyQuizGame(navController = navController)
                    }
                    composable("game/pattern_puzzle") {
                        PatternPuzzleGame(navController = navController)
                    }
                    composable("game/color_match") {
                        ColorMatchGame(navController = navController)
                    }
                    composable("game/memory_cards") {
                        MemoryCardsGame(navController = navController)
                    }
                    composable("game/world_explorer") {
                        WorldExplorerGame(navController = navController)
                    }
                    
                    composable("quizPlay") {
                        currentQuiz?.let { quiz ->
                            com.example.edukid_android.screens.QuizPlayScreen(
                                quiz,
                                navController,
                                onExit = {
                                    navController.popBackStack()
                                },
                                onQuizSubmitted = {
                                    currentChild = currentChild?.let { child ->
                                        val updated = child.quizzes.filter { it.id != quiz.id }
                                        child.copy(quizzes = updated)
                                    }
                                },
                                currentChild?.parentId,
                                currentChild?.id
                            )
                        } ?: run {
                            LaunchedEffect(Unit) {
                                navController.popBackStack()
                            }
                        }
                    }

                    composable(
                        route = "childDetails/{childId}",
                        arguments = listOf(
                            navArgument("childId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val childId = backStackEntry.arguments?.getString("childId")
                        val child = currentParent?.children?.find { it.id == childId }
                        
                        if (child != null) {
                            ChildProfileQRScreen(
                                child = child,
                                parentId = currentParent?.id,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onGenerateQuizClick = { subject, difficulty, numQuestions, customTopic ->
                                    // TODO: Implement quiz generation logic
                                    // For now, just navigate back or show a message
                                },
                                onViewResultsClick = {
                                    // TODO: Implement view results navigation
                                    // For now, just navigate back or show a message
                                },
                                onQuizGenerated = { quiz ->
                                    currentParent = currentParent?.let { p ->
                                        val updatedChildren = p.children.map { c ->
                                            if (c.id == childId) c.copy(quizzes = c.quizzes + quiz) else c
                                        }
                                        val updatedParent = p.copy(children = updatedChildren)
                                        PreferencesManager.saveParentData(this@MainActivity, updatedParent)
                                        updatedParent
                                    }
                                }
                            )
                        } else {
                            // If child not found, navigate back
                            LaunchedEffect(Unit) {
                                navController.popBackStack()
                            }
                        }
                    }
                    composable("addchild") { 
                        AddChildScreen(
                            parentId = currentParent?.id,
                            onAddChildSuccess = { updatedParent ->
                                // Update the current parent with the new child
                                currentParent = updatedParent
                                PreferencesManager.saveParentData(this@MainActivity, updatedParent)
                                // Navigate back to parent dashboard
                                navController.popBackStack()
                            },
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("parentSignup") { 
                        ParentSignUpScreen(
                            navController = navController,
                            onSignUpSuccess = { token, parent ->
                                // Store the access token and parent data
                                accessToken = token
                                currentParent = parent
                                // Navigate to parent dashboard
                                navController.navigate("parentDashboard") {
                                    // Clear the back stack so user can't go back to signup
                                    popUpTo("welcome") { inclusive = false }
                                }
                            },
                            onSignInClick = {
                                navController.navigate("parentLogin")
                            }
                        )
                    }
                    composable("parentDashboard") {
                        ParentDashboardScreen(
                            parent = currentParent,
                            onAddChildClick = {
                                navController.navigate("addchild")
                            },
                            onChildClick = { child ->
                                // Navigate to child details screen with child ID
                                child.id?.let { childId ->
                                    navController.navigate("childDetails/$childId")
                                }
                            },
                            onEditProfileClick = {
                                navController.navigate("parentEditProfile")
                            }
                        )
                    }
                    composable("parentEditProfile") {
                        ParentEditProfileScreen(
                            initialParent = currentParent,
                            onBackClick = { navController.popBackStack() },
                            onLogoutClick = {
                                currentParent = null
                                accessToken = null
                                PreferencesManager.clearAll(this@MainActivity)
                                navController.navigate("welcome") {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onParentUpdated = { updatedParent ->
                                currentParent = updatedParent
                                PreferencesManager.saveParentData(this@MainActivity, updatedParent)
                            }
                        )
                    }
//                    composable("childLogin") { ChildQRLoginScreen(navController) }
//                    composable("childLogin") { ChildQRLoginScreen(navController) }
//                    composable("childLogin") { ChildQRLoginScreen(navController) }
//        composable("") { ChildQRLoginScreen() }
                }
//                WelcomeScreen()
//            ParentDashboardScreen(
//                parent = parent
//            )
                //ChildQRLoginScreen()
                //ParentSignUpScreen()
//ParentSignInScreen()//                HomeScreen()
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    HomeScreen()
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
// }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Extract token from new intent and update state to trigger navigation
        deepLinkTokenState.value = extractTokenFromIntent(intent)
    }
    
    private fun extractTokenFromIntent(intent: Intent?): String? {
        val data: Uri? = intent?.data
        if (data != null && data.scheme == "edukid" && data.host == "reset-password") {
            // Extract token from path: edukid://reset-password/{token}
            // The token can be in pathSegments or as the last path segment
            val pathSegments = data.pathSegments
            if (pathSegments.isNotEmpty()) {
                return pathSegments[0]
            }
            // Alternative: check if token is in the path directly
            val path = data.path
            if (path != null && path.startsWith("/")) {
                return path.substring(1) // Remove leading slash
            }
        }
        return null
    }
}
