@file:JvmName("RenderUtil")

package org.polyfrost.chatting.core

import dev.deftu.omnicore.api.client.options.OmniChatSettings
import dev.deftu.omnicore.api.client.render.OmniResolution
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.pow

@JvmField
var renderingObj: Any? = null

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

fun recalculate(list: MutableList<McChatLine>, scrollPos: Int) {
    val chatHud = mainChatHud ?: return
    lineHeight = (9 * (1 + OmniChatSettings.chatLineSpacing)).toInt()
    val heightSettings = if (chatFocused) OmniChatSettings.chatHeightFocused else OmniChatSettings.chatHeightUnfocused
    maxLength = floor(20 + 160 * heightSettings).toInt() / lineHeight
    length = getVisibleLength(list, scrollPos)
    offsetX = (chatHud.get().x / mcScale - getVanillaChatX()).toInt()
    offsetY = (chatHud.get().y / mcScale).toInt() - getVanillaChatY()
    val chatScale = OmniChatSettings.chatScale.toFloat() * mcScale
    chatHud.get().width = floor(40 + 280 * OmniChatSettings.chatWidth).toFloat() + getExtraWidth()
    chatHud.get().height = lineHeight * length * chatScale
}

fun getExtraWidth(): Int {
    //#if MC >= 1.21.1
    return 12
    //#elseif MC >= 1.12.2
    //$$ return 6
    //#else
    //$$ return 4
    //#endif
}

fun getVanillaChatX(): Float {
    //#if MC == 1.16.5
    //$$ return 2 * (1 - OmniChatSettings.chatScale).toFloat()
    //#elseif MC >= 1.12.2
    return 0f
    //#else
    //$$ return 2f
    //#endif
}

fun getVanillaChatY(): Int {
    val startY = OmniResolution.scaledHeight - 28 - if (isModern()) 12 else 0
    return startY - (length * lineHeight * OmniChatSettings.chatScale).toInt()
}

fun getVisibleLength(list: MutableList<McChatLine>, scrollPos: Int): Int {
    if (list.isEmpty()) return 0
    var length = 0
    val focused = chatFocused
    var i = min(list.size - scrollPos, maxLength) - 1
    while (i >= 0) {
        if (list[i + scrollPos].canRender(focused)) length++
        i--
    }
    return length
}

fun McChatLine.canRender(focused: Boolean): Boolean {
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

fun push() {
    //#if MC >= 1.21.1
    val guiGraphics = renderingObj as net.minecraft.client.gui.GuiGraphics? ?: return
        //#if MC >= 1.21.8
        guiGraphics.pose().pushMatrix()
        //#else
        //$$ guiGraphics.pose().pushPose()
        //#endif
    //#elseif MC == 1.16.5
    val poseStack = renderingObj as com.mojang.blaze3d.vertex.PoseStack? ?: return
    poseStack.pushPose()
    //#else
    //$$ net.minecraft.client.renderer.GlStateManager.pushMatrix()
    //#endif
}

fun translate(x: Float, y: Float) {
    if (x == 0f && y == 0f) return
    //#if MC >= 1.21.1
    val guiGraphics = renderingObj as net.minecraft.client.gui.GuiGraphics? ?: return
        //#if MC >= 1.21.8
        guiGraphics.pose().translate(x, y)
        //#else
        //$$ guiGraphics.pose().translate(x, y, 0f)
        //#endif
    //#elseif MC == 1.16.5
    //$$ com.mojang.blaze3d.systems.RenderSystem.translatef(x, y, 0f)
    //#else
    //$$ net.minecraft.client.renderer.GlStateManager.translate(x, y, 0f)
    //#endif
}

fun pop() {
    //#if MC >= 1.21.1
        val guiGraphics = renderingObj as net.minecraft.client.gui.GuiGraphics? ?: return
        //#if MC >= 1.21.8
        guiGraphics.pose().popMatrix()
        //#else
        //$$ guiGraphics.pose().popPose()
        //#endif
    //#elseif MC == 1.16.5
    val poseStack = renderingObj as com.mojang.blaze3d.vertex.PoseStack? ?: return
    poseStack.popPose()
    //#else
    //$$ net.minecraft.client.renderer.GlStateManager.popMatrix()
    //#endif
}