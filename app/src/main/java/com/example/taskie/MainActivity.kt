package com.example.taskie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.taskie.data.LocationViewModel
import com.example.taskie.data.SortOption
import com.example.taskie.navigation.NavDestination
import com.example.taskie.ui.AddLocationScreen
import com.example.taskie.ui.LocationData
import com.example.taskie.ui.MainScreen
import com.example.taskie.ui.ProfileScreen
import com.example.taskie.ui.VisitedScreen
import com.example.taskie.ui.components.TaskieBottomNavigation
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
    
    // Navigation controller for app navigation
    val navController = rememberNavController()
    
    // Get the list of locations from ViewModel
    val locations by locationViewModel.locations.collectAsState()
    val unvisitedLocations by locationViewModel.unvisitedLocations.collectAsState()
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
        // Main app with bottom navigation
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
            bottomBar = {
                TaskieBottomNavigation(navController = navController)
            },
            floatingActionButton = {
                if (navController.currentBackStackEntryAsState().value?.destination?.route == NavDestination.Home.route) {
                    FloatingActionButton(
                        onClick = { isAddingLocation = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Location"
                        )
                    }
                }
            }
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                paddingValues = innerPadding,
                locations = unvisitedLocations,
                onEditLocation = { location ->
                    // Prepare for editing
                    currentEditLocation = location
                    isEditingLocation = true
                },
                onDeleteLocation = { location ->
                    // Delete the location
                    locationViewModel.deleteLocation(location)
                },
                onToggleVisited = { location ->
                    locationViewModel.toggleVisitedStatus(location)
                },
                searchQuery = searchQuery,
                onSearchQueryChange = locationViewModel::setSearchQuery,
                sortOption = sortOption ?: SortOption.NAME_ASC,
                onSortOptionChange = locationViewModel::setSortOption,
                visitedLocations = locations.filter { it.visited },
                onClearVisitedLocations = {
                    locationViewModel.clearAllVisitedLocations()
                }
            )
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    locations: List<LocationData>,
    onEditLocation: (LocationData) -> Unit,
    onDeleteLocation: (LocationData) -> Unit,
    onToggleVisited: (LocationData) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    sortOption: SortOption,
    onSortOptionChange: (SortOption) -> Unit,
    visitedLocations: List<LocationData>,
    onClearVisitedLocations: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.Home.route,
        // Remove default animations
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable(NavDestination.Home.route) {
            MainScreen(
                paddingValues = paddingValues,
                locations = locations,
                onEditLocation = onEditLocation,
                onDeleteLocation = onDeleteLocation,
                onToggleVisited = onToggleVisited,
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                sortOption = sortOption,
                onSortOptionChange = onSortOptionChange
            )
        }
        
        composable(NavDestination.Visited.route) {
            VisitedScreen(
                paddingValues = paddingValues,
                visitedLocations = visitedLocations,
                onClearVisitedLocations = onClearVisitedLocations,
                onToggleVisited = onToggleVisited,
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                sortOption = sortOption,
                onSortOptionChange = onSortOptionChange
            )
        }
        
        composable(NavDestination.Profile.route) {
            ProfileScreen(
                paddingValues = paddingValues,
                visitedLocations = visitedLocations,
                onClearVisitedLocations = onClearVisitedLocations
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TaskieTheme {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = {
                TaskieBottomNavigation(navController = navController)
            }
        ) { innerPadding ->
            MainScreen(
                paddingValues = innerPadding,
                locations = emptyList(),
                onEditLocation = {},
                onDeleteLocation = {},
                onToggleVisited = {},
                searchQuery = "",
                onSearchQueryChange = {},
                sortOption = SortOption.NAME_ASC,
                onSortOptionChange = {}
            )
        }
    }
}