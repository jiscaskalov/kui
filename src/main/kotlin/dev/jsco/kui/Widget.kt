package dev.jsco.kui

import dev.jsco.kui.component.Button
import dev.jsco.kui.component.Component
import dev.jsco.kui.component.Slider
import dev.jsco.kui.component.Text
import dev.jsco.kui.layout.Layout
import dev.jsco.kui.layout.VerticalLayout
import dev.jsco.kui.util.FontManager
import me.x150.renderer.render.ExtendedDrawContext
import me.x150.renderer.util.Color
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import org.joml.Vector4f

@Environment(EnvType.CLIENT)
class Widget(var title: net.minecraft.text.Text) : Component() {

    private val components = mutableListOf<Component>()
    private var layout: Layout = VerticalLayout()

    var backgroundColor: Int = Theme.backgroundColor
    var secondaryColor: Int = Theme.secondaryColor
    var textColor: Int = Theme.textColor
    var font: String = Theme.font
    var radius: Float = 0f
    var samples: Float = 1f

    fun theme(configure: Theme.() -> Unit) {
        Theme.apply(configure)
        backgroundColor = Theme.backgroundColor
        secondaryColor = Theme.secondaryColor
        textColor = Theme.textColor
        font = Theme.font
    }

    fun position(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun size(width: Float, height: Float) {
        this.width = width
        this.height = height
    }

    fun corners(radius: Float, samples: Float) {
        this.radius = radius
        this.samples = samples
    }

    fun layout(layout: Layout) {
        this.layout = layout
    }

    fun text(string: String, scale: Float, padding: Float, font: String? = this.font) {
        components.add(Text(this, string, scale, padding, font))
    }

    fun button(label: String): Button {
        val button = Button(this, label)
        components.add(button)
        return button
    }

    fun slider(label: String, range: IntRange, onChange: (Int) -> Unit) {
        components.add(Slider(label, range, onChange))
    }

    override fun render(context: DrawContext, tickCounter: RenderTickCounter) {
        val scale = 0.95f
        val padding = 1f

        ExtendedDrawContext.drawRoundedRect(context, x, y, width, height, Vector4f(radius), Color(backgroundColor))
        ExtendedDrawContext.drawRoundedRect(context, x, y, width, 10f, Vector4f(0f, radius, radius, 0f), Color(secondaryColor))
        FontManager.draw(FontManager.getOrCreateBuffer(title, "proggy-clean"), context, x+padding*4, y+padding)
        //RenderEngine2D.text(context, title, x+padding*4, y+padding, scale, Color(textColor), false, font)
        layout.arrange(components).forEach { it.render(context, tickCounter) }
    }
}