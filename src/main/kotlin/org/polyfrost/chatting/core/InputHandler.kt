package org.polyfrost.chatting.core

import dev.deftu.eventbus.SubscribeEvent
import dev.deftu.omnicore.api.client.chat.OmniClientChat
import dev.deftu.omnicore.api.client.events.ScreenEvent
import dev.deftu.omnicore.api.client.input.OmniKeyboard
import dev.deftu.omnicore.api.client.input.OmniKeys
import dev.deftu.omnicore.api.client.input.OmniMouse
import dev.deftu.omnicore.api.client.input.OmniMouseButtons
import dev.deftu.omnicore.api.client.render.ImmediateScreenRenderer
import dev.deftu.omnicore.api.color.OmniColor
import dev.deftu.omnicore.api.eventBus
import net.minecraft.client.gui.screens.ChatScreen
import org.polyfrost.chatting.core.ChatHandler.copyMessage
import org.polyfrost.chatting.core.ChatHandler.removeMessage
import org.polyfrost.oneconfig.api.event.v1.events.MouseInputEvent
import org.polyfrost.oneconfig.api.event.v1.invoke.EventHandler
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindManager
import org.polyfrost.polyui.input.KeybindHelper
import org.polyfrost.polyui.input.Keys
import org.polyfrost.polyui.unit.Vec2
import org.polyfrost.polyui.unit.by

object InputHandler {

    @JvmField
    var highLightedIndexes = -1..-1

    @JvmField
    var selectedIndexes: MutableSet<Int> = LinkedHashSet()

    @JvmField
    var functionalKeys = arrayListOf(OmniKeys.KEY_C, OmniKeys.KEY_S, OmniKeys.KEY_A).map { it.code }

    var hoveredIndex = -1
        set(value) {
            field = value
            inBound = value != -1
            updateHighLight(OmniKeyboard.isAltKeyPressed)
        }

    private var lastSelected = -1

    private var canDrag = false

    private var dragging = false

    private var startPos = Vec2.ZERO

    private var currentPos = Vec2.ZERO

    private var dragStartIndex = -1

    private var lastDragIndex = -1

    private var inBound = false

    private var lastOverlap = false

    private val mouseMoveHandler = object : EventHandler<MouseInputEvent.Moved>() {
        override fun handle(event: MouseInputEvent.Moved): Boolean {
            onMouseMove()
            return false
        }

        override fun getEventClass() = MouseInputEvent.Moved::class.java
    }

    fun initialize() {
        eventBus.register(this)
        // for debug purpose
        KeybindManager.registerKeybind(
            KeybindHelper.builder().keys(Keys.F9).does { down ->
                if (!down) return@does
                OmniClientChat.displayChatMessage("New Message")
            }.build()
        )
        KeybindManager.registerKeybind(
            KeybindHelper.builder().keys(Keys.F10).does { down ->
                if (!down) return@does
                OmniClientChat.displayChatMessage("New Message 1")
                OmniClientChat.displayChatMessage("New Message 2")
            }.build()
        )
    }

    fun updateChatState() {
        if (inChat) {
            mouseMoveHandler.register()
            onMouseMove()
        } else {
            mouseMoveHandler.unregister()
            clearSelection()
        }
    }

    fun shouldCancel(key: Int): Boolean {
        if (!inBound) return false
        if (key == OmniKeys.KEY_DELETE.code) return true
        if (!OmniKeyboard.isCtrlKeyPressed) return false
        return functionalKeys.contains(key)
    }

    @SubscribeEvent
    fun onKeyPress(event: ScreenEvent.KeyPress.Post) {
        if (event.screen !is ChatScreen) return
        when (event.key) {
            OmniKeys.KEY_DELETE -> removeMessage(selectedIndexes)
            OmniKeys.KEY_LEFT_ALT, OmniKeys.KEY_RIGHT_ALT -> updateHighLight(true)
        }
        if (event.modifiers.isCtrl) {
            when (event.key) {
                OmniKeys.KEY_A -> if (inBound) {
                    selectedIndexes.addAll(0 until messagesLength)
                    sortSelection()
                }
                OmniKeys.KEY_C -> copyMessage(selectedIndexes)
                OmniKeys.KEY_S -> ScreenshotHandler.screenshot(selectedIndexes)
            }
        }
    }

    @SubscribeEvent
    fun onKeyRelease(event: ScreenEvent.KeyRelease.Post) {
        if (event.screen !is ChatScreen) return
        when (event.key) {
            OmniKeys.KEY_LEFT_ALT, OmniKeys.KEY_RIGHT_ALT -> updateHighLight(false)
        }
    }

