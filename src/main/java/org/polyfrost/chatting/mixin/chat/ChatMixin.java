package org.polyfrost.chatting.mixin.chat;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.FormattedCharSequence;
import org.polyfrost.chatting.core.ChatHandler;
import org.polyfrost.chatting.core.ModConfig;
import org.polyfrost.chatting.core.Util;
import org.polyfrost.chatting.hook.ChatLineHook;
import org.polyfrost.polyui.color.PolyColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

//#if MC <= 1.21.5
//$$ import com.llamalad7.mixinextras.injector.wrapoperation.*;
//$$ import com.llamalad7.mixinextras.sugar.Local;
//#endif

@Mixin(ChatComponent.class)
public abstract class ChatMixin {

    // Chat Appearance

    @Shadow private int chatScrollbarPos;

    @ModifyArgs(
            //#if MC >= 1.21.8
            method = "method_71992",
            //#else
            //$$ method = "render",
            //#endif
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V",
                    ordinal = 0
            )
    )
    private void setBackgroundColor(Args args, @Local(ordinal =
            //#if MC >= 1.21.8
            6
            //#else
            //$$ 13
            //#endif
            , argsOnly = true) int lineIndex) {
        lineIndex += chatScrollbarPos;
        if (Util.mainChatHud == null) return;
        int index = 4;
        PolyColor bgColor = lineIndex == ChatHandler.hoveredIndex ? Util.mainChatHud.getBgColor_hovered() : ChatHandler.selectedIndexes.contains(lineIndex) ? Util.mainChatHud.getBgColor_selected() : Util.mainChatHud.getBgColor();
        int alpha = (int) (bgColor.alpha() * ((((int) args.get(index) >>  24) & 0xFF) / 127f));
        int color = (bgColor.getArgb() & 0x00FFFFFF) | (alpha << 24);
        args.set(index, color);
    }

    // Chat Message Fading

    //#if MC <= 1.21.5
    //$$ @ModifyConstant(method = "render", constant = @Constant(intValue = 200))
    //$$ private int setFadeTime(int value) {
    //$$     return (int) Math.ceil(20 * ModConfig.INSTANCE.getFadeTime());
    //$$ }
    //$$
    //$$ @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/GuiMessage$Line;addedTime()I"))
    //$$ private int toggleFade(net.minecraft.client.GuiMessage.Line instance,
    //$$     Operation<Integer> original, @Local(ordinal = 0, argsOnly = true) int ticks) {
    //$$     if (ModConfig.INSTANCE.getFade()) {
    //$$         return original.call(instance);
    //$$     } else {
    //$$         return ticks;
    //$$     }
    //$$ }
    //#endif

    @Inject(method = "getTimeFactor", at = @At("HEAD"), cancellable = true)
    private static void toggleFade(CallbackInfoReturnable<Double> cir) {
        if (!ModConfig.INSTANCE.getFade()) cir.setReturnValue(1.0);
    }

    @ModifyConstant(method = "getTimeFactor", constant = @Constant(doubleValue = 200.0))
    private static double setFadeTime(double value) {
        return 20 * ModConfig.INSTANCE.getFadeTime();
    }

    // Chat Peek

    @ModifyVariable(method = "render", at = @At(value = "HEAD", ordinal = 0), argsOnly = true)
    private boolean setPeek(boolean value) {
        return Util.getChatFocused();
    }

    // Chat Interaction

    @Unique String fullMessage = "";

    @Unique int size = -1;

    @Inject(method = "addMessageToDisplayQueue", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;isChatFocused()Z"))
    private void preAdd(GuiMessage guiMessage, CallbackInfo ci, @Local List<FormattedCharSequence> list) {
        fullMessage = Util.asString(guiMessage.content());
        size = list.size();
        ChatHandler.INSTANCE.shiftSelection(list.size());
    }

    @ModifyArgs(method = "addMessageToDisplayQueue", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V"))
    private void onAdd(Args args, @Local(ordinal = 1) int index) {
        GuiMessage.Line chatLine = args.get(1);
        ChatLineHook hook = (ChatLineHook) (Object) chatLine;
        assert hook != null;
        hook.chatting$setLeft(index + 1 - size);
        hook.chatting$setRight(index);
    }

    @Inject(method = "clearMessages", at = @At("HEAD"))
    private void onClear(boolean bl, CallbackInfo ci) {
        ChatHandler.INSTANCE.clearSelection();
    }

    @Inject(method = "refreshTrimmedMessages", at = @At("HEAD"))
    private void onRefresh(CallbackInfo ci) {
        ChatHandler.INSTANCE.clearSelection();
    }
}
