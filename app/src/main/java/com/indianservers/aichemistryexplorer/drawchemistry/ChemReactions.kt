package com.indianservers.aichemistryexplorer.drawchemistry

/** Editable balanced educational schemes. No reaction dynamics or mechanism is simulated. */
internal data class ReactionTemplate(val name:String,val reactants:List<String>,val products:List<String>,val conditions:String,val reversible:Boolean=false)
internal val reactionTemplates=listOf(
    ReactionTemplate("Addition",listOf("C=C","BrBr"),listOf("BrCCBr"),"Br₂ addition; inert solvent"),
    ReactionTemplate("Substitution",listOf("CBr","[OH-]"),listOf("CO","[Br-]"),"Aqueous hydroxide; SN2 example"),
    ReactionTemplate("Elimination",listOf("CCO"),listOf("C=C","O"),"Concentrated H₂SO₄, heat"),
    ReactionTemplate("Hydrogenation",listOf("C=C","[H][H]"),listOf("CC"),"Ni catalyst; hydrogenation"),
    ReactionTemplate("Esterification",listOf("CC(=O)O","CCO"),listOf("CC(=O)OCC","O"),"Acid catalyst, heat",true),
    ReactionTemplate("Hydrolysis",listOf("CC(=O)OCC","O"),listOf("CC(=O)O","CCO"),"Acid catalyst, heat",true),
    ReactionTemplate("Neutralization",listOf("[H+]","[OH-]"),listOf("O"),"Net ionic equation")
)
internal fun reactionGraph(template:ReactionTemplate):ChemGraph {
    var graph=ChemGraph(name=template.name,source="Educational balanced scheme; editable molecular graphs")
    val notes=mutableListOf<ChemAnnotation>();var cursor=0.0
    fun side(smiles:List<String>){smiles.forEachIndexed { index,s->
        val molecule=ChemToolkit.smiles(s);val min=molecule.atoms.minOf {it.x};val max=molecule.atoms.maxOf {it.x};val mid=molecule.atoms.map {it.y}.average()
        val offset=graph.atoms.maxOfOrNull {it.id}?:0
        graph=graph.copy(atoms=graph.atoms+molecule.atoms.map {it.copy(id=it.id+offset,x=it.x-min+cursor,y=it.y-mid)},bonds=graph.bonds+molecule.bonds.map {it.copy(a=it.a+offset,b=it.b+offset)})
        cursor+=(max-min).coerceAtLeast(1.0)+1.5
        if(index<smiles.lastIndex){notes+=ChemAnnotation(cursor-.4,0.0,"+");cursor+=1.0}
    }}
    side(template.reactants);notes+=ChemAnnotation(cursor+.2,0.0,if(template.reversible)"⇌" else "→","arrow");notes+=ChemAnnotation(cursor+.2,1.7,template.conditions);cursor+=3.0;side(template.products)
    return graph.copy(annotations=notes)
}
internal data class ChemViewport(val width:Float,val height:Float,val unit:Float,val originX:Float=0f,val originY:Float=0f,val panX:Float=0f,val panY:Float=0f){
    fun screen(x:Float,y:Float)=Pair(width/2+panX+(x-originX)*unit,height/2+panY-(y-originY)*unit)
    fun world(x:Float,y:Float)=Pair(originX+(x-width/2-panX)/unit,originY-(y-height/2-panY)/unit)
}
