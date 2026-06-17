# Native library

C source for the mod's native logic, bridged to Java via JNI (see `com.example.techmod.bridge.NativeCore`).

## Build

```
export JAVA_HOME=/path/to/jdk        # any JDK 8+, just needs jni.h
./build.sh
```

Detects your OS/architecture and writes the compiled library into
`../src/main/resources/natives/<platform>/`, e.g. `linux-aarch64` on Asahi.
That's where `NativeCore.ensureLoaded()` looks for it at runtime — it gets
bundled into the mod jar as a normal resource and extracted to a temp file
on first load (a shared library can't be loaded directly out of a jar).

## Adding a new native method

1. Declare it in `NativeCore.java`: `public static native <type> name(...);`
2. Add the matching declaration to `include/techmod_NativeCore.h`. The
   mangled name is always `Java_<package_with_underscores>_<Class>_<method>`,
   so you can hand-edit this, or regenerate it properly:
   ```
   javac -h include -d /tmp/jni-out src/main/java/com/example/techmod/bridge/NativeCore.java
   ```
   (needs the rest of the project's compile classpath to actually succeed —
   easiest to just run this from the project root with Forge's deobf jar on
   the classpath, or hand-edit the header, since the naming rule is fixed.)
3. Implement it in `src/techmod.c`.
4. `./build.sh` again.

## A note on what belongs here vs. in Java

Pure logic/computation can live entirely in C. Anything that needs a
Minecraft/Forge object — a `Block`, `World`, `TileEntity`, `EntityPlayer`,
registries, packets, GUIs — has to go back through Java, since those only
exist as Java objects. Call back into the JVM via the `JNIEnv*` argument
every native function receives (`(*env)->CallStaticVoidMethod(env, ...)`
and friends) when you need to do that.
