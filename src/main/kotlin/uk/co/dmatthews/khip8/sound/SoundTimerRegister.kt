package uk.co.dmatthews.khip8.sound

import uk.co.dmatthews.khip8.memory.TimerRegister

// TODO: Write tests for all the sound stuff
class SoundTimerRegister(value: UByte = 0u,
                         private val soundGenerator: SoundGenerator = SoundGenerator()): TimerRegister(value) {

    override var value = value
        set (value) {
            field = value

            if (value > 0u) {
                soundGenerator.start()
            }
        }

    override fun tick() {
        super.tick()

        if (value <= 0u) {
            soundGenerator.stop()
        }
    }
}