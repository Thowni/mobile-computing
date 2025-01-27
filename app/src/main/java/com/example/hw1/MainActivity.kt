package com.example.hw1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.hw1.ui.theme.HW1Theme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HW1Theme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "messages") {
                        composable("messages")  { MessagesScreen(navController) }
                        composable("second") { ProfileScreen(navController) }
                }
            }
        }
    }
}

