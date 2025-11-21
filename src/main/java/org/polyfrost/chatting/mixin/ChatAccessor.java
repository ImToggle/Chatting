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
        //#if MC >= 1.21.1
        GuiMessage.Line
        //#else
        //$$ GuiMessage
            //#if MC == 1.16.5
            //$$ <net.minecraft.network.chat.Component>
            //#endif
        //#endif
    > getTrimmedMessages();

}
