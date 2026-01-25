package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.screens.ChatScreen;
import org.polyfrost.chatting.core.InputHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Inject(method = "mouseClicked", at = @At("RETURN"))
    //#if MC >= 1.21.10
    private void onMouseClick(net.minecraft.client.input.MouseButtonEvent mouseButtonEvent, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            InputHandler.INSTANCE.onMouseRelease(mouseButtonEvent.x(), mouseButtonEvent.y(), mouseButtonEvent.button());
    //#else
    //$$ private void onMouseClick(double x, double y, int button, CallbackInfoReturnable<Boolean> cir) {
    //$$     if (!cir.getReturnValue()) {
    //$$         InputHandler.INSTANCE.onMouseRelease(x, y, button);
        }
    }
    //#endif
}