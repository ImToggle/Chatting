package org.polyfrost.chatting.mixin;

import net.minecraft.client.GuiMessage;
import org.polyfrost.chatting.hook.ChatLineHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GuiMessage.Line.class)
public class ChatLineMixin implements ChatLineHook {

    @Unique String fullMessage = "";

    @Override
    public void chatting$setFullMessage(String fullMessage) {
        this.fullMessage =  fullMessage;
    }

    @Override
    public String chatting$getFullMessage() {
        return this.fullMessage;
    }
}