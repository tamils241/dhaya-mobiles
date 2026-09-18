package com.example.data

import com.example.data.model.InvoiceEntity
import com.example.data.model.InvoiceItemEntity
import com.example.data.model.ProductEntity

object DefaultData {
    val initialProducts = listOf(
        ProductEntity(
            name = "Redmi 13C 5G (8GB / 256GB)",
            brand = "Xiaomi / Redmi",
            category = "Smartphones",
            sellingPrice = 13999.0,
            purchasePrice = 12400.0,
            stockQty = 6,
            minStockQty = 2,
            imeiOrSerial = "869402058392014",
            description = "Starshine Green, 50MP AI Camera, 5000mAh Battery"
        ),
        ProductEntity(
            name = "Samsung Galaxy A15 5G (128GB)",
            brand = "Samsung",
            category = "Smartphones",
            sellingPrice = 16499.0,
            purchasePrice = 14800.0,
            stockQty = 4,
            minStockQty = 2,
            imeiOrSerial = "358291048291047",
            description = "Blue Black, Super AMOLED, Knox Security"
        ),
        ProductEntity(
            name = "Vivo Y28 5G (6GB / 128GB)",
            brand = "Vivo",
            category = "Smartphones",
            sellingPrice = 15499.0,
            purchasePrice = 13900.0,
            stockQty = 5,
            minStockQty = 2,
            imeiOrSerial = "867201948291054",
            description = "Crystal Purple, Dual Camera, Fast Charging"
        ),
        ProductEntity(
            name = "OnePlus Nord CE4 Lite 5G",
            brand = "OnePlus",
            category = "Smartphones",
            sellingPrice = 19999.0,
            purchasePrice = 18200.0,
            stockQty = 3,
            minStockQty = 2,
            imeiOrSerial = "864920184729102",
            description = "Mega Blue, 80W SUPERVOOC, 120Hz AMOLED"
        ),
        ProductEntity(
            name = "Nokia 105 Single SIM Keypad",
            brand = "Nokia",
            category = "Keypad",
            sellingPrice = 1399.0,
            purchasePrice = 1100.0,
            stockQty = 12,
            minStockQty = 4,
            imeiOrSerial = "359182740192847",
            description = "Long battery life, Wireless FM, Built-in Torch"
        ),
        ProductEntity(
            name = "Itel MagicX 4G Keypad Phone",
            brand = "Itel",
            category = "Keypad",
            sellingPrice = 2199.0,
            purchasePrice = 1750.0,
            stockQty = 8,
            minStockQty = 3,
            imeiOrSerial = "863920184729104",
            description = "4G VoLTE, Hotspot tethering, Dual SIM"
        ),
        ProductEntity(
            name = "Boat Airdopes 141 ANC Earbuds",
            brand = "Boat",
            category = "Audio",
            sellingPrice = 1499.0,
            purchasePrice = 950.0,
            stockQty = 15,
            minStockQty = 5,
            description = "42H Playtime, Beast Mode 50ms, ENx Tech"
        ),
        ProductEntity(
            name = "Noise ColorFit Pulse 2 Smartwatch",
            brand = "Noise",
            category = "Accessories",
            sellingPrice = 1799.0,
            purchasePrice = 1150.0,
            stockQty = 7,
            minStockQty = 3,
            description = "1.85\" TFT, Bluetooth Calling, 100+ Sports Modes"
        ),
        ProductEntity(
            name = "25W Type-C Fast Adapter (Original)",
            brand = "Samsung",
            category = "Chargers",
            sellingPrice = 1299.0,
            purchasePrice = 850.0,
            stockQty = 9,
            minStockQty = 3,
            description = "PD 3.0 PPS Super Fast Charger"
        ),
        ProductEntity(
            name = "65W VOOC / Dart Warp Fast Charger",
            brand = "Realme / OnePlus",
            category = "Chargers",
            sellingPrice = 1499.0,
            purchasePrice = 900.0,
            stockQty = 8,
            minStockQty = 2,
            description = "Flash Charging with Overheat Protection"
        ),
        ProductEntity(
            name = "Braided Type-C to Type-C Cable 1.5m",
            brand = "Generic / MaxPro",
            category = "Accessories",
            sellingPrice = 299.0,
            purchasePrice = 110.0,
            stockQty = 25,
            minStockQty = 6,
            description = "60W Fast Charging, High Durability Braided Wire"
        ),
        ProductEntity(
            name = "9H UV Curved Tempered Glass + Fixing",
            brand = "MaxShield",
            category = "Cases & Glass",
            sellingPrice = 349.0,
            purchasePrice = 90.0,
            stockQty = 30,
            minStockQty = 8,
            description = "Full Edge to Edge Curved Screen Protection"
        ),
        ProductEntity(
            name = "Premium Matte Silicone Back Case",
            brand = "MaxArmor",
            category = "Cases & Glass",
            sellingPrice = 249.0,
            purchasePrice = 75.0,
            stockQty = 22,
            minStockQty = 5,
            description = "Shockproof Camera Protection Bumper"
        ),
        ProductEntity(
            name = "Original Display Replacement + Labor",
            brand = "Dhaya Service",
            category = "Repair",
            sellingPrice = 2800.0,
            purchasePrice = 1900.0,
            stockQty = 5,
            minStockQty = 2,
            description = "OEM Quality Display with 30 Days Touch Warranty"
        )
    )

