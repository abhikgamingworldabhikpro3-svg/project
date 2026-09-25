package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.models.*
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.TuitionViewModel

@Composable
fun AssignmentsAndExamsScreen(
    viewModel: TuitionViewModel,
    initialTab: Int = 0
) {
    var selectedTab by remember { mutableStateOf(initialTab) }
    val tabs = listOf("Assignments", "Exams & Tests")

    val assignments by viewModel.assignments.collectAsStateWithLifecycle()
    val submissions by viewModel.submissions.collectAsStateWithLifecycle()
    val exams by viewModel.exams.collectAsStateWithLifecycle()
    val examResults by viewModel.examResults.collectAsStateWithLifecycle()
    val classes by viewModel.classes.collectAsStateWithLifecycle()
    val students by viewModel.students.collectAsStateWithLifecycle()

    var showCreateAssignmentDialog by remember { mutableStateOf(false) }
    var showCreateExamDialog by remember { mutableStateOf(false) }
    var viewingAssignmentSubmissions by remember { mutableStateOf<AssignmentEntity?>(null) }
    var gradingExam by remember { mutableStateOf<ExamEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showCreateAssignmentDialog = true else showCreateExamDialog = true
                },
                containerColor = RoyalBlue600,
                contentColor = Color.White,
                modifier = Modifier.testTag("assignments_exams_fab")
            ) {
                Icon(
                    imageVector = if (selectedTab == 0) Icons.Default.PostAdd else Icons.Default.Addchart,
                    contentDescription = "Create"
                )
            }
        },
        containerColor = Slate50
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Tab Header
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (selectedTab) {
                0 -> {
                    // Assignments Tab
                    if (assignments.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No assignments published yet.", color = Slate600)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(assignments, key = { it.id }) { assignment ->
                                val count = submissions.count { it.assignmentId == assignment.id }
                                val cls = classes.find { it.id == assignment.classId }

                                AssignmentCardItem(
                                    assignment = assignment,
                                    className = cls?.name ?: "Class",
                                    submissionCount = count,
                                    onViewSubmissions = { viewingAssignmentSubmissions = assignment }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Exams Tab
                    if (exams.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No exams scheduled yet.", color = Slate600)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(exams, key = { it.id }) { exam ->
                                val cls = classes.find { it.id == exam.classId }
                                val gradedCount = examResults.count { it.examId == exam.id }

                                ExamCardItem(
                                    exam = exam,
                                    className = cls?.name ?: "Class",
                                    gradedCount = gradedCount,
                                    onEnterMarks = { gradingExam = exam }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Create Assignment Dialog
    if (showCreateAssignmentDialog) {
        CreateAssignmentDialog(
            classes = classes,
            onDismiss = { showCreateAssignmentDialog = false },
            onCreate = { clsId, title, subj, desc, inst, due, marks, att ->
                viewModel.addAssignment(clsId, title, subj, desc, inst, due, marks, att)
                showCreateAssignmentDialog = false
            }
        )
    }

    // Create Exam Dialog
    if (showCreateExamDialog) {
        CreateExamDialog(
            classes = classes,
            onDismiss = { showCreateExamDialog = false },
            onCreate = { clsId, title, type, subj, date, marks, dur, inst, rank ->
                viewModel.addExam(clsId, title, type, subj, date, marks, dur, inst, rank)
                showCreateExamDialog = false
            }
        )
    }

    // View / Grade Assignment Submissions Modal
    viewingAssignmentSubmissions?.let { assignment ->
        AssignmentSubmissionsModal(
            assignment = assignment,
            submissions = submissions.filter { it.assignmentId == assignment.id },
            onDismiss = { viewingAssignmentSubmissions = null },
            onGrade = { sub, marks, fb ->
                viewModel.gradeSubmission(sub, marks, fb)
            }
        )
    }

    // Enter Exam Marks Modal
    gradingExam?.let { exam ->
        val examStudents = students.filter { it.classId == exam.classId }
        val currentResults = examResults.filter { it.examId == exam.id }

        EnterExamMarksModal(
            exam = exam,
            students = examStudents,
            results = currentResults,
            onDismiss = { gradingExam = null },
            onSaveMarks = { sId, sName, marks, feedback ->
                viewModel.enterExamMarks(
                    examId = exam.id,
                    studentId = sId,
                    studentName = sName,
                    marksObtained = marks,
                    maxMarks = exam.totalMarks.toDouble(),
                    feedback = feedback
                )
            }
        )
    }
}

@Composable
fun AssignmentCardItem(
    assignment: AssignmentEntity,
    className: String,
    submissionCount: Int,
    onViewSubmissions: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("assignment_card_${assignment.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = assignment.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(status = "Max: ${assignment.maxMarks}m")
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$className • ${assignment.subject}",
                style = MaterialTheme.typography.bodySmall,
                color = RoyalBlue700
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = assignment.description,
                style = MaterialTheme.typography.bodySmall,
                color = Slate700
            )

            if (assignment.attachmentName.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AttachFile, contentDescription = null, tint = Slate600, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = assignment.attachmentName, style = MaterialTheme.typography.labelSmall, color = RoyalBlue600)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Slate100)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Due: ${assignment.dueDate} • $submissionCount Submissions",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate600
                )
                Button(
                    onClick = onViewSubmissions,
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Grade Submissions", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ExamCardItem(
    exam: ExamEntity,
    className: String,
    gradedCount: Int,
    onEnterMarks: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("exam_card_${exam.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = exam.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(status = exam.examType)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$className • ${exam.subject} • ${exam.durationMinutes} mins",
                style = MaterialTheme.typography.bodySmall,
                color = RoyalBlue700
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Date: ${exam.date} • Total Marks: ${exam.totalMarks} • Rankings: ${if (exam.rankingEnabled) "Enabled" else "Hidden"}",
                style = MaterialTheme.typography.labelMedium,
                color = Slate600
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Slate100)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$gradedCount Students Evaluated",
                    style = MaterialTheme.typography.labelSmall,
                    color = Emerald600,
                    fontWeight = FontWeight.SemiBold
                )
                Button(
                    onClick = onEnterMarks,
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Enter Marks", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AssignmentSubmissionsModal(
    assignment: AssignmentEntity,
    submissions: List<AssignmentSubmissionEntity>,
    onDismiss: () -> Unit,
    onGrade: (AssignmentSubmissionEntity, Int, String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Submissions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(text = "${assignment.title} (Max: ${assignment.maxMarks}m)", style = MaterialTheme.typography.bodySmall, color = Slate600)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (submissions.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No submissions yet for this assignment.", color = Slate600)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(submissions) { sub ->
                            var marksInput by remember { mutableStateOf(sub.marksObtained?.toString() ?: "") }
                            var feedbackInput by remember { mutableStateOf(sub.feedback) }

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Slate100),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = sub.studentName, fontWeight = FontWeight.Bold)
                                        StatusBadge(status = sub.status)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Submitted: ${sub.submittedDate}", style = MaterialTheme.typography.labelSmall, color = Slate600)
                                    Text(text = "Answer: ${sub.answerText}", style = MaterialTheme.typography.bodySmall, color = Slate800)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = marksInput,
                                            onValueChange = { marksInput = it },
                                            label = { Text("Marks") },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.width(90.dp)
                                        )
                                        OutlinedTextField(
                                            value = feedbackInput,
                                            onValueChange = { feedbackInput = it },
                                            label = { Text("Feedback") },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Button(
                                            onClick = {
                                                val m = marksInput.toIntOrNull() ?: 0
                                                onGrade(sub, m, feedbackInput)
                                            },
                                            contentPadding = PaddingValues(horizontal = 8.dp)
                                        ) {
                                            Text("Save")
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
fun EnterExamMarksModal(
    exam: ExamEntity,
    students: List<StudentEntity>,
    results: List<ExamResultEntity>,
    onDismiss: () -> Unit,
    onSaveMarks: (Long, String, Double, String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Enter Exam Marks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(text = "${exam.title} • Total Marks: ${exam.totalMarks}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(students) { student ->
                        val existingResult = results.find { it.studentId == student.id }
                        var marksText by remember { mutableStateOf(existingResult?.marksObtained?.toInt()?.toString() ?: "") }
                        var feedbackText by remember { mutableStateOf(existingResult?.feedback ?: "") }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Slate100),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = student.name, fontWeight = FontWeight.Bold)
                                    if (existingResult != null) {
                                        Text(text = "Grade: ${existingResult.grade} (${existingResult.percentage.toInt()}%)", color = Emerald600, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = marksText,
                                        onValueChange = { marksText = it },
                                        label = { Text("Score") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.width(90.dp)
                                    )
                                    OutlinedTextField(
                                        value = feedbackText,
                                        onValueChange = { feedbackText = it },
                                        label = { Text("Feedback (optional)") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Button(
                                        onClick = {
                                            val m = marksText.toDoubleOrNull() ?: 0.0
                                            onSaveMarks(student.id, student.name, m, feedbackText)
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Text("Save")
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
fun CreateAssignmentDialog(
    classes: List<ClassEntity>,
    onDismiss: () -> Unit,
    onCreate: (Long, String, String, String, String, String, Int, String) -> Unit
) {
    var selectedClassId by remember { mutableStateOf(classes.firstOrNull()?.id ?: 1L) }
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf(classes.firstOrNull()?.subject ?: "Mathematics") }
    var description by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("Show all steps clearly.") }
    var dueDate by remember { mutableStateOf("2026-10-05") }
    var maxMarks by remember { mutableStateOf("25") }
    var attachment by remember { mutableStateOf("Worksheet.pdf") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())
            ) {
                Text(text = "New Assignment", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Assignment Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Problems to solve") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Due Date") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = maxMarks,
                        onValueChange = { maxMarks = it },
                        label = { Text("Max Marks") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(
                        onClick = {
                            if (title.isNotEmpty()) {
                                onCreate(selectedClassId, title, subject, description, instructions, dueDate, maxMarks.toIntOrNull() ?: 25, attachment)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        modifier = Modifier.weight(1f)
                    ) { Text("Publish") }
                }
            }
        }
    }
}

@Composable
fun CreateExamDialog(
    classes: List<ClassEntity>,
    onDismiss: () -> Unit,
    onCreate: (Long, String, String, String, String, Int, Int, String, Boolean) -> Unit
) {
    var selectedClassId by remember { mutableStateOf(classes.firstOrNull()?.id ?: 1L) }
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Term Exam") }
    var subject by remember { mutableStateOf(classes.firstOrNull()?.subject ?: "Mathematics") }
    var date by remember { mutableStateOf("2026-10-10") }
    var totalMarks by remember { mutableStateOf("50") }
    var duration by remember { mutableStateOf("60") }
    var instructions by remember { mutableStateOf("Bring admit card and geometry box.") }
    var ranking by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())
            ) {
                Text(text = "Schedule Exam / Test", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Exam Name (e.g. Unit Test #2) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Exam Type (Term Exam, Mock Test, Quiz)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Exam Date") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = totalMarks,
                        onValueChange = { totalMarks = it },
                        label = { Text("Total Marks") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = ranking, onCheckedChange = { ranking = it })
                    Text("Display Student Rankings in Scorecard", fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(
                        onClick = {
                            if (title.isNotEmpty()) {
                                onCreate(selectedClassId, title, type, subject, date, totalMarks.toIntOrNull() ?: 50, duration.toIntOrNull() ?: 60, instructions, ranking)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        modifier = Modifier.weight(1f)
                    ) { Text("Schedule") }
                }
            }
        }
    }
}
