package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.AppDestination
import com.example.viewmodel.TuitionViewModel

@Composable
fun LandingAndAuthScreen(viewModel: TuitionViewModel) {
    var authMode by remember { mutableStateOf<String>("LANDING") } // LANDING, LOGIN, REGISTER
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    when (authMode) {
        "LANDING" -> {
            LandingContent(
                onLoginClick = { authMode = "LOGIN" },
                onRegisterClick = { authMode = "REGISTER" },
                onTeacherDemo = { viewModel.quickLoginAsTeacher() },
                onStudentDemo = { viewModel.quickLoginAsStudent() }
            )
        }
        "LOGIN" -> {
            LoginContent(
                viewModel = viewModel,
                onSwitchToRegister = { authMode = "REGISTER" },
                onBackToLanding = { authMode = "LANDING" },
                onForgotPassword = { showForgotPasswordDialog = true },
                onTeacherDemo = { viewModel.quickLoginAsTeacher() },
                onStudentDemo = { viewModel.quickLoginAsStudent() }
            )
        }
        "REGISTER" -> {
            RegisterContent(
                viewModel = viewModel,
                onSwitchToLogin = { authMode = "LOGIN" },
                onBackToLanding = { authMode = "LANDING" }
            )
        }
    }

    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(onDismiss = { showForgotPasswordDialog = false })
    }
}

@Composable
fun LandingContent(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onTeacherDemo: () -> Unit,
    onStudentDemo: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .verticalScroll(rememberScrollState())
    ) {
        // App Bar / Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(RoyalBlue600),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Logo",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "TuitionHub",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue900
                        )
                        Text(
                            text = "Tutor & Class Management",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate600
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onLoginClick,
                        modifier = Modifier.testTag("landing_nav_login_button"),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Login", fontSize = 13.sp)
                    }
                    Button(
                        onClick = onRegisterClick,
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                        modifier = Modifier.testTag("landing_nav_register_button"),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Register", fontSize = 13.sp)
                    }
                }
            }
        }

        // Hero Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = RoyalBlue100,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "✦ Next-Gen Tuition & Coaching Suite",
                    color = RoyalBlue900,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Text(
                text = "Manage Your Tuition Smarter",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = Slate900,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Manage students, attendance, fees, assignments, exams and communication from one simple dashboard.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Slate600,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // CTA Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Button(
                    onClick = onLoginClick,
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("hero_get_started_btn")
                ) {
                    Text("Get Started")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
                OutlinedButton(
                    onClick = onRegisterClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("hero_register_btn")
                ) {
                    Text("Create Account")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Demo 1-Click Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("instant_demo_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate100),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = Amber600)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Instant 1-Click Evaluation",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    Text(
                        text = "Experience pre-loaded tuition classes, attendance records, receipts, exams, and notices immediately:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onTeacherDemo,
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue700),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("demo_teacher_login_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Teacher Demo", fontSize = 12.sp)
                        }
                        Button(
                            onClick = onStudentDemo,
                            colors = ButtonDefaults.buttonColors(containerColor = Teal600),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("demo_student_login_btn")
                        ) {
                            Icon(imageVector = Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Student Demo", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Features Grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Key Features",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Spacer(modifier = Modifier.height(12.dp))

            FeatureRow(
                icon = Icons.Default.Groups,
                iconColor = RoyalBlue600,
                title = "Class & Student Batches",
                desc = "Organize batches, admission dates, join codes, and student profiles in one place."
            )
            FeatureRow(
                icon = Icons.Default.CheckCircle,
                iconColor = Emerald600,
                title = "Instant Attendance",
                desc = "1-tap bulk attendance, monthly statistics, and student attendance percentage."
            )
            FeatureRow(
                icon = Icons.Default.ReceiptLong,
                iconColor = Amber600,
                title = "Fees & Digital Receipts",
                desc = "Track pending dues, record cash/online payments, and generate official receipts."
            )
            FeatureRow(
                icon = Icons.Default.Assignment,
                iconColor = RoyalBlue700,
                title = "Assignments & Submissions",
                desc = "Publish problem sets, set deadlines, collect answers, and give feedback."
            )
            FeatureRow(
                icon = Icons.Default.EmojiEvents,
                iconColor = Rose600,
                title = "Exams & Results",
                desc = "Unit tests, mock exams, auto percentage & grade calculation with optional rankings."
            )
            FeatureRow(
                icon = Icons.Default.MenuBook,
                iconColor = Teal700,
                title = "Study Materials & Notices",
                desc = "Share PDFs, notes, formula sheets, and broadcast holiday notices instantly."
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Footer
        Surface(color = Slate900, modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TuitionHub Coaching System",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Designed for Tutors, Coaching Academies & Students",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate400
                )
            }
        }
    }
}

@Composable
private fun FeatureRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    desc: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Text(text = desc, style = MaterialTheme.typography.bodySmall, color = Slate600)
            }
        }
    }
}

