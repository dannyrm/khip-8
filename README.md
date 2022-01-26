# Kotlin Chip-8 Interpreter

A Kotlin multi-platform Chip-8 interpreter. Currently only supports the JVM.

A number of Chip-8 games are available within the `resources/c8` directory.

## Loading ROM files

Currently, the loading of ROM files is achieved by providing them on the command line. E.g.

`java -jar ./Khip-8.jar src/resources/c8/TETRIS`

## Configuration

The interpreter can be configured by modifying the `src/jvmMain/resources/standard.json` JSON file. An example configuration follows:

```json
{
  "systemMode": "SUPER_CHIP_MODE",
  "systemSpeedConfig": {
    "cpuSpeed": 540,
    "timerSpeed": 60,
    "displayRefreshRate": 60
  },
  "soundConfig": {
    "midiInstrumentNumber": 0,
    "midiNoteNumber": 80,
    "midiNoteVelocity": 100
  },
  "memoryConfig": {
    "memorySize": 4096,
    "stackSize": 16,
    "interpreterStartAddress": 0,
    "programStartAddress": 512
  },
  "frontEndConfig": {
    "frontEnd": "JAVA_AWT"
  }
}
```
## Libraries

* [Koin](https://github.com/InsertKoinIO/koin) for dependency injection
* [Hoplite](https://github.com/sksamuel/hoplite) for configuration files
* [KLogger](https://github.com/korlibs/klogger) for logging
* [JUnit 5](https://github.com/junit-team/junit5), [MockK](https://github.com/mockk/mockk), [Strikt](https://github.com/robfletcher/strikt) and [Kotest](https://github.com/kotest/kotest) for testing
* [Java AWT](https://docs.oracle.com/javase/7/docs/api/java/awt/package-summary.html) for the UI

## Future Work

* Switch to [KorGE](https://github.com/korlibs/korge) for the UI
* Allow loading of ROM files from within the UI