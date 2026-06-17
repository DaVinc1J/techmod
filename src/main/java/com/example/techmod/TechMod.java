package com.example.techmod;

import com.example.techmod.bridge.NativeCore;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class TechMod {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    /**
     * Take a look at how many FMLStateEvents you can listen to via the @Mod.EventHandler annotation here:
     * https://cleanroommc.com/wiki/forge-mod-development/event#overview
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Hello from {}!", Tags.MOD_NAME);

        // Sanity-check that the JNI bridge works before anything else touches it.
        // Real per-tick / per-event logic should call into NativeCore from wherever
        // it's actually needed, not just here.
        try {
            NativeCore.ensureLoaded();
            String greeting = NativeCore.nativeHello();
            int sum = NativeCore.nativeAdd(2, 3);
            LOGGER.info("Native bridge says: \"{}\" (2 + 3 from C = {})", greeting, sum);
        } catch (UnsatisfiedLinkError | RuntimeException e) {
            LOGGER.error("Failed to load native library — see native/README.md for build steps", e);
        }
    }

}
