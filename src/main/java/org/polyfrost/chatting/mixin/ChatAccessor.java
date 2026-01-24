package org.polyfrost.chatting.mixin;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChatComponent.class)
public interface ChatAccessor {

    @Accessor
    List<GuiMessage.Line> getTrimmedMessages();

    @Accessor
    int getChatScrollbarPos();

    @Invoker("getMessageLineIndexAt")
    int getIndexAt(double x, double y);

    @Invoker("screenToChatX")
    double getChatX(double x);

    @Invoker("screenToChatY")
    double getChatY(double y);

    @Invoker("getClickedComponentStyleAt")
    Style getStyleAt(double x, double y);

}
