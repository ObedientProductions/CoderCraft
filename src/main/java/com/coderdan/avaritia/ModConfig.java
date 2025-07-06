package com.coderdan.avaritia;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec CONFIG;

    public static ForgeConfigSpec.DoubleValue singularityDifficulty;
    public static ForgeConfigSpec.BooleanValue ProcessingSpeed;
    public static ForgeConfigSpec.IntValue trimSpawnChance;
    public static int TRIM_SPAWN_CHANCE = 750; // fallback default


    static {

        singularityDifficulty = BUILDER
                .comment("Difficulty multiplier for singularity cost. 1.0 = default")
                .defineInRange("singularityDifficulty", 1.0, 0.1, 100.0);

        ProcessingSpeed = BUILDER
                .comment("If true, the Compressor processes items over time based on the recipe's required speed.",
                        "If false, it ignores the recipe's processing speed and completes instantly.")
                .define("CompressorHasProcessingSpeed", true);

        ModConfig.trimSpawnChance = BUILDER
                .comment("1 = guaranteed, 750 = 1 in 750 chance")
                .defineInRange("trimSpawnChance", 750, 1, Integer.MAX_VALUE);


        CONFIG = BUILDER.build();
    }

    public static void bakeConfig() {
        TRIM_SPAWN_CHANCE = trimSpawnChance.get();
    }

}
