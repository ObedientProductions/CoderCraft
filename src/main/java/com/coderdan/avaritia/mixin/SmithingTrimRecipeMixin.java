package com.coderdan.avaritia.mixin;

import com.coderdan.avaritia.item.ModDataComponentTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingTrimRecipe.class)
public abstract class SmithingTrimRecipeMixin {

    @Inject(method = "assemble(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"), cancellable = true)
    private void injectTrimFlag(RecipeInput par1, HolderLookup.Provider par2, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();
        if (result.getItem() instanceof ArmorItem) {
            ArmorTrim trim = result.get(DataComponents.TRIM);
            if (trim != null && trim.pattern().isBound() && trim.material().isBound()) {
                String patternId = trim.pattern().getRegisteredName();
                String materialId = trim.material().get().ingredient().getRegisteredName();

                if (patternId.equals("avaritia:heavens_mark") && materialId.equals("avaritia:infinity_ingot")) {
                    result.set(ModDataComponentTypes.IS_INFINITY_TRIMMED.get(), 1.0f);
                    cir.setReturnValue(result); // update the result
                }
            }
        }
    }
}

