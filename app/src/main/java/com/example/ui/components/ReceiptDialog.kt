package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.models.ClassEntity
import com.example.data.models.FeeRecordEntity
import com.example.data.models.StudentEntity
import com.example.ui.theme.*

@Composable
fun ReceiptDialog(
    fee: FeeRecordEntity,
    student: StudentEntity?,
    tuitionClass: ClassEntity?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
                .testTag("tuition_receipt_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = "Receipt",
                            tint = RoyalBlue600,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Official Tuition Receipt",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_receipt_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Official Receipt Canvas Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Slate200, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Slate50),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        // Tuition Header
                        Text(
                            text = "APEX SCHOLARS COACHING ACADEMY",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = RoyalBlue900,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Recognized Tuition Centre • CBSE / ICSE / NEET & NDA",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate600,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Phone: +91 98765 43210 • Email: contact@apexscholars.edu",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate600,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Slate200)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Receipt & Date Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "RECEIPT NO:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate600
                                )
                                Text(
                                    text = fee.receiptNo,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalBlue700
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "DATE ISSUED:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate600
                                )
                                Text(
                                    text = fee.paidDate ?: fee.dueDate,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Slate900
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Student Details Box
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(0.5.dp, Slate200, RoundedCornerShape(8.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Student: ${student?.name ?: "Enrolled Student"}",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = student?.studentCode ?: "STU-2026",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Slate600
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Class: ${tuitionClass?.name ?: "Tuition Class"} (${tuitionClass?.batch ?: "Regular"})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate700
                                )
                                Text(
                                    text = "Billing Month: ${fee.monthYear}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate700
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Breakdown Table
                        ReceiptRow(label = "Monthly Tuition Fee", value = "₹${fee.amountDue.toInt()}")
                        if (fee.discount > 0.0) {
                            ReceiptRow(label = "Fee Concession / Discount", value = "-₹${fee.discount.toInt()}", valueColor = Emerald600)
                        }
                        ReceiptRow(
                            label = "Amount Paid",
                            value = "₹${fee.amountPaid.toInt()}",
                            fontWeight = FontWeight.Bold,
                            valueColor = RoyalBlue700
                        )
                        val remaining = (fee.amountDue - fee.discount - fee.amountPaid).coerceAtLeast(0.0)
                        ReceiptRow(
                            label = "Balance Due",
                            value = "₹${remaining.toInt()}",
                            valueColor = if (remaining > 0) Rose600 else Emerald600
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Slate200)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Payment Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Payment Mode: ${fee.paymentMethod.ifEmpty { "Cash/Online" }}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                                if (fee.transactionRef.isNotEmpty()) {
                                    Text(
                                        text = "Ref / Txn ID: ${fee.transactionRef}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate600,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            // Watermark Paid Stamp
                            Box(
                                modifier = Modifier
                                    .rotate(-10f)
                                    .border(
                                        width = 2.dp,
                                        color = if (fee.status == "Paid") Emerald600 else Amber600,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (fee.status == "Paid") "PAID & VERIFIED" else fee.status.uppercase(),
                                    color = if (fee.status == "Paid") Emerald600 else Amber600,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Signature line
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Prof. Rajesh Sharma",
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Authorized Tuition In-Charge",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate600
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Buttons: Share & Done
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    """
                                    *Tuition Fee Receipt*
                                    Receipt No: ${fee.receiptNo}
                                    Student: ${student?.name ?: "Student"} (${student?.studentCode})
                                    Class: ${tuitionClass?.name}
                                    Month: ${fee.monthYear}
                                    Amount Paid: ₹${fee.amountPaid.toInt()}
                                    Status: ${fee.status}
                                    Apex Scholars Coaching Academy
                                    """.trimIndent()
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Receipt"))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_receipt_button")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share")
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("done_receipt_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    fontWeight: FontWeight = FontWeight.Normal,
    valueColor: Color = Slate900
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Slate700)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = fontWeight, color = valueColor)
    }
}
