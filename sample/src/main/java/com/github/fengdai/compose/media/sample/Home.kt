package com.github.fengdai.compose.media.sample

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private val Samples = listOf(
    "basic" to "Basic",
    "list" to "Inside List",
    "fullscreen-toggle" to "Fullscreen Toggle",
    "timebars" to "TimeBars",
//    "exo-player-view" to "Exo PlayerView",
)

@Composable
fun Home(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sample") }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(Samples) { sample ->
                val (route, name) = sample
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(route)
                            }
                            .padding(horizontal = 0.dp, vertical = 15.dp),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