    fun onMousePress(mouseButton: Int) {
        startPos = currentPos
        dragStartIndex = getSelectedIndex(ignoreX = true, ignoreY = true)
        lastDragIndex = dragStartIndex
        canDrag = mouseButton == 0
        if (!inBound) return
        when (mouseButton) {
            0 -> {
                if (!OmniKeyboard.isCtrlKeyPressed) selectedIndexes.clear()
                when {
                    OmniKeyboard.isAltKeyPressed -> {
                        val hook = chatAccessor.trimmedMessages[hoveredIndex].asHook()
                        selectedIndexes.addAll(hoveredIndex + hook.`chatting$getLeft`()..hoveredIndex + hook.`chatting$getRight`())
                    }
                    OmniKeyboard.isShiftKeyPressed -> {
                        val lastIndex = if (lastSelected == -1) messagesLength - 1 else lastSelected
                        val start = minOf(lastIndex, hoveredIndex)
                        val end = maxOf(lastIndex, hoveredIndex)
                        selectedIndexes.addAll(start..end)
                    }
                    else -> selectedIndexes.add(hoveredIndex)
                }
                lastSelected = hoveredIndex
                sortSelection()
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

    private fun onMouseMove() {
        hoveredIndex = getSelectedIndex()
        currentPos = OmniMouse.scaledX by OmniMouse.scaledY
        if (!dragging && canDrag) {
            dragging = true
            if (!OmniKeyboard.isCtrlKeyPressed && !OmniKeyboard.isShiftKeyPressed) selectedIndexes.clear()
        }
        updateDragState()
    }

    @SubscribeEvent
    fun onMouseRelease(event: ScreenEvent.MouseRelease.Post) {
        if (event.screen !is ChatScreen) return
        if (event.button != OmniMouseButtons.LEFT) return
        if (dragging) {
            dragging = false
            lastOverlap = false
        } else {
            if (!inBound) selectedIndexes.clear()
        }
        canDrag = false
    }

    fun onScroll(amount: Int, visual: Boolean) {
        if (visual) {
            if (!dragging) return
            startPos = startPos.x by startPos.y - amount * lineHeight * chatScale * hudScale
        } else {
            hoveredIndex = getSelectedIndex()
        }
        updateDragState()
    }

    private fun updateHighLight(state: Boolean) {
        highLightedIndexes = if (inBound && state) {
            val hook = chatAccessor.trimmedMessages[hoveredIndex].asHook()
            hoveredIndex + hook.`chatting$getLeft`()..hoveredIndex + hook.`chatting$getRight`()
        } else {
            hoveredIndex..hoveredIndex
        }
    }

    fun updateDragState() {
        if (!dragging) return
        val intersect = intersect(startPos, currentPos)
        if (lastOverlap != intersect) {
            lastOverlap = intersect
            if (lastOverlap) {
                selectedIndexes.add(dragStartIndex)
            } else {
                for (i in minOf(dragStartIndex, lastDragIndex)..maxOf(dragStartIndex, lastDragIndex)) {
                    updateSelection(i, false)
                }
                lastDragIndex = dragStartIndex
            }
            sortSelection()
        }
        if (!intersect) return
        val dragIndex = getSelectedIndex(ignoreX = true, ignoreY = true)
        if (dragIndex == -1) return
        if (dragIndex != lastDragIndex) {
            lastSelected = dragIndex
            val rangeStart = minOf(lastDragIndex, dragIndex)
            val rangeEnd = maxOf(lastDragIndex, dragIndex)
            val direction = dragIndex > lastDragIndex // true: up, false: down
            for (i in rangeStart..rangeEnd) {
                if (i == dragStartIndex) continue
                val isExpanding = direction == i > dragStartIndex
                if (isExpanding && i == lastDragIndex) continue
                if (!isExpanding && i == dragIndex) continue
                updateSelection(i, isExpanding)
            }
            sortSelection()
            lastDragIndex = dragIndex
        }
    }

    private val dragColor = OmniColor(0, 127, 255, 64)

    @SubscribeEvent
    fun onScreenRender(event: ScreenEvent.Render.Post) {
        if (event.screen !is ChatScreen) return
        if (!dragging) return
        val ctx = event.context
        ImmediateScreenRenderer.render(ctx) {
            ctx.pose.push()
            val extraHeight = lineHeight * chatScale * hudScale
            ctx.withScissor(0, chatY - (extraHeight / 2f).toInt(), event.screen.width, chatEndY - chatY + extraHeight.toInt()) {
                ctx.renderQuad(startPos, currentPos, dragColor)
            }
            ctx.pose.pop()
        }
    }

    private fun sortSelection() {
        selectedIndexes = selectedIndexes.sortedDescending().toMutableSet()
    }

    private fun updateSelection(index: Int, select: Boolean) {
        val state = if (OmniKeyboard.isCtrlKeyPressed) !selectedIndexes.contains(index) else select
        if (state) selectedIndexes.add(index) else selectedIndexes.remove(index)
    }

    fun clearSelection() {
        lastSelected = -1
        selectedIndexes.clear()
        hoveredIndex = getSelectedIndex()
    }

    fun shiftSelection(amount: Int) {
        if (lastSelected != -1) lastSelected += amount
        if (dragging) {
            lastDragIndex += amount
            dragStartIndex += amount
        }
        selectedIndexes = selectedIndexes.mapTo(LinkedHashSet()) { it + amount }
        selectedIndexes.removeIf { it >= 100 }
    }

    private fun intersect(start: Vec2, end: Vec2): Boolean {
        if (maxOf(start.x, end.x) < chatX || minOf(start.x, end.x) > chatEndX) return false
        if (maxOf(start.y, end.y) < chatY || minOf(start.y, end.y) > chatEndY) return false
        return true
    }

}