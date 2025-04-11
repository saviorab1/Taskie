package com.example.taskie.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [LocationEntity::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TaskieDatabase : RoomDatabase() {
    
    abstract fun locationDao(): LocationDao
    
    companion object {
        @Volatile
        private var INSTANCE: TaskieDatabase? = null
        
        fun getDatabase(context: Context): TaskieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskieDatabase::class.java,
                    "taskie_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(TaskieDatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        private class TaskieDatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                
                // Populate the database with sample data when first created
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.locationDao())
                    }
                }
            }
            
            // Also populate on migration to ensure we have data
            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)
                
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.locationDao())
                    }
                }
            }
            
            // Also repopulate if the database is opened (existing installation)
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                
                // Check if there's data, if not, add sample data
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val locationDao = database.locationDao()
                        // Only populate if empty
                        if (!locationDao.hasAnyLocations()) {
                            populateDatabase(locationDao)
                        }
                    }
                }
            }
            
            suspend fun populateDatabase(locationDao: LocationDao) {
                // Delete all content
                locationDao.deleteAllLocations()
                
                // Add sample data
                val sampleLocations = LocationDataSource.getSampleData()
                locationDao.insertLocations(sampleLocations.map { it.toEntity() })
            }
        }
    }
} 