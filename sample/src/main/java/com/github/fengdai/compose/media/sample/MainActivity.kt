package com.github.fengdai.compose.media.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MaterialTheme {
                val systemUiController = rememberSystemUiController()
                SideEffect {
                    systemUiController.setStatusBarColor(Color.Transparent, darkIcons = true)
                    systemUiController.setNavigationBarColor(Color.Transparent, darkIcons = true)
                }
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
                    composable("fullscreen-toggle") {
                        FullscreenToggle(navController)
                    }
                    composable("timebars") {
                        TimeBars(navController)
                    }
                    composable("exo-player-view") {
                        PlayerViewSample()
                    }
                }
            }
        }
    }
}
