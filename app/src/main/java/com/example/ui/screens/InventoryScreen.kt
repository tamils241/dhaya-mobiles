package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.BillingViewModel
import com.example.ui.components.PRODUCT_CATEGORIES
import com.example.ui.state.BillingUiState
import com.example.ui.theme.DhayaGoldBright
import com.example.ui.theme.DhayaNavy
import com.example.ui.theme.StatusDue
import com.example.ui.theme.StatusDueBg
import com.example.ui.theme.StatusPaid
import com.example.ui.theme.StatusPaidBg

@Composable
fun InventoryScreen(
    state: BillingUiState,
    viewModel: BillingViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var filterOnlyLowStock by remember { mutableStateOf(false) }

    val filteredProducts = state.products.filter { prod ->
        val matchesQuery = searchQuery.isBlank() ||
                prod.name.contains(searchQuery, ignoreCase = true) ||
                prod.brand.contains(searchQuery, ignoreCase = true) ||
                (prod.imeiOrSerial?.contains(searchQuery, ignoreCase = true) == true)
        val matchesCat = selectedCategory == "All" || prod.category.equals(selectedCategory, ignoreCase = true)
        val matchesLowStock = !filterOnlyLowStock || (prod.stockQty <= prod.minStockQty)
        matchesQuery && matchesCat && matchesLowStock
    }

    val totalStockUnits = state.products.sumOf { it.stockQty }
    val totalInventoryValue = state.products.sumOf { it.sellingPrice * it.stockQty }
    val lowStockCount = state.products.count { it.stockQty <= it.minStockQty }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 14.dp)
            .testTag("inventory_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp)
    ) {
        // Stock Stats Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DhayaNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TOTAL INVENTORY VALUE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = DhayaGoldBright
                            )
                            Text(
                                text = String.format("₹%,.0f", totalInventoryValue),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                        Button(
                            onClick = { viewModel.openAddProductDialog() },
                            colors = ButtonDefaults.buttonColors(containerColor = DhayaGoldBright),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("add_product_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = DhayaNavy, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Item", fontWeight = FontWeight.Bold, color = DhayaNavy, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InventoryStatBox(
                            label = "Total Items",
                            value = "${state.products.size} models",
                            modifier = Modifier.weight(1f)
                        )
                        InventoryStatBox(
                            label = "Units in Stock",
                            value = "$totalStockUnits units",
                            modifier = Modifier.weight(1f)
                        )
                        InventoryStatBox(
                            label = "Low Stock Alert",
                            value = "$lowStockCount items",
                            isWarning = lowStockCount > 0,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { filterOnlyLowStock = !filterOnlyLowStock }
                        )
                    }
                }
            }
        }

        // Search & Categories Filter
        item {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search product name, brand, or IMEI...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_inventory_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == "All" && !filterOnlyLowStock,
                            onClick = {
                                selectedCategory = "All"
                                filterOnlyLowStock = false
                            },
                            label = { Text("All (${state.products.size})") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = filterOnlyLowStock,
                            onClick = { filterOnlyLowStock = !filterOnlyLowStock },
                            label = { Text("⚠️ Low Stock ($lowStockCount)") }
                        )
                    }
                    items(PRODUCT_CATEGORIES) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }
            }
        }

        // Products List
        if (filteredProducts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No products found in inventory",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(filteredProducts, key = { it.id }) { product ->
                ProductInventoryCard(
                    product = product,
                    onStockAdjust = { viewModel.openStockAdjustDialog(product) },
                    onEdit = { viewModel.openEditProductDialog(product) },
                    onDelete = { viewModel.deleteProduct(product) }
                )
            }
        }
    }
}

@Composable
fun ProductInventoryCard(
    product: ProductEntity,
    onStockAdjust: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isLowStock = product.stockQty <= product.minStockQty

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("inventory_item_${product.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1.5f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = product.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${product.brand} • ${product.category}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    if (!product.imeiOrSerial.isNullOrBlank()) {
                        Text(
                            text = "IMEI: ${product.imeiOrSerial}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF475569)
                        )
                    }
                }

                // Stock Badge
                val (stockBg, stockColor, stockLabel) = when {
                    product.stockQty <= 0 -> Triple(StatusDueBg, StatusDue, "Out of Stock")
                    isLowStock -> Triple(StatusDueBg, StatusDue, "Low: ${product.stockQty}")
                    else -> Triple(StatusPaidBg, StatusPaid, "Stock: ${product.stockQty}")
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(stockBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stockLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = stockColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Price info and Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₹${product.sellingPrice.toInt()}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = DhayaNavy
                    )
                    if (product.purchasePrice > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cost: ₹${product.purchasePrice.toInt()}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedButton(
                        onClick = onStockAdjust,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.SwapVert, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("+/- Stock", fontSize = 11.sp)
                    }

                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = DhayaNavy, modifier = Modifier.size(18.dp))
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun InventoryStatBox(
    label: String,
    value: String,
    isWarning: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isWarning) Color(0x33EF4444) else Color(0xFF153462))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column {
            Text(label, fontSize = 9.sp, color = if (isWarning) Color(0xFFFCA5A5) else Color(0xFF94A3B8))
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isWarning) Color(0xFFFCA5A5) else Color.White)
        }
    }
}
