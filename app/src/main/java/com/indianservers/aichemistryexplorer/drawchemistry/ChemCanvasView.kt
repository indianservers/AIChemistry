package com.indianservers.aichemistryexplorer.drawchemistry

import android.content.Context
import android.graphics.*
import android.view.*
import kotlin.math.*

internal fun elementColor(symbol:String):Int=when(symbol){"C"->Color.rgb(52,65,83);"O"->Color.rgb(236,65,76);"N"->Color.rgb(47,105,232);"H"->Color.rgb(155,169,188);"S"->Color.rgb(213,164,15);"P"->Color.rgb(236,126,28);"F","Cl"->Color.rgb(33,166,92);"Br"->Color.rgb(163,69,50);"I"->Color.rgb(135,76,174);else->Color.rgb(52,143,162)}
internal enum class DrawTool { Select, Atom, Bond, Erase, Ring, Lasso }
internal class ChemCanvasView(context:Context):View(context){
    private var hasUserInteraction=false
    var graph=ChemGraph();set(v){val initialLoad=field.atoms.isEmpty()&&v.atoms.isNotEmpty()&&!hasUserInteraction;field=v;if(initialLoad&&width>0&&height>0)fit()else invalidate()}
    var skeletal=false;set(value){field=value;invalidate()}
    var selected=emptySet<Int>();set(v){field=v;invalidate()}
    var tool=DrawTool.Atom;var element="C";var bondOrder=1;var bondStereo="";var ringSize=6;var aromatic=false
    var onBondInspect:(ChemBond)->Unit={}
    var onChange:(ChemGraph)->Unit={};var onSelect:(Set<Int>)->Unit={};var onInspect:(Int)->Unit={}
    private val paint=Paint(Paint.ANTI_ALIAS_FLAG)
    var zoom=1f;private var panX=0f;private var panY=0f
    private val unit get()=50f*resources.displayMetrics.density*zoom
    private var start:PointF?=null;private var last:PointF?=null;private var startId:Int?=null
    private var multi=false;private var dragging=false;private var before=graph;private var path=mutableListOf<PointF>()
    private var lastUp=0L;private var lastTapX=0f;private var lastTapY=0f;private var lastTapTool:DrawTool?=null
    private val scale=ScaleGestureDetector(context,object:ScaleGestureDetector.SimpleOnScaleGestureListener(){override fun onScale(d:ScaleGestureDetector):Boolean{zoom=(zoom*d.scaleFactor).coerceIn(.15f,6f);invalidate();return true}})
    private val gestures=GestureDetector(context,object:GestureDetector.SimpleOnGestureListener(){override fun onDown(e:MotionEvent)=true
        override fun onLongPress(e:MotionEvent){val atom=hit(e.x,e.y);if(atom!=null){onInspect(atom);dragging=true}else hitBond(e.x,e.y)?.let {onBondInspect(it);dragging=true}}
    })
    init{isFocusable=true;contentDescription="Molecule drawing canvas. Tap to add atoms; drag to connect. Two fingers pan and pinch to zoom.";setBackgroundColor(Color.WHITE)}
    override fun onSizeChanged(w:Int,h:Int,oldw:Int,oldh:Int){super.onSizeChanged(w,h,oldw,oldh);fit()}
    fun fit(){zoom=1f;panX=0f;panY=0f
        origin=PointF(if(graph.atoms.isEmpty())0f else graph.atoms.map { it.x }.average().toFloat(),if(graph.atoms.isEmpty())0f else graph.atoms.map { it.y }.average().toFloat())
        if(graph.atoms.isNotEmpty()){
            val rangeX=(graph.atoms.maxOf { it.x }-graph.atoms.minOf { it.x }).toFloat().coerceAtLeast(2f)
            val rangeY=(graph.atoms.maxOf { it.y }-graph.atoms.minOf { it.y }).toFloat().coerceAtLeast(2f)
            zoom=min((width-100f)/(rangeX*50*resources.displayMetrics.density),(height-100f)/(rangeY*50*resources.displayMetrics.density)).coerceIn(.15f,2f)
        };invalidate()
    }
    private var origin=PointF(0f,0f)
    private fun center()=origin
    private fun viewport()=ChemViewport(width.toFloat(),height.toFloat(),unit,origin.x,origin.y,panX,panY)
    private fun screen(a:ChemAtom):PointF {val p=viewport().screen(a.x.toFloat(),a.y.toFloat());return PointF(p.first,p.second)}
    private fun world(x:Float,y:Float):PointF{val p=viewport().world(x,y);return PointF(p.first,p.second)}
    private fun visible(a:ChemAtom):Boolean = !skeletal || a.symbol!="H" || a.isotope!=null || graph.bonds.none {b->(b.a==a.id||b.b==a.id)&&b.stereo.isBlank()&&graph.atoms.any {it.id==(if(b.a==a.id)b.b else b.a)&&it.symbol!="H"}}
    private fun hit(x:Float,y:Float):Int?=graph.atoms.filter(::visible).minByOrNull { val p=screen(it);hypot(p.x-x,p.y-y) }?.takeIf { val p=screen(it);hypot(p.x-x,p.y-y)<26*resources.displayMetrics.density }?.id
    private fun hitBond(x:Float,y:Float):ChemBond?=graph.bonds.minByOrNull { b->segmentDistance(x,y,screen(graph.atoms.first { it.id==b.a }),screen(graph.atoms.first { it.id==b.b })) }?.takeIf { b->segmentDistance(x,y,screen(graph.atoms.first { it.id==b.a }),screen(graph.atoms.first { it.id==b.b }))<14*resources.displayMetrics.density }
    private fun segmentDistance(x:Float,y:Float,a:PointF,b:PointF):Float {val dx=b.x-a.x;val dy=b.y-a.y;val den=dx*dx+dy*dy;val t=if(den<.001)0f else ((x-a.x)*dx+(y-a.y)*dy)/den;return hypot(x-a.x-t.coerceIn(0f,1f)*dx,y-a.y-t.coerceIn(0f,1f)*dy)}
    override fun onDraw(c:Canvas){super.onDraw(c);paint.color=Color.rgb(236,241,247);paint.strokeWidth=1f
        val spacing=24*resources.displayMetrics.density;var x=0f;while(x<width){var y=0f;while(y<height){c.drawCircle(x,y,1f,paint);y+=spacing};x+=spacing}
        graph.bonds.filter {b->graph.atoms.filter {it.id==b.a||it.id==b.b}.all(::visible)}.forEach { b->val a=screen(graph.atoms.first { it.id==b.a });val z=screen(graph.atoms.first { it.id==b.b });paint.color=Color.rgb(25,32,42);paint.strokeWidth=1.6f*resources.displayMetrics.density;paint.style=Paint.Style.STROKE
            val dx=z.x-a.x;val dy=z.y-a.y;val len=hypot(dx,dy).coerceAtLeast(1f);val nx=-dy/len*4*resources.displayMetrics.density;val ny=dx/len*4*resources.displayMetrics.density
            when(b.stereo){
                "wedge"->{paint.style=Paint.Style.FILL;val p=Path();p.moveTo(a.x,a.y);p.lineTo(z.x+nx,z.y+ny);p.lineTo(z.x-nx,z.y-ny);p.close();c.drawPath(p,paint)}
                "dash"->{for(i in 1..7){val t=i/8f;val px=a.x+dx*t;val py=a.y+dy*t;c.drawLine(px-nx*t,py-ny*t,px+nx*t,py+ny*t,paint)}}
                "dative"->{c.drawLine(a.x,a.y,z.x,z.y,paint);c.drawLine(z.x,z.y,z.x-dx/len*12+nx,z.y-dy/len*12+ny,paint);c.drawLine(z.x,z.y,z.x-dx/len*12-nx,z.y-dy/len*12-ny,paint)}
                else->{if(b.stereo=="query")paint.pathEffect=DashPathEffect(floatArrayOf(6f,6f),0f)
                    val count=if(b.aromatic)1 else b.order;for(i in 0 until count){val offset=i-(count-1)/2f;c.drawLine(a.x+nx*offset,a.y+ny*offset,z.x+nx*offset,z.y+ny*offset,paint)};paint.pathEffect=null}
            }
            if(b.aromatic){paint.pathEffect=DashPathEffect(floatArrayOf(5f,5f),0f);c.drawLine(a.x+nx,a.y+ny,z.x+nx,z.y+ny,paint);paint.pathEffect=null}
        }
        graph.annotations.forEach { note->
            val p=screen(ChemAtom(0,"",note.x,note.y));paint.color=Color.rgb(52,76,101);paint.style=Paint.Style.FILL;paint.textAlign=Paint.Align.CENTER;paint.textSize=(if(note.kind=="arrow")28 else 12)*resources.displayMetrics.scaledDensity
            c.drawText(note.text,p.x,p.y,paint)
        }
        graph.atoms.filter(::visible).forEach { a->val p=screen(a);paint.style=Paint.Style.FILL
            if(a.id in selected){paint.color=Color.rgb(207,231,255);c.drawCircle(p.x,p.y,22*resources.displayMetrics.density,paint)}
            val carbonVertex=skeletal&&a.symbol=="C"&&a.isotope==null&&a.charge==0&&a.label.isBlank()&&graph.bonds.any {it.a==a.id||it.b==a.id}
            if(carbonVertex)return@forEach
            paint.color=Color.WHITE;c.drawCircle(p.x,p.y,13*resources.displayMetrics.density,paint)
            paint.color=elementColor(a.symbol);paint.textSize=18*resources.displayMetrics.scaledDensity;paint.typeface=Typeface.create("sans-serif-medium",Typeface.NORMAL);paint.textAlign=Paint.Align.CENTER
            val attachedH=if(skeletal&&a.symbol!="C"&&a.symbol!="H")graph.bonds.count {b->(b.a==a.id||b.b==a.id)&&graph.atoms.any {it.id==(if(b.a==a.id)b.b else b.a)&&it.symbol=="H"&&!visible(it)}}+(a.hydrogens?:0)else 0
            c.drawText(a.label.ifEmpty { a.symbol+if(attachedH>0)"H"+if(attachedH>1)attachedH.toString()else ""else "" },p.x,p.y-(paint.ascent()+paint.descent())/2,paint)
            paint.textSize=10*resources.displayMetrics.scaledDensity
            if(a.charge!=0)c.drawText(if(a.charge==1)"+" else if(a.charge== -1)"−" else "${abs(a.charge)}${if(a.charge>0)"+" else "−"}",p.x+17*resources.displayMetrics.density,p.y-11*resources.displayMetrics.density,paint)
            a.isotope?.let { c.drawText(it.toString(),p.x-16*resources.displayMetrics.density,p.y-11*resources.displayMetrics.density,paint) }
        }
        if(path.size>1){paint.color=Color.rgb(44,119,224);paint.style=Paint.Style.STROKE;val p=Path();p.moveTo(path[0].x,path[0].y);path.drop(1).forEach { p.lineTo(it.x,it.y) };c.drawPath(p,paint)}
    }
    override fun performClick():Boolean{super.performClick();return true}
    override fun onTouchEvent(e:MotionEvent):Boolean{
        parent.requestDisallowInterceptTouchEvent(true);scale.onTouchEvent(e);gestures.onTouchEvent(e)
        if(e.pointerCount>=2){
            val p=PointF((e.getX(0)+e.getX(1))/2,(e.getY(0)+e.getY(1))/2)
            if(multi && e.actionMasked==MotionEvent.ACTION_MOVE){last?.let { panX+=p.x-it.x;panY+=p.y-it.y }}
            multi=true;last=p;invalidate();return true
        }
        when(e.actionMasked){
            MotionEvent.ACTION_DOWN->{hasUserInteraction=true;multi=false;dragging=false;start=PointF(e.x,e.y);last=start;startId=hit(e.x,e.y);before=graph;path.clear();if(tool==DrawTool.Lasso)path.add(PointF(e.x,e.y))}
            MotionEvent.ACTION_MOVE->{if(!multi){if(tool==DrawTool.Lasso){path.add(PointF(e.x,e.y));invalidate()}else if(tool==DrawTool.Select && startId!=null){val s=start!!;if(hypot(e.x-s.x,e.y-s.y)>8){dragging=true;val ids=if(startId in selected)selected else setOf(startId!!);val dx=(e.x-s.x)/unit;val dy=-(e.y-s.y)/unit;graph=before.copy(atoms=before.atoms.map { if(it.id in ids)it.copy(x=it.x+dx,y=it.y+dy)else it })}}}}
            MotionEvent.ACTION_UP->{performClick()
                if(!multi){
                    if(tool==DrawTool.Select && dragging){onChange(graph)}
                    else if(!dragging){val s=start?:PointF(e.x,e.y);val moved=hypot(e.x-s.x,e.y-s.y)>15*resources.displayMetrics.density;val p=world(e.x,e.y)
                        when(tool){
                            DrawTool.Select->{val id=hit(e.x,e.y);if(id==null)hitBond(e.x,e.y)?.let { onBondInspect(it) };selected=if(id==null)emptySet()else if(id in selected)selected-id else selected+id;onSelect(selected)}
                            DrawTool.Erase->{val id=hit(e.x,e.y);if(id!=null)onChange(graph.delete(setOf(id)))else hitBond(e.x,e.y)?.let { onChange(graph.edited(bonds=graph.bonds-it)) }}
                            DrawTool.Atom,DrawTool.Bond->{if(graph.atoms.size>=500)return true
                                if(moved || tool==DrawTool.Bond){
                                    var next=graph;val sp=world(s.x,s.y);val first=startId?:run { next=next.addAtom(element,sp.x.toDouble(),sp.y.toDouble());next.atoms.last().id }
                                    val existing=hit(e.x,e.y)?.takeIf { it!=first }
                                    val end=existing?:run{val px=if(!moved)sp.x+1.4f else p.x;val py=if(!moved)sp.y else p.y;next=next.addAtom(element,px.toDouble(),py.toDouble());next.atoms.last().id}
                                    onChange(next.bond(first,end,bondOrder,bondStereo))
                                }else {val id=hit(e.x,e.y);onChange(if(id==null)graph.addAtom(element,p.x.toDouble(),p.y.toDouble())else graph.edited(atoms=graph.atoms.map { if(it.id==id)it.copy(symbol=element,hydrogens=null,isotope=null,charge=0)else it }))}}
                            DrawTool.Ring->{var next=graph;val ids=mutableListOf<Int>();val anchor=hit(e.x,e.y)
                                val radius=1.4/(2*sin(PI/ringSize));val cx=p.x+if(anchor!=null)radius else 0.0
                                for(i in 0 until ringSize){if(i==0 && anchor!=null)ids+=anchor else {val a=PI+2*PI*i/ringSize;next=next.addAtom("C",cx+radius*cos(a),p.y+radius*sin(a));ids+=next.atoms.last().id}}
                                for(i in ids.indices)next=next.bond(ids[i],ids[(i+1)%ids.size],if(aromatic && i%2==0)2 else 1)
                                onChange(next)}
                            DrawTool.Lasso->{val polygon=Path();if(path.size>2){polygon.moveTo(path[0].x,path[0].y);path.drop(1).forEach { polygon.lineTo(it.x,it.y) };polygon.close();val region=Region();region.setPath(polygon,Region(0,0,width,height));selected=graph.atoms.filter { val a=screen(it);region.contains(a.x.toInt(),a.y.toInt()) }.map { it.id }.toSet();onSelect(selected)};path.clear();invalidate()}
                        }
                    }
                    if(tool==DrawTool.Select && lastTapTool==DrawTool.Select && !dragging && e.eventTime-lastUp<300 && hypot(e.x-lastTapX,e.y-lastTapY)<32*resources.displayMetrics.density){fit()};lastUp=e.eventTime;lastTapX=e.x;lastTapY=e.y;lastTapTool=tool
                };multi=false;start=null;last=null
            }
            MotionEvent.ACTION_CANCEL->{if(dragging)graph=before;start=null;path.clear();multi=false;invalidate()}
        };return true
    }
}
