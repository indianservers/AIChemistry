package com.indianservers.aichemistryexplorer.contentpack

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "mcq_packs",
    indices = [Index("subject"), Index(value = ["subject", "level", "classNumber"])]
)
internal data class McqPackEntity(
    @PrimaryKey val packId: String,
    val subject: String,
    val title: String,
    val language: String,
    val level: String,
    val classNumber: Int?,
    val version: Int,
    val questionCount: Int,
    val contentLicense: String,
    val installedAtEpochMs: Long,
    val sourceSha256: String
)

@Entity(
    tableName = "mcq_questions",
    foreignKeys = [
        ForeignKey(
            entity = McqPackEntity::class,
            parentColumns = ["packId"],
            childColumns = ["packId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("packId"),
        Index("subject"),
        Index("level"),
        Index("classNumber"),
        Index("category"),
        Index("subcategory"),
        Index("conceptId"),
        Index("difficulty"),
        Index(value = ["packId", "conceptId"])
    ]
)
internal data class McqQuestionEntity(
    @PrimaryKey val id: String,
    val packId: String,
    val schemaVersion: Int,
    val subject: String,
    val language: String,
    val level: String,
    val classNumber: Int?,
    val category: String,
    val subcategory: String,
    val conceptId: String,
    val concept: String,
    val difficulty: String,
    val cognitiveLevel: String,
    val type: String,
    val question: String,
    val correctOptionId: String,
    val explanation: String,
    val hint: String,
    val formula: String?,
    val relatedConceptsJson: String,
    val syllabusReferencesJson: String,
    val tagsJson: String,
    val estimatedSeconds: Int,
    val shuffleOptions: Boolean,
    val revision: Int,
    val mediaPath: String?
)

@Entity(
    tableName = "mcq_options",
    primaryKeys = ["questionId", "optionId"],
    foreignKeys = [
        ForeignKey(
            entity = McqQuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("questionId")]
)
internal data class McqOptionEntity(
    val questionId: String,
    val optionId: String,
    val text: String,
    val explanation: String,
    val displayOrder: Int
)

internal data class StoredQuestion(
    @Embedded val question: McqQuestionEntity,
    @Relation(parentColumn = "id", entityColumn = "questionId")
    val options: List<McqOptionEntity>
)

@Dao
internal interface McqContentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPack(pack: McqPackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<McqQuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptions(options: List<McqOptionEntity>)

    @Query("DELETE FROM mcq_questions WHERE packId = :packId")
    suspend fun deleteQuestionsForPack(packId: String)

    @Query("DELETE FROM mcq_packs WHERE packId = :packId")
    suspend fun deletePack(packId: String)

    @Query("SELECT * FROM mcq_packs WHERE packId = :packId")
    suspend fun getPack(packId: String): McqPackEntity?

    @Query("SELECT * FROM mcq_packs WHERE subject = :subject ORDER BY level, classNumber, title")
    fun observeInstalledPacks(subject: String): Flow<List<McqPackEntity>>

    @Query(
        """
        SELECT * FROM mcq_questions
        WHERE subject = :subject
          AND (:level IS NULL OR level = :level)
          AND (:classNumber IS NULL OR classNumber = :classNumber)
          AND (:category IS NULL OR category = :category)
          AND (:subcategory IS NULL OR subcategory = :subcategory)
          AND (:conceptId IS NULL OR conceptId = :conceptId)
          AND (:difficulty IS NULL OR difficulty = :difficulty)
        ORDER BY RANDOM()
        LIMIT :limit
        """
    )
    @Transaction
    suspend fun selectQuiz(
        subject: String,
        level: String?,
        classNumber: Int?,
        category: String?,
        subcategory: String?,
        conceptId: String?,
        difficulty: String?,
        limit: Int
    ): List<StoredQuestion>

    @Query("SELECT COUNT(*) FROM mcq_questions WHERE packId = :packId")
    suspend fun countQuestions(packId: String): Int
}

@Database(
    entities = [McqPackEntity::class, McqQuestionEntity::class, McqOptionEntity::class],
    version = 1,
    exportSchema = true
)
internal abstract class McqContentDatabase : RoomDatabase() {
    abstract fun contentDao(): McqContentDao

    companion object {
        @Volatile
        private var instance: McqContentDatabase? = null

        fun get(context: Context): McqContentDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    McqContentDatabase::class.java,
                    "downloaded-mcq-content.db"
                ).build().also { instance = it }
            }
    }
}
