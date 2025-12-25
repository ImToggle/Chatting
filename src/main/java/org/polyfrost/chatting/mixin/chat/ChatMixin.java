package org.polyfrost.chatting.mixin.chat;

import net.minecraft.client.gui.components.ChatComponent;
import org.polyfrost.chatting.core.McChat;
import org.polyfrost.chatting.core.ModConfig;
import org.polyfrost.chatting.core.RenderUtil;
import org.polyfrost.chatting.core.Util;
import org.polyfrost.oneconfig.api.hud.v1.HudManager;
import org.polyfrost.polyui.color.PolyColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

//#if MC <= 1.21.5
//$$ import com.llamalad7.mixinextras.injector.wrapoperation.*;
//$$ import com.llamalad7.mixinextras.sugar.Local;
//#endif

//#if MC <= 1.16.5
//$$ import net.minecraft.network.chat.Component;
//#endif

@Mixin(ChatComponent.class)
public abstract class ChatMixin {

    //Chat Appearance

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
        RenderUtil.currentIndex--;
        int index = Util.is11605() ? 5 : 4;
        System.out.println(RenderUtil.currentIndex + ", " + McChat.selectedIndex);
        PolyColor bgColor = RenderUtil.currentIndex == McChat.selectedIndex ? ModConfig.INSTANCE.getHoveredChatBackgroundColor() : ModConfig.INSTANCE.getChatBackgroundColor();
        int alpha = (int) (bgColor.alpha() * ((((int) args.get(index) >>  24) & 0xFF) / 127f));
        int color = (bgColor.getArgb() & 0x00FFFFFF) | (alpha << 24);
        args.set(index, color);
    }

    //Chat Message Fading

    //#if MC <= 1.21.5
    //$$ @ModifyConstant(method = "render", constant = @Constant(intValue = 200))
    //$$ private int setFadeTime(int value) {
    //$$     return (int) Math.ceil(20 * ModConfig.INSTANCE.getFadeTime());
    //$$ }
    //$$
        //#if MC > 1.16.5
        //$$ @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/GuiMessage$Line;addedTime()I"))
        //$$ private int toggleFade(net.minecraft.client.GuiMessage.Line instance,
        //#elseif MC == 1.16.5
        //$$ @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/GuiMessage;getAddedTime()I"))
        //$$ private int toggleFade(net.minecraft.client.GuiMessage<?> instance,
        //#else
        //$$ @WrapOperation(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ChatLine;getUpdatedCounter()I"))
        //$$ private int toggleFade(net.minecraft.client.gui.ChatLine instance,
        //#endif
    //$$     Operation<Integer> original, @Local(ordinal = 0, argsOnly = true) int ticks) {
    //$$     if (ModConfig.INSTANCE.getFade()) {
    //$$         return original.call(instance);
    //$$     } else {
    //$$         return ticks;
    //$$     }
    //$$ }
    //#endif

    //#if MC >= 1.16.5
    @Inject(method = "getTimeFactor", at = @At("HEAD"), cancellable = true)
    private static void toggleFade(CallbackInfoReturnable<Double> cir) {
        if (!ModConfig.INSTANCE.getFade()) cir.setReturnValue(1.0);
    }

    @ModifyConstant(method = "getTimeFactor", constant = @Constant(doubleValue = 200.0))
    private static double setFadeTime(double value) {
        return 20 * ModConfig.INSTANCE.getFadeTime();
    }
    //#else
    //$$
    //$$ @ModifyConstant(method = "drawChat", constant = @Constant(doubleValue = 200.0))
    //$$ private double setFadeTime(double value) {
    //$$     return 20 * ModConfig.INSTANCE.getFadeTime();
    //$$ }
    //#endif

    //Chat Peek

    //#if MC > 1.16.5
    @ModifyVariable(method = "render", at = @At(value = "HEAD", ordinal = 0), argsOnly = true)
    private boolean setPeek(boolean value) {
        return value || Util.peeking || HudManager.isEditing();
    }
    //#else
    //$$ @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;isChatFocused()Z"))
    //$$ private boolean setPeek(ChatComponent instance, Operation<Boolean> original) {
    //$$     return original.call(instance) || Util.peeking || HudManager.isEditing();
    //$$ }
    //#endif
}
