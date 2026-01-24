package org.polyfrost.chatting.hook;

public interface ChatLineHook {

    void chatting$setLeft(int chattingHashCode);

    int chatting$getLeft();

    void chatting$setRight(int chattingHashCode);

    int chatting$getRight();

}