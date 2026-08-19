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
                        page.pageContent.length < 380 -> add("$title page is too short.")
                        !page.pageContent.contains("Phase: $title") -> add("$title page missing phase marker.")
                        page.pageContent.contains("undergraduate", ignoreCase = true) ||
                            page.pageContent.contains("postgraduate", ignoreCase = true) -> add("$title page contains advanced-level wording.")
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

private fun rewriteLessonPages(module: CurriculumModule, lesson: CurriculumLesson): List<LessonPageEntity> {
    val now = System.currentTimeMillis()
    val style = teachingStyleFor(lesson.title)
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

private fun teachingStyleFor(title: String): String {
    val styles = listOf("Story", "Observation", "Analogy", "Question-answer", "Mini experiment", "Detective approach", "Comparison")
    return styles[(title.hashCode().let { if (it == Int.MIN_VALUE) 0 else kotlin.math.abs(it) }) % styles.size]
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

internal fun schoolBriefFor(lesson: CurriculumLesson): SchoolLessonBrief = when (lesson.title) {
    "Some basic concepts of chemistry" -> seniorBrief(
        "The counting language of chemistry",
        "A reaction is easy only when we know how much of each substance is present.",
        "basic concepts connect atoms, molecules, mass, volume and equations in one common language.",
        "This lesson introduces amount of substance, mole, molar mass, formula mass and balanced equations as tools for chemical calculation.",
        "It is like learning currency before shopping; mole and molar mass are the currency of chemistry.",
        "mole, molar mass, formula mass, Avogadro number, stoichiometry",
        "Count particles with mole, weigh them with molar mass.",
        "A packet label tells mass; chemistry uses molar mass to connect that mass with particles.",
        "A balanced equation is used to compare reacting amounts.",
        "A small sample of water still contains a huge number of molecules.",
        "Write one equation and mark what each coefficient tells about particles.",
        "If the equation is not balanced, can we trust the mole ratio?",
        "No. Mole ratios come from coefficients in a balanced equation.",
        "Do not use coefficients from an unbalanced equation.",
        "One mole contains 6.022 × 10²³ particles.",
        "Molar mass connects grams and moles.",
        "Balanced equations give mole ratios.",
        "Some basic concepts of chemistry help us calculate amounts in reactions using mole, molar mass and balanced chemical equations.",
        "Mole turns invisible particles into measurable chemistry.",
        "Check any medicine strip, food label or lab bottle and notice that chemistry begins with measured amounts."
    )
    "Stoichiometry and limiting reagent" -> seniorBrief(
        "The reactant that runs out first",
        "If bread is finished, sandwiches stop even if vegetables are still left.",
        "stoichiometry tells how much product can form and which reactant limits the reaction.",
        "Stoichiometry uses a balanced equation to compare reactants and products. The limiting reagent is the reactant used up first.",
        "A reaction is like making sandwiches: the ingredient that finishes first decides the maximum number made.",
        "stoichiometry, mole ratio, limiting reagent, excess reagent, yield",
        "Balance, convert to moles, compare, decide.",
        "Cooking uses fixed recipes; reactions use fixed mole ratios.",
        "Students compare given moles with equation coefficients.",
        "Plants stop growing if one required nutrient is too low, even if others are enough.",
        "Use 2H2 + O2 -> 2H2O and decide what limits water formation when H2 is low.",
        "If one reactant is in excess, will all of it react?",
        "No. Reaction stops when the limiting reagent is consumed.",
        "Do not choose the limiting reagent by mass alone; compare mole ratios.",
        "Stoichiometry uses balanced equations.",
        "The limiting reagent is used up first.",
        "The excess reagent remains after reaction.",
        "Stoichiometry calculates amounts of reactants and products. The limiting reagent decides the maximum product formed.",
        "The balanced equation is the recipe; the limiting reagent controls the result."
    )
    "Atomic structure for class 11" -> seniorBrief(
        "Light reveals atom structure",
        "Coloured light from heated gases shows that electrons have fixed energy changes.",
        "atomic structure explains spectra, electronic configuration and why elements behave differently.",
        "Class 11 atomic structure studies subatomic particles, atomic number, shells, subshells, orbitals and electronic configuration.",
        "An atom is like a school with floors, rooms and seats; electrons fill allowed places.",
        "proton, electron, shell, orbital, electronic configuration",
        "Protons name the element; electrons explain behaviour.",
        "Neon signs glow because atoms emit light of particular colours.",
        "Electronic configurations are written using aufbau, Pauli and Hund rules.",
        "Sunlight and flame colours are connected with electronic energy changes.",
        "Write electronic configuration for sodium and mark the valence electron.",
        "Why is atomic number more important than mass number for element identity?",
        "Atomic number counts protons, and proton count defines the element.",
        "Do not treat shells as solid circular tracks; they are models for electron arrangement.",
        "Atomic number is number of protons.",
        "Electrons occupy shells and subshells.",
        "Valence electrons strongly affect chemical properties.",
        "Atomic structure explains the arrangement of protons, neutrons and electrons and helps predict element behaviour.",
        "Find the electrons, and many properties start making sense."
    )
    "Hydrogen and its compounds" -> seniorBrief(
        "The smallest element with many roles",
        "Hydrogen is present in water, acids, fuels and many organic compounds.",
        "hydrogen connects bonding, acids, water, fuels and the periodic table.",
        "Hydrogen is the lightest element. It forms H2 gas, combines with oxygen to form water, and forms hydrides with many elements.",
        "Hydrogen is like a small key that can fit into many chemical locks.",
        "hydrogen, hydride, water, isotope, fuel",
        "Smallest atom, many compounds.",
        "Water and acids both contain hydrogen.",
        "Hydrogen preparation and flame testing must be a teacher-led laboratory activity with eye protection, a small sample and no nearby flame except the controlled test flame.",
        "Stars contain large amounts of hydrogen.",
        "Make a chart of hydrogen uses: water, fuel, ammonia and acids.",
        "Why is hydrogen placed separately in many periodic tables?",
        "It shows some similarities with alkali metals and some with halogens, but it is unique.",
        "Do not assume hydrogen behaves exactly like Group 1 metals.",
        "Hydrogen is the lightest element.",
        "It forms water with oxygen.",
        "It has isotopes called protium, deuterium and tritium.",
        "Hydrogen is a light, reactive element that forms many useful compounds such as water, ammonia and hydrides.",
        "Hydrogen is small, but its chemistry is everywhere."
    )
    "Environmental chemistry for school" -> seniorBrief(
        "Chemistry of clean air and water",
        "Smoke, sewage, plastic waste and polluted water are chemistry problems we can observe.",
        "environmental chemistry helps protect health, air, water, soil and future resources.",
        "Environmental chemistry studies chemical changes and pollutants in air, water and soil, and methods to reduce harm.",
        "The environment is like a shared classroom; if one corner is polluted, everyone is affected.",
        "pollution, pollutant, greenhouse effect, ozone, water treatment",
        "Identify pollutant, source, harm and control.",
        "Vehicle smoke and burning waste affect the air we breathe.",
        "Water samples can be tested for pH, conductivity or TDS, turbidity and colour using approved methods.",
        "Ozone high in the atmosphere protects life from harmful UV rays.",
        "Choose one pollutant and write source, effect and prevention.",
        "Will dilution alone solve pollution?",
        "No. Dilution may spread pollutants; prevention and treatment are better.",
        "Do not think pollution is only dirty-looking material; invisible gases can also pollute.",
        "Pollutants harm air, water or soil.",
        "Prevention is better than cleanup.",
        "Green chemistry reduces waste and hazards.",
        "Environmental chemistry studies pollutants, their effects and ways to prevent or reduce environmental damage.",
        "Clean chemistry supports clean living."
    )
    "States of matter and gas laws" -> seniorBrief(
        "Gases respond to pressure and temperature",
        "A balloon shrinks in cold air and expands in warmth.",
        "gas laws help predict pressure, volume, temperature and amount in gases.",
        "Gas laws describe how pressure, volume, temperature and moles of gas are related under suitable conditions.",
        "Gas particles are like fast-moving players inside a room; changing room size or heat changes collisions.",
        "pressure, volume, temperature, mole, ideal gas",
        "For gases, always use kelvin temperature.",
        "A pressure cooker works because pressure affects boiling and steam behaviour.",
        "Students plot pressure-volume or volume-temperature data.",
        "Air pressure changes with altitude.",
        "Explain what happens to balloon volume when temperature increases.",
        "What happens to volume if pressure increases at constant temperature?",
        "Volume decreases because gas particles have less space under higher pressure.",
        "Do not use Celsius directly in gas-law calculations.",
        "Pressure comes from particle collisions.",
        "Volume changes with pressure and temperature.",
        "Gas-law temperature must be in kelvin.",
        "Gas laws relate pressure, volume, temperature and amount of gas, often combined as PV = nRT.",
        "Gas behaviour becomes simple when you track P, V, n and T."
    )
    "Thermodynamics at school level" -> seniorBrief(
        "Heat, work and direction of change",
        "Hot tea cools down on its own, but cold tea does not become hot without energy.",
        "thermodynamics explains heat flow, energy changes and spontaneity in chemical processes.",
        "Thermodynamics studies heat, work, internal energy, enthalpy, entropy and Gibbs energy in physical and chemical changes.",
        "It is like an energy account book: heat and work are entries, and the system's energy changes.",
        "system, surroundings, heat, enthalpy, entropy, Gibbs energy",
        "System is what we study; surroundings are everything else.",
        "Ice melting and water boiling involve energy changes.",
        "Calorimetry measures heat change in a controlled setup.",
        "Living organisms constantly exchange energy with surroundings.",
        "Mark system and surroundings for a cup of hot water.",
        "Why does heat flow from hot object to cold object naturally?",
        "Energy spreads from higher temperature to lower temperature until thermal balance is approached.",
        "Do not confuse heat with temperature; heat is energy transfer.",
        "Thermodynamics tracks energy changes.",
        "Enthalpy is heat change at constant pressure.",
        "Gibbs energy helps judge spontaneity.",
        "Thermodynamics explains energy changes in chemical and physical processes using system, surroundings, heat, work and state functions.",
        "Every reaction has an energy story."
    )
    "Chemical and ionic equilibrium" -> seniorBrief(
        "Forward and reverse at balance",
        "In a closed bottle of soda, gas dissolves and escapes at the same time until balance is reached.",
        "equilibrium explains reversible reactions, acid-base behaviour, solubility and buffers.",
        "Chemical equilibrium occurs when forward and reverse reaction rates become equal. Ionic equilibrium applies this idea to ions in solution.",
        "Equilibrium is like two equal-speed escalators moving opposite ways; movement continues but the crowd looks steady.",
        "equilibrium, reversible, ion, acid, base, buffer",
        "Equal rates, not stopped reactions.",
        "A fizzy drink keeps carbon dioxide better when sealed.",
        "Indicators and pH show ionic equilibrium in acid-base systems.",
        "Many natural waters contain dissolved ions in equilibrium.",
        "Write one reversible reaction and mark forward and reverse arrows.",
        "At equilibrium, have reactions stopped?",
        "No. Both directions continue, but their rates are equal.",
        "Do not say equilibrium means equal amounts of reactants and products.",
        "Equilibrium is dynamic.",
        "Forward and reverse rates are equal.",
        "Ionic equilibrium explains pH, buffers and solubility.",
        "Chemical equilibrium is a dynamic state where forward and reverse reaction rates are equal; ionic equilibrium deals with ions in solution.",
        "Equilibrium means balance in motion."
    )
    "Redox reactions and oxidation number" -> seniorBrief(
        "Tracking electron change",
        "Rusting, batteries and bleaching all involve redox changes.",
        "oxidation numbers help identify oxidation and reduction in reactions.",
        "A redox reaction involves oxidation and reduction together. Oxidation number is a bookkeeping charge used to track electron change.",
        "Oxidation number is like a scorecard that shows which atom gained or lost electron control.",
        "oxidation, reduction, oxidation number, electron, redox",
        "OIL RIG: oxidation is loss, reduction is gain.",
        "Rusting iron is a redox process.",
        "Students assign oxidation numbers to atoms in simple compounds.",
        "Photosynthesis and respiration include redox changes.",
        "Find oxidation number of oxygen in H2O and CO2.",
        "Can oxidation happen without reduction?",
        "No. Electrons lost by one species are gained by another.",
        "Do not confuse oxygen addition with every oxidation case; electron change is the deeper idea.",
        "Oxidation and reduction occur together.",
        "Oxidation number helps track redox.",
        "Batteries work through redox reactions.",
        "Redox reactions involve oxidation and reduction, and oxidation numbers help identify electron transfer or electron-control changes.",
        "Redox is chemistry's electron accounting."
    )
    "Organic chemistry basic principles" -> seniorBrief(
        "How organic reactions begin",
        "Carbon compounds react in patterns, not random ways.",
        "basic principles help us understand naming, electron effects, intermediates and reaction mechanisms.",
        "Organic chemistry basic principles include tetravalency of carbon, functional groups, inductive effect, resonance, nucleophiles, electrophiles and reaction intermediates.",
        "A functional group is like a handle on a molecule; it decides many reactions.",
        "functional group, nucleophile, electrophile, resonance, intermediate",
        "Electron-rich attacks electron-poor.",
        "Vinegar, alcohol and fuels are carbon compounds with different structures; vinegar and alcohol contain characteristic functional groups.",
        "Students identify functional groups in simple structures.",
        "Many smells in fruits come from organic compounds.",
        "Circle the functional group in ethanol, ethanoic acid and chloroethane.",
        "Why do compounds with different functional groups behave differently?",
        "Functional groups create different electron-rich and electron-poor sites.",
        "Do not learn mechanisms as arrows only; each arrow shows electron movement.",
        "Carbon forms four bonds.",
        "Functional groups control many properties.",
        "Nucleophiles and electrophiles explain many reactions.",
        "Organic chemistry principles explain how structure, functional groups and electron movement control reactions.",
        "Organic reactions become easier when you follow electrons."
    )
    "Solutions and Raoult's Law" -> seniorBrief(
        "When liquids mix and vapour changes",
        "A closed bottle of perfume gives smell because some liquid particles escape as vapour.",
        "Raoult's law helps explain vapour pressure changes in solutions.",
        "A solution contains solute and solvent. Raoult's law says the vapour pressure of a solvent in an ideal solution depends on its mole fraction.",
        "If fewer solvent particles are at the surface, fewer can escape as vapour.",
        "solution, solvent, solute, vapour pressure, mole fraction",
        "More non-volatile solute means lower solvent vapour pressure.",
        "Salt water does not evaporate exactly like pure water.",
        "Students compare pure solvent and solution vapour pressure using a diagram.",
        "Seawater behaves differently from pure water because dissolved salts are present.",
        "Draw solvent particles at the surface before and after adding a non-volatile solute.",
        "What happens to vapour pressure when a non-volatile solute is added?",
        "It decreases because fewer solvent particles can escape from the surface.",
        "Do not apply Raoult's law blindly to every concentrated or strongly interacting solution.",
        "Solutions contain solute and solvent.",
        "Vapour pressure comes from particles escaping liquid surface.",
        "Raoult's law links vapour pressure with mole fraction.",
        "Raoult's law states that the partial vapour pressure of a solvent is proportional to its mole fraction in an ideal solution.",
        "A solution changes how easily solvent particles escape."
    )
    "Colligative properties" -> seniorBrief(
        "Properties that count particles",
        "Salt on icy roads and antifreeze in vehicles both use particle effects in solutions.",
        "colligative properties help find molar mass and explain boiling/freezing changes.",
        "Colligative properties depend mainly on the number of solute particles, not their chemical identity.",
        "It is like crowding a doorway; the number of people matters more than their names.",
        "relative lowering, boiling point elevation, freezing point depression, osmotic pressure, particles",
        "Colligative means count particles.",
        "Adding salt changes how water freezes and boils.",
        "Osmosis can be demonstrated with a membrane and sugar solution.",
        "Plant cells are affected by water movement through membranes.",
        "Compare one mole of sugar and one mole of salt in water and ask which gives more particles.",
        "Why does salt often affect freezing point more than sugar at the same mole amount?",
        "Salt separates into ions, so it gives more dissolved particles.",
        "Do not ignore dissociation when counting solute particles.",
        "Colligative properties depend on number of particles.",
        "They include boiling, freezing, vapour pressure and osmotic effects.",
        "Electrolytes can produce more particles than non-electrolytes.",
        "Colligative properties are solution properties that depend on the number of solute particles present.",
        "For colligative properties, count particles carefully."
    )
    "d- and f-block elements" -> seniorBrief(
        "Colourful metals in the middle",
        "Many transition metal compounds are coloured, and some metals show more than one charge.",
        "d- and f-block chemistry explains coloured compounds, variable oxidation states, catalysts and alloys.",
        "d-block elements have electrons entering d orbitals. f-block elements have electrons entering f orbitals and include lanthanoids and actinoids.",
        "The periodic table is like a city map; d- and f-blocks are special neighbourhoods with their own behaviour.",
        "transition element, d block, f block, oxidation state, colour",
        "d block: variable states and colours.",
        "Iron, copper and chromium compounds appear in many objects and reactions.",
        "Students observe colours of copper, iron and permanganate compounds.",
        "Some minerals get colour from transition metal ions.",
        "Make a table for Fe, Cu, Cr and Mn with one common oxidation state and one use.",
        "Why do many d-block elements show variable oxidation states?",
        "Their outer and nearby d electrons can take part in bonding or ion formation.",
        "Do not say every d-block element is coloured in every compound.",
        "d-block elements often show variable oxidation states.",
        "Many transition metal compounds are coloured.",
        "Lanthanoids and actinoids belong to f block.",
        "d- and f-block elements show important properties such as variable oxidation states, coloured ions, magnetism and catalytic activity.",
        "The middle blocks make inorganic chemistry colourful."
    )
    "Coordination compounds exam basics" -> seniorBrief(
        "Metal ions with attached groups",
        "The deep blue colour formed with copper ions and ammonia is a clue to coordination compounds.",
        "coordination compounds are important in colours, medicines, analysis and biological systems.",
        "A coordination compound has a central metal atom or ion attached to ligands by coordinate bonds.",
        "Think of a metal ion as a centre and ligands as helpers standing around it.",
        "central metal, ligand, coordination number, complex ion, isomerism",
        "Metal in centre, ligands around.",
        "Haemoglobin uses a metal complex to carry oxygen in blood.",
        "Students learn naming, coordination number and simple geometries.",
        "Chlorophyll contains a magnesium complex used by plants.",
        "Identify central metal, ligand and coordination number in a simple complex.",
        "Can a ligand donate an electron pair to a metal ion?",
        "Yes. Ligands form coordinate bonds by donating electron pairs.",
        "Do not count ions outside brackets as ligands inside the complex.",
        "Ligands donate electron pairs.",
        "Coordination number counts ligand donor atoms attached to metal.",
        "Complexes can show isomerism and colour.",
        "Coordination compounds contain a central metal atom or ion surrounded by ligands, forming coordinate bonds.",
        "Find the metal centre first; the complex becomes readable."
    )
    "Electrochemistry cells and Nernst equation" -> seniorBrief(
        "Chemistry that gives voltage",
        "A battery runs a torch because chemical change pushes electric charge through a circuit.",
        "electrochemistry explains batteries, corrosion, electrolysis and cell voltage.",
        "Galvanic cells convert chemical energy into electrical energy, whereas electrolytic cells use electrical energy to drive chemical change. The Nernst equation relates cell potential to concentration and temperature.",
        "A cell is like a pump for electrons; concentration changes can change the push.",
        "anode, cathode, EMF, electrode potential, Nernst equation",
        "Oxidation at anode, reduction at cathode.",
        "Phone batteries use redox processes to store and deliver energy.",
        "Students draw galvanic cells with salt bridge, anode and cathode.",
        "Corrosion is an unwanted electrochemical process.",
        "Label anode, cathode and electron flow in a zinc-copper cell.",
        "Will cell potential change if ion concentration changes?",
        "Yes. The Nernst equation shows concentration affects electrode potential.",
        "Do not reverse anode and cathode: oxidation is always at anode.",
        "Galvanic cells produce electrical energy.",
        "Electrolytic cells use electrical energy.",
        "Nernst equation corrects potential for non-standard conditions.",
        "Electrochemistry studies redox reactions that produce or use electricity; the Nernst equation relates cell potential to concentrations.",
        "Follow the electrons to understand the cell."
    )
    "Haloalkanes and haloarenes" -> seniorBrief(
        "Carbon compounds with halogens",
        "Some cleaning solvents, medicines and refrigerant-related compounds contain halogens bonded to carbon.",
        "halo compounds teach substitution, elimination and environmental care.",
        "Haloalkanes have halogen attached to an alkyl group. Haloarenes have halogen attached to an aromatic ring.",
        "A halogen is like a replaceable handle on many carbon compounds.",
        "haloalkane, haloarene, halogen, substitution, elimination",
        "Alkane side or arene ring decides behaviour.",
        "Some antiseptic and solvent molecules contain carbon-halogen bonds.",
        "Students compare reactions of chloroethane and chlorobenzene.",
        "Some halogenated compounds can affect the ozone layer, so safe use matters.",
        "Write one difference between haloalkanes and haloarenes.",
        "Why are haloarenes less reactive than haloalkanes toward nucleophilic substitution at the carbon-halogen bond?",
        "Haloarenes are less reactive than haloalkanes toward nucleophilic substitution at the carbon-halogen bond because resonance gives the bond partial double-bond character.",
        "Do not assume every C-X bond reacts at the same speed.",
        "Haloalkanes contain halogen on alkyl carbon.",
        "Haloarenes contain halogen on aromatic ring.",
        "They can undergo substitution and elimination reactions.",
        "Haloalkanes and haloarenes are halogen-containing organic compounds with different structures and reactivities.",
        "In organic chemistry, position changes behaviour."
    )
    "Alcohols, phenols and ethers" -> seniorBrief(
        "Oxygen in organic families",
        "Sanitizer, phenolic disinfectants and ethers all show oxygen-containing organic compounds.",
        "these functional groups appear in medicines, fuels, solvents and daily-use products.",
        "Alcohols contain -OH on saturated carbon, phenols contain -OH on an aromatic ring, and ethers contain C-O-C linkage.",
        "The oxygen atom is like a connector that changes the personality of the molecule.",
        "alcohol, phenol, ether, hydroxyl group, functional group",
        "Alcohol: OH on alkyl; phenol: OH on ring; ether: C-O-C.",
        "Ethanol is present in many sanitizers.",
        "Students compare acidity of alcohols and phenols using reactions.",
        "Some natural oils and fragrances contain oxygenated organic compounds.",
        "Classify ethanol, phenol and dimethyl ether by functional group.",
        "Which is usually more acidic: phenol or ethanol?",
        "Phenol is more acidic because its ion is stabilised by resonance.",
        "Do not call every compound with oxygen an alcohol.",
        "Alcohols, phenols and ethers are oxygen-containing compounds.",
        "Functional group decides naming and reactions.",
        "Phenols are more acidic than ordinary alcohols.",
        "Alcohols, phenols and ethers are organic compounds classified by the position and bonding of oxygen in their functional groups.",
        "Spot the functional group before predicting reaction."
    )
    "Aldehydes, ketones and carboxylic acids" -> seniorBrief(
        "The carbonyl family",
        "Vinegar, perfumes and many medicines contain carbonyl-related compounds.",
        "carbonyl chemistry explains oxidation, reduction, addition reactions and acidity.",
        "Aldehydes and ketones contain carbonyl group C=O. Carboxylic acids contain -COOH group and show acidic behaviour.",
        "The carbonyl group is like a magnet for many organic reactions.",
        "aldehyde, ketone, carboxylic acid, carbonyl, oxidation",
        "Aldehyde at end, ketone inside, acid has COOH.",
        "Vinegar contains ethanoic acid.",
        "Students use Tollens or Fehling tests conceptually to distinguish aldehydes.",
        "Many fruit smells come from carbonyl compounds and related esters.",
        "Circle the carbonyl group in ethanal, propanone and ethanoic acid.",
        "Why do aldehydes often oxidise more easily than ketones?",
        "Aldehydes have a hydrogen attached to the carbonyl carbon, making oxidation easier.",
        "Do not confuse aldehydes and ketones only by formula; check carbonyl position.",
        "Aldehydes have terminal carbonyl group.",
        "Ketones have carbonyl group within the chain.",
        "Carboxylic acids contain -COOH.",
        "Aldehydes, ketones and carboxylic acids are carbonyl-containing organic compounds with different structures and reactions.",
        "Carbonyl position tells the compound family."
    )
    "Amines and Diazonium Salts" -> seniorBrief(
        "Nitrogen in organic chemistry",
        "Many dyes and medicines contain nitrogen compounds.",
        "amines and diazonium salts connect organic bases, aromatic reactions and dye formation.",
        "Amines are organic derivatives of ammonia. Diazonium salts are reactive aromatic nitrogen compounds useful in making dyes and substituted arenes.",
        "Amines are like ammonia wearing carbon groups.",
        "amine, ammonia, basicity, diazonium salt, azo dye",
        "Amine comes from ammonia.",
        "Some drug molecules contain amine groups.",
        "Students learn preparation and reactions of aniline diazonium chloride.",
        "Natural alkaloids often contain nitrogen atoms.",
        "Mark the nitrogen functional group in methylamine and aniline.",
        "Why are many amines basic?",
        "Nitrogen has a lone pair that can accept a proton.",
        "Do not treat all amines as equally basic; structure affects basicity.",
        "Amines are ammonia derivatives.",
        "Amines can act as bases.",
        "Diazonium salts are useful intermediates in aromatic chemistry.",
        "Amines are nitrogen-containing organic compounds derived from ammonia, while diazonium salts are reactive compounds used in synthesis and dye formation.",
        "Nitrogen's lone pair drives much of amine chemistry."
    )
    "Biomolecules, vitamins and polymers" -> seniorBrief(
        "Chemistry inside living things",
        "Food, muscles, DNA, vitamins and plastics all connect with large molecules.",
        "biomolecules and polymers explain nutrition, health, heredity and useful materials.",
        "Biomolecules include carbohydrates, proteins, nucleic acids and vitamins. Polymers are large molecules made by joining many smaller units.",
        "A polymer is like a long chain made from many repeated beads.",
        "carbohydrate, protein, vitamin, nucleic acid, polymer",
        "Mono means one; poly means many.",
        "Starch in food, proteins in dal and vitamins in fruits are biomolecules.",
        "Students identify monomers and polymers using simple examples.",
        "DNA carries genetic information in living organisms.",
        "Make a chart of biomolecule, source and function.",
        "Why are proteins called polymers of amino acids?",
        "Proteins are made by joining many amino acid units in long chains.",
        "Do not think polymers are only plastics; proteins and DNA are natural polymers.",
        "Many carbohydrates, such as sugars and starch, provide energy.",
        "Proteins are made of amino acids.",
        "Polymers are made of repeating units.",
        "Biomolecules are important molecules in living systems, and polymers are large molecules formed by joining many small units.",
        "Life uses chemistry to build large useful molecules."
    )
    "Materials by appearance and hardness" -> SchoolLessonBrief(
        discoverTitle = "Why objects feel different",
        hook = "Pick up a spoon, a rubber band and a notebook. They do not look or feel the same.",
        whyItMatters = "we choose materials by their properties, such as shine, hardness, softness and transparency.",
        dailyLink = "Look at five objects near you and describe each one using simple words: shiny, dull, hard, soft, rough or smooth.",
        simpleMeaning = "A material is what an object is made of. Appearance tells how it looks. Hardness tells how easily a material can be scratched or indented. Flexibility tells how easily it bends.",
        analogy = "Materials are like students in a class. Each has its own features, so we identify them by observing carefully.",
        keyWords = "material, property, appearance, hardness, transparency",
        memoryTrick = "Look first, touch safely second, compare third.",
        homeExample = "Steel spoons are hard and shiny. Cotton cloth is soft and flexible.",
        schoolExample = "A glass window is transparent, while a wooden desk is opaque.",
        natureExample = "Stone is hard, but dry leaves are light and easily crushed.",
        activity = "Make a table of five safe objects. Write material, appearance and hardness for each.",
        prediction = "Which will scratch more easily: chalk or a steel spoon?",
        reason = "Chalk is softer than steel, so it gets scratched or powdered more easily.",
        misconception = "Shiny does not always mean strongest. Shine and hardness are different properties.",
        factOne = "Materials are chosen because of their properties.",
        factTwo = "Appearance means how a material looks.",
        factThree = "Hardness tells how easily a material can be scratched or indented.",
        examAnswer = "Materials can be grouped by properties such as appearance, hardness, softness, transparency and texture.",
        takeaway = "Careful observation is the first step of chemistry."
    )
    "Soluble and insoluble substances" -> SchoolLessonBrief(
        discoverTitle = "Why sugar disappears in water",
        hook = "When sugar is stirred in water, it seems to disappear. Sand does not disappear in the same way.",
        whyItMatters = "solubility helps us understand drinks, medicines, cleaning, cooking and water pollution.",
        dailyLink = "Compare what happens when sugar, salt, sand and oil are added separately to water.",
        simpleMeaning = "A soluble substance dissolves in a liquid. An insoluble substance does not dissolve and can often be seen separately.",
        analogy = "A soluble substance mixes like a friend joining a group. An insoluble substance stays apart.",
        keyWords = "soluble, insoluble, dissolve, solute, solvent",
        memoryTrick = "Soluble means capable of dissolving in a particular solvent.",
        homeExample = "Salt dissolves in soup. Sand settles at the bottom of a bucket.",
        schoolExample = "A teacher may test different powders in water and compare the results.",
        natureExample = "Rainwater dissolves some minerals as it moves through soil and rocks.",
        activity = "Stir a small spoon of salt in water. Observe before and after stirring. Do not taste lab samples.",
        prediction = "Will oil dissolve fully in water like salt?",
        reason = "Oil does not mix well with water, so it forms a separate layer.",
        misconception = "When sugar dissolves, it is not destroyed. It is spread through the water.",
        factOne = "Soluble substances dissolve in a solvent.",
        factTwo = "Insoluble substances do not dissolve easily.",
        factThree = "Dissolving does not mean the substance has vanished forever.",
        examAnswer = "A soluble substance dissolves in a solvent to form a solution. An insoluble substance does not dissolve in that solvent.",
        takeaway = "If it dissolves, it spreads out; it does not magically vanish."
    )
    "Floating and sinking materials" -> SchoolLessonBrief(
        discoverTitle = "Why some things float",
        hook = "A leaf may float on water, but a stone usually sinks. Both are made of matter, but they behave differently.",
        whyItMatters = "floating and sinking help us choose materials for boats, life jackets and storage containers.",
        dailyLink = "Drop safe objects like a leaf, plastic cap and small stone into water with an adult nearby.",
        simpleMeaning = "An object floats when water can support it. It sinks when it is pulled down more than the water can push it up.",
        analogy = "Water is like a helping hand from below. Some objects get enough help to stay up; some do not.",
        keyWords = "float, sink, density, material, shape",
        memoryTrick = "Float means stay on top. Sink means go down.",
        homeExample = "A plastic bottle with air inside floats, but a metal coin sinks.",
        schoolExample = "Students can compare same-sized objects made of different materials.",
        natureExample = "Wood often floats, which is why logs can move along rivers.",
        activity = "Test three safe objects in water. Write float or sink, then guess why.",
        prediction = "Will a closed empty plastic bottle float better than a bottle filled with water?",
        reason = "The empty bottle has air inside, so it is lighter for its size.",
        misconception = "Heavy things do not always sink. Shape and air inside can also matter.",
        factOne = "Floating and sinking depend on material, shape and trapped air.",
        factTwo = "Some light-looking objects can sink if they are dense.",
        factThree = "Testing gives better answers than guessing by size only.",
        examAnswer = "Objects float or sink depending on their material, shape and how strongly water can support them.",
        takeaway = "Do not judge by size alone; observe what happens in water."
    )
    "Handpicking, sieving and winnowing" -> SchoolLessonBrief(
        discoverTitle = "Separating mixtures by simple clues",
        hook = "Before cooking rice or pulses, people often remove small stones or dust. That is separation.",
        whyItMatters = "we separate useful things from unwanted things in food, farming and daily cleaning.",
        dailyLink = "Notice how grains, tea leaves, flour or pulses are cleaned before use.",
        simpleMeaning = "Handpicking removes large visible impurities. Sieving separates by particle size. Winnowing separates lighter particles from heavier grains using air.",
        analogy = "Separation is like sorting coins. You choose the method based on the difference you can see.",
        keyWords = "mixture, handpicking, sieving, winnowing, impurity",
        memoryTrick = "Pick by hand, sieve by size, winnow by wind.",
        homeExample = "Tea strainer separates tea leaves from tea.",
        schoolExample = "A sieve can separate sand and stones because their sizes are different.",
        natureExample = "Wind can carry dry leaves or husk farther than heavier seeds.",
        activity = "Mix rice with a few larger pulses. Separate them by hand and write why handpicking worked.",
        prediction = "Can sieving separate salt dissolved in water?",
        reason = "No. Sieving works for different-sized solid particles, not dissolved substances.",
        misconception = "One separation method does not work for every mixture.",
        factOne = "Handpicking works when impurities are large and visible.",
        factTwo = "Sieving works when particle sizes are different.",
        factThree = "Winnowing uses moving air to remove lighter particles.",
        examAnswer = "Handpicking, sieving and winnowing are methods used to separate mixtures based on visible impurities, particle size and weight difference.",
        takeaway = "Choose the separation method by noticing the difference."
    )
    "Sedimentation, decantation, filtration and evaporation" -> SchoolLessonBrief(
        discoverTitle = "Cleaning a mixture step by step",
        hook = "Muddy water becomes clearer if left undisturbed. That simple observation teaches separation.",
        whyItMatters = "these methods help us clean water, separate solids and recover dissolved substances.",
        dailyLink = "Watch soil in a glass of water settle at the bottom. Do this only with dirty water meant for observation, not drinking.",
        simpleMeaning = "Sedimentation lets heavy insoluble solids settle. Decantation pours off the clearer liquid. Filtration uses a filter. Evaporation removes liquid as vapour.",
        analogy = "It is like cleaning a room: first let heavy things settle, then remove what you can, then use a finer tool.",
        keyWords = "sedimentation, decantation, filtration, evaporation, residue",
        memoryTrick = "Settle, pour, filter, evaporate.",
        homeExample = "A tea strainer filters tea leaves from tea.",
        schoolExample = "Sand can be separated from water by filtration.",
        natureExample = "River water slows down in ponds, and heavier mud can settle.",
        activity = "Draw four boxes showing muddy water settling, pouring, filtering and evaporating salt water.",
        prediction = "Which method can get salt back from salt water?",
        reason = "Evaporation removes water as vapour and leaves salt behind.",
        misconception = "Filtering cannot remove dissolved salt because salt particles are spread through water.",
        factOne = "Sedimentation allows heavy solids to settle.",
        factTwo = "Filtration separates insoluble solids from liquids.",
        factThree = "Evaporation can recover a dissolved solid.",
        examAnswer = "Sedimentation, decantation, filtration and evaporation are separation methods based on settling, pouring, filtering and removal of solvent.",
        takeaway = "A mixture becomes easy when you separate it one difference at a time."
    )
    "Reversible and irreversible changes" -> SchoolLessonBrief(
        discoverTitle = "Some changes can come back",
        hook = "Ice can melt into water and freeze again. But a burnt matchstick cannot become new again.",
        whyItMatters = "knowing the type of change helps us understand cooking, melting, drying, burning and rusting.",
        dailyLink = "Find one change at home that can be reversed and one that cannot be reversed easily.",
        simpleMeaning = "A reversible change can be undone to get back the original substance. An irreversible change cannot be undone easily.",
        analogy = "A reversible change is like opening and closing a door. An irreversible change is like tearing paper into tiny pieces.",
        keyWords = "change, reversible, irreversible, melting, burning",
        memoryTrick = "Reversible returns. Irreversible does not return easily.",
        homeExample = "Melting butter can often be reversed by cooling. Cooking an egg cannot be reversed.",
        schoolExample = "Folding paper is reversible, but burning paper is irreversible.",
        natureExample = "Water changes to ice and back again in cold places.",
        activity = "Make two columns: changes that can come back and changes that cannot come back easily.",
        prediction = "Can water from melted ice become ice again?",
        reason = "Yes. Cooling removes heat, so water can freeze again.",
        misconception = "Every physical-looking change is not always reversible; check whether the original thing can really come back.",
        factOne = "Reversible changes can be undone.",
        factTwo = "Irreversible changes cannot be undone easily.",
        factThree = "Melting and freezing of water are reversible changes.",
        examAnswer = "A reversible change can be reversed to get the original substance back. An irreversible change cannot be reversed easily.",
        takeaway = "Ask one question: can the original thing come back?"
    )
    "Natural indicators and litmus" -> SchoolLessonBrief(
        discoverTitle = "Colours that test substances",
        hook = "Some plant colours change when they touch acidic or basic substances.",
        whyItMatters = "indicators help us identify acids and bases without tasting unsafe substances.",
        dailyLink = "Think of turmeric stains changing colour when soap touches them.",
        simpleMeaning = "An indicator is a substance that changes colour in acids or bases. Litmus is a common indicator.",
        analogy = "An indicator is like a traffic light for acids and bases. The colour gives a signal.",
        keyWords = "indicator, litmus, acid, base, colour change",
        memoryTrick = "Blue to red means acid; red to blue means base.",
        homeExample = "Turmeric can show a colour change with basic soap solution.",
        schoolExample = "Litmus paper is used to test whether a solution is acidic or basic.",
        natureExample = "China rose and red cabbage contain natural colours that can act as indicators.",
        activity = "Draw litmus paper before and after touching an acid and a base. Do not taste test liquids.",
        prediction = "What happens to blue litmus in vinegar?",
        reason = "Vinegar is acidic, so it turns blue litmus red.",
        misconception = "Colourful liquid is not automatically an indicator. It must show a useful colour change.",
        factOne = "Indicators show acids and bases by colour change.",
        factTwo = "Litmus is a common indicator.",
        factThree = "Acids turn blue litmus red.",
        examAnswer = "Indicators are substances that show whether a solution is acidic or basic by changing colour. Litmus is one common indicator.",
        takeaway = "Indicators let colour do the testing."
    )
    "Neutralisation in daily life" -> SchoolLessonBrief(
        discoverTitle = "When acid and base calm each other",
        hook = "An antacid tablet can reduce acidity in the stomach. This is a daily-life clue to neutralisation.",
        whyItMatters = "neutralisation is used in digestion, soil treatment, insect stings and wastewater treatment.",
        dailyLink = "Notice antacids, toothpaste, lime added to acidic soil, and treatment of factory waste.",
        simpleMeaning = "Neutralisation happens when an acid and a base react and reduce each other's effect, usually forming salt and water.",
        analogy = "It is like mixing hot and cold water to make it closer to normal temperature.",
        keyWords = "acid, base, neutralisation, salt, water",
        memoryTrick = "Acid plus base gives salt plus water.",
        homeExample = "Antacids are basic and help reduce extra stomach acid.",
        schoolExample = "A teacher may mix dilute acid and base with indicator to show colour moving toward neutral.",
        natureExample = "Farmers may add lime to acidic soil so plants can grow better.",
        activity = "Write two daily uses of neutralisation and identify which substance is acidic or basic.",
        prediction = "Why can a base reduce the effect of an acid?",
        reason = "Acid particles and base particles react, so their strong effects decrease.",
        misconception = "Neutralisation does not mean both substances disappear without products; salt and water can form.",
        factOne = "Neutralisation is a reaction between acid and base.",
        factTwo = "It usually forms salt and water.",
        factThree = "Antacids use neutralisation in daily life.",
        examAnswer = "Neutralisation is the reaction between an acid and a base to form salt and water, reducing acidic or basic nature.",
        takeaway = "Neutralisation is chemistry that brings balance."
    )
    "Rusting of iron" -> SchoolLessonBrief(
        discoverTitle = "The brown coat on iron",
        hook = "An old iron gate may turn reddish-brown after rain. That brown layer is rust.",
        whyItMatters = "rusting damages bridges, vehicles, tools, gates and machines.",
        dailyLink = "Look for rust on an old nail, gate, bicycle chain or iron stand.",
        simpleMeaning = "Rusting is a slow chemical change in which iron reacts with oxygen and water to form rust.",
        analogy = "Rust is like a weak, flaky coat that forms on iron when air and water keep touching it.",
        keyWords = "rusting, iron, oxygen, water, corrosion",
        memoryTrick = "Iron plus air plus water gives rust.",
        homeExample = "Wet iron tools rust faster if not dried.",
        schoolExample = "Iron nails can be kept in different conditions to test the need for air and water.",
        natureExample = "Humid coastal air can make iron rust faster.",
        activity = "Draw three nails: dry air, boiled water with oil, and moist air. Predict which rusts fastest.",
        prediction = "Will an iron nail rust faster in moist air or dry air?",
        reason = "Moist air has water vapour and oxygen, both needed for rusting.",
        misconception = "Rusting is not just dirt on iron. It is a new substance formed by chemical change.",
        factOne = "Rusting needs iron, oxygen and water.",
        factTwo = "Rust is reddish-brown and flaky.",
        factThree = "Rusting is a chemical change.",
        examAnswer = "Rusting is the slow reaction of iron with oxygen and water to form reddish-brown rust.",
        takeaway = "Keep iron away from water and air to slow rust."
    )
    "Preventing corrosion" -> SchoolLessonBrief(
        discoverTitle = "Protecting metals from damage",
        hook = "Paint on a gate is not only for beauty. It also protects the metal below.",
        whyItMatters = "corrosion wastes money and weakens useful metal objects.",
        dailyLink = "Notice painted gates, oiled tools, stainless steel utensils and galvanised buckets.",
        simpleMeaning = "Corrosion is damage to metals by air, water or chemicals. We prevent it by blocking contact with these substances.",
        analogy = "A coating is like a raincoat for metal.",
        keyWords = "corrosion, coating, painting, oiling, galvanising",
        memoryTrick = "Cover the metal, protect the metal.",
        homeExample = "Oil on bicycle chains helps reduce rusting.",
        schoolExample = "Painting an iron strip protects it from moist air.",
        natureExample = "Salty sea air speeds up corrosion, so coastal metal objects need more protection.",
        activity = "List three metal objects and write how each is protected from corrosion.",
        prediction = "Why does paint reduce rusting of an iron gate?",
        reason = "Paint forms a barrier, so oxygen and water cannot easily touch the iron.",
        misconception = "Wiping rust after it forms is not the same as preventing corrosion.",
        factOne = "Corrosion is damage to metals.",
        factTwo = "Coatings block air and water.",
        factThree = "Painting, oiling and galvanising help prevent corrosion.",
        examAnswer = "Corrosion can be prevented by painting, oiling, greasing, galvanising or using alloys that resist corrosion.",
        takeaway = "Protection works by keeping metal away from attack."
    )
    "Metals and non-metals around us" -> SchoolLessonBrief(
        discoverTitle = "Shiny metals, useful non-metals",
        hook = "A copper wire, iron nail, oxygen in air and carbon in pencil lead all show different types of elements.",
        whyItMatters = "knowing metals and non-metals helps us choose materials for wires, utensils, medicines and fuels.",
        dailyLink = "Find three metal objects and three non-metal examples around you.",
        simpleMeaning = "Metals are usually shiny, hard, sonorous and good conductors. Non-metals usually lack these properties, though there are exceptions.",
        analogy = "Metals and non-metals are like two teams with different common habits.",
        keyWords = "metal, non-metal, conductor, malleable, ductile",
        memoryTrick = "Metals usually shine, ring and conduct.",
        homeExample = "Aluminium foil is a metal; oxygen in air is a non-metal.",
        schoolExample = "Copper wire conducts electricity, while sulfur does not conduct like copper.",
        natureExample = "Iron occurs in ores; oxygen is present in air and water.",
        activity = "Sort ten examples into metal and non-metal. Write one property for each.",
        prediction = "Which is better for electric wires: copper or sulfur?",
        reason = "Copper is a metal and conducts electricity well.",
        misconception = "All solids are not metals. Many non-metals, like sulfur and carbon, can be solid.",
        factOne = "Metals usually conduct heat and electricity.",
        factTwo = "Non-metals usually do not show metallic shine.",
        factThree = "There are exceptions, so observations matter.",
        examAnswer = "Metals and non-metals are groups of elements with different properties. Metals are generally shiny, malleable, ductile and good conductors.",
        takeaway = "Properties help us choose the right element for the job."
    )
    "Reactions of metals with oxygen, water and acids" -> SchoolLessonBrief(
        discoverTitle = "Metals do not all react the same way",
        hook = "Sodium reacts strongly with water, but gold does not. Metals have different reactivities.",
        whyItMatters = "reactivity explains storage, safety, corrosion and why some metals are found as ores.",
        dailyLink = "Think of iron rusting, magnesium ribbon burning, and copper vessels losing shine.",
        simpleMeaning = "Some metals react with oxygen, water or acids to form new substances. More reactive metals react faster or more strongly.",
        analogy = "Reactivity is like eagerness. Some metals are very eager to react; some are calm.",
        keyWords = "metal, oxygen, water, acid, reactivity",
        memoryTrick = "Metal reactions depend on the metal and the condition.",
        homeExample = "Iron reacts slowly with oxygen and water to form rust.",
        schoolExample = "Magnesium burns in oxygen to form a white ash called magnesium oxide.",
        natureExample = "Reactive metals are usually found in compounds, not as free metals.",
        activity = "Make a table: metal, reacts with oxygen, reacts with water, reacts with acid.",
        prediction = "Will all metals react with cold water at the same speed?",
        reason = "No. Metals have different reactivities, so their reactions differ.",
        misconception = "Do not assume every metal reacts violently. Reactivity changes from metal to metal.",
        factOne = "Metals can react with oxygen to form oxides.",
        factTwo = "Some metals react with water or acids.",
        factThree = "Reactivity decides how strong the reaction is.",
        examAnswer = "Metals differ in reactivity. Some react with oxygen, water or acids to form new substances such as metal oxides, hydroxides or salts.",
        takeaway = "The same test can show different metal behaviours."
    )
    "Conductors and insulators in liquids" -> SchoolLessonBrief(
        discoverTitle = "Can a liquid carry current?",
        hook = "A metal wire conducts electricity, but liquids must be tested carefully to know if they conduct.",
        whyItMatters = "this helps explain batteries, electrolysis, electroplating and electrical safety near water.",
        dailyLink = "Think about why wet hands near switches are dangerous, especially when dissolved salts are present.",
        simpleMeaning = "A conductor allows electric current to pass. An insulator does not allow it easily. Some liquids conduct when they contain dissolved ions.",
        analogy = "Current needs moving helpers. In wires, electrons help; in liquids, ions help.",
        keyWords = "conductor, insulator, liquid, ion, current",
        memoryTrick = "Ions in liquid help current move.",
        homeExample = "Salt water conducts better than pure water.",
        schoolExample = "A simple LED tester can compare liquids under teacher supervision.",
        natureExample = "Sea water conducts electricity better than rainwater because it has more dissolved salts.",
        activity = "Draw a battery, LED and two electrodes dipped in salt water. Label the circuit parts.",
        prediction = "Will sugar water conduct as well as salt water?",
        reason = "Sugar dissolves but does not make many ions, so it conducts poorly compared with salt water.",
        misconception = "All liquids are not good conductors. Dissolved ions are important.",
        factOne = "Conductors allow current to pass.",
        factTwo = "Insulators do not allow current easily.",
        factThree = "Liquids conduct better when they contain ions.",
        examAnswer = "Liquids conduct electricity when they contain movable ions. Liquids without enough ions are poor conductors.",
        takeaway = "For liquids, current follows ions."
    )
    "Electrolytes and simple testers" -> SchoolLessonBrief(
        discoverTitle = "Testing liquids with a circuit",
        hook = "A small LED can glow when a liquid lets current pass through it.",
        whyItMatters = "simple testers help us compare liquids safely and understand electrolytes.",
        dailyLink = "Salt water, lemon water and tap water behave differently because they contain different dissolved substances.",
        simpleMeaning = "An electrolyte is a liquid or solution that conducts electricity because it has movable ions. A tester shows whether current passes.",
        analogy = "A tester is like a yes-or-no signal. Glow means current found a path.",
        keyWords = "electrolyte, tester, electrode, ion, circuit",
        memoryTrick = "Electrolyte equals electric path through ions.",
        homeExample = "Lemon water conducts because it contains ions from acids and salts.",
        schoolExample = "A teacher may use an LED tester to compare distilled water, tap water and salt water.",
        natureExample = "Minerals dissolved in natural water can make it conduct electricity.",
        activity = "Do not test mains electricity. Draw a safe battery tester and predict which liquids make the LED glow.",
        prediction = "Which will glow brighter: distilled water or salt water?",
        reason = "Salt water has more ions, so it usually allows more current.",
        misconception = "A liquid can look clear and still conduct electricity if ions are present.",
        factOne = "Electrolytes contain movable ions.",
        factTwo = "A tester shows whether current passes.",
        factThree = "Under the same testing conditions, greater conductivity may produce a brighter glow.",
        examAnswer = "An electrolyte conducts electricity due to movable ions. A simple tester with a cell, LED and electrodes can compare conductivity of liquids.",
        takeaway = "A glowing LED can reveal invisible ions."
    )
    "Simple electroplating for class 8" -> SchoolLessonBrief(
        discoverTitle = "Coating with electricity",
        hook = "Some objects get a shiny metal coating using electricity and a metal salt solution.",
        whyItMatters = "electroplating improves appearance, reduces corrosion and makes cheaper objects look better.",
        dailyLink = "Look at shiny taps, medals, jewellery or cycle parts that may have a thin coating.",
        simpleMeaning = "Electroplating is depositing a thin layer of one metal on another object using electric current.",
        analogy = "It is like painting with metal, but the brush is electric current.",
        keyWords = "electroplating, metal coating, electrode, electrolyte, current",
        memoryTrick = "Plate means coat; electro means electricity.",
        homeExample = "Chromium-plated taps are shiny and resist rust better.",
        schoolExample = "A diagram can show an object connected to the negative terminal in a plating setup.",
        natureExample = "Nature causes corrosion; electroplating is a human method to protect surfaces.",
        activity = "Draw the setup: battery, metal plate, object, electrolyte and wires. Label the object to be plated.",
        prediction = "Why should the object be cleaned before electroplating?",
        reason = "A clean surface helps the new metal layer stick evenly.",
        misconception = "Electroplating gives only a thin coating; it does not turn the whole object into that metal.",
        factOne = "Electroplating uses electric current.",
        factTwo = "It deposits a thin metal layer.",
        factThree = "It can improve shine and protection.",
        examAnswer = "Electroplating is the process of coating an object with a thin layer of metal using electric current.",
        takeaway = "Electricity can place a thin metal coat on a surface."
    )
    "Evaporation and diffusion" -> SchoolLessonBrief(
        discoverTitle = "Smell and drying both spread",
        hook = "Wet clothes dry in sunlight, and the smell of perfume spreads across a room.",
        whyItMatters = "evaporation and diffusion explain drying, cooling, smell, gases mixing and many daily observations.",
        dailyLink = "Notice how wet hair dries faster under a fan and how food smell reaches another room.",
        simpleMeaning = "Evaporation is slow change of liquid into vapour from the surface. Diffusion is spreading of particles from a crowded region to a less crowded region.",
        analogy = "Diffusion is like students spreading in an empty playground. Evaporation is like only the top particles leaving first.",
        keyWords = "evaporation, diffusion, vapour, particles, surface",
        memoryTrick = "Evaporation leaves from surface; diffusion spreads everywhere.",
        homeExample = "Sweat evaporates from skin and cools the body.",
        schoolExample = "A drop of ink slowly spreads in water without shaking.",
        natureExample = "Water evaporates from ponds and helps form water vapour in air.",
        activity = "Place one wet cloth in shade and one near moving air. Compare drying time safely.",
        prediction = "Will wet clothes dry faster on a windy day?",
        reason = "Moving air carries away water vapour, so more water can evaporate from the cloth.",
        misconception = "Evaporation is not the same as boiling. Evaporation can happen below boiling point.",
        factOne = "Evaporation happens at the surface of a liquid.",
        factTwo = "Diffusion means particles spread out.",
        factThree = "Heat, wind and surface area can increase evaporation.",
        examAnswer = "Evaporation is the change of a liquid into vapour from its surface. Diffusion is the spreading of particles from higher concentration to lower concentration.",
        takeaway = "Particles are always moving, even when we cannot see them."
    )
    "Sublimation and latent heat" -> SchoolLessonBrief(
        discoverTitle = "A solid can skip liquid",
        hook = "Camphor slowly becomes vapour and disappears without first becoming a visible liquid.",
        whyItMatters = "sublimation and latent heat explain unusual state changes and why heat can hide during melting or boiling.",
        dailyLink = "Think of camphor, naphthalene balls and ice needing heat to melt.",
        simpleMeaning = "Sublimation is change from solid directly to gas. Latent heat is heat used during a state change without showing a temperature rise.",
        analogy = "Latent heat is like effort spent on changing dress, not on growing taller.",
        keyWords = "sublimation, latent heat, solid, gas, state change",
        memoryTrick = "Sublimation skips liquid. Latent heat hides in change.",
        homeExample = "Camphor used in homes slowly becomes vapour.",
        schoolExample = "Heating ammonium chloride can show sublimation under teacher supervision.",
        natureExample = "Ice and snow can slowly change to vapour in very cold dry places.",
        activity = "Draw three arrows: solid to liquid, liquid to gas, and solid directly to gas.",
        prediction = "During melting, does temperature always rise while heat is supplied?",
        reason = "During melting, heat is used to change state, so temperature can stay constant for some time.",
        misconception = "If temperature does not rise, heat is not wasted. It may be used for state change.",
        factOne = "Sublimation changes solid directly into gas.",
        factTwo = "Latent heat is used during change of state.",
        factThree = "Temperature may stay constant during melting or boiling.",
        examAnswer = "Sublimation is the direct change of solid into gas. Latent heat is the heat absorbed or released during change of state without change in temperature.",
        takeaway = "Heat can change state even when the thermometer looks steady."
    )
    "Solutions, suspensions and colloids" -> SchoolLessonBrief(
        discoverTitle = "Not every mixture is the same",
        hook = "Salt water, muddy water and milk all look like mixtures, but they behave differently.",
        whyItMatters = "this helps us understand medicines, drinks, paints, smoke, fog and water cleaning.",
        dailyLink = "Compare salt water, flour in water and milk. Notice whether particles settle or scatter light.",
        simpleMeaning = "A solution is a uniform mixture. A suspension has larger particles that can settle. A colloid has tiny particles that do not settle easily but can scatter light.",
        analogy = "A solution is fully mixed, a suspension stands apart, and a colloid is in between.",
        keyWords = "solution, suspension, colloid, solute, particles",
        memoryTrick = "Solution clear, suspension settles, colloid scatters.",
        homeExample = "Salt water is a solution. Muddy water is a suspension. Milk is a colloid.",
        schoolExample = "Chalk powder in water settles after some time.",
        natureExample = "Fog is a colloid of tiny water droplets in air.",
        activity = "Make a comparison table for salt water, muddy water and milk.",
        prediction = "Which mixture will settle faster: muddy water or salt water?",
        reason = "Mud particles are large and insoluble, so they settle. Salt particles dissolve.",
        misconception = "A clear mixture is not always pure; it can be a solution.",
        factOne = "Solutions are uniform mixtures.",
        factTwo = "Suspension particles can settle.",
        factThree = "Colloids scatter light but do not settle quickly.",
        examAnswer = "Solutions, suspensions and colloids are mixtures that differ in particle size, uniformity, settling and light scattering.",
        takeaway = "Mixtures can look simple but behave in different ways."
    )
    "Tyndall effect and concentration" -> SchoolLessonBrief(
        discoverTitle = "Light can reveal hidden particles",
        hook = "A beam of sunlight becomes visible in dusty air because tiny particles scatter the light.",
        whyItMatters = "Tyndall effect helps identify colloids, and concentration tells how much solute is present.",
        dailyLink = "Notice light beams in fog, dusty rooms or smoke.",
        simpleMeaning = "Tyndall effect is scattering of light by colloid particles. Concentration tells the amount of solute in a given amount of solution.",
        analogy = "Tiny particles act like small mirrors that throw light toward our eyes.",
        keyWords = "Tyndall effect, colloid, concentration, solute, solution",
        memoryTrick = "Tyndall tells tiny particles are there.",
        homeExample = "Headlight beams become visible in fog because droplets scatter light.",
        schoolExample = "Milk diluted with water can scatter a torch beam better than salt solution.",
        natureExample = "Sun rays can become visible through mist in the morning.",
        activity = "Draw a torch beam through salt solution and through a colloid. Mark where light is visible.",
        prediction = "Will a true salt solution show a strong Tyndall effect?",
        reason = "Salt solution particles are too small to scatter light strongly.",
        misconception = "Tyndall effect is not just brightness; it is light scattering by tiny particles.",
        factOne = "Colloids show Tyndall effect.",
        factTwo = "True solutions usually do not show it clearly.",
        factThree = "Concentration tells how much solute is in solution.",
        examAnswer = "Tyndall effect is scattering of light by colloidal particles. Concentration is the amount of solute present in a given amount of solution.",
        takeaway = "A light beam can make invisible particles noticeable."
    )
    "Separating mixtures in class 9" -> SchoolLessonBrief(
        discoverTitle = "Choose the right separation tool",
        hook = "Salt, sand, water, ink and cream cannot all be separated by one method.",
        whyItMatters = "separation methods help purify water, prepare medicines, test samples and use materials properly.",
        dailyLink = "Think of tea straining, butter from curd, salt from seawater and sorting grains.",
        simpleMeaning = "Mixtures are separated by using differences in properties such as size, solubility, boiling point, magnetism and density.",
        analogy = "Separation is like choosing the correct key for a lock. Each mixture needs the right method.",
        keyWords = "filtration, evaporation, distillation, chromatography, centrifugation",
        memoryTrick = "Find the difference, then choose the method.",
        homeExample = "A strainer separates tea leaves from tea.",
        schoolExample = "Chromatography can separate colours in ink.",
        natureExample = "Salt can be obtained from seawater by evaporation in salt pans.",
        activity = "Match mixtures with methods: sand-water, salt-water, ink colours, iron-sulfur.",
        prediction = "Which method separates ink colours on paper?",
        reason = "Chromatography works because colours move differently with the solvent on paper.",
        misconception = "Filtration cannot separate dissolved salt from water.",
        factOne = "Different mixtures need different separation methods.",
        factTwo = "Evaporation can recover dissolved solid.",
        factThree = "Chromatography separates colours or dissolved substances.",
        examAnswer = "Mixtures are separated by methods such as filtration, evaporation, distillation, chromatography and centrifugation, depending on differences in properties.",
        takeaway = "First study the mixture, then pick the method."
    )
    "Laws of chemical combination" -> SchoolLessonBrief(
        discoverTitle = "Reactions follow fixed rules",
        hook = "When substances react, atoms do not vanish or appear from nowhere.",
        whyItMatters = "these laws help us write formulae, balance equations and trust measurements.",
        dailyLink = "When a candle burns, products form, but matter is not magically lost.",
        simpleMeaning = "The law of conservation of mass says mass is conserved in a chemical reaction. The law of constant proportions says a pure compound has elements in a fixed ratio.",
        analogy = "A reaction is like rearranging building blocks. Blocks move, but the number of blocks stays the same.",
        keyWords = "conservation, mass, constant proportion, compound, ratio",
        memoryTrick = "Mass stays; compound ratio stays.",
        homeExample = "Cooking changes food, but matter changes form rather than disappearing.",
        schoolExample = "Balanced equations show equal atoms on both sides.",
        natureExample = "Rusting changes iron into rust by combining with oxygen and water.",
        activity = "Count atoms on both sides of a simple balanced equation.",
        prediction = "Can a balanced chemical equation have fewer atoms after reaction?",
        reason = "No. Atoms rearrange, so each type of atom must be conserved.",
        misconception = "Gas escaping can make mass seem lost in an open container, but matter has moved away.",
        factOne = "Mass is conserved in chemical reactions.",
        factTwo = "A pure compound has a fixed composition.",
        factThree = "Balancing equations follows conservation of atoms.",
        examAnswer = "Laws of chemical combination include conservation of mass and constant proportions. They show that matter is conserved and compounds have fixed composition.",
        takeaway = "Chemical reactions rearrange atoms; they do not create or destroy them."
    )
    "Dalton atomic theory at school level" -> SchoolLessonBrief(
        discoverTitle = "Tiny particles explain big changes",
        hook = "We cannot see atoms with our eyes, but many experiments make sense if matter is made of tiny particles.",
        whyItMatters = "Dalton's theory helped students explain elements, compounds and chemical reactions.",
        dailyLink = "Think of sugar crystals. Crushing them makes smaller pieces, but the idea of tiny particles helps explain matter.",
        simpleMeaning = "Dalton said matter is made of tiny particles called atoms. Atoms of one element are alike, and atoms combine to form compounds.",
        analogy = "Atoms are like letters. Different letters join in fixed ways to make different words.",
        keyWords = "atom, element, compound, Dalton, particle",
        memoryTrick = "Atoms join to make compounds.",
        homeExample = "Water is made of hydrogen and oxygen atoms joined in a fixed way.",
        schoolExample = "Models with balls can show atoms combining to form molecules.",
        natureExample = "Air contains different gases made of tiny particles.",
        activity = "Use circles to draw two oxygen atoms and two hydrogen atoms joining to form water molecules.",
        prediction = "If compounds form from atoms, should atoms be counted in equations?",
        reason = "Yes. Chemical equations count atoms because reactions rearrange atoms.",
        misconception = "Dalton's model was important, but later discoveries showed atoms have smaller particles inside.",
        factOne = "Matter is made of atoms.",
        factTwo = "Atoms combine to form compounds.",
        factThree = "Chemical reactions rearrange atoms.",
        examAnswer = "Dalton's atomic theory states that matter is made of atoms and atoms combine in fixed ratios to form compounds.",
        takeaway = "Atoms are tiny, but they explain large chemical changes."
    )
    "Atomic mass and molecular mass" -> SchoolLessonBrief(
        discoverTitle = "Weighing tiny particles",
        hook = "Atoms are too small to weigh one by one on a school balance, so chemists use relative masses.",
        whyItMatters = "atomic and molecular masses help us calculate formula mass, moles and quantities in reactions.",
        dailyLink = "Just as a shopkeeper counts items by packets, chemists handle atoms using relative mass and moles.",
        simpleMeaning = "Atomic mass is the relative mass of an atom. Molecular mass is the sum of atomic masses of all atoms in a molecule.",
        analogy = "A molecule is like a fruit basket. Its mass is found by adding the masses of all fruits inside.",
        keyWords = "atomic mass, molecular mass, formula, atom, molecule",
        memoryTrick = "Molecular mass means add all atoms in the formula.",
        homeExample = "A water molecule has two hydrogen atoms and one oxygen atom.",
        schoolExample = "For H2O, molecular mass is 2 times hydrogen plus 1 oxygen.",
        natureExample = "Oxygen gas molecules are O2, so their molecular mass uses two oxygen atoms.",
        activity = "Calculate molecular mass of CO2 using C = 12 and O = 16.",
        prediction = "Will CO2 be heavier than O2 by relative molecular mass?",
        reason = "CO2 has one carbon and two oxygen atoms, so its relative mass is 44, while O2 is 32.",
        misconception = "Do not forget the small number in a formula. H2 means two hydrogen atoms.",
        factOne = "Atomic mass is relative mass of one atom.",
        factTwo = "Molecular mass is the sum of atomic masses.",
        factThree = "Subscripts in formulae must be counted.",
        examAnswer = "Molecular mass is calculated by adding the atomic masses of all atoms present in one molecule.",
        takeaway = "Read the formula carefully, then add."
    )
    "Valency and writing formulae" -> SchoolLessonBrief(
        discoverTitle = "Atoms combine with combining power",
        hook = "Sodium and chlorine form NaCl, but magnesium and chlorine form MgCl2. The difference comes from valency.",
        whyItMatters = "valency helps us write correct chemical formulae without guessing.",
        dailyLink = "Common salt has formula NaCl because sodium and chlorine combine in a one-to-one ratio.",
        simpleMeaning = "Valency is the combining capacity of an atom or ion. Chemical formulae show how atoms or ions combine in a compound.",
        analogy = "Valency is like the number of hands an atom uses to hold other atoms.",
        keyWords = "valency, formula, ion, compound, combining capacity",
        memoryTrick = "Criss-cross valencies, then simplify if needed.",
        homeExample = "Water is H2O because oxygen has valency 2 and hydrogen has valency 1.",
        schoolExample = "Magnesium chloride is MgCl2 because magnesium has valency 2 and chlorine has valency 1.",
        natureExample = "Minerals have fixed formulae because atoms combine in fixed ratios.",
        activity = "Write formulae for sodium chloride, magnesium oxide and aluminium oxide using valencies.",
        prediction = "What formula forms from Mg with valency 2 and Cl with valency 1?",
        reason = "Two chlorine atoms are needed to balance magnesium's combining capacity, so the formula is MgCl2.",
        misconception = "Do not write charges inside the final neutral formula unless showing ions separately.",
        factOne = "Valency means combining capacity.",
        factTwo = "Formulae show fixed ratios of atoms or ions.",
        factThree = "Criss-cross method helps write many formulae.",
        examAnswer = "Valency is the combining capacity of an element. Chemical formulae are written by balancing valencies of combining atoms or ions.",
        takeaway = "Valency turns formula writing from guessing into logic."
    )
    "Mole concept introduction" -> SchoolLessonBrief(
        discoverTitle = "Counting atoms by packets",
        hook = "We buy eggs by dozen because they are many. Chemists count particles by mole because atoms are tiny and many.",
        whyItMatters = "mole concept connects tiny atoms with measurable mass in the laboratory.",
        dailyLink = "A dozen means 12 items. A mole means a very large fixed number of particles.",
        simpleMeaning = "One mole is 6.022 × 10²³ particles. Molar mass is the mass of one mole of a substance in grams.",
        analogy = "Mole is a counting packet, just like dozen, but much larger.",
        keyWords = "mole, Avogadro number, particle, molar mass, gram",
        memoryTrick = "Mole counts particles; molar mass weighs one mole.",
        homeExample = "A dozen bananas means 12 bananas, whatever their size.",
        schoolExample = "18 g of water is one mole of water molecules.",
        natureExample = "Even a small drop of water contains an enormous number of molecules.",
        activity = "Compare dozen and mole in a two-column table.",
        prediction = "Why do chemists not count atoms one by one?",
        reason = "Atoms are extremely tiny and present in huge numbers, so mole makes counting practical.",
        misconception = "Mole is a number of particles, not only a mass.",
        factOne = "One mole contains 6.022 × 10²³ particles.",
        factTwo = "Molar mass is mass of one mole.",
        factThree = "Mole links particles and grams.",
        examAnswer = "A mole is the amount of substance containing 6.022 × 10²³ particles. Molar mass is the mass of one mole in grams.",
        takeaway = "Mole is the chemist's counting packet."
    )
    "Chemical equations and conservation" -> SchoolLessonBrief(
        discoverTitle = "Equations tell reaction stories",
        hook = "A chemical equation is a short way to show reactants changing into products.",
        whyItMatters = "balanced equations prove that atoms are conserved during chemical reactions.",
        dailyLink = "Burning LPG, rusting iron and baking all involve substances changing into new substances.",
        simpleMeaning = "A chemical equation uses formulae to show reactants and products. A balanced equation has equal numbers of each atom on both sides.",
        analogy = "Balancing is like keeping the same number of building blocks before and after making a new shape.",
        keyWords = "reactant, product, equation, coefficient, balanced",
        memoryTrick = "Count atoms, change coefficients, never change formulae.",
        homeExample = "Cooking gas burns with oxygen to form carbon dioxide and water.",
        schoolExample = "2H2 + O2 gives 2H2O is balanced because H and O atoms match.",
        natureExample = "Photosynthesis and respiration can be represented by chemical equations.",
        activity = "Count atoms in H2 + O2 gives H2O, then balance it using coefficients.",
        prediction = "Can we change H2O to H2O2 just to balance an equation?",
        reason = "No. Changing formula changes the substance. We balance by changing coefficients.",
        misconception = "Balancing does not mean changing subscripts inside formulae.",
        factOne = "Reactants are starting substances.",
        factTwo = "Products are new substances formed.",
        factThree = "Balanced equations conserve atoms.",
        examAnswer = "A balanced chemical equation has equal numbers of each type of atom on the reactant and product sides.",
        takeaway = "Balance atoms by coefficients, not by changing substances."
    )
    "Important salts in daily life" -> SchoolLessonBrief(
        discoverTitle = "Useful salts at home",
        hook = "Baking soda, washing soda and plaster of Paris are chemicals with everyday uses.",
        whyItMatters = "these salts appear in cleaning, cooking, construction, medicine and disinfection.",
        dailyLink = "Notice baking soda in kitchens, washing soda in cleaning and plaster in wall repair or casts.",
        simpleMeaning = "Important salts are ionic compounds used in daily life, such as baking soda, washing soda, bleaching powder and plaster of Paris.",
        analogy = "Salts are like different tools in a toolbox. Each has a special use.",
        keyWords = "baking soda, washing soda, bleaching powder, plaster of Paris, salt",
        memoryTrick = "Bake, wash, bleach, plaster: four salts, four uses.",
        homeExample = "Baking soda helps make cakes soft and can reduce acidity.",
        schoolExample = "Plaster of Paris sets with water to form a hard solid.",
        natureExample = "Common salt is found in seawater and salt deposits.",
        activity = "Make a table with salt name, formula if known, and one use.",
        prediction = "Why is bleaching powder used in water treatment?",
        reason = "It helps disinfect water by killing many germs.",
        misconception = "All salts are not common salt. Common salt is only one example.",
        factOne = "Baking soda is sodium hydrogen carbonate.",
        factTwo = "Washing soda is used in cleaning.",
        factThree = "Plaster of Paris sets into a hard mass with water.",
        examAnswer = "Important salts such as baking soda, washing soda, bleaching powder and plaster of Paris have useful applications in daily life.",
        takeaway = "A salt's use depends on its chemical properties."
    )
    "Metals, metallurgy and corrosion" -> SchoolLessonBrief(
        discoverTitle = "From ore to useful metal",
        hook = "Iron in a bridge and aluminium in foil usually begin as minerals in the Earth.",
        whyItMatters = "metallurgy explains how metals are obtained, purified and protected from corrosion.",
        dailyLink = "Look at utensils, wires, gates and vehicles. Metals must be extracted and protected.",
        simpleMeaning = "Metallurgy is the process of obtaining metals from ores. Corrosion is slow damage of metals by air, water or chemicals.",
        analogy = "Ore is like a mixed bag. Metallurgy separates and cleans the useful metal.",
        keyWords = "ore, gangue, metallurgy, reduction, corrosion",
        memoryTrick = "Ore to metal: concentrate, reduce, refine.",
        homeExample = "Iron gates are painted to reduce corrosion.",
        schoolExample = "A flow chart can show ore concentration, reduction and refining.",
        natureExample = "Reactive metals are found mostly as compounds in ores.",
        activity = "Draw a simple ore-to-metal flow chart and add one corrosion prevention method.",
        prediction = "Why is aluminium not found easily as free metal in nature?",
        reason = "Aluminium is reactive, so it occurs mainly in combined form in minerals.",
        misconception = "An ore is not pure metal. It contains useful compound plus unwanted impurities.",
        factOne = "Ores contain useful metal compounds.",
        factTwo = "Metallurgy extracts metals from ores.",
        factThree = "Corrosion damages metals and must be prevented.",
        examAnswer = "Metallurgy is the extraction of metals from ores through steps such as concentration, reduction and refining. Corrosion is gradual damage of metals.",
        takeaway = "Getting metal is only half the job; protecting it is the other half."
    )
    "Carbon bonding and homologous series" -> SchoolLessonBrief(
        discoverTitle = "Carbon makes long families",
        hook = "Cooking gas, petrol, alcohol and vinegar all contain carbon compounds, but their properties differ.",
        whyItMatters = "carbon bonding explains fuels, food molecules, medicines, plastics and soaps.",
        dailyLink = "Notice LPG, candle wax, sugar, vinegar and alcohol-based sanitizer as carbon compound examples.",
        simpleMeaning = "Carbon forms covalent bonds and can join to other carbon atoms. A homologous series is a family of compounds with the same functional group and general formula, similar chemical properties, and successive members differ by –CH₂–.",
        analogy = "Carbon atoms are like clips that can join into chains, branches and rings.",
        keyWords = "carbon, covalent bond, chain, homologous series, functional group",
        memoryTrick = "Carbon can chain; series has same family name.",
        homeExample = "Methane in natural gas and propane in LPG belong to hydrocarbon families.",
        schoolExample = "Alkanes follow a regular pattern in formulae and names.",
        natureExample = "Living things contain many carbon compounds like sugars and proteins.",
        activity = "Write methane, ethane and propane in order and notice how the formula changes.",
        prediction = "Why can carbon form many compounds?",
        reason = "Carbon can form four covalent bonds and bond with other carbon atoms.",
        misconception = "All carbon compounds are not fuels; many are foods, medicines or body molecules.",
        factOne = "Carbon usually forms four covalent bonds.",
        factTwo = "Carbon can form chains and rings.",
        factThree = "A homologous series has the same functional group and general formula; successive members differ by –CH₂–.",
        examAnswer = "Carbon forms many compounds because it is tetravalent and shows catenation. A homologous series has the same functional group and general formula, similar chemical properties, and successive members differ by –CH₂–.",
        takeaway = "Carbon is small, but its bonding choices are huge."
    )
    "Ethanol, ethanoic acid, soaps and detergents" -> SchoolLessonBrief(
        discoverTitle = "Carbon compounds in daily life",
        hook = "Sanitizer, vinegar and soap are everyday examples of carbon chemistry.",
        whyItMatters = "these compounds help us understand cleaning, food, smell, acidity and safe use of chemicals.",
        dailyLink = "Notice vinegar in food, sanitizer labels and soap or detergent used for washing.",
        simpleMeaning = "Ethanol is an alcohol, ethanoic acid is the acid in vinegar, and soaps and detergents help remove oily dirt from surfaces.",
        analogy = "Soap molecules act like tiny helpers with one side holding oil and the other side liking water.",
        keyWords = "ethanol, ethanoic acid, soap, detergent, cleaning",
        memoryTrick = "Ethanol in sanitizer, ethanoic acid in vinegar, soap lifts oil.",
        homeExample = "Vinegar has sour taste because it contains ethanoic acid.",
        schoolExample = "Soap solution can form lather and help remove oily stains.",
        natureExample = "Some fruits become sour because they contain acids.",
        activity = "Make a use chart: ethanol, ethanoic acid, soap and detergent. Add one safety note.",
        prediction = "Why does soap help clean oily dirt better than plain water?",
        reason = "Soap has a part that attaches to oil and a part that mixes with water, so oily dirt can be washed away.",
        misconception = "Ethanol is useful but not harmless. It must be handled safely and not consumed carelessly.",
        factOne = "Ethanol is an alcohol used in sanitizers and solvents.",
        factTwo = "Ethanoic acid is present in vinegar.",
        factThree = "Soaps and detergents help remove oily dirt.",
        examAnswer = "Ethanol, ethanoic acid, soaps and detergents are important carbon compounds with daily uses in sanitizing, food and cleaning.",
        takeaway = "Organic chemistry is already in your kitchen and bathroom."
    )
    "Natural and synthetic fibres" -> SchoolLessonBrief(
        discoverTitle = "Your clothes are chemistry",
        hook = "Touch your shirt, towel or school bag. Each one is made of thin thread-like fibres.",
        whyItMatters = "fibres decide whether cloth feels soft, dries fast, stretches or stays warm.",
        dailyLink = "Check one clothing label at home. Find words like cotton, wool, polyester, nylon or rayon.",
        simpleMeaning = "A fibre is a very thin strand. Natural fibres come from plants or animals. Synthetic fibres are made by humans using chemicals.",
        analogy = "Think of fibres like tiny noodles. Many tiny strands twist together to make yarn, and yarn makes cloth.",
        keyWords = "fibre, yarn, fabric, natural, synthetic",
        memoryTrick = "Natural means from nature. Synthetic means made by people.",
        homeExample = "Cotton towels absorb water well. Polyester sports clothes dry faster.",
        schoolExample = "Uniforms often mix cotton and polyester so they are comfortable and easier to wash.",
        natureExample = "Cotton grows around cotton seeds. Wool grows on sheep.",
        activity = "Look at two fabrics. Touch them, drop a little water on each, and note which one absorbs faster.",
        prediction = "If a cloth is cotton, will it absorb more water than a plastic raincoat?",
        reason = "Cotton fibres hold water more easily. Plastic-like fibres often push water away.",
        misconception = "Synthetic does not always mean bad. It means human-made, and it can be useful when chosen properly.",
        factOne = "Fibres are thin strands used to make cloth.",
        factTwo = "Natural fibres come from plants or animals.",
        factThree = "Synthetic fibres are made by people and often dry quickly.",
        examAnswer = "Natural fibres come from plants and animals, such as cotton and wool. Synthetic fibres are made by humans, such as nylon and polyester.",
        takeaway = "Read a cloth label and you are already reading chemistry."
    )
    "Thermoplastics and thermosets" -> SchoolLessonBrief(
        discoverTitle = "Why some plastics bend and some do not",
        hook = "A plastic bottle can change shape when heated, but a cooker handle should not melt in your hand.",
        whyItMatters = "we use different plastics for bottles, switches, handles, lunch boxes and safety items.",
        dailyLink = "Find one bendable plastic item and one hard heat-resistant plastic item at home.",
        simpleMeaning = "Thermoplastics soften on heating and can be reshaped. Thermosets become hard and cannot be remoulded easily after setting.",
        analogy = "Thermoplastic is like wax that can soften again. Thermoset is like a baked cake that cannot become batter again.",
        keyWords = "thermoplastic, thermoset, soften, remould, heat-resistant",
        memoryTrick = "Thermo-plastic can be shaped again. Thermo-set is set forever.",
        homeExample = "Water bottles and plastic bags are usually thermoplastics.",
        schoolExample = "Electrical switches are made from thermosetting plastics because they are heat-resistant and electrical insulators.",
        natureExample = "There is no plastic tree; plastics are human-made materials, so we must use and dispose of them carefully.",
        activity = "Do not heat plastic. Instead, sort safe household plastic items into flexible and hard groups.",
        prediction = "Which should be used for a hot pan handle: thermoplastic or thermoset?",
        reason = "A hot pan handle must resist heat and keep its shape, so thermoset is safer.",
        misconception = "All plastics are not the same. Their uses depend on how they behave with heat.",
        factOne = "Thermoplastics soften on heating.",
        factTwo = "Thermosets do not remould easily after setting.",
        factThree = "Heat-resistant plastics are useful for switches and handles.",
        examAnswer = "Thermoplastics can be softened and reshaped by heating. Thermosetting plastics become hard and cannot be reshaped easily after they are set.",
        takeaway = "The right plastic is chosen by asking: what happens when it gets hot?"
    )
    "Coal and petroleum fractions" -> SchoolLessonBrief(
        discoverTitle = "Fuel has a long journey",
        hook = "Petrol in a vehicle and LPG in a kitchen did not appear suddenly. They come from fuels found under the Earth.",
        whyItMatters = "coal and petroleum give energy, but they are limited and can pollute the air.",
        dailyLink = "Notice where your home uses fuel: cooking gas, transport, electricity or a generator.",
        simpleMeaning = "Coal is a solid fuel formed from ancient plants. Petroleum is a dark liquid mixture found underground. It is separated into useful parts called fractions.",
        analogy = "Petroleum is like a mixed basket. Refining separates it into useful groups, like petrol, diesel, kerosene and bitumen.",
        keyWords = "coal, petroleum, fossil fuel, fraction, refining",
        memoryTrick = "Coal is solid. Petroleum is liquid. Refining separates the liquid mixture.",
        homeExample = "LPG is used for cooking in many homes.",
        schoolExample = "A chart of petroleum fractions shows petrol, diesel, kerosene and lubricating oil.",
        natureExample = "Fossil fuels formed from ancient living things over a very long time.",
        activity = "Make a two-column list: fuels we use daily and ways to save them.",
        prediction = "If we waste petrol today, can it be replaced quickly by nature?",
        reason = "Fossil fuels take millions of years to form, so they are called exhaustible resources.",
        misconception = "Petroleum is not one pure substance. It is a mixture that is separated into fractions.",
        factOne = "Coal is a solid fossil fuel.",
        factTwo = "Petroleum is a liquid mixture.",
        factThree = "Refining separates petroleum into useful fractions.",
        examAnswer = "Coal and petroleum are fossil fuels formed from ancient living matter. Petroleum is separated in refineries into fractions such as petrol, diesel, kerosene and lubricating oil.",
        takeaway = "Saving fuel is also a chemistry decision."
    )
    "Combustion and flame" -> SchoolLessonBrief(
        discoverTitle = "A flame tells a story",
        hook = "A candle flame, gas stove and matchstick all show combustion, but they do not all burn in the same way.",
        whyItMatters = "burning gives heat and light, but unsafe burning can cause fire, smoke and harmful gases.",
        dailyLink = "Observe a candle or gas stove only with an adult nearby. Notice colour, heat and smoke.",
        simpleMeaning = "Combustion is burning. A substance reacts with oxygen and gives heat. Sometimes it also gives light.",
        analogy = "A fire is like a three-legged stool: fuel, oxygen and enough heat. Remove one leg and it stops.",
        keyWords = "combustion, fuel, oxygen, ignition temperature, flame",
        memoryTrick = "Fire needs F-O-H: Fuel, Oxygen, Heat.",
        homeExample = "LPG burns in oxygen to cook food.",
        schoolExample = "A candle flame has different zones with different temperatures.",
        natureExample = "Forest fires spread faster when dry leaves, air and heat are all present.",
        activity = "Draw the fire triangle and write one safe way to remove each side.",
        prediction = "What happens if a burning candle is covered with a glass jar?",
        reason = "The flame goes out when oxygen inside the jar falls below the concentration required to sustain combustion.",
        misconception = "Oxygen itself is not the fuel. Oxygen helps the fuel burn.",
        factOne = "Combustion is burning in oxygen.",
        factTwo = "Fire needs fuel, oxygen and heat.",
        factThree = "A flame can have different hot and less-hot zones.",
        examAnswer = "Combustion is a chemical process in which a substance burns in oxygen and produces heat and usually light.",
        takeaway = "Control fuel, oxygen or heat, and you control fire."
    )
    "Fuel efficiency and pollution" -> SchoolLessonBrief(
        discoverTitle = "Good fuel, cleaner air",
        hook = "Two vehicles may travel the same distance but use different amounts of fuel and produce different smoke.",
        whyItMatters = "efficient fuels save money and reduce air pollution.",
        dailyLink = "Notice smoke from vehicles, generators or burning waste. Clean burning matters for health.",
        simpleMeaning = "Fuel efficiency is the fraction or percentage of input energy converted into useful output. Pollution means harmful substances enter air, water or land.",
        analogy = "Efficient fuel use is like using a sharp pencil carefully. Wasteful fuel use is like sharpening the pencil again and again.",
        keyWords = "fuel efficiency, pollution, smoke, carbon dioxide, harmful gases",
        memoryTrick = "Less fuel wasted means less smoke created.",
        homeExample = "Using a pressure cooker can save cooking gas because food cooks faster.",
        schoolExample = "A clean burner flame usually gives less soot than a smoky yellow flame.",
        natureExample = "Polluted air can harm plants, animals and people.",
        activity = "Write three actions that save fuel: walking short distances, switching off engines, and using public transport.",
        prediction = "If a vehicle is serviced regularly, will it usually waste less fuel?",
        reason = "A well-maintained engine burns fuel more completely and produces less smoke.",
        misconception = "A fuel is not good only because it burns strongly. We also check smoke, cost, safety and pollution.",
        factOne = "Efficient fuel gives more useful energy with less waste.",
        factTwo = "Incomplete burning can produce smoke and soot.",
        factThree = "Saving fuel helps reduce pollution.",
        examAnswer = "Fuel efficiency is the fraction or percentage of input energy converted into useful output. A good fuel is efficient, safe, affordable and causes less pollution.",
        takeaway = "The cleanest fuel is the fuel we do not waste."
    )
    "Modern periodic law" -> SchoolLessonBrief(
        discoverTitle = "The periodic table has an order",
        hook = "A library is easy to use because books are arranged in order. The periodic table also has an order.",
        whyItMatters = "this order helps us predict properties of elements without memorising every element separately.",
        dailyLink = "Find oxygen, sodium, chlorine and iron on a periodic table. Notice their positions.",
        simpleMeaning = "Modern periodic law says the properties of elements repeat when elements are arranged in increasing atomic number.",
        analogy = "Think of days in a week. Monday returns after every seven days. In the table, similar properties also return in a pattern.",
        keyWords = "atomic number, element, period, group, property",
        memoryTrick = "Mendeleev used atomic mass; the modern table uses atomic number.",
        homeExample = "Salt has sodium and chlorine. Their table positions help explain why they form ions.",
        schoolExample = "Elements in the same group often show similar reactions in demonstrations.",
        natureExample = "Oxygen supports burning, while noble gases are much less reactive because of their outer electrons.",
        activity = "Choose any one group and list two elements from it. Write one property they may share.",
        prediction = "If two elements are in the same group, will they usually behave somewhat similarly?",
        reason = "Elements in one group have a similar number of outer-shell electrons.",
        misconception = "The modern periodic table is not arranged mainly by atomic mass. It is arranged by atomic number.",
        factOne = "Atomic number means number of protons.",
        factTwo = "Elements are arranged in increasing atomic number.",
        factThree = "Properties vary periodically across periods, while elements in the same group usually have similar chemical properties.",
        examAnswer = "Modern periodic law states that the properties of elements are periodic functions of their atomic numbers.",
        takeaway = "Atomic number is the address system of the periodic table."
    )
    "Periodic trends at secondary level" -> SchoolLessonBrief(
        discoverTitle = "Properties change like a pattern",
        hook = "When you move across or down the periodic table, element properties do not change randomly.",
        whyItMatters = "trends help us compare elements such as sodium, magnesium, chlorine and oxygen.",
        dailyLink = "Compare sodium in salt with chlorine in salt. They are very different, but their positions explain their behaviour.",
        simpleMeaning = "Periodic trends are regular changes in properties across a period or down a group.",
        analogy = "A staircase changes step by step. Periodic properties also change step by step.",
        keyWords = "trend, group, period, atomic size, metallic character",
        memoryTrick = "Across means left to right. Down means top to bottom.",
        homeExample = "Metals used in utensils are shiny and conduct heat; non-metals like oxygen behave differently.",
        schoolExample = "A teacher may compare metals and non-metals using samples, charts or reaction videos.",
        natureExample = "Reactive metals are not usually found freely in nature because they react easily.",
        activity = "Draw arrows on a periodic table: metallic character increases down and to the left.",
        prediction = "Which is usually more metallic: sodium or chlorine?",
        reason = "Sodium is on the left side of the table and loses electrons more easily than chlorine.",
        misconception = "A trend is a general pattern, not a rule without exceptions.",
        factOne = "Atomic size generally decreases across a period.",
        factTwo = "Atomic size generally increases down a group.",
        factThree = "Metallic character is stronger toward the left and lower side.",
        examAnswer = "Periodic trends are regular changes in element properties across periods and down groups, such as atomic size and metallic character.",
        takeaway = "The table is a map, and trends are the directions."
    )
    "Chemical effects of electric current" -> SchoolLessonBrief(
        discoverTitle = "Electricity can cause chemical change",
        hook = "A bulb glows because of electricity. In some liquids, electricity can also start a chemical change.",
        whyItMatters = "this explains electrolysis, electroplating and how some substances break into products.",
        dailyLink = "Batteries, chargers and plated metal objects all connect electricity with chemistry.",
        simpleMeaning = "Chemical effects of electric current are changes produced when electric current passes through a conducting liquid or solution.",
        analogy = "Electric current is like a push that makes charged particles move in a liquid.",
        keyWords = "current, electrolyte, electrode, ion, chemical change",
        memoryTrick = "An electrolyte is a conducting medium in which current is carried by moving ions.",
        homeExample = "A rechargeable battery uses chemical changes linked with electric current.",
        schoolExample = "In copper sulfate electrolysis, copper deposits at the cathode. The blue colour may fade with inert electrodes or stay broadly similar with a copper anode.",
        natureExample = "Lightning can cause chemical changes in air, forming small amounts of nitrogen compounds.",
        activity = "Draw two electrodes dipped in an electrolyte and label positive and negative terminals.",
        prediction = "Will pure distilled water conduct electricity well?",
        reason = "Pure water has very few ions, so it is a poor conductor. Salt or acid adds ions.",
        misconception = "All liquids do not conduct electricity. A conducting liquid needs movable ions.",
        factOne = "Current can cause chemical changes in electrolytes.",
        factTwo = "Electrodes connect the liquid to the circuit.",
        factThree = "Movable ions carry charge in solution.",
        examAnswer = "The chemical effect of electric current means chemical changes occur when current passes through a conducting liquid or solution.",
        takeaway = "In liquids, moving ions make electrical chemistry possible."
    )
    "Electrolysis and electrode products" -> SchoolLessonBrief(
        discoverTitle = "Breaking compounds with electricity",
        hook = "Electricity can split some compounds into simpler substances when ions are free to move.",
        whyItMatters = "electrolysis is used to obtain metals, purify substances and make useful chemicals.",
        dailyLink = "Electrolysis helps in metal extraction and in coating objects with a thin metal layer.",
        simpleMeaning = "Electrolysis is the chemical decomposition of an electrolyte by electric current when the electrolyte is molten or dissolved in aqueous solution and has mobile ions.",
        analogy = "Imagine electricity sorting charged particles: positive ions move one way, negative ions move the other way.",
        keyWords = "electrolysis, cathode, anode, cation, anion",
        memoryTrick = "Cations go to cathode. Anions go to anode.",
        homeExample = "Many shiny plated objects are made using electrolysis.",
        schoolExample = "Acidified water electrolysis is a teacher-led demonstration with eye protection and no nearby flame because hydrogen is highly flammable.",
        natureExample = "Electrolysis is human-controlled; nature usually uses other paths for chemical changes.",
        activity = "Make a table with columns: ion, electrode it moves to, product formed.",
        prediction = "Where will copper ions move during electrolysis of copper sulfate?",
        reason = "Copper ions are positive, so they move to the negative cathode and can become copper metal.",
        misconception = "Electrolysis is not simple melting. It is decomposition caused by electric current.",
        factOne = "Electrolysis needs an electrolyte, electrodes and current.",
        factTwo = "Positive ions move to the cathode.",
        factThree = "Products form at electrodes.",
        examAnswer = "Electrolysis is the decomposition of an electrolyte by passing electric current through it. Products are formed at the electrodes.",
        takeaway = "Electricity can separate ions into new substances."
    )
    "Electroplating" -> SchoolLessonBrief(
        discoverTitle = "A thin metal coat",
        hook = "Some jewellery, taps and bicycle parts look shiny because a thin metal layer covers them.",
        whyItMatters = "electroplating improves appearance and can protect objects from corrosion.",
        dailyLink = "Look for shiny metal-coated objects such as taps, handles, medals or decorative items.",
        simpleMeaning = "Electroplating is coating one metal object with a thin layer of another metal using electric current.",
        analogy = "It is like giving an object a very thin metal jacket.",
        keyWords = "electroplating, coating, cathode, anode, electrolyte",
        memoryTrick = "The object to be plated is placed at the cathode.",
        homeExample = "Chromium-plated taps look shiny and resist rust better.",
        schoolExample = "Copper can be plated on an object using copper sulfate solution under proper supervision.",
        natureExample = "Rusting is natural corrosion; electroplating is a human method to reduce it.",
        activity = "Draw an electroplating setup and label object, metal plate, electrolyte and battery.",
        prediction = "Why should a spoon be cleaned before electroplating?",
        reason = "Dirt or oil stops the metal layer from sticking evenly.",
        misconception = "Electroplating does not make the whole object the coating metal. It only adds a thin surface layer.",
        factOne = "Electroplating uses electric current.",
        factTwo = "The object receives a thin metal coating.",
        factThree = "It can improve shine and protection.",
        examAnswer = "Electroplating is the process of depositing a thin layer of one metal on another object using electric current.",
        takeaway = "Electroplating changes the surface, not the whole object."
    )
    "Preparing and diluting solutions" -> SchoolLessonBrief(
        discoverTitle = "Making a solution carefully",
        hook = "A glass of lemon water tastes different when you add more water. Chemistry solutions behave the same way.",
        whyItMatters = "correct solutions are needed for tests, titrations and fair experiments.",
        dailyLink = "Think about making juice: too much powder or too much water changes the taste.",
        simpleMeaning = "Preparing a solution means dissolving a solute in a solvent. Diluting means adding more solvent to make it less concentrated.",
        analogy = "Solute is like sugar. Solvent is like water. Solution is the mixed drink.",
        keyWords = "solute, solvent, solution, concentration, dilution",
        memoryTrick = "Solute is added. Solvent does the dissolving.",
        homeExample = "Salt water is made by dissolving salt in water.",
        schoolExample = "A measured solution is prepared using a balance, measuring cylinder or volumetric flask.",
        natureExample = "An estuary or saltwater pond becomes less salty when fresh rainwater dilutes it.",
        activity = "Write the difference between adding more salt and adding more water to salt solution.",
        prediction = "What happens to concentration when more water is added?",
        reason = "The amount of solute stays the same, but total volume increases, so concentration decreases.",
        misconception = "Dilution does not remove solute. It spreads the same solute through more solvent.",
        factOne = "Solute dissolves in solvent.",
        factTwo = "Solution is the uniform mixture formed.",
        factThree = "Dilution lowers concentration by adding solvent.",
        examAnswer = "A solution is prepared by dissolving a solute in a solvent. Dilution is done by adding more solvent to reduce concentration.",
        takeaway = "Dilution spreads the same solute into more liquid."
    )
    "Testing gases" -> SchoolLessonBrief(
        discoverTitle = "Invisible gases leave clues",
        hook = "Many gases cannot be seen, so chemists identify them using simple tests.",
        whyItMatters = "gas tests help confirm products in reactions safely and clearly.",
        dailyLink = "Bubbles in soda show carbon dioxide escaping from the drink.",
        simpleMeaning = "Testing gases means using a safe observation to identify a gas, such as a pop sound, glowing splint or limewater change. Splint and flame tests must be done by or under a teacher.",
        analogy = "A gas test is like asking the gas to show its ID card.",
        keyWords = "hydrogen, oxygen, carbon dioxide, limewater, splint",
        memoryTrick = "Teacher flame tests: hydrogen pops, oxygen relights, carbon dioxide turns limewater milky.",
        homeExample = "Carbon dioxide makes bubbles in fizzy drinks.",
        schoolExample = "All gas tests using splints or flames must be conducted by or under a teacher; hydrogen is highly flammable.",
        natureExample = "Plants take in carbon dioxide and release oxygen during photosynthesis in daylight.",
        activity = "Make a three-row table: gas, test, observation.",
        prediction = "Which gas turns limewater milky?",
        reason = "Carbon dioxide reacts with limewater to form a white solid, so it looks milky.",
        misconception = "Smelling an unknown gas directly is unsafe. Use proper gas tests instead.",
        factOne = "Hydrogen gives a pop sound with a flame.",
        factTwo = "Oxygen relights a glowing splint.",
        factThree = "Carbon dioxide turns limewater milky.",
        examAnswer = "Gases are identified by characteristic tests, such as hydrogen giving a pop sound, oxygen relighting a glowing splint and carbon dioxide turning limewater milky.",
        takeaway = "A gas may be invisible, but its test result is visible."
    )
    "Identifying acids and bases" -> SchoolLessonBrief(
        discoverTitle = "Sour, soapy and indicator colours",
        hook = "Lemon juice is known to be sour and soap solution is known to feel slippery, but never taste or touch an unknown substance to identify it.",
        whyItMatters = "acids and bases are used in food, cleaning, digestion, soil treatment and medicines.",
        dailyLink = "Notice lemon, vinegar, soap, toothpaste and antacid tablets.",
        simpleMeaning = "Acids and bases are two important types of substances. Indicators help us identify them by colour change.",
        analogy = "An indicator is like a colour alarm. It changes colour to show acid or base.",
        keyWords = "acid, base, indicator, litmus, pH",
        memoryTrick = "Acid turns blue litmus red. Base turns red litmus blue.",
        homeExample = "Vinegar and lemon juice are acidic. Soap solution is basic.",
        schoolExample = "Litmus paper or universal indicator is used to test solutions.",
        natureExample = "Some soils are acidic or basic, which affects plant growth.",
        activity = "Make a table of five safe household substances and predict acid, base or neutral.",
        prediction = "What happens to blue litmus in lemon juice?",
        reason = "Lemon juice is acidic, so it turns blue litmus red.",
        misconception = "Do not taste laboratory chemicals to check acids or bases. Use indicators.",
        factOne = "Acids turn blue litmus red.",
        factTwo = "Bases turn red litmus blue.",
        factThree = "Indicators show acids and bases by colour change.",
        examAnswer = "Acids and bases can be identified using indicators. Acids turn blue litmus red, while bases turn red litmus blue.",
        takeaway = "Indicators make invisible acid-base nature visible."
    )
    "Precipitation observations" -> SchoolLessonBrief(
        discoverTitle = "When a solid suddenly appears",
        hook = "Two clear liquids can be mixed, and suddenly a cloudy solid may appear.",
        whyItMatters = "precipitation helps identify ions and shows that a new substance has formed.",
        dailyLink = "Hard water can leave deposits on taps and kettles, which reminds us that dissolved substances can form solids.",
        simpleMeaning = "A precipitate is an insoluble solid formed when two solutions react.",
        analogy = "It is like invisible dissolved particles joining hands and becoming too heavy to stay dissolved.",
        keyWords = "precipitate, insoluble, solution, ion, observation",
        memoryTrick = "Precipitate means particles appear as a solid.",
        homeExample = "Soap scum in hard water is a common deposit formed from dissolved substances.",
        schoolExample = "Silver nitrate and chloride solution can form a white precipitate of silver chloride.",
        natureExample = "Mineral deposits can form when dissolved substances come out of water.",
        activity = "Draw before and after beakers: clear solutions before, cloudy solid after.",
        prediction = "If a precipitate forms, will the mixture remain perfectly clear?",
        reason = "The new insoluble solid scatters light, so the liquid looks cloudy or solid particles are seen.",
        misconception = "A precipitate is not always dirt. It can be a newly formed substance.",
        factOne = "A precipitate is an insoluble solid.",
        factTwo = "It can form when two solutions react.",
        factThree = "Cloudiness or solid particles are important observations.",
        examAnswer = "Precipitation is a reaction in which an insoluble solid, called a precipitate, forms when solutions are mixed.",
        takeaway = "A sudden solid is strong evidence of a chemical reaction."
    )
    "Recording results and uncertainty" -> SchoolLessonBrief(
        discoverTitle = "Good science needs good records",
        hook = "If two students do the same experiment, their readings may be slightly different.",
        whyItMatters = "careful records help us trust results and find mistakes.",
        dailyLink = "When you measure height, weight or cooking ingredients, small reading differences can happen.",
        simpleMeaning = "Recording results means writing observations and measurements clearly. Uncertainty is the estimated range linked with a measurement because of instrument resolution and measurement conditions.",
        analogy = "A measurement is like a photograph. A clear photo is useful; a shaky photo can confuse us.",
        keyWords = "observation, reading, repeat, average, uncertainty",
        memoryTrick = "Record, repeat, review.",
        homeExample = "Measuring one cup of water may not be exact every time.",
        schoolExample = "Students repeat readings and calculate an average to improve reliability.",
        natureExample = "Temperature and wind readings outdoors can change from minute to minute.",
        activity = "Measure the length of a pencil three times and compare the readings.",
        prediction = "Will repeated measurements always be exactly the same?",
        reason = "Small differences happen because of instrument limits, eye position and method.",
        misconception = "A small difference in readings does not always mean the experiment failed.",
        factOne = "Results must be recorded clearly.",
        factTwo = "Repeated readings make results more reliable.",
        factThree = "Measurement uncertainty expresses an estimated range associated with the measured value.",
        examAnswer = "Results should be recorded carefully with units. Repeated readings and averages reduce the effect of random variation, but they do not automatically correct systematic error. Uncertainty shows the estimated measurement range.",
        takeaway = "Careful records turn observations into science."
    )
    else -> genericSchoolBrief(lesson)
}

private fun genericSchoolBrief(lesson: CurriculumLesson): SchoolLessonBrief = SchoolLessonBrief(
    discoverTitle = "Start with something you can see",
    hook = "This lesson begins with a simple observation from daily life.",
    whyItMatters = "${lesson.title.lowercase(Locale.US)} helps us explain changes around us.",
    dailyLink = "Find one safe example of this topic at home or school and describe what you observe.",
    simpleMeaning = lesson.concept,
    analogy = "Think of the idea as a small tool. It helps you explain one observation clearly.",
    keyWords = lesson.title.split(" ").take(3).joinToString(", "),
    memoryTrick = "Observe first, name second, explain third.",
    homeExample = "Look for the topic in cooking, cleaning, water, air, clothes, fuels or medicines.",
    schoolExample = "Use a chart, safe demo or simple measurement to connect the idea with evidence.",
    natureExample = "Nature gives many clues when we watch colour, smell, temperature, state or movement.",
    activity = "Write one observation. Then complete this sentence: I think this happens because...",
    prediction = "If one condition changes, what visible change do you expect?",
    reason = "Chemistry explanations connect what we see with particles and substances.",
    misconception = lesson.commonMistake,
    factOne = "${lesson.title} explains one clear chemistry idea.",
    factTwo = "Examples make the definition easier to remember.",
    factThree = "Good answers use observation plus reason.",
    examAnswer = "${lesson.title} means ${lesson.concept}",
    takeaway = "Chemistry becomes easy when words connect with real observations."
)

private fun discoverHtml(module: CurriculumModule, lesson: CurriculumLesson, style: String, brief: SchoolLessonBrief): String =
    htmlPage(
        "Discover",
        style,
        brief.discoverTitle,
        listOf(
            brief.hook.escapeHtml(),
            "Today we learn <b>${lesson.title.escapeHtml()}</b>. This topic matters because ${brief.whyItMatters.escapeHtml()}.",
            "Goal: by the end, you should be able to explain it using one real example from your own life."
        ),
        "Daily-life link",
        brief.dailyLink.escapeHtml()
    )

private fun understandHtml(lesson: CurriculumLesson, style: String, brief: SchoolLessonBrief): String =
    htmlPage(
        "Understand",
        style,
        "The idea in simple words",
        listOf(
            brief.simpleMeaning.escapeHtml(),
            "Easy picture: ${brief.analogy.escapeHtml()}",
            "Key words: <b>${brief.keyWords.escapeHtml()}</b>",
            "Memory trick: <b>${brief.memoryTrick.escapeHtml()}</b>"
        ),
        "Teacher tip",
        "Do not memorise the sentence first. First make the picture in your mind. Then say it in your own words."
    )

private fun observeHtml(lesson: CurriculumLesson, style: String, brief: SchoolLessonBrief): String =
    htmlPage(
        "Observe",
        style,
        "See it in real life",
        listOf(
            "Home example: ${brief.homeExample.escapeHtml()}",
            "School example: ${brief.schoolExample.escapeHtml()}",
            "Nature example: ${brief.natureExample.escapeHtml()}"
        ),
        "Mini activity",
        brief.activity
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
        "Fix the fear",
        "For ${lesson.title.escapeHtml()}, check the common mistake above first. Then compare your answer with the observation and try again."
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
