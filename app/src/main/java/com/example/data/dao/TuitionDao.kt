package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TuitionDao {

    // Users
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    // Classes
    @Query("SELECT * FROM classes ORDER BY id DESC")
    fun getAllClasses(): Flow<List<ClassEntity>>

    @Query("SELECT * FROM classes WHERE id = :id LIMIT 1")
    suspend fun getClassById(id: Long): ClassEntity?

    @Query("SELECT * FROM classes WHERE joinCode = :code LIMIT 1")
    suspend fun getClassByJoinCode(code: String): ClassEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(tuitionClass: ClassEntity): Long

    @Update
    suspend fun updateClass(tuitionClass: ClassEntity)

    @Query("DELETE FROM classes WHERE id = :id")
    suspend fun deleteClass(id: Long)

    // Students
    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE classId = :classId ORDER BY name ASC")
    fun getStudentsByClass(classId: Long): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentById(id: Long): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteStudent(id: Long)

    // Attendance
    @Query("SELECT * FROM attendance WHERE date = :date AND classId = :classId")
    fun getAttendanceByDateAndClass(date: String, classId: Long): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId")
    fun getAttendanceForStudent(studentId: Long): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAttendance(attendances: List<AttendanceEntity>)

    // Fees
    @Query("SELECT * FROM fee_records ORDER BY id DESC")
    fun getAllFeeRecords(): Flow<List<FeeRecordEntity>>

    @Query("SELECT * FROM fee_records WHERE studentId = :studentId ORDER BY id DESC")
    fun getFeesForStudent(studentId: Long): Flow<List<FeeRecordEntity>>

    @Query("SELECT * FROM fee_records WHERE classId = :classId ORDER BY id DESC")
    fun getFeesForClass(classId: Long): Flow<List<FeeRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeRecord(fee: FeeRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeRecords(fees: List<FeeRecordEntity>)

    @Update
    suspend fun updateFeeRecord(fee: FeeRecordEntity)

    @Query("DELETE FROM fee_records WHERE id = :id")
    suspend fun deleteFeeRecord(id: Long)

    // Assignments
    @Query("SELECT * FROM assignments ORDER BY id DESC")
    fun getAllAssignments(): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE classId = :classId ORDER BY id DESC")
    fun getAssignmentsByClass(classId: Long): Flow<List<AssignmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: AssignmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignments(assignments: List<AssignmentEntity>)

    @Query("DELETE FROM assignments WHERE id = :id")
    suspend fun deleteAssignment(id: Long)

    // Submissions
    @Query("SELECT * FROM assignment_submissions WHERE assignmentId = :assignmentId")
    fun getSubmissionsForAssignment(assignmentId: Long): Flow<List<AssignmentSubmissionEntity>>

    @Query("SELECT * FROM assignment_submissions WHERE studentId = :studentId")
    fun getSubmissionsForStudent(studentId: Long): Flow<List<AssignmentSubmissionEntity>>

    @Query("SELECT * FROM assignment_submissions")
    fun getAllSubmissions(): Flow<List<AssignmentSubmissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: AssignmentSubmissionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmissions(submissions: List<AssignmentSubmissionEntity>)

    @Update
    suspend fun updateSubmission(submission: AssignmentSubmissionEntity)

    // Exams
    @Query("SELECT * FROM exams ORDER BY id DESC")
    fun getAllExams(): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams WHERE classId = :classId ORDER BY id DESC")
    fun getExamsByClass(classId: Long): Flow<List<ExamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExams(exams: List<ExamEntity>)

    @Query("DELETE FROM exams WHERE id = :id")
    suspend fun deleteExam(id: Long)

    // Exam Results
    @Query("SELECT * FROM exam_results WHERE examId = :examId")
    fun getResultsForExam(examId: Long): Flow<List<ExamResultEntity>>

    @Query("SELECT * FROM exam_results WHERE studentId = :studentId")
    fun getResultsForStudent(studentId: Long): Flow<List<ExamResultEntity>>

    @Query("SELECT * FROM exam_results")
    fun getAllResults(): Flow<List<ExamResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamResult(result: ExamResultEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamResults(results: List<ExamResultEntity>)

    @Update
    suspend fun updateExamResult(result: ExamResultEntity)

    // Study Materials
    @Query("SELECT * FROM study_materials ORDER BY id DESC")
    fun getAllStudyMaterials(): Flow<List<StudyMaterialEntity>>

    @Query("SELECT * FROM study_materials WHERE classId = :classId ORDER BY id DESC")
    fun getStudyMaterialsByClass(classId: Long): Flow<List<StudyMaterialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyMaterial(material: StudyMaterialEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyMaterials(materials: List<StudyMaterialEntity>)

    @Query("DELETE FROM study_materials WHERE id = :id")
    suspend fun deleteStudyMaterial(id: Long)

    // Notices
    @Query("SELECT * FROM notices ORDER BY id DESC")
    fun getAllNotices(): Flow<List<NoticeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: NoticeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotices(notices: List<NoticeEntity>)

    @Query("DELETE FROM notices WHERE id = :id")
    suspend fun deleteNotice(id: Long)

    // Timetable
    @Query("SELECT * FROM timetable ORDER BY id ASC")
    fun getAllTimetable(): Flow<List<TimetableEntity>>

    @Query("SELECT * FROM timetable WHERE dayOfWeek = :day ORDER BY startTime ASC")
    fun getTimetableForDay(day: String): Flow<List<TimetableEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetableItem(item: TimetableEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetable(items: List<TimetableEntity>)

    @Query("DELETE FROM timetable WHERE id = :id")
    suspend fun deleteTimetableItem(id: Long)

    // Data wipe / reset
    @Query("DELETE FROM users")
    suspend fun clearUsers()

    @Query("DELETE FROM classes")
    suspend fun clearClasses()

    @Query("DELETE FROM students")
    suspend fun clearStudents()

    @Query("DELETE FROM attendance")
    suspend fun clearAttendance()

    @Query("DELETE FROM fee_records")
    suspend fun clearFees()

    @Query("DELETE FROM assignments")
    suspend fun clearAssignments()

    @Query("DELETE FROM assignment_submissions")
    suspend fun clearSubmissions()

    @Query("DELETE FROM exams")
    suspend fun clearExams()

    @Query("DELETE FROM exam_results")
    suspend fun clearExamResults()

    @Query("DELETE FROM study_materials")
    suspend fun clearStudyMaterials()

    @Query("DELETE FROM notices")
    suspend fun clearNotices()

    @Query("DELETE FROM timetable")
    suspend fun clearTimetable()
}
