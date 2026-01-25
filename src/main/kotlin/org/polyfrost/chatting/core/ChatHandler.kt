package org.polyfrost.chatting.core

import dev.deftu.clipboard.Clipboard
import dev.deftu.eventbus.SubscribeEvent
import dev.deftu.omnicore.api.client.events.ScreenEvent
import dev.deftu.omnicore.api.client.input.OmniKeyboard
import dev.deftu.omnicore.api.client.input.OmniKeys
import dev.deftu.omnicore.api.eventBus
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.GuiMessage
import net.minecraft.client.gui.screens.ChatScreen
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import kotlin.math.floor

object ChatHandler {

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

    fun initialize() {
        eventBus.register(this)
        ScreenEvents.AFTER_INIT.register { _, screen, _, _ ->
            if (screen !is ChatScreen) return@register
            ScreenEvents.Remove { screen ->
                clearSelection()
            }
        }
    }

    @SubscribeEvent
    fun onKeyRelease(event: ScreenEvent.KeyRelease.Post) {
        if (event.screen !is ChatScreen) return
        if (hoveredIndex == -1) return
        when (event.key) {
            OmniKeys.KEY_DELETE -> removeMessage(selectedIndexes)
            OmniKeys.KEY_C -> if (event.modifiers.isCtrl) copyMessage(selectedIndexes)
        }
        if (event.key == OmniKeys.KEY_DELETE) {
            removeMessage(selectedIndexes)
        }
    }

    @SubscribeEvent
    fun onMouseRelease(event: ScreenEvent.MouseRelease.Post) {
        if (event.screen !is ChatScreen) return
        val isHovered = hoveredIndex != -1
        if (!isHovered ) {
            clearSelection()
            return
        }
        when (event.button.code) {
            0 -> {
                if (!OmniKeyboard.isCtrlKeyPressed) selectedIndexes.clear()
                when {
                    OmniKeyboard.isAltKeyPressed -> {
                        val hook = chatAccessor.trimmedMessages[hoveredIndex].asHook()
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
        return
    }

    fun clearSelection() {
        lastSelected = -1
        selectedIndexes.clear()
    }

    fun shiftSelection(amount: Int = 1) {
        selectedIndexes = selectedIndexes.mapTo(LinkedHashSet()) { it + amount }
        selectedIndexes.removeIf { it >= 100 }
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
        clearSelection()
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