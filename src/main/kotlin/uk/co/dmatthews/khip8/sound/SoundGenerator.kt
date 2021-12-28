package uk.co.dmatthews.khip8.sound

import uk.co.dmatthews.khip8.config.SoundConfig
import javax.sound.midi.MidiChannel
import javax.sound.midi.MidiSystem

/**
 * Thanks go to this answer on StackOverflow for helping tremendously:
 * https://stackoverflow.com/a/36466737
 */
class SoundGenerator(private val soundConfig: SoundConfig) {
    private val midiChannel: MidiChannel

    init {
        val midiSynthesizer = MidiSystem.getSynthesizer()
        midiSynthesizer.open()

        val instruments = midiSynthesizer.defaultSoundbank.instruments
        midiChannel = midiSynthesizer.channels[0]

        midiSynthesizer.loadInstrument(instruments[soundConfig.midiInstrumentNumber])
    }

    fun start() {
        midiChannel.noteOn(soundConfig.midiNoteNumber, soundConfig.midiNoteVelocity)
    }

    fun stop() {
        midiChannel.noteOff(soundConfig.midiNoteNumber)
    }
}