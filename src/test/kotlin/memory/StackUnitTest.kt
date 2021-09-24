package memory

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo

class StackUnitTest {

    @Test
    fun `push and pop`() {
        val stack = Stack(4)
        stack.push(1.toUInt())

        expectThat(stack.pop()).isEqualTo(1.toUInt())
    }

    @Test
    fun `push and pop multiple times`() {
        val stack = Stack(4)
        stack.push(1.toUInt())
        stack.push(2.toUInt())
        stack.push(3.toUInt())
        stack.push(4.toUInt())

        expectThat(stack.pop()).isEqualTo(4.toUInt())
        expectThat(stack.pop()).isEqualTo(3.toUInt())
        expectThat(stack.pop()).isEqualTo(2.toUInt())
        expectThat(stack.pop()).isEqualTo(1.toUInt())
    }

    @Test
    fun `push to full stack`() {
        val stack = Stack(4)
        stack.push(1.toUInt())
        stack.push(2.toUInt())
        stack.push(3.toUInt())
        stack.push(4.toUInt())
        expectThrows<IllegalStateException> { stack.push(5.toUInt()) }
    }

    @Test
    fun `pop from empty stack`() {
        val stack = Stack(4)
        stack.push(1.toUInt())

        expectThat(stack.pop()).isEqualTo(1.toUInt())
        expectThrows<IllegalStateException> { stack.pop() }
    }

    @Test
    fun `Check toString correct with elements`() {
        val stack = Stack(4)
        stack.push(1.toUInt())
        stack.push(2.toUInt())
        stack.push(3.toUInt())
        stack.push(4.toUInt())

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