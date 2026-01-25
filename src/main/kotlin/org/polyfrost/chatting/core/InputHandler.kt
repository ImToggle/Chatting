package org.polyfrost.chatting.core

import dev.deftu.eventbus.SubscribeEvent
import dev.deftu.omnicore.api.client.events.ScreenEvent
import dev.deftu.omnicore.api.client.input.OmniKeyboard
import dev.deftu.omnicore.api.client.input.OmniKeys
import dev.deftu.omnicore.api.eventBus
import net.minecraft.client.gui.screens.ChatScreen
import org.polyfrost.chatting.core.ChatHandler.copyMessage
import org.polyfrost.chatting.core.ChatHandler.removeMessage

object InputHandler {

    @JvmField
    var hoveredIndex = -1

    @JvmField
    var selectedIndexes: MutableSet<Int> = LinkedHashSet()

    @JvmField
    var functionalKeys = arrayListOf(OmniKeys.KEY_C, OmniKeys.KEY_S, OmniKeys.KEY_A).map { it.code }

    var lastSelected = -1
        get() = if (field == -1) messagesLength - 1 else field

    fun initialize() {
        eventBus.register(this)
    }

    fun shouldCancel(key: Int): Boolean {
        if (selectedIndexes.isEmpty()) return false
        if (key == OmniKeys.KEY_DELETE.code) return true
        if (!OmniKeyboard.isCtrlKeyPressed) return false
        return functionalKeys.contains(key)
    }

    @SubscribeEvent
    fun onKeyRelease(event: ScreenEvent.KeyPress.Post) {
        if (event.screen !is ChatScreen) return
        if (event.key == OmniKeys.KEY_DELETE) {
            removeMessage(selectedIndexes)
        }
        if (event.modifiers.isCtrl) {
            when (event.key) {
                OmniKeys.KEY_A -> if (hoveredIndex != -1) selectedIndexes.addAll(0 until messagesLength)
                OmniKeys.KEY_C -> copyMessage(selectedIndexes)
                OmniKeys.KEY_S -> ScreenshotHandler.screenshot(selectedIndexes)
            }
        }
    }

    @SubscribeEvent
    fun onMousePress(event: ScreenEvent.MouseClick.Post) {
        if (event.screen !is ChatScreen) return
    }

    fun onMouseRelease(mouseX: Double, mouseY: Double, mouseButton: Int) {
        val isHovered = hoveredIndex != -1
        if (!isHovered ) {
            clearSelection()
            return
        }
        when (mouseButton) {
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

}