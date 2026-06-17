package com.example.techmod.bridge;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Java-side half of the JNI bridge.
 *
 * The mod loader requires Java entry points (the @Mod class, event handlers, etc.),
 * so that's what this class is: as thin as possible. It only exists to (a) load the
 * compiled native library and (b) declare the native methods that the C side
 * implements. All the actual logic for those methods lives in native/src/techmod.c.
 *
 * A .so/.dll/.dylib can't be loaded directly out of a jar, so on first use we copy
 * the bundled native library out to a temp file and load it from there.
 */
public final class NativeCore {

    private static volatile boolean loaded = false;

    private NativeCore() {
    }

    // --- Native method declarations -----------------------------------------
    // Add new native methods here, then implement them in native/src/techmod.c.
    // After changing a signature, regenerate the JNI header with:
    //   javac -h native/include -d /tmp/jni-out src/main/java/com/example/techmod/bridge/NativeCore.java
    // (You'll need Forge's jar on the classpath for that to compile standalone;
    // easier in practice to just hand-edit the header, see native/include/techmod_NativeCore.h)

    public static native String nativeHello();

    public static native int nativeAdd(int a, int b);

    // --------------------------------------------------------------------------

    /**
     * Extracts and loads the native library for the current OS/architecture.
     * Safe to call repeatedly; only loads once.
     */
    public static synchronized void ensureLoaded() {
        if (loaded) {
            return;
        }

        String os = System.getProperty("os.name", "").toLowerCase();
        String arch = System.getProperty("os.arch", "").toLowerCase();
        boolean isArm = arch.contains("aarch64") || arch.contains("arm");

        String platformDir;
        String libFileName;
        if (os.contains("win")) {
            platformDir = isArm ? "windows-aarch64" : "windows-x86_64";
            libFileName = "techmod.dll";
        } else if (os.contains("mac") || os.contains("darwin")) {
            platformDir = isArm ? "macos-aarch64" : "macos-x86_64";
            libFileName = "libtechmod.dylib";
        } else {
            platformDir = isArm ? "linux-aarch64" : "linux-x86_64";
            libFileName = "libtechmod.so";
        }

        String resourcePath = "/natives/" + platformDir + "/" + libFileName;

        try (InputStream in = NativeCore.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new UnsatisfiedLinkError(
                        "No native library bundled at " + resourcePath
                                + " — build it for this platform (see native/README.md) "
                                + "and make sure it landed in src/main/resources" + resourcePath);
            }

            String suffix = libFileName.substring(libFileName.lastIndexOf('.'));
            Path tempFile = Files.createTempFile("techmod-native-", suffix);
            tempFile.toFile().deleteOnExit();
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);

            System.load(tempFile.toAbsolutePath().toString());
            loaded = true;
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract native library from " + resourcePath, e);
        }
    }
}
