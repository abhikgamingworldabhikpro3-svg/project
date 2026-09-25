package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.models.*
import com.example.data.repository.TuitionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppDestination {
    LANDING,
    LOGIN,
    REGISTER,
    TEACHER_PORTAL,
    STUDENT_PORTAL
}

enum class TeacherSection {
    DASHBOARD,
    STUDENTS,
    CLASSES,
    ATTENDANCE,
    FEES,
    ASSIGNMENTS,
    EXAMS,
    MATERIALS,
    NOTICES,
    TIMETABLE,
    REPORTS,
    SETTINGS,
    PROFILE
}

enum class StudentSection {
    HOME,
    MY_CLASSES,
    MY_ATTENDANCE,
    MY_FEES,
    MY_ASSIGNMENTS,
    MY_EXAMS,
    MY_MATERIALS,
    MY_NOTICES,
    MY_TIMETABLE
}

class TuitionViewModel(private val repository: TuitionRepository) : ViewModel() {

    // App Navigation State
    private val _currentDestination = MutableStateFlow(AppDestination.TEACHER_PORTAL)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    private val _teacherSection = MutableStateFlow(TeacherSection.DASHBOARD)
    val teacherSection: StateFlow<TeacherSection> = _teacherSection.asStateFlow()

    private val _studentSection = MutableStateFlow(StudentSection.HOME)
    val studentSection: StateFlow<StudentSection> = _studentSection.asStateFlow()

    // Current User
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Snack / Feedback Message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Search and Filters
    val searchQuery = MutableStateFlow("")
    val selectedClassFilter = MutableStateFlow<Long?>(null) // null = all classes
    val selectedAttendanceDate = MutableStateFlow(SimpleDateFormat("2026-09-25", Locale.getDefault()).format(Date()))

    // Active Receipt for Printing/Viewing
    private val _selectedReceipt = MutableStateFlow<FeeRecordEntity?>(null)
    val selectedReceipt: StateFlow<FeeRecordEntity?> = _selectedReceipt.asStateFlow()

    // Active Student for Profile Detail Modal
    private val _selectedStudentProfile = MutableStateFlow<StudentEntity?>(null)
    val selectedStudentProfile: StateFlow<StudentEntity?> = _selectedStudentProfile.asStateFlow()

    // Selected Assignment for Submissions/Grading
    private val _selectedAssignment = MutableStateFlow<AssignmentEntity?>(null)
    val selectedAssignment: StateFlow<AssignmentEntity?> = _selectedAssignment.asStateFlow()

    // Selected Exam for Marks Entry
    private val _selectedExam = MutableStateFlow<ExamEntity?>(null)
    val selectedExam: StateFlow<ExamEntity?> = _selectedExam.asStateFlow()

    // Database Observables
    val classes: StateFlow<List<ClassEntity>> = repository.allClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val students: StateFlow<List<StudentEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fees: StateFlow<List<FeeRecordEntity>> = repository.allFees
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val assignments: StateFlow<List<AssignmentEntity>> = repository.allAssignments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val submissions: StateFlow<List<AssignmentSubmissionEntity>> = repository.allSubmissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exams: StateFlow<List<ExamEntity>> = repository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val examResults: StateFlow<List<ExamResultEntity>> = repository.allExamResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val materials: StateFlow<List<StudyMaterialEntity>> = repository.allMaterials
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notices: StateFlow<List<NoticeEntity>> = repository.allNotices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val timetable: StateFlow<List<TimetableEntity>> = repository.allTimetable
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attendance: StateFlow<List<AttendanceEntity>> = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedDemoData()
            // Default logged in as Teacher for quick, interactive testing
            val teacher = repository.getUserById(1)
            _currentUser.value = teacher
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun navigateTo(dest: AppDestination) {
        _currentDestination.value = dest
    }

    fun selectTeacherSection(sec: TeacherSection) {
        _teacherSection.value = sec
    }

    fun selectStudentSection(sec: StudentSection) {
        _studentSection.value = sec
    }

    fun viewReceipt(fee: FeeRecordEntity?) {
        _selectedReceipt.value = fee
    }

    fun viewStudentProfile(student: StudentEntity?) {
        _selectedStudentProfile.value = student
    }

    fun selectAssignment(assignment: AssignmentEntity?) {
        _selectedAssignment.value = assignment
    }

