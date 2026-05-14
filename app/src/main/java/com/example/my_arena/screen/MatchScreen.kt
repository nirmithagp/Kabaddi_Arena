package com.example.my_arena.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.my_arena.model.AppState
import com.example.my_arena.model.MatchEntity
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchScreen(navController: NavController) {
    val match = AppState.currentMatch ?: return
    val player = AppState.currentPlayer ?: return
    val scope = rememberCoroutineScope()

    var secondsElapsed by remember { mutableStateOf(0) }
    var isRaidingMode by remember { mutableStateOf(true) }
    var updateTrigger by remember { mutableStateOf(0) } 

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            secondsElapsed++
        }
    }

    val timerText = remember(secondsElapsed) {
        val mins = secondsElapsed / 60
        val secs = secondsElapsed % 60
        "%02d:%02d".format(mins, secs)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Top Section: Scoreboard Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1f)) {
                        Text(
                            text = "OUR TEAM",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${match.totalPoints()}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                        Text(
                            text = "OPPONENT",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            text = match.opponentTeam,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = timerText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Toggle Section
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            SegmentedButton(
                selected = isRaidingMode,
                onClick = { isRaidingMode = true },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) {
                Text("RAIDING")
            }
            SegmentedButton(
                selected = !isRaidingMode,
                onClick = { isRaidingMode = false },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) {
                Text("TACKLING")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons Section
        if (isRaidingMode) {
            RaidingActions(match) { updateTrigger++ }
        } else {
            TacklingActions(match) { updateTrigger++ }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // History Section
        Text(
            text = "MATCH TIMELINE",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        key(updateTrigger) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(match.actionsHistory.reversed()) { action ->
                    HistoryItem(action)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    // Change 2: Save match locally
                    val matchEntity = MatchEntity(
                        opponentName = match.opponentTeam,
                        totalScore = match.totalPoints(),
                        raidSuccess = match.raidSuccessRate(),
                        tackleSuccess = match.tackleSuccessRate(),
                        actionHistoryJson = Gson().toJson(match.actionsHistory),
                        matchDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                    )
                    AppState.repository?.insert(matchEntity)

                    // Update player career stats
                    player.totalMatches++
                    player.totalRaids += match.raids
                    player.totalTouchPoints += match.touchPoints
                    player.totalBonusPoints += match.bonusPoints
                    player.totalSuperRaids += match.superRaids
                    player.totalCaught += match.caught
                    player.totalTacklePoints += match.tacklePoints
                    
                    navController.navigate("result")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("FINISH MATCH", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun RaidingActions(match: com.example.my_arena.model.MatchStats, onUpdate: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionCard(
                text = "Touch Point",
                points = "+1",
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            ) {
                match.touchPoints++
                match.raids++
                match.addAction("Touch Point", 1)
                onUpdate()
            }
            ActionCard(
                text = "Bonus Point",
                points = "+1",
                color = Color(0xFFFFC107),
                modifier = Modifier.weight(1f)
            ) {
                match.bonusPoints++
                match.raids++
                match.addAction("Bonus Point", 1)
                onUpdate()
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionCard(
                text = "Super Raid",
                points = "+3",
                color = Color(0xFF2E7D32),
                modifier = Modifier.weight(1f)
            ) {
                match.superRaids++
                match.raids++
                match.addAction("Super Raid", 3)
                onUpdate()
            }
            ActionCard(
                text = "Empty Raid",
                points = "0",
                color = Color(0xFF90A4AE),
                modifier = Modifier.weight(1f)
            ) {
                match.raids++
                match.addAction("Empty Raid", 0)
                onUpdate()
            }
        }
        ActionCard(
            text = "Caught / Out",
            points = "OUT",
            color = Color(0xFFE53935),
            modifier = Modifier.fillMaxWidth()
        ) {
            match.caught++
            match.raids++
            match.addAction("Caught", 0)
            onUpdate()
        }
    }
}

@Composable
fun TacklingActions(match: com.example.my_arena.model.MatchStats, onUpdate: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionCard(
                text = "Tackle Point",
                points = "+1",
                color = Color(0xFF2196F3),
                modifier = Modifier.weight(1f)
            ) {
                match.tacklePoints++
                match.totalTackles++
                match.successfulTackles++
                match.addAction("Tackle Point", 1)
                onUpdate()
            }
            ActionCard(
                text = "Super Tackle",
                points = "+2",
                color = Color(0xFF3F51B5),
                modifier = Modifier.weight(1f)
            ) {
                match.tacklePoints += 2
                match.totalTackles++
                match.successfulTackles++
                match.addAction("Super Tackle", 2)
                onUpdate()
            }
        }
        ActionCard(
            text = "Failed Tackle / Out",
            points = "OUT",
            color = Color(0xFFE53935),
            modifier = Modifier.fillMaxWidth()
        ) {
            match.totalTackles++
            match.addAction("Failed Tackle", 0)
            onUpdate()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCard(
    text: String,
    points: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = color)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(points, color = Color.White.copy(alpha = 0.9f), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun HistoryItem(action: com.example.my_arena.model.Action) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Change 1: Remove timestamp
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            if (action.points > 0) {
                Text(
                    text = "+${action.points}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
            } else if (action.title == "Caught" || action.title == "Failed Tackle") {
                Text(
                    text = "OUT",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
