package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.InvoiceEntity
import com.example.data.model.InvoiceItemEntity
import com.example.ui.theme.DhayaGold
import com.example.ui.theme.DhayaGoldBright
import com.example.ui.theme.DhayaNavy
import com.example.ui.theme.StatusDue
import com.example.ui.theme.StatusDueBg
import com.example.ui.theme.StatusPaid
import com.example.ui.theme.StatusPaidBg
import com.example.ui.theme.StatusPartial
import com.example.ui.theme.StatusPartialBg
import com.example.ui.theme.WhatsAppGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InvoiceDetailDialog(
    invoice: InvoiceEntity,
    items: List<InvoiceItemEntity>,
    onDismiss: () -> Unit,
    onCollectDue: ((InvoiceEntity) -> Unit)? = null
) {
    val context = LocalContext.current
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(invoice.timestamp))

    fun buildBillText(): String {
        val sb = StringBuilder()
        sb.appendLine("📱 *DHAYA MOBILES* 📱")
        sb.appendLine("📞 Contact: +91 7550281815")
        sb.appendLine("📍 Retail Mobile Sales, Spares & Service")
        sb.appendLine("----------------------------------------")
        sb.appendLine("🧾 *TAX INVOICE: ${invoice.invoiceNumber}*")
        sb.appendLine("📅 Date: $dateStr")
        sb.appendLine("👤 Customer: ${invoice.customerName}")
        sb.appendLine("📱 Phone: ${invoice.customerPhone}")
        sb.appendLine("----------------------------------------")
        items.forEachIndexed { i, item ->
            sb.appendLine("${i + 1}. *${item.productName}*")
            if (!item.imeiOrSerial.isNullOrBlank()) {
                sb.appendLine("   IMEI/SN: ${item.imeiOrSerial}")
            }
            sb.appendLine("   Qty: ${item.quantity} × ₹${item.unitPrice.toInt()} = ₹${item.total.toInt()}")
        }
        sb.appendLine("----------------------------------------")
        sb.appendLine("Subtotal: ₹${invoice.subtotal.toInt()}")
        if (invoice.discountAmount > 0) {
            sb.appendLine("Discount: -₹${invoice.discountAmount.toInt()}")
        }
        if (invoice.gstPercent > 0) {
            sb.appendLine("GST (${invoice.gstPercent.toInt()}%): +₹${invoice.gstAmount.toInt()}")
        }
        sb.appendLine("*Grand Total: ₹${invoice.grandTotal.toInt()}*")
        sb.appendLine("Payment Mode: ${invoice.paymentMode}")
        sb.appendLine("Paid Amount: ₹${invoice.paidAmount.toInt()}")
        if (invoice.dueAmount > 0) {
            sb.appendLine("⚠️ *Balance Due: ₹${invoice.dueAmount.toInt()}*")
        }
        sb.appendLine("----------------------------------------")
        sb.appendLine("✨ Thank you for choosing Dhaya Mobiles! ✨")
        return sb.toString()
    }

    fun shareWhatsApp() {
        val message = buildBillText()
        val phoneClean = invoice.customerPhone.filter { it.isDigit() }
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
            context.startActivity(Intent.createChooser(sendIntent, "Share Bill via"))
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .testTag("invoice_detail_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Invoice Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = DhayaNavy
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Printable Receipt Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFAFAFA))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Shop Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "DHAYA MOBILES",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DhayaNavy
                                )
                                Text(
                                    text = "Ph: 7550281815",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A)
                                )
                                Text(
                                    text = "Mobile Sales, Spares & Billing",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                            DhayaLogoBadge(size = 50.dp, showContact = false)
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFFE2E8F0)
                        )

                        // Meta Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("INVOICE NO", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                                Text(invoice.invoiceNumber, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DhayaNavy)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("DATE & TIME", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                                Text(dateStr, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Customer Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("BILLED TO:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Text(invoice.customerName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DhayaNavy)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("CONTACT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Text(invoice.customerPhone, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = DhayaNavy)
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFFE2E8F0)
                        )

                        // Items Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ITEM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.weight(2f))
                            Text("QTY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.weight(0.7f))
                            Text("AMOUNT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Items List
                        items.forEach { item ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(2f)) {
                                        Text(
                                            text = item.productName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF0F172A)
                                        )
                                        if (!item.imeiOrSerial.isNullOrBlank()) {
                                            Text(
                                                text = "IMEI: ${item.imeiOrSerial}",
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = Color(0xFF475569)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${item.quantity}x",
                                        fontSize = 12.sp,
                                        color = Color(0xFF475569),
                                        modifier = Modifier.weight(0.7f)
                                    )
                                    Text(
                                        text = "₹${item.total.toInt()}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DhayaNavy,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFFE2E8F0)
                        )

                        // Calculation summary
                        SummaryRow("Subtotal", "₹${invoice.subtotal.toInt()}")
                        if (invoice.discountAmount > 0) {
                            SummaryRow("Discount", "-₹${invoice.discountAmount.toInt()}", valueColor = Color(0xFF16A34A))
                        }
                        if (invoice.gstPercent > 0) {
                            SummaryRow("GST (${invoice.gstPercent.toInt()}%)", "+₹${invoice.gstAmount.toInt()}")
                        }
                        SummaryRow(
                            "Grand Total",
                            "₹${invoice.grandTotal.toInt()}",
                            isBold = true,
                            fontSize = 16
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Status & Paid Pill
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Payment: ${invoice.paymentMode}", fontSize = 11.sp, color = Color.Gray)
                                Text("Paid: ₹${invoice.paidAmount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                            }
                            val (badgeBg, badgeColor, statusText) = when (invoice.status) {
                                "PAID" -> Triple(StatusPaidBg, StatusPaid, "PAID")
                                "PARTIAL" -> Triple(StatusPartialBg, StatusPartial, "PARTIAL DUE")
                                else -> Triple(StatusDueBg, StatusDue, "DUE (KHATA)")
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(badgeBg)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = statusText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeColor
                                )
                            }
                        }

                        if (invoice.dueAmount > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "Balance Due: ₹${invoice.dueAmount.toInt()}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusDue
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Button(
                    onClick = { shareWhatsApp() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("share_whatsapp_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share Bill on WhatsApp",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Dhaya Bill", buildBillText()))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Text", fontSize = 12.sp)
                    }

                    if (invoice.dueAmount > 0 && onCollectDue != null) {
                        Button(
                            onClick = {
                                onCollectDue(invoice)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DhayaNavy),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp), tint = DhayaGoldBright)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Collect Due", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    fontSize: Int = 12,
    valueColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = fontSize.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) DhayaNavy else Color(0xFF475569)
        )
        Text(
            text = value,
            fontSize = fontSize.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = if (valueColor != Color.Unspecified) valueColor else if (isBold) DhayaNavy else Color(0xFF0F172A)
        )
    }
}
