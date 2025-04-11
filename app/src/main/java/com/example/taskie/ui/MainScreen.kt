package com.example.taskie.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
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
    val visited: Boolean = false
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
    onDeleteLocation: (LocationData) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
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
                    text = "No locations added yet",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Press the + button to add your first location",
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
                onDeleteLocation = onDeleteLocation
            )
        }
    }
}

@Composable
fun LocationRecyclerView(
    locations: List<LocationData>,
    onLocationClick: (LocationData) -> Unit,
    onEditLocation: (LocationData) -> Unit,
    onDeleteLocation: (LocationData) -> Unit
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
                onSwipeToEdit = { 
                    onEditLocation(location)
                },
                onSwipeToDelete = {
                    scope.launch {
                        // Small delay to allow swipe animation to complete
                        delay(200)
                        onDeleteLocation(location)
                    }
                }
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
    onSwipeToDelete: () -> Unit
) {
    val scope = rememberCoroutineScope()
    
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
                            else -> Text(
                                text = "L",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
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
                
                // Visited badge if applicable
                if (location.visited) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "Visited",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
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

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TaskieTheme {
        MainScreen(PaddingValues(0.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyMainScreenPreview() {
    TaskieTheme {
        MainScreen(
            paddingValues = PaddingValues(0.dp),
            locations = emptyList()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LocationCardPreview() {
    TaskieTheme {
        SwipeableLocationCard(
            location = LocationData(
                id = 1,
                name = "Eiffel Tower",
                address = "Champ de Mars, 5 Avenue Anatole France, 75007 Paris, France",
                description = "The Eiffel Tower is a wrought-iron lattice tower on the Champ de Mars in Paris, France.",
                category = LocationCategory.LANDMARK,
                priority = PriorityLevel.HIGH
            ),
            onClick = {},
            onSwipeToEdit = {},
            onSwipeToDelete = {}
        )
    }
}
