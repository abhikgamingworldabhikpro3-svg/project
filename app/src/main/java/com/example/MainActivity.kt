package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.TuitionDatabase
import com.example.data.repository.TuitionRepository
import com.example.ui.screens.LandingAndAuthScreen
import com.example.ui.screens.StudentPortalScreen
import com.example.ui.screens.TeacherPortalScreen
import com.example.ui.theme.RoyalBlue600
import com.example.ui.theme.TuitionTheme
import com.example.viewmodel.*

class MainActivity : ComponentActivity() {

    private val viewModel: TuitionViewModel by viewModels {
        val database = TuitionDatabase.getDatabase(applicationContext)
        val repository = TuitionRepository(database.tuitionDao())
        TuitionViewModelFactory(repository)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TuitionTheme {
                val destination by viewModel.currentDestination.collectAsStateWithLifecycle()
                val teacherSection by viewModel.teacherSection.collectAsStateWithLifecycle()
                val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
                val snackbarHostState = remember { SnackbarHostState() }

                // Display snackbars
                LaunchedEffect(userMessage) {
                    userMessage?.let { msg ->
                        snackbarHostState.showSnackbar(
                            message = msg,
                            duration = SnackbarDuration.Short
                        )
                        viewModel.clearUserMessage()
                    }
                }

                // Handle Android Back button gracefully
                BackHandler(enabled = destination != AppDestination.LANDING) {
                    when (destination) {
                        AppDestination.LOGIN, AppDestination.REGISTER -> {
                            viewModel.navigateTo(AppDestination.LANDING)
                        }
                        AppDestination.STUDENT_PORTAL -> {
                            viewModel.navigateTo(AppDestination.LANDING)
                        }
                        AppDestination.TEACHER_PORTAL -> {
                            if (teacherSection != TeacherSection.DASHBOARD) {
                                viewModel.selectTeacherSection(TeacherSection.DASHBOARD)
                            } else {
                                viewModel.navigateTo(AppDestination.LANDING)
                            }
                        }
                        else -> {}
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    contentWindowInsets = WindowInsets.systemBars
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (destination) {
                            AppDestination.LANDING,
                            AppDestination.LOGIN,
                            AppDestination.REGISTER -> {
                                LandingAndAuthScreen(viewModel = viewModel)
                            }
                            AppDestination.TEACHER_PORTAL -> {
                                TeacherPortalScreen(viewModel = viewModel)
                            }
                            AppDestination.STUDENT_PORTAL -> {
                                Scaffold(
                                    topBar = {
                                        TopAppBar(
                                            title = {
                                                Column {
                                                    Text(
                                                        text = "Student Portal",
                                                        fontWeight = FontWeight.Bold,
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                    Text(
                                                        text = "Apex Scholars Coaching Academy",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            },
                                            navigationIcon = {
                                                IconButton(
                                                    onClick = { viewModel.navigateTo(AppDestination.LANDING) },
                                                    modifier = Modifier.testTag("student_back_button")
                                                ) {
                                                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                                }
                                            },
                                            actions = {
                                                // Switch to Teacher mode button
                                                TextButton(
                                                    onClick = { viewModel.quickLoginAsTeacher() },
                                                    modifier = Modifier.testTag("student_switch_teacher_btn")
                                                ) {
                                                    Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Teacher View", color = RoyalBlue600)
                                                }

                                                IconButton(
                                                    onClick = { viewModel.logout() },
                                                    modifier = Modifier.testTag("student_logout_btn")
                                                ) {
                                                    Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                                                }
                                            },
                                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                                        )
                                    }
                                ) { studentPadding ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(studentPadding)
                                    ) {
                                        StudentPortalScreen(viewModel = viewModel)
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
