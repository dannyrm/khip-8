package uk.co.dmatthews.khip8.util

import uk.co.dmatthews.khip8.util.InstructionFeature.FX55_I_INCREMENT
import uk.co.dmatthews.khip8.util.InstructionFeature.FX65_I_INCREMENT

object FeatureManager {
    var systemMode: SystemMode = SystemMode.SUPER_CHIP_MODE

    fun isEnabled(instructionFeature: InstructionFeature): Boolean {
        return systemMode.instructionFeatures.contains(instructionFeature)
    }
}

enum class SystemMode(vararg val instructionFeatures: InstructionFeature) {
    CHIP_8_MODE(FX55_I_INCREMENT, FX65_I_INCREMENT),
    CHIP_48_MODE(FX55_I_INCREMENT, FX65_I_INCREMENT),
    SUPER_CHIP_MODE()
}

enum class InstructionFeature {
    FX55_I_INCREMENT,
    FX65_I_INCREMENT
}