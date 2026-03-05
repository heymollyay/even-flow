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
import com.example.periodtracker.EncryptionManager
import com.example.periodtracker.data.AppDatabase
import com.example.periodtracker.data.CycleData
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.example.periodtracker.ui.theme.PeriodRed


@Composable
fun JournalScreen() {
    var showEntryForm by remember { mutableStateOf(false) }

    if (showEntryForm) {
        JournalEntryForm(onBack = { showEntryForm = false })
    } else {
        JournalListScreen(onAddEntry = { showEntryForm = true })
    }
}

@Composable
fun JournalListScreen(onAddEntry: () -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val entries by db.cycleDao().getAll().collectAsState(initial = emptyList())

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
                text = "Your cycle entries.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )

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
                        JournalEntryCard(entry = entry)
                    }
                }
            }
        }

        // FAB add button
        FloatingActionButton(
            onClick = onAddEntry,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add entry")
        }
    }
}

@Composable
fun JournalEntryCard(entry: CycleData) {
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
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
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
                if (flow.isNotBlank() && flow != "None") {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = flow,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            if (notes.isNotBlank()) {
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun JournalEntryForm(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getInstance(context) }

    var selectedDate by remember { mutableStateOf(LocalDate.now().toString()) }
    var selectedFlow by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
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
                text = "New Entry",
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
                            encryptedDate = EncryptionManager.encrypt(selectedDate.trim()),
                            encryptedFlow = EncryptionManager.encrypt(selectedFlow.ifBlank { "None" }),
                            encryptedNotes = EncryptionManager.encrypt(notes.trim())
                        )
                        db.cycleDao().insert(entry)
                        saved = true
                        selectedFlow = ""
                        notes = ""
                    }
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