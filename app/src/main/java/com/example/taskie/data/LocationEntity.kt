package com.example.taskie.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.taskie.ui.LocationCategory
import com.example.taskie.ui.LocationData
import com.example.taskie.ui.PriorityLevel

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val address: String,
    val description: String,
    val category: LocationCategory,
    val priority: PriorityLevel,
    val visited: Boolean = false,
    val imageResId: String = ""
)

// Extension functions to convert between domain and entity models
fun LocationEntity.toDomainModel(): LocationData {
    return LocationData(
        id = id,
        name = name,
        address = address,
        description = description,
        category = category,
        priority = priority,
        visited = visited,
        imageResId = imageResId
    )
}

fun LocationData.toEntity(): LocationEntity {
    return LocationEntity(
        id = id,
        name = name,
        address = address,
        description = description,
        category = category,
        priority = priority,
        visited = visited,
        imageResId = imageResId
    )
}

// Type converters for Room to handle enum classes
class Converters {
    @TypeConverter
    fun fromLocationCategory(value: LocationCategory): String {
        return value.name
    }

    @TypeConverter
    fun toLocationCategory(value: String): LocationCategory {
        return LocationCategory.valueOf(value)
    }

    @TypeConverter
    fun fromPriorityLevel(value: PriorityLevel): String {
        return value.name
    }

    @TypeConverter
    fun toPriorityLevel(value: String): PriorityLevel {
        return PriorityLevel.valueOf(value)
    }
} 