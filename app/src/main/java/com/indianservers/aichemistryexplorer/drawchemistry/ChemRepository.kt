package com.indianservers.aichemistryexplorer.drawchemistry

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.LocalDate

@Entity(tableName="structures")
internal data class SavedStructure(@PrimaryKey val id:String, val name:String, val project:String, val favorite:Boolean=false, val updated:Long=System.currentTimeMillis())
@Dao internal interface StructureDao {
    @Query("SELECT * FROM structures ORDER BY updated DESC") fun observe():Flow<List<SavedStructure>>
    @Query("SELECT * FROM structures WHERE id=:id") suspend fun get(id:String):SavedStructure?
    @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun save(item:SavedStructure)
    @Query("DELETE FROM structures WHERE id=:id") suspend fun delete(id:String)
}
@Database(entities=[SavedStructure::class],version=1,exportSchema=true)
internal abstract class DrawDatabase:RoomDatabase(){abstract fun structures():StructureDao
    companion object {
        @Volatile private var instance:DrawDatabase?=null
        fun get(context:Context):DrawDatabase=instance?:synchronized(this){instance?:Room.databaseBuilder(context.applicationContext,DrawDatabase::class.java,"draw-chemistry.db").build().also { instance=it }}
    }
}
internal data class LibraryEntry(val cid:Int,val name:String,val category:String)
internal class ChemRepository(private val context:Context,private val symbols:List<String>){
    val dao=DrawDatabase.get(context).structures()
    fun library():List<LibraryEntry> {
        val a=JSONArray(context.assets.open("drawchemistry/library.json").bufferedReader().use { it.readText() })
        return (0 until a.length()).map { val o=a.getJSONObject(it);val cid=o.getInt("cid");LibraryEntry(cid,o.getString("name"),when(cid){962,280,297,222,1118,944,5234->"Basic chemistry";241,1140,8078->"Hydrocarbons";5793,5984,750,5997,5957->"Biomolecules";2519,2244,3672,1983->"Pharmaceuticals";else->"Functional groups"}) }
    }
    suspend fun bundled(entry:LibraryEntry)=withContext(Dispatchers.IO){parseRecord(JSONObject(context.assets.open("drawchemistry/${entry.cid}.json").bufferedReader().use { it.readText() }))}
    fun parseRecord(record:JSONObject):ChemGraph {
        val two=record.getJSONObject("two");val atomObj=two.getJSONObject("atoms");val ids=atomObj.getJSONArray("aid");val elements=atomObj.getJSONArray("element")
        val coord=two.getJSONArray("coords").getJSONObject(0);val coordIds=coord.getJSONArray("aid");val c=coord.getJSONArray("conformers").getJSONObject(0)
        fun indexed(a:JSONArray?,id:Int):Int? {if(a==null)return null;for(i in 0 until a.length()){val v=a.getJSONObject(i);if(v.getInt("aid")==id)return v.getInt("value")};return null}
        val atoms=(0 until ids.length()).map { i->val id=ids.getInt(i);val ci=(0 until coordIds.length()).first { coordIds.getInt(it)==id };ChemAtom(id,symbols[elements.getInt(i)-1],c.getJSONArray("x").getDouble(ci),c.getJSONArray("y").getDouble(ci),charge=indexed(atomObj.optJSONArray("charge"),id)?:0,isotope=indexed(atomObj.optJSONArray("isotope"),id),hydrogens=0) }
        require(atoms.size<=500){"Maximum 500 atoms."}
        val b=two.optJSONObject("bonds");val bonds=if(b==null)emptyList()else (0 until b.getJSONArray("aid1").length()).mapNotNull { i->val order=b.getJSONArray("order").getInt(i);if(order==7)null else ChemBond(b.getJSONArray("aid1").getInt(i),b.getJSONArray("aid2").getInt(i),order.coerceIn(1,3),if(order>3)"query" else "") }
        val props=mutableMapOf<String,String>();val pa=two.optJSONArray("props")?:JSONArray()
        for(i in 0 until pa.length()){val p=pa.getJSONObject(i);val urn=p.getJSONObject("urn");val value=p.getJSONObject("value");if(value.has("binary"))continue;val key=listOf(urn.optString("label"),urn.optString("name")).filter { it.isNotEmpty() }.joinToString(" · ");props[key]=value.keys().asSequence().map { value.get(it).toString() }.joinToString()}
        props["Retrieved"]=record.optString("retrieved");props["Formal charge"]=two.optInt("charge").toString()
        val three=record.optJSONObject("three")
        val conformer=if(three==null)emptyList()else {
            val tc=three.getJSONArray("coords").getJSONObject(0);val t=tc.getJSONArray("conformers").getJSONObject(0);val aids=tc.getJSONArray("aid")
            if(!t.has("z"))emptyList()else (0 until aids.length()).map { i->val a=atoms.first { it.id==aids.getInt(i) };a.copy(x=t.getJSONArray("x").getDouble(i),y=t.getJSONArray("y").getDouble(i),z=t.getJSONArray("z").getDouble(i)) }
        }
        // MOL retains the source's tetrahedral/bond stereo for unedited round trips.
        val mol=record.optString("mol").substringBefore("$$$$")
        val graph=ChemGraph(record.getString("name"),atoms,bonds,"PubChem · computed conformer (not experimental)",record.getInt("cid"),props,conformer,if(bonds.size==two.optJSONObject("bonds")?.optJSONArray("aid1")?.length())mol else "")
        return if(mol.isNotBlank())runCatching { val parsed=ChemToolkit.mol(mol);graph.copy(bonds=graph.bonds.map { bond->bond.copy(stereo=parsed.bonds.firstOrNull { setOf(it.a,it.b)==setOf(bond.a,bond.b) }?.stereo?:bond.stereo) }) }.getOrDefault(graph)else graph
    }
    suspend fun search(query:String,type:String="name"):List<Int> {
        val path=when(type){"CID"->"cid";"SMILES"->"smiles";"Formula"->"fastformula";else->"name"}
        val encoded=URLEncoder.encode(query.trim(),"UTF-8").replace("+","%20")
        val result=JSONObject(request("compound/$path/$encoded/cids/JSON"))
        val a=result.getJSONObject("IdentifierList").getJSONArray("CID")
        return (0 until a.length().coerceAtMost(100)).map { a.getInt(it) }
    }
    suspend fun remote(cid:Int):ChemGraph {
        dao.get("cache:$cid")?.let { return ChemProject.decode(it.project) }
        val two=JSONObject(request("compound/cid/$cid/JSON?record_type=2d")).getJSONArray("PC_Compounds").getJSONObject(0)
        val three=try{JSONObject(request("compound/cid/$cid/JSON?record_type=3d")).getJSONArray("PC_Compounds").getJSONObject(0)}catch(e:Exception){null}
        val mol=request("compound/cid/$cid/SDF?record_type=2d")
        val name=two.optJSONArray("props")?.let { a->(0 until a.length()).map { a.getJSONObject(it) }.firstOrNull { it.getJSONObject("urn").optString("label")=="IUPAC Name" }?.getJSONObject("value")?.optString("sval") }?:"PubChem $cid"
        val g=withContext(Dispatchers.Default){parseRecord(JSONObject().put("name",name).put("cid",cid).put("two",two).put("three",three).put("mol",mol).put("retrieved",LocalDate.now().toString()))}
        dao.save(SavedStructure("cache:$cid",g.name,ChemProject.encode(g)))
        return g
    }
    private suspend fun request(path:String):String=withContext(Dispatchers.IO){networkLock.withLock {
        var failure:Exception?=null
        repeat(3){attempt->
            delay(350L*(attempt+1))
            val connection=URL("https://pubchem.ncbi.nlm.nih.gov/rest/pug/$path").openConnection() as HttpURLConnection
            try {
                connection.connectTimeout=15000;connection.readTimeout=30000
                connection.setRequestProperty("User-Agent","AIChemistryExplorer/1.1 DrawChemistry")
                val code=connection.responseCode
                if(code==404)throw IllegalArgumentException("No matching PubChem record or conformer.")
                if(code!=200)throw java.io.IOException("PubChem HTTP $code. Try again later.")
                val bytes=connection.inputStream.use { it.readNBytes(2_000_001) };require(bytes.size<=2_000_000){"Record exceeds 2 MB."}
                return@withLock bytes.toString(Charsets.UTF_8)
            }catch(e:IllegalArgumentException){throw e}catch(e:Exception){failure=e}finally{connection.disconnect()}
        }
        throw java.io.IOException("Online lookup unavailable. Local drawings and saved structures remain available.",failure)
    }}
    companion object {private val networkLock=Mutex()}
}
