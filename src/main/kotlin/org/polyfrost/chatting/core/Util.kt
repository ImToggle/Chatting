@file:JvmName("Util")

package org.polyfrost.chatting.core

import com.mojang.authlib.GameProfile
import dev.deftu.omnicore.api.client.chatHud
import dev.deftu.omnicore.api.client.input.OmniKeyboard
import dev.deftu.omnicore.api.client.input.OmniMouse
import dev.deftu.omnicore.api.client.options.OmniChatSettings
import dev.deftu.omnicore.api.client.render.OmniResolution
import net.minecraft.ChatFormatting
import net.minecraft.client.GuiMessage
import net.minecraft.client.gui.components.ComponentRenderUtils
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.FormattedCharSequence
import net.minecraft.world.entity.player.ChatVisiblity
import org.polyfrost.chatting.hook.ChatLineHook
import org.polyfrost.chatting.hud.MainChatHud
import org.polyfrost.chatting.mixin.chat.ChatAccessor
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import java.util.*
import kotlin.math.floor
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
var currentSender: PlayerInfo? = null

@JvmField
var shouldReduce = false

@JvmField
var inChat = false

val chatFocused
    get() = inChat || peeking || HudManager.isEditing

private val COLOR_MAP: Map<Int, Char> by lazy {
    ChatFormatting.entries
        .filter { it.isColor }
        .associate { it.color!! to it.char }
}

fun GuiMessage.Line.asHook() = this as Any as ChatLineHook

val chatAccessor
    get() = mc.gui.chat as ChatAccessor

fun scrollChat(value: Double) {
    var amount = clamp(value, -1.0, 1.0)
    if (!OmniKeyboard.isShiftKeyPressed) {
        amount *= 7
    }
    chatHud?.scrollChat(amount.toInt())
}

fun getSelectedIndex(x: Double = OmniMouse.scaledX, y: Double = OmniMouse.scaledY, ignoreX: Boolean = false, ignoreY: Boolean = false): Int {
    if (!inChat) return -1
    if (mc.options.chatVisibility().get() == ChatVisiblity.HIDDEN) return -1
    if (length == 0) return -1
    if (!ignoreX && x !in chatX.toDouble()..chatEndX.toDouble()) return -1
    val mouseY = if (ignoreY) y.coerceIn(chatY.toDouble() + 0.0001..chatEndY.toDouble()) else y
    if (!ignoreY && mouseY !in chatY.toDouble()..chatEndY.toDouble()) return -1
    val index = floor((chatEndY - mouseY) / (lineHeight * chatScale * hudScale)).toInt() + scrollPos
    if (index !in 0 until messagesLength) return -1
    return index
}

fun clamp(value: Double, min: Double, max: Double): Double {
    return if (value < min) min else min(value, max)
}

fun getPlayerInfo(gameProfile: GameProfile) = mc.connection?.getPlayerInfo(gameProfile.id)

fun String.toChatLine(): GuiMessage {
    return GuiMessage(-1, Component.literal(this), null, null)
}

fun GuiMessage.toLines(width: Int): List<GuiMessage.Line> {
    var width = width
    this.tag?.icon?.let { icon ->
        width -= icon.width + 4 + 2
    }
    val list = ComponentRenderUtils.wrapComponents(this.content(), width, mc.font)
    return list.map { it ->
        GuiMessage.Line(this.addedTime, it, this.tag, it == list.last())
    }
}

fun Component.asString(): String = buildString {
    var lastBits = 0
    var lastColor: Int? = null

    this@asString.visit({ style, text ->
        if (text.isEmpty()) return@visit Optional.empty()

        val currentBits = style.toBits()
        val currentColor = style.color?.value

        val lostBits = (lastBits and currentBits.inv()) != 0
        val colorChanged = currentColor != lastColor

        if (lostBits || colorChanged) {
            appendFullStyle(currentBits, currentColor)
        } else {
            val newBits = currentBits and lastBits.inv()
            appendBits(newBits)
        }

        append(text)

        lastBits = currentBits
        lastColor = currentColor

        return@visit Optional.empty<Any>()
    }, Style.EMPTY)
}

fun FormattedCharSequence.asString(): String = buildString {
    var lastBits = 0
    var lastColor: Int? = null

    this@asString.accept { _, style, codepoint ->
        val currentBits = style.toBits()
        val currentColor = style.color?.value

        val lostBits = (lastBits and currentBits.inv()) != 0
        val colorChanged = currentColor != lastColor

        if (lostBits || colorChanged) {
            appendFullStyle(currentBits, currentColor)
        } else {
            val newBits = currentBits and lastBits.inv()
            appendBits(newBits)
        }

        append(Character.toChars(codepoint))

        lastBits = currentBits
        lastColor = currentColor
        true
    }
}

private fun Style.toBits(): Int {
    var bits = 0
    if (isBold) bits = bits or 0x01
    if (isItalic) bits = bits or 0x02
    if (isUnderlined) bits = bits or 0x04
    if (isStrikethrough) bits = bits or 0x08
    if (isObfuscated) bits = bits or 0x10
    return bits
}

private fun StringBuilder.appendBits(bits: Int) {
    if (bits and 0x01 != 0) append("§l")
    if (bits and 0x02 != 0) append("§o")
    if (bits and 0x04 != 0) append("§n")
    if (bits and 0x08 != 0) append("§m")
    if (bits and 0x10 != 0) append("§k")
}

private fun StringBuilder.appendFullStyle(bits: Int, colorValue: Int?) {
    val colorChar = colorValue?.let { COLOR_MAP[it] } ?: 'r';
    append("§$colorChar")
    appendBits(bits)
}