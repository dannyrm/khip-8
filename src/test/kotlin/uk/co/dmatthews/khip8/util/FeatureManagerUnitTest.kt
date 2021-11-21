package uk.co.dmatthews.khip8.util

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class FeatureManagerUnitTest {

    @Test
    fun `Super chip mode instruction features`() {
        FeatureManager.systemMode = SystemMode.SUPER_CHIP_MODE

        expectThat(FeatureManager.isEnabled(SystemDependentInstructionFeature.I_INCREMENT_FX55)).isFalse()
        expectThat(FeatureManager.isEnabled(SystemDependentInstructionFeature.I_INCREMENT_FX65)).isFalse()
    }

    @Test
    fun `Chip 8 mode instruction features`() {
        FeatureManager.systemMode = SystemMode.CHIP_8_MODE

        expectThat(FeatureManager.isEnabled(SystemDependentInstructionFeature.I_INCREMENT_FX55)).isTrue()
        expectThat(FeatureManager.isEnabled(SystemDependentInstructionFeature.I_INCREMENT_FX65)).isTrue()
    }

    @Test
    fun `Chip 48 mode instruction features`() {
        FeatureManager.systemMode = SystemMode.CHIP_48_MODE

        expectThat(FeatureManager.isEnabled(SystemDependentInstructionFeature.I_INCREMENT_FX55)).isTrue()
        expectThat(FeatureManager.isEnabled(SystemDependentInstructionFeature.I_INCREMENT_FX65)).isTrue()
    }
}