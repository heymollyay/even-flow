package com.example.periodtracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.periodtracker.Biometrics
import com.example.periodtracker.EncryptionManager
import com.example.periodtracker.data.AppDatabase
import com.example.periodtracker.data.CycleData
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.example.periodtracker.ui.theme.PeriodRed
import com.example.periodtracker.data.DataExport
import androidx.fragment.app.FragmentActivity //UPDATED TO USE THIS FOR BIOMETRICS CAPABILITY

@Composable
fun JournalScreen() {
    var showEntryForm by remember { mutableStateOf(false) }
    var entryToEdit by remember { mutableStateOf<CycleData?>(null) }

    if (showEntryForm) {
        JournalEntryForm(
            entryToEdit = entryToEdit,
            onBack = {
                showEntryForm = false
                entryToEdit = null
            }
        )
    } else {
        JournalListScreen(
            onAddEntry = {
                entryToEdit = null
                showEntryForm = true
            },
            onEditEntry = { entry ->
                entryToEdit = entry
                showEntryForm = true
            }
        )
    }
}

@Composable
fun JournalListScreen(onAddEntry: () -> Unit, onEditEntry: (CycleData) -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val entries by db.cycleDao().getAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var exportMessage by remember { mutableStateOf("") }
    val activity = remember(context) {
        var ctx: android.content.Context = context
        while (ctx is android.content.ContextWrapper) {
            if (ctx is FragmentActivity) return@remember ctx
            ctx = ctx.baseContext
        }
        null
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Journal",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Text(
                text = "Your cycle entries.\n\n",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            if (exportMessage.isNotEmpty()) {
                Text(
                    text = exportMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No entries yet. Tap + to add one.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(entries) { entry ->
                        JournalEntryCard(
                            entry = entry,
                            onDelete = {scope.launch { db.cycleDao().delete(entry)} },
                            onEdit = {onEditEntry(entry)}
                        )
                    }
                }
            }
        }

        //export button
        FloatingActionButton(
            onClick = {
                if (activity != null) {
                    Biometrics.authenticate(
                        activity = activity,
                        title = "Even Flow",
                        subtitle = "Verify your identity to export data",
                        onSuccess = {
                            scope.launch {
                                val success = DataExport.exportToCSV(context, entries)
                                exportMessage = if (success) "Exported to Downloads." else "Export failed."
                            }
                        },
                        onFailure = {
                            android.widget.Toast.makeText(
                                context,
                                "Authentication required to export data.",
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(all = 60.dp)
                .fillMaxWidth(0.4f)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Text(
                text = "EXPORT",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // FAB add button
        FloatingActionButton(
            onClick = onAddEntry,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add entry")
        }
    }
}

@Composable
fun JournalEntryCard(entry: CycleData, onDelete: (CycleData) -> Unit, onEdit: (CycleData) -> Unit ) {
    val date = try { EncryptionManager.decrypt(entry.encryptedDate) } catch (e: Exception) { "Unknown" }
    val flow = try { EncryptionManager.decrypt(entry.encryptedFlow) } catch (e: Exception) { "" }
    val notes = try { EncryptionManager.decrypt(entry.encryptedNotes) } catch (e: Exception) { "" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (flow.isNotBlank() && flow != "None") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = flow,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    TextButton(
                        onClick = { onEdit(entry) },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Text(
                            "Edit",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    TextButton(
                        onClick = { onDelete(entry) },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Text(
                            "Delete",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
            if (notes.isNotBlank()) {
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
fun JournalEntryForm(entryToEdit: CycleData? = null, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getInstance(context) }

    var selectedDate by remember {
        mutableStateOf(
            if (entryToEdit != null)
                try { EncryptionManager.decrypt(entryToEdit.encryptedDate) } catch (e: Exception) { "" }
            else LocalDate.now().toString()
        )
    }
    var selectedFlow by remember {
        mutableStateOf(
            if (entryToEdit != null)
                try { EncryptionManager.decrypt(entryToEdit.encryptedFlow) } catch (e: Exception) { "" }
            else ""
        )
    }
    var notes by remember {
        mutableStateOf(
            if (entryToEdit != null)
                try { EncryptionManager.decrypt(entryToEdit.encryptedNotes) } catch (e: Exception) { "" }
            else ""
        )
    }
    var saved by remember { mutableStateOf(false) }

    val flowOptions = listOf("None", "Light", "Medium", "Heavy", "Abnormal")

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (entryToEdit != null) "Edit Entry" else "New Entry",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )

            // Date card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Date",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = selectedDate,
                        onValueChange = { selectedDate = it },
                        placeholder = { Text("YYYY-MM-DD") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }

            // Flow card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Flow",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        flowOptions.forEach { option ->
                            val isSelected = selectedFlow == option
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) PeriodRed
                                        else MaterialTheme.colorScheme.tertiary
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.secondary,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedFlow = option }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onTertiary,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // Notes card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = { Text("How are you feeling?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        val entry = CycleData(
                            id = entryToEdit?.id ?: 0,
                            encryptedDate = EncryptionManager.encrypt(selectedDate.trim()),
                            encryptedFlow = EncryptionManager.encrypt(selectedFlow.ifBlank { "None" }),
                            encryptedNotes = EncryptionManager.encrypt(notes.trim())
                        )
                        if (entryToEdit != null) {
                            db.cycleDao().update(entry)
                        } else {
                            db.cycleDao().insert(entry)
                        }
                        saved = true
                        selectedFlow = ""
                        notes = ""
                    }
                    onBack()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = "Save Entry",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (saved) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(
                        text = "Entry saved securely.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        FloatingActionButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Text("←", style = MaterialTheme.typography.titleLarge)
        }
    }
}