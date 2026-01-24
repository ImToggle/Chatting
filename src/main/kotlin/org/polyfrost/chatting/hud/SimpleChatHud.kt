package org.polyfrost.chatting.hud

import org.polyfrost.oneconfig.api.config.v1.annotations.Color
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.component.Drawable

abstract class SimpleChatHud(id: String, title: String) : Hud<Drawable>(id, title, Category.INFO) {

    @Color(
        title = "Background Color"
    )
    var bgColor = rgba(0, 0, 0, 0.5f)

    @Color(
        title = "Hovered Background Color"
    )
    var bgColor_hovered = rgba(80, 80, 80, 0.5f)

    abstract fun createDrawable(): Drawable

    override fun create(): Drawable {
        return createDrawable()
    }

    override fun update(): Boolean {
        return false
    }
}