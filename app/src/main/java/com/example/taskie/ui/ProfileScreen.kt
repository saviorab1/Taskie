package com.example.taskie.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.taskie.R
import com.example.taskie.ui.theme.TaskieTheme

data class UserProfile(
    var name: String = "Dinh Le Hoang Anh",
    var studentId: String = "105508318",
    var hobbies: String = "Travelling, Gaming, Sleeping"
)

@Composable
fun ProfileScreen(
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    visitedLocations: List<LocationData> = emptyList()
) {
    // User profile state
    var userProfile by remember { mutableStateOf(UserProfile()) }
    
    // Edit dialog states
    var showNameEditDialog by remember { mutableStateOf(false) }
    var showStudentIdEditDialog by remember { mutableStateOf(false) }
    var showHobbiesEditDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile picture
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.deadinside),
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // User name
        Text(
            text = userProfile.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // User details with edit buttons
        EditableUserDetailCard(
            title = "Name",
            value = userProfile.name,
            onEditClick = { showNameEditDialog = true }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        EditableUserDetailCard(
            title = "SID",
            value = userProfile.studentId,
            onEditClick = { showStudentIdEditDialog = true }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Hobbies
        EditableUserDetailCard(
            title = "Hobbies",
            value = userProfile.hobbies,
            onEditClick = { showHobbiesEditDialog = true }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Visited places section
        Text(
            text = "Places Visited",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            fontWeight = FontWeight.Bold
        )
        
        if (visitedLocations.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No places visited yet. Start exploring!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // List of visited places would go here
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                visitedLocations.forEach { location ->
                    VisitedLocationItem(location = location)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
    
    // Edit dialogs
    if (showNameEditDialog) {
        EditFieldDialog(
            title = "Edit Name",
            currentValue = userProfile.name,
            onDismiss = { showNameEditDialog = false },
            onSave = { newValue ->
                userProfile = userProfile.copy(name = newValue)
                showNameEditDialog = false
            }
        )
    }
    
    if (showStudentIdEditDialog) {
        EditFieldDialog(
            title = "Edit Student ID",
            currentValue = userProfile.studentId,
            onDismiss = { showStudentIdEditDialog = false },
            onSave = { newValue ->
                userProfile = userProfile.copy(studentId = newValue)
                showStudentIdEditDialog = false
            }
        )
    }
    
    if (showHobbiesEditDialog) {
        EditFieldDialog(
            title = "Edit Hobbies",
            currentValue = userProfile.hobbies,
            onDismiss = { showHobbiesEditDialog = false },
            onSave = { newValue ->
                userProfile = userProfile.copy(hobbies = newValue)
                showHobbiesEditDialog = false
            }
        )
    }
}

@Composable
fun EditableUserDetailCard(title: String, value: String, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit $title",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun EditFieldDialog(
    title: String,
    currentValue: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var fieldValue by remember { mutableStateOf(currentValue) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = fieldValue,
                onValueChange = { fieldValue = it },
                singleLine = title != "Edit Hobbies", // Allow multiple lines for hobbies
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(fieldValue) }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun VisitedLocationItem(location: LocationData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Location image or category icon
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (location.imageResId.isNotEmpty()) {
                Image(
                    painter = painterResource(
                        id = LocalContext.current.resources.getIdentifier(
                            location.imageResId,
                            "drawable",
                            LocalContext.current.packageName
                        )
                    ),
                    contentDescription = "Location image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    location.category.getIcon()
                }
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = location.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = location.address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    TaskieTheme {
        ProfileScreen(
            paddingValues = androidx.compose.foundation.layout.PaddingValues(0.dp)
        )
    }
} 