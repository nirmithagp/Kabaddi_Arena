package com.example.my_arena.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.my_arena.model.AppState
import com.example.my_arena.model.MatchEntity
import com.example.my_arena.model.MatchStats
import com.example.my_arena.model.Action
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviousMatchesScreen(navController: NavController) {
    val matchesState = AppState.repository?.allMatches?.collectAsState(initial = emptyList())
    val matches = matchesState?.value ?: emptyList()

    val groupedMatches = remember(matches) {
        matches.groupBy { it.matchDate }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        if (matches.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No matches played yet", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                groupedMatches.forEach { (date, dateMatches) ->
                    item {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(dateMatches) { match ->
                        MatchHistoryItem(match) {
                            // Reconstruct MatchStats from stored JSON to view in ResultScreen
                            val actionListType = object : TypeToken<List<Action>>() {}.type
                            val actions: List<Action> = Gson().fromJson(match.actionHistoryJson, actionListType)
                            
                            AppState.currentMatch = MatchStats(
                                opponentTeam = match.opponentName,
                                raids = 0, 
                                touchPoints = actions.filter { it.title == "Touch Point" }.sumOf { it.points },
                                bonusPoints = actions.filter { it.title == "Bonus Point" }.sumOf { it.points },
                                superRaids = actions.count { it.title == "Super Raid" },
                                caught = actions.count { it.title == "Caught" },
                                tacklePoints = actions.filter { it.title.contains("Tackle") }.sumOf { it.points },
                                totalTackles = actions.count { it.title.contains("Tackle") },
                                successfulTackles = actions.count { it.title.contains("Tackle") && it.points > 0 },
                                actionsHistory = actions.toMutableList()
                            ).apply {
                                // Recalculate raids count from history
                                raids = actions.count { it.title == "Touch Point" || it.title == "Bonus Point" || it.title == "Super Raid" || it.title == "Empty Raid" || it.title == "Caught" }
                            }
                            navController.navigate("result")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchHistoryItem(match: MatchEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "vs ${match.opponentName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Score: ${match.totalScore}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "VIEW",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
