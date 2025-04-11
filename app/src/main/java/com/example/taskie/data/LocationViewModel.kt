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

// Enum for sorting options
enum class SortOption {
    ALPHABETICAL, PRIORITY_HIGH_TO_LOW, PRIORITY_LOW_TO_HIGH, NAME_A_TO_Z, NAME_Z_TO_A
}

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: LocationRepository
    
    // StateFlow to expose locations to the UI
    private val _locations = MutableStateFlow<List<LocationData>>(emptyList())
    val locations: StateFlow<List<LocationData>> = _locations.asStateFlow()
    
    // State for current search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // State for current sort option
    private val _sortOption = MutableStateFlow(SortOption.PRIORITY_HIGH_TO_LOW)
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
            SortOption.ALPHABETICAL, SortOption.NAME_A_TO_Z -> {
                filteredList.sortedBy { it.name }
            }
            SortOption.NAME_Z_TO_A -> {
                filteredList.sortedByDescending { it.name }
            }
            SortOption.PRIORITY_HIGH_TO_LOW -> {
                filteredList.sortedWith(
                    compareByDescending<LocationData> { 
                        when (it.priority) {
                            PriorityLevel.HIGH -> 3
                            PriorityLevel.MEDIUM -> 2
                            PriorityLevel.LOW -> 1
                        }
                    }.thenBy { it.name }
                )
            }
            SortOption.PRIORITY_LOW_TO_HIGH -> {
                filteredList.sortedWith(
                    compareBy<LocationData> { 
                        when (it.priority) {
                            PriorityLevel.LOW -> 1
                            PriorityLevel.MEDIUM -> 2
                            PriorityLevel.HIGH -> 3
                        }
                    }.thenBy { it.name }
                )
            }
        }
        
        // Log sorting for debugging
        android.util.Log.d("Taskie", "Applied sort: ${_sortOption.value}, results: ${filteredList.size}")
        
        _locations.value = filteredList
    }
    
    // Function to get locations sorted by priority (legacy function)
    fun getLocationsSortedByPriority() {
        setSortOption(SortOption.PRIORITY_HIGH_TO_LOW)
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
            repository.updateVisitedStatus(location.id, !location.visited)
            getAllLocations() // Refresh the list
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