package com.example.my_arena.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.my_arena.screen.*

@Composable
fun NavGraph() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "profile"
    ) {
        composable("profile") {
            ProfileScreen(navController)
        }

        composable(
            route = "dashboard/{name}/{level}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("level") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val level = backStackEntry.arguments?.getString("level") ?: ""
            DashboardScreen(navController, name, level)
        }

        composable("scout") {
            ScoutScreen(navController)
        }

        composable("match") {
            MatchScreen(navController)
        }

        composable("result") {
            ResultScreen(navController)
        }
    }
}