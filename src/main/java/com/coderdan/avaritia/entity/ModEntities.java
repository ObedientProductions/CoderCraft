package com.coderdan.avaritia.entity;

import com.coderdan.avaritia.Avaritia;
import com.coderdan.avaritia.entity.custom.GapingVoidEntity;
import com.coderdan.avaritia.entity.custom.InfinityArrowEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Avaritia.MOD_ID);



    public static final RegistryObject<EntityType<InfinityArrowEntity>> INFINITY_ARROW = ENTITIES.register("infinity_arrow",
            () -> EntityType.Builder.<InfinityArrowEntity>of(InfinityArrowEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f) // size of the arrow hitbox
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("infinity_arrow"));

    public static final RegistryObject<EntityType<GapingVoidEntity>> GAPING_VOID = ENTITIES.register("gaping_void",
            () -> EntityType.Builder.<GapingVoidEntity>of(GapingVoidEntity::new, MobCategory.MISC)
                    .sized(1f, 1f)
                    .clientTrackingRange(20)
                    .updateInterval(20)
                    .build("gaping_void"));


    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }

}
