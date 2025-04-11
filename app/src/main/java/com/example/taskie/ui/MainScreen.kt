package com.example.taskie.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.taskie.data.SortOption
import com.example.taskie.ui.theme.TaskieTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

// Data class for location item
data class LocationData(
    val id: Int,
    val name: String,
    val address: String,
    val description: String,
    val category: LocationCategory,
    val priority: PriorityLevel,
    val visited: Boolean = false,
    val imageResId: String = "" // Image resource ID for location photo
)

// Enum for location categories
enum class LocationCategory {
    RESTAURANT, PARK, LANDMARK, SHOPPING, MUSEUM, BEACH, OTHER
}

// Enum for priority levels
enum class PriorityLevel {
    LOW, MEDIUM, HIGH
}

// Extension function to get color based on priority
@Composable
fun PriorityLevel.getColor(): Color {
    return when (this) {
        PriorityLevel.LOW -> Color(0xFF4CAF50)     // Green
        PriorityLevel.MEDIUM -> Color(0xFFFFC107)  // Amber
        PriorityLevel.HIGH -> Color(0xFFF44336)    // Red
    }
}

// Extension function to get icon based on category
@Composable
fun LocationCategory.getIcon() {
    when (this) {
        LocationCategory.RESTAURANT -> Icon(
            imageVector = Icons.Outlined.FavoriteBorder,
            contentDescription = "Restaurant",
            tint = MaterialTheme.colorScheme.primary
        )
        LocationCategory.LANDMARK -> Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Landmark",
            tint = MaterialTheme.colorScheme.primary
        )
        else -> Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Other",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun MainScreen(
    paddingValues: PaddingValues,
    locations: List<LocationData> = emptyList(),
    onEditLocation: (LocationData) -> Unit = {},
    onDeleteLocation: (LocationData) -> Unit = {},
    onToggleVisited: (LocationData) -> Unit = {},
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    sortOption: SortOption = SortOption.NAME_ASC,
    onSortOptionChange: (SortOption) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Search and Sort UI
        SearchAndSortBar(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            currentSortOption = sortOption,
            onSortOptionChange = onSortOptionChange
        )
        
        // Main content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (locations.isEmpty()) {
                // Show empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No locations found",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (searchQuery.isEmpty()) 
                            "Press the + button to add your first location" 
                        else 
                            "Try a different search term",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Location List
                LocationRecyclerView(
                    locations = locations,
                    onLocationClick = { /* Handle location click */ },
                    onEditLocation = onEditLocation,
                    onDeleteLocation = onDeleteLocation,
                    onToggleVisited = onToggleVisited
                )
            }
        }
    }
}

