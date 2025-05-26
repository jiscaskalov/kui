package dev.jsco.kui.layout

import dev.jsco.kui.component.Component
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
class VerticalLayout : Layout {
    override fun arrange(components: List<Component>): List<Component> {
        return components // Simple arrangement logic, can be expanded for spacing
    }
}