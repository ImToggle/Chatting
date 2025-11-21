package org.polyfrost.chatting.mixin.chat;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.ChatComponent;
import org.polyfrost.chatting.core.RenderUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatMixin_Transformation {

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
        //#if MC >= 1.16.5
        RenderUtil.renderingObj = obj;
        //#endif
        RenderUtil.push();
        RenderUtil.translate(RenderUtil.xOffset, RenderUtil.yOffset);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void postRender(CallbackInfo ci) {
        RenderUtil.pop();
    }

    //#if MC >= 1.21.1
    @ModifyVariable(method = "screenToChatX", at = @At("HEAD"), argsOnly = true)
    private double translateMouseX(double d) {
        return d - RenderUtil.xOffset;
    }

    @ModifyVariable(method = "screenToChatY", at = @At("HEAD"), argsOnly = true)
    private double translateMouseY(double d) {
        return d - RenderUtil.yOffset;
    }
    //#elseif MC == 1.16.5
    //$$ @ModifyVariable(method = "handleChatQueueClicked", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    //$$ private double translateMouseX(double value) {
    //$$     return value - RenderUtil.xOffset;
    //$$ }
    //$$
    //$$ @ModifyVariable(method = "handleChatQueueClicked", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    //$$ private double translateMouseY(double value) {
    //$$     return value - RenderUtil.yOffset;
    //$$ }
    //$$
    //$$ @ModifyVariable(method = "getClickedComponentStyleAt", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    //$$ private double translateMouseX1(double value) {
    //$$     return value - RenderUtil.xOffset;
    //$$ }
    //$$
    //$$ @ModifyVariable(method = "getClickedComponentStyleAt", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    //$$ private double translateMouseY1(double value) {
    //$$     return value - RenderUtil.yOffset;
    //$$ }
    //#else
    //$$ @ModifyVariable(method = "getChatComponent", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    //$$ private int translateMouseX(int value) {
    //$$     return value - (int) RenderUtil.xOffset;
    //$$ }
    //$$
    //$$ @ModifyVariable(method = "getChatComponent", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    //$$ private int translateMouseY(int value) {
    //$$     return value - (int) RenderUtil.yOffset;
    //$$ }
    //#endif

}