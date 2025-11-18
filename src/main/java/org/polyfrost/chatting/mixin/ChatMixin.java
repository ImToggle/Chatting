package org.polyfrost.chatting.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC <= 1.16.5
//$$ import net.minecraft.network.chat.Component;
//#endif

@Mixin(ChatComponent.class)
public abstract class ChatMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void preRender(
            CallbackInfo ci
            //#if MC >= 1.21.1
            , @Local(argsOnly = true)
            net.minecraft.client.gui.GuiGraphics obj
            //#elseif MC >= 1.16.5
            //$$ , @Local(argsOnly = true)
            //$$ com.mojang.blaze3d.vertex.PoseStack obj
            //#endif
    ) {
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void postRender(CallbackInfo ci) {
    }
}
