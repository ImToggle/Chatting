package org.polyfrost.chatting.core

import dev.deftu.clipboard.Clipboard
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.GuiMessage
import net.minecraft.client.gui.screens.ChatScreen
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import kotlin.math.floor

object ChatHandler {

    private val editorMessages = mutableListOf(
        "§b§lChatting",
        "",
        "This is a movable chat",
        "§eDrag me around!"
    ).reversed().map { it.toChatLine() }

    private var editorLines = emptyList<GuiMessage.Line>()

    private var lastWidth = -1

    fun initialize() {
        ScreenEvents.AFTER_INIT.register { _, screen, _, _ ->
            if (screen !is ChatScreen) return@register
            ScreenEvents.remove(screen).register { _ ->
                InputHandler.clearSelection()
            }
        }
    }

    fun copyMessage(selection: MutableSet<Int>) {
        val text = selection.joinToString("\n") { index ->
            chatAccessor.trimmedMessages[index].content.asString()
        }
        Clipboard.getInstance().string = text
        /* notifications are broken */
//        Notifications.enqueue(Notifications.Type.Success, "Chatting", "Successfully copied \"$text\" to clipboard.")
    }

    fun removeMessage(selection: MutableSet<Int>) {
        val targets = selection.map {
            chatAccessor.trimmedMessages[it].asHook().`chatting$getParent`()
        }
        chatAccessor.allMessages.removeIf {
            targets.contains(it.hashCode())
        }
        chatAccessor.invokeRefreshTrimmedMessages()
        InputHandler.clearSelection()
    }

    fun getEditorLines(): List<GuiMessage.Line> {
        val width = floor(getWidth().toDouble() / mc.gui.chat.scale).toInt()
        if (width != lastWidth) {
            lastWidth = width
            editorLines = editorMessages.flatMap {
                it.toLines(width).reversed()
            }
        }
        return editorLines
    }

}