package dev.jsco.kui.mixin;

import dev.jsco.kui.Kui;
import dev.jsco.kui.listener.MouseButtonListener;
import dev.jsco.kui.listener.MouseHoverListener;
import dev.jsco.kui.util.Input;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Mouse;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT) @Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        Input.INSTANCE.setButtonState(button, action != GLFW.GLFW_RELEASE);
        int mouseX = (int) (Kui.Companion.getClient().mouse.getX() * (double) Kui.Companion.getClient().getWindow().getScaledWidth() / (double) Kui.Companion.getClient().getWindow().getWidth());
        int mouseY = (int) (Kui.Companion.getClient().mouse.getY() * (double) Kui.Companion.getClient().getWindow().getScaledWidth() / (double) Kui.Companion.getClient().getWindow().getWidth());
        MouseButtonListener.MouseButtonEvent event = new MouseButtonListener.MouseButtonEvent(window, button, mouseX, mouseY, Input.KeyAction.Companion.get(action));
        Kui.Companion.getEventBus().call(MouseButtonListener.MouseButtonEvent.Companion.getID(), event);
    }

    @Inject(method = "onCursorPos", at = @At("HEAD"))
    private void onCursorPos(long window, double x, double y, CallbackInfo ci) {
        int mouseX = (int) (Kui.Companion.getClient().mouse.getX() * (double) Kui.Companion.getClient().getWindow().getScaledWidth() / (double) Kui.Companion.getClient().getWindow().getWidth());
        int mouseY = (int) (Kui.Companion.getClient().mouse.getY() * (double) Kui.Companion.getClient().getWindow().getScaledWidth() / (double) Kui.Companion.getClient().getWindow().getWidth());
        MouseHoverListener.MouseHoverEvent event = new MouseHoverListener.MouseHoverEvent(window, mouseX, mouseY);
        Kui.Companion.getEventBus().call(MouseHoverListener.MouseHoverEvent.Companion.getID(), event);
    }

}
