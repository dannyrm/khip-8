package com.github.dannyrm.khip8.input

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.awt.event.KeyEvent
import javax.swing.JPanel

@ExtendWith(MockKExtension::class)
class KeyboardManagerUnitTest {
    @MockK(relaxed = true) private lateinit var chip8InputManager: Chip8InputManager
    @MockK(relaxed = true) private lateinit var systemActionInputManager: SystemActionInputManager

    @InjectMockKs private lateinit var keyboardManager: KeyboardManager

    @Test
    fun `Chip 8 1 key pressed`() {
        keyboardManager.keyPressed(createDummyKeyEvent(KeyEvent.VK_1))

        verify { chip8InputManager[Chip8Inputs.ONE] = true }
    }

    @Test
    fun `Chip 8 2 key pressed`() {
        keyboardManager.keyPressed(createDummyKeyEvent(KeyEvent.VK_2))

        verify { chip8InputManager[Chip8Inputs.TWO] = true }
    }

    @Test
    fun `Chip 8 3 key pressed`() {
        keyboardManager.keyPressed(createDummyKeyEvent(KeyEvent.VK_3))

        verify { chip8InputManager[Chip8Inputs.THREE] = true }
    }

    @Test
    fun `Chip 8 C key pressed`() {
        keyboardManager.keyPressed(createDummyKeyEvent(KeyEvent.VK_4))

        verify { chip8InputManager[Chip8Inputs.C] = true }
    }

    @Test
    fun `Chip 8 4 key pressed`() {
        keyboardManager.keyPressed(createDummyKeyEvent(KeyEvent.VK_Q))

        verify { chip8InputManager[Chip8Inputs.FOUR] = true }
    }

    @Test
    fun `Chip 8 5 key pressed`() {
        keyboardManager.keyPressed(createDummyKeyEvent(KeyEvent.VK_W))

        verify { chip8InputManager[Chip8Inputs.FIVE] = true }
    }

    @Test
    fun `Chip 8 6 key pressed`() {
        keyboardManager.keyPressed(createDummyKeyEvent(KeyEvent.VK_E))

        verify { chip8InputManager[Chip8Inputs.SIX] = true }
    }

    @Test
    fun `Chip 8 D key pressed`() {
        keyboardManager.keyPressed(createDummyKeyEvent(KeyEvent.VK_R))

        verify { chip8InputManager[Chip8Inputs.D] = true }
    }

    @Test
    fun `Chip 8 7 key pressed`() {
        keyboardManager.keyPressed(createDummyKeyEvent(KeyEvent.VK_A))

        verify { chip8InputManager[Chip8Inputs.SEVEN] = true }
    }

    @Test
    fun `Chip 8 8 key pressed`() {
        keyboardManager.keyPressed(createDummyKeyEvent(KeyEvent.VK_S))

        verify { chip8InputManager[Chip8Inputs.EIGHT] = true }
    }

    @Test
    fun `Chip 8 9 key pressed`() {
        keyboardManager.keyPressed(createDummyKeyEvent(KeyEvent.VK_D))

        verify { chip8InputManager[Chip8Inputs.NINE] = true }
    }

    @Test
    fun `Chip 8 E key pressed`() {
        keyboardManager.keyPressed(createDummyKeyEvent(KeyEvent.VK_F))

        verify { chip8InputManager[Chip8Inputs.E] = true }
    }

    @Test
    fun `Chip 8 A key pressed`() {
        keyboardManager.keyPressed(createDummyKeyEvent(KeyEvent.VK_Z))

        verify { chip8InputManager[Chip8Inputs.A] = true }
    }

    @Test
    fun `Chip 8 0 key pressed`() {
        keyboardManager.keyPressed(createDummyKeyEvent(KeyEvent.VK_X))

        verify { chip8InputManager[Chip8Inputs.ZERO] = true }
    }

    @Test
    fun `Chip 8 B key pressed`() {
        keyboardManager.keyPressed(createDummyKeyEvent(KeyEvent.VK_C))

        verify { chip8InputManager[Chip8Inputs.B] = true }
    }

    @Test
    fun `Chip 8 F key pressed`() {
        keyboardManager.keyPressed(createDummyKeyEvent(KeyEvent.VK_V))

        verify { chip8InputManager[Chip8Inputs.F] = true }
    }

    @Test
    fun `Chip 8 key released`() {
        keyboardManager.keyReleased(createDummyKeyEvent(KeyEvent.VK_V))

        verify { chip8InputManager[Chip8Inputs.F] = false }
    }

    @Test
    fun `Chip 8 key pressed then released`() {
        keyboardManager.keyPressed(createDummyKeyEvent(KeyEvent.VK_V))
        keyboardManager.keyReleased(createDummyKeyEvent(KeyEvent.VK_V))

        verify { chip8InputManager[Chip8Inputs.F] = false }
    }

    @Test
    fun `Chip 8 F1 system key pressed`() {
        keyboardManager.keyPressed(createDummyKeyEvent(KeyEvent.VK_F1))

        verify { systemActionInputManager.memoryDumpFunction() }
    }

    @Test
    fun `Chip 8 F1 system key released does not call function`() {
        keyboardManager.keyReleased(createDummyKeyEvent(KeyEvent.VK_F1))

        verify(inverse = true) { systemActionInputManager.memoryDumpFunction() }
    }

    private fun createDummyKeyEvent(keyEvent: Int): KeyEvent {
        return KeyEvent(jPanel, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keyEvent, 'X')
    }

    companion object {
        private var jPanel: JPanel? = null

        @BeforeAll
        @JvmStatic
        fun `Create JPanel`() {
            jPanel = JPanel()
        }

        @AfterAll
        @JvmStatic
        fun `Dispose of JPanel`() {
            jPanel = null
        }
    }
}