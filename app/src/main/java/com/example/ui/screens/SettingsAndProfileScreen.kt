package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.InitialsAvatar
import com.example.ui.theme.*
import com.example.viewmodel.TuitionViewModel

@Composable
fun SettingsAndProfileScreen(viewModel: TuitionViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf(currentUser?.name ?: "Prof. Rajesh Sharma") }
    var institute by remember { mutableStateOf(currentUser?.instituteName ?: "Apex Scholars Coaching Academy") }
    var email by remember { mutableStateOf(currentUser?.email ?: "teacher@tuitionhub.com") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "+91 98765 43210") }
    var subjects by remember { mutableStateOf("Mathematics, Physics, Defense Math") }
    var bio by remember { mutableStateOf("Mentoring Class 10, 12, NEET and NDA aspirants for over 12 years with proven top rank results.") }

    var feeReminderNotifications by remember { mutableStateOf(true) }
    var attendanceAlerts by remember { mutableStateOf(true) }
    var examScoreAlerts by remember { mutableStateOf(true) }

    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("settings_screen")
    ) {
        // Teacher Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    InitialsAvatar(name = name, modifier = Modifier.size(54.dp), backgroundColor = RoyalBlue700)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(text = name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(text = institute, style = MaterialTheme.typography.bodySmall, color = RoyalBlue700)
                        Text(text = "Role: ${currentUser?.role ?: "TEACHER"}", style = MaterialTheme.typography.labelSmall, color = Slate600)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = institute,
                    onValueChange = { institute = it },
                    label = { Text("Tuition / Coaching Centre Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Official Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = subjects,
                    onValueChange = { subjects = it },
                    label = { Text("Subjects Taught") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Tuition Bio / Experience") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        // Profile updated message
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Save Profile")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Notification Preferences
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Notification Preferences", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Tuition Fee Reminders", fontWeight = FontWeight.Medium)
                        Text("Send SMS/app alerts before fee due date", style = MaterialTheme.typography.labelSmall, color = Slate600)
                    }
                    Switch(checked = feeReminderNotifications, onCheckedChange = { feeReminderNotifications = it })
                }

                HorizontalDivider(color = Slate100, modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Attendance Absent Alerts", fontWeight = FontWeight.Medium)
                        Text("Notify parents immediately when student is absent", style = MaterialTheme.typography.labelSmall, color = Slate600)
                    }
                    Switch(checked = attendanceAlerts, onCheckedChange = { attendanceAlerts = it })
                }

                HorizontalDivider(color = Slate100, modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Exam Results Release", fontWeight = FontWeight.Medium)
                        Text("Broadcast scorecard notifications to batch", style = MaterialTheme.typography.labelSmall, color = Slate600)
                    }
                    Switch(checked = examScoreAlerts, onCheckedChange = { examScoreAlerts = it })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // System & Data Reset
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("System & Demo Tools", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "You can reset the local database to the pristine default demo dataset at any time.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { showResetDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Amber600),
                    modifier = Modifier.fillMaxWidth().testTag("reset_demo_data_button")
                ) {
                    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset to Fresh Demo Data")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.logout() },
                    colors = ButtonDefaults.buttonColors(containerColor = Rose600),
                    modifier = Modifier.fillMaxWidth().testTag("settings_logout_button")
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Logout from TuitionHub")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "TuitionHub v2.0 • Offline-Ready Room Database • Material 3",
            style = MaterialTheme.typography.labelSmall,
            color = Slate400,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Demo Data?") },
            text = { Text("This will reload all default tuition batches, students, fee records, attendance, assignments, and exams.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetDemoData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Rose600)
                ) {
                    Text("Reset Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }
}
