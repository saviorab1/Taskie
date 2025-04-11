package com.example.taskie

import android.app.Application
import com.example.taskie.data.TaskieDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class TaskieApplication : Application() {
    // Application scope - will be canceled when the application is destroyed
    private val applicationScope = CoroutineScope(SupervisorJob())
    
    // Database instance - lazy initialization
    val database by lazy { 
        TaskieDatabase.getDatabase(this)
    }
    
    override fun onCreate() {
        super.onCreate()
        // Additional initialization code can be added here
    }
    
    override fun onTerminate() {
        super.onTerminate()
        // Cancel the application scope when the app is terminated
        applicationScope.cancel()
    }
} 