package com.github.dannyrm.khip8.executors

import com.github.dannyrm.khip8.config.MemoryConfig
import com.github.dannyrm.khip8.cpu.InstructionDecoder
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.test.utils.TestFile
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.expect

class DissasemblerIntegrationTest {

    @Test
    fun `Check dissasembly of 15 puzzle`() {
        val memoryConfig =
            MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)
        val memoryManager = MemoryManager(soundRegister = mockk(), memoryConfig = memoryConfig)

        memoryManager.loadProgram(
            TestFile("inputs/15-puzzle.ch8", fromClasspath = true).getAbsolutePath()
        )

        execute(memoryManager, InstructionDecoder(), DissassemblerInstructionExecutor(),
            listOf(
                "CLS",
                "LD VC, 0",
                "SNE VC, 0",
                "LD VE, F",
                "LD I, 203",
                "LD V0, 20",
                "LD [I], V0",
                "CLS",
                "CALL 2BE",
                "CALL 276",
                "CALL 28E",
                "CALL 25E",
                "CALL 246",
                "JP 210",
                "LD V1, 0",
                "LD V2, 17",
                "LD V3, 4",
                "SNE V1, 10",
                "RET",
                "LD I, 2E8",
                "ADD I, V1",
                "LD V0, [I]",
                "SNE V0, 0",
                "JP 234",
                "LD F, V0",
                "DRW V2, V3, 5",
                "ADD V1, 1",
                "ADD V2, 5",
                "LD V4, 3",
                "AND V4, V1",
                "SE V4, 0",
                "JP 222",
                "LD V2, 17",
                "ADD V3, 6",
                "JP 222",
                "LD V4, 3",
                "AND V4, VE",
                "LD V5, 3",
                "AND V5, VD",
                "SNE V4, V5",
                "RET",
                "SNE V4, 3",
                "RET",
                "LD V4, 1",
                "ADD V4, VE",
                "CALL 2A6",
                "JP 246",
                "LD V4, 3",
                "AND V4, VE",
                "LD V5, 3",
                "AND V5, VD",
                "SNE V4, V5",
                "RET",
                "SNE V4, 0",
                "RET",
                "LD V4, FF",
                "ADD V4, VE",
                "CALL 2A6",
                "JP 25E",
                "LD V4, C",
                "AND V4, VE",
                "LD V5, C",
                "AND V5, VD",
                "SNE V4, V5",
                "RET",
                "SNE V4, 0",
                "RET",
                "LD V4, FC",
                "ADD V4, VE",
                "CALL 2A6",
                "JP 276",
                "LD V4, C",
                "AND V4, VE",
                "LD V5, C",
                "AND V5, VD",
                "SNE V4, V5",
                "RET",
                "SNE V4, C",
                "RET",
                "LD V4, 4",
                "ADD V4, VE",
                "CALL 2A6",
                "JP 28E",
                "LD I, 2E8",
                "ADD I, V4",
                "LD V0, [I]",
                "LD I, 2E8",
                "ADD I, VE",
                "LD [I], V0",
                "LD V0, 0",
                "LD I, 2E8",
                "ADD I, V4",
                "LD [I], V0",
                "LD VE, V4",
                "RET",
                "SE VC, 0",
                "JP 2D2",
                "CALL 21C",
                "CALL 2D8",
                "CALL 21C",
                "LD I, 2F8",
                "ADD I, VD",
                "LD V0, [I]",
                "LD VD, V0",
                "RET",
                "ADD VC, FF",
                "RND VD, F",
                "RET",
                "ADD VD, 1",
                "LD V0, F",
                "AND VD, V0",
                "SKP VD",
                "JP 2D8",
                "SKNP VD",
                "JP 2E2",
                "RET",
                // Probably sprite data
                "SYS 102",
                "SYS 304",
                "SYS 506",
                "SYS 708",
                "SYS 90A",
                "SYS B0C",
                "SYS D0E",
                "SYS F00",
                "SYS D00",
                "SYS 102",
                "SYS 405",
                "SYS 608",
                "SYS 90A",
                "SYS C0E",
                "SYS 307",
                "SYS B0F"))
    }

    private fun execute(memoryManager: MemoryManager, instructionDecoder: InstructionDecoder,
                        dissassemblerInstructionExecutor: DissassemblerInstructionExecutor,
                        expectedValues: List<String>) {
        for (i in expectedValues.indices) {
            instructionDecoder.decodeAndExecute(memoryManager.fetchNextInstruction(), instructionExecutors = listOf(dissassemblerInstructionExecutor))

            expect(expectedValues[i]) { dissassemblerInstructionExecutor.codeListing[i] }
        }
    }
}