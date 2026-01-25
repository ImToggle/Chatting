package org.polyfrost.chatting.core

import org.polyfrost.chatting.ChattingConstants
import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindManager
import org.polyfrost.oneconfig.utils.v1.dsl.mc
import org.polyfrost.polyui.color.rgba
import org.polyfrost.polyui.input.KeybindHelper
import org.polyfrost.polyui.input.Keys

object ModConfig : Config("${ChattingConstants.MODID}.json", ChattingConstants.NAME, Category.OTHER) {

    @Dropdown(
        title = "Text Render Type", category = "General", options = ["No Shadow", "Shadow", "Full Shadow"],
        description = "Specifies how text should be rendered in the chat. Full Shadow displays a shadow on all sides of the text, while Shadow only displays a shadow on the right and bottom sides of the text."
    )
    var textRenderType = 1

    @Checkbox(
        title = "Message Fade"
    )
    var fade = true

    @Slider(
        title = "Time Before Fade",
        min = 0f, max = 20f
    )
    var fadeTime = 10f

    @Switch(
        title = "Chat Peek",
        description = "Allows you to view / scroll chat while moving around."
    )
    var chatPeek = false

    @Switch(
        title = "Chat Peek Scrolling",
    )
    var peekScrolling = true

    @Keybind(
        title = "Peek KeyBind"
    )
    var chatPeekBind = KeybindHelper.builder().keys(Keys.Z).does { down ->
        if (!chatPeek) return@does
        val last = peeking
        if (peekMode == 0) {
            peeking = down
        } else {
            if (down) peeking = !peeking
        }
        if (peeking != last && !peeking) mc.gui.chat.resetChatScroll()
    } .build()

    @RadioButton(
        title = "Peek Mode",
        options = ["Held", "Toggle"]
    )
    var peekMode = 0

    @Switch(
        title = "Underlined Links", category = "General",
        description = "Makes clickable links in chat blue and underlined."
    )
    var underlinedLinks = false

    @Switch(
        title = "Smooth Chat Messages",
        category = "Animations", subcategory = "Messages",
        description = "Smoothly animate chat messages when they appear."
    )
    var smoothChat = true

    @Slider(
        title = "Message Animation Speed",
        category = "Animations", subcategory = "Messages",
        min = 0.0f, max = 1.0f,
        description = "The speed at which chat messages animate."
    )
    var messageSpeed = 0.5f

    @Switch(
        title = "Disable for Edits",
        category = "Animations", subcategory = "Messages",
        description = "Disable smooth animations for edited messages."
    )
    var disableSmoothEdits = true

    @Switch(
        title = "Smooth Chat Scrolling",
        category = "Animations", subcategory = "Scrolling",
        description = "Smoothly animate scrolling when scrolling through the chat."
    )
    var smoothScrolling = true

    @Slider(
        title = "Scrolling Animation Speed",
        category = "Animations", subcategory = "Scrolling",
        min = 0.0f, max = 1.0f,
        description = "The speed at which scrolling animates."
    )
    var scrollingSpeed = 0.15f

    @Switch(
        title = "Remove Scroll Bar",
        category = "Animations", subcategory = "Scrolling",
        description = "Removes the vanilla scroll bar from the chat."
    )
    var removeScrollBar = true

    @Color(
        title = "Chat Button Color", category = "Buttons",
        description = "The color of the chat button."
    )
    var chatButtonColor = rgba(255, 255, 255, 1f)

    @Color(
        title = "Chat Button Hovered Color", category = "Buttons",
        description = "The color of the chat button when hovered."
    )
    var chatButtonHoveredColor = rgba(255, 255, 160, 1f)

    @Color(
        title = "Chat Button Background Color", category = "Buttons",
        description = "The color of the chat button background."
    )
    var chatButtonBackgroundColor = rgba(0, 0, 0, 0.5f)

    @Color(
        title = "Chat Button Hovered Background Color", category = "Buttons",
        description = "The color of the chat button background when hovered."
    )
    var chatButtonHoveredBackgroundColor = rgba(255, 255, 255, 0.5f)

    @Switch(
        title = "Button Shadow", category = "Buttons",
        description = "Enable button shadow."
    )
    var buttonShadow = true

    @Switch(
        title = "Extend Chat Background",
        category = "Buttons",
        description = "Extends the chat background if buttons are enabled."
    )
    var extendBG = true

    @Switch(
        title = "Chat Copying Button", category = "Buttons",
        description = "Enable copying chat messages via a button."
    )
    var chatCopy = true

    @Switch(
        title = "Delete Chat Message Button", category = "Buttons",
        description = "Enable deleting individual chat messages via a button."
    )
    var chatDelete = true

    @Switch(
        title = "Delete Chat History Button", category = "Buttons",
        description = "Enable deleting chat history via a button."
    )
    var chatDeleteHistory = true

    @Switch(
        title = "Chat Screenshot Button", category = "Buttons",
        description = "Enable taking a screenshot of the chat via a button."
    )
    var chatScreenshot = true

    @Switch(
        title = "Chat Searching", category = "Buttons",
        description = "Enable searching through chat messages."
    )
    var chatSearch = true

    @Switch(
        title = "Show Chat Heads", description = "Show the chat heads of players in chat", category = "Chat Heads",
    )
    var showChatHeads = true

    @Switch(
        title = "Offset Non-Player Messages",
        description = "Offset all messages, even if a player has not been detected.",
        category = "Chat Heads"
    )
    var offsetAll = false

    @Dropdown(
        title = "Screenshot Mode", category = "Screenshotting", options = ["Save To System", "Add To Clipboard", "Both"],
        description = "What to do when taking a screenshot."
    )
    var scMode = 0

    init {
        addCallback("chatPeek") {
            if (!chatPeek) peeking = false
        }
        addCallback("showChatHeads") {
            chatAccessor.invokeRefreshTrimmedMessages()
        }
        KeybindManager.registerKeybind(chatPeekBind)
    }

}
