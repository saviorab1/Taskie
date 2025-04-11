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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.taskie.ui.AddLocationScreen
import com.example.taskie.ui.LocationCategory
import com.example.taskie.ui.LocationData
import com.example.taskie.ui.MainScreen
import com.example.taskie.ui.PriorityLevel
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
    var isAddingLocation by remember { mutableStateOf(false) }
    
    // Sample initial data for demonstration
    var locations by remember { 
        mutableStateOf(
            listOf(
                LocationData(
                    id = 1,
                    name = "Fansipan Summit",
                    address = "Fansipan, Sapa",
                    description = "The highest mountain in Vietnam.",
                    category = LocationCategory.LANDMARK,
                    priority = PriorityLevel.HIGH
                ),
                LocationData(
                    id = 2,
                    name = "Cu Chi Tunnel",
                    address = "Cu Chi, Ho Chi Minh City",
                    description = "An immense network of connecting tunnels during Vietnam War.",
                    category = LocationCategory.LANDMARK,
                    priority = PriorityLevel.MEDIUM
                )
            )
        ) 
    }
    
    if (isAddingLocation) {
        // Show the add location screen
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Add New Location") },
                    navigationIcon = {
                        IconButton(onClick = { isAddingLocation = false }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Go Back"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            AddLocationScreen(
                paddingValues = innerPadding,
                onLocationAdded = { newLocation ->
                    // Generate a simple ID for the new location
                    val maxId = locations.maxByOrNull { it.id }?.id ?: 0
                    val locationWithId = newLocation.copy(id = maxId + 1)
                    
                    // Add the new location to the list
                    locations = locations + locationWithId
                    isAddingLocation = false // Return to main screen after adding
                }
            )
        }
    } else {
        // Show the main screen with locations
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
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { isAddingLocation = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Location"
                    )
                }
            }
        ) { innerPadding ->
            MainScreen(
                paddingValues = innerPadding,
                locations = locations
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TaskieTheme {
        HomeScreen()
    }
}