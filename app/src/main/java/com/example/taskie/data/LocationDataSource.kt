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
                priority = PriorityLevel.HIGH,
                imageResId = "fansipan"
            ),
            LocationData(
                id = 2,
                name = "Cu Chi Tunnel",
                address = "Cu Chi, Ho Chi Minh City",
                description = "An immense network of connecting tunnels during Vietnam War.",
                category = LocationCategory.LANDMARK,
                priority = PriorityLevel.MEDIUM,
                imageResId = "cuchi"
            ),
            LocationData(
                id = 3,
                name = "Ben Thanh Market",
                address = "Le Loi, District 1, Ho Chi Minh City",
                description = "Popular market with local cuisine and crafts.",
                category = LocationCategory.SHOPPING,
                priority = PriorityLevel.MEDIUM,
                imageResId = "benthanh"
            ),
            LocationData(
                id = 4,
                name = "Hoan Kiem Lake",
                address = "Hoan Kiem District, Hanoi",
                description = "Scenic lake in the historical center of Hanoi.",
                category = LocationCategory.PARK,
                priority = PriorityLevel.HIGH,
                imageResId = "hoankiem"
            ),
            LocationData(
                id = 5,
                name = "Ha Long Bay",
                address = "Quang Ninh Province",
                description = "UNESCO World Heritage Site known for its emerald waters and limestone karsts.",
                category = LocationCategory.LANDMARK,
                priority = PriorityLevel.HIGH,
                imageResId = "halong"
            ),
            LocationData(
                id = 6,
                name = "Hoi An Ancient Town",
                address = "Quang Nam Province",
                description = "Well-preserved ancient town showing a unique blend of local and foreign influences.",
                category = LocationCategory.LANDMARK,
                priority = PriorityLevel.HIGH,
                imageResId = "hoian"
            ),
            LocationData(
                id = 7,
                name = "Mui Ne Beach",
                address = "Phan Thiet, Binh Thuan Province",
                description = "Beautiful beach resort town known for its sand dunes and water sports.",
                category = LocationCategory.BEACH,
                priority = PriorityLevel.MEDIUM,
                imageResId = "muine"
            ),
            LocationData(
                id = 8,
                name = "Phong Nha Caves",
                address = "Quang Binh Province",
                description = "One of the world's largest cave systems with stunning formations.",
                category = LocationCategory.LANDMARK,
                priority = PriorityLevel.HIGH,
                imageResId = "phongnha"
            )
        )
    }
} 