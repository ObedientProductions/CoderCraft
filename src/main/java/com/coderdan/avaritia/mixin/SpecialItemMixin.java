package com.coderdan.avaritia.mixin;

import com.coderdan.avaritia.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class SpecialItemMixin {

    private Player self()
    {
        return (Player) (Object) this;
    }

    /**
     * @author
     * @reason
     */
    @Inject(method = "touch", at = @At("HEAD"))
    private void touch(Entity pEntity, CallbackInfo cb) {
        self().displayClientMessage(Component.literal("touched!").withStyle(ChatFormatting.BOLD), true);
    }

}

