package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BillingViewModel
import com.example.ui.state.BillingUiState
import com.example.ui.state.CustomerDueSummary
import com.example.ui.theme.DhayaGoldBright
import com.example.ui.theme.DhayaNavy
import com.example.ui.theme.StatusDue
import com.example.ui.theme.WhatsAppGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun KhataScreen(
    state: BillingUiState,
    viewModel: BillingViewModel
) {
    val context = LocalContext.current
    val duesList = state.customerDuesList
    val totalPendingAmount = state.totalOutstandingDues

    fun sendWhatsAppReminder(customer: CustomerDueSummary) {
        val message = "Dear ${customer.customerName},\n\n" +
                "This is a gentle payment reminder from *DHAYA MOBILES*.\n" +
                "Your current outstanding balance is *₹${customer.totalDue.toInt()}*.\n\n" +
                "Kindly pay via UPI to *7550281815@upi* or visit our shop to settle.\n" +
                "Contact: +91 7550281815\n\n" +
                "Thank you for your business!"

        val phoneClean = customer.customerPhone.filter { it.isDigit() }
        val targetPhone = if (phoneClean.length == 10) "91$phoneClean" else phoneClean

        try {
            val url = "https://api.whatsapp.com/send?phone=$targetPhone&text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            context.startActivity(Intent.createChooser(sendIntent, "Send Reminder via"))
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 14.dp)
            .testTag("khata_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp)
    ) {
        // Outstanding Dues Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1D1D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TOTAL OUTSTANDING CUSTOMER DUES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFCA5A5)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format("₹%,.0f", totalPendingAmount),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${duesList.size} Customers with pending balance",
                            fontSize = 12.sp,
                            color = Color(0xFFFEE2E2)
                        )
                        Text(
                            text = "${state.dueInvoices.size} Unsettled Bills",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFEE2E2)
                        )
                    }
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "Customer Ledger & Dues",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = DhayaNavy
            )
        }

        if (duesList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "All customer dues are clear!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                        Text(
                            text = "No pending payments in Khata ledger",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(duesList) { customer ->
                CustomerDueCard(
                    customer = customer,
                    onSendReminder = { sendWhatsAppReminder(customer) },
                    onCollectPayment = {
                        // Find the oldest unsettled invoice for this customer
                        val inv = state.dueInvoices.firstOrNull {
                            it.customerPhone == customer.customerPhone || it.customerName == customer.customerName
                        }
                        if (inv != null) {
                            viewModel.openCollectPaymentDialog(inv)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CustomerDueCard(
    customer: CustomerDueSummary,
    onSendReminder: () -> Unit,
    onCollectPayment: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(customer.lastBillDate))

    Card(
        shape = RoundedCornerShape(14.dp),
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
                Column {
                    Text(
                        text = customer.customerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "📱 ${customer.customerPhone} • Last bill: $dateStr",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${customer.totalDue.toInt()}",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = StatusDue
                    )
                    Text(
                        text = "${customer.invoicesCount} unpaid bill(s)",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSendReminder,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = null,
                        tint = WhatsAppGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "WhatsApp Reminder",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhatsAppGreen
                    )
                }

                Button(
                    onClick = onCollectPayment,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = DhayaNavy),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        Icons.Default.Payment,
                        contentDescription = null,
                        tint = DhayaGoldBright,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Collect Payment",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
