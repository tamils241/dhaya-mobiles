package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ProductEntity
import com.example.ui.BillingViewModel
import com.example.ui.components.PRODUCT_CATEGORIES
import com.example.ui.state.BillingUiState
import com.example.ui.state.CartItem
import com.example.ui.theme.DhayaGold
import com.example.ui.theme.DhayaGoldBright
import com.example.ui.theme.DhayaNavy
import com.example.ui.theme.DhayaNavyLight
import com.example.ui.theme.StatusDue
import com.example.ui.theme.StatusPaid
import com.example.ui.theme.UpiBlue

@Composable
fun BillingScreen(
    state: BillingUiState,
    viewModel: BillingViewModel
) {
    var searchProductQuery by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf("All") }
    var showCustomItemDialog by remember { mutableStateOf(false) }

    val filteredProducts = state.products.filter { prod ->
        val matchesQuery = searchProductQuery.isBlank() ||
                prod.name.contains(searchProductQuery, ignoreCase = true) ||
                prod.brand.contains(searchProductQuery, ignoreCase = true) ||
                (prod.imeiOrSerial?.contains(searchProductQuery, ignoreCase = true) == true)
        val matchesCat = selectedCat == "All" || prod.category.equals(selectedCat, ignoreCase = true)
        matchesQuery && matchesCat
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 14.dp)
            .testTag("billing_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp)
    ) {
        // Customer Details Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Customer Information",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DhayaNavy
                        )
                        Text(
                            text = "Step 1 of 2",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = state.customerName,
                            onValueChange = { viewModel.setCustomerName(it) },
                            label = { Text("Customer Name") },
                            placeholder = { Text("e.g. Ramesh Kumar") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = DhayaNavy) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("customer_name_input"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = state.customerPhone,
                            onValueChange = { viewModel.setCustomerPhone(it) },
                            label = { Text("Mobile Number") },
                            placeholder = { Text("10-digit phone") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF16A34A)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("customer_phone_input"),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Add Products to Bill Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select Products & Accessories",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DhayaNavy
                        )
                        OutlinedButton(
                            onClick = { showCustomItemDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("add_custom_item_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Custom Service", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Search Product Field
                    OutlinedTextField(
                        value = searchProductQuery,
                        onValueChange = { searchProductQuery = it },
                        placeholder = { Text("Search phone, charger, tempered glass, IMEI...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_product_pos_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCat == "All",
                                onClick = { selectedCat = "All" },
                                label = { Text("All") }
                            )
                        }
                        items(PRODUCT_CATEGORIES) { cat ->
                            FilterChip(
                                selected = selectedCat == cat,
                                onClick = { selectedCat = cat },
                                label = { Text(cat) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Products Quick Pick Cards (scrollable horizontal or compact row)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filteredProducts.take(12)) { product ->
                            ProductPosChip(
                                product = product,
                                onAdd = { viewModel.addProductToCart(product) }
                            )
                        }
                    }
                }
            }
        }

        // Active Cart Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AddShoppingCart,
                                contentDescription = null,
                                tint = DhayaNavy,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Billed Items (${state.cartItems.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = DhayaNavy
                            )
                        }

                        if (state.cartItems.isNotEmpty()) {
                            Text(
                                text = "Clear",
                                fontSize = 12.sp,
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { viewModel.clearCart() }
                                    .padding(4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (state.cartItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Receipt,
                                    contentDescription = null,
                                    tint = Color(0xFFCBD5E1),
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "No items in bill cart",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "Pick products above or tap Custom Service",
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    } else {
                        state.cartItems.forEach { item ->
                            CartItemRow(
                                item = item,
                                onQuantityChange = { viewModel.updateCartItemQuantity(item.id, it) },
                                onPriceChange = { viewModel.updateCartItemPrice(item.id, it) },
                                onImeiChange = { viewModel.updateCartItemImei(item.id, it) },
                                onRemove = { viewModel.removeCartItem(item.id) }
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 6.dp),
                                color = Color(0xFFF1F5F9)
                            )
                        }
                    }
                }
            }
        }

        // Billing Calculations & Payment Modes Card
        if (state.cartItems.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Payment & Tax Calculations",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DhayaNavy
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Discount & GST Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = if (state.discountAmount > 0) state.discountAmount.toInt().toString() else "",
                                onValueChange = {
                                    val discount = it.toDoubleOrNull() ?: 0.0
                                    viewModel.setDiscountAmount(discount)
                                },
                                label = { Text("Discount (₹)") },
                                placeholder = { Text("0") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            // GST Rate Selector
                            Column(modifier = Modifier.weight(1f)) {
                                Text("GST Rate", fontSize = 11.sp, color = Color.Gray)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    listOf(0.0, 5.0, 12.0, 18.0).forEach { rate ->
                                        val isSel = state.gstPercent == rate
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isSel) DhayaNavy else Color(0xFFF1F5F9))
                                                .clickable { viewModel.setGstPercent(rate) }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${rate.toInt()}%",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSel) DhayaGoldBright else Color(0xFF334155)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Subtotal, GST, Grand Total Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0A1931))
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Subtotal", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                                    Text("₹${state.subtotal.toInt()}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                                if (state.discountAmount > 0) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Discount Applied", color = Color(0xFF4ADE80), fontSize = 12.sp)
                                        Text("-₹${state.discountAmount.toInt()}", color = Color(0xFF4ADE80), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                if (state.gstPercent > 0) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("GST (${state.gstPercent.toInt()}%)", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                                        Text("+₹${state.gstAmount.toInt()}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = Color(0xFF334E68)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "GRAND TOTAL",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = DhayaGoldBright
                                    )
                                    Text(
                                        text = "₹${state.grandTotal.toInt()}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 22.sp,
                                        color = DhayaGoldBright
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Payment Modes Selector
                        Text("Payment Mode", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DhayaNavy)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("CASH", "UPI", "CARD", "CREDIT").forEach { mode ->
                                val isSel = state.paymentMode == mode
                                val label = if (mode == "CREDIT") "KHATA (DUE)" else mode
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSel) {
                                                if (mode == "CREDIT") Color(0xFFEF4444) else DhayaNavy
                                            } else Color(0xFFF1F5F9)
                                        )
                                        .clickable { viewModel.setPaymentMode(mode) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isSel) Color.White else Color(0xFF334155)
                                    )
                                }
                            }
                        }

                        // If UPI mode: show Quick QR Button
                        if (state.paymentMode == "UPI") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { viewModel.showUpiQr(state.grandTotal, "NEW") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = UpiBlue),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.QrCode2, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Show Dhaya Mobiles UPI QR (₹${state.grandTotal.toInt()})", fontWeight = FontWeight.Bold)
                            }
                        }

                        // If Partial / Khata: Paid & Due Row
                        if (state.paymentMode == "CREDIT" || state.paidAmountInput.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = state.paidAmountInput,
                                    onValueChange = { viewModel.setPaidAmountInput(it) },
                                    label = { Text("Amount Paid (₹)") },
                                    placeholder = { Text("0") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFEF2F2))
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Balance Due", fontSize = 11.sp, color = Color(0xFF991B1B))
                                        Text(
                                            "₹${state.dueAmount.toInt()}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black,
                                            color = StatusDue
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Generate Bill Button
                        Button(
                            onClick = { viewModel.generateBill() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("generate_bill_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Generate Tax Bill (₹${state.grandTotal.toInt()})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCustomItemDialog) {
        CustomItemDialog(
            onDismiss = { showCustomItemDialog = false },
            onAdd = { name, cat, price, qty, imei ->
                viewModel.addCustomItemToCart(name, cat, price, qty, imei)
                showCustomItemDialog = false
            }
        )
    }
}

@Composable
private fun ProductPosChip(
    product: ProductEntity,
    onAdd: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF1F5F9))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
            .clickable { onAdd() }
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = DhayaNavy,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = product.category,
                fontSize = 9.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹${product.sellingPrice.toInt()}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    color = DhayaNavy
                )
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(DhayaNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        tint = DhayaGoldBright,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onPriceChange: (Double) -> Unit,
    onImeiChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1.5f)) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DhayaNavy
                )
                Text(
                    text = item.category,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            // Quantity Stepper
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                IconButton(
                    onClick = { onQuantityChange(item.quantity - 1) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(14.dp))
                }
                Text(
                    text = "${item.quantity}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
                IconButton(
                    onClick = { onQuantityChange(item.quantity + 1) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(14.dp))
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Total
            Text(
                text = "₹${item.total.toInt()}",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = DhayaNavy
            )

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Optional IMEI / Serial input field for phones
        if (item.category.equals("Smartphones", ignoreCase = true) || item.category.equals("Keypad", ignoreCase = true)) {
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = item.imeiOrSerial ?: "",
                onValueChange = onImeiChange,
                label = { Text("Device IMEI / Serial No.") },
                placeholder = { Text("e.g. 869402058392014") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@Composable
fun CustomItemDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, cat: String, price: Double, qty: Int, imei: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Accessories") }
    var priceStr by remember { mutableStateOf("") }
    var qtyStr by remember { mutableStateOf("1") }
    var imei by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Add Custom Service / Line Item",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DhayaNavy
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name *") },
                    placeholder = { Text("e.g. Screen Replacement / SIM Activation") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Price (₹) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = qtyStr,
                        onValueChange = { qtyStr = it },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = imei,
                    onValueChange = { imei = it },
                    label = { Text("IMEI / Serial (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val price = priceStr.toDoubleOrNull() ?: 0.0
                        val qty = qtyStr.toIntOrNull() ?: 1
                        if (name.isNotBlank() && price > 0) {
                            onAdd(name, category, price, qty, imei)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DhayaNavy),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Add to Bill Cart", color = DhayaGoldBright, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
