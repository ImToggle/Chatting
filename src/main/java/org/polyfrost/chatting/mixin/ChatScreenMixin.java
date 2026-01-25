package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.screens.ChatScreen;
import org.polyfrost.chatting.core.InputHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    //#if MC >= 1.21.10
    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z"))
    private void onMouseClick(net.minecraft.client.input.MouseButtonEvent mouseButtonEvent, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        InputHandler.INSTANCE.onMouseRelease(mouseButtonEvent.x(), mouseButtonEvent.y(), mouseButtonEvent.button());
    //#else
    //$$ @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;mouseClicked(DDI)Z"))
    //$$ private void onMouseClick(double x, double y, int button, CallbackInfoReturnable<Boolean> cir) {
    //$$     InputHandler.INSTANCE.onMouseRelease(x, y, button);
    }
    //#endif

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    //#if MC >= 1.21.10
    private void onKeyPressed(net.minecraft.client.input.KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        if (InputHandler.INSTANCE.shouldCancel(keyEvent.key())) cir.setReturnValue(false);
    //#else
    //$$ private void onKeyPressed(int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {
    //$$     if (InputHandler.INSTANCE.shouldCancel(i)) cir.setReturnValue(false);
    //#endif
    }

}