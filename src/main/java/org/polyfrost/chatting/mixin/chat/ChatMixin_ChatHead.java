package org.polyfrost.chatting.mixin.chat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.polyfrost.chatting.core.Util;
import org.polyfrost.chatting.hook.GuiMessageHook;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(ChatComponent.class)
public class ChatMixin_ChatHead {

    @Shadow @Final private Minecraft minecraft;

    @ModifyVariable(
            method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V",
            at = @At("STORE"), ordinal = 0
    )
    private GuiMessage injectProfile(GuiMessage value) {
        ((GuiMessageHook) (Object) value).chatting$setSender(Util.currentGameProfile);
        return value;
    }

    @Inject(method = "addMessageToDisplayQueue", at = @At("HEAD"))
    private void preSplit(GuiMessage guiMessage, CallbackInfo ci) {
        if (((GuiMessageHook) (Object) guiMessage).chatting$getSender() != null) {
            Util.shouldReduce = true;
        }
    }

    @ModifyArgs(method = "getClickedComponentStyleAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/StringSplitter;componentStyleAtWidth(Lnet/minecraft/util/FormattedCharSequence;I)Lnet/minecraft/network/chat/Style;"))
    private void spacing(Args args, @Local GuiMessage.Line line) {
        if (Util.asHook(line).hasHead()) {
            args.set(1, (int) args.get(1) - 10);
        }
    }

    //#if MC >= 1.21.8
    @WrapOperation(method = "method_71991", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)V"))
    private void renderHead(GuiGraphics instance, Font font, FormattedCharSequence content, int x, int y, int color, Operation<Void> original, @Local(argsOnly = true) GuiMessage.Line line) {
        PlayerInfo sender = Util.asHook(line).chatting$getSender();
        if (sender != null) {
            ResourceLocation texture =
                    //#if MC >= 1.21.10
                    sender.getSkin().body().texturePath();
                    //#else
                    //$$ sender.getSkin().texture();
                    //#endif
            PlayerFaceRenderer.draw(instance, texture, x, y, 8, sender.showHat(), false, color);
            x += 10;
        }
        original.call(instance, font, content, x, y, color);
    }
    //#else
    //$$ @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/util/FormattedCharSequence;III)I"))
    //$$ private int renderHead(GuiGraphics instance, Font font, FormattedCharSequence content, int x, int y, int color, Operation<Integer> original, @Local GuiMessage.Line line) {
    //$$ PlayerInfo sender = Util.asHook(line).chatting$getSender();
    //$$ if (sender != null) {
            //#if MC > 1.21.1
            //$$ boolean showHat = sender.showHat();
            //#else
            //$$ net.minecraft.world.entity.player.Player player = this.minecraft.level.getPlayerByUUID(sender.getProfile().getId());
            //$$ boolean showHat = player.isModelPartShown(net.minecraft.world.entity.player.PlayerModelPart.HAT.HAT);
            //#endif
    //$$ PlayerFaceRenderer.draw(instance, sender.getSkin().texture(), x, y, 8, showHat, false
        //#if MC > 1.21.1
        //$$ , color
        //#endif
    //$$ );
    //$$ x += 10;
    //$$ }
    //$$ return original.call(instance, font, content, x, y, color);
    //$$ }
    //#endif

}
