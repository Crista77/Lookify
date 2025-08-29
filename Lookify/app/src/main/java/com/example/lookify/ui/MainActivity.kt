package com.example.lookify.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.lookify.ui.theme.LookifyTheme
import com.example.traveldiary.ui.TravelDiaryNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LookifyTheme() {
                val navController = rememberNavController()
                TravelDiaryNavGraph(navController)
            }
        }
    }
}
