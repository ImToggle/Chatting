@file:JvmName("Util")

package org.polyfrost.chatting.core

import com.mojang.authlib.GameProfile
import dev.deftu.omnicore.api.client.render.OmniResolution
import dev.deftu.textile.TextStyle
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.text.Text

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

fun String.toChatLine(): McChatLine {
    return ChatHudLine(
        -1,
        //#if MC >= 1.16.5
        Text.literal(this),
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
    ChatHudLine
    //#if MC == 1.16.5
    //$$ <net.minecraft.text.Text>
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