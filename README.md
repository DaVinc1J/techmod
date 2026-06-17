## Tech Mod (skeleton)

A Minecraft Forge 1.12.2 mod skeleton for [Cleanroom Loader](https://cleanroommc.com/), with a working JNI bridge to a native C library. Based on Cleanroom's official [ForgeDevEnv](https://github.com/CleanroomMC/ForgeDevEnv) template.

Stack: **Java 25** + **Gradle 9.5.1** + **[RetroFuturaGradle](https://github.com/GTNewHorizons/RetroFuturaGradle) 2.0.2** (the build system Cleanroom now uses instead of legacy ForgeGradle) + **Forge 14.23.5.2847 / Cleanroom**.

### Why it's structured this way

Forge's mod loader requires Java classes as entry points (`@Mod`-annotated classes, event handlers, registries) — there's no way around that, it's baked into the loader. So the split here is:

- **Java** (`src/main/java`) — the minimum glue Forge actually requires: `TechMod.java` is the `@Mod` entry point, and `bridge/NativeCore.java` loads the native library and declares the `native` methods.
- **C** (`native/src`) — everything else. Your actual mod logic goes here. If it ever needs to touch a Minecraft/Forge object (a `Block`, `World`, `TileEntity`, etc.), it has to call back into Java through the `JNIEnv*` passed into every native function, since those are Java objects with no native representation.

### First-time setup

1. Make sure `JAVA_HOME` points at a JDK 25 install (any vendor — Azul/Adoptium/etc. all have linux-aarch64 builds, which is what you need on Asahi).
2. `./gradlew runClient` — this is the first real test. It'll download Forge/MCP/library dependencies (takes a while the first time), decompile/patch Minecraft, then launch the game with the mod loaded. Watch the log for:
   ```
   Hello from Tech Mod!
   Native bridge says: "hello from C" (2 + 3 from C = 5)
   ```
   If you see that, the whole pipeline — Forge → Java → JNI → C → back to Java — is working end-to-end.

A prebuilt `linux-aarch64` native library is already checked into `src/main/resources/natives/linux-aarch64/libtechmod.so`, so step 2 should work immediately without touching the C toolchain. You only need to rebuild it once you start changing `native/src/techmod.c`.

### Working on the C side

1. Edit `native/src/techmod.c` (and `native/include/techmod_NativeCore.h` if you add/change method signatures — see the comment at the top of that file for the naming rule, or just regenerate it with `javac -h`).
2. Add the matching `native` method declaration to `NativeCore.java`.
3. Rebuild:
   ```
   cd native
   ./build.sh
   ```
   This detects your OS/arch (Linux aarch64 on Asahi) and writes the compiled `.so` straight into `src/main/resources/natives/linux-aarch64/`, which is where `NativeCore.ensureLoaded()` looks for it.
4. Re-run `./gradlew runClient`.

Note `NativeCore` extracts whichever platform's library to a temp file before loading it, since a `.so` can't be `System.load()`-ed directly out of a jar. If you ever build for a teammate on a different OS/CPU, just run `native/build.sh` on their machine (or cross-compile) and commit the result under the matching `natives/<platform>/` folder — `NativeCore` picks the right one automatically at runtime.

### Renaming the mod

Everything mod-identity-related (`mod_id`, `mod_name`, package name) is parameterized in `gradle.properties` — `Tags.MOD_ID` / `Tags.MOD_NAME` / `Tags.VERSION` are generated from it at build time, so you don't hand-edit a Tags class. If you change `root_package` or `mod_id`, also move the Java package directories under `src/main/java` to match, and update the JNI mangled names in the header/`.c` file accordingly (they encode the full package path).

### IDE

If you'd rather use an IDE than the CLI: IntelliJ IDEA picks this up as a normal Gradle project (`Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JVM` should be set to Java 25), with run configurations for `runClient`/`runServer` auto-generated.

### Notes

- Dependencies script: [gradle/scripts/dependencies.gradle](gradle/scripts/dependencies.gradle) (commented inline).
- Publishing script: [gradle/scripts/publishing.gradle](gradle/scripts/publishing.gradle) — disabled by default (`publish_to_*` flags in `gradle.properties`), safe to ignore for now.
- Mixins aren't enabled in this skeleton (`use_mixins = false`). Turn that on later if you need to alter vanilla/Forge behavior rather than just adding new content.
