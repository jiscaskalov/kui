package dev.jsco.kui.layout

import dev.jsco.kui.component.Component
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
interface Layout {
    fun arrange(components: List<Component>): List<Component>
}