package dev.jsco.kui.mixin;

import dev.jsco.kui.util.FontManager;
import net.minecraft.client.resource.ResourceReloadLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResourceReloadLogger.class)
public class ResourceReloadLoggerMixin {

    @Inject(method = "finish", at = @At("HEAD"))
    private void finish(CallbackInfo ci) {
        FontManager.INSTANCE.init();
    }

}
