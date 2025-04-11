package com.example.taskie.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.taskie.ui.theme.TaskieTheme

// List of available images for location cards with friendly names
val availableImages = listOf(
    "fansipan" to "Fansipan",
    "cuchi" to "Cu Chi Tunnels",
    "benthanh" to "Ben Thanh Market",
    "hoankiem" to "Hoan Kiem Lake",
    "halong" to "Ha Long Bay",
    "hoian" to "Hoi An",
    "muine" to "Mui Ne Beach",
    "phongnha" to "Phong Nha Caves"
)

// Function to get friendly name from resource ID
fun getImageFriendlyName(resourceId: String): String {
    return availableImages.find { it.first == resourceId }?.second ?: 
        resourceId.replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() 
        }
}

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
    var selectedImageResId by remember { mutableStateOf(initialLocationData?.imageResId ?: "") }
    var expandedCategoryMenu by remember { mutableStateOf(false) }
    var expandedPriorityMenu by remember { mutableStateOf(false) }
    var showImageSelector by remember { mutableStateOf(false) }
    
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
            
            // Image Selection Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column {
                    Text(
                        text = "Location Image",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedImageResId.isEmpty()) 
                                "No image selected" 
                            else 
                                "Selected: ${getImageFriendlyName(selectedImageResId)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (selectedImageResId.isNotEmpty()) {
                            Button(
                                onClick = { selectedImageResId = "" },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Clear")
                            }
                        }
                        
                        Button(
                            onClick = { showImageSelector = !showImageSelector },
                            modifier = Modifier.padding(start = 0.dp)
                        ) {
                            Text(if (showImageSelector) "Hide Options" else "Show Options")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Selected image preview or placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { showImageSelector = !showImageSelector }
                            .padding(if (selectedImageResId.isEmpty()) 0.dp else 0.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageResId.isNotEmpty()) {
                            val context = LocalContext.current
                            val resourceId = context.resources.getIdentifier(
                                selectedImageResId,
                                "drawable",
                                context.packageName
                            )
                            if (resourceId != 0) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    androidx.compose.foundation.Image(
                                        painter = painterResource(id = resourceId),
                                        contentDescription = "Selected Location Image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    
                                    // Show image name overlay at the bottom
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.BottomCenter)
                                            .background(Color.Black.copy(alpha = 0.5f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = getImageFriendlyName(selectedImageResId),
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }
                                }
                            } else {
                                // If there's an issue loading the resource
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Select Image",
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Tap to select an image",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 64.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Select Image",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Tap to select an image",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 64.dp)
                            )
                        }
                    }
                    
                    // Image selector
                    if (showImageSelector) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(availableImages) { (imageName, friendlyName) ->
                                val context = LocalContext.current
                                val resourceId = context.resources.getIdentifier(
                                    imageName,
                                    "drawable",
                                    context.packageName
                                )
                                
                                if (resourceId != 0) {
                                    val isSelected = selectedImageResId == imageName
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(90.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary 
                                                            else MaterialTheme.colorScheme.outline,
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                                .clickable {
                                                    selectedImageResId = imageName
                                                }
                                        ) {
                                            androidx.compose.foundation.Image(
                                                painter = painterResource(id = resourceId),
                                                contentDescription = "Location Image Option",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            
                                            // Show checkmark for selected image
                                            if (isSelected) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .align(Alignment.TopEnd)
                                                        .padding(4.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(MaterialTheme.colorScheme.primary),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Selected",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                        
                                        // Display friendly name below image
                                        Text(
                                            text = friendlyName,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.onSurface,
                                            maxLines = 2,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
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
                                visited = visited,
                                imageResId = selectedImageResId
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

@Preview(showBackground = true)
@Composable
fun EditLocationScreenPreview() {
    val sampleLocation = LocationData(
        id = 1,
        name = "Hoi An Ancient Town",
        address = "Quang Nam Province",
        description = "Well-preserved ancient town showing a unique blend of local and foreign influences.",
        category = LocationCategory.LANDMARK,
        priority = PriorityLevel.HIGH,
        visited = true,
        imageResId = "hoian"
    )
    
    TaskieTheme {
        AddLocationScreen(
            paddingValues = PaddingValues(0.dp),
            initialLocationData = sampleLocation,
            onLocationAdded = {}
        )
    }
} 