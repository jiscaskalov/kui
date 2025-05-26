package dev.jsco.kui.component

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter

@Environment(EnvType.CLIENT)
class Slider(
    private val label: String,
    private val range: IntRange,
    private val onChange: (Int) -> Unit
) : Component() {
    private var value = range.first

    override fun render(context: DrawContext, tickCounter: RenderTickCounter) {
        //println("Rendering Slider: $label with value: $value")
        // Add Minecraft rendering logic here
    }

    fun setValue(newValue: Int) {
        if (newValue in range) {
            value = newValue
            onChange(value)
        }
    }
}