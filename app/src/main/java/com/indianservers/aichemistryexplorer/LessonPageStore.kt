package com.indianservers.aichemistryexplorer

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

@Entity(tableName = "lesson_pages", primaryKeys = ["lesson_id", "page_no"])
internal data class LessonPageEntity(
    @ColumnInfo(name = "lesson_id") val lessonId: String,
    @ColumnInfo(name = "page_no") val pageNo: Int,
    @ColumnInfo(name = "page_title") val pageTitle: String,
    @ColumnInfo(name = "page_content") val pageContent: String,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)

@Dao
internal interface LessonPageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPages(pages: List<LessonPageEntity>)

    @Query("SELECT * FROM lesson_pages WHERE lesson_id = :lessonId ORDER BY page_no")
    fun observePages(lessonId: String): Flow<List<LessonPageEntity>>

    @Query("SELECT * FROM lesson_pages ORDER BY lesson_id, page_no")
    fun observeAllPages(): Flow<List<LessonPageEntity>>

    @Query("SELECT COUNT(*) FROM lesson_pages WHERE lesson_id = :lessonId")
    suspend fun countPages(lessonId: String): Int
}

@Database(entities = [LessonPageEntity::class], version = 1, exportSchema = true)
internal abstract class LessonPageDatabase : RoomDatabase() {
    abstract fun lessonPageDao(): LessonPageDao

    companion object {
        @Volatile private var instance: LessonPageDatabase? = null

        fun get(context: Context): LessonPageDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LessonPageDatabase::class.java,
                    "chemistry-lessons.db"
                ).build().also { instance = it }
            }
    }
}

internal class LessonPageRepository(context: Context) {
    private val dao = LessonPageDatabase.get(context).lessonPageDao()

    fun observeLessonPages(lessonId: String): Flow<List<LessonPageEntity>> =
        dao.observePages(lessonId)

    fun observeSchoolLessonAudits(): Flow<List<LessonPageAudit>> =
        dao.observeAllPages().map { pages -> auditSchoolLessonPages(curriculum, pages) }

    suspend fun replaceSelectedLesson(module: CurriculumModule, lesson: CurriculumLesson) {
        if (!module.isClassSixToTwelve()) return
        dao.upsertPages(rewriteLessonPages(module, lesson))
    }

    suspend fun replaceClassSixToTenModule(module: CurriculumModule) {
        if (!module.isClassSixToTwelve()) return
        dao.upsertPages(module.lessons.flatMap { lesson -> rewriteLessonPages(module, lesson) })
    }

    suspend fun replaceAllClassSixToTenLessons() {
        dao.upsertPages(
            curriculum
                .filter { it.isClassSixToTwelve() }
                .flatMap { module -> module.lessons.flatMap { lesson -> rewriteLessonPages(module, lesson) } }
        )
    }
}

internal data class LessonPageAudit(
    val moduleTitle: String,
    val lessonTitle: String,
    val lessonId: String,
    val pageCount: Int,
    val updatedAt: Long?,
    val issues: List<String>
) {
    val isReady: Boolean get() = issues.isEmpty()
}

internal fun lessonPageId(moduleTitle: String, lessonTitle: String): String =
    "${moduleTitle.toSlug()}__${lessonTitle.stableLessonSlug()}"

private fun CurriculumModule.isClassSixToTwelve(): Boolean =
    level.contains("6") || level.contains("7") || level.contains("8") ||
        level.contains("9") || level.contains("10") || level.contains("11") || level.contains("12")

private fun String.toSlug(): String = lowercase(Locale.US)
    .replace(Regex("[^a-z0-9]+"), "-")
    .trim('-')

private fun String.stableLessonSlug(): String = when (this) {
    "Solutions and Raoult's Law" -> "solutions-and-raoult-law"
    "d- and f-block elements" -> "d-and-f-block-elements"
    "Alcohols, phenols and ethers" -> "alcohols-phenols-and-ethers"
    "Aldehydes, ketones and carboxylic acids" -> "aldehydes-ketones-and-carboxylic-acids"
    "Amines and Diazonium Salts" -> "amines-and-diazonium-salts"
    "Biomolecules, vitamins and polymers" -> "biomolecules-vitamins-and-polymers"
    else -> replace("Raoult's law", "Raoult law").toSlug()
}

