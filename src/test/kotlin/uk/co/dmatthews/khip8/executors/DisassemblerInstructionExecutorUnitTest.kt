package uk.co.dmatthews.khip8.executors

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@ExtendWith(MockKExtension::class)
class DisassemblerInstructionExecutorUnitTest {
    @InjectMockKs private lateinit var disassemblerInstructionExecutor: DissassemblerInstructionExecutor

    @Test
    fun `Sys call instruction`() {
        disassemblerInstructionExecutor.sysCall(0x0F6Du)
        expectThat(disassemblerInstructionExecutor.codeListing.size).isEqualTo(1)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("SYS F6D")
    }

    // TODO All the rest of the disassembler implementation
}