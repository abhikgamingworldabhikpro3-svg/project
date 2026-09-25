package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.ClassEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.viewmodel.TuitionViewModel

@Composable
fun ClassManagementScreen(viewModel: TuitionViewModel) {
    val context = LocalContext.current
    val classes by viewModel.classes.collectAsStateWithLifecycle()
    val students by viewModel.students.collectAsStateWithLifecycle()

    var showAddClassDialog by remember { mutableStateOf(false) }
    var viewingJoinCodeClass by remember { mutableStateOf<ClassEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddClassDialog = true },
                containerColor = RoyalBlue600,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_class_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Class")
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tuition Batches & Classes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = "Manage schedules, rooms, fees, and student join codes",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (classes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tuition classes created yet. Tap + to add one.", color = Slate600)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(classes, key = { it.id }) { cls ->
                        val count = students.count { it.classId == cls.id }
                        ClassCardItem(
                            tuitionClass = cls,
                            studentCount = count,
                            onShowJoinCode = { viewingJoinCodeClass = cls },
                            onDelete = { viewModel.deleteClass(cls.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddClassDialog) {
        AddClassDialog(
            onDismiss = { showAddClassDialog = false },
            onAdd = { name, subject, batch, sched, room, fee, code ->
                viewModel.addClass(name, subject, batch, sched, room, fee, code)
                showAddClassDialog = false
            }
        )
    }

    viewingJoinCodeClass?.let { cls ->
        ClassJoinCodeDialog(
            tuitionClass = cls,
            onDismiss = { viewingJoinCodeClass = null },
            onShare = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Join my tuition class '${cls.name}' on TuitionHub! Use Class Join Code: ${cls.joinCode}"
                    )
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share Class Code"))
            }
        )
    }
}

@Composable
fun ClassCardItem(
    tuitionClass: ClassEntity,
    studentCount: Int,
    onShowJoinCode: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("class_card_${tuitionClass.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tuitionClass.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = "${tuitionClass.subject} • ${tuitionClass.batch}",
                        style = MaterialTheme.typography.bodySmall,
                        color = RoyalBlue700,
                        fontWeight = FontWeight.Medium
                    )
                }

                StatusBadge(status = tuitionClass.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = Slate600, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = tuitionClass.schedule, style = MaterialTheme.typography.bodySmall, color = Slate700)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.MeetingRoom, contentDescription = null, tint = Slate600, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Room: ${tuitionClass.room} • Instructor: ${tuitionClass.teacherName}", style = MaterialTheme.typography.bodySmall, color = Slate700)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Slate100)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$studentCount Students Enrolled",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate900
                    )
                    Text(
                        text = "₹${tuitionClass.monthlyFee.toInt()} / month",
                        style = MaterialTheme.typography.labelSmall,
                        color = Emerald600,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onShowJoinCode,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("btn_join_code_${tuitionClass.id}")
                    ) {
                        Icon(imageVector = Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Join Code: ${tuitionClass.joinCode}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Rose600, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ClassJoinCodeDialog(
    tuitionClass: ClassEntity,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .testTag("class_join_code_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Class Join Code",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = tuitionClass.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Simulated QR Box
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate100)
                        .border(1.dp, Slate200, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "QR Code",
                            tint = Slate900,
                            modifier = Modifier.size(80.dp)
                        )
                        Text(
                            text = "SCAN TO JOIN",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Slate600,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Big Join Code Box
                Surface(
                    color = RoyalBlue100,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = tuitionClass.joinCode,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = RoyalBlue900,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 4.sp,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Students can enter this code in their TuitionHub app to join '${tuitionClass.name}' instantly.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate600,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Close")
                    }
                    Button(
                        onClick = onShare,
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Code")
                    }
                }
            }
        }
    }
}

@Composable
fun AddClassDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, String, Double, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var batch by remember { mutableStateOf("") }
    var schedule by remember { mutableStateOf("Mon, Wed, Fri • 04:00 PM - 05:30 PM") }
    var room by remember { mutableStateOf("Hall 101") }
    var fee by remember { mutableStateOf("1500") }
    var code by remember { mutableStateOf("BATCH${(10..99).random()}") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("add_class_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Create New Tuition Batch",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Set up a subject class, timings, and join code",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (code.startsWith("BATCH") && it.length >= 3) {
                            code = it.take(4).uppercase() + (10..99).random()
                        }
                    },
                    label = { Text("Class Name (e.g. Class 11 Chemistry) *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_class_name")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject (e.g. Chemistry, Biology) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = batch,
                    onValueChange = { batch = it },
                    label = { Text("Batch Name (e.g. Target 2027 Morning) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = schedule,
                    onValueChange = { schedule = it },
                    label = { Text("Schedule / Days & Timings") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Room / Location (e.g. Room 204)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = fee,
                    onValueChange = { fee = it },
                    label = { Text("Monthly Fee (₹) *") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.uppercase() },
                    label = { Text("Student Join Code *") },
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
                            if (name.isNotEmpty() && subject.isNotEmpty()) {
                                onAdd(
                                    name,
                                    subject,
                                    batch.ifEmpty { "General Batch" },
                                    schedule,
                                    room,
                                    fee.toDoubleOrNull() ?: 1500.0,
                                    code
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("confirm_create_class_button")
                    ) {
                        Text("Create Batch")
                    }
                }
            }
        }
    }
}
