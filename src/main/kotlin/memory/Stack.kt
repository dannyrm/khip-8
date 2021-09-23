package memory

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
}