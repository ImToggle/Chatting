package org.polyfrost.chatting.mixin;

import net.minecraft.client.GuiMessage;
import org.polyfrost.chatting.hook.ChatLineHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GuiMessage.Line.class)
public class ChatLineMixin implements ChatLineHook {

    @Unique int left = 0;

    @Unique int right = 0;

    @Unique int parent = 0;

    @Override
    public void chatting$setLeft(int left) {
        this.left = left;
    }

    @Override
    public int chatting$getLeft() {
        return this.left;
    }

    @Override
    public void chatting$setRight(int right) {
        this.right = right;
    }

    @Override
    public int chatting$getRight() {
        return this.right;
    }

    @Override
    public void chatting$setParent(int parent) {
        this.parent = parent;
    }

    @Override
    public int chatting$getParent() {
        return this.parent;
    }
}