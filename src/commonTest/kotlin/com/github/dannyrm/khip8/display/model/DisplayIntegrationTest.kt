package com.github.dannyrm.khip8.display.model

import com.github.dannyrm.khip8.util.lineSeparator
import kotlin.test.Test
import kotlin.test.expect

class DisplayIntegrationTest {

    @Test
    fun `toString implementation`() {
        val displayMemory = DisplayMemory()
        displayMemory[5,5] = 0xFFu
        displayMemory[25,31] = 0xFFu

        val display = Display(displayMemory)

        var newLine = lineSeparator()

        expect(
            "Display Memory {$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000011111111000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000000000000000000000000000000000000000000$newLine" +
                    "\t0000000000000000000000000111111110000000000000000000000000000000$newLine" +
                    "}"
            ) { display.toString() }
    }
}