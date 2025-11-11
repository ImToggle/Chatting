package org.polyfrost.chatting.mixin;

import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

//#if MC <= 1.16.5
//$$ import net.minecraft.text.Text;
//#endif

@Mixin(net.minecraft.client.gui.hud.ChatHud.class)
public abstract class ChatMixin {

    @Inject(
            //#if MC <= 1.12.2
            //$$ method = "drawChat",
            //#else
            method = "render",
            //#endif
            at = @At("HEAD"), cancellable = true
    )
    private void cancelRender(CallbackInfo ci) {
        ci.cancel();
    }

    @Shadow
    @Final
    private List<
            ChatHudLine
            //#if MC == 11605
            //$$ <Text>
            //#endif
            > messages;

}
