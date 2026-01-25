package org.polyfrost.chatting.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.GuiMessage;
import org.polyfrost.chatting.hook.GuiMessageHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GuiMessage.class)
public class GuiMessageMixin implements GuiMessageHook {

    @Unique private GameProfile sender;

    @Override
    public void chatting$setSender(GameProfile sender) {
        this.sender = sender;
    }

    @Override
    public GameProfile chatting$getSender() {
        return this.sender;
    }

}