package com.github.dannyrm.khip8.input

enum class KeyboardInput(val bitMask: UInt) {
    ZERO(bitMask = 0x1u),
    ONE(bitMask = 0x2u),
    TWO(bitMask = 0x4u),
    THREE(bitMask = 0x8u),
    FOUR(0x10u),
    FIVE(bitMask = 0x20u),
    SIX(bitMask = 0x40u),
    SEVEN(bitMask = 0x80u),
    EIGHT(bitMask = 0x100u),
    NINE(bitMask = 0x200u),
    A(bitMask = 0x400u),
    B(bitMask = 0x800u),
    C(bitMask = 0x1000u),
    D(bitMask = 0x2000u),
    E(bitMask = 0x8000u),
    F(bitMask = 0x10000u)
}
