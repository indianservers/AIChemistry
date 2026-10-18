package com.indianservers.aichemistryexplorer.drawchemistry

import android.graphics.Bitmap
import android.graphics.Canvas as NativeCanvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.*

internal val StudioBlue=Color(0xFF0079F8)
internal val StudioInk=Color(0xFF182331)
internal val StudioMuted=Color(0xFF718092)
internal val StudioBackground=Color(0xFFF6F9FC)
internal val StudioLine=Color(0xFFEAF0F6)

/** Small, consistent vector icons; no font glyph substitutions or bitmap tool icons. */
@Composable internal fun StudioIcon(name:String,modifier:Modifier=Modifier,color:Color=StudioInk){
    Canvas(modifier.size(23.dp)){
        val s=size.minDimension/24f;val w=1.65f*s
        fun p(x:Float,y:Float)=Offset(x*s,y*s)
        fun line(x:Float,y:Float,a:Float,b:Float)=drawLine(color,p(x,y),p(a,b),w,StrokeCap.Round)
        fun circle(x:Float,y:Float,r:Float,fill:Boolean=false)=drawCircle(color,r*s,p(x,y),style=if(fill)androidx.compose.ui.graphics.drawscope.Fill else Stroke(w))
        fun polygon(n:Int,r:Float=8f,rotation:Float= -90f){val path=Path();repeat(n){i->val t=Math.toRadians((rotation+360f*i/n).toDouble());val x=12+r*cos(t).toFloat();val y=12+r*sin(t).toFloat();if(i==0)path.moveTo(x*s,y*s)else path.lineTo(x*s,y*s)};path.close();drawPath(path,color,style=Stroke(w))}
        when(name){
            "favorite"->{val path=Path();path.moveTo(12*s,21*s);path.cubicTo(0f,13*s,1*s,3*s,7*s,3*s);path.cubicTo(10*s,3*s,12*s,6*s,12*s,6*s);path.cubicTo(15*s,0f,23*s,3*s,22*s,9*s);path.cubicTo(22*s,14*s,16*s,18*s,12*s,21*s);drawPath(path,color,style=Stroke(w))}
            "back"->{line(17f,12f,5f,12f);line(5f,12f,10f,7f);line(5f,12f,10f,17f)}
            "search"->{circle(10f,10f,6f);line(15f,15f,21f,21f)}
            "more"->listOf(5f,12f,19f).forEach {circle(it,12f,1.4f,true)}
            "select"->{val path=Path();path.moveTo(6*s,3*s);path.lineTo(18*s,13*s);path.lineTo(12*s,14*s);path.lineTo(10*s,21*s);path.close();drawPath(path,color,style=Stroke(w))}
            "atom","structures"->{line(12f,5f,5f,18f);line(12f,5f,19f,18f);line(5f,18f,19f,18f);circle(12f,4f,2f);circle(4f,19f,2f);circle(20f,19f,2f)}
            "bond"->{line(7f,17f,17f,7f);line(10f,18f,20f,8f);circle(5f,19f,2f);circle(19f,5f,2f)}
            "ring","hexagon"->{polygon(6);line(7f,8f,7f,15f);line(11f,19f,17f,15f)}
            "pentagon"->polygon(5)
            "square"->polygon(4,9f,-45f)
            "triangle"->polygon(3)
            "lasso"->{drawArc(color,30f,295f,false,topLeft=p(4f,3f),size=androidx.compose.ui.geometry.Size(16*s,17*s),style=Stroke(w,pathEffect=androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(3*s,2*s))));line(18f,19f,22f,22f)}
            "erase"->{val path=Path();path.moveTo(3*s,15*s);path.lineTo(13*s,4*s);path.lineTo(21*s,12*s);path.lineTo(12*s,21*s);path.lineTo(8*s,21*s);path.close();drawPath(path,color,style=Stroke(w));line(8f,10f,16f,18f);line(8f,21f,22f,21f)}
            "undo","redo"->{val flip=if(name=="redo")-1f else 1f;fun px(v:Float)=if(flip==1f)v else 24-v
                line(px(9f),5f,px(4f),10f);line(px(4f),10f,px(10f),10f);drawArc(color,if(flip==1f)-90f else -90f,if(flip==1f)240f else -240f,false,p(6f,7f),androidx.compose.ui.geometry.Size(14*s,13*s),style=Stroke(w))}
            "delete"->{line(4f,6f,20f,6f);line(9f,3f,15f,3f);line(6f,6f,7f,21f);line(7f,21f,17f,21f);line(17f,21f,18f,6f);line(10f,10f,10f,17f);line(14f,10f,14f,17f)}
            "copy"->{drawRoundRect(color,p(8f,4f),androidx.compose.ui.geometry.Size(12*s,16*s),androidx.compose.ui.geometry.CornerRadius(2*s),style=Stroke(w));line(5f,8f,3f,8f);line(3f,8f,3f,22f);line(3f,22f,14f,22f)}
            "fit"->{line(3f,9f,3f,3f);line(3f,3f,9f,3f);line(15f,3f,21f,3f);line(21f,3f,21f,9f);line(21f,15f,21f,21f);line(21f,21f,15f,21f);line(9f,21f,3f,21f);line(3f,21f,3f,15f)}
            "rotate","reset"->{drawArc(color,-55f,295f,false,p(4f,4f),androidx.compose.ui.geometry.Size(16*s,16*s),style=Stroke(w));line(18f,3f,20f,8f);line(20f,8f,15f,7f)}
            "edit"->{line(5f,19f,17f,4f);line(8f,21f,21f,7f);line(17f,4f,21f,7f);line(5f,19f,4f,22f);line(4f,22f,8f,21f)}
            "properties"->{drawRoundRect(color,p(5f,5f),androidx.compose.ui.geometry.Size(14*s,17*s),androidx.compose.ui.geometry.CornerRadius(2*s),style=Stroke(w));line(9f,5f,9f,2f);line(9f,2f,16f,2f);line(9f,17f,9f,13f);line(12f,17f,12f,10f);line(15f,17f,15f,7f)}
            "3d","space"->{listOf(Offset(8f,7f),Offset(15f,7f),Offset(6f,14f),Offset(13f,14f),Offset(18f,16f)).forEach {circle(it.x,it.y,if(name=="space")4f else 2.6f,name=="space")};line(8f,7f,13f,14f);line(15f,7f,6f,14f);line(13f,14f,18f,16f)}
            "2d"->{drawOval(color,p(3f,8f),androidx.compose.ui.geometry.Size(18*s,8*s),style=Stroke(w));drawOval(color,p(8f,3f),androidx.compose.ui.geometry.Size(8*s,18*s),style=Stroke(w));circle(12f,12f,2.3f,true)}
            "settings"->{circle(12f,12f,4f);polygon(8,10f)}
            "zoom"->{circle(10f,10f,6f);line(15f,15f,21f,21f);line(7f,10f,13f,10f);line(10f,7f,10f,13f)}
            "pan"->{line(3f,12f,21f,12f);line(12f,3f,12f,21f);line(3f,12f,6f,9f);line(21f,12f,18f,15f);line(12f,3f,9f,6f);line(12f,21f,15f,18f)}
            "download"->{line(12f,2f,12f,16f);line(12f,16f,7f,11f);line(12f,16f,17f,11f);line(4f,17f,4f,22f);line(4f,22f,20f,22f);line(20f,22f,20f,17f)}
            "external"->{line(14f,3f,21f,3f);line(21f,3f,21f,10f);line(11f,13f,21f,3f);line(10f,5f,4f,5f);line(4f,5f,4f,21f);line(4f,21f,19f,21f);line(19f,21f,19f,14f)}
            "share"->{circle(5f,12f,3f);circle(19f,5f,3f);circle(19f,20f,3f);line(8f,11f,16f,6f);line(8f,14f,16f,19f)}
            "light"->{circle(12f,8f,6f);line(9f,14f,10f,20f);line(10f,20f,14f,20f);line(14f,20f,15f,14f)}
            "clear"->{line(12f,2f,14f,9f);line(14f,9f,21f,12f);line(21f,12f,14f,15f);line(14f,15f,12f,22f);line(12f,22f,9f,15f);line(9f,15f,2f,12f);line(2f,12f,9f,9f);line(9f,9f,12f,2f)}
            else->{polygon(6);circle(12f,12f,2f)}
        }
    }
}
@Composable internal fun StudioPanel(modifier:Modifier=Modifier,content:@Composable ()->Unit){
    Surface(modifier.shadow(9.dp,RoundedCornerShape(17.dp),ambientColor=Color(0xFF849DB5).copy(.14f),spotColor=Color(0xFF849DB5).copy(.12f)),shape=RoundedCornerShape(17.dp),color=Color.White.copy(.97f),border=BorderStroke(1.dp,Color.White)){content()}
}
@Composable internal fun StudioAction(label:String,icon:String,onClick:()->Unit,modifier:Modifier=Modifier,selected:Boolean=false,enabled:Boolean=true,compact:Boolean=false){
    Column(modifier.defaultMinSize(minWidth=44.dp,minHeight=44.dp).clip(RoundedCornerShape(12.dp)).background(if(selected)StudioBlue else Color.Transparent).semantics {this.selected=selected;contentDescription=label}.clickable(enabled=enabled,onClick=onClick).padding(horizontal=5.dp,vertical=if(compact)5.dp else 9.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){
        StudioIcon(icon,Modifier.size(if(compact)19.dp else 23.dp),if(!enabled)Color(0xFFBCC7D3)else if(selected)Color.White else StudioInk)
        if(label.isNotBlank())Text(label,color=if(!enabled)Color(0xFFADB9C7)else if(selected)Color.White else StudioInk,fontSize=if(compact)9.sp else 10.sp,maxLines=1,overflow=TextOverflow.Ellipsis,modifier=Modifier.padding(top=5.dp))
    }
}
@Composable internal fun StudioSquare(label:String,icon:String,onClick:()->Unit,enabled:Boolean=true){
    Surface(Modifier.size(40.dp).semantics {contentDescription=label}.clip(RoundedCornerShape(12.dp)).clickable(enabled=enabled,onClick=onClick),shape=RoundedCornerShape(12.dp),color=Color.White,shadowElevation=4.dp){Box(contentAlignment=Alignment.Center){StudioIcon(icon,Modifier.size(21.dp),if(enabled)StudioInk else Color(0xFFBBC6D1))}}
}
@Composable internal fun StudioTabs(selected:Int,compact:Boolean=false,onSelect:(Int)->Unit){
    Row(Modifier.fillMaxWidth().padding(horizontal=10.dp,vertical=3.dp)){
        listOf("Draw 2D" to "2d","Build 3D" to "3d","Structures" to "structures","Properties" to "properties").forEachIndexed {i,(label,icon)->
            Column(Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(if(selected==i)Color(0xFFEFF6FF)else Color.Transparent).semantics {this.selected=selected==i}.clickable {onSelect(i)}.padding(top=if(compact)3.dp else 9.dp,bottom=3.dp),horizontalAlignment=Alignment.CenterHorizontally){
                if(compact)Row(verticalAlignment=Alignment.CenterVertically){StudioIcon(icon,Modifier.size(18.dp),if(selected==i)StudioBlue else StudioInk);Text(label,fontSize=10.sp,color=if(selected==i)StudioBlue else StudioInk,modifier=Modifier.padding(start=6.dp,top=5.dp,bottom=7.dp))}
                else{StudioIcon(icon,Modifier.size(23.dp),if(selected==i)StudioBlue else StudioInk)
                Text(label,fontSize=10.sp,color=if(selected==i)StudioBlue else StudioInk,fontWeight=if(selected==i)FontWeight.Bold else FontWeight.Medium,modifier=Modifier.padding(top=6.dp,bottom=8.dp))}
                Box(Modifier.width(43.dp).height(2.dp).background(if(selected==i)StudioBlue else Color.Transparent))
            }
        }
    }
}
@Composable internal fun MoleculePreview(graph:ChemGraph?,modifier:Modifier=Modifier){
    val bitmap by produceState<Bitmap?>(null,graph){value=graph?.let {g->withContext(Dispatchers.Default){MoleculeThumbnails.render(g)}}}
    Box(modifier,contentAlignment=Alignment.Center){bitmap?.let { Image(it.asImageBitmap(),contentDescription=null,modifier=Modifier.fillMaxSize()) }?:StudioIcon("structures",Modifier.size(36.dp),Color(0xFFD3E1EC))}
}
/** CPU thumbnail cache uses source conformer coordinates, never a fabricated chemical model. */
internal object MoleculeThumbnails {
    private val cache=android.util.LruCache<String,Bitmap>(40)
    fun render(g:ChemGraph):Bitmap {
        val key="${g.cid}:${g.hashCode()}";synchronized(cache){cache.get(key)?.let {return it}}
        val bitmap=Bitmap.createBitmap(320,260,Bitmap.Config.ARGB_8888);val c=NativeCanvas(bitmap);val paint=Paint(Paint.ANTI_ALIAS_FLAG)
        val atoms=if(g.conformer.isNotEmpty())g.conformer else g.atoms
        if(atoms.isEmpty())return bitmap
        val cx=atoms.map {it.x}.average();val cy=atoms.map {it.y}.average();val cz=atoms.map {it.z}.average()
        data class Point(val a:ChemAtom,val x:Float,val y:Float,val z:Float)
        // Choose an orthographic camera exposing the molecule's principal spread.
        // Only the view changes; source coordinates, connectivity and measurements are untouched.
        val angles=listOf(-90,-60,-30,0,30,60,90).map {Math.toRadians(it.toDouble())}
        val points=angles.flatMap {ax->angles.map {ay->
            atoms.map {a->val x=a.x-cx;val y=a.y-cy;val z=a.z-cz;val yy=y*cos(ax)-z*sin(ax);val zz=y*sin(ax)+z*cos(ax);Point(a,(x*cos(ay)+zz*sin(ay)).toFloat(),yy.toFloat(),(zz*cos(ay)-x*sin(ay)).toFloat())}
        }}.maxBy {view->view.sumOf {p->(p.x*p.x+p.y*p.y).toDouble()}}
        val range=max(points.maxOf {abs(it.x)},points.maxOf {abs(it.y)}).coerceAtLeast(1.4f)
        val scale=100/range;val byId=points.associateBy {it.a.id}
        fun sx(p:Point)=160+p.x*scale
        fun sy(p:Point)=130-p.y*scale
        g.bonds.forEach {b->val a=byId[b.a];val d=byId[b.b];if(a!=null&&d!=null){paint.color=android.graphics.Color.rgb(74,79,88);paint.strokeWidth=(scale*.12f).coerceIn(2f,7f);paint.strokeCap=Paint.Cap.ROUND;val dx=sx(d)-sx(a);val dy=sy(d)-sy(a);val length=hypot(dx,dy).coerceAtLeast(1f);repeat(b.order.coerceIn(1,3)){i->val o=(i-(b.order-1)/2f)*4;c.drawLine(sx(a)-dy/length*o,sy(a)+dx/length*o,sx(d)-dy/length*o,sy(d)+dx/length*o,paint)}}}
        points.sortedBy {it.z}.forEach {p->val radius=(if(p.a.symbol=="H").20f else .34f)*scale;val color=if(p.a.symbol=="H")android.graphics.Color.rgb(225,230,237)else elementColor(p.a.symbol);val bright=android.graphics.Color.rgb((android.graphics.Color.red(color)*.5+127).toInt(),(android.graphics.Color.green(color)*.5+127).toInt(),(android.graphics.Color.blue(color)*.5+127).toInt());paint.shader=RadialGradient(sx(p)-radius*.35f,sy(p)-radius*.4f,radius*1.4f,intArrayOf(bright,color,android.graphics.Color.rgb((android.graphics.Color.red(color)*.55).toInt(),(android.graphics.Color.green(color)*.55).toInt(),(android.graphics.Color.blue(color)*.55).toInt())),floatArrayOf(0f,.48f,1f),Shader.TileMode.CLAMP);c.drawCircle(sx(p),sy(p),radius,paint);paint.shader=null}
        synchronized(cache){cache.put(key,bitmap)};return bitmap
    }
}
@Composable internal fun StudioPropertyRows(rows:List<Pair<String,String>>,copy:(String)->Unit){
    StudioPanel(Modifier.fillMaxWidth()){
        Column(Modifier.padding(horizontal=13.dp)){
            rows.forEachIndexed {i,(label,value)->
                Row(Modifier.fillMaxWidth().heightIn(min=49.dp).padding(vertical=9.dp),verticalAlignment=Alignment.CenterVertically){
                    Text(label,fontSize=11.sp,color=Color(0xFF536170),modifier=Modifier.weight(.95f))
                    Text(value,fontSize=11.sp,lineHeight=15.sp,color=StudioInk,maxLines=if(label in setOf("Validation","Scope","Source","Spectral data","Safety data","Supported-rule checks"))3 else 1,overflow=TextOverflow.Ellipsis,modifier=Modifier.weight(1.2f))
                    if(label in setOf("SMILES","InChI","InChIKey"))Box(Modifier.size(32.dp).semantics {contentDescription="Copy $label"}.clickable {copy(value)},contentAlignment=Alignment.Center){StudioIcon("copy",Modifier.size(17.dp),StudioBlue)}
                }
                if(i<rows.lastIndex)HorizontalDivider(color=StudioLine)
            }
        }
    }
}