@Composable
fun LoginContent(
    viewModel: TuitionViewModel,
    onSwitchToRegister: () -> Unit,
    onBackToLanding: () -> Unit,
    onForgotPassword: () -> Unit,
    onTeacherDemo: () -> Unit,
    onStudentDemo: () -> Unit
) {
    var email by remember { mutableStateOf("teacher@tuitionhub.com") }
    var password by remember { mutableStateOf("password") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackToLanding, modifier = Modifier.testTag("login_back_button")) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "TuitionHub", fontWeight = FontWeight.Bold, color = RoyalBlue900)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(RoyalBlue100),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = RoyalBlue600, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(text = "Sign In to Your Account", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = "Access your classes, attendance, fees & notices", style = MaterialTheme.typography.bodySmall, color = Slate600)

        Spacer(modifier = Modifier.height(24.dp))

        // Card Container
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_email_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_password_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onForgotPassword) {
                        Text("Forgot Password?", fontSize = 12.sp, color = RoyalBlue600)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.login(email, password) },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("login_submit_button")
                ) {
                    Text("Sign In", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(color = Slate200)

                Spacer(modifier = Modifier.height(16.dp))

                // Fast 1-Click Evaluation buttons
                Text(
                    text = "Quick Demo Evaluation:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate600,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onTeacherDemo,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("login_quick_teacher_btn")
                    ) {
                        Text("Teacher Demo", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = onStudentDemo,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("login_quick_student_btn")
                    ) {
                        Text("Student Demo", fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Don't have an account?", style = MaterialTheme.typography.bodySmall, color = Slate600)
            TextButton(onClick = onSwitchToRegister) {
                Text(text = "Register Now", fontWeight = FontWeight.Bold, color = RoyalBlue600)
            }
        }
    }
}

@Composable
fun RegisterContent(
    viewModel: TuitionViewModel,
    onSwitchToLogin: () -> Unit,
    onBackToLanding: () -> Unit
) {
    var role by remember { mutableStateOf("TEACHER") } // TEACHER or STUDENT
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var institute by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackToLanding) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Create Account", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Role Tabs
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = role == "TEACHER",
                onClick = { role = "TEACHER" },
                shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
            ) {
                Text("Teacher / Tutor")
            }
            SegmentedButton(
                selected = role == "STUDENT",
                onClick = { role = "STUDENT" },
                shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
            ) {
                Text("Student")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_email_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_phone_input")
                )

                if (role == "TEACHER") {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = institute,
                        onValueChange = { institute = it },
                        label = { Text("Tuition / Coaching Name") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reg_institute_input")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reg_password_input")
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                            if (role == "TEACHER") {
                                viewModel.registerTeacher(name, email, phone, institute.ifEmpty { "My Tuition" }, password)
                            } else {
                                viewModel.registerStudent(name, email, phone, password)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("reg_submit_button")
                ) {
                    Text(if (role == "TEACHER") "Register as Teacher" else "Register as Student")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Already have an account?", style = MaterialTheme.typography.bodySmall, color = Slate600)
            TextButton(onClick = onSwitchToLogin) {
                Text(text = "Sign In", fontWeight = FontWeight.Bold, color = RoyalBlue600)
            }
        }
    }
}

@Composable
fun ForgotPasswordDialog(onDismiss: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Reset Password") },
        text = {
            Column {
                if (!sent) {
                    Text(text = "Enter your registered email address to receive password reset instructions.")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = "Reset link sent to $email! Please check your inbox.",
                        color = Emerald600,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            if (!sent) {
                Button(
                    onClick = { if (email.isNotEmpty()) sent = true },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue600)
                ) {
                    Text("Send Reset Link")
                }
            } else {
                Button(onClick = onDismiss) {
                    Text("Close")
                }
            }
        },
        dismissButton = {
            if (!sent) {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        }
    )
}
