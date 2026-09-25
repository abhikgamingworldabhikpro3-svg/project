package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.InitialsAvatar
import com.example.ui.theme.*
import com.example.viewmodel.AppDestination
import com.example.viewmodel.TeacherSection
import com.example.viewmodel.TuitionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherPortalScreen(viewModel: TuitionViewModel) {
    val currentSection by viewModel.teacherSection.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val notices by viewModel.notices.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var showNoticesSheet by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(300.dp)
            ) {
                // Drawer Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RoyalBlue900)
                        .padding(20.dp)
                ) {
                    InitialsAvatar(
                        name = currentUser?.name ?: "Prof. Rajesh Sharma",
                        modifier = Modifier.size(50.dp),
                        backgroundColor = RoyalBlue600
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = currentUser?.name ?: "Prof. Rajesh Sharma",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = currentUser?.instituteName?.ifEmpty { "Apex Scholars Coaching Academy" } ?: "Apex Scholars Coaching Academy",
                        color = RoyalBlue100,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Drawer Navigation Items
                DrawerItem(
                    icon = Icons.Default.Dashboard,
                    label = "Dashboard",
                    selected = currentSection == TeacherSection.DASHBOARD,
                    onClick = {
                        viewModel.selectTeacherSection(TeacherSection.DASHBOARD)
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                DrawerItem(
                    icon = Icons.Default.People,
                    label = "Students",
                    selected = currentSection == TeacherSection.STUDENTS,
                    onClick = {
                        viewModel.selectTeacherSection(TeacherSection.STUDENTS)
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                DrawerItem(
                    icon = Icons.Default.Class,
                    label = "Classes & Batches",
                    selected = currentSection == TeacherSection.CLASSES,
                    onClick = {
                        viewModel.selectTeacherSection(TeacherSection.CLASSES)
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                DrawerItem(
                    icon = Icons.Default.FactCheck,
                    label = "Attendance",
                    selected = currentSection == TeacherSection.ATTENDANCE,
                    onClick = {
                        viewModel.selectTeacherSection(TeacherSection.ATTENDANCE)
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                DrawerItem(
                    icon = Icons.Default.ReceiptLong,
                    label = "Fees & Receipts",
                    selected = currentSection == TeacherSection.FEES,
                    onClick = {
                        viewModel.selectTeacherSection(TeacherSection.FEES)
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                DrawerItem(
                    icon = Icons.Default.Assignment,
                    label = "Assignments",
                    selected = currentSection == TeacherSection.ASSIGNMENTS,
                    onClick = {
                        viewModel.selectTeacherSection(TeacherSection.ASSIGNMENTS)
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                DrawerItem(
                    icon = Icons.Default.EmojiEvents,
                    label = "Exams & Results",
                    selected = currentSection == TeacherSection.EXAMS,
                    onClick = {
                        viewModel.selectTeacherSection(TeacherSection.EXAMS)
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                DrawerItem(
                    icon = Icons.Default.MenuBook,
                    label = "Study Materials",
                    selected = currentSection == TeacherSection.MATERIALS,
                    onClick = {
                        viewModel.selectTeacherSection(TeacherSection.MATERIALS)
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                DrawerItem(
                    icon = Icons.Default.Campaign,
                    label = "Notices & Broadcasts",
                    selected = currentSection == TeacherSection.NOTICES,
                    onClick = {
                        viewModel.selectTeacherSection(TeacherSection.NOTICES)
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                DrawerItem(
                    icon = Icons.Default.CalendarMonth,
                    label = "Timetable & Schedule",
                    selected = currentSection == TeacherSection.TIMETABLE,
                    onClick = {
                        viewModel.selectTeacherSection(TeacherSection.TIMETABLE)
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                DrawerItem(
                    icon = Icons.Default.BarChart,
                    label = "Reports & Analytics",
                    selected = currentSection == TeacherSection.REPORTS,
                    onClick = {
                        viewModel.selectTeacherSection(TeacherSection.REPORTS)
                        coroutineScope.launch { drawerState.close() }
                    }
                )

                HorizontalDivider(color = Slate200, modifier = Modifier.padding(vertical = 8.dp))

                DrawerItem(
                    icon = Icons.Default.School,
                    label = "Switch to Student Portal",
                    selected = false,
                    onClick = {
                        viewModel.quickLoginAsStudent()
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                DrawerItem(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    selected = currentSection == TeacherSection.SETTINGS,
                    onClick = {
                        viewModel.selectTeacherSection(TeacherSection.SETTINGS)
                        coroutineScope.launch { drawerState.close() }
                    }
                )
                DrawerItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    label = "Logout",
                    selected = false,
                    onClick = {
                        viewModel.logout()
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = when (currentSection) {
                                    TeacherSection.DASHBOARD -> "Tuition Dashboard"
                                    TeacherSection.STUDENTS -> "Students Directory"
                                    TeacherSection.CLASSES -> "Tuition Batches"
                                    TeacherSection.ATTENDANCE -> "Daily Attendance"
                                    TeacherSection.FEES -> "Fee Management"
                                    TeacherSection.ASSIGNMENTS -> "Assignments"
                                    TeacherSection.EXAMS -> "Exams & Results"
                                    TeacherSection.MATERIALS -> "Study Materials"
                                    TeacherSection.NOTICES -> "Notices & Broadcasts"
                                    TeacherSection.TIMETABLE -> "Weekly Timetable"
                                    TeacherSection.REPORTS -> "Reports & Analytics"
                                    TeacherSection.SETTINGS, TeacherSection.PROFILE -> "Settings & Profile"
                                },
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Apex Scholars Academy",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate600
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { coroutineScope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("teacher_menu_button")
                        ) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        // Notice Bell
                        IconButton(
                            onClick = { viewModel.selectTeacherSection(TeacherSection.NOTICES) },
                            modifier = Modifier.testTag("teacher_notices_bell")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (notices.isNotEmpty()) {
                                        Badge(containerColor = Amber600) { Text("${notices.size}") }
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications")
                            }
                        }

                        // Switch to Student Mode Quick Button
                        IconButton(
                            onClick = { viewModel.quickLoginAsStudent() },
                            modifier = Modifier.testTag("switch_to_student_btn")
                        ) {
                            Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Switch to Student View", tint = RoyalBlue600)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentSection == TeacherSection.DASHBOARD,
                        onClick = { viewModel.selectTeacherSection(TeacherSection.DASHBOARD) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_dashboard")
                    )
                    NavigationBarItem(
                        selected = currentSection == TeacherSection.STUDENTS,
                        onClick = { viewModel.selectTeacherSection(TeacherSection.STUDENTS) },
                        icon = { Icon(Icons.Default.People, contentDescription = "Students") },
                        label = { Text("Students", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_students")
                    )
                    NavigationBarItem(
                        selected = currentSection == TeacherSection.ATTENDANCE,
                        onClick = { viewModel.selectTeacherSection(TeacherSection.ATTENDANCE) },
                        icon = { Icon(Icons.Default.FactCheck, contentDescription = "Attendance") },
                        label = { Text("Attendance", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_attendance")
                    )
                    NavigationBarItem(
                        selected = currentSection == TeacherSection.FEES,
                        onClick = { viewModel.selectTeacherSection(TeacherSection.FEES) },
                        icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Fees") },
                        label = { Text("Fees", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_fees")
                    )
                    NavigationBarItem(
                        selected = currentSection == TeacherSection.CLASSES ||
                                currentSection == TeacherSection.ASSIGNMENTS ||
                                currentSection == TeacherSection.EXAMS ||
                                currentSection == TeacherSection.MATERIALS ||
                                currentSection == TeacherSection.NOTICES ||
                                currentSection == TeacherSection.TIMETABLE ||
                                currentSection == TeacherSection.REPORTS ||
                                currentSection == TeacherSection.SETTINGS,
                        onClick = { coroutineScope.launch { drawerState.open() } },
                        icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "More") },
                        label = { Text("More", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_more")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentSection) {
                    TeacherSection.DASHBOARD -> TeacherDashboardScreen(viewModel)
                    TeacherSection.STUDENTS -> StudentManagementScreen(viewModel)
                    TeacherSection.CLASSES -> ClassManagementScreen(viewModel)
                    TeacherSection.ATTENDANCE -> AttendanceScreen(viewModel)
                    TeacherSection.FEES -> FeeManagementScreen(viewModel)
                    TeacherSection.ASSIGNMENTS -> AssignmentsAndExamsScreen(viewModel, initialTab = 0)
                    TeacherSection.EXAMS -> AssignmentsAndExamsScreen(viewModel, initialTab = 1)
                    TeacherSection.MATERIALS -> MaterialsAndNoticesScreen(viewModel, initialTab = 0)
                    TeacherSection.NOTICES -> MaterialsAndNoticesScreen(viewModel, initialTab = 1)
                    TeacherSection.TIMETABLE -> TimetableAndReportsScreen(viewModel, initialTab = 0)
                    TeacherSection.REPORTS -> TimetableAndReportsScreen(viewModel, initialTab = 1)
                    TeacherSection.SETTINGS, TeacherSection.PROFILE -> SettingsAndProfileScreen(viewModel)
                }
            }
        }
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = { Icon(imageVector = icon, contentDescription = label) },
        label = { Text(label, fontSize = 14.sp) },
        selected = selected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = RoyalBlue100,
            selectedTextColor = RoyalBlue900,
            selectedIconColor = RoyalBlue900
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
    )
}
