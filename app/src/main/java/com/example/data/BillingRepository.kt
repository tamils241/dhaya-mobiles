package com.example.data

import com.example.data.dao.InvoiceDao
import com.example.data.dao.PaymentDao
import com.example.data.dao.ProductDao
import com.example.data.model.InvoiceEntity
import com.example.data.model.InvoiceItemEntity
import com.example.data.model.PaymentRecordEntity
import com.example.data.model.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BillingRepository(
    private val productDao: ProductDao,
    private val invoiceDao: InvoiceDao,
    private val paymentDao: PaymentDao
) {
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val lowStockProducts: Flow<List<ProductEntity>> = productDao.getLowStockProducts()
    val allInvoices: Flow<List<InvoiceEntity>> = invoiceDao.getAllInvoices()
    val dueInvoices: Flow<List<InvoiceEntity>> = invoiceDao.getDueInvoices()
    val allPayments: Flow<List<PaymentRecordEntity>> = paymentDao.getAllPayments()

    suspend fun initializeSampleDataIfNeeded() = withContext(Dispatchers.IO) {
        if (productDao.getProductCount() == 0) {
            productDao.insertAll(DefaultData.initialProducts)
        }
        if (invoiceDao.getInvoiceCount() == 0) {
            val samples = DefaultData.createInitialInvoices()
            for ((invoice, items) in samples) {
                val invId = invoiceDao.insertInvoice(invoice)
                val itemsWithId = items.map { it.copy(invoiceId = invId) }
                invoiceDao.insertInvoiceItems(itemsWithId)
                if (invoice.paidAmount > 0) {
                    paymentDao.insertPayment(
                        PaymentRecordEntity(
                            invoiceId = invId,
                            customerPhone = invoice.customerPhone,
                            amount = invoice.paidAmount,
                            paymentMode = invoice.paymentMode,
                            timestamp = invoice.timestamp,
                            note = "Initial payment upon invoice creation"
                        )
                    )
                }
            }
        }
    }

    fun searchProducts(query: String): Flow<List<ProductEntity>> {
        return productDao.searchProducts(query)
    }

    suspend fun addProduct(product: ProductEntity): Long = withContext(Dispatchers.IO) {
        productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.updateProduct(product)
    }

    suspend fun adjustProductStock(id: Long, qtyDelta: Int) = withContext(Dispatchers.IO) {
        productDao.adjustStock(id, qtyDelta)
    }

    suspend fun deleteProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.deleteProduct(product)
    }

    fun searchInvoices(query: String): Flow<List<InvoiceEntity>> {
        return invoiceDao.searchInvoices(query)
    }

    suspend fun getInvoiceItems(invoiceId: Long): List<InvoiceItemEntity> = withContext(Dispatchers.IO) {
        invoiceDao.getItemsForInvoiceSync(invoiceId)
    }

    suspend fun getInvoicePayments(invoiceId: Long): Flow<List<PaymentRecordEntity>> {
        return paymentDao.getPaymentsForInvoice(invoiceId)
    }

    suspend fun getNextInvoiceNumber(): String = withContext(Dispatchers.IO) {
        val count = invoiceDao.getInvoiceCount() + 1
        "DM-${1000 + count}"
    }

    suspend fun createInvoice(
        invoice: InvoiceEntity,
        items: List<InvoiceItemEntity>
    ): Long = withContext(Dispatchers.IO) {
        val invoiceId = invoiceDao.insertInvoice(invoice)
        val itemsWithId = items.map { it.copy(invoiceId = invoiceId) }
        invoiceDao.insertInvoiceItems(itemsWithId)

        // Deduct stock for inventory items
        for (item in items) {
            item.productId?.let { prodId ->
                productDao.adjustStock(prodId, -item.quantity)
            }
        }

        // Record initial payment if any
        if (invoice.paidAmount > 0) {
            paymentDao.insertPayment(
                PaymentRecordEntity(
                    invoiceId = invoiceId,
                    customerPhone = invoice.customerPhone,
                    amount = invoice.paidAmount,
                    paymentMode = invoice.paymentMode,
                    timestamp = invoice.timestamp,
                    note = "Payment at billing"
                )
            )
        }

        invoiceId
    }

    suspend fun recordDueSettlement(
        invoiceId: Long,
        amount: Double,
        paymentMode: String,
        note: String?
    ) = withContext(Dispatchers.IO) {
        val invoice = invoiceDao.getInvoiceById(invoiceId) ?: return@withContext
        val newPaid = invoice.paidAmount + amount
        val newDue = (invoice.grandTotal - newPaid).coerceAtLeast(0.0)
        val newStatus = if (newDue <= 0.01) "PAID" else "PARTIAL"

        val updatedInvoice = invoice.copy(
            paidAmount = newPaid,
            dueAmount = newDue,
            status = newStatus
        )
        invoiceDao.updateInvoice(updatedInvoice)

        paymentDao.insertPayment(
            PaymentRecordEntity(
                invoiceId = invoiceId,
                customerPhone = invoice.customerPhone,
                amount = amount,
                paymentMode = paymentMode,
                timestamp = System.currentTimeMillis(),
                note = note ?: "Due settlement"
            )
        )
    }
}