    fun selectExam(exam: ExamEntity?) {
        _selectedExam.value = exam
    }

    // Auth actions
    fun login(email: String, pass: String) {
        viewModelScope.launch {
            val user = repository.login(email, pass)
            if (user != null) {
                _currentUser.value = user
                if (user.role == "TEACHER") {
                    _currentDestination.value = AppDestination.TEACHER_PORTAL
                    _teacherSection.value = TeacherSection.DASHBOARD
                } else {
                    _currentDestination.value = AppDestination.STUDENT_PORTAL
                    _studentSection.value = StudentSection.HOME
                }
                _userMessage.value = "Welcome back, ${user.name}!"
            } else {
                _userMessage.value = "Invalid credentials. Please check email and password."
            }
        }
    }

    fun quickLoginAsTeacher() {
        viewModelScope.launch {
            val teacher = repository.getUserById(1)
            _currentUser.value = teacher
            _currentDestination.value = AppDestination.TEACHER_PORTAL
            _teacherSection.value = TeacherSection.DASHBOARD
            _userMessage.value = "Logged in as Teacher (Prof. Rajesh Sharma)"
        }
    }

    fun quickLoginAsStudent() {
        viewModelScope.launch {
            val student = repository.getUserById(2)
            _currentUser.value = student
            _currentDestination.value = AppDestination.STUDENT_PORTAL
            _studentSection.value = StudentSection.HOME
            _userMessage.value = "Logged in as Student (Aarav Sharma)"
        }
    }

    fun registerTeacher(name: String, email: String, phone: String, institute: String, pass: String) {
        viewModelScope.launch {
            val newUser = UserEntity(
                email = email.trim(),
                password = pass,
                role = "TEACHER",
                name = name.trim(),
                phone = phone.trim(),
                instituteName = institute.trim()
            )
            val id = repository.registerUser(newUser)
            _currentUser.value = newUser.copy(id = id)
            _currentDestination.value = AppDestination.TEACHER_PORTAL
            _teacherSection.value = TeacherSection.DASHBOARD
            _userMessage.value = "Teacher registration successful!"
        }
    }

