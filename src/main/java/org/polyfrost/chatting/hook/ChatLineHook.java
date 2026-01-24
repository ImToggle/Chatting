package org.polyfrost.chatting.hook;

public interface ChatLineHook {

    void chatting$setFullMessage(String fullMessage);

    String chatting$getFullMessage();

}