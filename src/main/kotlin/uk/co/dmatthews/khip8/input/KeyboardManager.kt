package uk.co.dmatthews.khip8.input

import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

class KeyboardManager(private val chip8InputManager: Chip8InputManager,
                      private val systemActionInputManager: SystemActionInputManager): KeyAdapter() {

    override fun keyPressed(e: KeyEvent) {
        setKeyEvent(e, true)
    }

    override fun keyReleased(e: KeyEvent) {
        setKeyEvent(e, false)
    }

    private fun setKeyEvent(e: KeyEvent, switchOn: Boolean) {
        when (e.keyCode) {
            KeyEvent.VK_1 -> chip8InputManager[Chip8Inputs.ONE] = switchOn
            KeyEvent.VK_2 -> chip8InputManager[Chip8Inputs.TWO] = switchOn
            KeyEvent.VK_3 -> chip8InputManager[Chip8Inputs.THREE] = switchOn
            KeyEvent.VK_4 -> chip8InputManager[Chip8Inputs.C] = switchOn
            KeyEvent.VK_Q -> chip8InputManager[Chip8Inputs.FOUR] = switchOn
            KeyEvent.VK_W -> chip8InputManager[Chip8Inputs.FIVE] = switchOn
            KeyEvent.VK_E -> chip8InputManager[Chip8Inputs.SIX] = switchOn
            KeyEvent.VK_R -> chip8InputManager[Chip8Inputs.D] = switchOn
            KeyEvent.VK_A -> chip8InputManager[Chip8Inputs.SEVEN] = switchOn
            KeyEvent.VK_S -> chip8InputManager[Chip8Inputs.EIGHT] = switchOn
            KeyEvent.VK_D -> chip8InputManager[Chip8Inputs.NINE] = switchOn
            KeyEvent.VK_F -> chip8InputManager[Chip8Inputs.E] = switchOn
            KeyEvent.VK_Z -> chip8InputManager[Chip8Inputs.A] = switchOn
            KeyEvent.VK_X -> chip8InputManager[Chip8Inputs.ZERO] = switchOn
            KeyEvent.VK_C -> chip8InputManager[Chip8Inputs.B] = switchOn
            KeyEvent.VK_V -> chip8InputManager[Chip8Inputs.F] = switchOn
        }

        // For system operations we only want to detect the initial press, ignoring the release
        if (switchOn) {
            when (e.keyCode) {
                KeyEvent.VK_F1 -> systemActionInputManager.memoryDumpFunction()
            }
        }
    }
}