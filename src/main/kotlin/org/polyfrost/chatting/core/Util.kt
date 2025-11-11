@file:JvmName("Util")

package org.polyfrost.chatting.core

import com.mojang.authlib.GameProfile
import dev.deftu.omnicore.api.client.render.OmniResolution
import dev.deftu.omnicore.api.client.screen.currentScreen
import dev.deftu.textile.TextStyle
import net.minecraft.client.GuiMessage
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.network.chat.Component
import org.polyfrost.chatting.hud.MainChatHud
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import kotlin.math.min
import kotlin.math.pow

val mcScale
    get() = OmniResolution.scaleFactor.toFloat()

val editorMessages = mutableListOf(
    "§b§lChatting",
    "",
    "This is a movable chat",
    "§eDrag me around!"
)

@JvmField
var currentSender: GameProfile? = null

@JvmField
var mainChatHud: MainChatHud? = null

val chatFocused
    get() = currentScreen is ChatScreen

fun getVisibleLength(list: MutableList<McChatVisible>): Int {
    var length = 0
    val focused = chatFocused
    list.forEach {
        if (it.canRender(focused)) length++
    }
    return length
}

fun McChatVisible.canRender(focused: Boolean): Boolean {
    val age = mc.gui.guiTicks - this.addedTime
    val opacity = if (focused) {
        1f
    } else {
        clamp((1 - age.toDouble() / 200.0) * 10, 0.0, 1.0).pow(2).toFloat()
    }
    return opacity > 1.0E-5F
}

fun clamp(value: Double, min: Double, max: Double): Double {
    return if (value < min) min else min(value, max)
}

fun String.toChatLine(): McChatLine {
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

typealias McChatLine =
    GuiMessage
    //#if MC == 1.16.5
    //$$ <net.minecraft.network.chat.Component>
    //#endif

typealias McChatVisible =
    //#if MC >= 1.21.1
    GuiMessage.Line
    //#else
    //$$ McChatLine
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