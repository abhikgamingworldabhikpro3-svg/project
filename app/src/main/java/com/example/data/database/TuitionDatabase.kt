package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.TuitionDao
import com.example.data.models.*

@Database(
    entities = [
        UserEntity::class,
        ClassEntity::class,
        StudentEntity::class,
        AttendanceEntity::class,
        FeeRecordEntity::class,
        AssignmentEntity::class,
        AssignmentSubmissionEntity::class,
        ExamEntity::class,
        ExamResultEntity::class,
        StudyMaterialEntity::class,
        NoticeEntity::class,
        TimetableEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TuitionDatabase : RoomDatabase() {
    abstract fun tuitionDao(): TuitionDao

    companion object {
        @Volatile
        private var INSTANCE: TuitionDatabase? = null

        fun getDatabase(context: Context): TuitionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TuitionDatabase::class.java,
                    "tuition_hub.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
