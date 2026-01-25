package org.polyfrost.chatting.hook;

import com.mojang.authlib.GameProfile;

public interface GuiMessageHook {

    void chatting$setSender(GameProfile sender);

    GameProfile chatting$getSender();

}