package org.polyfrost.chatting.core

import dev.deftu.clipboard.Clipboard
import dev.deftu.eventbus.SubscribeEvent
import dev.deftu.omnicore.api.client.events.ScreenEvent
import dev.deftu.omnicore.api.client.input.OmniKeyboard
import dev.deftu.omnicore.api.eventBus
import net.minecraft.client.GuiMessage
import org.polyfrost.oneconfig.api.ui.v1.Notifications
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import kotlin.math.floor

object McChat {

    @JvmField
    var hoveredIndex = -1

    @JvmField
    var selectedIndexes: MutableSet<Int> = LinkedHashSet()

    var lastSelected = -1
        get() = if (field == -1) messagesLength - 1 else field

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

    @Suppress("unused")
    @SubscribeEvent
    fun onMouseReleased(event: ScreenEvent.MouseRelease.Post) {
        val isHovered = hoveredIndex != -1
        if (!isHovered ) {
            lastSelected = -1
            selectedIndexes.clear()
            return
        }
        when (event.button.code) {
            0 -> {
                if (!OmniKeyboard.isCtrlKeyPressed) selectedIndexes.clear()
                when {
                    OmniKeyboard.isAltKeyPressed -> {
                        val hook = getAllMessages()[hoveredIndex].asHook()
                        selectedIndexes.addAll(hoveredIndex + hook.`chatting$getLeft`()..hoveredIndex + hook.`chatting$getRight`())
                    }
                    OmniKeyboard.isShiftKeyPressed -> {
                        val start = minOf(lastSelected, hoveredIndex)
                        val end = maxOf(lastSelected, hoveredIndex)
                        selectedIndexes.addAll(start..end)
                    }
                    else -> selectedIndexes.add(hoveredIndex)
                }
                lastSelected = hoveredIndex
                selectedIndexes = selectedIndexes.sortedDescending().toMutableSet()
            }
            1 -> copyMessage(
                if (selectedIndexes.contains(hoveredIndex)) {
                    selectedIndexes
                } else {
                    selectedIndexes.clear()
                    mutableSetOf(hoveredIndex)
                }
            )
        }
    }

    fun clearSelection() {
        selectedIndexes.clear()
    }

    fun shiftSelection(amount: Int = 1) {
        selectedIndexes = selectedIndexes.mapTo(LinkedHashSet()) { it + amount }
        selectedIndexes.removeIf { it >= 100 }
    }

    fun copyMessage(selection: MutableSet<Int>) {
        val text = selection.joinToString("\n") { index ->
            getAllMessages()[index].content.asString()
        }
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