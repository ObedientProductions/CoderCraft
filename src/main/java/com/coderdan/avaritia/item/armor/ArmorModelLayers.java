package com.coderdan.avaritia.item.armor;

import com.coderdan.avaritia.Avaritia;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class ArmorModelLayers {


    public static final ModelLayerLocation INFINITY_ARMOR = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(Avaritia.MOD_ID, "infinity_armor"), "main"
    );

    public static void register(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(INFINITY_ARMOR, ModInfinityArmorModel::createBodyLayer);

        // If you have more armor models, just add more:
        // event.registerLayerDefinition(OTHER_ARMOR, OtherArmorModel::createBodyLayer);
    }
}
