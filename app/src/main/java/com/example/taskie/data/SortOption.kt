package com.example.taskie.data

enum class SortOption {
    NAME_ASC,
    NAME_DESC,
    PRIORITY_HIGH,
    PRIORITY_LOW,
    CATEGORY;

    fun getDisplayName(): String {
        return when (this) {
            NAME_ASC -> "Name (A-Z)"
            NAME_DESC -> "Name (Z-A)"
            PRIORITY_HIGH -> "Priority (High to Low)"
            PRIORITY_LOW -> "Priority (Low to High)"
            CATEGORY -> "Category"
        }
    }
} 