@Composable
fun LocationRecyclerView(
    locations: List<LocationData>,
    onLocationClick: (LocationData) -> Unit,
    onEditLocation: (LocationData) -> Unit,
    onDeleteLocation: (LocationData) -> Unit,
    onToggleVisited: (LocationData) -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = locations,
            key = { it.id }
        ) { location ->
            SwipeableLocationCard(
                location = location,
                onClick = { onLocationClick(location) },
                onSwipeToEdit = { onEditLocation(location) },
                onSwipeToDelete = { onDeleteLocation(location) },
                onToggleVisited = { onToggleVisited(location) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableLocationCard(
    location: LocationData,
    onClick: () -> Unit,
    onSwipeToEdit: () -> Unit,
    onSwipeToDelete: () -> Unit,
    onToggleVisited: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // Get the resource ID for the image
    val imageResId = remember(location) {
        if (location.imageResId.isNotEmpty()) {
            val resourceId = context.resources.getIdentifier(
                location.imageResId,
                "drawable",
                context.packageName
            )
            // If the resource isn't found, log the issue
            if (resourceId == 0) {
                android.util.Log.e("Taskie", "Resource not found: ${location.imageResId}")
                android.util.Log.e("Taskie", "Available resources: " + 
                    context.resources.getIdentifier("phongnha", "drawable", context.packageName) + ", " +
                    context.resources.getIdentifier("muine", "drawable", context.packageName) + ", " +
                    context.resources.getIdentifier("hoian", "drawable", context.packageName) + ", " +
                    context.resources.getIdentifier("benthanh", "drawable", context.packageName) + ", " +
                    context.resources.getIdentifier("halong", "drawable", context.packageName) + ", " +
                    context.resources.getIdentifier("hoankiem", "drawable", context.packageName) + ", " +
                    context.resources.getIdentifier("cuchi", "drawable", context.packageName) + ", " +
                    context.resources.getIdentifier("fansipan", "drawable", context.packageName)
                )
            }
            resourceId
        } else {
            0
        }
    }
    
    // For horizontal dragging
    var offsetX by remember { mutableFloatStateOf(0f) }
    
    // Threshold for triggering actions (in pixels)
    val actionThreshold = with(LocalDensity.current) { 100.dp.toPx() }
    
    // The current active action (based on drag direction and distance)
    var currentAction by remember { mutableStateOf<String?>(null) }
    
    // Animated values for background elements
    val deleteIconAlpha by animateFloatAsState(
        targetValue = if (offsetX > 0) 1f else 0f,
        label = "Delete Icon Alpha"
    )
    
    val editIconAlpha by animateFloatAsState(
        targetValue = if (offsetX < 0) 1f else 0f,
        label = "Edit Icon Alpha"
    )
    
    val deleteIconScale by animateFloatAsState(
        targetValue = if (offsetX > actionThreshold / 2) 1f else 0.5f,
        label = "Delete Icon Scale"
    )
    
    val editIconScale by animateFloatAsState(
        targetValue = if (offsetX < -actionThreshold / 2) 1f else 0.5f,
        label = "Edit Icon Scale"
    )
    
    // Animated values for background colors
    val deleteBackgroundAlpha by animateFloatAsState(
        targetValue = if (offsetX > 0) min(offsetX / actionThreshold, 1f) else 0f,
        label = "Delete Background Alpha"
    )
    
    val editBackgroundAlpha by animateFloatAsState(
        targetValue = if (offsetX < 0) min(-offsetX / actionThreshold, 1f) else 0f,
        label = "Edit Background Alpha"
    )
    
    // Draggable state
    val draggableState = rememberDraggableState { delta ->
        // Update the horizontal offset
        offsetX += delta
        
        // Determine the current action based on drag distance
        currentAction = when {
            offsetX > actionThreshold -> "delete"
            offsetX < -actionThreshold -> "edit"
            else -> null
        }
    }
    
    // Store measurements of this card to size the background
    var cardHeight by remember { mutableStateOf(0) }
    val density = LocalDensity.current.density
    
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Background with action indicators - sized to match the card's height
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((cardHeight / density).dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            // Delete background (visible when swiping right)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFC3F39).copy(alpha = deleteBackgroundAlpha)) // Red for delete with alpha
                    .align(Alignment.CenterStart),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .scale(deleteIconScale)
                        .alpha(deleteIconAlpha)
                )
            }
            
            // Edit background (visible when swiping left)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF3D91DD).copy(alpha = editBackgroundAlpha)) // Blue for edit with alpha
                    .align(Alignment.CenterEnd),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .scale(editIconScale)
                        .alpha(editIconAlpha)
                )
            }
        }
        
        // Foreground - The actual card that can be dragged
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        // When drag stops, check if we crossed the threshold
                        when {
                            offsetX > actionThreshold -> {
                                // Trigger delete (swipe right)
                                scope.launch {
                                    onSwipeToDelete()
                                    // Reset offset after action completes
                                    offsetX = 0f
                                }
                            }
                            offsetX < -actionThreshold -> {
                                // Trigger edit (swipe left)
                                scope.launch {
                                    onSwipeToEdit()
                                    // Reset offset after action completes
                                    offsetX = 0f
                                }
                            }
                            else -> {
                                // Not enough to trigger action, spring back to center
                                scope.launch {
                                    offsetX = 0f
                                }
                            }
                        }
                    }
                )
                .onGloballyPositioned { coordinates ->
                    // Store the card's height to size the background properly
                    cardHeight = coordinates.size.height
                },
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            shape = RoundedCornerShape(12.dp),
            onClick = onClick
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Display location image if available
                if (imageResId != 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = "Location Image: ${location.name}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        location.category.getIcon()
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = location.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = location.address,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    
                    // Priority indicator
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(location.priority.getColor(), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        when (location.priority) {
                            PriorityLevel.HIGH -> Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "High Priority",
                                tint = Color.White
                            )
                            PriorityLevel.MEDIUM -> Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = "Medium Priority",
                                tint = Color.White
                            )
                            PriorityLevel.LOW -> Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = "Low Priority",
                                tint = Color.White
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = location.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Visited button
                Button(
                    onClick = onToggleVisited,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (location.visited) 
                            MaterialTheme.colorScheme.tertiaryContainer 
                        else 
                            MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = if (location.visited) "Mark as Unvisited" else "Mark as Visited",
                        color = if (location.visited) 
                            MaterialTheme.colorScheme.onTertiaryContainer 
                        else 
                            MaterialTheme.colorScheme.onPrimary
                    )
                }
                
                // Display swipe hint if needed
                if (abs(offsetX) < 10f) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Swipe right to delete or left to edit",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndSortBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    currentSortOption: SortOption,
    onSortOptionChange: (SortOption) -> Unit
) {
    var showSortOptions by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search locations...") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )
        
        // Sort options
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sort by:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Current sort option display
            Box {
                Row(
                    modifier = Modifier
                        .clickable { showSortOptions = !showSortOptions }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (currentSortOption) {
                            SortOption.NAME_ASC -> "Name (A-Z)"
                            SortOption.NAME_DESC -> "Name (Z-A)"
                            SortOption.PRIORITY_HIGH -> "Priority (High to Low)"
                            SortOption.PRIORITY_LOW -> "Priority (Low to High)"
                            SortOption.CATEGORY -> "Category"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Sort options",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Dropdown menu for sort options
                DropdownMenu(
                    expanded = showSortOptions,
                    onDismissRequest = { showSortOptions = false }
                ) {
                    // Name A-Z option
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = currentSortOption == SortOption.NAME_ASC,
                                    onClick = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Name (A-Z)")
                            }
                        },
                        onClick = {
                            onSortOptionChange(SortOption.NAME_ASC)
                            showSortOptions = false
                        }
                    )
                    
                    // Name Z-A option
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = currentSortOption == SortOption.NAME_DESC,
                                    onClick = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Name (Z-A)")
                            }
                        },
                        onClick = {
                            onSortOptionChange(SortOption.NAME_DESC)
                            showSortOptions = false
                        }
                    )
                    
                    // Priority High to Low option
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = currentSortOption == SortOption.PRIORITY_HIGH,
                                    onClick = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Priority (High to Low)")
                            }
                        },
                        onClick = {
                            onSortOptionChange(SortOption.PRIORITY_HIGH)
                            showSortOptions = false
                        }
                    )
                    
                    // Priority Low to High option
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = currentSortOption == SortOption.PRIORITY_LOW,
                                    onClick = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Priority (Low to High)")
                            }
                        },
                        onClick = {
                            onSortOptionChange(SortOption.PRIORITY_LOW)
                            showSortOptions = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TaskieTheme {
        MainScreen(
            paddingValues = PaddingValues(0.dp),
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

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    TaskieTheme {
        SearchAndSortBar(
            searchQuery = "",
            onSearchQueryChange = {},
            currentSortOption = SortOption.NAME_ASC,
            onSortOptionChange = {}
        )
    }
}
