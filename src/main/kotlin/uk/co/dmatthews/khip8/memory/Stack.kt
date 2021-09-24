package uk.co.dmatthews.khip8.memory

import toHex
import wordHex

@OptIn(ExperimentalUnsignedTypes::class)
data class Stack(private val stackSize: Int,
                 var SP: Int = 0, // 8 bits, stack pointer, represented as an int for simplicity
                 private val stack: UIntArray = UIntArray(stackSize)) {
    fun push(value: UInt) {
        if (SP+1 > stackSize) {
            throw IllegalStateException("Attempting to push to a full stack")
        }
        stack[SP++] = value
    }

    fun pop(): UInt {
        if (SP-1 < 0) {
            throw IllegalStateException("Attempting to pop from an empty stack")
        }
        return stack[--SP]
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()

        val stackDelimiters = "\t----------"

        stringBuilder.append("\tSize = $stackSize, SP = ${toHex(SP.toUByte())}")
        stringBuilder.append(System.lineSeparator())

        if (SP > 0) {
            stringBuilder.append(stackDelimiters)
            stringBuilder.append(System.lineSeparator())

            for(i in SP-1 downTo 0) {
                stringBuilder.append("\t| ${wordHex(stack[i])} | ${System.lineSeparator()}")
            }

            stringBuilder.append(stackDelimiters)
            stringBuilder.append(System.lineSeparator())
        }

        return stringBuilder.toString()
    }
}