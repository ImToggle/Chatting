package org.polyfrost.chatting.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import org.polyfrost.chatting.core.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 1.16.5
@Mixin(net.minecraft.client.MouseHandler.class)
//#else
//$$ import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//$$ import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//$$ @Mixin(value = net.minecraft.client.Minecraft.class, remap = false)
//#endif
public class PeekScrollMixin {

    //#if MC >= 1.16.5
    @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"), cancellable = true)
    private void chatPeekScrolling(long l, double d, double e, CallbackInfo ci,
        //#if MC > 1.16.5
        @Local(ordinal = 4)
        //#else
        //$$ @Local(ordinal = 2)
        //#endif
        double amount
    ) {
        if (Util.peeking) {
            Util.scrollChat(amount);
            ci.cancel();
        }
    }
    //#else
    //$$ @WrapOperation(
             //#if MC == 1.8.9
             //$$ method = "runTick",
             //#else
             //$$ method = "runTickMouse",
             //#endif
    //$$ at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I"))
    //$$ private int chatPeekScrolling(Operation<Integer> original) {
    //$$     int value = original.call();
    //$$     if (Util.peeking) {
    //$$         Util.scrollChat(value);
    //$$         return 0;
    //$$     }
    //$$     return value;
    //$$ }
    //#endif

}