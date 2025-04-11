package com.example.taskie.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskie.ui.theme.TaskieTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLocationScreen(
    paddingValues: PaddingValues,
    initialLocationData: LocationData? = null,
    onLocationAdded: (LocationData) -> Unit
) {
    // Initialize form values with data if editing, or empty values if adding new
    var name by remember { mutableStateOf(initialLocationData?.name ?: "") }
    var address by remember { mutableStateOf(initialLocationData?.address ?: "") }
    var description by remember { mutableStateOf(initialLocationData?.description ?: "") }
    var selectedCategory by remember { mutableStateOf(initialLocationData?.category ?: LocationCategory.LANDMARK) }
    var selectedPriority by remember { mutableStateOf(initialLocationData?.priority ?: PriorityLevel.MEDIUM) }
    var visited by remember { mutableStateOf(initialLocationData?.visited ?: false) }
    var expandedCategoryMenu by remember { mutableStateOf(false) }
    var expandedPriorityMenu by remember { mutableStateOf(false) }
    
    // Form validation state
    var nameError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }
    
    // Validation functions
    fun validateName() {
        nameError = if (name.isBlank()) "Name cannot be empty" else null
    }
    
    fun validateAddress() {
        addressError = if (address.isBlank()) "Address cannot be empty" else null
    }
    
    fun validateForm(): Boolean {
        validateName()
        validateAddress()
        return nameError == null && addressError == null
    }
    
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (initialLocationData == null) "Enter Location Details" else "Edit Location",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            OutlinedTextField(
                value = name,
                onValueChange = { 
                    name = it
                    nameError = null // Clear error when user types
                },
                label = { Text("Location Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = nameError != null,
                supportingText = { nameError?.let { Text(it) } }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { 
                    address = it 
                    addressError = null // Clear error when user types
                },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = addressError != null,
                supportingText = { addressError?.let { Text(it) } }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category dropdown
            ExposedDropdownMenuBox(
                expanded = expandedCategoryMenu,
                onExpandedChange = { expandedCategoryMenu = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategory.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoryMenu) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedCategoryMenu,
                    onDismissRequest = { expandedCategoryMenu = false }
                ) {
                    LocationCategory.values().forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category
                                expandedCategoryMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Priority dropdown
            ExposedDropdownMenuBox(
                expanded = expandedPriorityMenu,
                onExpandedChange = { expandedPriorityMenu = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedPriority.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Priority") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPriorityMenu) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedPriorityMenu,
                    onDismissRequest = { expandedPriorityMenu = false }
                ) {
                    PriorityLevel.values().forEach { priority ->
                        DropdownMenuItem(
                            text = { Text(priority.name) },
                            onClick = {
                                selectedPriority = priority
                                expandedPriorityMenu = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Visited checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = visited,
                    onCheckedChange = { visited = it }
                )
                Text(
                    text = "Visited",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (validateForm()) {
                        onLocationAdded(
                            LocationData(
                                id = initialLocationData?.id ?: 0, // Use existing ID if editing
                                name = name,
                                address = address,
                                description = description,
                                category = selectedCategory,
                                priority = selectedPriority,
                                visited = visited
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && address.isNotBlank()
            ) {
                Text(if (initialLocationData == null) "Add Location" else "Update Location")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddLocationScreenPreview() {
    TaskieTheme {
        AddLocationScreen(
            paddingValues = PaddingValues(0.dp),
            onLocationAdded = {}
        )
    }
} 