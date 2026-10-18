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
    var onZoomChange:(Float)->Unit={}
    var gestureMode=0
    var graph=ChemGraph();set(value){field=value;renderer.graph=value;requestRender()}
    var style=0;set(value){field=value;renderer.style=value;requestRender()}
    var hydrogens=true;set(value){field=value;renderer.hydrogens=value;requestRender()}
    var selected=emptySet<Int>();set(value){field=value;renderer.selected=value;requestRender()}
    private var lastX=0f;private var lastY=0f;private var downX=0f;private var downY=0f;private var multi=false
    private val scale=ScaleGestureDetector(context,object:ScaleGestureDetector.SimpleOnScaleGestureListener(){override fun onScale(d:ScaleGestureDetector):Boolean{setZoom(renderer.zoom*d.scaleFactor);return true}})
    init {setEGLContextClientVersion(2);setRenderer(renderer);renderMode=RENDERMODE_WHEN_DIRTY;preserveEGLContextOnPause=true;contentDescription="Interactive 3D molecule. Drag to rotate, pinch to zoom, two fingers to pan, tap atoms to measure."}
    fun setZoom(value:Float){renderer.zoom=value.coerceIn(.4f,3f);onZoomChange(renderer.zoom);requestRender()}
    fun orient(axis:Int){renderer.rx=if(axis==1)90f else 0f;renderer.ry=if(axis==0)90f else 0f;renderer.panX=0f;renderer.panY=0f;requestRender()}
    fun reset(){renderer.rx=20f;renderer.ry= -20f;renderer.panX=0f;renderer.panY=0f;setZoom(1f)}
    override fun performClick():Boolean{super.performClick();return true}
    override fun onTouchEvent(e:MotionEvent):Boolean {
        parent.requestDisallowInterceptTouchEvent(true);scale.onTouchEvent(e)
        if(e.actionMasked==MotionEvent.ACTION_DOWN){lastX=e.x;lastY=e.y;downX=e.x;downY=e.y;multi=false}
        if(e.pointerCount>1){multi=true;if(e.actionMasked==MotionEvent.ACTION_MOVE){renderer.panX+=(e.x-lastX)/width*2;renderer.panY-=(e.y-lastY)/height*2}}
        else if(e.actionMasked==MotionEvent.ACTION_MOVE && !multi){when(gestureMode){
            1->setZoom(renderer.zoom*exp(-(e.y-lastY)/220f))
            2->{renderer.panX+=(e.x-lastX)/width*2;renderer.panY-=(e.y-lastY)/height*2}
            else->{renderer.ry+=(e.x-lastX)*.4f;renderer.rx+=(e.y-lastY)*.4f}
        }}
        if(e.actionMasked==MotionEvent.ACTION_UP && !multi && hypot(e.x-downX,e.y-downY)<12*resources.displayMetrics.density){performClick();renderer.pick(e.x,e.y)?.let(onSelect)}
        lastX=e.x;lastY=e.y;requestRender();return true
    }
    /** Cached sphere/cylinder meshes. Camera gestures update matrices without rebuilding geometry. */
    internal class MoleculeRenderer:Renderer {
        @Volatile var graph=ChemGraph();@Volatile var style=0;@Volatile var hydrogens=true;@Volatile var selected=emptySet<Int>()
        @Volatile var rx=20f;@Volatile var ry= -20f;@Volatile var zoom=1f;@Volatile var panX=0f;@Volatile var panY=0f
        private var program=0;private var buffer=0;private var count=0;private var w=1;private var h=1
        private var meshGraph:ChemGraph?=null;private var meshStyle= -1;private var meshHydrogens=false;private var meshSelection=emptySet<Int>()
        private val matrix=FloatArray(16);private val projection=FloatArray(16);private val model=FloatArray(16)
        @Volatile private var projected=emptyList<Triple<Int,Float,Float>>()
        override fun onSurfaceCreated(gl:GL10?,config:EGLConfig?){
            val vertex="""uniform mat4 mvp; uniform mat4 model; attribute vec3 pos; attribute vec3 normal; attribute vec4 color; varying vec3 vNormal; varying vec4 vColor; void main(){gl_Position=mvp*vec4(pos,1.0);vNormal=mat3(model)*normal;vColor=color;}"""
            val fragment="""precision mediump float; uniform int unlit; varying vec3 vNormal; varying vec4 vColor; void main(){if(unlit==1){gl_FragColor=vColor;}else{vec3 n=normalize(vNormal);vec3 l=normalize(vec3(-0.45,0.65,1.0));float diffuse=max(dot(n,l),0.0);float shine=pow(max(dot(n,normalize(l+vec3(0.0,0.0,1.0))),0.0),42.0);gl_FragColor=vec4(vColor.rgb*(0.25+0.75*diffuse)+vec3(0.72*shine),1.0);}}"""
            fun shader(type:Int,src:String):Int{val id=glCreateShader(type);glShaderSource(id,src);glCompileShader(id);val status=IntArray(1);glGetShaderiv(id,GL_COMPILE_STATUS,status,0);check(status[0]!=0){glGetShaderInfoLog(id)};return id}
            val vs=shader(GL_VERTEX_SHADER,vertex);val fs=shader(GL_FRAGMENT_SHADER,fragment);program=glCreateProgram();glAttachShader(program,vs);glAttachShader(program,fs);glLinkProgram(program)
            val status=IntArray(1);glGetProgramiv(program,GL_LINK_STATUS,status,0);check(status[0]!=0){glGetProgramInfoLog(program)};glDeleteShader(vs);glDeleteShader(fs)
            val ids=IntArray(1);glGenBuffers(1,ids,0);buffer=ids[0];meshGraph=null
            glEnable(GL_DEPTH_TEST);glClearColor(.96f,.975f,.99f,1f)
        }
        override fun onSurfaceChanged(gl:GL10?,width:Int,height:Int){w=width.coerceAtLeast(1);h=height.coerceAtLeast(1);glViewport(0,0,w,h)}
        override fun onDrawFrame(gl:GL10?){
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
            val g=graph;val atoms=g.conformer.filter {hydrogens||it.symbol!="H"};if(atoms.isEmpty())return
            if(meshGraph!==g||meshStyle!=style||meshHydrogens!=hydrogens||meshSelection!=selected){buildMesh(g,atoms);meshGraph=g;meshStyle=style;meshHydrogens=hydrogens;meshSelection=selected}
            val cx=atoms.map {it.x}.average();val cy=atoms.map {it.y}.average();val cz=atoms.map {it.z}.average()
            val extent=atoms.maxOf {max(abs(it.x-cx),max(abs(it.y-cy),abs(it.z-cz)))}.toFloat().coerceAtLeast(1.4f)+.7f
            val aspect=w.toFloat()/h;Matrix.orthoM(projection,0,-extent*aspect/zoom,extent*aspect/zoom,-extent/zoom,extent/zoom,-100f,100f)
            Matrix.setIdentityM(model,0);Matrix.translateM(model,0,panX*extent,panY*extent,0f);Matrix.rotateM(model,0,rx,1f,0f,0f);Matrix.rotateM(model,0,ry,0f,1f,0f);Matrix.translateM(model,0,-cx.toFloat(),-cy.toFloat(),-cz.toFloat());Matrix.multiplyMM(matrix,0,projection,0,model,0)
            glUseProgram(program);glUniformMatrix4fv(glGetUniformLocation(program,"mvp"),1,false,matrix,0);glUniformMatrix4fv(glGetUniformLocation(program,"model"),1,false,model,0);glUniform1i(glGetUniformLocation(program,"unlit"),if(style==3)1 else 0)
            glBindBuffer(GL_ARRAY_BUFFER,buffer)
            val p=glGetAttribLocation(program,"pos");val n=glGetAttribLocation(program,"normal");val c=glGetAttribLocation(program,"color")
            glEnableVertexAttribArray(p);glVertexAttribPointer(p,3,GL_FLOAT,false,40,0);glEnableVertexAttribArray(n);glVertexAttribPointer(n,3,GL_FLOAT,false,40,12);glEnableVertexAttribArray(c);glVertexAttribPointer(c,4,GL_FLOAT,false,40,24)
            glLineWidth(2f);glDrawArrays(if(style==3)GL_LINES else GL_TRIANGLES,0,count);glBindBuffer(GL_ARRAY_BUFFER,0)
            projected=atoms.map {a->val out=FloatArray(4);Matrix.multiplyMV(out,0,matrix,0,floatArrayOf(a.x.toFloat(),a.y.toFloat(),a.z.toFloat(),1f),0);Triple(a.id,(out[0]/out[3]+1)*w/2,(1-out[1]/out[3])*h/2)}
        }
        private fun buildMesh(g:ChemGraph,atoms:List<ChemAtom>){
            val data=ArrayList<Float>();val positions=atoms.associateBy {it.id}
            fun color(a:ChemAtom):Int=if(a.id in selected)android.graphics.Color.rgb(0,180,225)else when(a.symbol){"H"->android.graphics.Color.rgb(225,229,235);"C"->android.graphics.Color.rgb(47,51,58);"O"->android.graphics.Color.rgb(228,28,37);else->elementColor(a.symbol)}
            fun vertex(x:Double,y:Double,z:Double,nx:Double,ny:Double,nz:Double,color:Int){data.add(x.toFloat());data.add(y.toFloat());data.add(z.toFloat());data.add(nx.toFloat());data.add(ny.toFloat());data.add(nz.toFloat());data.add(android.graphics.Color.red(color)/255f);data.add(android.graphics.Color.green(color)/255f);data.add(android.graphics.Color.blue(color)/255f);data.add(1f)}
            if(style!=1)g.bonds.forEach {b->val a=positions[b.a];val d=positions[b.b];if(a!=null&&d!=null){
                val dx=d.x-a.x;val dy=d.y-a.y;val dz=d.z-a.z;val length=sqrt(dx*dx+dy*dy+dz*dz).coerceAtLeast(1e-8)
                val ax=dx/length;val ay=dy/length;val az=dz/length
                var ux= -ay;var uy=ax;var uz=0.0;if(abs(ux)+abs(uy)<1e-5){ux=0.0;uy= -az;uz=ay};val ulen=sqrt(ux*ux+uy*uy+uz*uz).coerceAtLeast(1e-8);ux/=ulen;uy/=ulen;uz/=ulen
                val vx=ay*uz-az*uy;val vy=az*ux-ax*uz;val vz=ax*uy-ay*ux
                repeat(b.order.coerceIn(1,3)){bondIndex->val offset=(bondIndex-(b.order-1)/2.0)*.18
                    if(style==3){vertex(a.x+ux*offset,a.y+uy*offset,a.z+uz*offset,0.0,0.0,1.0,color(a));vertex(d.x+ux*offset,d.y+uy*offset,d.z+uz*offset,0.0,0.0,1.0,color(d))}
                    else {
                        val radius=if(style==2).12 else .075
                        fun cylinderVertex(end:Int,index:Int){val t=2*PI*index/12;val nx=ux*cos(t)+vx*sin(t);val ny=uy*cos(t)+vy*sin(t);val nz=uz*cos(t)+vz*sin(t);val atom=if(end==0)a else d;vertex(atom.x+ux*offset+radius*nx,atom.y+uy*offset+radius*ny,atom.z+uz*offset+radius*nz,nx,ny,nz,color(atom))}
                        repeat(12){i->cylinderVertex(0,i);cylinderVertex(1,i);cylinderVertex(1,i+1);cylinderVertex(0,i);cylinderVertex(1,i+1);cylinderVertex(0,i+1)}
                    }
                }
            }}
            if(style!=3)atoms.forEach {a->
                val radius=when(style){1->when(a.symbol){"H"->1.2;"C"->1.7;"O"->1.52;"N"->1.55;else->1.8};2->.14;else->if(a.symbol=="H").23 else .36}
                val lat=if(atoms.size>150)6 else 12;val lon=if(atoms.size>150)10 else 18
                fun sphereVertex(i:Int,j:Int){val theta=PI*i/lat;val phi=2*PI*j/lon;val nx=sin(theta)*cos(phi);val ny=cos(theta);val nz=sin(theta)*sin(phi);vertex(a.x+radius*nx,a.y+radius*ny,a.z+radius*nz,nx,ny,nz,color(a))}
                repeat(lat){i->repeat(lon){j->sphereVertex(i,j);sphereVertex(i+1,j);sphereVertex(i+1,j+1);sphereVertex(i,j);sphereVertex(i+1,j+1);sphereVertex(i,j+1)}}
            }
            val values=data.toFloatArray();val bytes=ByteBuffer.allocateDirect(values.size*4).order(ByteOrder.nativeOrder()).asFloatBuffer();bytes.put(values).position(0)
            glBindBuffer(GL_ARRAY_BUFFER,buffer);glBufferData(GL_ARRAY_BUFFER,values.size*4,bytes,GL_STATIC_DRAW);glBindBuffer(GL_ARRAY_BUFFER,0);count=values.size/10
        }
        fun pick(x:Float,y:Float):Int?=projected.minByOrNull {hypot(it.second-x,it.third-y)}?.takeIf {hypot(it.second-x,it.third-y)<50f}?.first
    }
}
