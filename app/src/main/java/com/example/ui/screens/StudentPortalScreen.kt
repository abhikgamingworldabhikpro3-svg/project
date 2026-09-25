package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.AssignmentEntity
import com.example.data.models.AssignmentSubmissionEntity
import com.example.data.models.FeeRecordEntity
import com.example.ui.components.InitialsAvatar
import com.example.ui.components.ReceiptDialog
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.StudentSection
import com.example.viewmodel.TuitionViewModel

@Composable
fun StudentPortalScreen(viewModel: TuitionViewModel) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val students by viewModel.students.collectAsStateWithLifecycle()
    val classes by viewModel.classes.collectAsStateWithLifecycle()
    val attendance by viewModel.attendance.collectAsStateWithLifecycle()
    val fees by viewModel.fees.collectAsStateWithLifecycle()
    val assignments by viewModel.assignments.collectAsStateWithLifecycle()
    val submissions by viewModel.submissions.collectAsStateWithLifecycle()
    val exams by viewModel.exams.collectAsStateWithLifecycle()
    val examResults by viewModel.examResults.collectAsStateWithLifecycle()
    val materials by viewModel.materials.collectAsStateWithLifecycle()
    val notices by viewModel.notices.collectAsStateWithLifecycle()
    val timetable by viewModel.timetable.collectAsStateWithLifecycle()
    val selectedReceipt by viewModel.selectedReceipt.collectAsStateWithLifecycle()

    val currentStudent = students.find { it.id == (currentUser?.studentIdRef ?: 1L) } ?: students.firstOrNull()
    val enrolledClass = classes.find { it.id == (currentStudent?.classId ?: 1L) }

    var activeTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Attendance", "Fees", "Assignments", "Results", "Materials", "Notices")

    var showJoinCodeDialog by remember { mutableStateOf(false) }
    var submittingAssignment by remember { mutableStateOf<AssignmentEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .testTag("student_portal_screen")
    ) {
        // Student Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RoyalBlue900),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        InitialsAvatar(
                            name = currentStudent?.name ?: "Student",
                            modifier = Modifier.size(46.dp),
                            backgroundColor = Color.White.copy(alpha = 0.2f),
                            textColor = Color.White
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = currentStudent?.name ?: "Aarav Sharma",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Roll: ${currentStudent?.studentCode ?: "STU-2026-001"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = RoyalBlue100
                            )
                        }
                    }

                    Button(
                        onClick = { showJoinCodeDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("student_join_class_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Join Class", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Enrolled Class", style = MaterialTheme.typography.labelSmall, color = RoyalBlue100)
                        Text(
                            text = enrolledClass?.name ?: "Class 10 Mathematics",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Batch Timings", style = MaterialTheme.typography.labelSmall, color = RoyalBlue100)
                        Text(
                            text = enrolledClass?.schedule ?: "Mon, Wed, Fri 4 PM",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Navigation Tabs (Horizontal Scroll)
        ScrollableTabRow(
            selectedTabIndex = activeTab,
            containerColor = Color.White,
            edgePadding = 16.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = activeTab == index,
                    onClick = { activeTab = index },
                    text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Content Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            when (activeTab) {
                0 -> {
                    // Overview
                    val studentAttendance = attendance.filter { it.studentId == (currentStudent?.id ?: 1L) }
                    val totalAtt = studentAttendance.size
                    val presentAtt = studentAttendance.count { it.status == "PRESENT" }
                    val attPct = if (totalAtt > 0) ((presentAtt.toFloat() / totalAtt) * 100).toInt() else 95

                    val studentFees = fees.filter { it.studentId == (currentStudent?.id ?: 1L) }
                    val feeStatus = studentFees.firstOrNull()?.status ?: "Paid"

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Attendance", style = MaterialTheme.typography.labelSmall, color = Slate600)
                                    Text("$attPct%", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Emerald600)
                                    Text("$presentAtt of $totalAtt sessions", style = MaterialTheme.typography.labelSmall, color = Slate600)
                                }
                            }
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Fee Status", style = MaterialTheme.typography.labelSmall, color = Slate600)
                                    Text(feeStatus, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = if (feeStatus == "Paid") Emerald600 else Amber600)
                                    Text("September 2026", style = MaterialTheme.typography.labelSmall, color = Slate600)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Upcoming Class Schedule
                        Text("My Class Schedule", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(text = enrolledClass?.name ?: "Class 10 Mathematics", fontWeight = FontWeight.Bold)
                                Text(text = "${enrolledClass?.schedule} • ${enrolledClass?.room}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Instructor: ${enrolledClass?.teacherName}", style = MaterialTheme.typography.labelSmall, color = RoyalBlue700)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Latest Notice preview
                        notices.firstOrNull()?.let { topNotice ->
                            Text("Latest Tuition Notice", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Amber100.copy(alpha = 0.4f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = topNotice.title, fontWeight = FontWeight.Bold)
                                        StatusBadge(status = topNotice.priority)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = topNotice.message, style = MaterialTheme.typography.bodySmall, color = Slate700)
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Attendance
                    val myAttendance = attendance.filter { it.studentId == (currentStudent?.id ?: 1L) }
                    if (myAttendance.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No attendance records logged yet.", color = Slate600)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(myAttendance) { att ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = att.date, fontWeight = FontWeight.Bold)
                                            if (att.note.isNotEmpty()) {
                                                Text(text = att.note, style = MaterialTheme.typography.labelSmall, color = Slate600)
                                            }
                                        }
                                        StatusBadge(status = att.status)
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Fees
                    val myFees = fees.filter { it.studentId == (currentStudent?.id ?: 1L) }
                    if (myFees.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No fee history found.", color = Slate600)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(myFees) { fee ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = fee.monthYear, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                            StatusBadge(status = fee.status)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "Amount: ₹${fee.amountPaid.toInt()} / ₹${fee.amountDue.toInt()}", style = MaterialTheme.typography.bodyMedium)
                                        Text(text = "Receipt No: ${fee.receiptNo} • Mode: ${fee.paymentMethod}", style = MaterialTheme.typography.labelSmall, color = Slate600)

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = { viewModel.viewReceipt(fee) },
                                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue100),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = RoyalBlue900, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("View / Download Receipt", color = RoyalBlue900, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Assignments
                    val classAssignments = assignments.filter { it.classId == (currentStudent?.classId ?: 1L) }
                    if (classAssignments.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No assignments active for your class.", color = Slate600)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(classAssignments) { asgn ->
                                val mySub = submissions.find { it.assignmentId == asgn.id && it.studentId == (currentStudent?.id ?: 1L) }

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = asgn.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                                            StatusBadge(status = mySub?.status ?: "Pending")
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = asgn.description, style = MaterialTheme.typography.bodySmall, color = Slate700)
                                        Text(text = "Due: ${asgn.dueDate} • Max Marks: ${asgn.maxMarks}", style = MaterialTheme.typography.labelSmall, color = Slate600)

                                        if (mySub != null && mySub.marksObtained != null) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Surface(color = Emerald100, shape = RoundedCornerShape(8.dp)) {
                                                Text(
                                                    text = "Marks Scored: ${mySub.marksObtained}/${asgn.maxMarks} • Teacher: ${mySub.feedback}",
                                                    color = Emerald600,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        if (mySub == null) {
                                            Button(
                                                onClick = { submittingAssignment = asgn },
                                                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
                                            ) {
                                                Text("Submit Assignment")
                                            }
                                        } else {
                                            OutlinedButton(
                                                onClick = { submittingAssignment = asgn }
                                            ) {
                                                Text("Resubmit / Edit")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                4 -> {
                    // Exam Results
                    val myResults = examResults.filter { it.studentId == (currentStudent?.id ?: 1L) }
                    if (myResults.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No examination scorecards published yet.", color = Slate600)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(myResults) { res ->
                                val exam = exams.find { it.id == res.examId }

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = exam?.title ?: "Examination", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                            StatusBadge(status = "Grade: ${res.grade}")
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Score: ${res.marksObtained.toInt()} / ${res.maxMarks.toInt()} (${res.percentage.toInt()}%)",
                                            fontWeight = FontWeight.ExtraBold,
                                            color = RoyalBlue700,
                                            fontSize = 16.sp
                                        )
                                        if (res.rank != null) {
                                            Text(text = "Rank in Batch: #${res.rank}", fontWeight = FontWeight.Bold, color = Amber600, fontSize = 12.sp)
                                        }
                                        if (res.feedback.isNotEmpty()) {
                                            Text(text = "Teacher Feedback: ${res.feedback}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                5 -> {
                    // Study Materials
                    val classMaterials = materials.filter { it.classId == (currentStudent?.classId ?: 1L) }
                    if (classMaterials.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No study materials for your class yet.", color = Slate600)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(classMaterials) { mat ->
                                MaterialCardItem(
                                    item = mat,
                                    className = enrolledClass?.name ?: "Class",
                                    onOpen = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mat.urlOrPath.ifEmpty { "https://google.com" }))
                                        try { context.startActivity(intent) } catch (e: Exception) {}
                                    }
                                )
                            }
                        }
                    }
                }
                6 -> {
                    // Notices
                    if (notices.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No announcements.", color = Slate600)
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(notices) { notice ->
                                NoticeCardItem(notice = notice, targetClassName = enrolledClass?.name ?: "All Classes")
                            }
                        }
                    }
                }
            }
        }
    }

    // Join Class Dialog
    if (showJoinCodeDialog) {
        StudentJoinClassDialog(
            onDismiss = { showJoinCodeDialog = false },
            onJoin = { code ->
                viewModel.studentJoinClassByCode(code)
                showJoinCodeDialog = false
            }
        )
    }

    // Submit Assignment Dialog
    submittingAssignment?.let { asgn ->
        StudentSubmitAssignmentDialog(
            assignment = asgn,
            onDismiss = { submittingAssignment = null },
            onSubmit = { text ->
                viewModel.submitStudentAssignment(
                    assignmentId = asgn.id,
                    studentId = currentStudent?.id ?: 1L,
                    studentName = currentStudent?.name ?: "Student",
                    answer = text
                )
                submittingAssignment = null
            }
        )
    }

    // Receipt Modal
    selectedReceipt?.let { fee ->
        ReceiptDialog(
            fee = fee,
            student = currentStudent,
            tuitionClass = enrolledClass,
            onDismiss = { viewModel.viewReceipt(null) }
        )
    }
}

@Composable
fun StudentJoinClassDialog(
    onDismiss: () -> Unit,
    onJoin: (String) -> Unit
) {
    var codeInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, tint = RoyalBlue600, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text("Join Class Batch", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "Enter the 6-character Class Join Code given by your tuition teacher (e.g. MATH10, PHY12, NEETBIO)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate600,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = codeInput,
                    onValueChange = { codeInput = it.uppercase() },
                    placeholder = { Text("e.g. MATH10") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("join_class_code_input")
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(
                        onClick = { if (codeInput.isNotEmpty()) onJoin(codeInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        modifier = Modifier.weight(1f).testTag("submit_join_code_button")
                    ) { Text("Join Batch") }
                }
            }
        }
    }
}

@Composable
fun StudentSubmitAssignmentDialog(
    assignment: AssignmentEntity,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var answerText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Submit Solution", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = assignment.title, style = MaterialTheme.typography.bodySmall, color = RoyalBlue700)

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Instructions: ${assignment.instructions}", style = MaterialTheme.typography.bodySmall, color = Slate700)

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = answerText,
                    onValueChange = { answerText = it },
                    label = { Text("Your Answers / Steps / Drive Link") },
                    minLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(
                        onClick = { if (answerText.isNotEmpty()) onSubmit(answerText) },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        modifier = Modifier.weight(1f)
                    ) { Text("Submit") }
                }
            }
        }
    }
}
