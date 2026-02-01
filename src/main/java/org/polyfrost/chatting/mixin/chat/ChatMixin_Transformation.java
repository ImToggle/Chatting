package org.polyfrost.chatting.mixin.chat;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import org.polyfrost.chatting.animation.AnimationUtil;
import org.polyfrost.chatting.core.ChatHandler;
import org.polyfrost.chatting.core.RenderUtil;
import org.polyfrost.chatting.core.Util;
import org.polyfrost.oneconfig.api.hud.v1.HudManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChatComponent.class)
public class ChatMixin_Transformation {

    @Shadow @Final private List<GuiMessage.Line> trimmedMessages;

    @Shadow private int chatScrollbarPos;

    @Inject(method = "render", at = @At("HEAD"))
    private void preRender(CallbackInfo ci, @Local(argsOnly = true) GuiGraphics guiGraphics) {
        RenderUtil.renderingChat = true;
        RenderUtil.recalculate(HudManager.isEditing() ? ChatHandler.INSTANCE.getEditorLines() : this.trimmedMessages, this.chatScrollbarPos);
        RenderUtil.push(guiGraphics);
        RenderUtil.translate(guiGraphics, RenderUtil.offsetX, RenderUtil.offsetY);
    }

    @ModifyVariable(method = "render", at = @At(value = "LOAD", ordinal = 1), ordinal = 0)
    private float modifyScale(float value) {
        return value * Util.getHudScale();
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void postRender(CallbackInfo ci, @Local(argsOnly = true) GuiGraphics guiGraphics) {
        RenderUtil.pop(guiGraphics);
        RenderUtil.renderingChat = false;
    }

    @Inject(method = "getLinesPerPage", at = @At("HEAD"), cancellable = true)
    private void linesPerPage(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(RenderUtil.maxLength);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;forEachLine(IIZILnet/minecraft/client/gui/components/ChatComponent$LineConsumer;)I", ordinal = 0))
    private void preBackground(GuiGraphics guiGraphics, int i, int j, int k, boolean bl, CallbackInfo ci) {
        RenderUtil.push(guiGraphics);
        RenderUtil.pushScissor(guiGraphics);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;forEachLine(IIZILnet/minecraft/client/gui/components/ChatComponent$LineConsumer;)I", ordinal = 1))
    private void preText(GuiGraphics guiGraphics, int i, int j, int k, boolean bl, CallbackInfo ci) {
        RenderUtil.popScissor(guiGraphics);
        RenderUtil.pop(guiGraphics);
        RenderUtil.push(guiGraphics);
        RenderUtil.pushScissor(guiGraphics);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;forEachLine(IIZILnet/minecraft/client/gui/components/ChatComponent$LineConsumer;)I", ordinal = 1, shift = At.Shift.AFTER))
    private void postText(GuiGraphics guiGraphics, int i, int j, int k, boolean bl, CallbackInfo ci) {
        RenderUtil.popScissor(guiGraphics);
        RenderUtil.pop(guiGraphics);
    }

}