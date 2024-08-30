package com.example.ecojourney

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ecojourney.menuprofile.AboutScreen
import com.example.ecojourney.menuprofile.SecurityScreen
import com.example.ecojourney.menuprofile.SettingsScreen
import com.example.ecojourney.onboard.OnboardingScreen
import com.example.ecojourney.screens.HomeDetail
import com.example.ecojourney.screens.ProfileDetail
import com.example.ecojourney.ui.theme.EcoJourneyTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var navController: NavHostController

    // ActivityResultLauncher for Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.let {
                firebaseAuthWithGoogle(it, navController)
            }
        } catch (e: ApiException) {
            // Handle sign-in error
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()

        // Keep splash screen visible for a longer time
        splashScreen.setKeepOnScreenCondition { true }

        // Add a delay to keep the splash screen visible
        lifecycleScope.launch {
            delay(2000)
            splashScreen.setKeepOnScreenCondition { false }
        }

        // Initialize Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("116343510296303041516") // Replace with your server client ID
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set the content first
        setContent {
            EcoJourneyTheme {
                navController = rememberNavController()
                NavigationView(navController)
            }
        }

        // Check if the user is already signed in
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            // Navigate to MainScreen after the content has been set
            lifecycleScope.launch {
                // Ensure this runs after the content has been set and navigation graph is available
                delay(100) // A slight delay to ensure the navController is ready
                navController.navigate("main") {
                    popUpTo("onboarding") { inclusive = true }
                }
            }
        }
    }

    @Composable
    fun NavigationView(navController: NavHostController) {
        val context = LocalContext.current
        val selectedItem = remember { mutableStateOf(0) }
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: "defaultUserId" // Get the userId from FirebaseAuth

        NavHost(navController = navController, startDestination = "onboarding") {
            composable("onboarding") { OnboardingScreen(navController) { /* Handle finish action if needed */ } }
            composable("login") {
                LoginScreen(
                    navController = navController,
                    onLoginResult = { isSuccess, message -> /* Handle login result */ },
                    onGoogleSignInClick = { startGoogleSignIn() }
                )
            }
            composable("resetPassword") { ResetPassword(navController) { isSuccess, message -> /* Handle login result */ } }
            composable("signup") {
                SignupScreen(
                    navController = navController,
                    onSignupResult = { isSuccess, message -> /* Handle signup result */ },
                    onGoogleSignInClick = { startGoogleSignIn() }
                )
            }
            composable("main") {
                MainScreen(
                    selectedItem = selectedItem,
                    navController = navController,
                    context = context,
                    userId = userId // Pass the userId here
                )
            }
            composable("homeDetail/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: "defaultUserId"
                HomeDetail(
                    navController = navController,
                    userId = userId
                )
            }
            composable("settings") { SettingsScreen(navController) }
            composable("security") { SecurityScreen(navController) }
            composable("about") { AboutScreen(navController) }
            composable("profile_detail") { ProfileDetail(navController) }
        }
    }


    private fun startGoogleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount, navController: NavHostController) {
        val auth = FirebaseAuth.getInstance()
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in successful, navigate to MainScreen
                    navController.navigate("main") {
                        popUpTo("signup") { inclusive = true }
                    }
                } else {
                    // Handle sign-in failure
                    val exception = task.exception
                    Log.e("SignInError", "Sign-in failed", exception)
                    // Show an error message to the user
                }
            }
    }
}
