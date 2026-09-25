package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.AttendanceEntity
import com.example.data.models.StudentEntity
import com.example.ui.components.InitialsAvatar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.TuitionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(viewModel: TuitionViewModel) {
    val classes by viewModel.classes.collectAsStateWithLifecycle()
    val students by viewModel.students.collectAsStateWithLifecycle()
    val allAttendance by viewModel.attendance.collectAsStateWithLifecycle()

    var selectedClassId by remember { mutableStateOf(classes.firstOrNull()?.id ?: 1L) }
    var selectedDate by remember { mutableStateOf("2026-09-25") }
    var classMenuExpanded by remember { mutableStateOf(false) }

    val currentClass = classes.find { it.id == selectedClassId }
    val classStudents = students.filter { it.classId == selectedClassId }
    val dayAttendance = allAttendance.filter { it.date == selectedDate && it.classId == selectedClassId }

    // Live calculations
    val presentCount = dayAttendance.count { it.status == "PRESENT" }
    val absentCount = dayAttendance.count { it.status == "ABSENT" }
    val lateCount = dayAttendance.count { it.status == "LATE" }
    val totalMarked = dayAttendance.size
    val attendancePct = if (classStudents.isNotEmpty()) {
        ((presentCount.toFloat() / classStudents.size.toFloat()) * 100).toInt()
    } else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(16.dp)
            .testTag("attendance_screen")
    ) {
        // Top Selection Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Class Dropdown Selector
                Text(text = "Tuition Batch", style = MaterialTheme.typography.labelSmall, color = Slate600)
                ExposedDropdownMenuBox(
                    expanded = classMenuExpanded,
                    onExpandedChange = { classMenuExpanded = !classMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = "${currentClass?.name ?: "Select Class"} (${currentClass?.batch ?: ""})",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classMenuExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = classMenuExpanded,
                        onDismissRequest = { classMenuExpanded = false }
                    ) {
                        classes.forEach { cls ->
                            DropdownMenuItem(
                                text = { Text("${cls.name} • ${cls.batch}") },
                                onClick = {
                                    selectedClassId = cls.id
                                    classMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Date Picker row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Attendance Date", style = MaterialTheme.typography.labelSmall, color = Slate600)
                        Text(text = selectedDate, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(onClick = {
                            selectedDate = if (selectedDate == "2026-09-25") "2026-09-24" else "2026-09-25"
                        }) {
                            Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Prev Date")
                        }
                        IconButton(onClick = {
                            selectedDate = if (selectedDate == "2026-09-24") "2026-09-25" else "2026-09-26"
                        }) {
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next Date")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Stats & Bulk Actions Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RoyalBlue100.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Attendance: $attendancePct%",
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue900,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "$presentCount Present • $absentCount Absent • $lateCount Late",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate700
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.markBulkAttendance(selectedClassId, selectedDate, "PRESENT") },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("mark_all_present_btn")
                        ) {
                            Text("All Present", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.markBulkAttendance(selectedClassId, selectedDate, "ABSENT") },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("All Absent", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Student Attendance List
        Text(
            text = "Class Roll Call (${classStudents.size} Students)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Slate900
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (classStudents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No students enrolled in this class batch yet.", color = Slate600)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(classStudents, key = { it.id }) { student ->
                    val record = dayAttendance.find { it.studentId == student.id }
                    val currentStatus = record?.status ?: "PENDING"

                    StudentAttendanceRow(
                        student = student,
                        currentStatus = currentStatus,
                        onStatusChange = { newStatus ->
                            viewModel.markAttendance(
                                studentId = student.id,
                                classId = selectedClassId,
                                date = selectedDate,
                                status = newStatus
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StudentAttendanceRow(
    student: StudentEntity,
    currentStatus: String,
    onStatusChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("attendance_row_${student.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                InitialsAvatar(name = student.name, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = student.studentCode,
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate600
                    )
                }
            }

            // P, A, L Status Selector
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                AttendanceStatusButton(
                    label = "P",
                    isSelected = currentStatus == "PRESENT",
                    activeBg = Emerald600,
                    onClick = { onStatusChange("PRESENT") }
                )
                AttendanceStatusButton(
                    label = "A",
                    isSelected = currentStatus == "ABSENT",
                    activeBg = Rose600,
                    onClick = { onStatusChange("ABSENT") }
                )
                AttendanceStatusButton(
                    label = "L",
                    isSelected = currentStatus == "LATE",
                    activeBg = Amber600,
                    onClick = { onStatusChange("LATE") }
                )
            }
        }
    }
}

@Composable
private fun AttendanceStatusButton(
    label: String,
    isSelected: Boolean,
    activeBg: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(36.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) activeBg else Slate100
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isSelected) Color.White else Slate700
            )
        }
    }
}
