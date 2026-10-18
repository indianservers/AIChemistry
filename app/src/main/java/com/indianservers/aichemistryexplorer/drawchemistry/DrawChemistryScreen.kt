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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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

private val Ink=Color(0xFF172C43)
private val Accent=Color(0xFF1879C8)
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
    MaterialTheme(colorScheme=lightColorScheme(primary=Accent,background=Color.White,surface=Color.White,onSurface=Ink)){
        Studio(elements,onBack)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun Studio(elements:List<Element>,onBack:()->Unit){
    val context=LocalContext.current;val scope=rememberCoroutineScope()
    val configuration=LocalConfiguration.current
    val landscape=configuration.screenWidthDp>configuration.screenHeightDp
    var searchExpanded by rememberSaveable { mutableStateOf(false) }
    val repo=remember { ChemRepository(context,elements.sortedBy { it.number }.map { it.symbol }) }
    var project by rememberSaveable { mutableStateOf("") };var ready by remember { mutableStateOf(false) }
    var graph by remember { mutableStateOf(if(project.isBlank())ChemGraph()else ChemProject.decode(project)) }
    val history=remember { ChemHistory(graph) }
    var tab by rememberSaveable { mutableIntStateOf(0) };var tool by remember { mutableStateOf(DrawTool.Atom) };var symbol by rememberSaveable { mutableStateOf("C") }
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
    val library=remember { repo.library() }
    val calculated by produceState(initialValue=Pair("Calculating…","Calculating…"),key1=graph){
        val snapshot=graph
        if(snapshot.annotations.any {it.kind=="arrow"}){value=Pair("Reaction scheme · multiple species","Not a single molecule");return@produceState}
        value=withContext(Dispatchers.Default){Pair(runCatching { ChemToolkit.formula(snapshot) }.getOrElse { "Unavailable for this graph" },runCatching { "${fmt(ChemToolkit.mass(snapshot))} g/mol" }.getOrElse { "Unavailable" })}
    }
    fun change(next:ChemGraph){if(graph.sourceMol.isNotBlank() && next.sourceMol.isBlank() && graph.atoms.isNotEmpty())status="Structure edited: reference properties/3D cleared. Review stereochemistry before export.";history.change(next);graph=next;project=ChemProject.encode(next);selected=selected.intersect(next.atoms.map { it.id }.toSet());measures=emptyList()}
    fun message(t:String){status=t}
    fun work(block:suspend ()->Unit){if(busy)return;busy=true;scope.launch {try{block()}catch(e:Exception){message(e.message?:"Operation failed; drawing retained.")}finally{busy=false}}}
    fun load(next:ChemGraph){change(next);tab=0;selected=emptySet();status="${next.name} · ${next.atoms.size} atoms";scope.launch { delay(60);canvas?.fit() }}
    LaunchedEffect(Unit){
        if(project.isBlank()){repo.dao.get("draft")?.let { runCatching { ChemProject.decode(it.project) }.onSuccess { graph=it;history.change(it);project=it.let(ChemProject::encode);status="${it.name} · ${it.atoms.size} atoms" } }}
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
        withContext(Dispatchers.IO){context.contentResolver.openOutputStream(uri)!!.use { out->if(bitmap!=null){bitmap.compress(Bitmap.CompressFormat.PNG,100,out);bitmap.recycle()}else out.write(when(format){"MOL V2000"->ChemToolkit.writeMol(snapshot);"SDF"->ChemToolkit.writeMol(snapshot)+"\n$$$$\n";"SMILES"->ChemToolkit.writeSmiles(snapshot);"SVG"->chemSvg(snapshot);"XYZ coordinates"->{require(snapshot.conformer.isNotEmpty()){ "A source 3D conformer is required." };"${snapshot.conformer.size}\n${snapshot.name} · ${snapshot.source}, coordinates in angstroms; no bond data\n"+snapshot.conformer.joinToString("\n"){"${it.symbol} ${it.x} ${it.y} ${it.z}"}};else->ChemProject.encode(snapshot)}.toByteArray()) }}
        message("Export saved: $format")
    }}
    Column(Modifier.fillMaxSize().background(Color.White)){
        Row(Modifier.fillMaxWidth().heightIn(min=48.dp).padding(horizontal=4.dp),verticalAlignment=Alignment.CenterVertically){
            TextButton(onClick=onBack){Text("‹ Back")};Text("Draw Chemistry",fontWeight=FontWeight.Bold,fontSize=19.sp,modifier=Modifier.weight(1f),color=Ink)
            if(landscape)TextButton(onClick={searchExpanded=!searchExpanded}){Text(if(searchExpanded)"Hide search"else "Search")}
            TextButton(onClick={dialog="Save"},enabled=ready){Text("Save")};TextButton(onClick={dialog="Actions"}){Text("•••")}
        }
        if(!landscape || searchExpanded)Row(Modifier.padding(horizontal=8.dp).fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){
            OutlinedTextField(query,{query=it},modifier=Modifier.weight(1f),singleLine=true,placeholder={Text("Search compound or enter SMILES",fontSize=12.sp)},textStyle=LocalTextStyle.current.copy(fontSize=13.sp))
            TextButton(onClick={dialog="Search"},enabled=!busy && query.isNotBlank()){Text("Find")}
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.SpaceEvenly){
            listOf("Draw 2D","Build 3D","Structures","Properties").forEachIndexed { i,t->TextButton(onClick={tab=i}){Text(t,color=if(tab==i)Accent else Color(0xFF61748A),fontWeight=if(tab==i)FontWeight.Bold else FontWeight.Normal,fontSize=12.sp)} }
        }
        if(busy)LinearProgressIndicator(Modifier.fillMaxWidth())
        Box(Modifier.weight(1f).fillMaxWidth()){
            when(tab){
                0->{AndroidView(factory={ChemCanvasView(it).also { v->canvas=v }},modifier=Modifier.fillMaxSize(),update={v->v.graph=graph;v.selected=selected;v.tool=tool;v.element=symbol;v.bondOrder=order;v.bondStereo=stereo;v.ringSize=ring;v.aromatic=aromatic;v.onBondInspect={bondInspect=it};v.onChange={change(it)};v.onSelect={selected=it};v.onInspect={inspect=it}})
                    val toolControls: @Composable ()->Unit = {
                        listOf(DrawTool.Select to "Select",DrawTool.Atom to "Atom",DrawTool.Bond to "Bond",DrawTool.Ring to "Ring",DrawTool.Lasso to "Lasso",DrawTool.Erase to "Erase").forEach { (mode,label)->
                            TextButton(onClick={tool=mode;if(mode==DrawTool.Bond)dialog="Bonds";if(mode==DrawTool.Ring)dialog="Rings"},modifier=Modifier.heightIn(min=44.dp)){Text(label,fontSize=11.sp,color=if(tool==mode)Accent else Ink,fontWeight=if(tool==mode)FontWeight.Bold else FontWeight.Normal)}
                        }
                    }
                    if(landscape)Row(Modifier.align(Alignment.TopStart).padding(4.dp).widthIn(max=430.dp).horizontalScroll(rememberScrollState()).background(Color(0xF5F1F6FB),RoundedCornerShape(14.dp))){toolControls()}
                    else Column(Modifier.align(Alignment.TopStart).padding(8.dp).width(62.dp).background(Color(0xF5F1F6FB),RoundedCornerShape(14.dp))){toolControls()}
                    Row(Modifier.align(Alignment.TopEnd).padding(4.dp).background(Color.White.copy(.9f),RoundedCornerShape(10.dp))){
                        TextButton(onClick={graph=history.undo();project=ChemProject.encode(graph);measures=emptyList();selected=emptySet()},enabled=history.canUndo){Text("Undo",fontSize=11.sp)}
                        TextButton(onClick={graph=history.redo();project=ChemProject.encode(graph);measures=emptyList();selected=emptySet()},enabled=history.canRedo){Text("Redo",fontSize=11.sp)}
                    }
                    TextButton(onClick={canvas?.fit()},modifier=Modifier.align(Alignment.BottomEnd)){Text("Fit")}
                    if(graph.atoms.isEmpty())Text("Tap to start a molecule\nPinch to zoom · two fingers to pan",modifier=Modifier.align(Alignment.Center).padding(start=60.dp),color=Color(0xFF8092A5),fontSize=12.sp)
                }
                1->if(graph.conformer.isEmpty())Column(Modifier.align(Alignment.Center).padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally){
                    Text("No verified 3D conformer",fontWeight=FontWeight.Bold,color=Ink)
                    Text("Open a bundled molecule or retrieve its PubChem record. Edited drawings invalidate reference coordinates. Offline force-field generation is not available.",fontSize=13.sp,modifier=Modifier.padding(vertical=12.dp),color=Ink)
                    Button(onClick={tab=2}){Text("Browse structures")}
                    TextButton(onClick={work { val smiles=withContext(Dispatchers.Default){ChemToolkit.writeSmiles(graph)};val ids=repo.search(smiles,"SMILES");require(ids.isNotEmpty());val matched=repo.remote(ids.first());change(matched);tab=1;message("Loaded PubChem match ${matched.cid}; inspect identity and stereochemistry before use.") }},enabled=graph.atoms.isNotEmpty()&&!busy){Text("Find PubChem structure match")}
                }else {
                    AndroidView(factory={ChemGlView(it).also { gl=it }},update={v->v.graph=graph;v.style=style;v.hydrogens=showH;v.selected=measures.toSet();v.onSelect={id->measures=if(id in measures)measures-id else (measures+id).takeLast(4)}},modifier=Modifier.fillMaxSize(),onRelease={it.onPause()})
                    val modelStyle: @Composable ()->Unit = {TextButton(onClick={style=(style+1)%4}){Text(listOf("Ball & stick","Space filling","Wireframe","Licorice")[style],fontSize=12.sp)}}
                    val modelControls: @Composable ()->Unit = {TextButton(onClick={showH=!showH}){Text(if(showH)"Hide H"else "Show H",fontSize=12.sp)};TextButton(onClick={gl?.reset();measures=emptyList()}){Text("Reset",fontSize=12.sp)}}
                    if(landscape)Row(Modifier.align(Alignment.TopStart).padding(4.dp).background(Color.White.copy(.94f),RoundedCornerShape(12.dp))){modelStyle();modelControls()}
                    else Column(Modifier.align(Alignment.TopStart).padding(8.dp).background(Color.White.copy(.94f),RoundedCornerShape(12.dp))){modelStyle();Row{modelControls()}}
                    Column(Modifier.align(Alignment.BottomStart).fillMaxWidth().background(Color.White.copy(.94f)).padding(if(landscape)4.dp else 8.dp)){
                        if(!landscape)Text(if(graph.source.startsWith("PubChem"))"PubChem computed conformer · coordinates in Å"else "Imported coordinates · source supplied by project · Å",fontSize=11.sp,color=Color(0xFF60758A))
                        val chosen=measures.mapNotNull { id->graph.conformer.firstOrNull { it.id==id } }
                        Text((if(landscape)if(graph.source.startsWith("PubChem"))"PubChem computed · "else "Imported · "else "")+runCatching { when(chosen.size){1->chosen[0].let { "${it.symbol}${it.id}  (${fmt(it.x)}, ${fmt(it.y)}, ${fmt(it.z)}) Å" };2->"Distance: ${fmt(distance(chosen[0],chosen[1]))} Å";3->"Angle: ${fmt(angle(chosen[0],chosen[1],chosen[2]))}°";4->"Dihedral: ${fmt(torsion(chosen[0],chosen[1],chosen[2],chosen[3]))}°";else->"Tap 2–4 atoms in order to measure."} }.getOrElse { "Selected atoms do not define a valid measurement." },fontSize=13.sp,color=Ink)
                    }
                }
                2->Column(Modifier.fillMaxSize()){
                    Row(Modifier.horizontalScroll(rememberScrollState())){listOf("All","Basic chemistry","Hydrocarbons","Functional groups","Biomolecules","Pharmaceuticals","My structures").forEach { c->FilterChip(selected=category==c,onClick={category=c},label={Text(c,fontSize=11.sp)},modifier=Modifier.padding(3.dp))}}
                    Row(verticalAlignment=Alignment.CenterVertically){Checkbox(favoritesOnly,{favoritesOnly=it});Text("Favorites only",fontSize=12.sp,color=Ink);Text("${library.size} verified source records",fontSize=11.sp,color=Color.Gray,modifier=Modifier.padding(start=8.dp))}
                    LazyColumn(Modifier.fillMaxSize()){
                        if(category!="My structures")items(library.filter { (category=="All"||it.category==category)&&(query.isBlank()||it.name.contains(query,true)||it.cid.toString()==query)&&(!favoritesOnly||saved.any { s->s.id=="favorite:${it.cid}" }) },key={"lib:${it.cid}"}){entry->
                            ListItem(headlineContent={Text(entry.name,fontSize=15.sp)},supportingContent={Text("${entry.category} · PubChem CID ${entry.cid}",fontSize=11.sp)},modifier=Modifier.clickable { work { load(repo.bundled(entry)) } },trailingContent={TextButton(onClick={work { val id="favorite:${entry.cid}";if(saved.any { it.id==id })repo.dao.delete(id)else {val g=repo.bundled(entry);repo.dao.save(SavedStructure(id,g.name,ChemProject.encode(g),true))} }}){Text(if(saved.any { it.id=="favorite:${entry.cid}" })"♥" else "♡")}})
                        }
                        items(saved.filter { it.id!="draft" && !it.id.startsWith("cache:") && !it.id.startsWith("favorite:") && (query.isBlank()||it.name.contains(query,true)) && (!favoritesOnly||it.favorite) },key={it.id}){s->ListItem(headlineContent={Text(s.name)},supportingContent={Text("Saved locally · ${if(s.favorite)"favorite" else "personal structure"}",fontSize=11.sp)},modifier=Modifier.clickable { load(ChemProject.decode(s.project)) },trailingContent={Row{TextButton(onClick={work {repo.dao.save(s.copy(favorite=!s.favorite))}}){Text(if(s.favorite)"♥" else "♡")};TextButton(onClick={work {repo.dao.delete(s.id)}}){Text("Delete",fontSize=11.sp)}}})}
                    }
                }
                3->LazyColumn(Modifier.fillMaxSize().padding(horizontal=16.dp)){
                    item {Text(graph.name,fontSize=22.sp,fontWeight=FontWeight.Bold,color=Ink);Text(graph.source,fontSize=12.sp,color=Color.Gray)}
                    item {Property("Calculated formula",calculated.first);Property("Calculated average molar mass",calculated.second);Property("Atoms / bonds","${graph.atoms.size} / ${graph.bonds.size}");Property("Validation",graph.validate().joinToString("\n").ifBlank { "No supported-rule violations found. Not a complete chemical validity certificate." })}
                    items(graph.properties.toList()){(k,v)->Property("PubChem computed/reference · $k",v)}
                    item {Text("3D conformers are computed models, not experimental structures. No force-field optimization, electron density or orbital calculation is performed here.",fontSize=12.sp,color=Color.Gray,modifier=Modifier.padding(vertical=16.dp))}
                }
            }
        }
        if(tab==0){
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).background(Color(0xFFF4F7FB)),verticalAlignment=Alignment.CenterVertically){
                listOf("C","H","O","N","S","P","F","Cl","Br","I","B","Si","Na","K","Mg","Ca","Fe","Cu","Zn").forEach { s->TextButton(onClick={symbol=s;tool=DrawTool.Atom}){Text(s,color=Color(elementColor(s)),fontWeight=if(s==symbol)FontWeight.Black else FontWeight.Normal)} };TextButton(onClick={dialog="Elements"}){Text("All 118")}
            }
            if(selected.isNotEmpty())Row(Modifier.horizontalScroll(rememberScrollState())){
                TextButton(onClick={inspect=selected.first()}){Text("Edit atom")}
                TextButton(onClick={change(graph.delete(selected));selected=emptySet()}){Text("Delete ${selected.size}")}
                TextButton(onClick={clipboard=graph.copy(atoms=graph.atoms.filter { it.id in selected },bonds=graph.bonds.filter { it.a in selected && it.b in selected },conformer=emptyList(),sourceMol="",properties=emptyMap(),cid=null);message("Fragment copied locally.")}){Text("Copy")}
                TextButton(onClick={val ids=selected;val cx=graph.atoms.filter { it.id in ids }.map { it.x }.average();val cy=graph.atoms.filter { it.id in ids }.map { it.y }.average();change(graph.copy(atoms=graph.atoms.map { if(it.id in ids){val x=it.x-cx;val y=it.y-cy;it.copy(x=cx+x*cos(PI/12)-y*sin(PI/12),y=cy+x*sin(PI/12)+y*cos(PI/12))}else it }))}){Text("Rotate 15°")}
            }
        }
        Text(status,modifier=Modifier.fillMaxWidth().padding(horizontal=12.dp,vertical=if(landscape)2.dp else 5.dp),fontSize=11.sp,maxLines=if(landscape)1 else 2,color=Color(0xFF60758A))
    }
    if(dialog.isNotEmpty()){
        when(dialog){
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
                    "Check chemistry"->{val issues=graph.validate();message(issues.joinToString("\n").ifBlank { "No supported-rule violations. Formula: ${runCatching {ChemToolkit.formula(graph)}.getOrDefault("unavailable")}" });tab=3}
                    "Paste fragment"->{clipboard?.let { fragment->change(mergeFragment(graph,fragment));canvas?.fit() }?:message("Select and copy a fragment first.")}
                    "Duplicate drawing"->{change(mergeFragment(graph,graph));canvas?.fit()}
                    "Import document"->importFile.launch(arrayOf("*/*"))
                    "Export"->dialog="Export"
                    "Clear drawing"->dialog="Clear"
                    else->dialog="About"
                }}){Text(action)}}
            }}
            "About"->StudioDialog("Draw Chemistry capabilities",{dialog=""}){Text("Native 2D vector editor and OpenGL ES 3D viewer. CDK 2.13 parses SMILES and MOL V2000, assigns supported hydrogen counts and arranges 2D structures. PubChem supplies reference identifiers, properties and available computed conformers.\n\n25 offline compounds. No offline force-field optimizer, protein ribbons, surfaces, orbital calculations, automated reaction mechanisms or thousands-compound library. Seven editable reaction templates are educational balanced schemes. Unsupported chemistry requires specialist review.\n\nCDK: LGPL-2.1-or-later; source https://github.com/cdk/cdk (tag cdk-2.13). License and attribution are included in the application assets. PubChem data: https://pubchem.ncbi.nlm.nih.gov/docs/pug-rest",fontSize=12.sp)}
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
