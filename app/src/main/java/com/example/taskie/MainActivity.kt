package com.example.taskie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskie.data.LocationViewModel
import com.example.taskie.data.SortOption
import com.example.taskie.ui.AddLocationScreen
import com.example.taskie.ui.LocationData
import com.example.taskie.ui.MainScreen
import com.example.taskie.ui.theme.TaskieTheme

class MainActivity : ComponentActivity() {
    // Initialize ViewModel with factory
    private val locationViewModel: LocationViewModel by viewModels { 
        LocationViewModel.Factory(application) 
    }
    
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
                    TaskieApp(locationViewModel = locationViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskieApp(locationViewModel: LocationViewModel) {
    // States for screen navigation
    var isAddingLocation by remember { mutableStateOf(false) }
    var isEditingLocation by remember { mutableStateOf(false) }
    var currentEditLocation by remember { mutableStateOf<LocationData?>(null) }
    
    // Get the list of locations from ViewModel
    val locations by locationViewModel.locations.collectAsState()
    val searchQuery by locationViewModel.searchQuery.collectAsState()
    val sortOption by locationViewModel.sortOption.collectAsState()
    
    // Log whenever sort option changes for debugging
    if (sortOption != null) {
        android.util.Log.d("Taskie", "Current sort option: $sortOption")
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
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                    // Add new location using the provided callback
                    locationViewModel.addLocation(newLocation)
                    isAddingLocation = false // Return to main screen after adding
                }
            )
        }
    } else if (isEditingLocation && currentEditLocation != null) {
        // Show the edit location screen
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit Location") },
                    navigationIcon = {
                        IconButton(onClick = { 
                            isEditingLocation = false
                            currentEditLocation = null
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go Back"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            // Use the same AddLocationScreen but initialize with the current location data
            currentEditLocation?.let { locationData ->
                AddLocationScreen(
                    paddingValues = innerPadding,
                    initialLocationData = locationData,
                    onLocationAdded = { updatedLocation ->
                        // Update the location
                        locationViewModel.updateLocation(updatedLocation)
                        isEditingLocation = false
                        currentEditLocation = null
                    }
                )
            }
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
                locations = locations,
                onEditLocation = { location ->
                    // Prepare for editing
                    currentEditLocation = location
                    isEditingLocation = true
                },
                onDeleteLocation = { location ->
                    // Delete the location
                    locationViewModel.deleteLocation(location)
                },
                searchQuery = searchQuery,
                onSearchQueryChange = locationViewModel::setSearchQuery,
                sortOption = sortOption,
                onSortOptionChange = locationViewModel::setSortOption
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TaskieTheme {
        MainScreen(PaddingValues(0.dp))
    }
}