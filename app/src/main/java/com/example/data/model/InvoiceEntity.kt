package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceNumber: String,
    val customerName: String,
    val customerPhone: String,
    val timestamp: Long = System.currentTimeMillis(),
    val subtotal: Double,
    val discountPercent: Double = 0.0,
    val discountAmount: Double = 0.0,
    val gstPercent: Double = 0.0, // 0, 5, 12, 18
    val gstAmount: Double = 0.0,
    val grandTotal: Double,
    val paidAmount: Double,
    val dueAmount: Double = 0.0,
    val paymentMode: String, // CASH, UPI, CARD, CREDIT, SPLIT
    val status: String = "PAID", // PAID, PARTIAL, DUE
    val notes: String? = null
)

@Entity(
    tableName = "invoice_items",
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["invoiceId"])]
)
data class InvoiceItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceId: Long,
    val productId: Long? = null,
    val productName: String,
    val category: String,
    val imeiOrSerial: String? = null,
    val unitPrice: Double,
    val quantity: Int = 1,
    val total: Double
)

@Entity(
    tableName = "payment_records",
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["invoiceId"])]
)
data class PaymentRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceId: Long,
    val customerPhone: String,
    val amount: Double,
    val paymentMode: String, // CASH, UPI, CARD
    val timestamp: Long = System.currentTimeMillis(),
    val note: String? = null
)
