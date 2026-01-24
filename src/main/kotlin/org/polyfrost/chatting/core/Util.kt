@file:JvmName("Util")

package org.polyfrost.chatting.core

import dev.deftu.omnicore.api.client.chatHud
import dev.deftu.omnicore.api.client.input.OmniKeyboard
import dev.deftu.omnicore.api.client.input.OmniMouse
import dev.deftu.omnicore.api.client.options.OmniChatSettings
import dev.deftu.omnicore.api.client.render.OmniResolution
import dev.deftu.omnicore.api.client.screen.currentScreen
import dev.deftu.textile.TextStyle
import net.minecraft.client.GuiMessage
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.network.chat.Component
import org.polyfrost.chatting.hud.MainChatHud
import org.polyfrost.chatting.mixin.ChatAccessor
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import kotlin.math.min

val mcScale
    get() = OmniResolution.scaleFactor.toFloat()

val hudScale
    get() = mainChatHud?.get()?.scaleX ?: 1f

val chatScale
    get() = OmniChatSettings.chatScale

@JvmField
var mainChatHud: MainChatHud? = null

@JvmField
var peeking = false

@JvmField
var gettingIndex = false

val chatFocused
    get() = currentScreen is ChatScreen || peeking || HudManager.isEditing

fun scrollChat(value: Double) {
    var amount = clamp(value, -1.0, 1.0)
    if (!OmniKeyboard.isShiftKeyPressed) {
        amount *= 7
    }
    chatHud?.scrollChat(amount.toInt())
}

fun getSelectedIndex(x: Double = OmniMouse.scaledX, y: Double = OmniMouse.scaledY) {
    gettingIndex = true
    val accessor = mc.gui.chat as ChatAccessor
    McChat.selectedIndex = accessor.getIndexAt(accessor.getChatX(x), accessor.getChatY(y))
    gettingIndex = false
}

fun clamp(value: Double, min: Double, max: Double): Double {
    return if (value < min) min else min(value, max)
}

fun String.toChatLine(): GuiMessage {
    return GuiMessage(-1, Component.literal(this), null, null)
}

fun GuiMessage.toLines(width: Int): List<GuiMessage.Line> {
    var width = width
    this.tag?.icon?.let { icon ->
        width -= icon.width + 4 + 2
    }
    val list = net.minecraft.client.gui.components.ComponentRenderUtils.wrapComponents(this.content(), width, mc.font)
    return list.map { it ->
        GuiMessage.Line(this.addedTime, it, this.tag, it == list.last())
    }
}

fun <T> visitNode(
    text: dev.deftu.textile.Text,
    inheritedStyle: TextStyle = TextStyle.EMPTY,
    visitor: (node: dev.deftu.textile.Text, content: String, style: TextStyle) -> T?
): T? {
    val style = text.style.inherited(inheritedStyle)
    val hit = text.content.visit({ content, innerStyle ->
        if (content.isEmpty()) {
            return@visit null
        }

        visitor(text, content, style)
    }, style)

    if (hit != null) {
        return hit
    }

    for (sibling in text.siblings) {
        val siblingHit = visitNode(sibling, style, visitor)
        if (siblingHit != null) {
            return siblingHit
        }
    }

    return null
}