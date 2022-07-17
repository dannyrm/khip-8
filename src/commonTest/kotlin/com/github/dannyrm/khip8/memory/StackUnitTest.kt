package com.github.dannyrm.khip8.memory

import com.github.dannyrm.khip8.lineSeparator
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.expect

class StackUnitTest {

    @Test
    fun `push and pop`() {
        val stack = Stack()
        stack.push(1u)

        expect(1u) { stack.pop() }
    }

    @Test
    fun `push with empty stack`() {
        val stack = Stack(0)
        assertFailsWith<IllegalStateException> { stack.push(5u) }
    }

    @Test
    fun `Clear with empty stack`() {
        val stack = Stack()
        stack.clear()

        expect(0) { stack.sp }
    }

    @Test
    fun `push and pop with overflow`() {
        val stack = Stack()
        stack.push(0x10000u)
        stack.push(0x10200u)

        expect(0x200u) { stack.pop() }
        expect(0u) { stack.pop() }
    }

    @Test
    fun `push and pop multiple times`() {
        val stack = Stack()
        stack.push(1u)
        stack.push(2u)
        stack.push(3u)
        stack.push(4u)

        expect(4u) { stack.pop() }
        expect(3u) { stack.pop() }
        expect(2u) { stack.pop() }
        expect(1u) { stack.pop() }
    }

    @Test
    fun `push to full stack`() {
        val stack = Stack()
        stack.push(1u)
        stack.push(2u)
        stack.push(3u)
        stack.push(4u)
        assertFailsWith<IllegalStateException> { stack.push(5u) }
    }

    @Test
    fun `pop from empty stack`() {
        val stack = Stack()
        stack.push(1u)

        expect(1u) { stack.pop() }
        assertFailsWith<IllegalStateException> { stack.pop() }
    }

    @Test
    fun `Clear stack`() {
        val stack = Stack()
        stack.push(1u)
        stack.push(2u)

        stack.clear()

        expect(0) { stack.sp }
        assertFailsWith<IllegalStateException> { stack.pop() }
    }

    @Test
    fun `Check toString correct with elements`() {
        val stack = Stack()
        stack.push(1u)
        stack.push(2u)
        stack.push(3u)
        stack.push(4u)

        val nl = lineSeparator()

        expect(
            "\tSize = 4, SP = 0x04$nl" +
                    "\t----------$nl" +
                    "\t| 0x0004 | $nl" +
                    "\t| 0x0003 | $nl" +
                    "\t| 0x0002 | $nl" +
                    "\t| 0x0001 | $nl" +
                    "\t----------$nl"
        ) {
            stack.toString()
        }
    }

    @Test
    fun `Check toString correct without elements`() {
        val stack = Stack()

        val nl = lineSeparator()

        expect("\tSize = 4, SP = 0x00$nl") { stack.toString() }
    }

    @Test
    fun `Check toString correct with empty stack`() {
        val stack = Stack()

        val nl = lineSeparator()

        expect("\tSize = 0, SP = 0x00$nl") { stack.toString() }
    }
}