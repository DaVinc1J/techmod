/* Hand-written to match javac -h output format. If you add/change native
 * methods in NativeCore.java, regenerate this with:
 *
 *   javac -h native/include -d /tmp/jni-out \
 *       -cp <forge-deobf-jar-from-build/rfg-cache> \
 *       src/main/java/com/example/techmod/bridge/NativeCore.java
 *
 * In practice it's usually faster to just hand-edit this file, since the
 * mangled names follow a fixed rule: Java_<package_with_underscores>_<Class>_<method>
 */
#include <jni.h>
/* Header for class com_example_techmod_bridge_NativeCore */

#ifndef _Included_com_example_techmod_bridge_NativeCore
#define _Included_com_example_techmod_bridge_NativeCore
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     com_example_techmod_bridge_NativeCore
 * Method:    nativeHello
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_example_techmod_bridge_NativeCore_nativeHello
  (JNIEnv *, jclass);

/*
 * Class:     com_example_techmod_bridge_NativeCore
 * Method:    nativeAdd
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_example_techmod_bridge_NativeCore_nativeAdd
  (JNIEnv *, jclass, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
