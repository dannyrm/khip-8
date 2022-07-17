package com.github.dannyrm.khip8.config

enum class Khip8Property(val propertyName: String, val defaultValue: Int) {
    SPEED_CPU("system.speed.cpu", 540),
    SPEED_TIMER("system.speed.timer", 60),
    SPEED_DISPLAY_REFRESH("system.speed.display.refresh", 30),

    MEMORY_RAM_SIZE("system.memory.ram.size", 4096),
    MEMORY_STACK_SIZE("system.memory.stack.size", 16),
    MEMORY_INTERPRETER_START_ADDRESS("system.memory.interpreter.start.address", 0),
    MEMORY_PROGRAM_START_ADDRESS("system.memory.program.start.address", 512),
    MEMORY_PROGRAM_REGISTER_COUNT("system.memory.register.count", 16), // 16 registers, named Vx where x = 1...F
}
