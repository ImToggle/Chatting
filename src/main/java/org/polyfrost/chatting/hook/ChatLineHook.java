package org.polyfrost.chatting.hook;

public interface ChatLineHook {

    void chatting$setLeft(int left);

    int chatting$getLeft();

    void chatting$setRight(int right);

    int chatting$getRight();

    void chatting$setParent(int parent);

    int chatting$getParent();

}