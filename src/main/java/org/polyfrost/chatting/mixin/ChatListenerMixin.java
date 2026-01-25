package org.polyfrost.chatting.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.polyfrost.chatting.core.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;

@Mixin(ChatListener.class)
public class ChatListenerMixin {

    @Inject(method = "showMessageToPlayer", at = @At("HEAD"))
    public void captureGameProfile(ChatType.Bound bound, PlayerChatMessage playerChatMessage, Component component, GameProfile gameProfile, boolean bl, Instant instant, CallbackInfoReturnable<Boolean> cir) {
        Util.currentGameProfile = gameProfile;
    }

    @Inject(method = "showMessageToPlayer", at = @At("RETURN"))
    public void clearGameProfile(ChatType.Bound bound, PlayerChatMessage playerChatMessage, Component component, GameProfile gameProfile, boolean bl, Instant instant, CallbackInfoReturnable<Boolean> cir) {
        Util.currentGameProfile = null;
    }
}