internal fun auditSchoolLessonPages(
    modules: List<CurriculumModule>,
    pages: List<LessonPageEntity>
): List<LessonPageAudit> {
    val expectedTitles = listOf("Discover", "Understand", "Observe", "Think", "Remember")
    val grouped = pages.groupBy { it.lessonId }
    return modules.filter { it.isClassSixToTwelve() }.flatMap { module ->
        module.lessons.map { lesson ->
            val lessonId = lessonPageId(module.title, lesson.title)
            val saved = grouped[lessonId].orEmpty().sortedBy { it.pageNo }
            val issues = buildList {
                if (saved.size != 5) add("Expected 5 pages, found ${saved.size}.")
                expectedTitles.forEachIndexed { index, title ->
                    val page = saved.firstOrNull { it.pageNo == index + 1 }
                    when {
                        page == null -> add("Missing page ${index + 1}: $title.")
                        page.pageTitle != title -> add("Page ${index + 1} title should be $title.")
                        page.pageContent.isBlank() -> add("$title page is empty.")
                        !page.pageContent.contains("Phase: $title") -> add("$title page missing phase marker.")
                        page.pageContent.contains("undergraduate", ignoreCase = true) ||
                            page.pageContent.contains("postgraduate", ignoreCase = true) -> add("$title page contains advanced-level wording.")
                    }
                }
                val contentAnchors = mapOf(1 to lesson.workedExample, 2 to lesson.concept,
                    3 to lesson.workedExample, 4 to lesson.question, 5 to lesson.formula)
                contentAnchors.forEach { (number, anchor) ->
                    val page = saved.firstOrNull { it.pageNo == number }
                    if (page != null && !page.pageContent.contains(anchor.escapeHtml())) {
                        add("Page $number does not contain the current lesson-specific content.")
                    }
                }
                if (saved.map { it.pageNo }.distinct().size != saved.size) add("Duplicate page_no rows detected.")
            }
            LessonPageAudit(
                moduleTitle = module.title,
                lessonTitle = lesson.title,
                lessonId = lessonId,
                pageCount = saved.size,
                updatedAt = saved.maxOfOrNull { it.updatedAt },
                issues = issues
            )
        }
    }
}

internal fun rewriteLessonPages(module: CurriculumModule, lesson: CurriculumLesson): List<LessonPageEntity> {
    val now = System.currentTimeMillis()
    val style = "Worked case"
    val brief = schoolBriefFor(lesson)
    val pages = listOf(
        "Discover" to discoverHtml(module, lesson, style, brief),
        "Understand" to understandHtml(lesson, style, brief),
        "Observe" to observeHtml(lesson, style, brief),
        "Think" to thinkHtml(lesson, style, brief),
        "Remember" to rememberHtml(lesson, brief)
    )
    return pages.mapIndexed { index, (title, html) ->
        LessonPageEntity(
            lessonId = lessonPageId(module.title, lesson.title),
            pageNo = index + 1,
            pageTitle = title,
            pageContent = html,
            updatedAt = now
        )
    }
}

internal data class SchoolLessonBrief(
    val discoverTitle: String,
    val hook: String,
    val whyItMatters: String,
    val dailyLink: String,
    val simpleMeaning: String,
    val analogy: String,
    val keyWords: String,
    val memoryTrick: String,
    val homeExample: String,
    val schoolExample: String,
    val natureExample: String,
    val activity: String,
    val prediction: String,
    val reason: String,
    val misconception: String,
    val factOne: String,
    val factTwo: String,
    val factThree: String,
    val examAnswer: String,
    val takeaway: String
)

