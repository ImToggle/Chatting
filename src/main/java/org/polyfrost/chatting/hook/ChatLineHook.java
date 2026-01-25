package org.polyfrost.chatting.hook;

import net.minecraft.client.multiplayer.PlayerInfo;

public interface ChatLineHook {

    void chatting$setLeft(int left);

    int chatting$getLeft();

    void chatting$setRight(int right);

    int chatting$getRight();

    void chatting$setParent(int parent);

    int chatting$getParent();

    void chatting$setSender(PlayerInfo sender);

    PlayerInfo chatting$getSender();

    default boolean hasHead() {
        return chatting$getSender() != null;
    }

}