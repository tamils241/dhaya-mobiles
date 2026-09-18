package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String,
    val category: String, // Smartphones, Keypad, Accessories, Audio, Chargers, Cases & Glass, Repair
    val sellingPrice: Double,
    val purchasePrice: Double = 0.0,
    val stockQty: Int,
    val minStockQty: Int = 3,
    val imeiOrSerial: String? = null,
    val barcode: String? = null,
    val description: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)
