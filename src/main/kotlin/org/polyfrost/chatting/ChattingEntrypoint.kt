package org.polyfrost.chatting

import org.polyfrost.chatting.core.ChattingClient
import net.fabricmc.api.ClientModInitializer

class ChattingEntrypoint : ClientModInitializer {

    override fun onInitializeClient() {
        ChattingClient.initialize()
    }

}
