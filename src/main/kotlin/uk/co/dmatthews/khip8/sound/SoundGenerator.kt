package uk.co.dmatthews.khip8.sound

import javax.sound.midi.MidiChannel
import javax.sound.midi.MidiSystem

/**
 * Thanks go to this answer on StackOverflow for helping tremendously:
 * https://stackoverflow.com/a/36466737
 */
class SoundGenerator {
    private val midiChannel: MidiChannel

    init {
        val midiSynthesizer = MidiSystem.getSynthesizer()
        midiSynthesizer.open()

        val instruments = midiSynthesizer.defaultSoundbank.instruments
        midiChannel = midiSynthesizer.channels[0]

        midiSynthesizer.loadInstrument(instruments[MIDI_INSTRUMENT_NUMBER])
    }

    fun start() {
        midiChannel.noteOn(MIDI_NOTE_NUMBER, MIDI_NOTE_VELOCITY)
    }

    fun stop() {
        midiChannel.noteOff(MIDI_NOTE_NUMBER)
    }

    companion object {
        private const val MIDI_INSTRUMENT_NUMBER = 0
        private const val MIDI_NOTE_NUMBER = 60
        private const val MIDI_NOTE_VELOCITY = 100
    }
}