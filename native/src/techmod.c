#include "techmod_NativeCore.h"

/*
 * This is where your mod's actual logic lives. The two functions below are
 * just a wired-up "hello world" proving the Java <-> C round trip works;
 * replace/extend them with whatever the mod actually needs to compute.
 *
 * Anything that needs to touch Minecraft/Forge objects (blocks, entities,
 * TileEntities, etc.) has to call back up into Java via the JNIEnv*, since
 * those are Java objects with no native representation. Pure computation
 * (simulation, pathfinding, number crunching, whatever) can live entirely
 * in here. Note the (*env)->Method(env, ...) calling convention -- this is
 * plain C, not C++, so JNIEnv is a pointer-to-a-pointer-to-a-function-table.
 */

JNIEXPORT jstring JNICALL Java_com_example_techmod_bridge_NativeCore_nativeHello
  (JNIEnv *env, jclass clazz) {
    (void) clazz;
    return (*env)->NewStringUTF(env, "hello from C");
}

JNIEXPORT jint JNICALL Java_com_example_techmod_bridge_NativeCore_nativeAdd
  (JNIEnv *env, jclass clazz, jint a, jint b) {
    (void) env;
    (void) clazz;
    return a + b;
}
