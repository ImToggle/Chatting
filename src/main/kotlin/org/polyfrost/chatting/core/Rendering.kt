@file:JvmName("RenderUtil")

package org.polyfrost.chatting.core

import dev.deftu.omnicore.api.client.options.OmniChatSettings
import dev.deftu.omnicore.api.client.render.OmniResolution
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import kotlin.math.ceil
import kotlin.math.pow

@JvmField
var renderingObj: Any? = null

@JvmField
var chatX = 0f

@JvmField
var chatY = 0f

@JvmField
var xOffset = 0

@JvmField
var yOffset = 0

@JvmField
var length = 0

fun recalculate(list: MutableList<McChatLine>) {
    length = getVisibleLength(list)
    xOffset = (chatX / mcScale).toInt()
    yOffset = (chatY / mcScale).toInt() - getVanillaChatY()
}

fun getVanillaChatY(): Int {
    val startY = OmniResolution.scaledHeight - 28 - if (isModern()) 12 else 0
    return startY - length * (9 * (1 + OmniChatSettings.chatLineSpacing) * OmniChatSettings.chatScale).toInt()
}

fun getVisibleLength(list: MutableList<McChatLine>): Int {
    var length = 0
    val focused = chatFocused
    list.forEach {
        if (it.canRender(focused)) length++
    }
    return length
}

fun McChatLine.canRender(focused: Boolean): Boolean {
    val age = mc.gui.guiTicks - this.addedTime
    val opacity = if (focused || !ModConfig.fade) {
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