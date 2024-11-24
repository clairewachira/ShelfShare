package com.example.shelfshare.ui.screens.cart

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.shelfshare.model.CartItemWithBook
import com.example.shelfshare.model.CartUiState
import com.example.shelfshare.model.PaymentState
import com.example.shelfshare.ui.screens.MpesaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    openScreen: (String) -> Unit,
    anotherVm: MpesaViewModel = hiltViewModel(),
    viewModel: CartViewModel = hiltViewModel(),
) {
    val cartState by viewModel.cartState.collectAsState()
    val paymentState by viewModel.paymentState.collectAsState()

    var showPaymentSheet by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping Cart") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            cartState.let { state ->
                if (state is CartUiState.Success && state.items.isNotEmpty()) {
                    CartBottomBar(
                        totalAmount = state.totalAmount,
                        selectedItems = state.selectedItems,
                        onCheckout = { showPaymentSheet = true }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (cartState) {
                is CartUiState.Loading -> LoadingState()
                is CartUiState.Error -> ErrorState((cartState as CartUiState.Error).message)
                is CartUiState.Success -> {
                    val state = cartState as CartUiState.Success
                    if (state.items.isEmpty()) {
                        EmptyCartState()
                    } else {
                        CartContent(
                            items = state.items,
                            onRemoveItem = viewModel::removeFromCart,
                            onToggleSelection = viewModel::toggleItemSelection
                        )
                    }
                }
            }

            // Payment Bottom Sheet
            if (showPaymentSheet) {
                PaymentBottomSheet(
                    onDismiss = { showPaymentSheet = false },
                    phoneNumber = phoneNumber,
                    onPhoneNumberChange = { phoneNumber = it },
                    onProceed = {
                        anotherVm.initiatePayment(phoneNumber, 5.0)
                        showPaymentSheet = false
                    }
                )
            }

            // Payment Status Dialog
            when (paymentState) {
                is PaymentState.Processing -> PaymentProcessingDialog()
                is PaymentState.Success -> PaymentSuccessDialog(
                    transactionId = (paymentState as PaymentState.Success).transactionId,
                    onDismiss = {}
                )

                is PaymentState.Error -> PaymentErrorDialog(
                    message = (paymentState as PaymentState.Error).message,
                    onDismiss = { /* Reset payment state */ }
                )

                else -> { /* Idle state - no dialog */
                }
            }
        }
    }
}

@Composable
fun CartContent(
    items: List<CartItemWithBook>,
    onRemoveItem: (String) -> Unit,
    onToggleSelection: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { cartItem ->
            CartItemCard(
                cartItem = cartItem,
                onRemove = { onRemoveItem(cartItem.cartItem.cartItemId) },
                onToggleSelection = { onToggleSelection(cartItem.cartItem.cartItemId) }
            )
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItemWithBook,
    onRemove: () -> Unit,
    onToggleSelection: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = cartItem.isSelected,
                onCheckedChange = { onToggleSelection() }
            )

            AsyncImage(
                model = cartItem.book?.imageUrl,
                contentDescription = cartItem.book?.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                cartItem.book?.let {
                    Text(
                        text = it.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Ksh. ${cartItem.book?.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )

            }

            IconButton(onClick = { onRemove() }) {
                Icon(Icons.Default.Delete, "Remove item")
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun CartBottomBar(
    totalAmount: Double,
    selectedItems: Int,
    onCheckout: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Total Amount")
                Text(
                    text = "Ksh. ${String.format("%.2f", totalAmount)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = onCheckout,
                enabled = selectedItems > 0
            ) {
                Text("Checkout ($selectedItems)")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentBottomSheet(
    onDismiss: () -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    onProceed: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                "M-Pesa Payment",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChange,
                label = { Text("Phone Number") },
                placeholder = { Text("Enter M-Pesa number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onProceed,
                modifier = Modifier.fillMaxWidth(),
                enabled = phoneNumber.length >= 10
            ) {
                Text("Proceed to Pay")
            }
        }
    }
}

@Composable
fun PaymentProcessingDialog() {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Processing Payment") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Please check your phone for the M-Pesa prompt",
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = { }
    )
}

@Composable
fun PaymentSuccessDialog(
    transactionId: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
        title = { Text("Payment Successful") },
        text = {
            Column {
                Text("Your payment has been processed successfully!")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Transaction ID: $transactionId",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("View Orders")
            }
        }
    )
}

@Composable
fun PaymentErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Error, contentDescription = null) },
        title = { Text("Payment Failed") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun EmptyCartState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your cart is empty",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Add some books to get started",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
