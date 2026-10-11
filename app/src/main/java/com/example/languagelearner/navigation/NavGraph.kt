package com.example.languagelearner.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.languagelearner.screens.SplashScreen
import com.example.languagelearner.screens.LoginSignupScreen
import com.example.languagelearner.screens.HomeScreen
import com.example.languagelearner.screens.LanguageSelectionScreen
import com.example.languagelearner.screens.LevelSelectionScreen
import com.example.languagelearner.screens.SituationSelectionScreen
import com.example.languagelearner.screens.SpeakingPracticeScreen
import com.example.languagelearner.screens.AiFeedbackScreen
import com.example.languagelearner.screens.ProgressScreen
import com.example.languagelearner.screens.ProfileScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.LoginSignup.route) {
            LoginSignupScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.LanguageSelection.route) {
            LanguageSelectionScreen(navController = navController)
        }
        composable(Screen.LevelSelection.route) {
            LevelSelectionScreen(navController = navController)
        }
        composable(Screen.SituationSelection.route) {
            SituationSelectionScreen(navController = navController)
        }
        composable(Screen.SpeakingPractice.route) {
            SpeakingPracticeScreen(navController = navController)
        }
        composable(Screen.AiFeedback.route) {
            AiFeedbackScreen(navController = navController)
        }
        composable(Screen.Progress.route) {
            ProgressScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title)
    }
}