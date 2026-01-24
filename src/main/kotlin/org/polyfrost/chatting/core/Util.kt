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

val chatScale
    get() = OmniChatSettings.chatScale * (mainChatHud?.get()?.scaleX ?: 1f)

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

@JvmField
var gettingIndex = false

val chatFocused
    get() = currentScreen is ChatScreen || peeking || HudManager.isEditing || !ModConfig.fade

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

fun getSelectedIndex(x: Double = OmniMouse.scaledX, y: Double = OmniMouse.scaledY) {
    gettingIndex = true
    val accessor = mc.gui.chat as ChatAccessor
    //#if MC >= 1.21.1
    McChat.selectedIndex = accessor.getIndexAt(accessor.getChatX(x), accessor.getChatY(y))
    //#elseif MC == 1.16.5
    //$$ accessor.getStyleAt(x, y)
    //#else
    //$$ accessor.getComponentAt(x.toInt(), y.toInt())
    //#endif
    gettingIndex = false
}

fun clamp(value: Double, min: Double, max: Double): Double {
    return if (value < min) min else min(value, max)
}

fun String.toChatLine(): McChatMessage<Component> {
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

fun McChatMessage<Component>.toLines(width: Int): List<McChatLine<*>> {
    var width = width
    //#if MC >= 1.21.1
    this.tag?.icon?.let { icon ->
        width -= icon.width + 4 + 2
    }
    //#endif
    //#if MC >= 1.16.5
    val list = net.minecraft.client.gui.components.ComponentRenderUtils.wrapComponents(this.content(), width, mc.font)
    return list.map { it ->
        McChatLine<net.minecraft.util.FormattedCharSequence>(this.addedTime, it,
            //#if MC >= 1.21.1
            this.tag, it == list.last()
            //#else
            //$$ this.id
            //#endif
        )
    }
    //#else
    //$$ val list = net.minecraft.client.gui.GuiUtilRenderComponents.splitText(chatComponent, width, mc.fontRenderer, false, false)
    //$$ return list.map { it ->
    //$$     McChatLine<Any>(this.updatedCounter, it, this.chatLineID)
    //$$ }
    //#endif
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

typealias McChatMessage<T> =
    GuiMessage
    //#if MC == 1.16.5
    //$$ <T>
    //#endif

typealias McChatLine<T> =
    //#if MC >= 1.21.1
    GuiMessage.Line
    //#else
    //$$ McChatMessage<T>
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