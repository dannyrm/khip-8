package uk.co.dmatthews.khip8.sound

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.sound.midi.*

class SoundGeneratorUnitTest {

    @BeforeEach
    fun setup() {

    }

    @Test
    fun `Check sound generator initialised correctly`() {
        val midiSynthesiser = mockk<Synthesizer>(relaxed = true)
        val midiChannel = mockk<MidiChannel>(relaxed = true)
        val soundbank = mockk<Soundbank>()
        val instrument = mockk<Instrument>()

        mockkStatic(MidiSystem::class)
        every { MidiSystem.getSynthesizer() } returns midiSynthesiser
        every { midiSynthesiser.channels } returns arrayOf(midiChannel)
        every { midiSynthesiser.defaultSoundbank } returns soundbank
        every { soundbank.instruments } returns arrayOf(instrument)

        SoundGenerator()

        verify { midiSynthesiser.open() }
        verify { midiSynthesiser.loadInstrument(instrument) }
    }

    @Test
    fun `Check sound generator switches sound on when instructed`() {
        val midiSynthesiser = mockk<Synthesizer>(relaxed = true)
        val midiChannel = mockk<MidiChannel>(relaxed = true)
        val soundbank = mockk<Soundbank>()
        val instrument = mockk<Instrument>()

        mockkStatic(MidiSystem::class)
        every { MidiSystem.getSynthesizer() } returns midiSynthesiser
        every { midiSynthesiser.channels } returns arrayOf(midiChannel)
        every { midiSynthesiser.defaultSoundbank } returns soundbank
        every { soundbank.instruments } returns arrayOf(instrument)

        val soundGenerator = SoundGenerator()

        soundGenerator.start()

        verify { midiChannel.noteOn(60, 100) }
        verify(inverse = true) { midiChannel.noteOff(any()) }
    }

    @Test
    fun `Check sound generator switches sound off when instructed`() {
        val midiSynthesiser = mockk<Synthesizer>(relaxed = true)
        val midiChannel = mockk<MidiChannel>(relaxed = true)
        val soundbank = mockk<Soundbank>()
        val instrument = mockk<Instrument>()

        mockkStatic(MidiSystem::class)
        every { MidiSystem.getSynthesizer() } returns midiSynthesiser
        every { midiSynthesiser.channels } returns arrayOf(midiChannel)
        every { midiSynthesiser.defaultSoundbank } returns soundbank
        every { soundbank.instruments } returns arrayOf(instrument)

        val soundGenerator = SoundGenerator()

        soundGenerator.stop()

        verify { midiChannel.noteOff(60) }
        verify(inverse = true) { midiChannel.noteOn(any(), any()) }
    }
}