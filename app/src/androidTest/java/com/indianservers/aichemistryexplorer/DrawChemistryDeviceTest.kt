package com.indianservers.aichemistryexplorer

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.activity.ComponentActivity
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.action.ViewActions.click
import com.indianservers.aichemistryexplorer.drawchemistry.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.*
import org.junit.Assert.*
import org.json.JSONObject

class DrawChemistryDeviceTest {
    @get:Rule val compose=createAndroidComposeRule<ComponentActivity>()
    private val context get()=InstrumentationRegistry.getInstrumentation().targetContext
    private var savedIds=emptySet<String>()
    private fun repo()=ChemRepository(context,completePeriodicTable(emptyList()).sortedBy { it.number }.map { it.symbol })
    private var previous:SavedStructure?=null
    @Before fun preserveDraft()=runBlocking {previous=repo().dao.get("draft");repo().dao.delete("draft");savedIds=repo().dao.observe().first().map {it.id}.toSet()}
    @After fun restoreDraft()=runBlocking {repo().dao.delete("draft");previous?.let {repo().dao.save(it)};repo().dao.observe().first().filter {it.id!="draft" && it.id !in savedIds && !it.id.startsWith("cache:")}.forEach {repo().dao.delete(it.id)};Unit}
    @Test fun allBundledGraphsMatchSourceFormulaAndConformerConnectivity()=runBlocking {
        val r=repo();val entries=r.library();assertEquals(26,entries.size);assertEquals(26,entries.map {it.cid}.distinct().size)
        var conformers=0
        entries.forEach { entry->val g=r.bundled(entry);val expected=g.properties.entries.first {it.key.startsWith("Molecular Formula")}.value
            assertEquals(entry.name,expected,ChemToolkit.formula(g));assertEquals(entry.cid,g.cid)
            assertTrue(entry.name,g.atoms.isNotEmpty());assertTrue(entry.name,g.bonds.all {b->g.atoms.any {it.id==b.a}&&g.atoms.any {it.id==b.b}})
            assertEquals(g,ChemProject.decode(ChemProject.encode(g)))
            if(g.conformer.isNotEmpty()){conformers++;assertEquals(g.atoms.map {it.id}.toSet(),g.conformer.map {it.id}.toSet());assertTrue(g.conformer.all {it.x.isFinite()&&it.y.isFinite()&&it.z.isFinite()})}
        };assertEquals(25,conformers)
    }
    @Test fun localDatabaseRoundTripWorksWithoutNetwork()=runBlocking {
        val r=repo();val g=r.bundled(r.library().first {it.cid==2244});val saved=SavedStructure("test:aspirin",g.name,ChemProject.encode(g),true)
        try{r.dao.save(saved);val read=r.dao.get(saved.id)!!;assertTrue(read.favorite);assertEquals(g,ChemProject.decode(read.project))}finally{r.dao.delete(saved.id)}
    }
    @Test fun pubChemSearchAndCachedRecord()=runBlocking {
        val r=repo();r.dao.delete("cache:702")
        val ids=r.search("ethanol");assertTrue(ids.contains(702));val g=r.remote(702);assertEquals("C2H6O",ChemToolkit.formula(g));assertTrue(g.conformer.isNotEmpty());assertEquals(g,r.remote(702));assertNotNull(r.dao.get("cache:702"))
    }
    @Test fun drawingUndoRedoSaveAndLibraryNavigation(){
        compose.activityRule.scenario.onActivity {it.setShowWhenLocked(true);it.setTurnScreenOn(true);it.window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)}
        compose.setContent { DrawChemistryScreen(completePeriodicTable(emptyList()),{}) }
        compose.waitUntil(5000){compose.onAllNodes(hasText("Save") and isEnabled()).fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithContentDescription("Clear",substring=false).performClick()
        compose.onNodeWithText("Clear drawing",substring=false).performClick()
        compose.onNodeWithText("O",useUnmergedTree=true).performClick()
        onView(isAssignableFrom(ChemCanvasView::class.java)).perform(click())
        compose.onNodeWithText("Properties").performClick()
        compose.waitUntil(5000){compose.onAllNodesWithText("H2O",substring=false).fetchSemanticsNodes().isNotEmpty()}
        compose.onAllNodesWithText("H2O",substring=false).onFirst().assertIsDisplayed()
        compose.onNodeWithText("Draw 2D").performClick()
        compose.onNodeWithContentDescription("Undo",substring=false).performClick()
        compose.onNodeWithContentDescription("Redo",substring=false).performClick()
        compose.onNodeWithText("Save",substring=false).performClick()
        compose.onNodeWithText("Save as new").performClick()
        compose.waitUntil(5000){compose.onAllNodesWithText("Save as new").fetchSemanticsNodes().isEmpty()}
        compose.onNodeWithText("Structures",substring=false).performClick()
        compose.onNodeWithText("Water",substring=false).performClick()
        compose.waitUntil(10000){compose.onAllNodesWithText("Water · 3 atoms").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Build 3D").performClick()
        compose.onNodeWithText("PubChem computed conformer · coordinates in Å").assertIsDisplayed()
        compose.onNodeWithText("Space filling").performClick()
        compose.onNodeWithText("Space filling").assertIsSelected()
        compose.onNodeWithText("Hide H").performClick()
        compose.onNodeWithText("Show H").assertIsDisplayed()
        compose.onNodeWithText("Reset",substring=false).performClick()
    }
    @Test fun nativeCanvasTouchGraphEditingAndPngRendering(){
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val frame=android.widget.FrameLayout(context);val view=ChemCanvasView(context);frame.addView(view)
            view.layout(0,0,1080,1600);view.onChange={view.graph=it}
            fun event(action:Int,x:Float,y:Float,t:Long){val e=android.view.MotionEvent.obtain(t,t,action,x,y,0);view.dispatchTouchEvent(e);e.recycle()}
            fun tap(x:Float,y:Float){val t=android.os.SystemClock.uptimeMillis();event(0,x,y,t);event(1,x,y,t+30)}
            fun drag(x1:Float,y1:Float,x2:Float,y2:Float){val t=android.os.SystemClock.uptimeMillis();event(0,x1,y1,t);event(2,x2,y2,t+100);event(1,x2,y2,t+130)}
            view.element="O";tap(540f,800f);view.element="H";tap(360f,950f);tap(720f,950f)
            assertEquals(3,view.graph.atoms.size)
            view.tool=DrawTool.Bond;drag(540f,800f,360f,950f);drag(540f,800f,720f,950f)
            assertEquals(2,view.graph.bonds.size);assertEquals("H2O",ChemToolkit.formula(view.graph))
            view.tool=DrawTool.Select;tap(540f,800f);assertEquals(setOf(1),view.selected)
            drag(540f,800f,540f,750f);assertEquals(3,view.graph.atoms.size)
            val snapshot=view.graph;view.fit();assertEquals(snapshot,view.graph)
            val bitmap=android.graphics.Bitmap.createBitmap(1080,1600,android.graphics.Bitmap.Config.ARGB_8888)
            view.draw(android.graphics.Canvas(bitmap));val file=java.io.File(context.getExternalFilesDir(null),"draw-chemistry-native-canvas.png")
            file.outputStream().use {assertTrue(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,it))};bitmap.recycle();assertTrue(file.length()>1000)
            val svg=chemSvg(view.graph);assertTrue(svg.contains("<svg"));assertEquals(3,Regex("<circle").findAll(svg).count())
        }
    }
    @Test fun actualGlesRendererSupportsConformersAndStyles()=runBlocking {
        val r=repo();val graph=r.bundled(r.library().first {it.cid==2519})
        val display=android.opengl.EGL14.eglGetDisplay(android.opengl.EGL14.EGL_DEFAULT_DISPLAY)
        val version=IntArray(2);assertTrue(android.opengl.EGL14.eglInitialize(display,version,0,version,1))
        val configs=arrayOfNulls<android.opengl.EGLConfig>(1);val count=IntArray(1)
        val attributes=intArrayOf(android.opengl.EGL14.EGL_RENDERABLE_TYPE,android.opengl.EGL14.EGL_OPENGL_ES2_BIT,android.opengl.EGL14.EGL_SURFACE_TYPE,android.opengl.EGL14.EGL_PBUFFER_BIT,android.opengl.EGL14.EGL_RED_SIZE,8,android.opengl.EGL14.EGL_GREEN_SIZE,8,android.opengl.EGL14.EGL_BLUE_SIZE,8,android.opengl.EGL14.EGL_DEPTH_SIZE,16,android.opengl.EGL14.EGL_NONE)
        assertTrue(android.opengl.EGL14.eglChooseConfig(display,attributes,0,configs,0,1,count,0));assertTrue(count[0]>0)
        val config=configs[0]!!
        val eglContext=android.opengl.EGL14.eglCreateContext(display,config,android.opengl.EGL14.EGL_NO_CONTEXT,intArrayOf(android.opengl.EGL14.EGL_CONTEXT_CLIENT_VERSION,2,android.opengl.EGL14.EGL_NONE),0)
        val surface=android.opengl.EGL14.eglCreatePbufferSurface(display,config,intArrayOf(android.opengl.EGL14.EGL_WIDTH,640,android.opengl.EGL14.EGL_HEIGHT,640,android.opengl.EGL14.EGL_NONE),0)
        try{
            assertTrue(android.opengl.EGL14.eglMakeCurrent(display,surface,surface,eglContext))
            val renderer=ChemGlView.MoleculeRenderer();renderer.graph=graph;renderer.onSurfaceCreated(null,null);renderer.onSurfaceChanged(null,640,640)
            val times=mutableListOf<Double>()
            repeat(4){style->renderer.style=style;renderer.onDrawFrame(null);android.opengl.GLES20.glFinish();assertEquals(android.opengl.GLES20.GL_NO_ERROR,android.opengl.GLES20.glGetError())}
            renderer.style=0
            repeat(120){i->renderer.rx=i*3f;renderer.ry=i*3f;val start=System.nanoTime();renderer.onDrawFrame(null);android.opengl.GLES20.glFinish();times+=(System.nanoTime()-start)/1_000_000.0}
            val pixels=java.nio.ByteBuffer.allocateDirect(640*640*4).order(java.nio.ByteOrder.nativeOrder())
            android.opengl.GLES20.glReadPixels(0,0,640,640,android.opengl.GLES20.GL_RGBA,android.opengl.GLES20.GL_UNSIGNED_BYTE,pixels)
            var colored=0;for(i in 0 until 640*640){val red=pixels.get(i*4).toInt()and 255;val green=pixels.get(i*4+1).toInt()and 255;val blue=pixels.get(i*4+2).toInt()and 255;if(red<180||green<180||blue<180)colored++};assertTrue("Molecule should render visible pixels",colored>500)
            pixels.position(0);val bitmap=android.graphics.Bitmap.createBitmap(640,640,android.graphics.Bitmap.Config.ARGB_8888);bitmap.copyPixelsFromBuffer(pixels)
            val flipped=android.graphics.Bitmap.createBitmap(bitmap,0,0,640,640,android.graphics.Matrix().apply {preScale(1f,-1f)},true)
            java.io.File(context.getExternalFilesDir(null),"draw-chemistry-gles-caffeine.png").outputStream().use {flipped.compress(android.graphics.Bitmap.CompressFormat.PNG,100,it)};bitmap.recycle();flipped.recycle()
            renderer.hydrogens=false;renderer.onDrawFrame(null);android.opengl.GLES20.glFinish();assertEquals(0,android.opengl.GLES20.glGetError())
            val sorted=times.sorted();java.io.File(context.getExternalFilesDir(null),"draw-chemistry-gles-timing.json").writeText("{\"frames\":120,\"width\":640,\"height\":640,\"medianMs\":${sorted[60]},\"p95Ms\":${sorted[114]},\"kind\":\"offscreen GLES render plus glFinish; not on-screen FPS\"}")
        }finally{android.opengl.EGL14.eglMakeCurrent(display,android.opengl.EGL14.EGL_NO_SURFACE,android.opengl.EGL14.EGL_NO_SURFACE,android.opengl.EGL14.EGL_NO_CONTEXT);android.opengl.EGL14.eglDestroySurface(display,surface);android.opengl.EGL14.eglDestroyContext(display,eglContext);android.opengl.EGL14.eglTerminate(display)}
    }
}
