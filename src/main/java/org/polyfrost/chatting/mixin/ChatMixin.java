package org.polyfrost.chatting.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.ChatComponent;
import org.polyfrost.chatting.core.ModConfig;
import org.polyfrost.chatting.core.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

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

    @ModifyArgs(method = "render",
            at = @At(
                    value = "INVOKE",
                    //#if MC >= 1.21.1
                    target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V",
                    //#elseif MC == 1.16.5
                    //$$ target = "Lnet/minecraft/client/gui/components/ChatComponent;fill(Lcom/mojang/blaze3d/vertex/PoseStack;IIIII)V",
                    //#else
                    //$$ target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V",
                    //#endif
                    ordinal = 0
            )
    )
    private void setBackgroundColor(Args args) {
        int index = Util.is11605() ? 5 : 4;
        int alpha = ((int) args.get(index) >>  24) & 0xFF;
        int color = ModConfig.INSTANCE.getChatBackgroundColor().getArgb() << alpha;
        args.set(index, color);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void postRender(CallbackInfo ci) {
    }
}
