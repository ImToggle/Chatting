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
    List<
        //#if MC >= 1.21.1
        GuiMessage.Line
        //#else
        //$$ GuiMessage
            //#if MC == 1.16.5
            //$$ <?>
            //#endif
        //#endif
    > getTrimmedMessages();

    @Accessor
    int getChatScrollbarPos();

    //#if MC >= 1.21.1
    @Invoker("getMessageLineIndexAt")
    int getIndexAt(double x, double y);
    //#endif

    //#if MC >= 1.16.5
    @Invoker("getClickedComponentStyleAt")
    Style getStyleAt(double x, double y);
    //#endif

    //#if MC <= 1.12.2
    //$$ @Invoker("getChatComponent")
    //$$ net.minecraft.util.text.ITextComponent getComponentAt(int mouseX, int mouseY);
    //#endif
}
