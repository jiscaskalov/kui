package dev.jsco.kui.component

import dev.jsco.kui.Widget
import dev.jsco.kui.util.FontManager
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
class Text(
    var parent: Widget,
    var value: String = "Hello World!",
    var scale: Float = 1f,
    var padding: Float = 2f,
    var font: String? = parent.font
) : Component() {

    override fun render(context: DrawContext, tickCounter: RenderTickCounter) {
        FontManager.draw(FontManager.getOrCreateBuffer(Text.literal(value).withColor(parent.textColor), parent.font), context, parent.x+padding, parent.y+15)
    }

}