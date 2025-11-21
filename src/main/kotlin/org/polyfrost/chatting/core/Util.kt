@file:JvmName("Util")

package org.polyfrost.chatting.core

import dev.deftu.omnicore.api.client.chatHud
import dev.deftu.omnicore.api.client.input.OmniKeyboard
import dev.deftu.omnicore.api.client.render.OmniResolution
import dev.deftu.omnicore.api.client.screen.currentScreen
import dev.deftu.textile.TextStyle
import net.minecraft.client.GuiMessage
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.network.chat.Component
import org.polyfrost.chatting.hud.MainChatHud
import kotlin.math.min

val mcScale
    get() = OmniResolution.scaleFactor.toFloat()

val editorMessages = mutableListOf(
    "§b§lChatting",
    "",
    "This is a movable chat",
    "§eDrag me around!"
)

@JvmField
var mainChatHud: MainChatHud? = null

@JvmField
var peeking = false

val chatFocused
    get() = currentScreen is ChatScreen

fun scrollChat(value: Double) {
    var amount = clamp(value, -1.0, 1.0)
    if (!OmniKeyboard.isShiftKeyPressed) {
        amount *= 7
    }
    chatHud?.scrollChat(
        //#if MC == 1.16.5
        //$$ amount
        //#else
        amount.toInt()
        //#endif
    )
}

fun clamp(value: Double, min: Double, max: Double): Double {
    return if (value < min) min else min(value, max)
}

fun String.toChatLine(): McChatMessage {
    return GuiMessage(
        -1,
        //#if MC >= 1.16.5
        Component.literal(this),
        //#else
        //$$ net.minecraft.util.text.TextComponentString(this),
        //#endif
        //#if MC > 1.16.5
        null,
        null
        //#else
        //$$ -1
        //#endif
    )
}

fun is11605() : Boolean {
    //#if MC == 1.16.5
    //$$ return true
    //#else
    return false
    //#endif
}

fun isModern(): Boolean {
    //#if MC >= 1.16.5
    return true
    //#else
    //$$ return false
    //#endif
}

typealias McChatMessage =
    GuiMessage
    //#if MC == 1.16.5
    //$$ <net.minecraft.network.chat.Component>
    //#endif

typealias McChatLine =
    //#if MC >= 1.21.1
    GuiMessage.Line
    //#else
    //$$ McChatMessage
    //#endif

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