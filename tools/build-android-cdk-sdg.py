"""Rebuild the narrowly patched LGPL CDK SDG artifact after Gradle resolves 2.13 dependencies."""
from pathlib import Path
import zipfile,subprocess,hashlib
root=Path(__file__).resolve().parents[1]
cache=Path.home()/'.gradle/caches/modules-2/files-2.1'
jars=list((cache/'org.openscience.cdk').rglob('2.13/**/*.jar'))+list((cache/'javax.vecmath').rglob('*.jar'))
original=next(p for p in jars if p.name=='cdk-sdg-2.13.jar')
work=root/'third_party/cdk-android/build';work.mkdir(parents=True,exist_ok=True)
source=root/'third_party/cdk-android/src/org/openscience/cdk/layout/LayoutRefiner.java'
args=['--release','11','-encoding','UTF-8','-cp',';'.join(str(p) for p in jars),'-d',str(work),str(source)]
argfile=work/'javac.args';argfile.write_text('\n'.join('"'+a.replace('\\','/')+'"' for a in args),encoding='utf-8')
subprocess.run(['C:/Program Files/Android/Android Studio/jbr/bin/javac.exe','@'+str(argfile)],check=True)
output=root/'app/libs/cdk-sdg-2.13-android.jar';output.parent.mkdir(exist_ok=True)
with zipfile.ZipFile(original) as zin,zipfile.ZipFile(output,'w',zipfile.ZIP_DEFLATED) as zout:
 for item in zin.infolist():
  if not item.filename.startswith('org/openscience/cdk/layout/LayoutRefiner'):
   zout.writestr(item,zin.read(item.filename))
 for cls in work.rglob('*.class'):zout.write(cls,cls.relative_to(work).as_posix())
print(str(output),hashlib.sha256(output.read_bytes()).hexdigest())
