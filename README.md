# Kotlin Chip-8 Interpreter

A Chip-8 interpreter written in Kotlin.

A number of Chip-8 games are available within the `src/main/resources/games/c8` directory.

## Loading ROM files

Currently, the loading of ROM files is achieved by providing them on the command line. E.g.

`java -jar ./Khip-8.jar src/main/resources/games/c8/TETRIS`

## Configuration

The interpreter can be configured by modifying the `src/main/resources/standard.json` JSON file. An example configuration follows:

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
  }
}
```
## Libraries

* [Koin](https://github.com/InsertKoinIO/koin) for dependency injection
* [Hoplite](https://github.com/sksamuel/hoplite) for configuration files
* [Logback](https://github.com/qos-ch/logback) for logging
* [JUnit 5](https://github.com/junit-team/junit5), [MockK](https://github.com/mockk/mockk) and [Strikt](https://github.com/robfletcher/strikt) for testing