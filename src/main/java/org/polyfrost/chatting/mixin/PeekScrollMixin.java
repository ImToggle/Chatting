package org.polyfrost.chatting.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import org.polyfrost.chatting.core.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.MouseHandler.class)
public class PeekScrollMixin {

    @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"), cancellable = true)
    private void chatPeekScrolling(long l, double d, double e, CallbackInfo ci, @Local(ordinal = 4) double amount) {
        if (Util.peeking) {
            Util.scrollChat(amount);
            ci.cancel();
        }
    }

}