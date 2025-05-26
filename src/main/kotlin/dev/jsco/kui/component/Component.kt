package dev.jsco.kui.component

import dev.jsco.kui.Kui
import dev.jsco.kui.listener.MouseButtonListener
import dev.jsco.kui.listener.MouseHoverListener
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter

@Environment(EnvType.CLIENT)
abstract class Component : MouseButtonListener, MouseHoverListener {

    var x: Float = 10f
    var y: Float = 10f
    var width: Float = 200f
    var height: Float = 100f

    private var onClick: () -> Unit = {}
    private var onHover: () -> Unit = {}

    init {
        Kui.eventBus.subscribe(MouseButtonListener.MouseButtonEvent.ID, this)
        Kui.eventBus.subscribe(MouseHoverListener.MouseHoverEvent.ID, this)
    }

    fun isHovered(mouseX: Double, mouseY: Double): Boolean {
        return mouseX > x && mouseX < (x + width) && mouseY > y && mouseY < (y + height)
    }

    fun onClick(onClick: () -> Unit) {
        this.onClick = onClick
    }
    
    fun onHover(onHover: () -> Unit) {
        this.onHover = onHover
    }

    abstract fun render(context: DrawContext, tickCounter: RenderTickCounter)
    override fun onMouse(event: MouseButtonListener.MouseButtonEvent) {
        if (isHovered(event.mouseX, event.mouseY)) onClick()
    }
    override fun onHover(event: MouseHoverListener.MouseHoverEvent) {
        if (!isHovered(event.mouseX, event.mouseY)) onHover()
    }
}