package org.polyfrost.chatting.mixin.chat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.components.ChatComponent;
import org.objectweb.asm.Opcodes;
import org.polyfrost.chatting.core.ChatHandler;
import org.polyfrost.oneconfig.api.hud.v1.HudManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ChatComponent.class)
public class ChatMixin_Replacement {


    @WrapOperation(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/ChatComponent;trimmedMessages:Ljava/util/List;", opcode = Opcodes.GETFIELD))
    private List<?> replacement1(ChatComponent instance, Operation<List<?>> original) {
        return HudManager.isEditing() ? ChatHandler.INSTANCE.getEditorLines() : original.call(instance);
    }

    @WrapOperation(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/ChatComponent;chatScrollbarPos:I", opcode = Opcodes.GETFIELD))
    private int replaceScroll1(ChatComponent instance, Operation<Integer> original) {
        return HudManager.isEditing() ? 0 : original.call(instance);
    }

    //#if MC >= 1.21.8
    @WrapOperation(method = "forEachLine", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/ChatComponent;trimmedMessages:Ljava/util/List;", opcode = Opcodes.GETFIELD))
    private List<?> replacement2(ChatComponent instance, Operation<List<?>> original) {
        return HudManager.isEditing() ? ChatHandler.INSTANCE.getEditorLines() : original.call(instance);
    }

    @WrapOperation(method = "forEachLine", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/ChatComponent;chatScrollbarPos:I", opcode = Opcodes.GETFIELD))
    private int replaceScroll2(ChatComponent instance, Operation<Integer> original) {
        return HudManager.isEditing() ? 0 : original.call(instance);
    }
    //#endif

}