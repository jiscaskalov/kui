package dev.jsco.kui.util

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import org.lwjgl.glfw.GLFW

@Environment(EnvType.CLIENT)
object Input {
    private val buttons = BooleanArray(16)

    fun setButtonState(button: Int, pressed: Boolean) {
        if (button >= 0 && button < buttons.size) buttons[button] = pressed
    }

    enum class KeyAction {
        Press,
        Repeat,
        Release;

        companion object {
            fun get(action: Int): KeyAction {
                return when (action) {
                    GLFW.GLFW_PRESS -> Press
                    GLFW.GLFW_RELEASE -> Release
                    else -> Repeat
                }
            }
        }
    }
}