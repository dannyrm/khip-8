package com.github.dannyrm.khip8.config

data class MemoryConfig(val memorySize: Int,
                        val stackSize: Int,
                        val interpreterStartAddress: Int,
                        val programStartAddress: Int,
                        val numberOfGeneralPurposeRegisters: Int)