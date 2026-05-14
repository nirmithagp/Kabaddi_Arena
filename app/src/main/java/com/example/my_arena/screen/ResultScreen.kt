package com.example.my_arena.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.my_arena.model.AppState
import com.example.my_arena.model.MatchStats

@Composable
fun ResultScreen(navController: NavController) {
    val player = AppState.currentPlayer ?: return
    val match = AppState.currentMatch ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "MATCH ANALYSIS",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // 1. Performance Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard("Total Points", "${match.totalPoints()}", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
            StatCard("Raid Success", "${"%.0f".format(match.raidSuccessRate())}%", Color(0xFF4CAF50), Modifier.weight(1f))
            StatCard("Tackle Success", "${"%.0f".format(match.tackleSuccessRate())}%", Color(0xFF2196F3), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Match Momentum Line Chart
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "MATCH MOMENTUM",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        MomentumChart(match)

        Spacer(modifier = Modifier.height(24.dp))

        // 3. AI Scout Recommendation
        ScoutRecommendationSection(match, player.skillLevel)

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Action History Summary
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ACTION LOG SUMMARY",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        ActionHistorySummary(match)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                navController.navigate("dashboard/${player.name}/${player.skillLevel}") {
                    popUpTo("profile") { inclusive = false }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("BACK TO DASHBOARD", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
// ... rest of the file remains same


@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = color)
        }
    }
}

@Composable
fun MomentumChart(match: MatchStats) {
    // reversed because we added actions at the end of the list, but we want chronological order for the chart
    val pointsHistory = match.actionsHistory.runningFold(0) { acc, action -> acc + action.points }
    val maxPoints = pointsHistory.maxOrNull()?.toFloat()?.coerceAtLeast(1f) ?: 10f
    
    Card(
        modifier = Modifier.fillMaxWidth().height(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            if (pointsHistory.size < 2) return@Canvas
            
            val width = size.width
            val height = size.height
            val stepX = width / (pointsHistory.size - 1)
            val scaleY = height / maxPoints

            val path = Path()
            pointsHistory.forEachIndexed { index, points ->
                val x = index * stepX
                val y = height - (points * scaleY)
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }

            drawPath(
                path = path,
                color = Color(0xFF6200EE),
                style = Stroke(width = 3.dp.toPx())
            )
            
            // Baseline
            drawLine(
                color = Color.Gray.copy(alpha = 0.2f),
                start = Offset(0f, height),
                end = Offset(width, height),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

@Composable
fun ScoutRecommendationSection(match: MatchStats, skillLevel: String) {
    val raidRate = match.raidSuccessRate()
    val tackleRate = match.tackleSuccessRate()
    
    val insight = when {
        raidRate > 80 -> "Strong Raider! Your offensive success is exceptional. You dominated the mat with your speed and touches."
        tackleRate > 70 -> "Defensive Powerhouse! Your tackle success was match-winning. Great anticipation and strength."
        match.superRaids > 0 -> "Aggressive Playstyle! Those Super Raids turned the momentum. Excellent clutch performance."
        raidRate < 40 && tackleRate < 40 -> "Tough match. Work on both offensive escape techniques and defensive coordination."
        raidRate < 40 -> "Raiding improvement needed. Focus on footwork and 'Dubki' techniques to evade defenders."
        tackleRate < 40 -> "Defensive improvement needed. Work on your timing for ankle holds and chain coordination."
        else -> "Solid Performance! You contributed effectively in both departments. Keep up the consistency."
    }

    val drills = when {
        raidRate < 60 -> listOf("Toe touch speed drills", "Dubki & Escape practice", "Multi-point raid scenarios")
        tackleRate < 60 -> listOf("Ankle hold grip strength", "Thigh hold timing", "Corner-to-Corner chain movement")
        else -> listOf("Advanced tactical raid logic", "Strategic tackle blocks", "Match situation stamina")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SCOUT'S RECOMMENDATION",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(text = "Performance insight:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(text = insight, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Focus drills:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            drills.forEachIndexed { index, drill ->
                Text(text = "${index + 1}. $drill", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Player level: ", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = skillLevel.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun ActionHistorySummary(match: MatchStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Newest at the top
            match.actionsHistory.reversed().take(5).forEach { action ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = action.title, style = MaterialTheme.typography.bodySmall)
                    Text(text = "+${action.points}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if(action.points > 0) Color(0xFF4CAF50) else Color.Gray)
                }
            }
            if (match.actionsHistory.size > 5) {
                Text(
                    text = "Summary of last 5 of ${match.actionsHistory.size} actions",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}