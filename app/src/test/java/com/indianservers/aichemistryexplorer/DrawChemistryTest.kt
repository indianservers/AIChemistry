package com.indianservers.aichemistryexplorer

import com.indianservers.aichemistryexplorer.drawchemistry.*
import org.junit.Assert.*
import org.junit.Test

class DrawChemistryTest {
    @Test fun referenceFormulasAndMasses(){
        listOf("O" to "H2O","O=C=O" to "CO2","C" to "CH4","N" to "H3N","CCO" to "C2H6O","CC(=O)O" to "C2H4O2","CC(=O)C" to "C3H6O","c1ccccc1" to "C6H6","n1ccccc1" to "C5H5N","[Na+].[Cl-]" to "ClNa").forEach { (smiles,expected)->assertEquals(smiles,expected,ChemToolkit.formula(ChemToolkit.smiles(smiles))) }
        assertEquals(18.015,ChemToolkit.mass(ChemToolkit.smiles("O")),.01)
        assertEquals(46.069,ChemToolkit.mass(ChemToolkit.smiles("CCO")),.01)
    }
    @Test fun isotopeChargeAndStereochemistrySurviveSupportedRoundTrips(){
        listOf("[13CH3]O","N[C@@H](C)C(=O)O","F/C=C/F","[NH4+]","[Na+].[Cl-]","c1ccccc1").forEach { input->
            val g=ChemToolkit.smiles(input);val canonical=ChemToolkit.writeSmiles(g)
            val project=ChemProject.decode(ChemProject.encode(g));assertEquals(canonical,ChemToolkit.writeSmiles(project))
            val mol=ChemToolkit.mol(ChemToolkit.writeMol(g));assertEquals(ChemToolkit.formula(g),ChemToolkit.formula(mol))
            assertEquals(canonical,ChemToolkit.writeSmiles(mol))
        }
    }
    @Test fun ringTemplatesHaveClosedConnectivity(){ringTemplates.forEach { (name,smiles)->val g=ChemToolkit.smiles(smiles,name);assertTrue(name,g.bonds.size>=g.atoms.size);assertTrue(name,g.atoms.all { a->g.bonds.count { it.a==a.id||it.b==a.id }>=2 });assertTrue(ChemToolkit.formula(g).isNotBlank())}}
    @Test fun editsInvalidatePropertiesAndConformers(){val g=ChemToolkit.smiles("CCO").copy(cid=702,conformer=listOf(ChemAtom(1,"C",0.0,0.0,1.0)),properties=mapOf("mass" to "46"));val edited=g.edited(atoms=g.atoms.map { if(it.id==1)it.copy(symbol="N",hydrogens=null)else it });assertNull(edited.cid);assertTrue(edited.conformer.isEmpty());assertTrue(edited.properties.isEmpty());assertEquals("CH5NO",ChemToolkit.formula(edited))}
    @Test fun replacementBondOrdersAndDelete(){var g=ChemGraph().addAtom("C",0.0,0.0).addAtom("O",1.4,0.0);g=g.bond(1,2,1).bond(1,2,2);assertEquals(1,g.bonds.size);assertEquals(2,g.bonds.single().order);assertEquals("CH2O",ChemToolkit.formula(g));assertTrue(g.delete(setOf(2)).bonds.isEmpty())}
    @Test fun impossibleValenceIsReportedWithoutDestroyingDrawing(){var g=ChemGraph().addAtom("O",0.0,0.0);repeat(3){g=g.addAtom("H",it.toDouble(),1.0).bond(1,it+2,1)};assertTrue(g.validate().any { it.contains("exceeds") });assertEquals(4,g.atoms.size)}
    @Test fun historyRestoresGraphAndTruncatesRedo(){val h=ChemHistory(ChemGraph());h.change(h.current.addAtom("C",0.0,0.0));val first=h.current;h.change(h.current.addAtom("O",1.0,0.0));assertEquals(first,h.undo());assertEquals(2,h.redo().atoms.size);h.undo();h.change(h.current.addAtom("N",2.0,0.0));assertFalse(h.canRedo)}
    @Test fun projectRoundTripAndMalformedSafety(){val g=ChemToolkit.smiles("CC(=O)O");assertEquals(g,ChemProject.decode(ChemProject.encode(g)));listOf("{}","not json","{\"version\":100}").forEach { assertTrue(runCatching { ChemProject.decode(it) }.isFailure) };assertTrue(runCatching {ChemToolkit.smiles("C1CC")}.isFailure);assertTrue(runCatching {ChemToolkit.mol("V3000")}.isFailure)}
    @Test fun measurementsUseAngstromCoordinates(){val o=ChemAtom(1,"O",0.0,0.0,0.0);val h1=ChemAtom(2,"H",.9572,0.0);val theta=Math.toRadians(104.5);val h2=ChemAtom(3,"H",.9572*kotlin.math.cos(theta),.9572*kotlin.math.sin(theta));assertEquals(.9572,distance(o,h1),1e-6);assertEquals(104.5,angle(h1,o,h2),1e-6);assertTrue(runCatching {angle(o,o,h1)}.isFailure)}
    @Test fun fragmentsDuplicateWithUniqueIdentifiers(){val g=ChemToolkit.smiles("CCO");val copy=mergeFragment(g,g);assertEquals(g.atoms.size*2,copy.atoms.size);assertEquals(copy.atoms.size,copy.atoms.map { it.id }.distinct().size);assertTrue(copy.validate().none { it.contains("Duplicate") })}
    @Test fun viewportTransformsRoundTripAtMultipleZoomsAndDensities(){
        for(density in listOf(1f,2f,3.5f))for(zoom in listOf(.15f,1f,6f)){
            val view=ChemViewport(1080f,1700f,50*density*zoom,2.1f,-1.2f,130f,-240f)
            listOf(0f to 0f,-3f to 8f,4.75f to -12f).forEach {(x,y)->val screen=view.screen(x,y);val back=view.world(screen.first,screen.second);assertEquals(x,back.first,.0001f);assertEquals(y,back.second,.0001f)}
        }
    }
    @Test fun reactionTemplatesAreAtomAndChargeBalanced(){
        fun inventory(smiles:List<String>):Map<String,Int>{val result=mutableMapOf<String,Int>();smiles.forEach {s->
            Regex("([A-Z][a-z]?)([0-9]*)").findAll(ChemToolkit.formula(ChemToolkit.smiles(s))).forEach {m->val symbol=m.groupValues[1];val n=m.groupValues[2].toIntOrNull()?:1;result[symbol]=(result[symbol]?:0)+n}
        };return result}
        reactionTemplates.forEach {t->assertEquals(t.name,inventory(t.reactants),inventory(t.products));val g=reactionGraph(t);assertTrue(g.annotations.any {it.kind=="arrow"});assertEquals(g,ChemProject.decode(ChemProject.encode(g)));assertTrue(g.validate().none {it.contains("Duplicate")})}
    }
    @Test fun malformedConformersCannotBeLoadedAsValidatedData(){
        val g=ChemToolkit.smiles("CCO").copy(conformer=listOf(ChemAtom(500,"Fe",1.0,2.0,3.0)))
        assertTrue(runCatching {ChemProject.decode(ChemProject.encode(g))}.isFailure)
    }
    @Test fun functionalGroupPatternsDistinguishAlcoholEsterAndAmide(){
        assertTrue(ChemToolkit.functionalGroups(ChemToolkit.smiles("CCO")).containsKey("Alcohol"))
        assertTrue(ChemToolkit.functionalGroups(ChemToolkit.smiles("CC(=O)OCC")).containsKey("Ester"))
        assertFalse(ChemToolkit.functionalGroups(ChemToolkit.smiles("CC(=O)N")).containsKey("Amine"))
        assertTrue(ChemToolkit.functionalGroups(ChemToolkit.smiles("CC(=O)N")).containsKey("Amide"))
    }
    @Test fun cleanRemapsConformersWhenProjectAtomIdentifiersAreNotSequential(){
        val source=ChemToolkit.smiles("CCO")
        val g=source.copy(atoms=source.atoms.map {it.copy(id=it.id+10)},bonds=source.bonds.map {it.copy(a=it.a+10,b=it.b+10)},sourceMol="",conformer=source.atoms.map {it.copy(id=it.id+10,z=.5)})
        val clean=ChemToolkit.clean(g)
        assertEquals(clean.atoms.map {it.id}.toSet(),clean.conformer.map {it.id}.toSet())
        assertEquals(clean,ChemProject.decode(ChemProject.encode(clean)))
    }
    @Test fun wedgesRetainTetrahedralStereoAfterReferenceMetadataIsRemoved(){
        val alanine=ChemToolkit.smiles("N[C@@H](C)C(=O)O")
        val before=ChemToolkit.writeSmiles(alanine)
        assertTrue(before.contains("@"))
        assertEquals(before,ChemToolkit.writeSmiles(alanine.edited()))
    }
}
