package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String,
    val password: String,
    val role: String, // "TEACHER" or "STUDENT"
    val name: String,
    val phone: String,
    val instituteName: String = "",
    val studentIdRef: Long? = null
)

@Entity(tableName = "classes")
data class ClassEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val subject: String,
    val batch: String,
    val teacherName: String,
    val schedule: String,
    val room: String,
    val monthlyFee: Double,
    val startDate: String,
    val status: String = "Active",
    val joinCode: String
)

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentCode: String,
    val name: String,
    val email: String,
    val phone: String,
    val parentName: String,
    val parentPhone: String,
    val address: String,
    val gender: String,
    val dob: String,
    val classId: Long,
    val batch: String,
    val admissionDate: String,
    val monthlyFee: Double,
    val status: String = "Active"
)

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val classId: Long,
    val date: String, // YYYY-MM-DD
    val status: String, // PRESENT, ABSENT, LATE
    val note: String = ""
)

@Entity(tableName = "fee_records")
data class FeeRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val receiptNo: String,
    val studentId: Long,
    val classId: Long,
    val monthYear: String,
    val amountDue: Double,
    val amountPaid: Double,
    val discount: Double = 0.0,
    val status: String, // Paid, Pending, Partially Paid, Overdue
    val dueDate: String,
    val paidDate: String? = null,
    val paymentMethod: String = "",
    val transactionRef: String = "",
    val notes: String = ""
)

@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val classId: Long,
    val title: String,
    val subject: String,
    val description: String,
    val instructions: String,
    val dueDate: String,
    val maxMarks: Int,
    val attachmentName: String = ""
)

@Entity(tableName = "assignment_submissions")
data class AssignmentSubmissionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val assignmentId: Long,
    val studentId: Long,
    val studentName: String,
    val submittedDate: String,
    val answerText: String,
    val status: String = "Submitted", // Not Started, Submitted, Late, Graded
    val marksObtained: Int? = null,
    val feedback: String = ""
)

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val classId: Long,
    val title: String,
    val examType: String, // Unit Test, Term Exam, Mock Test, Weekly Quiz
    val subject: String,
    val date: String,
    val totalMarks: Int,
    val durationMinutes: Int,
    val instructions: String,
    val rankingEnabled: Boolean = false
)

@Entity(tableName = "exam_results")
data class ExamResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examId: Long,
    val studentId: Long,
    val studentName: String,
    val marksObtained: Double,
    val maxMarks: Double,
    val percentage: Double,
    val grade: String,
    val feedback: String = "",
    val rank: Int? = null
)

@Entity(tableName = "study_materials")
data class StudyMaterialEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val classId: Long,
    val title: String,
    val subject: String,
    val chapter: String,
    val topic: String,
    val fileType: String, // PDF, Notes, Video, Document, Link
    val fileSize: String,
    val urlOrPath: String,
    val uploadDate: String
)

@Entity(tableName = "notices")
data class NoticeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val targetClassId: Long? = null, // null = All classes
    val publishDate: String,
    val priority: String = "Normal", // Normal, Important, Urgent
    val attachmentName: String = ""
)

@Entity(tableName = "timetable")
data class TimetableEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val classId: Long,
    val className: String,
    val subject: String,
    val batch: String,
    val dayOfWeek: String, // Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
    val startTime: String,
    val endTime: String,
    val room: String,
    val teacher: String
)
