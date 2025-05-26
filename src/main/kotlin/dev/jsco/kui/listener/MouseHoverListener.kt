package dev.jsco.kui.listener

import de.florianmichael.dietrichevents2.AbstractEvent
import dev.jsco.kui.Kui
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
interface MouseHoverListener {

    fun onHover(event: MouseHoverEvent)

    class MouseHoverEvent(val window: Long, val mouseX: Double, val mouseY: Double) : AbstractEvent<MouseHoverListener>() {

        companion object {
            val ID = Kui.eventId.incrementAndGet()
        }

        var cancelled: Boolean = false

        override fun call(listener: MouseHoverListener) {
            listener.onHover(this)
        }
    }
}