    fun registerStudent(name: String, email: String, phone: String, pass: String) {
        viewModelScope.launch {
            val newStudent = StudentEntity(
                studentCode = "STU-2026-${(100..999).random()}",
                name = name.trim(),
                email = email.trim(),
                phone = phone.trim(),
                parentName = "Parent of $name",
                parentPhone = phone.trim(),
                address = "Local Area",
                gender = "Not specified",
                dob = "2010-01-01",
                classId = 1,
                batch = "Batch A",
                admissionDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                monthlyFee = 1500.0,
                status = "Active"
            )
            val sId = repository.addStudent(newStudent)

            val newUser = UserEntity(
                email = email.trim(),
                password = pass,
                role = "STUDENT",
                name = name.trim(),
                phone = phone.trim(),
                instituteName = "Apex Scholars Coaching Academy",
                studentIdRef = sId
            )
            val uId = repository.registerUser(newUser)
            _currentUser.value = newUser.copy(id = uId)
            _currentDestination.value = AppDestination.STUDENT_PORTAL
            _studentSection.value = StudentSection.HOME
            _userMessage.value = "Student registration successful!"
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentDestination.value = AppDestination.LANDING
        _userMessage.value = "Logged out successfully"
    }

    // Class Operations
    fun addClass(name: String, subject: String, batch: String, schedule: String, room: String, fee: Double, code: String) {
        viewModelScope.launch {
            val c = ClassEntity(
                name = name.trim(),
                subject = subject.trim(),
                batch = batch.trim(),
                teacherName = _currentUser.value?.name ?: "Prof. Rajesh Sharma",
                schedule = schedule.trim(),
                room = room.trim(),
                monthlyFee = fee,
                startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                status = "Active",
                joinCode = code.trim().uppercase()
            )
            repository.addClass(c)
            _userMessage.value = "Class '$name' created successfully!"
        }
    }

    fun deleteClass(id: Long) {
        viewModelScope.launch {
            repository.deleteClass(id)
            _userMessage.value = "Class removed."
        }
    }

    // Student Operations
    fun addStudent(
        name: String, email: String, phone: String, parentName: String,
        parentPhone: String, address: String, gender: String, classId: Long,
        batch: String, fee: Double
    ) {
        viewModelScope.launch {
            val code = "STU-2026-${(100..999).random()}"
            val s = StudentEntity(
                studentCode = code,
                name = name.trim(),
                email = email.trim(),
                phone = phone.trim(),
                parentName = parentName.trim(),
                parentPhone = parentPhone.trim(),
                address = address.trim(),
                gender = gender,
                dob = "2010-01-01",
                classId = classId,
                batch = batch,
                admissionDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                monthlyFee = fee,
                status = "Active"
            )
            repository.addStudent(s)
            _userMessage.value = "Student '$name' added with ID $code"
        }
    }

    fun updateStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.updateStudent(student)
            _userMessage.value = "Student record updated."
        }
    }

    fun deleteStudent(id: Long) {
        viewModelScope.launch {
            repository.deleteStudent(id)
            _userMessage.value = "Student de-enrolled."
            _selectedStudentProfile.value = null
        }
    }

    // Attendance Operations
    fun markAttendance(studentId: Long, classId: Long, date: String, status: String, note: String = "") {
        viewModelScope.launch {
            val record = AttendanceEntity(
                studentId = studentId,
                classId = classId,
                date = date,
                status = status,
                note = note
            )
            repository.saveAttendance(listOf(record))
        }
    }

    fun markBulkAttendance(classId: Long, date: String, status: String) {
        viewModelScope.launch {
            val classStudents = students.value.filter { it.classId == classId }
            val records = classStudents.map { s ->
                AttendanceEntity(
                    studentId = s.id,
                    classId = classId,
                    date = date,
                    status = status,
                    note = "Bulk marked"
                )
            }
            repository.saveAttendance(records)
            _userMessage.value = "All marked as $status for $date"
        }
    }

    // Fee Operations
    fun recordFeePayment(
        studentId: Long, classId: Long, monthYear: String,
        amountDue: Double, amountPaid: Double, method: String, ref: String, notes: String
    ) {
        viewModelScope.launch {
            val rcp = "RCP-2026-${(1000..9999).random()}"
            val status = when {
                amountPaid >= amountDue -> "Paid"
                amountPaid > 0 -> "Partially Paid"
                else -> "Pending"
            }
            val fee = FeeRecordEntity(
                receiptNo = rcp,
                studentId = studentId,
                classId = classId,
                monthYear = monthYear,
                amountDue = amountDue,
                amountPaid = amountPaid,
                status = status,
                dueDate = SimpleDateFormat("yyyy-MM-10", Locale.getDefault()).format(Date()),
                paidDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                paymentMethod = method,
                transactionRef = ref,
                notes = notes
            )
            repository.addFeeRecord(fee)
            _userMessage.value = "Payment recorded! Receipt $rcp generated."
            _selectedReceipt.value = fee
        }
    }

    // Assignments
    fun addAssignment(classId: Long, title: String, subject: String, desc: String, instructions: String, dueDate: String, maxMarks: Int, attachment: String) {
        viewModelScope.launch {
            val a = AssignmentEntity(
                classId = classId,
                title = title.trim(),
                subject = subject.trim(),
                description = desc.trim(),
                instructions = instructions.trim(),
                dueDate = dueDate,
                maxMarks = maxMarks,
                attachmentName = attachment
            )
            repository.addAssignment(a)
            _userMessage.value = "Assignment '$title' assigned successfully!"
        }
    }

    fun submitStudentAssignment(assignmentId: Long, studentId: Long, studentName: String, answer: String) {
        viewModelScope.launch {
            val sub = AssignmentSubmissionEntity(
                assignmentId = assignmentId,
                studentId = studentId,
                studentName = studentName,
                submittedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                answerText = answer.trim(),
                status = "Submitted",
                marksObtained = null,
                feedback = ""
            )
            repository.submitAssignment(sub)
            _userMessage.value = "Assignment submitted successfully!"
        }
    }

    fun gradeSubmission(sub: AssignmentSubmissionEntity, marks: Int, feedback: String) {
        viewModelScope.launch {
            repository.gradeSubmission(
                sub.copy(
                    marksObtained = marks,
                    feedback = feedback,
                    status = "Graded"
                )
            )
            _userMessage.value = "Submission graded: $marks/${selectedAssignment.value?.maxMarks ?: 100}"
        }
    }

    // Exams
    fun addExam(classId: Long, title: String, type: String, subject: String, date: String, totalMarks: Int, duration: Int, instructions: String, ranking: Boolean) {
        viewModelScope.launch {
            val exam = ExamEntity(
                classId = classId,
                title = title.trim(),
                examType = type,
                subject = subject.trim(),
                date = date,
                totalMarks = totalMarks,
                durationMinutes = duration,
                instructions = instructions.trim(),
                rankingEnabled = ranking
            )
            repository.addExam(exam)
            _userMessage.value = "Exam '$title' created!"
        }
    }

    fun enterExamMarks(examId: Long, studentId: Long, studentName: String, marksObtained: Double, maxMarks: Double, feedback: String) {
        viewModelScope.launch {
            val pct = (marksObtained / maxMarks) * 100.0
            val grade = when {
                pct >= 90 -> "A+"
                pct >= 80 -> "A"
                pct >= 70 -> "B+"
                pct >= 60 -> "B"
                pct >= 50 -> "C"
                pct >= 40 -> "D"
                else -> "F"
            }
            val res = ExamResultEntity(
                examId = examId,
                studentId = studentId,
                studentName = studentName,
                marksObtained = marksObtained,
                maxMarks = maxMarks,
                percentage = pct,
                grade = grade,
                feedback = feedback
            )
            repository.saveExamResults(listOf(res))
            _userMessage.value = "Marks entered for $studentName: $marksObtained/$maxMarks ($grade)"
        }
    }

    // Materials
    fun addStudyMaterial(classId: Long, title: String, subject: String, chapter: String, topic: String, fileType: String, size: String, link: String) {
        viewModelScope.launch {
            val m = StudyMaterialEntity(
                classId = classId,
                title = title.trim(),
                subject = subject.trim(),
                chapter = chapter.trim(),
                topic = topic.trim(),
                fileType = fileType,
                fileSize = size,
                urlOrPath = link.trim(),
                uploadDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
            repository.addMaterial(m)
            _userMessage.value = "Study Material '$title' published!"
        }
    }

    // Notices
    fun addNotice(title: String, msg: String, targetClassId: Long?, priority: String, attachment: String) {
        viewModelScope.launch {
            val n = NoticeEntity(
                title = title.trim(),
                message = msg.trim(),
                targetClassId = targetClassId,
                publishDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                priority = priority,
                attachmentName = attachment
            )
            repository.addNotice(n)
            _userMessage.value = "Notice '$title' broadcasted!"
        }
    }

    // Timetable
    fun addTimetableItem(classId: Long, className: String, subject: String, batch: String, day: String, start: String, end: String, room: String) {
        viewModelScope.launch {
            val t = TimetableEntity(
                classId = classId,
                className = className,
                subject = subject,
                batch = batch,
                dayOfWeek = day,
                startTime = start,
                endTime = end,
                room = room,
                teacher = _currentUser.value?.name ?: "Tutor"
            )
            repository.addTimetableItem(t)
            _userMessage.value = "Timetable slot added for $day ($start - $end)"
        }
    }

    // Student Join Class by Code
    fun studentJoinClassByCode(code: String) {
        viewModelScope.launch {
            val foundClass = repository.getClassByJoinCode(code)
            if (foundClass != null) {
                val student = students.value.find { it.id == (_currentUser.value?.studentIdRef ?: 1L) }
                if (student != null) {
                    repository.updateStudent(student.copy(classId = foundClass.id, batch = foundClass.batch))
                    _userMessage.value = "Successfully joined '${foundClass.name}'!"
                } else {
                    _userMessage.value = "Joined '${foundClass.name}'! (Class code verified)"
                }
            } else {
                _userMessage.value = "Invalid class code: '$code'. Please ask your teacher for the valid join code."
            }
        }
    }

    // Reset Demo Data
    fun resetDemoData() {
        viewModelScope.launch {
            repository.seedDemoData(forceReset = true)
            val teacher = repository.getUserById(1)
            _currentUser.value = teacher
            _userMessage.value = "All tuition records reset to fresh demo state."
        }
    }
}

class TuitionViewModelFactory(private val repository: TuitionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TuitionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TuitionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
