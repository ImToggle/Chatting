package org.polyfrost.chatting.mixin.chat;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import org.polyfrost.chatting.core.McChat;
import org.polyfrost.chatting.core.RenderUtil;
import org.polyfrost.oneconfig.api.hud.v1.HudManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(ChatComponent.class)
public class ChatMixin_Transformation {

    @Shadow @Final private List<
            //#if MC >= 1.21.1
            GuiMessage.Line
            //#else
            //$$ GuiMessage
                //#if MC == 1.16.5
                //$$ <?>
                //#endif
            //#endif
        > trimmedMessages;

    @Shadow private int chatScrollbarPos;

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
        RenderUtil.recalculate(HudManager.isEditing() ? McChat.INSTANCE.getEditorLines() : this.trimmedMessages, this.chatScrollbarPos);
        RenderUtil.push();
        RenderUtil.translate(RenderUtil.offsetX, RenderUtil.offsetY);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void postRender(CallbackInfo ci) {
        RenderUtil.pop();
    }

    //#if MC >= 1.21.1
    @ModifyVariable(method = "screenToChatX", at = @At("HEAD"), argsOnly = true)
    private double translateMouseX(double value) {
        return value - RenderUtil.offsetX;
    }

    @ModifyVariable(method = "screenToChatY", at = @At("HEAD"), argsOnly = true)
    private double translateMouseY(double value) {
        return value - RenderUtil.offsetY;
    }
    //#elseif MC == 1.16.5
    //$$ @ModifyVariable(method = "handleChatQueueClicked", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    //$$ private double translateMouseX(double value) {
    //$$     return value - RenderUtil.offsetX;
    //$$ }
    //$$
    //$$ @ModifyVariable(method = "handleChatQueueClicked", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    //$$ private double translateMouseY(double value) {
    //$$     return value - RenderUtil.offsetY;
    //$$ }
    //$$
    //$$ @ModifyVariable(method = "getClickedComponentStyleAt", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    //$$ private double translateMouseX1(double value) {
    //$$     return value - RenderUtil.offsetX;
    //$$ }
    //$$
    //$$ @ModifyVariable(method = "getClickedComponentStyleAt", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    //$$ private double translateMouseY1(double value) {
    //$$     return value - RenderUtil.offsetY;
    //$$ }
    //#else
    //$$ @ModifyVariable(method = "getChatComponent", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    //$$ private int translateMouseX(int value) {
    //$$     return value - RenderUtil.offsetX;
    //$$ }
    //$$
    //$$ @ModifyVariable(method = "getChatComponent", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    //$$ private int translateMouseY(int value) {
    //$$     return value - RenderUtil.offsetY;
    //$$ }
    //#endif

    //#if MC <= 1.16.5
        //#if MC <= 1.12.2
        //$$ @ModifyVariable(method = "getChatComponent", at = @At(value = "STORE"), ordinal = 6)
        //#else
        //$$ @ModifyVariable(method = "getClickedComponentStyleAt", at = @At(value = "STORE"), ordinal = 1)
        //#endif
    //$$ private int selectedIndex(int value) {
    //$$     if (org.polyfrost.chatting.core.Util.gettingIndex) {
    //$$         if (value >= 0 && value < this.trimmedMessages.size()) {
    //$$             org.polyfrost.chatting.core.McChat.selectedIndex = value;
    //$$         } else {
    //$$             org.polyfrost.chatting.core.McChat.selectedIndex = -1;
    //$$         }
    //$$     }
    //$$     return value;
    //$$ }
    //#endif

}