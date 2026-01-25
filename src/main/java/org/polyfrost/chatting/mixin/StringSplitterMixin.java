package org.polyfrost.chatting.mixin;

import net.minecraft.client.StringSplitter;
import org.polyfrost.chatting.core.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(StringSplitter.class)
public class StringSplitterMixin {

    @ModifyVariable(
            method = "splitLines(Lnet/minecraft/network/chat/FormattedText;ILnet/minecraft/network/chat/Style;Ljava/util/function/BiConsumer;)V",
            at = @At(value = "LOAD", ordinal = 0), ordinal = 0,
            argsOnly = true
    )
    private int modifyWidth(int value) {
        if (Util.shouldReduce) {
            Util.shouldReduce = false;
            return value - 10;
        }
        return value;
    }
}
