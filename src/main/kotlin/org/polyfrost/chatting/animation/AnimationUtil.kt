@file:JvmName("AnimationUtil")

package org.polyfrost.chatting.animation

import org.polyfrost.polyui.animate.Animation
import org.polyfrost.polyui.utils.Clock

object AnimationUtil {

    private val clock = Clock()

    private var delta = 0L

    @JvmField
    var chatAnimation: Animation = DummyAnimation(0f)

    fun initialize() {
    }

    fun update() {
        delta = clock.delta
        if (chatAnimation !is DummyAnimation) {
            chatAnimation.update(delta)
            if (chatAnimation.isFinished) {
                chatAnimation = DummyAnimation(chatAnimation.to)
            }
        }
    }

    fun onScroll(amount: Int) {
    }

}