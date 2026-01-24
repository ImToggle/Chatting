package org.polyfrost.chatting.mixin.chat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.ChatComponent;
import org.polyfrost.chatting.core.RenderUtil;
import org.polyfrost.chatting.core.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatComponent.class)
public class ChatMixin_Input {

    @ModifyVariable(method = "screenToChatX", at = @At("HEAD"), argsOnly = true)
    private double translateMouseX(double value) {
        return (value - RenderUtil.offsetX) / Util.getHudScale();
    }

    @ModifyVariable(method = "screenToChatY", at = @At("HEAD"), argsOnly = true)
    private double translateMouseY(double value) {
        return value - RenderUtil.offsetY;
    }

    @Inject(method = "screenToChatY", at = @At("RETURN"), cancellable = true)
    private void scaleMouseY(double d, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(cir.getReturnValueD() / Util.getHudScale());
    }

    @ModifyVariable(method = "handleChatQueueClicked", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    private double translateMouseX1(double value) {
        return (value - RenderUtil.offsetX - 2) / Util.getHudScale() + 2;
    }

    @ModifyVariable(method = "handleChatQueueClicked", at = @At(value = "HEAD"), ordinal = 1, argsOnly = true)
    private double translateMouseY1(double value) {
        return value - RenderUtil.offsetY;
    }

    @ModifyConstant(method = "handleChatQueueClicked", constant = @Constant(doubleValue = -9.0))
    private double scaleMouseY1(double value) {
        return value * Util.getHudScale();
    }

    @WrapOperation(method = "handleChatQueueClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;floor(D)I", ordinal = 0))
    private int checkLeft(double d, Operation<Integer> original, @Local(ordinal = 2) double f) {
        if (f <= 0) {
            return (int) f - 2;
        } else {
            return original.call(d);
        }
    }

}