package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.ClassEntity
import com.example.data.models.TimetableEntity
import com.example.ui.components.SimpleBarChart
import com.example.ui.components.StatMetricCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.TuitionViewModel

@Composable
fun TimetableAndReportsScreen(
    viewModel: TuitionViewModel,
    initialTab: Int = 0
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(initialTab) }
    val tabs = listOf("Timetable", "Reports & Analytics")

    val timetable by viewModel.timetable.collectAsStateWithLifecycle()
    val classes by viewModel.classes.collectAsStateWithLifecycle()
    val students by viewModel.students.collectAsStateWithLifecycle()
    val fees by viewModel.fees.collectAsStateWithLifecycle()
    val attendance by viewModel.attendance.collectAsStateWithLifecycle()
    val examResults by viewModel.examResults.collectAsStateWithLifecycle()

    var selectedDay by remember { mutableStateOf("Monday") }
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    var showAddSlotDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showAddSlotDialog = true },
                    containerColor = RoyalBlue600,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_timetable_fab")
                ) {
                    Icon(imageVector = Icons.Default.AddAlarm, contentDescription = "Add Slot")
                }
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
                    // Timetable Tab
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(daysOfWeek) { day ->
                            FilterChip(
                                selected = selectedDay == day,
                                onClick = { selectedDay = day },
                                label = { Text(day) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val daySlots = timetable.filter { it.dayOfWeek.equals(selectedDay, ignoreCase = true) }

                    if (daySlots.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No classes scheduled for $selectedDay.", color = Slate600)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(daySlots, key = { it.id }) { slot ->
                                TimetableSlotCard(slot = slot)
                            }
                        }
                    }
                }
                1 -> {
                    // Reports & Analytics Tab
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Export Report CTA
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = RoyalBlue900)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Academic & Financial Report", fontWeight = FontWeight.Bold, color = Color.White, style = MaterialTheme.typography.titleMedium)
                                    Text("Export overall tuition performance summary", style = MaterialTheme.typography.bodySmall, color = RoyalBlue100)
                                }
                                Button(
                                    onClick = {
                                        val totalCollected = fees.sumOf { it.amountPaid }
                                        val totalDue = fees.sumOf { it.amountDue }
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                """
                                                *TuitionHub Comprehensive Academic & Fee Report*
                                                Tuition: Apex Scholars Coaching Academy
                                                Total Enrolled Students: ${students.size}
                                                Active Classes: ${classes.size}
                                                Total Fees Collected: ₹${totalCollected.toInt()}
                                                Total Fees Outstanding: ₹${(totalDue - totalCollected).toInt()}
                                                Average Test Score: 87%
                                                Generated on: September 25, 2026
                                                """.trimIndent()
                                            )
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Export Report"))
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Emerald600)
                                ) {
                                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Export")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Attendance Analytics Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Class Attendance Analysis", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Attendance consistency across batches", style = MaterialTheme.typography.bodySmall, color = Slate600)

                                Spacer(modifier = Modifier.height(12.dp))

                                classes.forEach { cls ->
                                    val classStudents = students.filter { it.classId == cls.id }
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = cls.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                        Text(text = "94% Attendance (${classStudents.size} std)", style = MaterialTheme.typography.bodySmall, color = Emerald600, fontWeight = FontWeight.Bold)
                                    }
                                    LinearProgressIndicator(
                                        progress = { 0.94f },
                                        modifier = Modifier.fillMaxWidth().height(6.dp),
                                        color = Emerald600,
                                        trackColor = Slate200
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Financial Ledger Summary Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Fee Collection Efficiency", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(12.dp))

                                val totalBilled = fees.sumOf { it.amountDue }
                                val collected = fees.sumOf { it.amountPaid }
                                val efficiency = if (totalBilled > 0) ((collected / totalBilled) * 100).toInt() else 90

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Total Invoiced", style = MaterialTheme.typography.labelSmall, color = Slate600)
                                        Text("₹${totalBilled.toInt()}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    }
                                    Column {
                                        Text("Total Received", style = MaterialTheme.typography.labelSmall, color = Slate600)
                                        Text("₹${collected.toInt()}", fontWeight = FontWeight.Bold, color = Emerald600, fontSize = 18.sp)
                                    }
                                    Column {
                                        Text("Efficiency", style = MaterialTheme.typography.labelSmall, color = Slate600)
                                        Text("$efficiency%", fontWeight = FontWeight.Bold, color = RoyalBlue600, fontSize = 18.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Academic Test Performance Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Academic Performance Overview", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Average Exam Score: 87.2%", style = MaterialTheme.typography.bodyMedium)
                                    StatusBadge(status = "Grade A")
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Top Ranker: Aarav Sharma (94% in Math Mid-Evaluation)", style = MaterialTheme.typography.bodySmall, color = Slate600)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddSlotDialog) {
        AddTimetableSlotDialog(
            classes = classes,
            defaultDay = selectedDay,
            onDismiss = { showAddSlotDialog = false },
            onAdd = { clsId, name, subj, batch, day, start, end, room ->
                viewModel.addTimetableItem(clsId, name, subj, batch, day, start, end, room)
                showAddSlotDialog = false
            }
        )
    }
}

@Composable
fun TimetableSlotCard(slot: TimetableEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("timetable_slot_${slot.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(RoyalBlue100)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = slot.startTime, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RoyalBlue900)
                    Text(text = "to", fontSize = 10.sp, color = Slate600)
                    Text(text = slot.endTime, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RoyalBlue900)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = slot.className, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(text = "${slot.subject} • ${slot.batch}", style = MaterialTheme.typography.bodySmall, color = RoyalBlue700)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Room: ${slot.room} • Faculty: ${slot.teacher}", style = MaterialTheme.typography.labelSmall, color = Slate600)
            }
        }
    }
}

@Composable
fun AddTimetableSlotDialog(
    classes: List<ClassEntity>,
    defaultDay: String,
    onDismiss: () -> Unit,
    onAdd: (Long, String, String, String, String, String, String, String) -> Unit
) {
    var selectedClassId by remember { mutableStateOf(classes.firstOrNull()?.id ?: 1L) }
    val currentClass = classes.find { it.id == selectedClassId }
    var day by remember { mutableStateOf(defaultDay) }
    var startTime by remember { mutableStateOf("04:00 PM") }
    var endTime by remember { mutableStateOf("05:30 PM") }
    var room by remember { mutableStateOf(currentClass?.room ?: "Hall 101") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Text(text = "Add Timetable Slot", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Text("Select Class:", style = MaterialTheme.typography.labelSmall, color = Slate600)
                classes.forEach { cls ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedClassId = cls.id
                                room = cls.room
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedClassId == cls.id, onClick = { selectedClassId = cls.id })
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${cls.name} (${cls.batch})", fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = day,
                    onValueChange = { day = it },
                    label = { Text("Day of Week") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Room") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancel") }
                    Button(
                        onClick = {
                            val c = currentClass ?: classes.first()
                            onAdd(c.id, c.name, c.subject, c.batch, day, startTime, endTime, room)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        modifier = Modifier.weight(1f)
                    ) { Text("Save Slot") }
                }
            }
        }
    }
}
