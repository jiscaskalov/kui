package dev.jsco.kui.component

import dev.jsco.kui.Kui
import dev.jsco.kui.Widget
import dev.jsco.kui.listener.MouseHoverListener
import dev.jsco.kui.util.FontManager
import me.x150.renderer.render.ExtendedDrawContext
import me.x150.renderer.util.Color
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.text.Text
import org.joml.Vector4f

@Environment(EnvType.CLIENT)
class Button(
    private var parent: Widget,
    private var label: String,
) : Component() {

    private var textScale: Float = 1f
    private var font: String? = parent.font
    private var padding: Float = 2f
    private var radius: Float = 0f
    private var samples: Float = 1f
    private var backgroundColor: Int = parent.secondaryColor

    var fakeX: Double = 0.0
    var fakeY: Double = 0.0

    fun corners(radius: Float, samples: Float) {
        this.radius = radius
        this.samples = samples
    }

    fun text(textScale: Float, font: String? = this.font) {
        this.textScale = textScale
        this.font = font
    }

    override fun render(context: DrawContext, tickCounter: RenderTickCounter) {
        ExtendedDrawContext.drawRoundedRect(context, parent.x+padding, parent.y+25, Kui.client.textRenderer.getWidth(label).toFloat()+padding*2,10f,
            Vector4f(radius), Color(backgroundColor))
        FontManager.draw(FontManager.getOrCreateBuffer(Text.literal(label).withColor(parent.textColor), parent.font), context, parent.x+padding*2, parent.y+27)
        ExtendedDrawContext.drawEllipse(context, fakeX.toFloat(), fakeY.toFloat(), 2f, 2f, Color(0xFF0000))
    }

    override fun onHover(event: MouseHoverListener.MouseHoverEvent) {
        fakeX = event.mouseX
        fakeY = event.mouseY
    }
}