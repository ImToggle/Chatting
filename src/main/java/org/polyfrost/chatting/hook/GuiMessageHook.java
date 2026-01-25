package org.polyfrost.chatting.hook;

import net.minecraft.client.multiplayer.PlayerInfo;

public interface GuiMessageHook {

    void chatting$setSender(PlayerInfo sender);

    PlayerInfo chatting$getSender();

}