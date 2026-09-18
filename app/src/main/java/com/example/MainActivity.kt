package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.BillingRepository
import com.example.ui.BillingViewModel
import com.example.ui.BillingViewModelFactory
import com.example.ui.components.AddEditProductDialog
import com.example.ui.components.CollectPaymentDialog
import com.example.ui.components.DhayaHeader
import com.example.ui.components.InvoiceDetailDialog
import com.example.ui.components.StockAdjustDialog
import com.example.ui.components.UpiQrCodeDialog
import com.example.ui.screens.BillingScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.InvoicesScreen
import com.example.ui.screens.KhataScreen
import com.example.ui.screens.StoreProfileScreen
import com.example.ui.state.ScreenTab
import com.example.ui.theme.DhayaGold
import com.example.ui.theme.DhayaGoldBright
import com.example.ui.theme.DhayaNavy
import com.example.ui.theme.DhayaNavyDark
import com.example.ui.theme.DhayaNavyLight
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val repository = BillingRepository(
            productDao = database.productDao(),
            invoiceDao = database.invoiceDao(),
            paymentDao = database.paymentDao()
        )

        setContent {
            MyApplicationTheme {
                val viewModel: BillingViewModel = viewModel(
                    factory = BillingViewModelFactory(repository)
                )
                DhayaMobilesApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DhayaMobilesApp(viewModel: BillingViewModel) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dhaya_mobiles_app"),
        topBar = {
            DhayaHeader(
                lowStockCount = state.lowStockProducts.size,
                onLowStockClick = { viewModel.switchTab(ScreenTab.INVENTORY) }
            )
        },
        bottomBar = {
            DhayaBottomNavigation(
                currentTab = state.currentTab,
                onTabSelect = { viewModel.switchTab(it) }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
        ) {
            when (state.currentTab) {
                ScreenTab.BILLING -> BillingScreen(state = state, viewModel = viewModel)
                ScreenTab.INVOICES -> InvoicesScreen(state = state, viewModel = viewModel)
                ScreenTab.INVENTORY -> InventoryScreen(state = state, viewModel = viewModel)
                ScreenTab.KHATA -> KhataScreen(state = state, viewModel = viewModel)
                ScreenTab.STORE -> StoreProfileScreen(state = state, viewModel = viewModel)
            }

            // Dialogs
            state.activeInvoiceForDetail?.let { invoice ->
                InvoiceDetailDialog(
                    invoice = invoice,
                    items = state.activeInvoiceItems,
                    onDismiss = { viewModel.dismissInvoiceDetail() },
                    onCollectDue = {
                        viewModel.dismissInvoiceDetail()
                        viewModel.openCollectPaymentDialog(it)
                    }
                )
            }

            if (state.showUpiQrDialog) {
                UpiQrCodeDialog(
                    amount = state.upiQrAmount,
                    invoiceNumber = state.upiQrInvoiceNumber,
                    onDismiss = { viewModel.dismissUpiQr() },
                    onPaymentConfirmed = {
                        viewModel.dismissUpiQr()
                        viewModel.showSnackbar("Payment confirmed via UPI QR!")
                    }
                )
            }

            if (state.showAddProductDialog) {
                AddEditProductDialog(
                    initialProduct = state.editingProduct,
                    onDismiss = { viewModel.dismissProductDialog() },
                    onSave = { id, name, brand, category, sell, cost, stock, minStock, imei, desc ->
                        viewModel.saveProduct(id, name, brand, category, sell, cost, stock, minStock, imei, desc)
                    }
                )
            }

            state.showStockAdjustProduct?.let { product ->
                StockAdjustDialog(
                    product = product,
                    onDismiss = { viewModel.dismissStockAdjustDialog() },
                    onAdjust = { id, delta -> viewModel.adjustStock(id, delta) }
                )
            }

            state.showCollectPaymentInvoice?.let { invoice ->
                CollectPaymentDialog(
                    invoice = invoice,
                    onDismiss = { viewModel.dismissCollectPaymentDialog() },
                    onConfirmPayment = { id, amount, mode, note ->
                        viewModel.recordDuePayment(id, amount, mode, note)
                    }
                )
            }
        }
    }
}

@Composable
fun DhayaBottomNavigation(
    currentTab: ScreenTab,
    onTabSelect: (ScreenTab) -> Unit
) {
    NavigationBar(
        containerColor = DhayaNavy,
        contentColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("bottom_nav_bar")
    ) {
        val navItems = listOf(
            Triple(ScreenTab.BILLING, "Billing", Icons.Default.Receipt),
            Triple(ScreenTab.INVOICES, "Bills", Icons.Default.History),
            Triple(ScreenTab.INVENTORY, "Stock", Icons.Default.Inventory2),
            Triple(ScreenTab.KHATA, "Khata", Icons.Default.AccountBalanceWallet),
            Triple(ScreenTab.STORE, "Store", Icons.Default.Store)
        )

        navItems.forEach { (tab, label, icon) ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelect(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) DhayaGoldBright else Color(0xFF94A3B8)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) DhayaGoldBright else Color(0xFF94A3B8)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = DhayaNavyLight
                ),
                modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
            )
        }
    }
}