private fun seniorBrief(
    discoverTitle: String,
    hook: String,
    whyItMatters: String,
    simpleMeaning: String,
    analogy: String,
    keyWords: String,
    memoryTrick: String,
    homeExample: String,
    schoolExample: String,
    natureExample: String,
    activity: String,
    prediction: String,
    reason: String,
    misconception: String,
    factOne: String,
    factTwo: String,
    factThree: String,
    examAnswer: String,
    takeaway: String,
    dailyLink: String = "Before reading, notice one related substance, reaction, diagram or measurement in the examples below."
): SchoolLessonBrief = SchoolLessonBrief(
    discoverTitle = discoverTitle,
    hook = hook,
    whyItMatters = whyItMatters,
    dailyLink = dailyLink,
    simpleMeaning = simpleMeaning,
    analogy = analogy,
    keyWords = keyWords,
    memoryTrick = memoryTrick,
    homeExample = homeExample,
    schoolExample = schoolExample,
    natureExample = natureExample,
    activity = activity,
    prediction = prediction,
    reason = reason,
    misconception = misconception,
    factOne = factOne,
    factTwo = factTwo,
    factThree = factThree,
    examAnswer = examAnswer,
    takeaway = takeaway
)

internal fun schoolBriefFor(lesson: CurriculumLesson): SchoolLessonBrief = lessonSpecificBrief(lesson)

private fun discoverHtml(module: CurriculumModule, lesson: CurriculumLesson, style: String, brief: SchoolLessonBrief): String =
    htmlPage(
        "Discover",
        style,
        brief.discoverTitle,
        listOf(
            brief.hook.escapeHtml(),
            "Today we learn <b>${lesson.title.escapeHtml()}</b>. This topic matters because ${brief.whyItMatters.escapeHtml()}.",
            "Chemical relationship: <b>${lesson.formula.escapeHtml()}</b>"
        ),
        "Daily-life link",
        brief.dailyLink.escapeHtml()
    )

private fun understandHtml(lesson: CurriculumLesson, style: String, brief: SchoolLessonBrief): String =
    htmlPage(
        "Understand",
        style,
        lesson.title,
        listOf(
            brief.simpleMeaning.escapeHtml(),
            brief.analogy.escapeHtml(),
            "Key words: <b>${brief.keyWords.escapeHtml()}</b>",
            "Relationship: <b>${brief.memoryTrick.escapeHtml()}</b>"
        ),
        "Interpretation and limits",
        lesson.commonMistake.escapeHtml()
    )

private fun observeHtml(lesson: CurriculumLesson, style: String, brief: SchoolLessonBrief): String =
    htmlPage(
        "Observe",
        style,
        "See it in real life",
        listOf(
            "Worked case: ${brief.schoolExample.escapeHtml()}",
            "Chemical explanation: ${brief.natureExample.escapeHtml()}",
            "Application: ${brief.homeExample.escapeHtml()}"
        ),
        "Case walkthrough",
        brief.activity.escapeHtml().replace("\n", "<br>")
    )

private fun thinkHtml(lesson: CurriculumLesson, style: String, brief: SchoolLessonBrief): String =
    htmlPage(
        "Think",
        style,
        "Build scientific thinking",
        listOf(
            "Prediction: ${brief.prediction.escapeHtml()}",
            "Why it happens: ${brief.reason.escapeHtml()}",
            "Common mistake: ${brief.misconception.escapeHtml()}"
        ),
        "Worked comparison",
        lesson.workedExample.escapeHtml()
    )

private fun rememberHtml(lesson: CurriculumLesson, brief: SchoolLessonBrief): String =
    htmlPage(
        "Remember",
        "Revision",
        "Quick revision",
        listOf(
            "Key point 1: ${brief.factOne.escapeHtml()}",
            "Key point 2: ${brief.factTwo.escapeHtml()}",
            "Key point 3: ${brief.factThree.escapeHtml()}"
        ),
        "Exam-ready answer",
        "${brief.examAnswer.escapeHtml()}<br><br>One-line takeaway: <b>${brief.takeaway.escapeHtml()}</b>"
    )

private fun htmlPage(
    phase: String,
    style: String,
    heading: String,
    paragraphs: List<String>,
    boxTitle: String,
    boxBody: String
): String = buildString {
    append("<section class='lesson-page'>")
    append("<p class='phase'>Phase: ${phase.escapeHtml()}</p>")
    append("<h2>${heading.escapeHtml()}</h2>")
    paragraphs.forEach { append("<p>$it</p>") }
    append("<div class='teacher-box'><h3>${boxTitle.escapeHtml()}</h3><p>$boxBody</p></div>")
    append("</section>")
}

private fun String.escapeHtml(): String =
    replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
