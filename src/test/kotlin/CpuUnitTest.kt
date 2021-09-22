import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import memory.MemoryManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@ExtendWith(MockKExtension::class)
class CpuUnitTest {
    @MockK private lateinit var memoryManager: MemoryManager
    @InjectMockKs private lateinit var cpu: Cpu

    @Test
    fun `Decode Clear Screen instruction`() {
        expectThat(cpu.decode(0x00E0.toUInt())).isEqualTo(cpu::clearScreen)
    }

    @Test
    fun `Decode Subroutine return instruction`() {
        expectThat(cpu.decode(0x00EE.toUInt())).isEqualTo(cpu::doReturn)
    }

    @Test
    fun `Decode Syscall instruction`() {
        expectThat(cpu.decode(0x0123.toUInt())).isEqualTo(cpu::sysCall)
    }

    @Test
    fun `Get Right most byte`() {
        expectThat(
            rightByte(0x4321.toUInt())).isEqualTo(0x21.toUByte()
        )
    }

    @Test
    fun `Get left Nibble`() {
        expectThat(
            leftNibble(0x4321.toUInt())).isEqualTo(0x4.toUByte()
        )
    }

    @Test
    fun `Get right Nibble`() {
        expectThat(
            rightNibble(0x4321.toUInt())).isEqualTo(0x1.toUByte()
        )
    }

    @Test
    fun `Get x value`() {
        expectThat(
            x(0x4321.toUInt())).isEqualTo(0x3.toUByte()
        )
    }

    @Test
    fun `Get y value`() {
        expectThat(
            y(0x4321.toUInt())).isEqualTo(0x2.toUByte()
        )
    }

    @Test
    fun `Get right nibble bytes`() {
        expectThat(
            rightNibbleByte(0x4321.toUInt())).isEqualTo(0x321.toUInt()
        )
    }
}