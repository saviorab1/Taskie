package com.example.taskie.data

import com.example.taskie.ui.LocationCategory
import com.example.taskie.ui.LocationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocationRepository(private val locationDao: LocationDao) {
    
    // Get all locations as a Flow of domain models
    fun getAllLocations(): Flow<List<LocationData>> {
        return locationDao.getAllLocations().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    // Get locations sorted by priority
    fun getLocationsSortedByPriority(): Flow<List<LocationData>> {
        return locationDao.getLocationsSortedByPriority().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    // Get locations by category
    fun getLocationsByCategory(category: LocationCategory): Flow<List<LocationData>> {
        return locationDao.getLocationsByCategory(category.name).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    // Insert a new location
    suspend fun insertLocation(location: LocationData): Long {
        return locationDao.insertLocation(location.toEntity())
    }
    
    // Insert multiple locations
    suspend fun insertLocations(locations: List<LocationData>) {
        locationDao.insertLocations(locations.map { it.toEntity() })
    }
    
    // Update an existing location
    suspend fun updateLocation(location: LocationData) {
        locationDao.updateLocation(location.toEntity())
    }
    
    // Delete a location
    suspend fun deleteLocation(location: LocationData) {
        locationDao.deleteLocation(location.toEntity())
    }
    
    // Delete a location by ID
    suspend fun deleteLocationById(id: Int) {
        locationDao.deleteLocationById(id)
    }
    
    // Delete all locations
    suspend fun deleteAllLocations() {
        locationDao.deleteAllLocations()
    }
    
    // Get a location by ID
    suspend fun getLocationById(id: Int): LocationData? {
        return locationDao.getLocationById(id)?.toDomainModel()
    }
    
    // Update visited status
    suspend fun updateVisitedStatus(id: Int, visited: Boolean) {
        locationDao.updateVisitedStatus(id, visited)
    }
} 