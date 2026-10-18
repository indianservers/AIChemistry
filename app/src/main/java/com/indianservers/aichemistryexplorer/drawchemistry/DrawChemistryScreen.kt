package com.indianservers.aichemistryexplorer.drawchemistry

import android.graphics.Bitmap
import android.graphics.Canvas
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextOverflow
import android.content.Intent
import android.content.ClipData
import android.content.ClipboardManager
import android.net.Uri
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.indianservers.aichemistryexplorer.Element
import kotlinx.coroutines.*
import java.util.UUID
import java.util.Locale
import kotlin.math.*

private val Ink=StudioInk
private val Accent=StudioBlue
@Composable internal fun DrawChemistryHomeCard(onOpen:()->Unit){
    Card(onClick=onOpen,modifier=Modifier.fillMaxWidth().padding(horizontal=20.dp,vertical=6.dp),colors=CardDefaults.cardColors(containerColor=Color(0xFFF3FAFF)),shape=RoundedCornerShape(20.dp)){
        Row(Modifier.padding(18.dp),verticalAlignment=Alignment.CenterVertically){
            Text("⌬",fontSize=44.sp,color=Accent,modifier=Modifier.padding(end=16.dp))
            Column(Modifier.weight(1f)){Text("Draw Chemistry",fontWeight=FontWeight.Bold,fontSize=21.sp,color=Ink);Text("Draw structures · explore molecules in 3D",fontSize=12.sp,color=Color(0xFF586D82))}
            Text("→",color=Accent,fontSize=24.sp)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable internal fun DrawChemistryScreen(elements:List<Element>,onBack:()->Unit){
    val density=LocalDensity.current
    val systemFontScale=LocalContext.current.resources.configuration.fontScale
    // Reading zoom belongs to lesson prose; the workstation honors Android accessibility scaling.
    CompositionLocalProvider(LocalDensity provides Density(density.density,systemFontScale)){
        MaterialTheme(colorScheme=lightColorScheme(primary=Accent,background=Color.White,surface=Color.White,onSurface=Ink)){
            Studio(elements,onBack)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun Studio(elements:List<Element>,onBack:()->Unit){
    val context=LocalContext.current;val scope=rememberCoroutineScope()
    val focusManager=LocalFocusManager.current
    val configuration=LocalConfiguration.current
    val landscape=configuration.screenWidthDp>configuration.screenHeightDp
    var searchExpanded by rememberSaveable { mutableStateOf(false) }
    val repo=remember { ChemRepository(context,elements.sortedBy { it.number }.map { it.symbol }) }
    var project by rememberSaveable { mutableStateOf("") };var ready by remember { mutableStateOf(false) }
    var graph by remember { mutableStateOf(if(project.isBlank())ChemGraph()else ChemProject.decode(project)) }
    val history=remember { ChemHistory(graph) }
    var tab by rememberSaveable { mutableIntStateOf(0) };var tool by remember { mutableStateOf(DrawTool.Select) };var symbol by rememberSaveable { mutableStateOf("C") }
    var order by remember { mutableIntStateOf(1) };var stereo by remember { mutableStateOf("") };var ring by remember { mutableIntStateOf(6) };var aromatic by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(emptySet<Int>()) };var measures by remember { mutableStateOf(emptyList<Int>()) }
    var query by rememberSaveable { mutableStateOf("") };var searchType by remember { mutableStateOf("Name") };var busy by remember { mutableStateOf(false) };var status by remember { mutableStateOf(if(graph.atoms.isEmpty())"Tap to place an atom. Drag between atoms to draw a bond."else "${graph.name} · ${graph.atoms.size} atoms") }
    var dialog by remember { mutableStateOf("") };var inspect by remember { mutableStateOf<Int?>(null) };var bondInspect by remember { mutableStateOf<ChemBond?>(null) }
    var style by remember { mutableIntStateOf(0) };var showH by remember { mutableStateOf(true) }
    var canvas by remember { mutableStateOf<ChemCanvasView?>(null) };var gl by remember { mutableStateOf<ChemGlView?>(null) }
    var exportFormat by remember { mutableStateOf("Project JSON") };var clipboard by remember { mutableStateOf<ChemGraph?>(null) }
    var results by remember { mutableStateOf(emptyList<Int>()) };var resultPage by remember { mutableIntStateOf(0) }
    var favoritesOnly by remember { mutableStateOf(false) };var category by remember { mutableStateOf("All") }
    val saved by repo.dao.observe().collectAsState(initial=emptyList())
    val library=remember {val priority=listOf(962,702,241,176,2244,2519,5793,5997,243);repo.library().sortedBy {e->priority.indexOf(e.cid).takeIf {it>=0}?:100+e.cid} }
    var libraryGroup by rememberSaveable {mutableStateOf("Common")}
    var propertyPage by rememberSaveable {mutableStateOf("Overview")}
    var gestureMode by rememberSaveable {mutableIntStateOf(0)}
    var modelZoom by rememberSaveable {mutableFloatStateOf(1f)}
    var focused by rememberSaveable {mutableStateOf(false)}
    val previews by produceState<Map<Int,ChemGraph>>(emptyMap(),repo){
        value=withContext(Dispatchers.IO){library.associate {entry->entry.cid to repo.bundled(entry)}}
    }
    val calculated by produceState(initialValue=Pair("Calculating…","Calculating…"),key1=graph){
        val snapshot=graph
        if(snapshot.annotations.any {it.kind=="arrow"}){value=Pair("Reaction scheme · multiple species","Not a single molecule");return@produceState}
        value=withContext(Dispatchers.Default){Pair(runCatching { ChemToolkit.formula(snapshot) }.getOrElse { "Unavailable for this graph" },runCatching { "${fmt(ChemToolkit.mass(snapshot))} g/mol" }.getOrElse { "Unavailable" })}
    }
    fun change(next:ChemGraph){if(graph.sourceMol.isNotBlank() && next.sourceMol.isBlank() && graph.atoms.isNotEmpty())status="Structure edited: reference properties/3D cleared. Review stereochemistry before export.";history.change(next);graph=next;project=ChemProject.encode(next);selected=selected.intersect(next.atoms.map { it.id }.toSet());measures=emptyList()}
    fun message(t:String){status=t}
    fun work(block:suspend ()->Unit){
        if(busy)return
        busy=true
        scope.launch {
            try{block()}
            catch(e:CancellationException){throw e}
            catch(e:Exception){val notice=e.message?:"Operation failed; drawing retained.";message(notice);android.widget.Toast.makeText(context,notice,android.widget.Toast.LENGTH_LONG).show()}
            finally{busy=false}
        }
    }
    fun load(next:ChemGraph){focusManager.clearFocus();change(next);tab=0;selected=emptySet();status="${next.name} · ${next.atoms.size} atoms";scope.launch { delay(60);canvas?.fit() }}
    LaunchedEffect(Unit){
        if(project.isBlank()){
            val draft=repo.dao.get("draft")
            val restored=if(draft!=null)runCatching {ChemProject.decode(draft.project)}.getOrNull()else repo.bundled(library.first {it.cid==243})
            restored?.let {graph=it;history.change(it);project=ChemProject.encode(it);status="${it.name} · ${it.atoms.size} atoms"}
        }
        ready=true
    }
    LaunchedEffect(project,ready){if(ready && project.isNotBlank()){delay(250);try{withContext(Dispatchers.IO){repo.dao.save(SavedStructure("draft",graph.name,project))}}catch(e:Exception){if(e is CancellationException)throw e;status="Draft could not be saved. Export a copy to retain your work."}}}
    val importFile=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){uri->if(uri!=null)work {
        val text=withContext(Dispatchers.IO){context.contentResolver.openInputStream(uri)!!.use { val bytes=it.readNBytes(2_000_001);require(bytes.size<=2_000_000){"Import exceeds 2 MB."};bytes.toString(Charsets.UTF_8) }}
        val next=withContext(Dispatchers.Default){when{ text.trimStart().startsWith("{")->ChemProject.decode(text).copy(source="Imported project · provenance supplied by file");text.contains("V2000")||text.contains("V3000")->ChemToolkit.mol(text);else->ChemToolkit.smiles(text.trim().lineSequence().first()) }}
        load(next)
    }}
    val exportFile=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")){uri->if(uri!=null)work {
        val snapshot=graph;val format=exportFormat
        require(snapshot.annotations.none {it.kind=="arrow"} || format in listOf("Project JSON","SVG","PNG")){"Export reaction schemes as project JSON, SVG or PNG. Molecular formats do not retain the scheme."}
        val bitmap=if(format=="PNG")canvas?.let { view->Bitmap.createBitmap(view.width.coerceAtLeast(1),view.height.coerceAtLeast(1),Bitmap.Config.ARGB_8888).also { view.draw(Canvas(it)) } }else null
        withContext(Dispatchers.IO){context.contentResolver.openOutputStream(uri)!!.use { out->if(bitmap!=null){bitmap.compress(Bitmap.CompressFormat.PNG,100,out);bitmap.recycle()}else out.write(when(format){"MOL V2000"->ChemToolkit.writeMol(snapshot);"SDF"->ChemToolkit.writeMol(snapshot)+"\n$$$$\n";"3D SDF"->ChemToolkit.writeMol(snapshot,true)+"\n$$$$\n";"SMILES"->ChemToolkit.writeSmiles(snapshot);"SVG"->chemSvg(snapshot);"XYZ coordinates"->{require(snapshot.conformer.isNotEmpty()){ "A source 3D conformer is required." };"${snapshot.conformer.size}\n${snapshot.name} · ${snapshot.source}, coordinates in angstroms; no bond data\n"+snapshot.conformer.joinToString("\n"){"${it.symbol} ${it.x} ${it.y} ${it.z}"}};else->ChemProject.encode(snapshot)}.toByteArray()) }}
        message("Export saved: $format")
    }}
    fun copyText(value:String){(context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("Chemical structure",value));message("Copied to clipboard")}
    fun sourceLink(){graph.cid?.let {context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://pubchem.ncbi.nlm.nih.gov/compound/$it")))}}
    fun copyFragment(){clipboard=graph.copy(atoms=graph.atoms.filter {selected.isEmpty()||it.id in selected},bonds=graph.bonds.filter {selected.isEmpty()||(it.a in selected&&it.b in selected)},conformer=emptyList(),sourceMol="",properties=emptyMap(),cid=null);message("Fragment copied. Use Paste fragment from the menu.")}
    fun rotateSelection(){val ids=selected.ifEmpty {graph.atoms.map {it.id}.toSet()};if(ids.isNotEmpty()){val cx=graph.atoms.filter {it.id in ids}.map {it.x}.average();val cy=graph.atoms.filter {it.id in ids}.map {it.y}.average();change(graph.copy(atoms=graph.atoms.map {if(it.id in ids){val x=it.x-cx;val y=it.y-cy;it.copy(x=cx+x*cos(PI/12)-y*sin(PI/12),y=cy+x*sin(PI/12)+y*cos(PI/12))}else it}))}}
    val formula=graph.properties.entries.firstOrNull {it.key=="Molecular Formula"}?.value?:calculated.first
    fun property(prefix:String)=graph.properties.entries.firstOrNull {it.key.startsWith(prefix)}?.value
    val smiles=property("SMILES")
    val searchPane: @Composable ()->Unit = {
        if(!focused && (!landscape||searchExpanded)){
            Surface(Modifier.fillMaxWidth().padding(horizontal=14.dp,vertical=6.dp).shadow(7.dp,RoundedCornerShape(28.dp),ambientColor=Color(0xFFC4D1E0),spotColor=Color(0xFFDEE6F0)),shape=RoundedCornerShape(28.dp),color=Color.White,border=BorderStroke(1.dp,StudioLine)){
                Row(Modifier.heightIn(min=44.dp).padding(start=16.dp,end=5.dp),verticalAlignment=Alignment.CenterVertically){
                    androidx.compose.foundation.text.BasicTextField(query,{query=it},singleLine=true,textStyle=LocalTextStyle.current.copy(color=StudioInk,fontSize=11.sp,lineHeight=14.sp),modifier=Modifier.weight(1f),decorationBox={inner->if(query.isBlank())Text("Search compound or enter SMILES",color=StudioMuted,fontSize=11.sp);inner()})
                    Box(Modifier.size(40.dp).semantics {contentDescription="Find"}.clickable(enabled=query.isNotBlank()&&!busy){focusManager.clearFocus();dialog="Search"},contentAlignment=Alignment.Center){StudioIcon("search",Modifier.size(21.dp),if(query.isBlank())StudioInk else StudioBlue)}
                }
            }
        }
    }
    val toolPane: @Composable ()->Unit = {
        if(!focused)StudioTabs(tab,compact=false){focusManager.clearFocus();tab=it}
        if(busy)LinearProgressIndicator(Modifier.fillMaxWidth(),color=StudioBlue)
        if(tab==0 && !focused){StudioPanel(Modifier.padding(horizontal=14.dp,vertical=if(landscape)3.dp else 8.dp)){
            Row(Modifier.fillMaxWidth().padding(3.dp)){
                listOf(DrawTool.Select to "select",DrawTool.Atom to "atom",DrawTool.Bond to "bond",DrawTool.Ring to "ring",DrawTool.Lasso to "lasso",DrawTool.Erase to "erase").forEach {(mode,icon)->
                    StudioAction(mode.name,icon,{focusManager.clearFocus();tool=mode;if(mode==DrawTool.Bond)dialog="Bonds";if(mode==DrawTool.Ring)dialog="Rings"},Modifier.weight(1f),selected=tool==mode,compact=landscape)
                }
            }
        }}
        if(tab==1 && !focused){StudioPanel(Modifier.padding(horizontal=14.dp,vertical=if(landscape)3.dp else 8.dp)){
            Row(Modifier.fillMaxWidth().padding(3.dp)){
                listOf("Ball & stick" to "atom","Space filling" to "space","Sticks" to "bond","Wireframe" to "3d").forEachIndexed {i,(label,icon)->StudioAction(label,icon,{style=i},Modifier.weight(1f),selected=style==i,compact=landscape)}
            }
        }}
    }
    val footerPane: @Composable ()->Unit = {
        if(tab==0 && !focused){
            Row(Modifier.fillMaxWidth().padding(horizontal=12.dp,vertical=6.dp)){
                StudioAction("Edit atom","edit",{if(selected.isNotEmpty())inspect=selected.first()else dialog="Atoms"},Modifier.weight(1f),enabled=graph.atoms.isNotEmpty(),compact=true)
                StudioAction("Delete","delete",{if(selected.isNotEmpty()){change(graph.delete(selected));selected=emptySet()}else dialog="Clear"},Modifier.weight(1f),enabled=graph.atoms.isNotEmpty(),compact=true)
                StudioAction("Copy","copy",::copyFragment,Modifier.weight(1f),enabled=graph.atoms.isNotEmpty(),compact=true)
                StudioAction("Rotate 15°","rotate",::rotateSelection,Modifier.weight(1f),enabled=graph.atoms.isNotEmpty(),compact=true)
                StudioAction("Clear","clear",{dialog="Clear"},Modifier.weight(1f),enabled=graph.atoms.isNotEmpty(),compact=true)
            }
            if(!landscape)StudioPanel(Modifier.fillMaxWidth().padding(horizontal=13.dp,vertical=5.dp)){Row(Modifier.padding(10.dp),verticalAlignment=Alignment.CenterVertically){StudioIcon("light",Modifier.size(22.dp),Color(0xFFE5B328));Text(status,fontSize=10.sp,color=StudioMuted,maxLines=2,modifier=Modifier.padding(start=10.dp).weight(1f))}}
        }
        if(tab==1 && !focused){
            Column(Modifier.padding(horizontal=15.dp,vertical=5.dp)){
                if(landscape)Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){
                    Row(Modifier.weight(1f).background(Color(0xFFEEF2F7),RoundedCornerShape(12.dp)).padding(3.dp)){
                        listOf("Rotate","Zoom","Pan").forEachIndexed {i,label->Text(label,modifier=Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(if(gestureMode==i)StudioBlue else Color.Transparent).clickable {gestureMode=i}.padding(vertical=9.dp),fontSize=10.sp,color=if(gestureMode==i)Color.White else StudioMuted,textAlign=androidx.compose.ui.text.style.TextAlign.Center)}
                    }
                    TextButton(onClick={showH=!showH}){Text(if(showH)"Hide H"else "Show H",fontSize=10.sp)}
                }
                if(!landscape)Row(Modifier.fillMaxWidth().background(Color(0xFFEEF2F7),RoundedCornerShape(12.dp)).padding(3.dp)){
                    listOf("Rotate","Zoom","Pan").forEachIndexed {i,label->Text(label,modifier=Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(if(gestureMode==i)StudioBlue else Color.Transparent).clickable {gestureMode=i}.padding(vertical=10.dp),fontSize=11.sp,color=if(gestureMode==i)Color.White else StudioMuted,textAlign=androidx.compose.ui.text.style.TextAlign.Center)}
                }
                if(!landscape){
                    Row(Modifier.fillMaxWidth().padding(top=5.dp)){
                        listOf("X axis","Y axis","Z axis").forEachIndexed {i,label->Column(Modifier.weight(1f).clickable {gl?.orient(i)},horizontalAlignment=Alignment.CenterHorizontally){StudioIcon("atom",Modifier.padding(top=6.dp).size(23.dp),listOf(Color(0xFFD74A56),Color(0xFF23846A),StudioBlue)[i]);Text(label,fontSize=9.sp,color=StudioMuted,modifier=Modifier.padding(top=5.dp))}}
                        StudioAction("Reset","reset",{gl?.reset();measures=emptyList()},Modifier.weight(1f),compact=true)
                    }
                    Slider(value=modelZoom,onValueChange={modelZoom=it;gl?.setZoom(it)},valueRange=.4f..3f,modifier=Modifier.fillMaxWidth().height(30.dp),thumb={Box(Modifier.size(17.dp).background(StudioBlue,CircleShape))},track={androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(4.dp)){drawRoundRect(Color(0xFFE4E9F2),cornerRadius=androidx.compose.ui.geometry.CornerRadius(20f));drawRoundRect(StudioBlue,size=androidx.compose.ui.geometry.Size(size.width*((modelZoom-.4f)/2.6f).coerceIn(0f,1f),size.height),cornerRadius=androidx.compose.ui.geometry.CornerRadius(20f))}})
                    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){
                        Row(Modifier.weight(1f).horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(7.dp)){listOf(243,2244,702,2519).forEach {cid->Surface(Modifier.size(66.dp).clip(RoundedCornerShape(11.dp)).border(if(graph.cid==cid)2.dp else 1.dp,if(graph.cid==cid)StudioBlue else StudioLine,RoundedCornerShape(11.dp)).clickable {previews[cid]?.let {change(it);status="${it.name} · ${it.atoms.size} atoms"}},shape=RoundedCornerShape(11.dp),color=Color.White){MoleculePreview(previews[cid],Modifier.fillMaxSize())}}}
                        TextButton(onClick={showH=!showH}){Text(if(showH)"Hide H"else "Show H",fontSize=10.sp)}
                    }
                }
                Text(if(graph.source.startsWith("PubChem"))"PubChem computed conformer · coordinates in Å"else graph.source,fontSize=8.sp,color=StudioMuted,maxLines=1,modifier=Modifier.padding(top=3.dp))
            }
        }
    }
    val workspace: @Composable (Modifier)->Unit = { workspaceModifier ->
        BoxWithConstraints(workspaceModifier.padding(horizontal=if(focused)0.dp else 11.dp).clip(RoundedCornerShape(18.dp)).background(Color.White)){
            when(tab){
                0->{
                    AndroidView(factory={ChemCanvasView(it).also {v->canvas=v}},modifier=Modifier.fillMaxSize().then(if(landscape)Modifier.padding(start=51.dp,top=54.dp)else Modifier).testTag("drawing-viewport"),update={v->v.skeletal=true;v.graph=graph;v.selected=selected;v.tool=tool;v.element=symbol;v.bondOrder=order;v.bondStereo=stereo;v.ringSize=ring;v.aromatic=aromatic;v.onBondInspect={bondInspect=it};v.onChange={change(it)};v.onSelect={selected=it};v.onInspect={inspect=it}})
                    StudioPanel(Modifier.align(Alignment.TopStart).padding(6.dp).width(39.dp).heightIn(max=minOf(if(landscape)160.dp else 367.dp,(maxHeight-if(landscape)10.dp else 65.dp).coerceAtLeast(44.dp)))){
                        Column(Modifier.verticalScroll(rememberScrollState()).padding(vertical=3.dp),horizontalAlignment=Alignment.CenterHorizontally){
                            listOf("C","H","O","N","S","P","Cl","Br","I").forEach {s->Box(Modifier.size(36.dp).semantics {contentDescription="Element $s"}.clickable {symbol=s;tool=DrawTool.Atom},contentAlignment=Alignment.Center){
                                Box(Modifier.size(27.dp).background(if(s=="H")Color(0xFFE7ECF1)else Color(elementColor(s)),CircleShape).then(if(s==symbol)Modifier.border(2.dp,StudioBlue,CircleShape)else Modifier),contentAlignment=Alignment.Center){Text(s,fontSize=11.sp,color=if(s=="H")StudioInk else Color.White,fontWeight=FontWeight.Medium)}
                            }}
                            Box(Modifier.size(36.dp).semantics {contentDescription="All 118 elements"}.clickable {dialog="Elements"},contentAlignment=Alignment.Center){StudioIcon("more",Modifier.size(18.dp))}
                        }
                    }
                    val editControls: @Composable ()->Unit = {
                        StudioSquare("Undo","undo",{graph=history.undo();project=ChemProject.encode(graph);selected=emptySet();measures=emptyList()},history.canUndo)
                        StudioSquare("Redo","redo",{graph=history.redo();project=ChemProject.encode(graph);selected=emptySet();measures=emptyList()},history.canRedo)
                        StudioSquare("Delete selection","delete",{if(selected.isNotEmpty()){change(graph.delete(selected));selected=emptySet()}else dialog="Clear"},graph.atoms.isNotEmpty())
                        StudioSquare("Fit","fit",{canvas?.fit()})
                    }
                    if(landscape)Row(Modifier.align(Alignment.TopEnd).padding(7.dp),horizontalArrangement=Arrangement.spacedBy(7.dp)){editControls()}
                    else Column(Modifier.align(Alignment.TopEnd).padding(7.dp),verticalArrangement=Arrangement.spacedBy(7.dp)){editControls()}
                    if(!landscape)StudioPanel(Modifier.align(Alignment.BottomCenter).padding(horizontal=9.dp,vertical=9.dp)){
                        Row(Modifier.fillMaxWidth().padding(3.dp),horizontalArrangement=Arrangement.SpaceEvenly){
                            listOf(Triple(6,true,"hexagon"),Triple(5,false,"pentagon"),Triple(6,false,"ring"),Triple(6,true,"ring"),Triple(3,false,"triangle"),Triple(4,false,"square")).forEachIndexed {i,(n,ar,icon)->
                                Box(Modifier.size(39.dp).semantics {contentDescription=if(i==0)"Benzene ring"else "$n member ring preset $i"}.clickable {ring=n;aromatic=ar;tool=DrawTool.Ring;message("Tap the canvas to add a ring.")},contentAlignment=Alignment.Center){StudioIcon(icon,Modifier.size(25.dp))}
                            }
                        }
                    }
                }
                1->if(graph.conformer.isEmpty())Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally){
                    StudioIcon("3d",Modifier.size(42.dp),StudioBlue)
                    Text("No verified 3D conformer",fontWeight=FontWeight.Bold,color=StudioInk,modifier=Modifier.padding(top=12.dp))
                    Text("Choose a library structure or retrieve a PubChem match. Editing connectivity clears the previous 3D model.",fontSize=12.sp,color=StudioMuted,modifier=Modifier.padding(vertical=12.dp))
                    Button(onClick={tab=2}){Text("Browse structures")}
                    TextButton(onClick={work {val ids=repo.search(withContext(Dispatchers.Default){ChemToolkit.writeSmiles(graph)},"SMILES");require(ids.isNotEmpty());val match=repo.remote(ids.first());change(match);tab=1}},enabled=graph.atoms.isNotEmpty()&&!busy){Text("Find PubChem structure match")}
                }else{
                    AndroidView(factory={ChemGlView(it).also {gl=it;it.setZoom(modelZoom)}},update={v->v.graph=graph;v.style=style;v.hydrogens=showH;v.gestureMode=gestureMode;v.selected=measures.toSet();v.onZoomChange={modelZoom=it};v.onSelect={id->measures=if(id in measures)measures-id else (measures+id).takeLast(4)}},modifier=Modifier.fillMaxSize().then(if(landscape)Modifier.padding(top=54.dp)else Modifier).testTag("model-viewport"),onRelease={it.onPause()})
                    val viewerControls: @Composable ()->Unit = {
                        StudioSquare(if(focused)"Exit full screen"else "Full screen","fit",{focused=!focused})
                        StudioSquare("Reset","reset",{gl?.reset();measures=emptyList()})
                        StudioSquare("Model settings","settings",{dialog="Model settings"})
                    }
                    if(landscape)Row(Modifier.align(Alignment.TopEnd).padding(7.dp),horizontalArrangement=Arrangement.spacedBy(7.dp)){viewerControls()}
                    else Column(Modifier.align(Alignment.TopEnd).padding(9.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){viewerControls()}
                    if(measures.isNotEmpty()){
                        val chosen=measures.mapNotNull {id->graph.conformer.firstOrNull {it.id==id}}
                        Text(runCatching {when(chosen.size){1->chosen[0].let {"${it.symbol}${it.id}: ${fmt(it.x)}, ${fmt(it.y)}, ${fmt(it.z)} Å"};2->"Distance: ${fmt(distance(chosen[0],chosen[1]))} Å";3->"Angle: ${fmt(angle(chosen[0],chosen[1],chosen[2]))}°";4->"Dihedral: ${fmt(torsion(chosen[0],chosen[1],chosen[2],chosen[3]))}°";else->"Select atoms to measure"}}.getOrDefault("Undefined measurement"),modifier=Modifier.align(Alignment.BottomCenter).background(Color.White.copy(.94f),RoundedCornerShape(10.dp)).padding(8.dp),fontSize=11.sp,color=StudioInk)
                    }
                }
                2->Column(Modifier.fillMaxSize().background(StudioBackground)){
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom=9.dp),horizontalArrangement=Arrangement.spacedBy(5.dp)){
                        listOf("Common","Amino acids","Sugars","Drug-like","Biomolecules","Favorites","My structures").forEach {group->
                            Text(group,modifier=Modifier.clip(RoundedCornerShape(22.dp)).background(if(libraryGroup==group)StudioBlue else Color(0xFFEFF3F7)).clickable {libraryGroup=group}.padding(horizontal=13.dp,vertical=11.dp),fontSize=10.sp,color=if(libraryGroup==group)Color.White else StudioInk)
                        }
                    }
                    val filtered=library.filter {entry->(query.isBlank()||entry.name.contains(query,true)||entry.cid.toString()==query)&&when(libraryGroup){"Amino acids"->entry.cid==750;"Sugars"->entry.cid in setOf(5793,5984);"Drug-like"->entry.cid in setOf(2244,3672,1983,2519);"Biomolecules"->entry.cid in setOf(5793,5984,750,5997,5957);"Favorites"->saved.any {it.id=="favorite:${entry.cid}"};"My structures"->false;else->true}}
                    LazyVerticalGrid(columns=if(landscape)GridCells.Adaptive(145.dp)else GridCells.Fixed(2),horizontalArrangement=Arrangement.spacedBy(9.dp),verticalArrangement=Arrangement.spacedBy(10.dp),contentPadding=PaddingValues(bottom=10.dp)){
                        gridItems(filtered,key={it.cid}){entry->
                            Surface(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable {work {load(previews[entry.cid]?:repo.bundled(entry))}},shape=RoundedCornerShape(16.dp),color=Color.White,border=BorderStroke(1.dp,Color(0xFFF0F4F8))){
                                Column{Box{MoleculePreview(previews[entry.cid],Modifier.fillMaxWidth().height(if(landscape)100.dp else 128.dp));Box(Modifier.align(Alignment.TopEnd).size(34.dp).semantics {contentDescription="Favorite ${entry.name}"}.clickable {work {val id="favorite:${entry.cid}";if(saved.any {it.id==id})repo.dao.delete(id)else {val molecule=previews[entry.cid]?:repo.bundled(entry);repo.dao.save(SavedStructure(id,molecule.name,ChemProject.encode(molecule),true))}}},contentAlignment=Alignment.Center){StudioIcon("favorite",Modifier.size(15.dp),if(saved.any {it.id=="favorite:${entry.cid}"})StudioBlue else Color(0xFFCFD9E4))}};Column(Modifier.padding(horizontal=12.dp,vertical=11.dp)){Text(entry.name,fontSize=12.sp,fontWeight=FontWeight.SemiBold,color=StudioInk,maxLines=1,overflow=TextOverflow.Ellipsis);Text(previews[entry.cid]?.properties?.get("Molecular Formula")?:"PubChem ${entry.cid}",fontSize=11.sp,color=StudioMuted,modifier=Modifier.padding(top=3.dp))}}
                            }
                        }
                        if(libraryGroup=="My structures")gridItems(saved.filter {it.id!="draft"&&!it.id.startsWith("cache:")&&(query.isBlank()||it.name.contains(query,true))},key={it.id}){item->
                            val itemGraph=remember(item.project){ChemProject.decode(item.project)}
                            Surface(Modifier.clip(RoundedCornerShape(16.dp)).clickable {load(itemGraph)},shape=RoundedCornerShape(16.dp),color=Color.White){Column{MoleculePreview(itemGraph,Modifier.fillMaxWidth().height(128.dp));Text(item.name,fontSize=12.sp,fontWeight=FontWeight.SemiBold,color=StudioInk,modifier=Modifier.padding(10.dp));Row{TextButton(onClick={work {repo.dao.save(item.copy(favorite=!item.favorite))}}){Text(if(item.favorite)"♥"else "♡")};TextButton(onClick={work {repo.dao.delete(item.id)}}){Text("Delete",fontSize=10.sp)}}}}
                        }
                    }
                }
                3->Column(Modifier.fillMaxSize().background(StudioBackground)){
                    val propertyHero: @Composable ()->Unit = {
                    Row(Modifier.fillMaxWidth().padding(vertical=8.dp),verticalAlignment=Alignment.CenterVertically){
                        StudioPanel(Modifier.width(if(landscape)64.dp else 112.dp).height(if(landscape)58.dp else 131.dp)){MoleculePreview(graph,Modifier.fillMaxSize())}
                        Column(Modifier.weight(1f).padding(start=14.dp)){
                            Row(verticalAlignment=Alignment.CenterVertically){Text(graph.name,fontSize=16.sp,fontWeight=FontWeight.Bold,color=StudioInk,modifier=Modifier.weight(1f));Box(Modifier.size(34.dp).semantics {contentDescription="Rename molecule"}.clickable {dialog="Save"},contentAlignment=Alignment.Center){StudioIcon("edit",Modifier.size(16.dp),StudioBlue)}}
                            Text(formula,fontSize=12.sp,color=StudioMuted,modifier=Modifier.padding(vertical=7.dp))
                            if(graph.cid!=null)Row(Modifier.clickable {sourceLink()},verticalAlignment=Alignment.CenterVertically){Text("PubChem CID ${graph.cid}",fontSize=11.sp,color=StudioMuted);StudioIcon("external",Modifier.padding(start=6.dp).size(14.dp),StudioMuted)}
                        }
                    }
                    }
                    if(!landscape)propertyHero()
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical=if(landscape)2.dp else 10.dp)){
                        listOf("Overview","Physical","Spectra","Safety","More").forEach {page->Column(Modifier.clickable {propertyPage=page}.padding(horizontal=9.dp,vertical=8.dp),horizontalAlignment=Alignment.CenterHorizontally){Text(page,fontSize=10.sp,fontWeight=if(page==propertyPage)FontWeight.Bold else FontWeight.Normal,color=if(page==propertyPage)StudioBlue else StudioInk);Box(Modifier.padding(top=9.dp).height(2.dp).width(40.dp).background(if(page==propertyPage)StudioBlue else Color.Transparent))}}
                    }
                    LazyColumn(Modifier.weight(1f).testTag("property-list"),verticalArrangement=Arrangement.spacedBy(12.dp),contentPadding=PaddingValues(bottom=12.dp)){
                        if(landscape)item{propertyHero()}
                        item{
                            val rows=when(propertyPage){
                                "Physical"->listOf("Molar mass" to calculated.second)+graph.properties.filterKeys {it.startsWith("Mass")||it.contains("Log P")||it.contains("Polar")||it.contains("Rotatable")}.map {it.key to it.value}
                                "Identifiers"->listOfNotNull(graph.cid?.let {"PubChem CID" to it.toString()},property("InChI")?.let {"InChI" to it},property("InChIKey")?.let {"InChIKey" to it},smiles?.let {"SMILES" to it})
                                "Validation"->listOf("Supported-rule checks" to graph.validate().joinToString("\n").ifBlank {"No supported-rule violations found."},"Scope" to "Not a complete chemical validity certificate.","Atoms / bonds" to "${graph.atoms.size} / ${graph.bonds.size}")
                                "Spectra"->listOf("Spectral data" to "Not included in this offline compound record.","Source" to "Open the compound’s PubChem record for available experimental spectra.")
                                "Safety"->listOf("Safety data" to "Not included in this offline compound record.","Source" to "Consult the compound’s current source record and supplier safety data sheet.")
                                "More"->listOf("Validation" to graph.validate().joinToString("\n").ifBlank {"No supported-rule violations found."},"Scope" to "Not a complete chemical validity certificate.","Data source" to graph.source,"Retrieved" to (property("Retrieved")?:"Local drawing"),"3D representation" to if(graph.conformer.isNotEmpty())"Computed coordinates; not experimental"else "No source conformer")
                                else->listOfNotNull("Molecular formula" to formula,"Molecular weight" to (property("Molecular Weight")?.let {"$it g/mol"}?:calculated.second),property("IUPAC Name · Preferred")?.let {"IUPAC name" to it},smiles?.let {"SMILES" to it},property("InChI ·")?.let {"InChI" to it},property("InChIKey")?.let {"InChIKey" to it})
                            }
                            StudioPropertyRows(rows,::copyText)
                        }
                        if(propertyPage=="Overview")item{StudioPanel{Column(Modifier.padding(13.dp)){Text("Structure identifiers",fontSize=12.sp,fontWeight=FontWeight.Bold,color=StudioInk,modifier=Modifier.padding(bottom=10.dp));StudioPropertyRows(listOf("PubChem CID" to (graph.cid?.toString()?:"Not assigned"),"Formal charge" to graph.atoms.sumOf {it.charge}.toString(),"Data source" to graph.source),::copyText)}}}
                    }
                    Row(Modifier.fillMaxWidth().padding(vertical=if(landscape)3.dp else 8.dp),horizontalArrangement=Arrangement.spacedBy(7.dp)){
                        listOf(Triple("Copy SMILES","copy",StudioBlue),Triple(if(graph.conformer.isNotEmpty())"3D SDF"else "Export SDF","download",Color(0xFF00A482)),Triple("View in PubChem","external",Color(0xFF5A40DD)),Triple("Share","share",Color(0xFFEAF0F6))).forEachIndexed {i,(label,icon,color)->
                            Column(Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(color).clickable(enabled=graph.atoms.isNotEmpty()&&(i!=2||graph.cid!=null)){when(i){0->work {copyText(withContext(Dispatchers.Default){ChemToolkit.writeSmiles(graph)})};1->{exportFormat=if(graph.conformer.isNotEmpty())"3D SDF"else "SDF";exportFile.launch("${graph.name}.sdf")};2->sourceLink();else->work {val value=withContext(Dispatchers.Default){ChemToolkit.writeSmiles(graph)};context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT,"${graph.name}\n$value"),"Share molecular structure"))}}}.padding(vertical=if(landscape)5.dp else 11.dp,horizontal=3.dp),horizontalAlignment=Alignment.CenterHorizontally){StudioIcon(icon,Modifier.size(20.dp),if(i==3)StudioInk else Color.White);Text(label,fontSize=8.sp,maxLines=2,color=if(i==3)StudioInk else Color.White,modifier=Modifier.padding(top=5.dp))}
                        }
                    }
                }
            }
        }
    }
    Column(Modifier.fillMaxSize().background(StudioBackground)){
        Row(Modifier.fillMaxWidth().heightIn(min=49.dp).padding(horizontal=8.dp),verticalAlignment=Alignment.CenterVertically){
            Box(Modifier.size(40.dp).semantics {contentDescription="Back"}.clickable(onClick=onBack),contentAlignment=Alignment.Center){StudioIcon("back",Modifier.size(20.dp))}
            Text("Draw Chemistry",fontWeight=FontWeight.Bold,fontSize=17.sp,modifier=Modifier.weight(1f),color=StudioInk,maxLines=1)
            if(landscape)TextButton(onClick={searchExpanded=!searchExpanded}){Text(if(searchExpanded)"Hide search"else "Search",fontSize=11.sp)}
            Button(onClick={dialog="Save"},enabled=ready,shape=RoundedCornerShape(24.dp),contentPadding=PaddingValues(horizontal=18.dp,vertical=0.dp),modifier=Modifier.height(35.dp),colors=ButtonDefaults.buttonColors(containerColor=StudioBlue)){Text("Save",fontSize=12.sp)}
            Box(Modifier.size(40.dp).semantics {contentDescription="Molecule actions"}.clickable {dialog="Actions"},contentAlignment=Alignment.Center){StudioIcon("more",Modifier.size(20.dp))}
        }
        if(landscape && !focused){
            Row(Modifier.weight(1f).fillMaxWidth()){
                Column(Modifier.width(284.dp).fillMaxHeight().testTag("landscape-controls").verticalScroll(rememberScrollState())){
                    searchPane()
                    toolPane()
                    footerPane()
                }
                workspace(Modifier.weight(1f).fillMaxHeight())
            }
        }else{
            searchPane()
            toolPane()
            workspace(Modifier.weight(1f).fillMaxWidth())
            footerPane()
        }
    }
    if(dialog.isNotEmpty()){
        when(dialog){
            "Model settings"->StudioDialog("Model settings",{dialog=""}){Row(verticalAlignment=Alignment.CenterVertically){Checkbox(showH,{showH=it});Text("Show hydrogen atoms")};Text("Computed source coordinates in angstroms. Tap atoms to measure distances, angles and dihedrals. Surfaces require separate validated data and are not represented by the space-filling model.",fontSize=12.sp);TextButton(onClick={dialog="Atoms"}){Text("Inspect atoms")}}

            "Reactions"->StudioDialog("Editable reaction templates",{dialog=""}){Text("Opens a new balanced educational scheme. Undo restores the previous drawing. No reaction simulation is performed.",fontSize=12.sp);LazyColumn(Modifier.heightIn(max=350.dp)){items(reactionTemplates){t->TextButton(onClick={dialog="";work{load(withContext(Dispatchers.Default){reactionGraph(t)})}}){Text(t.name)}}}}
            "Annotations"->StudioDialog("Reaction labels & arrows",{dialog=""}){
                var text by remember {mutableStateOf("")};var x by remember {mutableStateOf("0")};var y by remember {mutableStateOf("2")}
                OutlinedTextField(text,{text=it},label={Text("Label, reagent, condition or arrow (→ ⇌)")})
                Row{OutlinedTextField(x,{x=it},label={Text("x")},modifier=Modifier.weight(1f));OutlinedTextField(y,{y=it},label={Text("y")},modifier=Modifier.weight(1f))}
                Button(onClick={val px=x.toDoubleOrNull();val py=y.toDoubleOrNull();if(px!=null&&py!=null&&px.isFinite()&&py.isFinite()&&text.isNotBlank()&&graph.annotations.size<100){change(graph.copy(annotations=graph.annotations+ChemAnnotation(px,py,text.take(200),if(text=="→"||text=="⇌")"arrow"else "text")));text=""}else message("Enter a label and finite drawing coordinates.")}){Text("Add annotation")}
                LazyColumn(Modifier.heightIn(max=200.dp)){items(graph.annotations.withIndex().toList()){(i,n)->Row(verticalAlignment=Alignment.CenterVertically){Text(n.text,modifier=Modifier.weight(1f),fontSize=12.sp);TextButton(onClick={change(graph.copy(annotations=graph.annotations.filterIndexed {index,_->index!=i}))}){Text("Remove")}}}}
            }
            "Atoms"->StudioDialog("Atoms & selection",{dialog=""}){LazyColumn(Modifier.heightIn(max=430.dp)){items(graph.atoms){a->TextButton(onClick={selected=setOf(a.id);if(tab==1)measures=(measures+a.id).distinct().takeLast(4)else inspect=a.id;dialog=""}){Text("${a.symbol}${a.id} · charge ${a.charge} · map ${a.mapping}")}}}}
            "Elements"->StudioDialog("Choose element",{dialog=""}){var filter by remember { mutableStateOf("") };OutlinedTextField(filter,{filter=it},label={Text("Name, symbol or atomic number")});LazyColumn(Modifier.heightIn(max=420.dp)){items(elements.filter { it.name.contains(filter,true)||it.symbol.equals(filter,true)||it.number.toString()==filter }){e->TextButton(onClick={symbol=e.symbol;tool=DrawTool.Atom;dialog=""}){Text("${e.number}  ${e.symbol}  ${e.name}")}}}}
            "Bonds"->StudioDialog("Bond tool",{dialog=""}){listOf("Single" to 1,"Double" to 2,"Triple" to 3,"Wedge" to 1,"Dashed wedge" to 1,"Coordinate" to 1,"Query" to 1).forEach { (name,n)->TextButton(onClick={order=n;stereo=when(name){"Wedge"->"wedge";"Dashed wedge"->"dash";"Coordinate"->"dative";"Query"->"query";else->""};dialog=""}){Text(name)}}}
            "Rings"->StudioDialog("Ring templates",{dialog=""}){LazyColumn(Modifier.heightIn(max=460.dp)){
                items((3..8).toList()){n->TextButton(onClick={ring=n;aromatic=false;tool=DrawTool.Ring;dialog="";message("Tap to place a $n-member ring; tap an atom for a spiro attachment.")}){Text("Cyclo${listOf("propane","butane","pentane","hexane","heptane","octane")[n-3]}")}}
                items(ringTemplates.toList()){(name,smiles)->TextButton(onClick={work { val next=withContext(Dispatchers.Default){ChemToolkit.smiles(smiles,name)};load(next);dialog="" }}){Text(name)}}
            }}
            "Save"->StudioDialog("Save molecule",{dialog=""}){var name by remember { mutableStateOf(graph.name) };OutlinedTextField(name,{name=it},label={Text("Molecule name")},singleLine=true);Button(onClick={work { val next=graph.copy(name=name.trim().ifBlank { "Untitled molecule" });change(next);repo.dao.save(SavedStructure(UUID.randomUUID().toString(),next.name,ChemProject.encode(next)));dialog="";message("Saved offline. Draft recovery is enabled.") }},enabled=name.isNotBlank()&&!busy){Text("Save as new")};Text("A copy is saved; existing library records are preserved.",fontSize=12.sp)}
            "Search"->StudioDialog("PubChem search",{dialog=""}){listOf("Name","CID","SMILES","Formula").forEach { type->TextButton(onClick={searchType=type}){Text(if(type==searchType)"✓ $type" else type)}};Button(onClick={dialog="";work { results=repo.search(query,searchType);resultPage=0;dialog="Results";message("${results.size} results returned (maximum 100).") }}){Text("Search online")};Text("Name also accepts IUPAC names. Network access required; loaded records are cached locally.",fontSize=12.sp)}
            "Results"->StudioDialog("PubChem results",{dialog=""}){LazyColumn(Modifier.heightIn(max=350.dp)){items(results.drop(resultPage*10).take(10)){cid->TextButton(onClick={dialog="";work { load(repo.remote(cid)) }}){Text("Open PubChem CID $cid")}}};Row{TextButton(onClick={resultPage--},enabled=resultPage>0){Text("Previous")};TextButton(onClick={resultPage++},enabled=(resultPage+1)*10<results.size){Text("Next")}}}
            "Clear"->StudioDialog("Clear drawing?",{dialog=""}){Text("Your current drawing can be recovered with Undo.");Button(onClick={change(ChemGraph());selected=emptySet();dialog=""}){Text("Clear drawing")}}
            "Export"->StudioDialog("Export molecule",{dialog=""}){listOf("Project JSON","MOL V2000","SDF","SMILES","SVG","PNG","XYZ coordinates").forEach { f->TextButton(onClick={exportFormat=f;dialog="";if(f=="PNG" && tab!=0){tab=0;message("Switched to 2D. Choose PNG export again when the canvas is visible.")}else exportFile.launch("${graph.name.replace(Regex("[^A-Za-z0-9_-]"),"_")}.${when(f){"MOL V2000"->"mol";"SDF"->"sdf";"SMILES"->"smi";"SVG"->"svg";"PNG"->"png";"XYZ coordinates"->"xyz";else->"json"}}")}){Text(f)}};Text("XYZ exports coordinates only. Import: project JSON, SMILES and MOL/SDF V2000 (first record).",fontSize=11.sp)}
            "Actions"->StudioDialog("Molecule actions",{dialog=""}){LazyColumn(Modifier.heightIn(max=500.dp)){
                val actions=listOf("Highlight functional groups","Reaction templates","Annotations","Inspect atoms","Clean 2D","Add explicit H","Remove explicit H","Check chemistry","Paste fragment","Duplicate drawing","Import document","Export","Clear drawing","About & limitations")
                items(actions){action->TextButton(onClick={dialog="";when(action){
                    "Highlight functional groups"->work{val found=withContext(Dispatchers.Default){ChemToolkit.functionalGroups(graph)};selected=found.values.flatten().toSet();tab=0;message(if(found.isEmpty())"No match among the supported functional-group SMARTS patterns."else "SMARTS matches: ${found.keys.joinToString()}")}
                    "Reaction templates"->dialog="Reactions"
                    "Annotations"->dialog="Annotations"
                    "Inspect atoms"->dialog="Atoms"
                    "Clean 2D"->work {change(withContext(Dispatchers.Default){ChemToolkit.clean(graph)});canvas?.fit()}
                    "Add explicit H","Remove explicit H"->work{change(withContext(Dispatchers.Default){ChemToolkit.hydrogens(graph,action=="Add explicit H")});canvas?.fit()}
                    "Check chemistry"->{val issues=graph.validate();message(issues.joinToString("\n").ifBlank { "No supported-rule violations. Formula: ${runCatching {ChemToolkit.formula(graph)}.getOrDefault("unavailable")}" });propertyPage="More";tab=3}
                    "Paste fragment"->{clipboard?.let { fragment->change(mergeFragment(graph,fragment));canvas?.fit() }?:message("Select and copy a fragment first.")}
                    "Duplicate drawing"->{change(mergeFragment(graph,graph));canvas?.fit()}
                    "Import document"->importFile.launch(arrayOf("*/*"))
                    "Export"->dialog="Export"
                    "Clear drawing"->dialog="Clear"
                    else->dialog="About"
                }}){Text(action)}}
            }}
            "About"->StudioDialog("Draw Chemistry capabilities",{dialog=""}){Text("Native 2D vector editor and OpenGL ES 3D viewer. CDK 2.13 parses SMILES and MOL V2000, assigns supported hydrogen counts and arranges 2D structures. PubChem supplies reference identifiers, properties and available computed conformers.\n\n26 offline compounds. No offline force-field optimizer, protein ribbons, surfaces, orbital calculations, automated reaction mechanisms or thousands-compound library. Seven editable reaction templates are educational balanced schemes. Unsupported chemistry requires specialist review.\n\nCDK: LGPL-2.1-or-later; source https://github.com/cdk/cdk (tag cdk-2.13). License and attribution are included in the application assets. PubChem data: https://pubchem.ncbi.nlm.nih.gov/docs/pug-rest",fontSize=12.sp)}
        }
    }
    bondInspect?.let { bond->StudioDialog("Edit bond ${bond.a}–${bond.b}",{bondInspect=null}){
        val a=graph.atoms.first {it.id==bond.a};val b=graph.atoms.first {it.id==bond.b}
        var length by remember(bond){mutableStateOf(fmt(hypot(b.x-a.x,b.y-a.y)))}
        var direction by remember(bond){mutableStateOf(fmt(Math.toDegrees(atan2(b.y-a.y,b.x-a.x))))}
        Text("2D drawing coordinates; these are not measured molecular bond lengths.",fontSize=11.sp)
        OutlinedTextField(length,{length=it},label={Text("Drawing length")},singleLine=true)
        OutlinedTextField(direction,{direction=it},label={Text("Direction in degrees")},singleLine=true)
        Row{(1..3).forEach { n->TextButton(onClick={change(graph.bond(bond.a,bond.b,n));bondInspect=null}){Text("Order $n")}}}
        Button(onClick={val len=length.toDoubleOrNull();val deg=direction.toDoubleOrNull();if(len!=null && deg!=null && len.isFinite() && deg.isFinite() && len in .1..10.0){val rad=Math.toRadians(deg);change(graph.copy(atoms=graph.atoms.map {if(it.id==b.id)it.copy(x=a.x+len*cos(rad),y=a.y+len*sin(rad))else it}));bondInspect=null}else message("Use a drawing length from 0.1–10 and a finite angle.")}){Text("Apply geometry")}
        TextButton(onClick={change(graph.edited(bonds=graph.bonds-bond));bondInspect=null}){Text("Delete bond")}
    }}
    inspect?.let { id->val atom=graph.atoms.firstOrNull { it.id==id };if(atom!=null)StudioDialog("Edit ${atom.symbol}$id",{inspect=null}){
        var charge by remember(id){mutableStateOf(atom.charge.toString())};var isotope by remember(id){mutableStateOf(atom.isotope?.toString()?:"")};var label by remember(id){mutableStateOf(atom.label)};var h by remember(id){mutableStateOf(atom.hydrogens?.toString()?:"")};var mapping by remember(id){mutableStateOf(atom.mapping.toString())}
        Column(Modifier.verticalScroll(rememberScrollState()).heightIn(max=400.dp)){OutlinedTextField(charge,{charge=it},label={Text("Formal charge")});OutlinedTextField(isotope,{isotope=it},label={Text("Isotope mass number (blank = natural)")});OutlinedTextField(h,{h=it},label={Text("Implicit H (blank = automatic)")});OutlinedTextField(label,{label=it},label={Text("Display alias (element remains ${atom.symbol})")});OutlinedTextField(mapping,{mapping=it},label={Text("Atom mapping number")})}
        Button(onClick={val q=charge.toIntOrNull();val iso=if(isotope.isBlank())null else isotope.toIntOrNull();val hc=if(h.isBlank())null else h.toIntOrNull();val map=mapping.toIntOrNull();if(q==null||q !in -8..8 || (isotope.isNotBlank()&&(iso==null||iso !in 1..350)) ||(h.isNotBlank()&&(hc==null||hc !in 0..8))||map==null||map !in 0..999){message("Enter valid charge, isotope, hydrogen and mapping values.")}else {change(graph.edited(atoms=graph.atoms.map { if(it.id==id)it.copy(charge=q,isotope=iso,hydrogens=hc,label=label.take(12),mapping=map)else it }));inspect=null}}){Text("Apply")}
    }}
}
@Composable private fun Property(label:String,value:String){Column(Modifier.fillMaxWidth().padding(vertical=9.dp)){Text(label,fontSize=11.sp,color=Color(0xFF61768B));Text(value,fontSize=14.sp,color=Ink);HorizontalDivider(Modifier.padding(top=8.dp),color=Color(0xFFECF0F5))}}
@Composable private fun StudioDialog(title:String,onDismiss:()->Unit,content:@Composable ColumnScope.()->Unit){Dialog(onDismissRequest=onDismiss){Surface(shape=RoundedCornerShape(20.dp),color=Color.White){Column(Modifier.padding(18.dp).heightIn(max=650.dp).verticalScroll(rememberScrollState())){Text(title,fontWeight=FontWeight.Bold,fontSize=19.sp,color=Ink,modifier=Modifier.padding(bottom=10.dp));content();TextButton(onClick=onDismiss,modifier=Modifier.align(Alignment.End)){Text("Close")}}}}}
private fun fmt(value:Double)=String.format(Locale.US,"%.3f",value)
internal val ringTemplates=linkedMapOf("Benzene" to "c1ccccc1","Pyridine" to "n1ccccc1","Pyrimidine" to "n1cnccc1","Pyrrole" to "[nH]1cccc1","Furan" to "o1cccc1","Thiophene" to "s1cccc1","Imidazole" to "c1ncc[nH]1","Indole" to "c1ccc2[nH]ccc2c1","Naphthalene" to "c1ccc2ccccc2c1")
internal fun mergeFragment(base:ChemGraph,fragment:ChemGraph):ChemGraph {require(base.atoms.size+fragment.atoms.size<=500){"Maximum 500 atoms."};val offset=base.atoms.maxOfOrNull { it.id }?:0;val shift=if(base.atoms.isEmpty())0.0 else base.atoms.maxOf { it.x }-(fragment.atoms.minOfOrNull { it.x }?:0.0)+2.0;return base.edited(base.atoms+fragment.atoms.map { it.copy(id=it.id+offset,x=it.x+shift) },base.bonds+fragment.bonds.map { it.copy(a=it.a+offset,b=it.b+offset) })}
internal fun chemSvg(g:ChemGraph):String {
    fun xml(s:String)=s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;")
    val xs=g.atoms.map {it.x}+g.annotations.map {it.x};val ys=g.atoms.map {it.y}+g.annotations.map {it.y}
    val minX=xs.minOrNull()?:0.0;val minY=ys.minOrNull()?:0.0;val maxX=xs.maxOrNull()?:1.0;val maxY=ys.maxOrNull()?:1.0
    fun x(a:ChemAtom)=(a.x-minX)*50+30
    fun y(a:ChemAtom)=(maxY-a.y)*50+30
    return buildString {
        append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"${(maxX-minX)*50+60}\" height=\"${(maxY-minY)*50+60}\"><rect width=\"100%\" height=\"100%\" fill=\"white\"/>")
        fun line(x1:Double,y1:Double,x2:Double,y2:Double,dashed:Boolean=false){append("<line x1=\"$x1\" y1=\"$y1\" x2=\"$x2\" y2=\"$y2\" stroke=\"#40536a\" stroke-width=\"2\"${if(dashed)" stroke-dasharray=\"3 3\""else ""}/>")}
        g.bonds.forEach {b->
            val a=g.atoms.first {it.id==b.a};val c=g.atoms.first {it.id==b.b}
            val dx=x(c)-x(a);val dy=y(c)-y(a);val length=hypot(dx,dy).coerceAtLeast(.01);val nx= -dy/length*4;val ny=dx/length*4
            when(b.stereo){
                "wedge"->append("<polygon points=\"${x(a)},${y(a)} ${x(c)+nx},${y(c)+ny} ${x(c)-nx},${y(c)-ny}\" fill=\"#40536a\"/>")
                "dash"->for(i in 1..7){val t=i/8.0;val px=x(a)+dx*t;val py=y(a)+dy*t;line(px-nx*t,py-ny*t,px+nx*t,py+ny*t)}
                "dative"->{line(x(a),y(a),x(c),y(c));line(x(c),y(c),x(c)-dx/length*12+nx,y(c)-dy/length*12+ny);line(x(c),y(c),x(c)-dx/length*12-nx,y(c)-dy/length*12-ny)}
                else->{val count=if(b.aromatic)1 else b.order;repeat(count){i->val o=i-(count-1)/2.0;line(x(a)+nx*o,y(a)+ny*o,x(c)+nx*o,y(c)+ny*o,b.stereo=="query")}}
            }
            if(b.aromatic)line(x(a)+nx,y(a)+ny,x(c)+nx,y(c)+ny,true)
        }
        g.annotations.forEach {n->val a=ChemAtom(0,"",n.x,n.y);append("<text x=\"${x(a)}\" y=\"${y(a)}\" text-anchor=\"middle\" font-size=\"${if(n.kind=="arrow")28 else 12}\">${xml(n.text)}</text>")}
        g.atoms.forEach {a->
            append("<circle cx=\"${x(a)}\" cy=\"${y(a)}\" r=\"11\" fill=\"white\"/><text x=\"${x(a)}\" y=\"${y(a)+6}\" text-anchor=\"middle\" font-family=\"sans-serif\" font-size=\"18\" fill=\"#${Integer.toHexString(elementColor(a.symbol)).takeLast(6)}\">")
            a.isotope?.let {append("<tspan baseline-shift=\"super\" font-size=\"10\">$it</tspan>")}
            append(xml(a.label.ifBlank {a.symbol}))
            if(a.charge!=0)append("<tspan baseline-shift=\"super\" font-size=\"10\">${if(abs(a.charge)==1)""else abs(a.charge)}${if(a.charge>0)"+"else "−"}</tspan>")
            append("</text>")
        }
        append("</svg>")
    }
}
