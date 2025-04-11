package com.example.taskie.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskie.ui.LocationCategory
import com.example.taskie.ui.LocationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: LocationRepository
    
    // StateFlow to expose locations to the UI
    private val _locations = MutableStateFlow<List<LocationData>>(emptyList())
    val locations: StateFlow<List<LocationData>> = _locations.asStateFlow()
    
    init {
        val database = TaskieDatabase.getDatabase(application)
        val locationDao = database.locationDao()
        repository = LocationRepository(locationDao)
        
        // Force insert sample data in case the database is empty
        viewModelScope.launch {
            val count = repository.getLocationsCount()
            android.util.Log.d("Taskie", "Current location count: $count")
            
            if (count == 0) {
                android.util.Log.d("Taskie", "Database is empty, loading sample data...")
                val sampleData = LocationDataSource.getSampleData()
                repository.insertLocations(sampleData)
            }
        }
        
        // Load locations when ViewModel is created
        getAllLocations()
    }
    
    // Function to get all locations and update the StateFlow
    fun getAllLocations() {
        viewModelScope.launch {
            android.util.Log.d("Taskie", "Loading all locations...")
            repository.getAllLocations().collect { locationsList ->
                android.util.Log.d("Taskie", "Loaded ${locationsList.size} locations")
                for (location in locationsList) {
                    android.util.Log.d("Taskie", "Location: ${location.name}, Image: ${location.imageResId}")
                }
                _locations.value = locationsList
            }
        }
    }
    
    // Function to get locations sorted by priority
    fun getLocationsSortedByPriority() {
        viewModelScope.launch {
            repository.getLocationsSortedByPriority().collect { locationsList ->
                _locations.value = locationsList
            }
        }
    }
    
    // Function to filter locations by category
    fun filterByCategory(category: LocationCategory) {
        viewModelScope.launch {
            repository.getLocationsByCategory(category).collect { locationsList ->
                _locations.value = locationsList
            }
        }
    }
    
    // Function to add a new location
    fun addLocation(location: LocationData) {
        viewModelScope.launch {
            repository.insertLocation(location)
        }
    }
    
    // Function to update an existing location
    fun updateLocation(location: LocationData) {
        viewModelScope.launch {
            repository.updateLocation(location)
        }
    }
    
    // Function to delete a location
    fun deleteLocation(location: LocationData) {
        viewModelScope.launch {
            repository.deleteLocation(location)
        }
    }
    
    // Function to toggle the visited status
    fun toggleVisitedStatus(location: LocationData) {
        viewModelScope.launch {
            repository.updateVisitedStatus(location.id, !location.visited)
        }
    }
    
    // Factory for creating the ViewModel with correct dependencies
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LocationViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 