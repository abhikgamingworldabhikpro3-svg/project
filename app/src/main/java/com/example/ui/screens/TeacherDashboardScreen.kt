package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.SimpleBarChart
import com.example.ui.components.StatMetricCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.TeacherSection
import com.example.viewmodel.TuitionViewModel

@Composable
fun TeacherDashboardScreen(viewModel: TuitionViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val classes by viewModel.classes.collectAsStateWithLifecycle()
    val students by viewModel.students.collectAsStateWithLifecycle()
    val fees by viewModel.fees.collectAsStateWithLifecycle()
    val attendance by viewModel.attendance.collectAsStateWithLifecycle()
    val assignments by viewModel.assignments.collectAsStateWithLifecycle()
    val exams by viewModel.exams.collectAsStateWithLifecycle()
    val notices by viewModel.notices.collectAsStateWithLifecycle()

    // Calculated metrics
    val totalStudents = students.size
    val activeClasses = classes.filter { it.status == "Active" }.size

    val todayDate = viewModel.selectedAttendanceDate.collectAsStateWithLifecycle().value
    val todayAttendance = attendance.filter { it.date == todayDate }
    val presentCount = todayAttendance.count { it.status == "PRESENT" }
    val attendancePct = if (todayAttendance.isNotEmpty()) {
        ((presentCount.toFloat() / todayAttendance.size.toFloat()) * 100).toInt()
    } else 88

    val pendingFeesTotal = fees.filter { it.status != "Paid" }.sumOf { (it.amountDue - it.amountPaid).coerceAtLeast(0.0) }
    val totalCollected = fees.sumOf { it.amountPaid }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("teacher_dashboard_screen")
    ) {
        // Welcome Banner Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RoyalBlue900),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Good day, ${currentUser?.name ?: "Tutor"}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentUser?.instituteName?.ifEmpty { "Apex Scholars Coaching Academy" } ?: "Apex Scholars Coaching Academy",
                            style = MaterialTheme.typography.bodySmall,
                            color = RoyalBlue100
                        )
                        Text(
                            text = "Friday, 25 September 2026",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate400
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Action Chips Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.selectTeacherSection(TeacherSection.ATTENDANCE) },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Attendance", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.selectTeacherSection(TeacherSection.FEES) },
                        colors = ButtonDefaults.buttonColors(containerColor = Teal700),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Fees", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.selectTeacherSection(TeacherSection.NOTICES) },
                        colors = ButtonDefaults.buttonColors(containerColor = Slate700),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Notice", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 6 Stat Metrics (2x3 grid)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatMetricCard(
                title = "Total Students",
                value = "$totalStudents",
                subtitle = "Enrolled active scholars",
                icon = Icons.Default.Groups,
                iconTint = RoyalBlue600,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.selectTeacherSection(TeacherSection.STUDENTS) }
            )
            StatMetricCard(
                title = "Active Batches",
                value = "$activeClasses",
                subtitle = "Board & competitive",
                icon = Icons.Default.Class,
                iconTint = Teal600,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.selectTeacherSection(TeacherSection.CLASSES) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatMetricCard(
                title = "Today's Attendance",
                value = "$attendancePct%",
                subtitle = "$presentCount present recorded",
                icon = Icons.Default.CheckCircle,
                iconTint = Emerald600,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.selectTeacherSection(TeacherSection.ATTENDANCE) }
            )
            StatMetricCard(
                title = "Pending Fees",
                value = "₹${pendingFeesTotal.toInt()}",
                subtitle = "Dues to be collected",
                icon = Icons.Default.PendingActions,
                iconTint = Amber600,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.selectTeacherSection(TeacherSection.FEES) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatMetricCard(
                title = "Upcoming Exams",
                value = "${exams.size}",
                subtitle = "Term & speed quizzes",
                icon = Icons.Default.EmojiEvents,
                iconTint = Rose600,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.selectTeacherSection(TeacherSection.EXAMS) }
            )
            StatMetricCard(
                title = "Assignments",
                value = "${assignments.size}",
                subtitle = "Active problem sets",
                icon = Icons.Default.Assignment,
                iconTint = RoyalBlue700,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.selectTeacherSection(TeacherSection.ASSIGNMENTS) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Visual Attendance Trend Chart Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Weekly Attendance Trend",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Average attendance rate: 91.5%",
                            style = MaterialTheme.typography.labelSmall,
                            color = Emerald600
                        )
                    }
                    Surface(
                        color = Emerald100,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Last 6 Days",
                            color = Emerald600,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                SimpleBarChart(
                    data = listOf(
                        "Mon" to 92f,
                        "Tue" to 88f,
                        "Wed" to 95f,
                        "Thu" to 90f,
                        "Fri" to 86f,
                        "Sat" to 98f
                    ),
                    accentColor = RoyalBlue600
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fee Collection Progress Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "September Fee Collection",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Collected: ₹${totalCollected.toInt()}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Emerald600
                    )
                    Text(
                        text = "Target: ₹${(totalCollected + pendingFeesTotal).toInt()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate600
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                val collectionProgress = if (totalCollected + pendingFeesTotal > 0) {
                    (totalCollected / (totalCollected + pendingFeesTotal)).toFloat()
                } else 0.8f

                LinearProgressIndicator(
                    progress = { collectionProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = Emerald600,
                    trackColor = Slate200,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Today's Scheduled Classes
        Text(
            text = "Classes Scheduled for Today",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Slate900
        )
        Spacer(modifier = Modifier.height(8.dp))

        classes.take(2).forEach { cls ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable { viewModel.selectTeacherSection(TeacherSection.ATTENDANCE) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(RoyalBlue100),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = RoyalBlue700)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = cls.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(text = "${cls.schedule} • ${cls.room}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                        }
                    }

                    StatusBadge(status = "Active")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Latest Broadcast Notice preview
        if (notices.isNotEmpty()) {
            val topNotice = notices.first()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectTeacherSection(TeacherSection.NOTICES) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Amber100.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = Amber600)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = topNotice.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            StatusBadge(status = topNotice.priority)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = topNotice.message, style = MaterialTheme.typography.bodySmall, color = Slate700)
                    }
                }
            }
        }
    }
}
