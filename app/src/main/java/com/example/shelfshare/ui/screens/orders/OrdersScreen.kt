package com.example.shelfshare.ui.screens.orders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shelfshare.ORDERS_SCREEN
import com.example.shelfshare.model.Order
import com.example.shelfshare.model.OrderStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    openScreen : (String) -> Unit,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val purchasesState by viewModel.purchasesState.collectAsState()
    val salesState by viewModel.salesState.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OrderTabBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            AnimatedVisibility(
                visible = selectedTab == 0,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                OrderList(
                    orders = purchasesState,
                    title = "Purchases",
                    onOrderClick = openScreen
                )
            }

            AnimatedVisibility(
                visible = selectedTab == 1,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                OrderList(
                    orders = salesState,
                    title = "Sales",
                    onOrderClick = openScreen
                )
            }
        }
    }
}

@Composable
fun OrderTabBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        Tab(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            text = { Text("Purchases") },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Purchases") }
        )
        Tab(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            text = { Text("Sales") },
            icon = { Icon(Icons.Default.Store, contentDescription = "Sales") }
        )
    }
}

@Composable
fun OrderList(
    orders: List<Order>,
    title: String,
    onOrderClick: (String) -> Unit
) {
    if (orders.isEmpty()) {
        EmptyOrderState(title)
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders) { order ->
                OrderListItem(
                    order = order,
                    onClick = { onOrderClick("${ORDERS_SCREEN}/${order.orderId}") }
                )
            }
        }
    }
}

@Composable
fun OrderListItem(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Order Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.bookTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ksh. ${order.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Status Chip
                OrderStatusChip(status = order.status)
            }

            // Chevron Icon
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "View Order Details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun OrderStatusChip(status: OrderStatus) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(getStatusColor(status).copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.name.lowercase().capitalize(),
            style = MaterialTheme.typography.labelSmall,
            color = getStatusColor(status)
        )
    }
}

@Composable
fun EmptyOrderState(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.ListAlt,
                contentDescription = "No Orders",
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No $title Yet",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Your ${title.lowercase()} will appear here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

// Helper function to get status color
fun getStatusColor(status: OrderStatus): Color = when (status) {
    OrderStatus.PENDING -> Color(0xFFFFA500)   // Orange
    OrderStatus.PAID -> Color(0xFF4CAF50)      // Green
    OrderStatus.SHIPPED -> Color(0xFF2196F3)   // Blue
    OrderStatus.DELIVERED -> Color(0xFF9C27B0) // Purple
    OrderStatus.CANCELLED -> Color(0xFFF44336) // Red
    OrderStatus.REFUNDED -> Color(0xFF795548)  // Brown
}