package org.polyfrost.chatting.core

import org.polyfrost.chatting.hud.MainChatHud
import org.polyfrost.oneconfig.api.commands.v1.CommandManager
import org.polyfrost.oneconfig.api.hud.v1.HudManager

object ChattingClient {

    fun initialize() {
        ModConfig.preload()
        CommandManager.register(ModCommand)
        HudManager.register(MainChatHud())
        ChatHandler.initialize()
    }

}