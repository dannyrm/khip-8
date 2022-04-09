package com.github.dannyrm.khip8.config

data class MemoryConfig(val memorySize: Int = 4096,
                        val stackSize: Int = 16,
                        val interpreterStartAddress: Int = 0,
                        val programStartAddress: Int = 512,
                        val numberOfGeneralPurposeRegisters: Int = 16) // 16 registers, named Vx where x = 1...F