package dev.jsco.kui

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
object Theme {
    var backgroundColor: Int = 0xFFF0F0F0.toInt()
    var secondaryColor: Int = 0xFFD0D0D0.toInt()
    var textColor: Int = 0xFF000000.toInt()

    var font: String = "proggy-clean"

    fun apply(configure: Theme.() -> Unit) {
        this.configure()
    }
}
