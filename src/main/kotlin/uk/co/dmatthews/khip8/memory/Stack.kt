package uk.co.dmatthews.khip8.memory

import toHex
import wordHex

@OptIn(ExperimentalUnsignedTypes::class)
data class Stack(private val stackSize: Int,
                 var sp: Int = 0, // 8 bits, stack pointer, represented as an int for simplicity
                 private val stack: UIntArray = UIntArray(stackSize)) {
    fun push(value: UInt) {
        if (sp+1 > stackSize) {
            throw IllegalStateException("Attempting to push to a full stack")
        }
        stack[sp++] = (value % 0x10000u) // 16 bit values
    }

    fun pop(): UInt {
        if (sp-1 < 0) {
            throw IllegalStateException("Attempting to pop from an empty stack")
        }
        return stack[--sp]
    }

    fun clear() {
        sp = 0

        for (i in stack.indices) {
            stack[i] = 0u
        }
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()

        val stackDelimiters = "\t----------${System.lineSeparator()}"

        stringBuilder.append("\tSize = $stackSize, SP = ${toHex(sp.toUByte())}", System.lineSeparator())

        if (sp > 0) {
            stringBuilder.append(stackDelimiters)

            for(i in sp-1 downTo 0) {
                stringBuilder.append("\t| ${wordHex(stack[i])} | ${System.lineSeparator()}")
            }

            stringBuilder.append(stackDelimiters)
        }

        return stringBuilder.toString()
    }
}