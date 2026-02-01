@file:JvmName("RenderUtil")

package org.polyfrost.chatting.core

import dev.deftu.omnicore.api.client.options.OmniChatSettings
import dev.deftu.omnicore.api.client.render.OmniRenderingContext
import dev.deftu.omnicore.api.client.render.OmniResolution
import dev.deftu.omnicore.api.client.render.pipeline.OmniRenderPipelines
import dev.deftu.omnicore.api.color.OmniColor
import net.minecraft.client.GuiMessage
import net.minecraft.client.gui.GuiGraphics
import org.polyfrost.chatting.animation.AnimationUtil
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.unit.Vec2
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.pow

@JvmField
var renderingChat = false

@JvmField
var maxLength = 0

@JvmField
var offsetX = 0

@JvmField
var offsetY = 0

var length = 0

var messagesLength = 0

var lineHeight = 0

var chatX = 0

var chatY = 0

var chatEndX = 0

var chatEndY = 0

var scrollPos = 0

var totalScroll = 0

fun recalculate(list: MutableList<GuiMessage.Line>, scroll: Int) {
    var delta = list.size - scroll - messagesLength + scrollPos
    var scrolled = delta != 0
    if (scrolled) {
        totalScroll += delta
//        println("animation scroll $delta $totalScroll")
        InputHandler.onScroll(delta, true)
    }
    delta = scroll - scrollPos
    if (delta != 0) {
        scrollPos = scroll
        InputHandler.onScroll(delta, false)
        scrolled = true
    }
    if (scrolled) InputHandler.updateDragState()
    lineHeight = (9 * (1 + OmniChatSettings.chatLineSpacing)).toInt()
    val heightSettings = if (chatFocused) OmniChatSettings.chatHeightFocused else OmniChatSettings.chatHeightUnfocused
    maxLength = floor(20 + 160 * heightSettings).toInt() / lineHeight
    messagesLength = list.size
    length = getVisibleLength(list, scrollPos)
    offsetX = 0
    offsetY = 0
    val chatWidth = ((getWidth() + getExtraWidth()) * chatScale.toFloat()).toInt()
    val chatHeight = (lineHeight * length * chatScale.toFloat()).toInt()
    mainChatHud?.let { chatHud ->
        offsetX = (chatHud.get().x / mcScale - getVanillaChatX()).toInt()
        offsetY = (chatHud.get().y / mcScale).toInt() - getVanillaChatY() + (length * lineHeight * chatScale * hudScale).toInt()
        chatHud.get().width = chatWidth * mcScale
        chatHud.get().height = chatHeight * mcScale
    }
    chatX = getVanillaChatX() + offsetX
    chatEndX = chatX + chatWidth
    chatEndY = getVanillaChatY() + offsetY
    chatY = chatEndY - chatHeight
}

fun getWidth(): Int {
    return floor(40 + 280 * OmniChatSettings.chatWidth).toInt()
}

fun getExtraWidth(): Int {
    return 12
}

private fun getVanillaChatX(): Int {
    return 0
}

private fun getVanillaChatY(): Int {
    return OmniResolution.scaledHeight - 40
}

private fun getVisibleLength(list: MutableList<GuiMessage.Line>, scrollPos: Int): Int {
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

private fun GuiMessage.Line.canRender(focused: Boolean): Boolean {
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

fun GuiGraphics.pushScissor() {
    if (AnimationUtil.chatAnimation.isFinished) return
    this.enableScissor(-4, chatY - offsetY, chatEndX - chatX + 20, chatEndY - offsetY)
}

fun GuiGraphics.popScissor() {
    if (AnimationUtil.chatAnimation.isFinished) return
    this.disableScissor()
}

fun OmniRenderingContext.renderQuad(
    start: Vec2,
    end: Vec2,
    color: OmniColor
) {
    val buffer = OmniRenderPipelines.POSITION_COLOR.createBufferBuilder()
    buffer
        .vertex(pose, start.x.toDouble(), start.y.toDouble(), 0.0)
        .color(color)
        .next()
    buffer
        .vertex(pose, end.x.toDouble(), start.y.toDouble(), 0.0)
        .color(color)
        .next()
    buffer
        .vertex(pose, end.x.toDouble(), end.y.toDouble(), 0.0)
        .color(color)
        .next()
    buffer
        .vertex(pose, start.x.toDouble(), end.y.toDouble(), 0.0)
        .color(color)
        .next()
    buffer.buildOrThrow().drawAndClose(OmniRenderPipelines.POSITION_COLOR)
}