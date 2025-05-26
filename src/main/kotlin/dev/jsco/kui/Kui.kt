package dev.jsco.kui

import de.florianmichael.dietrichevents2.DietrichEvents2
import dev.jsco.kui.util.FontManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger

@Environment(EnvType.CLIENT)
class Kui : ClientModInitializer {

    companion object {
        private const val NAME = "Kui"
        private const val VERSION = "0.0.1"
        val logger: Logger = LoggerFactory.getLogger(NAME)
        val client: MinecraftClient = MinecraftClient.getInstance()
        val eventBus: DietrichEvents2 = DietrichEvents2.global()
        val eventId: AtomicInteger = AtomicInteger(0)
        val EXAMPLE_LAYER: Identifier? = Identifier.of("kui", "hud-example-layer")
    }

    override fun onInitializeClient() {
        logger.info("Loading $NAME - $VERSION")
        Runtime.getRuntime().addShutdownHook(Thread { FontManager.clean() })

        val widget = Widget(Text.literal("Test Widget")).apply {
            theme { font = "proggy-clean" }
            size(200f, 100f)
            corners(2f, 8f)
            text("Hello World!", 1f, 2f, "default")
            button("Click me").apply {
                onClick { client.player!!.sendMessage(Text.of("hey guys"), false) }
                corners(2f, 8f)
                text(1f)
            }
        }

        HudLayerRegistrationCallback.EVENT.register { it.attachLayerBefore(IdentifiedLayer.CHAT, EXAMPLE_LAYER, widget::render) }
    }
}
