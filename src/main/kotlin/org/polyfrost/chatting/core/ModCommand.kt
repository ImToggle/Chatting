package org.polyfrost.chatting.core

import dev.deftu.omnicore.api.client.chat.OmniClientChat
import dev.deftu.omnicore.api.client.chat.OmniClientChatSender
import org.polyfrost.chatting.ChattingConstants
import org.polyfrost.oneconfig.api.commands.v1.factories.annotated.Command
import org.polyfrost.oneconfig.api.commands.v1.factories.annotated.Handler
import org.polyfrost.oneconfig.api.commands.v1.factories.annotated.Param
import org.polyfrost.oneconfig.utils.v1.dsl.openUI

@Command(ChattingConstants.MODID)
object ModCommand {

    @Handler
    private fun main() {
        ModConfig.openUI()
    }

    @Handler
    private fun message_player(@Param quantity: Int = 1) {
        for (i in 1..quantity) {
            OmniClientChatSender.send("Hello World $i")
        }
    }

    @Handler
    private fun message_system(@Param quantity: Int = 1) {
        for (i in 1..quantity) {
            OmniClientChat.displayChatMessage("Test Message $i")
        }
    }

    @Handler
    private fun message_multiline(@Param quantity: Int = 1) {
        val message = (1..quantity).joinToString(separator = "\n") { "MultiLine $it" }
        OmniClientChat.displayChatMessage(message)
    }

}
