package org.polyfrost.chatting.hud

import org.polyfrost.chatting.core.mainChatHud
import org.polyfrost.polyui.component.Drawable
import org.polyfrost.polyui.component.impl.Block
import org.polyfrost.polyui.unit.Vec2
import org.polyfrost.polyui.unit.by

class MainChatHud : SimpleChatHud("mainChat.yml", "Main Chat") {

    init {
        mainChatHud = this
    }

    override fun multipleInstancesAllowed(): Boolean {
        return false
    }

    override fun defaultPosition(): Vec2 {
        return 0f by 500f
    }

    override fun createDrawable(): Drawable {
        return Block(size = 1f by 1f)
    }
}