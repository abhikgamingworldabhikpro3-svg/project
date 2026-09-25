package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.ClassEntity
import com.example.data.models.StudentEntity
import com.example.ui.components.InitialsAvatar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.TuitionViewModel

@Composable
fun StudentManagementScreen(viewModel: TuitionViewModel) {
    val context = LocalContext.current
    val students by viewModel.students.collectAsStateWithLifecycle()
    val classes by viewModel.classes.collectAsStateWithLifecycle()
    val selectedProfile by viewModel.selectedStudentProfile.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedClassId by remember { mutableStateOf<Long?>(null) }
    var showAddStudentDialog by remember { mutableStateOf(false) }

    // Filtered list
    val filteredStudents = students.filter { student ->
        val matchesClass = selectedClassId == null || student.classId == selectedClassId
        val matchesQuery = searchQuery.isEmpty() ||
                student.name.contains(searchQuery, ignoreCase = true) ||
                student.studentCode.contains(searchQuery, ignoreCase = true) ||
                student.phone.contains(searchQuery, ignoreCase = true)
        matchesClass && matchesQuery
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddStudentDialog = true },
                containerColor = RoyalBlue600,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_student_fab")
            ) {
                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add Student")
            }
        },
        containerColor = Slate50
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, ID or phone...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_search_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Class Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedClassId == null,
                        onClick = { selectedClassId = null },
                        label = { Text("All Classes (${students.size})") }
                    )
                }
                items(classes) { cls ->
                    val count = students.count { it.classId == cls.id }
                    FilterChip(
                        selected = selectedClassId == cls.id,
                        onClick = { selectedClassId = cls.id },
                        label = { Text("${cls.name} ($count)") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Student Count Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Showing ${filteredStudents.size} Students",
                    style = MaterialTheme.typography.labelMedium,
                    color = Slate600,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredStudents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PersonSearch,
                            contentDescription = null,
                            tint = Slate400,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No students found",
                            style = MaterialTheme.typography.titleMedium,
                            color = Slate600
                        )
                        Text(
                            text = "Try adjusting your search query or class filter.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredStudents, key = { it.id }) { student ->
                        val className = classes.find { it.id == student.classId }?.name ?: "Tuition Class"
                        StudentListItem(
                            student = student,
                            className = className,
                            onClick = { viewModel.viewStudentProfile(student) },
                            onCall = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${student.phone}"))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }

    // Add Student Dialog
    if (showAddStudentDialog) {
        AddStudentDialog(
            classes = classes,
            onDismiss = { showAddStudentDialog = false },
            onAdd = { name, email, phone, parentName, parentPhone, address, gender, classId, batch, fee ->
                viewModel.addStudent(
                    name = name,
                    email = email,
                    phone = phone,
                    parentName = parentName,
                    parentPhone = parentPhone,
                    address = address,
                    gender = gender,
                    classId = classId,
                    batch = batch,
                    fee = fee
                )
                showAddStudentDialog = false
            }
        )
    }

    // Student Profile Modal
    selectedProfile?.let { student ->
        StudentProfileModal(
            student = student,
            classes = classes,
            viewModel = viewModel,
            onDismiss = { viewModel.viewStudentProfile(null) }
        )
    }
}

@Composable
fun StudentListItem(
    student: StudentEntity,
    className: String,
    onClick: () -> Unit,
    onCall: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("student_item_${student.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InitialsAvatar(
                name = student.name,
                modifier = Modifier.size(46.dp),
                backgroundColor = RoyalBlue600
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    StatusBadge(status = student.status)
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${student.studentCode} • $className",
                    style = MaterialTheme.typography.bodySmall,
                    color = RoyalBlue700,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Phone: ${student.phone} • Fee: ₹${student.monthlyFee.toInt()}/mo",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate600
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onCall,
                modifier = Modifier.testTag("call_student_${student.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call",
                    tint = Emerald600,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun StudentProfileModal(
    student: StudentEntity,
    classes: List<ClassEntity>,
    viewModel: TuitionViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentClass = classes.find { it.id == student.classId }
    val attendanceList by viewModel.attendance.collectAsStateWithLifecycle()
    val studentAttendance = attendanceList.filter { it.studentId == student.id }
    val feeRecords by viewModel.fees.collectAsStateWithLifecycle()
    val studentFees = feeRecords.filter { it.studentId == student.id }
    val examResults by viewModel.examResults.collectAsStateWithLifecycle()
    val studentResults = examResults.filter { it.studentId == student.id }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Attendance", "Fees", "Results")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .testTag("student_profile_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        InitialsAvatar(name = student.name, modifier = Modifier.size(42.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = student.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(text = student.studentCode, style = MaterialTheme.typography.labelSmall, color = Slate600)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tabs
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (selectedTab) {
                        0 -> {
                            // Overview
                            ProfileDetailRow("Class & Batch", "${currentClass?.name ?: "Class"} • ${student.batch}")
                            ProfileDetailRow("Email", student.email)
                            ProfileDetailRow("Phone", student.phone)
                            ProfileDetailRow("Parent / Guardian", "${student.parentName} (${student.parentPhone})")
                            ProfileDetailRow("Address", student.address)
                            ProfileDetailRow("Gender & DOB", "${student.gender} • ${student.dob}")
                            ProfileDetailRow("Admission Date", student.admissionDate)
                            ProfileDetailRow("Monthly Fee", "₹${student.monthlyFee.toInt()}")
                            ProfileDetailRow("Status", student.status)

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${student.parentPhone}"))
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Call Parent")
                                }
                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${student.parentPhone}"))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Message")
                                }
                            }
                        }
                        1 -> {
                            // Attendance
                            val totalAtt = studentAttendance.size
                            val present = studentAttendance.count { it.status == "PRESENT" }
                            val attRate = if (totalAtt > 0) ((present.toFloat() / totalAtt) * 100).toInt() else 100

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Slate100),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Overall Attendance", fontWeight = FontWeight.SemiBold)
                                    Text("$attRate%", fontWeight = FontWeight.Bold, color = Emerald600, fontSize = 18.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            studentAttendance.forEach { att ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = att.date, fontWeight = FontWeight.Medium)
                                        if (att.note.isNotEmpty()) {
                                            Text(text = att.note, style = MaterialTheme.typography.labelSmall, color = Slate600)
                                        }
                                    }
                                    StatusBadge(status = att.status)
                                }
                                HorizontalDivider(color = Slate200)
                            }
                        }
                        2 -> {
                            // Fees
                            if (studentFees.isEmpty()) {
                                Text("No fee records found for this student.", color = Slate600)
                            } else {
                                studentFees.forEach { fee ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = Slate100)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(fee.monthYear, fontWeight = FontWeight.Bold)
                                                StatusBadge(status = fee.status)
                                            }
                                            Text("Paid: ₹${fee.amountPaid.toInt()} / ₹${fee.amountDue.toInt()}", style = MaterialTheme.typography.bodySmall)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            TextButton(
                                                onClick = { viewModel.viewReceipt(fee) },
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("View Official Receipt")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        3 -> {
                            // Results
                            if (studentResults.isEmpty()) {
                                Text("No examination records found.", color = Slate600)
                            } else {
                                studentResults.forEach { res ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = Slate100)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Marks: ${res.marksObtained.toInt()} / ${res.maxMarks.toInt()}", fontWeight = FontWeight.Bold)
                                                StatusBadge(status = "Grade: ${res.grade}")
                                            }
                                            Text("Percentage: ${res.percentage.toInt()}%", style = MaterialTheme.typography.bodySmall)
                                            if (res.feedback.isNotEmpty()) {
                                                Text("Feedback: ${res.feedback}", style = MaterialTheme.typography.labelSmall, color = Slate600)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileDetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Slate600)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = Slate900)
        HorizontalDivider(color = Slate100, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun AddStudentDialog(
    classes: List<ClassEntity>,
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, String, String, String, Long, String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var parentName by remember { mutableStateOf("") }
    var parentPhone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var selectedClassId by remember { mutableStateOf(classes.firstOrNull()?.id ?: 1L) }
    var batch by remember { mutableStateOf(classes.firstOrNull()?.batch ?: "Morning Batch A") }
    var monthlyFee by remember { mutableStateOf("${classes.firstOrNull()?.monthlyFee?.toInt() ?: 1500}") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("add_student_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Add New Student",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Enroll a student into a tuition class batch",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Full Name *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_student_name")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Student Phone Number *") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = parentName,
                    onValueChange = { parentName = it },
                    label = { Text("Parent / Guardian Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = parentPhone,
                    onValueChange = { parentPhone = it },
                    label = { Text("Parent Contact Number *") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Class selection
                Text("Select Class:", style = MaterialTheme.typography.labelSmall, color = Slate600)
                Spacer(modifier = Modifier.height(4.dp))
                classes.forEach { cls ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedClassId = cls.id
                                batch = cls.batch
                                monthlyFee = "${cls.monthlyFee.toInt()}"
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedClassId == cls.id,
                            onClick = {
                                selectedClassId = cls.id
                                batch = cls.batch
                                monthlyFee = "${cls.monthlyFee.toInt()}"
                            }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "${cls.name} (${cls.batch})", fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = monthlyFee,
                    onValueChange = { monthlyFee = it },
                    label = { Text("Monthly Tuition Fee (₹)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (name.isNotEmpty() && phone.isNotEmpty()) {
                                onAdd(
                                    name,
                                    email.ifEmpty { "${name.lowercase().replace(" ", "")}@student.com" },
                                    phone,
                                    parentName.ifEmpty { "Guardian of $name" },
                                    parentPhone.ifEmpty { phone },
                                    address.ifEmpty { "Local Area" },
                                    gender,
                                    selectedClassId,
                                    batch,
                                    monthlyFee.toDoubleOrNull() ?: 1500.0
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("confirm_add_student_button")
                    ) {
                        Text("Enroll Student")
                    }
                }
            }
        }
    }
}
