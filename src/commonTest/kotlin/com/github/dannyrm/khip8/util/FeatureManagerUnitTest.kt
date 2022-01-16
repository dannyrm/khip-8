package com.github.dannyrm.khip8.util

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FeatureManagerUnitTest {

    @Test
    fun `Super chip mode instruction features`() {
        FeatureManager.systemMode = SystemMode.SUPER_CHIP_MODE

        assertFalse { FeatureManager.isEnabled(SystemDependentInstructionFeature.I_INCREMENT_FX55) }
        assertFalse { FeatureManager.isEnabled(SystemDependentInstructionFeature.I_INCREMENT_FX65) }
    }

    @Test
    fun `Chip 8 mode instruction features`() {
        FeatureManager.systemMode = SystemMode.CHIP_8_MODE

        assertTrue { FeatureManager.isEnabled(SystemDependentInstructionFeature.I_INCREMENT_FX55) }
        assertTrue { FeatureManager.isEnabled(SystemDependentInstructionFeature.I_INCREMENT_FX65) }
    }

    @Test
    fun `Chip 48 mode instruction features`() {
        FeatureManager.systemMode = SystemMode.CHIP_48_MODE

        assertTrue { FeatureManager.isEnabled(SystemDependentInstructionFeature.I_INCREMENT_FX55) }
        assertTrue { FeatureManager.isEnabled(SystemDependentInstructionFeature.I_INCREMENT_FX65) }
    }

    @Test
    fun `System independent instruction feature is on`() {
        FeatureManager.systemIndependentFeatureMap[SystemIndependentInstructionFeature.SHIFT_RIGHT_INCLUDES_Y_8XY6] = true

        assertTrue { FeatureManager.isEnabled(SystemIndependentInstructionFeature.SHIFT_RIGHT_INCLUDES_Y_8XY6) }
    }

    @Test
    fun `System independent instruction feature is off`() {
        FeatureManager.systemIndependentFeatureMap[SystemIndependentInstructionFeature.SHIFT_RIGHT_INCLUDES_Y_8XY6] = false

        assertFalse { FeatureManager.isEnabled(SystemIndependentInstructionFeature.SHIFT_RIGHT_INCLUDES_Y_8XY6) }
    }

    @Test
    fun `System independent instruction feature is off is not in the map`() {
        FeatureManager.systemIndependentFeatureMap.remove(SystemIndependentInstructionFeature.SHIFT_RIGHT_INCLUDES_Y_8XY6)

        assertFalse { FeatureManager.isEnabled(SystemIndependentInstructionFeature.SHIFT_RIGHT_INCLUDES_Y_8XY6) }
    }
}