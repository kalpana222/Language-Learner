package com.example.languagelearner.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object LoginSignup : Screen("login_signup_screen")
    object Home : Screen("home_screen")
    object LanguageSelection : Screen("language_selection_screen")
    object LevelSelection : Screen("level_selection_screen")
    object SituationSelection : Screen("situation_selection_screen")
    object SpeakingPractice : Screen("speaking_practice_screen")
    object AiFeedback : Screen("ai_feedback_screen")
    object Progress : Screen("progress_screen")
    object Profile : Screen("profile_screen")
}