@file:JvmName("RenderUtil")

package org.polyfrost.chatting.core

import dev.deftu.omnicore.api.client.options.OmniChatSettings
import dev.deftu.omnicore.api.client.render.OmniResolution
import net.minecraft.client.GuiMessage
import net.minecraft.client.gui.GuiGraphics
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.pow

@JvmField
var length = 0

@JvmField
var maxLength = 0

@JvmField
var lineHeight = 0

@JvmField
var offsetX = 0

@JvmField
var offsetY = 0

var messagesLength = 0

fun recalculate(list: MutableList<GuiMessage.Line>, scrollPos: Int) {
    val chatHud = mainChatHud ?: return
    lineHeight = (9 * (1 + OmniChatSettings.chatLineSpacing)).toInt()
    val heightSettings = if (chatFocused) OmniChatSettings.chatHeightFocused else OmniChatSettings.chatHeightUnfocused
    maxLength = floor(20 + 160 * heightSettings).toInt() / lineHeight
    messagesLength = list.size
    length = getVisibleLength(list, scrollPos)
    offsetX = (chatHud.get().x / mcScale - getVanillaChatX()).toInt()
    offsetY = (chatHud.get().y / mcScale).toInt() - getVanillaChatY() + (length * lineHeight * chatScale * hudScale).toInt()
    chatHud.get().width = (getWidth() + getExtraWidth()) * chatScale.toFloat() * mcScale
    chatHud.get().height = lineHeight * length * chatScale.toFloat() * mcScale
    InputHandler.hoveredIndex = getSelectedIndex()
}

fun getWidth(): Int {
    return floor(40 + 280 * OmniChatSettings.chatWidth).toInt()
}

fun getExtraWidth(): Int {
    return 12
}

fun getVanillaChatX(): Float {
    return 0f
}

fun getVanillaChatY(): Int {
    return OmniResolution.scaledHeight - 40
}

fun getVisibleLength(list: MutableList<GuiMessage.Line>, scrollPos: Int): Int {
    if (list.isEmpty()) return 0
    var length = 0
    val focused = chatFocused
    var i = min(messagesLength - scrollPos, maxLength) - 1
    return if (ModConfig.fade) {
        while (i >= 0) {
            if (list[i + scrollPos].canRender(focused)) length++
            i--
        }
        length
    } else {
        i + 1
    }
}

fun GuiMessage.Line.canRender(focused: Boolean): Boolean {
    val age = mc.gui.guiTicks - this.addedTime
    val opacity = if (focused) {
        1f
    } else {
        clamp((1 - age.toDouble() / ceil(20.0 * ModConfig.fadeTime)) * 10, 0.0, 1.0).pow(2).toFloat()
    }
    //#if MC >= 1.21.8
    return opacity > 1.0E-5F
    //#else
    //$$ return 255 * opacity * OmniChatSettings.chatOpacity > 3
    //#endif
}

fun GuiGraphics.push() {
    //#if MC >= 1.21.8
    this.pose().pushMatrix()
    //#else
    //$$ this.pose().pushPose()
    //#endif
}

fun GuiGraphics.pop() {
    //#if MC >= 1.21.8
    this.pose().popMatrix()
    //#else
    //$$ this.pose().popPose()
    //#endif
}

fun GuiGraphics.translate(x: Float, y: Float) {
    if (x == 0f && y == 0f) return
    //#if MC >= 1.21.8
    this.pose().translate(x, y)
    //#else
    //$$ this.pose().translate(x, y, 0f)
    //#endif
}

fun GuiGraphics.scale(x: Float, y: Float) {
    if (x == 1f && y == 1f) return
    //#if MC >= 1.21.8
    this.pose().scale(x, y)
    //#else
    //$$ this.pose().scale(x, y, 1f)
    //#endif
}