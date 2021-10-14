package uk.co.dmatthews.khip8.memory

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo

class StackUnitTest {

    @Test
    fun `push and pop`() {
        val stack = Stack(4)
        stack.push(1u)

        expectThat(stack.pop()).isEqualTo(1u)
    }

    @Test
    fun `push and pop with overflow`() {
        val stack = Stack(4)
        stack.push(0x10000u)
        stack.push(0x10200u)

        expectThat(stack.pop()).isEqualTo(0x200u)
        expectThat(stack.pop()).isEqualTo(0u)
    }

    @Test
    fun `push and pop multiple times`() {
        val stack = Stack(4)
        stack.push(1u)
        stack.push(2u)
        stack.push(3u)
        stack.push(4u)

        expectThat(stack.pop()).isEqualTo(4u)
        expectThat(stack.pop()).isEqualTo(3u)
        expectThat(stack.pop()).isEqualTo(2u)
        expectThat(stack.pop()).isEqualTo(1u)
    }

    @Test
    fun `push to full stack`() {
        val stack = Stack(4)
        stack.push(1u)
        stack.push(2u)
        stack.push(3u)
        stack.push(4u)
        expectThrows<IllegalStateException> { stack.push(5u) }
    }

    @Test
    fun `pop from empty stack`() {
        val stack = Stack(4)
        stack.push(1u)

        expectThat(stack.pop()).isEqualTo(1u)
        expectThrows<IllegalStateException> { stack.pop() }
    }

    @Test
    fun `Check toString correct with elements`() {
        val stack = Stack(4)
        stack.push(1u)
        stack.push(2u)
        stack.push(3u)
        stack.push(4u)

        val nl = System.lineSeparator()

        expectThat(stack.toString()).isEqualTo("\tSize = 4, SP = 0x04$nl" +
                "\t----------$nl" +
                "\t| 0x0004 | $nl" +
                "\t| 0x0003 | $nl" +
                "\t| 0x0002 | $nl" +
                "\t| 0x0001 | $nl" +
                "\t----------$nl")
    }

    @Test
    fun `Check toString correct without elements`() {
        val stack = Stack(4)

        val nl = System.lineSeparator()

        expectThat(stack.toString()).isEqualTo("\tSize = 4, SP = 0x00$nl")
    }
}