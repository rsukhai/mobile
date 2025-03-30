package com.example.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add


data class Note(val title: String, val text: String)


class NotesViewModel : ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> get() = _notes

    fun addNote(title: String, text: String) {
        viewModelScope.launch {
            val newNote = Note(title, text)
            _notes.value = _notes.value + newNote
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesApp(viewModel: NotesViewModel) {
    var currentScreen by remember { mutableStateOf("home") }
    var selectedNote by remember { mutableStateOf<Note?>(null) }

    Scaffold(
        floatingActionButton = {
            if (currentScreen == "home") {
                ExtendedFloatingActionButton(
                    text = { Text("Add a Note") },
                    onClick = { currentScreen = "add" },
                    icon = { Icon(Icons.Filled.Add, contentDescription = "Add") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (currentScreen) {
                "home" -> NotesList(viewModel.notes.collectAsState().value) {
                    selectedNote = it
                    currentScreen = "details"
                }
                "add" -> AddNoteScreen(
                    onSave = { title, text ->
                        viewModel.addNote(title, text)
                        currentScreen = "home"
                    }
                )
                "details" -> selectedNote?.let { note ->
                    NoteDetailsScreen(note) { currentScreen = "home" }
                }
            }
        }
    }
}

@Composable
fun NotesList(notes: List<Note>, onNoteClick: (Note) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        notes.forEach { note ->
            Column(modifier = Modifier
                .fillMaxWidth()
                .clickable { onNoteClick(note) }
                .padding(8.dp)) {
                Text(text = note.title, style = MaterialTheme.typography.titleMedium)
                Text(text = note.text.take(50) + if (note.text.length > 50) "..." else "")
            }
        }
    }
}

@Composable
fun AddNoteScreen(onSave: (String, String) -> Unit) {
    var title by remember { mutableStateOf(TextFieldValue()) }
    var text by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Text") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onSave(title.text, text.text) }) {
            Text("Save")
        }
    }
}

@Composable
fun NoteDetailsScreen(note: Note, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = note.title, style = MaterialTheme.typography.titleLarge)
        Text(text = note.text)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}



