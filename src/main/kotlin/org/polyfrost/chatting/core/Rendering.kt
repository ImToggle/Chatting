@file:JvmName("RenderUtil")

package org.polyfrost.chatting.core

@JvmField
var renderingObj: Any? = null

@JvmField
var xOffset = 0f

@JvmField
var yOffset = 0f

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
    //#if MC >= 1.21.1
    val guiGraphics = renderingObj as net.minecraft.client.gui.GuiGraphics? ?: return
        //#if MC >= 1.21.8
        guiGraphics.pose().translate(x, y)
        //#else
        //$$ guiGraphics.pose().translate(x, y, 0f)
        //#endif
    //#elseif MC == 1.16.5
    val poseStack = renderingObj as com.mojang.blaze3d.vertex.PoseStack? ?: return
    poseStack.translate(x.toDouble(), y.toDouble(), 0.0)
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