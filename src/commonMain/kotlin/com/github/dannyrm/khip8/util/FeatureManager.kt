package com.github.dannyrm.khip8.util

import com.github.dannyrm.khip8.util.SystemDependentInstructionFeature.I_INCREMENT_FX55
import com.github.dannyrm.khip8.util.SystemDependentInstructionFeature.I_INCREMENT_FX65

object FeatureManager {
    var systemMode: SystemMode = SystemMode.SUPER_CHIP_MODE
    val systemIndependentFeatureMap = HashMap<SystemIndependentInstructionFeature, Boolean>()

    fun isEnabled(systemDependentInstructionFeature: SystemDependentInstructionFeature): Boolean {
        return systemMode.systemDependentInstructionFeatures.contains(systemDependentInstructionFeature)
    }
    fun isEnabled(feature: SystemIndependentInstructionFeature): Boolean {
        return systemIndependentFeatureMap[feature] ?: false
    }
}

enum class SystemMode(vararg val systemDependentInstructionFeatures: SystemDependentInstructionFeature) {
    CHIP_8_MODE(I_INCREMENT_FX55, I_INCREMENT_FX65),
    CHIP_48_MODE(I_INCREMENT_FX55, I_INCREMENT_FX65),
    SUPER_CHIP_MODE()
}

enum class SystemDependentInstructionFeature {
    I_INCREMENT_FX55,
    I_INCREMENT_FX65
}

enum class SystemIndependentInstructionFeature {
    SHIFT_RIGHT_INCLUDES_Y_8XY6
}
