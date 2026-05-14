package com.example.my_arena.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.my_arena.model.AppState
import com.example.my_arena.model.MatchStats

@Composable
fun ScoutScreen(navController: NavController) {

    var team by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Enter Opponent Team",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = team,
            onValueChange = { team = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Team Name") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (team.isNotBlank()) {
                    AppState.currentMatch = MatchStats(
                        opponentTeam = team
                    )
                    navController.navigate("match")
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Start Match")
        }
    }
}