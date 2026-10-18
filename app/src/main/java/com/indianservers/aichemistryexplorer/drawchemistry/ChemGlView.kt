package com.indianservers.aichemistryexplorer.drawchemistry

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*

/** GLES2 sphere impostors and bonds, rendered from source coordinates in angstroms. */
internal class ChemGlView(context:Context):GLSurfaceView(context){
    private val renderer=MoleculeRenderer()
    var onSelect:(Int)->Unit={}
    var graph=ChemGraph();set(value){field=value;renderer.graph=value;requestRender()}
    var style=0;set(value){field=value;renderer.style=value;requestRender()}
    var hydrogens=true;set(value){field=value;renderer.hydrogens=value;requestRender()}
    var selected=emptySet<Int>();set(value){field=value;renderer.selected=value;requestRender()}
    private var lastX=0f;private var lastY=0f;private var downX=0f;private var downY=0f;private var multi=false
    private val scale=ScaleGestureDetector(context,object:ScaleGestureDetector.SimpleOnScaleGestureListener(){override fun onScale(d:ScaleGestureDetector):Boolean{renderer.zoom=(renderer.zoom*d.scaleFactor).coerceIn(.3f,5f);requestRender();return true}})
    init {setEGLContextClientVersion(2);setRenderer(renderer);renderMode=RENDERMODE_WHEN_DIRTY;preserveEGLContextOnPause=true;contentDescription="Interactive 3D molecule. Drag to rotate, pinch to zoom, two fingers to pan, tap atoms to measure."}
    fun reset(){renderer.rx=20f;renderer.ry= -20f;renderer.zoom=1f;renderer.panX=0f;renderer.panY=0f;requestRender()}
    override fun performClick():Boolean{super.performClick();return true}
    override fun onTouchEvent(e:MotionEvent):Boolean {
        parent.requestDisallowInterceptTouchEvent(true);scale.onTouchEvent(e)
        if(e.actionMasked==MotionEvent.ACTION_DOWN){lastX=e.x;lastY=e.y;downX=e.x;downY=e.y;multi=false}
        if(e.pointerCount>1){multi=true;if(e.actionMasked==MotionEvent.ACTION_MOVE){renderer.panX+=(e.x-lastX)/width*2;renderer.panY-=(e.y-lastY)/height*2}}
        else if(e.actionMasked==MotionEvent.ACTION_MOVE && !multi){renderer.ry+=(e.x-lastX)*.4f;renderer.rx+=(e.y-lastY)*.4f}
        if(e.actionMasked==MotionEvent.ACTION_UP && !multi && hypot(e.x-downX,e.y-downY)<12*resources.displayMetrics.density){performClick();renderer.pick(e.x,e.y)?.let(onSelect)}
        lastX=e.x;lastY=e.y;requestRender();return true
    }
    internal class MoleculeRenderer:Renderer {
        @Volatile var graph=ChemGraph();@Volatile var style=0;@Volatile var hydrogens=true;@Volatile var selected=emptySet<Int>()
        @Volatile var rx=20f;@Volatile var ry= -20f;@Volatile var zoom=1f;@Volatile var panX=0f;@Volatile var panY=0f
        private var program=0;private var w=1;private var h=1
        private val matrix=FloatArray(16);private val view=FloatArray(16);private val projection=FloatArray(16);private val model=FloatArray(16);private val tmp=FloatArray(16)
        @Volatile private var projected=emptyList<Triple<Int,Float,Float>>()
        override fun onSurfaceCreated(gl:GL10?,config:EGLConfig?){
            val vertex="""uniform mat4 mvp; uniform float pointScale; attribute vec3 pos; attribute vec4 color; attribute float radius; varying vec4 vColor; void main(){ gl_Position=mvp*vec4(pos,1.0); gl_PointSize=radius*pointScale; vColor=color;}"""
            val fragment="""precision mediump float; uniform int spheres; varying vec4 vColor; void main(){if(spheres==1){vec2 p=gl_PointCoord*2.0-1.0;float r=dot(p,p);if(r>1.0)discard;float z=sqrt(1.0-r);float light=0.32+0.68*max(0.0,dot(normalize(vec3(p.x,-p.y,z)),normalize(vec3(-0.4,0.6,1.0))));gl_FragColor=vec4(vColor.rgb*light+vec3(pow(z,24.0)*0.15),1.0);}else gl_FragColor=vColor;}"""
            fun shader(type:Int,src:String):Int {val s=glCreateShader(type);glShaderSource(s,src);glCompileShader(s);val status=IntArray(1);glGetShaderiv(s,GL_COMPILE_STATUS,status,0);check(status[0]!=0){glGetShaderInfoLog(s)};return s}
            program=glCreateProgram();glAttachShader(program,shader(GL_VERTEX_SHADER,vertex));glAttachShader(program,shader(GL_FRAGMENT_SHADER,fragment));glLinkProgram(program)
            glEnable(GL_DEPTH_TEST);glClearColor(.985f,.99f,1f,1f)
        }
        override fun onSurfaceChanged(gl:GL10?,width:Int,height:Int){w=width;h=height;glViewport(0,0,width,height)}
        override fun onDrawFrame(gl:GL10?){
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT);val g=graph;val atoms=g.conformer.filter { hydrogens || it.symbol!="H" };if(atoms.isEmpty())return
            val cx=atoms.map { it.x }.average();val cy=atoms.map { it.y }.average();val cz=atoms.map { it.z }.average()
            val extent=atoms.maxOf { max(abs(it.x-cx),max(abs(it.y-cy),abs(it.z-cz))) }.toFloat().coerceAtLeast(1.5f)+1f
            val aspect=w.toFloat()/h;Matrix.orthoM(projection,0,-extent*aspect/zoom,extent*aspect/zoom,-extent/zoom,extent/zoom,-100f,100f)
            Matrix.setIdentityM(model,0);Matrix.translateM(model,0,panX*extent,panY*extent,0f);Matrix.rotateM(model,0,rx,1f,0f,0f);Matrix.rotateM(model,0,ry,0f,1f,0f)
            Matrix.translateM(model,0,-cx.toFloat(),-cy.toFloat(),-cz.toFloat());Matrix.setIdentityM(view,0);Matrix.multiplyMM(tmp,0,view,0,model,0);Matrix.multiplyMM(matrix,0,projection,0,tmp,0)
            glUseProgram(program);glUniformMatrix4fv(glGetUniformLocation(program,"mvp"),1,false,matrix,0)
            glUniform1f(glGetUniformLocation(program,"pointScale"),h/extent*zoom)
            val positions=atoms.associateBy { it.id }
            val bonds=mutableListOf<Float>();val spheres=mutableListOf<Float>();val points=mutableListOf<Triple<Int,Float,Float>>()
            fun vertex(list:MutableList<Float>,a:ChemAtom,r:Float){val color=if(a.id in selected)android.graphics.Color.rgb(18,184,186)else elementColor(a.symbol);list+=listOf(a.x.toFloat(),a.y.toFloat(),a.z.toFloat(),android.graphics.Color.red(color)/255f,android.graphics.Color.green(color)/255f,android.graphics.Color.blue(color)/255f,1f,r)}
            g.bonds.forEach { b->val a=positions[b.a];val z=positions[b.b];if(a!=null && z!=null && style!=1){
                val dx=z.x-a.x;val dy=z.y-a.y;val dz=z.z-a.z
                var nx= -dy;var ny=dx;var nz=0.0
                if(abs(nx)+abs(ny)<1e-6){nx=0.0;ny= -dz;nz=dy}
                val length=sqrt(nx*nx+ny*ny+nz*nz).coerceAtLeast(1e-8)
                val count=b.order.coerceIn(1,3)
                repeat(count){i->val offset=(i-(count-1)/2.0)*.13
                    vertex(bonds,a.copy(x=a.x+nx/length*offset,y=a.y+ny/length*offset,z=a.z+nz/length*offset),1f)
                    vertex(bonds,z.copy(x=z.x+nx/length*offset,y=z.y+ny/length*offset,z=z.z+nz/length*offset),1f)
                }
            } }
            atoms.forEach { a->val r=when(style){1->when(a.symbol){"H"->1.2f;"C"->1.7f;"O"->1.52f;"N"->1.55f;else->1.8f};2->.08f;3->.17f;else->if(a.symbol=="H").20f else .35f};vertex(spheres,a,r)
                val out=FloatArray(4);Matrix.multiplyMV(out,0,matrix,0,floatArrayOf(a.x.toFloat(),a.y.toFloat(),a.z.toFloat(),1f),0);points+=Triple(a.id,(out[0]/out[3]+1)*w/2,(1-out[1]/out[3])*h/2)
            };projected=points
            fun draw(data:List<Float>,mode:Int,sphere:Boolean){if(data.isEmpty())return;val buffer=ByteBuffer.allocateDirect(data.size*4).order(ByteOrder.nativeOrder()).asFloatBuffer();buffer.put(data.toFloatArray());buffer.position(0)
                val p=glGetAttribLocation(program,"pos");val c=glGetAttribLocation(program,"color");val r=glGetAttribLocation(program,"radius")
                buffer.position(0);glVertexAttribPointer(p,3,GL_FLOAT,false,32,buffer);glEnableVertexAttribArray(p)
                buffer.position(3);glVertexAttribPointer(c,4,GL_FLOAT,false,32,buffer);glEnableVertexAttribArray(c)
                buffer.position(7);glVertexAttribPointer(r,1,GL_FLOAT,false,32,buffer);glEnableVertexAttribArray(r)
                glUniform1i(glGetUniformLocation(program,"spheres"),if(sphere)1 else 0);glDrawArrays(mode,0,data.size/8)
            }
            glLineWidth(if(style==3)7f else if(style==2)1f else 3f);draw(bonds,GL_LINES,false);draw(spheres,GL_POINTS,true)
        }
        fun pick(x:Float,y:Float):Int?=projected.minByOrNull { hypot(it.second-x,it.third-y) }?.takeIf { hypot(it.second-x,it.third-y)<50f }?.first
    }
}
