package com.indianservers.aichemistryexplorer.drawchemistry

import org.json.JSONArray
import org.json.JSONObject
import org.openscience.cdk.silent.SilentChemObjectBuilder
import org.openscience.cdk.stereo.StereoElementFactory
import org.openscience.cdk.smarts.SmartsPattern
import org.openscience.cdk.smiles.SmilesParser
import org.openscience.cdk.smiles.SmilesGenerator
import org.openscience.cdk.interfaces.IAtomContainer
import org.openscience.cdk.interfaces.IBond
import org.openscience.cdk.layout.StructureDiagramGenerator
import org.openscience.cdk.io.MDLV2000Reader
import org.openscience.cdk.io.MDLV2000Writer
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator
import org.openscience.cdk.tools.CDKHydrogenAdder
import java.io.StringReader
import java.io.StringWriter
import kotlin.math.*

internal data class ChemAtom(val id: Int, val symbol: String, val x: Double, val y: Double, val z: Double = 0.0,
    val charge: Int = 0, val isotope: Int? = null, val hydrogens: Int? = null, val label: String = "", val mapping: Int = 0)
internal data class ChemBond(val a: Int, val b: Int, val order: Int = 1, val stereo: String = "", val aromatic: Boolean = false)
internal data class ChemAnnotation(val x:Double,val y:Double,val text:String,val kind:String="text")
internal data class ChemGraph(val name: String = "Untitled molecule", val atoms: List<ChemAtom> = emptyList(),
    val bonds: List<ChemBond> = emptyList(), val source: String = "User drawing", val cid: Int? = null,
    val properties: Map<String,String> = emptyMap(), val conformer: List<ChemAtom> = emptyList(), val sourceMol: String = "", val annotations:List<ChemAnnotation> = emptyList()) {
    fun edited(atoms: List<ChemAtom> = this.atoms, bonds: List<ChemBond> = this.bonds) = copy(atoms=atoms,bonds=bonds,
        cid=null, properties=emptyMap(),conformer=emptyList(),sourceMol="",source="User drawing · reference properties invalidated")
    fun addAtom(symbol: String, x: Double, y: Double): ChemGraph = edited(atoms + ChemAtom((atoms.maxOfOrNull { it.id } ?: 0)+1,symbol,x,y))
    fun bond(a: Int,b: Int,order: Int,stereo: String=""): ChemGraph {
        require(a!=b && atoms.any { it.id==a } && atoms.any { it.id==b })
        return edited(bonds=bonds.filterNot { setOf(it.a,it.b)==setOf(a,b) }+ChemBond(a,b,order,stereo))
    }
    fun delete(ids: Set<Int>) = edited(atoms.filterNot { it.id in ids },bonds.filterNot { it.a in ids || it.b in ids })
    fun validate(): List<String> {
        val issues=mutableListOf<String>()
        if(atoms.size>500) issues+="Large structure: interactive editing limited to 500 atoms."
        if(atoms.any { !it.x.isFinite() || !it.y.isFinite() || !it.z.isFinite() }) issues+="Invalid coordinates."
        if(atoms.map { it.id }.distinct().size!=atoms.size) issues+="Duplicate atom identifiers."
        if(bonds.any { it.a==it.b || atoms.none { a->a.id==it.a } || atoms.none { a->a.id==it.b } }) issues+="Invalid bond connectivity."
        if(bonds.map { setOf(it.a,it.b) }.distinct().size!=bonds.size)issues+="Duplicate bonds."
        atoms.forEach { a ->
            val order=bonds.filter { it.a==a.id || it.b==a.id }.sumOf { it.order }
            val max=when(a.symbol){"H","F","Cl","Br","I"->if(a.charge==0)1 else null;"C"->4;"N"->if(a.charge>0)4 else 3;"O"->if(a.charge>0)3 else if(a.charge<0)1 else 2;else->null}
            if(max!=null && order>max)issues+="${a.symbol}${a.id}: bond order sum $order exceeds supported valence $max."
        }
        if(atoms.any { it.symbol !in setOf("H","C","N","O","F","Cl","Br","I","B","Si","P","S") }) issues+="Metal / uncommon-element valence requires specialist review; not certified by this validator."
        if(bonds.any { it.stereo=="dative" || it.stereo=="query" })issues+="Coordinate/query bonds are annotations; formula and SMILES conversion are unavailable."
        return issues
    }
}
internal class ChemHistory(initial: ChemGraph) {
    var current=initial; private set
    private val past=ArrayDeque<ChemGraph>(); private val future=ArrayDeque<ChemGraph>()
    val canUndo get()=past.isNotEmpty(); val canRedo get()=future.isNotEmpty()
    fun change(next: ChemGraph){if(next==current)return;past.addLast(current);if(past.size>100)past.removeFirst();current=next;future.clear()}
    fun undo(): ChemGraph {if(past.isNotEmpty()){future.addLast(current);current=past.removeLast()};return current}
    fun redo(): ChemGraph {if(future.isNotEmpty()){past.addLast(current);current=future.removeLast()};return current}
}
internal object ChemToolkit {
    private val builder=SilentChemObjectBuilder.getInstance()
    fun smiles(text: String,name: String="Imported SMILES"): ChemGraph {
        require(text.length in 1..20000){"SMILES must contain 1–20,000 characters."}
        val mol=SmilesParser(builder).parseSmiles(text)
        require(mol.atomCount<=500){"Maximum 500 atoms."}
        StructureDiagramGenerator().generateCoordinates(mol)
        return fromCdk(mol,name)
    }
    fun mol(text: String): ChemGraph {
        require(text.length<=2_000_000){"File exceeds 2 MB."}
        require(!text.contains("V3000")){"V3000 is not supported; use V2000, SMILES or project JSON."}
        val mol=MDLV2000Reader(StringReader(text)).use { it.read(builder.newAtomContainer()) }
        require(mol.atomCount in 1..500){"MOL requires 1–500 atoms."}
        return fromCdk(mol,"Imported MOL")
    }
    fun toCdk(g: ChemGraph): IAtomContainer {
        require(g.bonds.none { it.stereo in setOf("dative","query") }){"Coordinate/query bond conversion is unsupported."}
        if(g.sourceMol.isNotEmpty()){
            val mol=MDLV2000Reader(StringReader(g.sourceMol)).use { it.read(builder.newAtomContainer()) }
            require(mol.atomCount==g.atoms.size){"Source graph mismatch."}
            g.atoms.forEachIndexed { i,a->mol.getAtom(i).point2d=javax.vecmath.Point2d(a.x,a.y) };return mol
        }
        val mol=builder.newAtomContainer()
        g.atoms.forEach { a ->
            val atom=builder.newAtom();atom.symbol=a.symbol;atom.formalCharge=a.charge;atom.massNumber=a.isotope
            atom.point2d=javax.vecmath.Point2d(a.x,a.y);atom.implicitHydrogenCount=a.hydrogens
            atom.mapIdx=a.mapping;mol.addAtom(atom)
        }
        g.bonds.forEach { b->
            val bond=builder.newBond();bond.setAtoms(arrayOf(mol.getAtom(g.atoms.indexOfFirst { it.id==b.a }),mol.getAtom(g.atoms.indexOfFirst { it.id==b.b })))
            bond.order=when(b.order){2->IBond.Order.DOUBLE;3->IBond.Order.TRIPLE;else->IBond.Order.SINGLE}
            bond.setIsAromatic(b.aromatic)
            bond.stereo=when(b.stereo){"wedge"->IBond.Stereo.UP;"dash"->IBond.Stereo.DOWN;else->IBond.Stereo.NONE}
            mol.addBond(bond)
        }
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol)
        CDKHydrogenAdder.getInstance(builder).addImplicitHydrogens(mol)
        g.atoms.forEachIndexed { i,a->if(a.hydrogens!=null)mol.getAtom(i).implicitHydrogenCount=a.hydrogens }
        if(g.bonds.any {it.stereo=="wedge"||it.stereo=="dash"})mol.setStereoElements(StereoElementFactory.using2DCoordinates(mol).createAll())
        return mol
    }
    fun fromCdk(mol:IAtomContainer,name:String): ChemGraph {
        val atoms=mol.atoms().mapIndexed { i,a -> ChemAtom(i+1,a.symbol,a.point2d?.x?:i*1.5,a.point2d?.y?:0.0,
            charge=a.formalCharge?:0,isotope=a.massNumber,hydrogens=a.implicitHydrogenCount,mapping=a.mapIdx) }
        val bonds=mol.bonds().map { b -> ChemBond(mol.indexOf(b.begin)+1,mol.indexOf(b.end)+1,b.order?.numeric()?:1,
            when(b.stereo){IBond.Stereo.UP->"wedge";IBond.Stereo.DOWN->"dash";else->""},b.isAromatic) }
        val writer=StringWriter();MDLV2000Writer(writer).use { it.write(mol) }
        return ChemGraph(name,atoms,bonds,source="CDK parsed structure",sourceMol=writer.toString())
    }
    fun functionalGroups(g:ChemGraph):Map<String,Set<Int>> {
        val mol=toCdk(g);SmartsPattern.prepare(mol)
        val patterns=linkedMapOf("Alcohol" to "[OX2H][CX4]","Phenol" to "[OX2H]c","Carbonyl" to "[CX3]=[OX1]","Carboxylic acid" to "[CX3](=[OX1])[OX2H1]","Ester" to "[CX3](=[OX1])[OX2][#6]","Amide" to "[CX3](=[OX1])[NX3]","Amine" to "[NX3;!$(N-C=O)]","Nitrile" to "[CX2]#[NX1]","Alkene" to "[CX3]=[CX3]")
        return patterns.mapValues {(_,smarts)->SmartsPattern.create(smarts).matchAll(mol).uniqueAtoms().toArray().flatMap {it.toList()}.map {g.atoms[it].id}.toSet()}.filterValues {it.isNotEmpty()}
    }
    fun writeMol(g:ChemGraph,coordinates3D:Boolean=false):String {
        val mol=toCdk(g)
        if(coordinates3D){require(g.conformer.size==g.atoms.size && g.conformer.isNotEmpty()){ "A source 3D conformer is required." };g.atoms.forEachIndexed {i,a->val point=g.conformer.first {it.id==a.id};mol.getAtom(i).point2d=null;mol.getAtom(i).point3d=javax.vecmath.Point3d(point.x,point.y,point.z)}}
        val writer=StringWriter();MDLV2000Writer(writer).use {it.write(mol)};return writer.toString()
    }
    fun writeSmiles(g:ChemGraph)=SmilesGenerator.isomeric().create(toCdk(g))
    fun formula(g:ChemGraph):String = if(g.atoms.isEmpty())"—" else MolecularFormulaManipulator.getString(MolecularFormulaManipulator.getMolecularFormula(toCdk(g)))
    fun mass(g:ChemGraph):Double=AtomContainerManipulator.getMass(toCdk(g),AtomContainerManipulator.MolWeight)
    fun clean(g:ChemGraph):ChemGraph {val mol=toCdk(g);StructureDiagramGenerator().generateCoordinates(mol);return fromCdk(mol,g.name).copy(source=g.source,cid=g.cid,properties=g.properties,conformer=g.conformer.map {a->a.copy(id=g.atoms.indexOfFirst {it.id==a.id}+1)})}
    fun hydrogens(g:ChemGraph,add:Boolean):ChemGraph {
        var mol=toCdk(g)
        if(add)AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol) else mol=AtomContainerManipulator.suppressHydrogens(mol)
        StructureDiagramGenerator().generateCoordinates(mol)
        return fromCdk(mol,g.name).copy(source="Edited hydrogen representation")
    }
}
internal object ChemProject {
    fun encode(g:ChemGraph):String=JSONObject().apply {
        put("version",1);put("name",g.name);put("source",g.source);put("cid",g.cid);put("sourceMol",g.sourceMol)
        put("annotations",JSONArray(g.annotations.map {JSONObject().put("x",it.x).put("y",it.y).put("text",it.text).put("kind",it.kind)}))
        put("properties",JSONObject(g.properties));put("atoms",atoms(g.atoms));put("conformer",atoms(g.conformer))
        put("bonds",JSONArray(g.bonds.map { JSONObject().put("a",it.a).put("b",it.b).put("order",it.order).put("stereo",it.stereo).put("aromatic",it.aromatic) }))
    }.toString()
    private fun atoms(list:List<ChemAtom>)=JSONArray(list.map { a->JSONObject().put("id",a.id).put("symbol",a.symbol).put("x",a.x).put("y",a.y).put("z",a.z).put("charge",a.charge).put("isotope",a.isotope).put("hydrogens",a.hydrogens).put("label",a.label).put("mapping",a.mapping) })
    fun decode(text:String):ChemGraph {
        require(text.length<=2_000_000){"Project exceeds 2 MB."};val j=JSONObject(text);require(j.getInt("version")==1)
        fun read(key:String):List<ChemAtom> {val a=j.optJSONArray(key)?:JSONArray();require(a.length()<=500);return (0 until a.length()).map { i->val o=a.getJSONObject(i);ChemAtom(o.getInt("id"),o.getString("symbol"),o.getDouble("x"),o.getDouble("y"),o.optDouble("z",0.0),o.optInt("charge"),if(o.has("isotope"))o.getInt("isotope") else null,if(o.has("hydrogens"))o.getInt("hydrogens") else null,o.optString("label"),o.optInt("mapping")) } }
        val b=j.getJSONArray("bonds");require(b.length()<=1000)
        val props=j.optJSONObject("properties")?:JSONObject()
        val g=ChemGraph(j.getString("name"),read("atoms"),(0 until b.length()).map { val o=b.getJSONObject(it);ChemBond(o.getInt("a"),o.getInt("b"),o.getInt("order"),o.optString("stereo"),o.optBoolean("aromatic")) },j.optString("source"),if(j.has("cid"))j.getInt("cid")else null,props.keys().asSequence().associateWith { props.getString(it) },read("conformer"),j.optString("sourceMol"))
        require(g.validate().none { it in listOf("Invalid coordinates.","Duplicate atom identifiers.","Invalid bond connectivity.","Duplicate bonds.") }){"Invalid project graph."}
        val notes=j.optJSONArray("annotations")?:JSONArray();require(notes.length()<=100)
        val annotations=(0 until notes.length()).map {val n=notes.getJSONObject(it);ChemAnnotation(n.getDouble("x"),n.getDouble("y"),n.getString("text").take(200),n.optString("kind","text"))}
        require(annotations.all {it.x.isFinite()&&it.y.isFinite()})
        require(g.conformer.isEmpty() || (g.conformer.size==g.atoms.size && g.conformer.all {a->a.x.isFinite()&&a.y.isFinite()&&a.z.isFinite()&&g.atoms.any {it.id==a.id&&it.symbol==a.symbol}})){"Conformer does not match graph."}
        return g.copy(annotations=annotations)
    }
}
internal fun distance(a:ChemAtom,b:ChemAtom)=sqrt((a.x-b.x).pow(2)+(a.y-b.y).pow(2)+(a.z-b.z).pow(2))
internal fun angle(a:ChemAtom,b:ChemAtom,c:ChemAtom):Double {
    val ab=distance(a,b);val bc=distance(b,c);require(ab>1e-8 && bc>1e-8)
    val dot=(a.x-b.x)*(c.x-b.x)+(a.y-b.y)*(c.y-b.y)+(a.z-b.z)*(c.z-b.z)
    return Math.toDegrees(acos((dot/ab/bc).coerceIn(-1.0,1.0)))
}
internal fun torsion(a:ChemAtom,b:ChemAtom,c:ChemAtom,d:ChemAtom):Double {
    fun sub(p:ChemAtom,q:ChemAtom)=doubleArrayOf(p.x-q.x,p.y-q.y,p.z-q.z)
    fun dot(p:DoubleArray,q:DoubleArray)=p.indices.sumOf { p[it]*q[it] }
    fun cross(p:DoubleArray,q:DoubleArray)=doubleArrayOf(p[1]*q[2]-p[2]*q[1],p[2]*q[0]-p[0]*q[2],p[0]*q[1]-p[1]*q[0])
    val v=sub(c,b);val n=sqrt(dot(v,v));require(n>1e-8);val u=v.map { it/n }.toDoubleArray()
    val n1=cross(sub(b,a),v);val n2=cross(v,sub(d,c));require(dot(n1,n1)>1e-12 && dot(n2,n2)>1e-12)
    return Math.toDegrees(atan2(dot(cross(n1,n2),u),dot(n1,n2)))
}
