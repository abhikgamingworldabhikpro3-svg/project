package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.ClassEntity
import com.example.data.models.FeeRecordEntity
import com.example.data.models.StudentEntity
import com.example.ui.components.ReceiptDialog
import com.example.ui.components.StatMetricCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.TuitionViewModel

@Composable
fun FeeManagementScreen(viewModel: TuitionViewModel) {
    val fees by viewModel.fees.collectAsStateWithLifecycle()
    val students by viewModel.students.collectAsStateWithLifecycle()
    val classes by viewModel.classes.collectAsStateWithLifecycle()
    val selectedReceipt by viewModel.selectedReceipt.collectAsStateWithLifecycle()

    var statusFilter by remember { mutableStateOf("All") } // All, Paid, Pending, Overdue
    var showRecordPaymentDialog by remember { mutableStateOf(false) }

    val totalCollected = fees.sumOf { it.amountPaid }
    val totalPending = fees.filter { it.status == "Pending" || it.status == "Partially Paid" }
        .sumOf { (it.amountDue - it.amountPaid).coerceAtLeast(0.0) }
    val totalOverdue = fees.filter { it.status == "Overdue" }
        .sumOf { (it.amountDue - it.amountPaid).coerceAtLeast(0.0) }

    val filteredFees = fees.filter { fee ->
        when (statusFilter) {
            "Paid" -> fee.status == "Paid"
            "Pending" -> fee.status == "Pending" || fee.status == "Partially Paid"
            "Overdue" -> fee.status == "Overdue"
            else -> true
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showRecordPaymentDialog = true },
                containerColor = RoyalBlue600,
                contentColor = Color.White,
                modifier = Modifier.testTag("record_payment_fab")
            ) {
                Icon(imageVector = Icons.Default.AddCard, contentDescription = "Record Fee")
            }
        },
        containerColor = Slate50
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .testTag("fee_management_screen")
        ) {
            // Header stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMetricCard(
                    title = "Collected",
                    value = "₹${totalCollected.toInt()}",
                    subtitle = "Cleared fees",
                    icon = Icons.Default.CheckCircle,
                    iconTint = Emerald600,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Pending",
                    value = "₹${totalPending.toInt()}",
                    subtitle = "Due this month",
                    icon = Icons.Default.HourglassTop,
                    iconTint = Amber600,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Overdue",
                    value = "₹${totalOverdue.toInt()}",
                    subtitle = "Delayed dues",
                    icon = Icons.Default.Warning,
                    iconTint = Rose600,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("All", "Paid", "Pending", "Overdue").forEach { status ->
                    item {
                        FilterChip(
                            selected = statusFilter == status,
                            onClick = { statusFilter = status },
                            label = { Text(status) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fees List
            Text(
                text = "Tuition Fee Ledger (${filteredFees.size} Records)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredFees.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No fee records found for this filter.", color = Slate600)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredFees, key = { it.id }) { fee ->
                        val student = students.find { it.id == fee.studentId }
                        val cls = classes.find { it.id == fee.classId }

                        FeeRecordCard(
                            fee = fee,
                            studentName = student?.name ?: "Student #${fee.studentId}",
                            className = cls?.name ?: "Class",
                            onViewReceipt = { viewModel.viewReceipt(fee) }
                        )
                    }
                }
            }
        }
    }

    if (showRecordPaymentDialog) {
        RecordFeePaymentDialog(
            students = students,
            classes = classes,
            onDismiss = { showRecordPaymentDialog = false },
            onRecord = { studentId, classId, month, due, paid, method, ref, notes ->
                viewModel.recordFeePayment(studentId, classId, month, due, paid, method, ref, notes)
                showRecordPaymentDialog = false
            }
        )
    }

    selectedReceipt?.let { fee ->
        val student = students.find { it.id == fee.studentId }
        val cls = classes.find { it.id == fee.classId }
        ReceiptDialog(
            fee = fee,
            student = student,
            tuitionClass = cls,
            onDismiss = { viewModel.viewReceipt(null) }
        )
    }
}

@Composable
fun FeeRecordCard(
    fee: FeeRecordEntity,
    studentName: String,
    className: String,
    onViewReceipt: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fee_card_${fee.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = studentName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = "$className • ${fee.monthYear}",
                        style = MaterialTheme.typography.bodySmall,
                        color = RoyalBlue700
                    )
                }
                StatusBadge(status = fee.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Paid: ₹${fee.amountPaid.toInt()} / ₹${fee.amountDue.toInt()}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (fee.status == "Paid") Emerald600 else Slate900
                    )
                    if (fee.paymentMethod.isNotEmpty()) {
                        Text(
                            text = "Mode: ${fee.paymentMethod} • Ref: ${fee.receiptNo}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate600
                        )
                    }
                }

                Button(
                    onClick = onViewReceipt,
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue100),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_view_receipt_${fee.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = RoyalBlue900,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Receipt", color = RoyalBlue900, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RecordFeePaymentDialog(
    students: List<StudentEntity>,
    classes: List<ClassEntity>,
    onDismiss: () -> Unit,
    onRecord: (Long, Long, String, Double, Double, String, String, String) -> Unit
) {
    var selectedStudentId by remember { mutableStateOf(students.firstOrNull()?.id ?: 1L) }
    val currentStudent = students.find { it.id == selectedStudentId }
    var selectedClassId by remember { mutableStateOf(currentStudent?.classId ?: classes.firstOrNull()?.id ?: 1L) }
    var monthYear by remember { mutableStateOf("September 2026") }
    var amountDue by remember { mutableStateOf("${currentStudent?.monthlyFee?.toInt() ?: 1500}") }
    var amountPaid by remember { mutableStateOf("${currentStudent?.monthlyFee?.toInt() ?: 1500}") }
    var method by remember { mutableStateOf("UPI / GPay") }
    var ref by remember { mutableStateOf("UPI${(100000..999999).random()}") }
    var notes by remember { mutableStateOf("Tuition fee collected") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("record_fee_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Record Fee Payment",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Record tuition fee and generate instant digital receipt",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Select Student:", style = MaterialTheme.typography.labelSmall, color = Slate600)
                students.take(5).forEach { s ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedStudentId = s.id
                                selectedClassId = s.classId
                                amountDue = "${s.monthlyFee.toInt()}"
                                amountPaid = "${s.monthlyFee.toInt()}"
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStudentId == s.id,
                            onClick = {
                                selectedStudentId = s.id
                                selectedClassId = s.classId
                                amountDue = "${s.monthlyFee.toInt()}"
                                amountPaid = "${s.monthlyFee.toInt()}"
                            }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${s.name} (${s.studentCode})", fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = monthYear,
                    onValueChange = { monthYear = it },
                    label = { Text("Billing Month & Year") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = amountDue,
                        onValueChange = { amountDue = it },
                        label = { Text("Amount Due (₹)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = amountPaid,
                        onValueChange = { amountPaid = it },
                        label = { Text("Amount Paid (₹)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = method,
                    onValueChange = { method = it },
                    label = { Text("Payment Mode (UPI, Cash, Bank)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = ref,
                    onValueChange = { ref = it },
                    label = { Text("Transaction / Receipt Ref ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val due = amountDue.toDoubleOrNull() ?: 1500.0
                            val paid = amountPaid.toDoubleOrNull() ?: due
                            onRecord(selectedStudentId, selectedClassId, monthYear, due, paid, method, ref, notes)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("confirm_record_fee_button")
                    ) {
                        Text("Save & Issue")
                    }
                }
            }
        }
    }
}
