package org.polyfrost.chatting.core

import dev.deftu.omnicore.api.client.framebuffer.OmniFramebuffers
import dev.deftu.omnicore.api.client.image.OmniImages
import dev.deftu.omnicore.api.client.options.OmniChatSettings
import dev.deftu.omnicore.api.client.render.OmniTextRenderer
import dev.deftu.omnicore.api.client.render.TextShadowType
import dev.deftu.omnicore.api.client.textures.OmniTextureFormat
import dev.deftu.omnicore.api.color.OmniColors
import net.minecraft.Util
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import java.io.File
import kotlin.math.round

object ScreenshotHandler {

    fun screenshot(selection: MutableSet<Int>) {
        val framebuffer = OmniFramebuffers.create(getWidth() + getExtraWidth(), lineHeight * length, OmniTextureFormat.RGBA8, OmniTextureFormat.DEPTH24_STENCIL8)
        try {
            framebuffer.usingToRender { matrices, _, _ ->
                framebuffer.clearColor(0f, 0f, 0f, 0f)
                matrices.push()
                val spacing = round(OmniChatSettings.chatLineSpacing * 5 + 1).toFloat()
                selection.forEach { index ->
                    val line = chatAccessor.trimmedMessages[index]
                    OmniTextRenderer.render(
                        matrices,
                        line.content.asString(),
                        4f, spacing,
                        OmniColors.WHITE,
                        TextShadowType.Drop
                    )
                    matrices.translate(0f, lineHeight.toFloat(), 0f)
                }
                matrices.pop()
            }
            val path = File(mc.gameDirectory, "screenshots").apply { mkdirs() }
            val targetFile = getFile(path)
            OmniImages.from(framebuffer.colorTexture).saveTo(targetFile)
        } finally {
            framebuffer.close()
        }
    }

    private fun getFile(directory: File): File {
        val timestamp = Util.getFilenameFormattedDateTime()
        return generateSequence(1) { it + 1 }
            .map { i ->
                val suffix = if (i == 1) "" else "_$i"
                File(directory, "${timestamp}${suffix}.png")
            }
            .first { !it.exists() }
    }
}