package org.polyfrost.chatting.core

import dev.deftu.clipboard.Clipboard
import dev.deftu.eventbus.SubscribeEvent
import dev.deftu.omnicore.api.client.events.ScreenEvent
import dev.deftu.omnicore.api.eventBus
import net.minecraft.client.GuiMessage
import org.polyfrost.chatting.hook.ChatLineHook
import org.polyfrost.chatting.mixin.chat.ChatAccessor
import org.polyfrost.oneconfig.api.ui.v1.Notifications
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import kotlin.math.floor

object McChat {

    @JvmField
    var hoveredIndex = -1

    private val editorMessages = mutableListOf(
        "§b§lChatting",
        "",
        "This is a movable chat",
        "§eDrag me around!"
    ).reversed().map { it.toChatLine() }

    private var editorLines = emptyList<GuiMessage.Line>()

    private var lastWidth = -1

    init {
        eventBus.register(this)
    }

    @SubscribeEvent
    fun onMouseReleased(event: ScreenEvent.MouseRelease.Post) {
        when (event.button.code) {
            1 -> {
                if (hoveredIndex == -1) return
                copyMessage(hoveredIndex)
            }
        }
    }

    fun copyMessage(index: Int) {
        val text = ((mc.gui.chat as ChatAccessor).trimmedMessages[index] as Any as ChatLineHook).`chatting$getFullMessage`()
        Clipboard.getInstance().string = text
        Notifications.enqueue(Notifications.Type.Success, "Chatting", "Successfully copied \"$text\" to clipboard.")
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