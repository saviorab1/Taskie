package com.example.taskie.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskie.R
import com.example.taskie.ui.theme.TaskieTheme

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
fun MainScreen(paddingValues: PaddingValues) {
    // Sample data for demonstration
    val locations = remember {
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
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LocationRecyclerView(
            locations = locations,
            onLocationClick = { /* Handle location click */ }
        )
    }
}

@Composable
fun LocationRecyclerView(
    locations: List<LocationData>,
    onLocationClick: (LocationData) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(locations) { location ->
            LocationCard(
                location = location,
                onClick = { onLocationClick(location) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationCard(
    location: LocationData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
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
            Divider()
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
fun LocationCardPreview() {
    TaskieTheme {
        LocationCard(
            location = LocationData(
                id = 1,
                name = "Eiffel Tower",
                address = "Champ de Mars, 5 Avenue Anatole France, 75007 Paris, France",
                description = "The Eiffel Tower is a wrought-iron lattice tower on the Champ de Mars in Paris, France.",
                category = LocationCategory.LANDMARK,
                priority = PriorityLevel.HIGH
            ),
            onClick = {}
        )
    }
} 