package com.example.data.repository

import com.example.data.dao.TuitionDao
import com.example.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TuitionRepository(private val dao: TuitionDao) {

    // Observables
    val allClasses: Flow<List<ClassEntity>> = dao.getAllClasses()
    val allStudents: Flow<List<StudentEntity>> = dao.getAllStudents()
    val allFees: Flow<List<FeeRecordEntity>> = dao.getAllFeeRecords()
    val allAssignments: Flow<List<AssignmentEntity>> = dao.getAllAssignments()
    val allSubmissions: Flow<List<AssignmentSubmissionEntity>> = dao.getAllSubmissions()
    val allExams: Flow<List<ExamEntity>> = dao.getAllExams()
    val allExamResults: Flow<List<ExamResultEntity>> = dao.getAllResults()
    val allMaterials: Flow<List<StudyMaterialEntity>> = dao.getAllStudyMaterials()
    val allNotices: Flow<List<NoticeEntity>> = dao.getAllNotices()
    val allTimetable: Flow<List<TimetableEntity>> = dao.getAllTimetable()
    val allAttendance: Flow<List<AttendanceEntity>> = dao.getAllAttendance()

    // Auth & Users
    suspend fun login(email: String, pass: String): UserEntity? = dao.login(email.trim(), pass)
    suspend fun registerUser(user: UserEntity): Long = dao.insertUser(user)
    suspend fun getUserById(id: Long): UserEntity? = dao.getUserById(id)

    // Classes
    suspend fun addClass(c: ClassEntity): Long = dao.insertClass(c)
    suspend fun updateClass(c: ClassEntity) = dao.updateClass(c)
    suspend fun deleteClass(id: Long) = dao.deleteClass(id)
    suspend fun getClassByJoinCode(code: String): ClassEntity? = dao.getClassByJoinCode(code.trim().uppercase())

    // Students
    fun getStudentsByClass(classId: Long): Flow<List<StudentEntity>> = dao.getStudentsByClass(classId)
    suspend fun addStudent(s: StudentEntity): Long = dao.insertStudent(s)
    suspend fun updateStudent(s: StudentEntity) = dao.updateStudent(s)
    suspend fun deleteStudent(id: Long) = dao.deleteStudent(id)
    suspend fun getStudentById(id: Long): StudentEntity? = dao.getStudentById(id)

    // Attendance
    fun getAttendanceForDateAndClass(date: String, classId: Long): Flow<List<AttendanceEntity>> =
        dao.getAttendanceByDateAndClass(date, classId)
    fun getAttendanceForStudent(studentId: Long): Flow<List<AttendanceEntity>> =
        dao.getAttendanceForStudent(studentId)
    suspend fun saveAttendance(records: List<AttendanceEntity>) = dao.insertAllAttendance(records)

    // Fees
    fun getFeesForStudent(studentId: Long): Flow<List<FeeRecordEntity>> = dao.getFeesForStudent(studentId)
    suspend fun addFeeRecord(f: FeeRecordEntity): Long = dao.insertFeeRecord(f)
    suspend fun updateFeeRecord(f: FeeRecordEntity) = dao.updateFeeRecord(f)
    suspend fun deleteFeeRecord(id: Long) = dao.deleteFeeRecord(id)

    // Assignments
    fun getSubmissionsForAssignment(aId: Long): Flow<List<AssignmentSubmissionEntity>> =
        dao.getSubmissionsForAssignment(aId)
    fun getSubmissionsForStudent(sId: Long): Flow<List<AssignmentSubmissionEntity>> =
        dao.getSubmissionsForStudent(sId)
    suspend fun addAssignment(a: AssignmentEntity): Long = dao.insertAssignment(a)
    suspend fun deleteAssignment(id: Long) = dao.deleteAssignment(id)
    suspend fun submitAssignment(sub: AssignmentSubmissionEntity): Long = dao.insertSubmission(sub)
    suspend fun gradeSubmission(sub: AssignmentSubmissionEntity) = dao.updateSubmission(sub)

    // Exams
    fun getResultsForExam(examId: Long): Flow<List<ExamResultEntity>> = dao.getResultsForExam(examId)
    fun getResultsForStudent(studentId: Long): Flow<List<ExamResultEntity>> = dao.getResultsForStudent(studentId)
    suspend fun addExam(exam: ExamEntity): Long = dao.insertExam(exam)
    suspend fun deleteExam(id: Long) = dao.deleteExam(id)
    suspend fun saveExamResults(results: List<ExamResultEntity>) = dao.insertExamResults(results)

    // Study Materials
    suspend fun addMaterial(m: StudyMaterialEntity): Long = dao.insertStudyMaterial(m)
    suspend fun deleteMaterial(id: Long) = dao.deleteStudyMaterial(id)

    // Notices
    suspend fun addNotice(n: NoticeEntity): Long = dao.insertNotice(n)
    suspend fun deleteNotice(id: Long) = dao.deleteNotice(id)

    // Timetable
    suspend fun addTimetableItem(t: TimetableEntity): Long = dao.insertTimetableItem(t)
    suspend fun deleteTimetableItem(id: Long) = dao.deleteTimetableItem(id)

    // Seed Demo Data
    suspend fun seedDemoData(forceReset: Boolean = false) {
        val existingClasses = dao.getAllClasses().first()
        if (existingClasses.isNotEmpty() && !forceReset) {
            return
        }

        if (forceReset) {
            dao.clearUsers()
            dao.clearClasses()
            dao.clearStudents()
            dao.clearAttendance()
            dao.clearFees()
            dao.clearAssignments()
            dao.clearSubmissions()
            dao.clearExams()
            dao.clearExamResults()
            dao.clearStudyMaterials()
            dao.clearNotices()
            dao.clearTimetable()
        }

        // 1. Users
        val teacherUser = UserEntity(
            id = 1,
            email = "teacher@tuitionhub.com",
            password = "password",
            role = "TEACHER",
            name = "Prof. Rajesh Sharma",
            phone = "+91 98765 43210",
            instituteName = "Apex Scholars Coaching Academy"
        )
        val studentUser = UserEntity(
            id = 2,
            email = "aarav@tuitionhub.com",
            password = "password",
            role = "STUDENT",
            name = "Aarav Sharma",
            phone = "+91 98111 22334",
            instituteName = "Apex Scholars Coaching Academy",
            studentIdRef = 1
        )
        dao.insertUser(teacherUser)
        dao.insertUser(studentUser)

        // 2. Classes
        val class1 = ClassEntity(
            id = 1,
            name = "Class 10 Mathematics",
            subject = "Mathematics",
            batch = "Morning Batch A",
            teacherName = "Prof. Rajesh Sharma",
            schedule = "Mon, Wed, Fri • 04:00 PM - 05:30 PM",
            room = "Hall 101",
            monthlyFee = 1500.0,
            startDate = "2026-06-01",
            status = "Active",
            joinCode = "MATH10"
        )
        val class2 = ClassEntity(
            id = 2,
            name = "Class 12 Physics",
            subject = "Physics",
            batch = "Evening Batch B",
            teacherName = "Prof. Rajesh Sharma",
            schedule = "Tue, Thu, Sat • 05:00 PM - 06:30 PM",
            room = "Science Lab 2",
            monthlyFee = 2200.0,
            startDate = "2026-06-01",
            status = "Active",
            joinCode = "PHY12"
        )
        val class3 = ClassEntity(
            id = 3,
            name = "NEET Biology Excellence",
            subject = "Biology",
            batch = "Weekend Medical Batch",
            teacherName = "Dr. Sunita Varma",
            schedule = "Sat, Sun • 09:00 AM - 12:00 PM",
            room = "Smart Room A",
            monthlyFee = 3000.0,
            startDate = "2026-05-15",
            status = "Active",
            joinCode = "NEETBIO"
        )
        val class4 = ClassEntity(
            id = 4,
            name = "NDA Mathematics & Aptitude",
            subject = "Mathematics",
            batch = "Defense Prep",
            teacherName = "Prof. Rajesh Sharma",
            schedule = "Mon, Wed • 06:30 PM - 08:00 PM",
            room = "Seminar Hall",
            monthlyFee = 2500.0,
            startDate = "2026-07-01",
            status = "Active",
            joinCode = "NDA26"
        )
        dao.insertClass(class1)
        dao.insertClass(class2)
        dao.insertClass(class3)
        dao.insertClass(class4)

        // 3. Students
        val sampleStudents = listOf(
            StudentEntity(
                id = 1,
                studentCode = "STU-2026-001",
                name = "Aarav Sharma",
                email = "aarav@tuitionhub.com",
                phone = "+91 98111 22334",
                parentName = "Suresh Sharma",
                parentPhone = "+91 98111 99887",
                address = "Flat 402, Green Avenue, Delhi",
                gender = "Male",
                dob = "2010-04-12",
                classId = 1,
                batch = "Morning Batch A",
                admissionDate = "2026-06-05",
                monthlyFee = 1500.0,
                status = "Active"
            ),
            StudentEntity(
                id = 2,
                studentCode = "STU-2026-002",
                name = "Diya Patel",
                email = "diya.patel@example.com",
                phone = "+91 98222 33445",
                parentName = "Mahesh Patel",
                parentPhone = "+91 98222 88776",
                address = "12 Heritage Park, Ahmedabad",
                gender = "Female",
                dob = "2010-08-20",
                classId = 1,
                batch = "Morning Batch A",
                admissionDate = "2026-06-05",
                monthlyFee = 1500.0,
                status = "Active"
            ),
            StudentEntity(
                id = 3,
                studentCode = "STU-2026-003",
                name = "Rohan Verma",
                email = "rohan.v@example.com",
                phone = "+91 98333 44556",
                parentName = "Anil Verma",
                parentPhone = "+91 98333 77665",
                address = "45 Lakeview Enclave, Jaipur",
                gender = "Male",
                dob = "2009-11-15",
                classId = 1,
                batch = "Morning Batch A",
                admissionDate = "2026-06-07",
                monthlyFee = 1500.0,
                status = "Active"
            ),
            StudentEntity(
                id = 4,
                studentCode = "STU-2026-004",
                name = "Ananya Singh",
                email = "ananya.s@example.com",
                phone = "+91 98444 55667",
                parentName = "Vikram Singh",
                parentPhone = "+91 98444 66554",
                address = "Sector 15, Chandigarh",
                gender = "Female",
                dob = "2008-02-18",
                classId = 2,
                batch = "Evening Batch B",
                admissionDate = "2026-06-02",
                monthlyFee = 2200.0,
                status = "Active"
            ),
            StudentEntity(
                id = 5,
                studentCode = "STU-2026-005",
                name = "Kabir Nair",
                email = "kabir.nair@example.com",
                phone = "+91 98555 66778",
                parentName = "Ramesh Nair",
                parentPhone = "+91 98555 55443",
                address = "Kakkanad, Kochi",
                gender = "Male",
                dob = "2008-07-25",
                classId = 2,
                batch = "Evening Batch B",
                admissionDate = "2026-06-03",
                monthlyFee = 2200.0,
                status = "Active"
            ),
            StudentEntity(
                id = 6,
                studentCode = "STU-2026-006",
                name = "Meera Mukherjee",
                email = "meera.m@example.com",
                phone = "+91 98666 77889",
                parentName = "Debashish Mukherjee",
                parentPhone = "+91 98666 44332",
                address = "Salt Lake, Kolkata",
                gender = "Female",
                dob = "2007-09-09",
                classId = 3,
                batch = "Weekend Medical Batch",
                admissionDate = "2026-05-20",
                monthlyFee = 3000.0,
                status = "Active"
            ),
            StudentEntity(
                id = 7,
                studentCode = "STU-2026-007",
                name = "Arjun Kapoor",
                email = "arjun.k@example.com",
                phone = "+91 98777 88990",
                parentName = "Prakash Kapoor",
                parentPhone = "+91 98777 33221",
                address = "Civil Lines, Dehradun",
                gender = "Male",
                dob = "2007-12-01",
                classId = 4,
                batch = "Defense Prep",
                admissionDate = "2026-07-02",
                monthlyFee = 2500.0,
                status = "Active"
            ),
            StudentEntity(
                id = 8,
                studentCode = "STU-2026-008",
                name = "Pooja Reddy",
                email = "pooja.reddy@example.com",
                phone = "+91 98888 99001",
                parentName = "Venkat Reddy",
                parentPhone = "+91 98888 22110",
                address = "Banjara Hills, Hyderabad",
                gender = "Female",
                dob = "2010-03-30",
                classId = 1,
                batch = "Morning Batch A",
                admissionDate = "2026-06-10",
                monthlyFee = 1500.0,
                status = "Active"
            )
        )
        dao.insertStudents(sampleStudents)

        // 4. Attendance
        val todayStr = "2026-09-25"
        val sampleAttendance = listOf(
            AttendanceEntity(studentId = 1, classId = 1, date = todayStr, status = "PRESENT", note = "On time"),
            AttendanceEntity(studentId = 2, classId = 1, date = todayStr, status = "PRESENT", note = "Active participation"),
            AttendanceEntity(studentId = 3, classId = 1, date = todayStr, status = "LATE", note = "10 mins late"),
            AttendanceEntity(studentId = 8, classId = 1, date = todayStr, status = "ABSENT", note = "Sick leave notified"),
            AttendanceEntity(studentId = 1, classId = 1, date = "2026-09-23", status = "PRESENT"),
            AttendanceEntity(studentId = 2, classId = 1, date = "2026-09-23", status = "PRESENT"),
            AttendanceEntity(studentId = 3, classId = 1, date = "2026-09-23", status = "PRESENT"),
            AttendanceEntity(studentId = 8, classId = 1, date = "2026-09-23", status = "PRESENT")
        )
        dao.insertAllAttendance(sampleAttendance)

        // 5. Fees & Receipts
        val sampleFees = listOf(
            FeeRecordEntity(
                receiptNo = "RCP-2026-0901",
                studentId = 1,
                classId = 1,
                monthYear = "September 2026",
                amountDue = 1500.0,
                amountPaid = 1500.0,
                discount = 0.0,
                status = "Paid",
                dueDate = "2026-09-10",
                paidDate = "2026-09-08",
                paymentMethod = "UPI / GPay",
                transactionRef = "UPI98234812739",
                notes = "Full tuition fee paid for September"
            ),
            FeeRecordEntity(
                receiptNo = "RCP-2026-0902",
                studentId = 2,
                classId = 1,
                monthYear = "September 2026",
                amountDue = 1500.0,
                amountPaid = 1500.0,
                discount = 0.0,
                status = "Paid",
                dueDate = "2026-09-10",
                paidDate = "2026-09-05",
                paymentMethod = "Cash",
                transactionRef = "CASH-REC-042",
                notes = "Received in person"
            ),
            FeeRecordEntity(
                receiptNo = "RCP-2026-0903",
                studentId = 3,
                classId = 1,
                monthYear = "September 2026",
                amountDue = 1500.0,
                amountPaid = 800.0,
                discount = 0.0,
                status = "Partially Paid",
                dueDate = "2026-09-10",
                paidDate = "2026-09-12",
                paymentMethod = "Bank Transfer",
                transactionRef = "IMPS88219401",
                notes = "Remaining ₹700 promised by end of week"
            ),
            FeeRecordEntity(
                receiptNo = "RCP-2026-0904",
                studentId = 8,
                classId = 1,
                monthYear = "September 2026",
                amountDue = 1500.0,
                amountPaid = 0.0,
                discount = 0.0,
                status = "Overdue",
                dueDate = "2026-09-10",
                notes = "Fee reminder notification sent to parent"
            ),
            FeeRecordEntity(
                receiptNo = "RCP-2026-0905",
                studentId = 4,
                classId = 2,
                monthYear = "September 2026",
                amountDue = 2200.0,
                amountPaid = 2200.0,
                status = "Paid",
                dueDate = "2026-09-10",
                paidDate = "2026-09-07",
                paymentMethod = "UPI / PhonePe",
                transactionRef = "UPI5521940182",
                notes = "Physics Class 12 fee cleared"
            )
        )
        dao.insertFeeRecords(sampleFees)

        // 6. Assignments
        val sampleAssignments = listOf(
            AssignmentEntity(
                id = 1,
                classId = 1,
                title = "Quadratic Equations Problem Set #4",
                subject = "Mathematics",
                description = "Solve all NCERT exemplar word problems from Chapter 4, Questions 1 to 15.",
                instructions = "Show step-by-step discriminant calculations and nature of roots. Submit handwritten or digital solution.",
                dueDate = "2026-09-28",
                maxMarks = 25,
                attachmentName = "Quadratic_Worksheet_Set4.pdf"
            ),
            AssignmentEntity(
                id = 2,
                classId = 1,
                title = "Arithmetic Progressions Quick Quiz Prep",
                subject = "Mathematics",
                description = "Formula sheet verification and solving 10 standard AP sum problems.",
                instructions = "Focus on nth term formula and sum of n terms formulas.",
                dueDate = "2026-10-02",
                maxMarks = 20,
                attachmentName = "AP_Formula_CheatSheet.pdf"
            ),
            AssignmentEntity(
                id = 3,
                classId = 2,
                title = "Electromagnetic Induction Numericals",
                subject = "Physics",
                description = "Faraday's Law and Lenz's Law practical application numericals.",
                instructions = "Draw magnetic flux diagrams for questions 3 and 7.",
                dueDate = "2026-09-30",
                maxMarks = 30,
                attachmentName = "EMI_Numericals_Class12.pdf"
            )
        )
        dao.insertAssignments(sampleAssignments)

        // 7. Submissions
        val sampleSubmissions = listOf(
            AssignmentSubmissionEntity(
                assignmentId = 1,
                studentId = 1,
                studentName = "Aarav Sharma",
                submittedDate = "2026-09-24",
                answerText = "Completed all 15 questions. Uploaded notebook scan. Question 12 verified with quadratic formula.",
                status = "Graded",
                marksObtained = 24,
                feedback = "Excellent working and neat presentation! Minor calculation correction on Q8."
            ),
            AssignmentSubmissionEntity(
                assignmentId = 1,
                studentId = 2,
                studentName = "Diya Patel",
                submittedDate = "2026-09-25",
                answerText = "Solved questions 1-15 with graph sketches attached.",
                status = "Submitted",
                marksObtained = null,
                feedback = ""
            )
        )
        dao.insertSubmissions(sampleSubmissions)

        // 8. Exams & Results
        val sampleExams = listOf(
            ExamEntity(
                id = 1,
                classId = 1,
                title = "Term 1 Mid-Evaluation Test",
                examType = "Term Exam",
                subject = "Mathematics",
                date = "2026-09-18",
                totalMarks = 50,
                durationMinutes = 90,
                instructions = "Sections A (MCQ), B (Short), C (Long questions). Calculator strictly prohibited.",
                rankingEnabled = true
            ),
            ExamEntity(
                id = 2,
                classId = 1,
                title = "Weekly Speed & Accuracy Quiz #7",
                examType = "Weekly Quiz",
                subject = "Mathematics",
                date = "2026-10-03",
                totalMarks = 20,
                durationMinutes = 30,
                instructions = "Rapid mental math and geometry theorems.",
                rankingEnabled = false
            ),
            ExamEntity(
                id = 3,
                classId = 2,
                title = "Optics & Ray Diagram Unit Test",
                examType = "Unit Test",
                subject = "Physics",
                date = "2026-09-22",
                totalMarks = 40,
                durationMinutes = 60,
                instructions = "Bring scale and sharp pencils for ray tracings.",
                rankingEnabled = true
            )
        )
        dao.insertExams(sampleExams)

        val sampleResults = listOf(
            ExamResultEntity(
                examId = 1,
                studentId = 1,
                studentName = "Aarav Sharma",
                marksObtained = 47.0,
                maxMarks = 50.0,
                percentage = 94.0,
                grade = "A+",
                feedback = "Outstanding clarity in proofs and algebra!",
                rank = 1
            ),
            ExamResultEntity(
                examId = 1,
                studentId = 2,
                studentName = "Diya Patel",
                marksObtained = 44.5,
                maxMarks = 50.0,
                percentage = 89.0,
                grade = "A",
                feedback = "Great grasp of concepts, revise circle theorems.",
                rank = 2
            ),
            ExamResultEntity(
                examId = 1,
                studentId = 3,
                studentName = "Rohan Verma",
                marksObtained = 39.0,
                maxMarks = 50.0,
                percentage = 78.0,
                grade = "B+",
                feedback = "Good effort, practice quadratic word problems more.",
                rank = 3
            )
        )
        dao.insertExamResults(sampleResults)

        // 9. Study Materials
        val sampleMaterials = listOf(
            StudyMaterialEntity(
                classId = 1,
                title = "Real Numbers & Polynomials Comprehensive Notes",
                subject = "Mathematics",
                chapter = "Chapter 1 & 2",
                topic = "Fundamental Theorem of Arithmetic & Zeroes",
                fileType = "PDF",
                fileSize = "2.4 MB",
                urlOrPath = "https://tuitionhub.example.com/notes/real_numbers.pdf",
                uploadDate = "2026-09-10"
            ),
            StudyMaterialEntity(
                classId = 1,
                title = "Master Formula Sheet: Triangles & Coordinate Geometry",
                subject = "Mathematics",
                chapter = "Chapter 6 & 7",
                topic = "Quick Revision Formulas",
                fileType = "Notes",
                fileSize = "1.1 MB",
                urlOrPath = "https://tuitionhub.example.com/notes/math_formulas.pdf",
                uploadDate = "2026-09-15"
            ),
            StudyMaterialEntity(
                classId = 1,
                title = "Live Class Recording: Quadratic Formula Derivation",
                subject = "Mathematics",
                chapter = "Chapter 4",
                topic = "Video Lecture Archive",
                fileType = "Video",
                fileSize = "45 MB (Stream)",
                urlOrPath = "https://tuitionhub.example.com/videos/quadratic_derivation.mp4",
                uploadDate = "2026-09-20"
            ),
            StudyMaterialEntity(
                classId = 2,
                title = "Current Electricity Detailed Handwritten Notes",
                subject = "Physics",
                chapter = "Chapter 3",
                topic = "Kirchhoff's Laws & Potentiometer",
                fileType = "PDF",
                fileSize = "3.8 MB",
                urlOrPath = "https://tuitionhub.example.com/notes/current_electricity.pdf",
                uploadDate = "2026-09-12"
            )
        )
        dao.insertStudyMaterials(sampleMaterials)

        // 10. Notices
        val sampleNotices = listOf(
            NoticeEntity(
                title = "Gandhi Jayanti & Dussehra Holiday Schedule",
                message = "Tuition centre will remain closed on Oct 2 and Oct 12. Compensatory doubt clearance classes will be conducted on Sunday Oct 5.",
                targetClassId = null,
                publishDate = "2026-09-24",
                priority = "Important",
                attachmentName = "Holiday_Calendar_Oct2026.pdf"
            ),
            NoticeEntity(
                title = "Monthly Fee Payment Reminder for September 2026",
                message = "Parents and students are requested to clear tuition dues by September 30 to receive the verified digital fee receipt.",
                targetClassId = null,
                publishDate = "2026-09-22",
                priority = "Urgent",
                attachmentName = ""
            ),
            NoticeEntity(
                title = "Formula Booklet Released for Class 10 Board Prep",
                message = "The 2026 revised board formula booklet has been uploaded to the Study Materials section. Please download and keep handy.",
                targetClassId = 1,
                publishDate = "2026-09-20",
                priority = "Normal",
                attachmentName = "Class10_Math_Formula_Booklet.pdf"
            )
        )
        dao.insertNotices(sampleNotices)

        // 11. Timetable
        val sampleTimetable = listOf(
            TimetableEntity(
                classId = 1,
                className = "Class 10 Mathematics",
                subject = "Mathematics",
                batch = "Morning A",
                dayOfWeek = "Monday",
                startTime = "04:00 PM",
                endTime = "05:30 PM",
                room = "Hall 101",
                teacher = "Prof. Rajesh Sharma"
            ),
            TimetableEntity(
                classId = 4,
                className = "NDA Mathematics",
                subject = "Aptitude & Math",
                batch = "Defense Prep",
                dayOfWeek = "Monday",
                startTime = "06:30 PM",
                endTime = "08:00 PM",
                room = "Seminar Hall",
                teacher = "Prof. Rajesh Sharma"
            ),
            TimetableEntity(
                classId = 2,
                className = "Class 12 Physics",
                subject = "Physics",
                batch = "Evening B",
                dayOfWeek = "Tuesday",
                startTime = "05:00 PM",
                endTime = "06:30 PM",
                room = "Science Lab 2",
                teacher = "Prof. Rajesh Sharma"
            ),
            TimetableEntity(
                classId = 1,
                className = "Class 10 Mathematics",
                subject = "Mathematics",
                batch = "Morning A",
                dayOfWeek = "Wednesday",
                startTime = "04:00 PM",
                endTime = "05:30 PM",
                room = "Hall 101",
                teacher = "Prof. Rajesh Sharma"
            ),
            TimetableEntity(
                classId = 2,
                className = "Class 12 Physics",
                subject = "Physics",
                batch = "Evening B",
                dayOfWeek = "Thursday",
                startTime = "05:00 PM",
                endTime = "06:30 PM",
                room = "Science Lab 2",
                teacher = "Prof. Rajesh Sharma"
            ),
            TimetableEntity(
                classId = 1,
                className = "Class 10 Mathematics",
                subject = "Mathematics",
                batch = "Morning A",
                dayOfWeek = "Friday",
                startTime = "04:00 PM",
                endTime = "05:30 PM",
                room = "Hall 101",
                teacher = "Prof. Rajesh Sharma"
            ),
            TimetableEntity(
                classId = 3,
                className = "NEET Biology Excellence",
                subject = "Biology",
                batch = "Weekend Medical",
                dayOfWeek = "Saturday",
                startTime = "09:00 AM",
                endTime = "12:00 PM",
                room = "Smart Room A",
                teacher = "Dr. Sunita Varma"
            )
        )
        dao.insertTimetable(sampleTimetable)
    }
}