    fun createInitialInvoices(): List<Pair<InvoiceEntity, List<InvoiceItemEntity>>> {
        val now = System.currentTimeMillis()
        val dayMillis = 86_400_000L

        val inv1 = InvoiceEntity(
            invoiceNumber = "DM-1001",
            customerName = "Karthik Raja",
            customerPhone = "9840212345",
            timestamp = now - (dayMillis * 1),
            subtotal = 13999.0 + 349.0,
            discountPercent = 0.0,
            discountAmount = 348.0,
            gstPercent = 18.0,
            gstAmount = 0.0, // Included
            grandTotal = 14000.0,
            paidAmount = 14000.0,
            dueAmount = 0.0,
            paymentMode = "UPI",
            status = "PAID",
            notes = "Redmi 13C with Tempered Glass package"
        )
        val items1 = listOf(
            InvoiceItemEntity(
                invoiceId = 0,
                productName = "Redmi 13C 5G (8GB / 256GB)",
                category = "Smartphones",
                imeiOrSerial = "869402058392014",
                unitPrice = 13999.0,
                quantity = 1,
                total = 13999.0
            ),
            InvoiceItemEntity(
                invoiceId = 0,
                productName = "9H UV Curved Tempered Glass + Fixing",
                category = "Cases & Glass",
                unitPrice = 349.0,
                quantity = 1,
                total = 349.0
            )
        )

        val inv2 = InvoiceEntity(
            invoiceNumber = "DM-1002",
            customerName = "Suresh Kumar",
            customerPhone = "9443198765",
            timestamp = now - (dayMillis * 2),
            subtotal = 1499.0 + 299.0,
            discountPercent = 0.0,
            discountAmount = 98.0,
            gstPercent = 0.0,
            gstAmount = 0.0,
            grandTotal = 1700.0,
            paidAmount = 1000.0,
            dueAmount = 700.0,
            paymentMode = "CASH",
            status = "PARTIAL",
            notes = "Customer promised balance payment this Saturday"
        )
        val items2 = listOf(
            InvoiceItemEntity(
                invoiceId = 0,
                productName = "Boat Airdopes 141 ANC Earbuds",
                category = "Audio",
                unitPrice = 1499.0,
                quantity = 1,
                total = 1499.0
            ),
            InvoiceItemEntity(
                invoiceId = 0,
                productName = "Braided Type-C to Type-C Cable 1.5m",
                category = "Accessories",
                unitPrice = 299.0,
                quantity = 1,
                total = 299.0
            )
        )

        val inv3 = InvoiceEntity(
            invoiceNumber = "DM-1003",
            customerName = "Praveen M",
            customerPhone = "9789012340",
            timestamp = now - (3600_000 * 3), // Today, 3 hrs ago
            subtotal = 1299.0 + 249.0,
            discountPercent = 0.0,
            discountAmount = 48.0,
            gstPercent = 0.0,
            gstAmount = 0.0,
            grandTotal = 1500.0,
            paidAmount = 1500.0,
            dueAmount = 0.0,
            paymentMode = "CASH",
            status = "PAID",
            notes = "Samsung 25W Charger + Back Cover"
        )
        val items3 = listOf(
            InvoiceItemEntity(
                invoiceId = 0,
                productName = "25W Type-C Fast Adapter (Original)",
                category = "Chargers",
                unitPrice = 1299.0,
                quantity = 1,
                total = 1299.0
            ),
            InvoiceItemEntity(
                invoiceId = 0,
                productName = "Premium Matte Silicone Back Case",
                category = "Cases & Glass",
                unitPrice = 249.0,
                quantity = 1,
                total = 249.0
            )
        )

        return listOf(
            Pair(inv1, items1),
            Pair(inv2, items2),
            Pair(inv3, items3)
        )
    }
}
