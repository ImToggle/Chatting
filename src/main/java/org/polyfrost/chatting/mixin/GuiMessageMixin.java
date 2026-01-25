package org.polyfrost.chatting.mixin;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.polyfrost.chatting.hook.GuiMessageHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GuiMessage.class)
public class GuiMessageMixin implements GuiMessageHook {

    @Unique private PlayerInfo sender;

    @Override
    public void chatting$setSender(PlayerInfo sender) {
        this.sender = sender;
    }

    @Override
    public PlayerInfo chatting$getSender() {
        return this.sender;
    }

}