package dev.jsco.kui.listener

import de.florianmichael.dietrichevents2.AbstractEvent
import dev.jsco.kui.Kui
import dev.jsco.kui.util.Input
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
interface MouseButtonListener {

    fun onMouse(event: MouseButtonEvent)

    class MouseButtonEvent(val window: Long, val button: Int, val mouseX: Double, val mouseY: Double, val action: Input.KeyAction) : AbstractEvent<MouseButtonListener>() {

        companion object {
            val ID = Kui.eventId.incrementAndGet()
        }

        var cancelled: Boolean = false

        override fun call(listener: MouseButtonListener) {
            listener.onMouse(this)
        }
    }
}