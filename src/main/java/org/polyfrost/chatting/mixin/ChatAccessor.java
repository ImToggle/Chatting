package org.polyfrost.chatting.mixin;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ChatComponent.class)
public interface ChatAccessor {

    @Accessor
    List<
        GuiMessage
        //#if MC == 11605
        //$$ <net.minecraft.network.chat.Component>
        //#endif
    > getAllMessages();

}
