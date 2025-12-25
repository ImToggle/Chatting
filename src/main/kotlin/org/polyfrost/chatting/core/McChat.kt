package org.polyfrost.chatting.core

import dev.deftu.omnicore.api.client.options.OmniChatSettings
import kotlin.math.floor

object McChat {

    @JvmField
    var selectedIndex = -1

    private var editorMessages = mutableListOf(
        "§b§lChatting",
        "",
        "This is a movable chat",
        "§eDrag me around!"
    ).map { it.toChatLine() }

    private var editorLines = emptyList<McChatLine<*>>()

    private var lastWidth = -1

    fun getEditorLines(): List<McChatLine<*>> {
        val width = floor(getWidth().toDouble() / OmniChatSettings.chatScale).toInt()
        if (width != lastWidth) {
            lastWidth = width
            editorLines = editorMessages.flatMap {
                it.toLines(width)
            }
        }
        return editorLines
    }

}