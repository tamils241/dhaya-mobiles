package com.example.ui.state

import com.example.data.model.InvoiceEntity
import com.example.data.model.InvoiceItemEntity
import com.example.data.model.PaymentRecordEntity
import com.example.data.model.ProductEntity

enum class ScreenTab(val title: String) {
    BILLING("New Bill"),
    INVOICES("Bills History"),
    INVENTORY("Inventory"),
    KHATA("Dues & Khata"),
    STORE("Shop & Stats")
}

data class CartItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val productId: Long? = null,
    val name: String,
    val category: String = "Accessories",
    val unitPrice: Double,
    val quantity: Int = 1,
    val imeiOrSerial: String? = null,
    val availableStock: Int? = null
) {
    val total: Double get() = unitPrice * quantity
}

data class CustomerDueSummary(
    val customerName: String,
    val customerPhone: String,
    val totalDue: Double,
    val invoicesCount: Int,
    val lastBillDate: Long
)

data class BillingUiState(
    val currentTab: ScreenTab = ScreenTab.BILLING,
    val products: List<ProductEntity> = emptyList(),
    val lowStockProducts: List<ProductEntity> = emptyList(),
    val invoices: List<InvoiceEntity> = emptyList(),
    val dueInvoices: List<InvoiceEntity> = emptyList(),
    val payments: List<PaymentRecordEntity> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    // Cart / POS state
    val customerName: String = "",
    val customerPhone: String = "",
    val cartItems: List<CartItem> = emptyList(),
    val gstPercent: Double = 0.0, // 0%, 5%, 12%, 18%
    val discountAmount: Double = 0.0,
    val paymentMode: String = "CASH", // CASH, UPI, CARD, CREDIT
    val paidAmountInput: String = "", // custom paid amount if partial/credit
    val notes: String = "",
    // Active Dialogs
    val activeInvoiceForDetail: InvoiceEntity? = null,
    val activeInvoiceItems: List<InvoiceItemEntity> = emptyList(),
    val showAddProductDialog: Boolean = false,
    val editingProduct: ProductEntity? = null,
    val showStockAdjustProduct: ProductEntity? = null,
    val showUpiQrDialog: Boolean = false,
    val upiQrAmount: Double = 0.0,
    val upiQrInvoiceNumber: String = "",
    val showCollectPaymentInvoice: InvoiceEntity? = null,
    val snackbarMessage: String? = null
) {
    val subtotal: Double
        get() = cartItems.sumOf { it.total }

    val gstAmount: Double
        get() = if (gstPercent > 0) ((subtotal - discountAmount).coerceAtLeast(0.0) * gstPercent / 100.0) else 0.0

    val grandTotal: Double
        get() = ((subtotal - discountAmount).coerceAtLeast(0.0) + gstAmount)

    val effectivePaidAmount: Double
        get() {
            if (paymentMode == "CREDIT") return 0.0
            val parsed = paidAmountInput.toDoubleOrNull()
            return if (parsed != null) parsed.coerceAtMost(grandTotal) else grandTotal
        }

    val dueAmount: Double
        get() = (grandTotal - effectivePaidAmount).coerceAtLeast(0.0)

    val customerDuesList: List<CustomerDueSummary>
        get() {
            return dueInvoices
                .groupBy { it.customerPhone.ifBlank { it.customerName } }
                .map { (_, invList) ->
                    CustomerDueSummary(
                        customerName = invList.firstOrNull()?.customerName ?: "Customer",
                        customerPhone = invList.firstOrNull()?.customerPhone ?: "",
                        totalDue = invList.sumOf { it.dueAmount },
                        invoicesCount = invList.size,
                        lastBillDate = invList.maxOf { it.timestamp }
                    )
                }
                .filter { it.totalDue > 0 }
                .sortedByDescending { it.totalDue }
        }

    val todaySalesTotal: Double
        get() {
            val startOfDay = getStartOfTodayMillis()
            return invoices.filter { it.timestamp >= startOfDay }.sumOf { it.grandTotal }
        }

    val todayCashTotal: Double
        get() {
            val startOfDay = getStartOfTodayMillis()
            return invoices.filter { it.timestamp >= startOfDay && it.paymentMode == "CASH" }.sumOf { it.paidAmount }
        }

    val todayUpiTotal: Double
        get() {
            val startOfDay = getStartOfTodayMillis()
            return invoices.filter { it.timestamp >= startOfDay && it.paymentMode == "UPI" }.sumOf { it.paidAmount }
        }

    val totalOutstandingDues: Double
        get() = dueInvoices.sumOf { it.dueAmount }

    private fun getStartOfTodayMillis(): Long {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
