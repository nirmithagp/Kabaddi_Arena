package com.example.my_arena

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.my_arena.model.AppState
import com.example.my_arena.navigation.NavGraph
import com.example.my_arena.ui.theme.My_ArenaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize AppState with database and repository
        AppState.initialize(this)

        setContent {
            My_ArenaTheme {
                NavGraph()
            }
        }
    }
}
