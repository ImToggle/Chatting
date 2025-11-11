package org.polyfrost.chatting.core

import org.polyfrost.oneconfig.api.commands.v1.CommandManager

object ChattingClient {

    fun initialize() {
        ModConfig.preload()
        CommandManager.register(ModCommand)
    }

}