package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.DefaultData
import com.example.ui.state.BillingUiState
import com.example.ui.state.CartItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Dhaya Mobiles", appName)
    }

    @Test
    fun `default products list contains sample phones and accessories`() {
        val products = DefaultData.initialProducts
        assertTrue(products.isNotEmpty())
        assertTrue(products.any { it.name.contains("Vivo", ignoreCase = true) })
        assertTrue(products.any { it.category == "Smartphones" })
        assertTrue(products.any { it.category == "Chargers" })
    }

    @Test
    fun `billing cart calculation calculates subtotal and grand total with discount and gst`() {
        val cartItems = listOf(
            CartItem(
                productId = 1L,
                name = "Charger",
                category = "Chargers",
                unitPrice = 500.0,
                quantity = 2
            ),
            CartItem(
                productId = 2L,
                name = "Glass",
                category = "Cases & Glass",
                unitPrice = 150.0,
                quantity = 1
            )
        )
        // Subtotal = 1000 + 150 = 1150
        val state = BillingUiState(
            cartItems = cartItems,
            discountAmount = 50.0,
            gstPercent = 18.0
        )

        assertEquals(1150.0, state.subtotal, 0.01)
        // Taxable = 1150 - 50 = 1100
        // GST (18%) = 198.0
        assertEquals(198.0, state.gstAmount, 0.01)
        // Grand Total = 1100 + 198 = 1298.0
        assertEquals(1298.0, state.grandTotal, 0.01)
    }

    @Test
    fun `due amount calculation when customer pays partially or on credit`() {
        val cartItems = listOf(
            CartItem(
                productId = 1L,
                name = "Phone",
                category = "Smartphones",
                unitPrice = 10000.0,
                quantity = 1
            )
        )
        val state = BillingUiState(
            cartItems = cartItems,
            paymentMode = "CASH",
            paidAmountInput = "4000"
        )
        assertEquals(10000.0, state.grandTotal, 0.01)
        assertEquals(4000.0, state.effectivePaidAmount, 0.01)
        assertEquals(6000.0, state.dueAmount, 0.01)
    }
}
