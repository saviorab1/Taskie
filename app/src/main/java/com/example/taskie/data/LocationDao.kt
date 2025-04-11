package com.example.taskie.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations ORDER BY name ASC")
    fun getAllLocations(): Flow<List<LocationEntity>>
    
    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getLocationById(id: Int): LocationEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<LocationEntity>)
    
    @Update
    suspend fun updateLocation(location: LocationEntity)
    
    @Delete
    suspend fun deleteLocation(location: LocationEntity)
    
    @Query("DELETE FROM locations WHERE id = :id")
    suspend fun deleteLocationById(id: Int)
    
    @Query("DELETE FROM locations")
    suspend fun deleteAllLocations()
    
    @Query("SELECT * FROM locations WHERE visited = 0 ORDER BY " +
           "CASE priority " +
           "WHEN 'HIGH' THEN 1 " +
           "WHEN 'MEDIUM' THEN 2 " +
           "ELSE 3 END, name ASC")
    fun getLocationsSortedByPriority(): Flow<List<LocationEntity>>
    
    @Query("SELECT * FROM locations WHERE category = :category")
    fun getLocationsByCategory(category: String): Flow<List<LocationEntity>>
    
    @Query("UPDATE locations SET visited = :visited WHERE id = :id")
    suspend fun updateVisitedStatus(id: Int, visited: Boolean)
} 