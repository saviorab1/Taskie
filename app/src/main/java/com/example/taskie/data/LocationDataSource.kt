package com.example.taskie.data

import com.example.taskie.ui.LocationCategory
import com.example.taskie.ui.LocationData
import com.example.taskie.ui.PriorityLevel

/**
 * Provides sample data for the application
 */
object LocationDataSource {
    
    /**
     * Get sample location data
     */
    fun getSampleData(): List<LocationData> {
        return listOf(
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
            ),
            LocationData(
                id = 3,
                name = "Ben Thanh Market",
                address = "Le Loi, District 1, Ho Chi Minh City",
                description = "Popular market with local cuisine and crafts.",
                category = LocationCategory.SHOPPING,
                priority = PriorityLevel.MEDIUM
            ),
            LocationData(
                id = 4,
                name = "Hoan Kiem Lake",
                address = "Hoan Kiem District, Hanoi",
                description = "Scenic lake in the historical center of Hanoi.",
                category = LocationCategory.PARK,
                priority = PriorityLevel.HIGH
            )
        )
    }
} 