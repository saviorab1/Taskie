package com.example.taskie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskie.SplashScreen
import com.example.taskie.ui.MainScreen
import com.example.taskie.ui.theme.TaskieTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskieTheme {
                // Start with SplashScreen,
                var showSplash by remember { mutableStateOf(true) }
                
                if (showSplash) {
                    SplashScreen {
                        showSplash = false
                    }
                } else {
                    HomeScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.taskie),
                            contentDescription = "Taskie Logo",
                            modifier = Modifier.size(140.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }
            )
        }
    ) { innerPadding ->
        MainScreen(innerPadding)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TaskieTheme {
        HomeScreen()
    }
}