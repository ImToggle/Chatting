package org.polyfrost.chatting.mixin.chat;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import org.polyfrost.chatting.core.Util;
import org.polyfrost.chatting.hook.GuiMessageHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatComponent.class)
public class ChatMixin_ChatHead {

    @ModifyVariable(
            method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V",
            at = @At("STORE"), ordinal = 0
    )
    private GuiMessage injectProfile(GuiMessage value) {
        ((GuiMessageHook) (Object) value).chatting$setSender(Util.currentGameProfile);
        return value;
    }

}
