package com.coderdan.avaritia.datagen;

import com.coderdan.avaritia.Avaritia;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Avaritia.MOD_ID, exFileHelper);
    }


    @Override
    protected void registerStatesAndModels() {

    }
}
