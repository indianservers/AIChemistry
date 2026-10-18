# CDK Android layout adaptation

CDK 2.13, LGPL-2.1-or-later, https://github.com/cdk/cdk/tree/cdk-2.13.

Only `org.openscience.cdk.layout.LayoutRefiner` is modified. The Android build replaces the AWT `Line2D.linesIntersect` call with an inclusive, bounding-box and cross-product segment test. All other classes/resources in cdk-sdg-2.13.jar are unchanged. This avoids a real NoClassDefFoundError on Android; it is not an R8 warning suppression.

Modified source is in src/. The complete upstream SDG source archive and license are included here. Other CDK modules are unmodified Maven dependencies with source artifacts at https://repo.maven.apache.org/maven2/org/openscience/cdk/.

Rebuild: resolve the application's CDK dependencies with Gradle, then run `python tools/build-android-cdk-sdg.py`. It compiles the modified class with Java 11 compatibility and replaces only that class in the original artifact. `app/libs/cdk-sdg-2.13-android.jar` is the resulting artifact. The build script is also part of the source distribution.

Recipients may modify, relink and reverse engineer the CDK components for debugging modifications as allowed by the LGPL. This adaptation remains under the same LGPL license. No warranty is provided.
