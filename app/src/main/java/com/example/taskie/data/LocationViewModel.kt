package com.example.taskie.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskie.ui.LocationCategory
import com.example.taskie.ui.LocationData
import com.example.taskie.ui.PriorityLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: LocationRepository
    
    // StateFlow to expose locations to the UI
    private val _locations = MutableStateFlow<List<LocationData>>(emptyList())
    val locations: StateFlow<List<LocationData>> = _locations.asStateFlow()
    
    // StateFlow to expose unvisited locations to the UI
    private val _unvisitedLocations = MutableStateFlow<List<LocationData>>(emptyList())
    val unvisitedLocations: StateFlow<List<LocationData>> = _unvisitedLocations.asStateFlow()
    
    // State for current search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // State for current sort option
    private val _sortOption = MutableStateFlow(SortOption.PRIORITY_HIGH)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()
    
    // Raw list of all locations before filtering or sorting
    private var allLocations = listOf<LocationData>()
    
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
                allLocations = locationsList
                applyFiltersAndSort()
            }
        }
    }
    
    // Function to set the search query
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        applyFiltersAndSort()
    }
    
    // Function to set the sort option
    fun setSortOption(option: SortOption) {
        _sortOption.value = option
        applyFiltersAndSort()
    }
    
    // Apply current search and sort settings to the locations list
    private fun applyFiltersAndSort() {
        var filteredList = allLocations
        
        // Apply search filter if there's a query
        if (_searchQuery.value.isNotEmpty()) {
            val query = _searchQuery.value.lowercase()
            filteredList = filteredList.filter { location ->
                location.name.lowercase().contains(query) ||
                location.address.lowercase().contains(query) ||
                location.description.lowercase().contains(query)
            }
        }
        
        // Apply sorting
        filteredList = when (_sortOption.value) {
            SortOption.NAME_ASC -> {
                filteredList.sortedBy { it.name }
            }
            SortOption.NAME_DESC -> {
                filteredList.sortedByDescending { it.name }
            }
            SortOption.PRIORITY_HIGH -> {
                filteredList.sortedByDescending { it.priority }
            }
            SortOption.PRIORITY_LOW -> {
                filteredList.sortedBy { it.priority }
            }
            SortOption.CATEGORY -> {
                filteredList.sortedBy { it.category.name }
            }
        }
        
        // Log sorting for debugging
        android.util.Log.d("Taskie", "Applied sort: ${_sortOption.value}, results: ${filteredList.size}")
        
        // Update both lists
        _locations.value = filteredList
        _unvisitedLocations.value = filteredList.filter { !it.visited }
    }
    
    // Function to get locations sorted by priority (legacy function)
    fun getLocationsSortedByPriority() {
        setSortOption(SortOption.PRIORITY_HIGH)
    }
    
    // Function to filter locations by category
    fun filterByCategory(category: LocationCategory) {
        viewModelScope.launch {
            repository.getLocationsByCategory(category).collect { locationsList ->
                allLocations = locationsList
                applyFiltersAndSort()
            }
        }
    }
    
    // Function to add a new location
    fun addLocation(location: LocationData) {
        viewModelScope.launch {
            repository.insertLocation(location)
            getAllLocations() // Refresh the list
        }
    }
    
    // Function to update an existing location
    fun updateLocation(location: LocationData) {
        viewModelScope.launch {
            repository.updateLocation(location)
            getAllLocations() // Refresh the list
        }
    }
    
    // Function to delete a location
    fun deleteLocation(location: LocationData) {
        viewModelScope.launch {
            repository.deleteLocation(location)
            getAllLocations() // Refresh the list
        }
    }
    
    // Function to toggle the visited status
    fun toggleVisitedStatus(location: LocationData) {
        viewModelScope.launch {
            val updatedLocation = location.copy(visited = !location.visited)
            repository.updateLocation(updatedLocation)
            getAllLocations() // Refresh the list
        }
    }
    
    // Function to clear all visited locations
    fun clearAllVisitedLocations() {
        viewModelScope.launch {
            val updatedLocations = allLocations.map { location ->
                if (location.visited) {
                    location.copy(visited = false)
                } else {
                    location
                }
            }
            updatedLocations.forEach { location ->
                repository.updateLocation(location)
            }
            getAllLocations() // Refresh the list
        }
    }
    
    // Function to get only visited locations
    fun getVisitedLocations(): List<LocationData> {
        return allLocations.filter { it.visited }
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