package com.example.shelfshare.ui.screens.sell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.shelfshare.model.Book

@Composable
fun SellScreen(
    openScreen: (String) -> Unit,
    viewModel: SellViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    Scaffold(
        topBar = {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Add Book") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("My Listings") }
                )
            }
        }
    ) { padding ->
        when (selectedTabIndex) {
            0 -> AddBookScreen(
                uiState = uiState,
                onUpdateField = { field, value ->
                    viewModel.updateBookField(field, value)
                },
                onAddBook = { viewModel.addBook() },
                padding = padding
            )
            1 -> MyListingsScreen(
                uiState = uiState,
                onDeleteBook = { viewModel.deleteBook(it) },
                padding = padding
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    uiState: SellUiState,
    onUpdateField: (BookField, String) -> Unit,
    onAddBook: () -> Unit,
    padding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Book Title
        OutlinedTextField(
            value = uiState.bookTitle,
            onValueChange = { onUpdateField(BookField.TITLE, it) },
            label = { Text("Book Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Book Description
        OutlinedTextField(
            value = uiState.bookDescription,
            onValueChange = { onUpdateField(BookField.DESCRIPTION, it) },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5,
            minLines = 3
        )

        // Book Price
        OutlinedTextField(
            value = uiState.bookPrice,
            onValueChange = { onUpdateField(BookField.PRICE, it) },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        // Book Category Dropdown
        var expanded by remember { mutableStateOf(false) }
        val categories = listOf("Fiction", "Non-Fiction", "Science", "History")

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = uiState.bookCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            onUpdateField(BookField.CATEGORY, category)
                            expanded = false
                        }
                    )
                }
            }
        }

        // Book Image Url
        OutlinedTextField(
            value = uiState.bookImageUrl,
            onValueChange = { onUpdateField(BookField.IMAGE, it) },
            label = { Text("Book Image Url") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        // Add Book Button
        Button(
            onClick = onAddBook,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = uiState.bookTitle.isNotBlank() &&
                    uiState.bookDescription.isNotBlank() &&
                    uiState.bookPrice.isNotBlank() &&
                    uiState.bookCategory.isNotBlank()
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Book")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Book")
        }
    }
}


@Composable
fun MyListingsScreen(
    uiState: SellUiState,
    onDeleteBook: (Book) -> Unit,
    padding: PaddingValues
) {
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (uiState.userListings.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No books listed yet",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(padding).padding(12.dp)
        ) {
            items(uiState.userListings) { book ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.7f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column {
                            // Book Image
                            AsyncImage(
                                model = book.imageUrl,
                                contentDescription = book.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )

                            // Book Details
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = book.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2
                                )
                                Text(
                                    text = "Ksh. ${book.price}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Delete Button
                        IconButton(
                            onClick = { onDeleteBook(book) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Book",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}