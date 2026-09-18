package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.BillingRepository
import com.example.data.model.InvoiceEntity
import com.example.data.model.InvoiceItemEntity
import com.example.data.model.ProductEntity
import com.example.ui.state.BillingUiState
import com.example.ui.state.CartItem
import com.example.ui.state.ScreenTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BillingViewModel(
    private val repository: BillingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BillingUiState(isLoading = true))
    val uiState: StateFlow<BillingUiState> = _uiState

    init {
        viewModelScope.launch {
            repository.initializeSampleDataIfNeeded()
            observeData()
        }
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                repository.allProducts,
                repository.lowStockProducts,
                repository.allInvoices,
                repository.dueInvoices,
                repository.allPayments
            ) { products, lowStock, invoices, dueInvoices, payments ->
                _uiState.update { current ->
                    current.copy(
                        products = products,
                        lowStockProducts = lowStock,
                        invoices = invoices,
                        dueInvoices = dueInvoices,
                        payments = payments,
                        isLoading = false
                    )
                }
            }.collect {}
        }
    }

    fun switchTab(tab: ScreenTab) {
        _uiState.update { it.copy(currentTab = tab, searchQuery = "") }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    // Cart Operations
    fun addProductToCart(product: ProductEntity) {
        _uiState.update { current ->
            val existing = current.cartItems.find { it.productId == product.id }
            val updatedList = if (existing != null) {
                current.cartItems.map {
                    if (it.productId == product.id) {
                        it.copy(quantity = it.quantity + 1)
                    } else it
                }
            } else {
                current.cartItems + CartItem(
                    productId = product.id,
                    name = product.name,
                    category = product.category,
                    unitPrice = product.sellingPrice,
                    quantity = 1,
                    imeiOrSerial = product.imeiOrSerial,
                    availableStock = product.stockQty
                )
            }
            current.copy(cartItems = updatedList)
        }
    }

    fun addCustomItemToCart(name: String, category: String, price: Double, qty: Int, imei: String?) {
        if (name.isBlank() || price <= 0) return
        _uiState.update { current ->
            current.copy(
                cartItems = current.cartItems + CartItem(
                    productId = null,
                    name = name.trim(),
                    category = category,
                    unitPrice = price,
                    quantity = qty.coerceAtLeast(1),
                    imeiOrSerial = imei?.takeIf { it.isNotBlank() }
                )
            )
        }
    }

    fun updateCartItemQuantity(itemId: String, newQty: Int) {
        _uiState.update { current ->
            if (newQty <= 0) {
                current.copy(cartItems = current.cartItems.filterNot { it.id == itemId })
            } else {
                current.copy(
                    cartItems = current.cartItems.map {
                        if (it.id == itemId) it.copy(quantity = newQty) else it
                    }
                )
            }
        }
    }

    fun updateCartItemPrice(itemId: String, newPrice: Double) {
        if (newPrice < 0) return
        _uiState.update { current ->
            current.copy(
                cartItems = current.cartItems.map {
                    if (it.id == itemId) it.copy(unitPrice = newPrice) else it
                }
            )
        }
    }

    fun updateCartItemImei(itemId: String, imei: String) {
        _uiState.update { current ->
            current.copy(
                cartItems = current.cartItems.map {
                    if (it.id == itemId) it.copy(imeiOrSerial = imei) else it
                }
            )
        }
    }

    fun removeCartItem(itemId: String) {
        _uiState.update { current ->
            current.copy(cartItems = current.cartItems.filterNot { it.id == itemId })
        }
    }

    fun clearCart() {
        _uiState.update {
            it.copy(
                cartItems = emptyList(),
                customerName = "",
                customerPhone = "",
                discountAmount = 0.0,
                gstPercent = 0.0,
                paymentMode = "CASH",
                paidAmountInput = "",
                notes = ""
            )
        }
    }

    // POS Customer & Discount Fields
    fun setCustomerName(name: String) {
        _uiState.update { it.copy(customerName = name) }
    }

    fun setCustomerPhone(phone: String) {
        _uiState.update { it.copy(customerPhone = phone) }
    }

    fun setGstPercent(gst: Double) {
        _uiState.update { it.copy(gstPercent = gst) }
    }

    fun setDiscountAmount(discount: Double) {
        _uiState.update { it.copy(discountAmount = discount) }
    }

    fun setPaymentMode(mode: String) {
        _uiState.update {
            it.copy(
                paymentMode = mode,
                paidAmountInput = if (mode == "CREDIT") "0" else ""
            )
        }
    }

    fun setPaidAmountInput(input: String) {
        _uiState.update { it.copy(paidAmountInput = input) }
    }

    fun setNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    // Checkout / Create Bill
    fun generateBill(onSuccess: (InvoiceEntity) -> Unit = {}) {
        val current = _uiState.value
        if (current.cartItems.isEmpty()) {
            showSnackbar("Cart is empty! Add products to create a bill.")
            return
        }

        viewModelScope.launch {
            val invoiceNumber = repository.getNextInvoiceNumber()
            val total = current.grandTotal
            val paid = current.effectivePaidAmount
            val due = current.dueAmount
            val status = when {
                due <= 0.01 -> "PAID"
                paid <= 0.01 -> "DUE"
                else -> "PARTIAL"
            }

            val invoice = InvoiceEntity(
                invoiceNumber = invoiceNumber,
                customerName = current.customerName.ifBlank { "Walk-in Customer" },
                customerPhone = current.customerPhone.ifBlank { "7550281815" },
                timestamp = System.currentTimeMillis(),
                subtotal = current.subtotal,
                discountPercent = 0.0,
                discountAmount = current.discountAmount,
                gstPercent = current.gstPercent,
                gstAmount = current.gstAmount,
                grandTotal = total,
                paidAmount = paid,
                dueAmount = due,
                paymentMode = current.paymentMode,
                status = status,
                notes = current.notes.ifBlank { null }
            )

            val invoiceItems = current.cartItems.map { cart ->
                InvoiceItemEntity(
                    invoiceId = 0,
                    productId = cart.productId,
                    productName = cart.name,
                    category = cart.category,
                    imeiOrSerial = cart.imeiOrSerial,
                    unitPrice = cart.unitPrice,
                    quantity = cart.quantity,
                    total = cart.total
                )
            }

            val invId = repository.createInvoice(invoice, invoiceItems)
            val created = invoice.copy(id = invId)

            clearCart()
            showInvoiceDetail(created)
            showSnackbar("Bill $invoiceNumber generated successfully!")
            onSuccess(created)
        }
    }

    fun showInvoiceDetail(invoice: InvoiceEntity) {
        viewModelScope.launch {
            val items = repository.getInvoiceItems(invoice.id)
            _uiState.update {
                it.copy(
                    activeInvoiceForDetail = invoice,
                    activeInvoiceItems = items
                )
            }
        }
    }

    fun dismissInvoiceDetail() {
        _uiState.update {
            it.copy(
                activeInvoiceForDetail = null,
                activeInvoiceItems = emptyList()
            )
        }
    }

    // Product Management
    fun openAddProductDialog() {
        _uiState.update { it.copy(showAddProductDialog = true, editingProduct = null) }
    }

    fun openEditProductDialog(product: ProductEntity) {
        _uiState.update { it.copy(showAddProductDialog = true, editingProduct = product) }
    }

    fun dismissProductDialog() {
        _uiState.update { it.copy(showAddProductDialog = false, editingProduct = null) }
    }

    fun saveProduct(
        id: Long,
        name: String,
        brand: String,
        category: String,
        sellingPrice: Double,
        purchasePrice: Double,
        stockQty: Int,
        minStockQty: Int,
        imei: String?,
        description: String?
    ) {
        if (name.isBlank() || sellingPrice <= 0) {
            showSnackbar("Please enter a valid product name and selling price.")
            return
        }

        viewModelScope.launch {
            val product = ProductEntity(
                id = id,
                name = name.trim(),
                brand = brand.trim().ifBlank { "Generic" },
                category = category,
                sellingPrice = sellingPrice,
                purchasePrice = purchasePrice,
                stockQty = stockQty,
                minStockQty = minStockQty,
                imeiOrSerial = imei?.takeIf { it.isNotBlank() },
                description = description?.takeIf { it.isNotBlank() }
            )
            if (id == 0L) {
                repository.addProduct(product)
                showSnackbar("Product added: $name")
            } else {
                repository.updateProduct(product)
                showSnackbar("Product updated: $name")
            }
            dismissProductDialog()
        }
    }

    fun openStockAdjustDialog(product: ProductEntity) {
        _uiState.update { it.copy(showStockAdjustProduct = product) }
    }

    fun dismissStockAdjustDialog() {
        _uiState.update { it.copy(showStockAdjustProduct = null) }
    }

    fun adjustStock(productId: Long, delta: Int) {
        viewModelScope.launch {
            repository.adjustProductStock(productId, delta)
            dismissStockAdjustDialog()
            showSnackbar("Stock adjusted by $delta")
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            showSnackbar("Deleted ${product.name}")
        }
    }

    // Khata / Due Settlement
    fun openCollectPaymentDialog(invoice: InvoiceEntity) {
        _uiState.update { it.copy(showCollectPaymentInvoice = invoice) }
    }

    fun dismissCollectPaymentDialog() {
        _uiState.update { it.copy(showCollectPaymentInvoice = null) }
    }

    fun recordDuePayment(invoiceId: Long, amount: Double, paymentMode: String, note: String?) {
        if (amount <= 0) return
        viewModelScope.launch {
            repository.recordDueSettlement(invoiceId, amount, paymentMode, note)
            dismissCollectPaymentDialog()
            showSnackbar("Payment of ₹${amount.toInt()} recorded successfully!")
        }
    }

    // UPI QR Code
    fun showUpiQr(amount: Double, invoiceNumber: String = "") {
        _uiState.update {
            it.copy(
                showUpiQrDialog = true,
                upiQrAmount = amount,
                upiQrInvoiceNumber = invoiceNumber
            )
        }
    }

    fun dismissUpiQr() {
        _uiState.update { it.copy(showUpiQrDialog = false) }
    }

    fun showSnackbar(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}

class BillingViewModelFactory(
    private val repository: BillingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BillingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BillingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
