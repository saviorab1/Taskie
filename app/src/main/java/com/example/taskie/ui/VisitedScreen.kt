package com.example.taskie.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.taskie.data.SortOption
import com.example.taskie.ui.theme.TaskieTheme

@Composable
fun VisitedScreen(
    paddingValues: PaddingValues,
    visitedLocations: List<LocationData> = emptyList(),
    onClearVisitedLocations: () -> Unit,
    onToggleVisited: (LocationData) -> Unit,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    sortOption: SortOption = SortOption.NAME_ASC,
    onSortOptionChange: (SortOption) -> Unit = {}
) {
    var showClearConfirmationDialog by remember { mutableStateOf(false) }
    var showSortOptions by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        // Search and Sort UI
        SearchAndSortBar(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            currentSortOption = sortOption,
            onSortOptionChange = onSortOptionChange
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Header with clear button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Visited Places",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight
            )
            
            if (visitedLocations.isNotEmpty()) {
                TextButton(
                    onClick = { showClearConfirmationDialog = true }
                ) {
                    Text("Clear All")
                }
            }
        }
        
        if (visitedLocations.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No places visited yet. Start exploring!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(visitedLocations) { location ->
                    SwipeableLocationCard(
                        location = location,
                        onClick = { /* Handle click if needed */ },
                        onSwipeToEdit = { /* Handle edit if needed */ },
                        onSwipeToDelete = { /* Handle delete if needed */ },
                        onToggleVisited = { onToggleVisited(location) }
                    )
                    Divider()
                }
            }
        }
    }
    
    // Clear confirmation dialog
    if (showClearConfirmationDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showClearConfirmationDialog = false },
            title = { Text("Clear Visited Places") },
            text = { Text("Are you sure you want to clear all visited places? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearVisitedLocations()
                        showClearConfirmationDialog = false
                    }
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearConfirmationDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun VisitedLocationCard(
    location: LocationData,
    onToggleVisited: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = location.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = MaterialTheme.typography.titleMedium.fontWeight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = location.address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Button(
                    onClick = onToggleVisited
                ) {
                    Text("Mark as Unvisited")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VisitedScreenPreview() {
    TaskieTheme {
        VisitedScreen(
            paddingValues = PaddingValues(0.dp),
            visitedLocations = listOf(
                LocationData(
                    id = 1,
                    name = "Sample Location",
                    address = "123 Sample St",
                    description = "A sample location",
                    category = LocationCategory.RESTAURANT,
                    priority = PriorityLevel.HIGH,
                    visited = true
                )
            ),
            onClearVisitedLocations = {},
            onToggleVisited = {}
        )
    }
} 