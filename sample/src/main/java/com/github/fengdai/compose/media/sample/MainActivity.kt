package com.github.fengdai.compose.media.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        Home(navController)
                    }
                    composable("basic") {
                        Basic(navController)
                    }
                    composable("list") {
                        InsideList(navController)
                    }
                    composable("exo-styled-player-view") {
                        StyledPlayerViewSample()
                    }
                }
            }
        }
    }
}
