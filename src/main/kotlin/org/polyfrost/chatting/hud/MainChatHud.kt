package org.polyfrost.chatting.hud

import org.polyfrost.chatting.core.mainChatHud
import org.polyfrost.polyui.color.Color
import org.polyfrost.polyui.component.Drawable
import org.polyfrost.polyui.component.impl.Block
import org.polyfrost.polyui.unit.Vec2
import org.polyfrost.polyui.unit.by

class MainChatHud : SimpleChatHud("mainChat.yml", "Main Chat") {

    override fun setup() {
        super.setup()
        if (isReal) mainChatHud = this
    }

    override fun multipleInstancesAllowed(): Boolean {
        return false
    }

    override fun defaultPosition(): Vec2 {
        return 0f by 0f
    }

    override fun hasBackground(): Boolean {
        return false
    }

    override fun createDrawable(): Drawable {
        return Block(size = 1f by 1f, color = Color.TRANSPARENT)
    }
}