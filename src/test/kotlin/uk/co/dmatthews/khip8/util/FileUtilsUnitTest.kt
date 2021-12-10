package uk.co.dmatthews.khip8.util

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.endsWith
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue
import strikt.assertions.startsWith
import java.io.File
import java.nio.file.Paths
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.io.path.isDirectory

class FileUtilsUnitTest {

    @Test
    fun `Save to disk writes data to disk`() {
        val file = File.createTempFile("saveDiskUTPrefix", "saveDiskUTSuffix")

        try {
            val theFile = saveToDisk(file, "to save data")

            expectThat(file.readText()).isEqualTo("to save data")
            expectThat(theFile).isEqualTo(file)
        } finally {
            file.delete()
        }
    }

    @Test
    fun `Construct date time based file name`() {
        val dateTime = LocalDateTime.of(2019, 10, 15, 16, 25, 45, 4)

        val clock = Clock.fixed(dateTime.toInstant(ZoneOffset.UTC), ZoneId.systemDefault())

        val fileName = constructDateTimeFileName("unitTestName", clock)
        expectThat(fileName).isEqualTo("unitTestName-2019-10-15T17-25-45-000000004")
    }

    @Test
    fun `Construct date time based file name with default time`() {
        val fileName = constructDateTimeFileName("unitTestName")
        expectThat(fileName).startsWith("unitTestName-")
    }

    @Test
    fun `Current directory`() {
        expectThat(currentDirectory().isDirectory()).isTrue()
    }

    @Test
    fun `Save memory dump`() {
        var file: File? = null

        try {
            file = memoryDump("this is saved")

            expectThat(file.isFile).isTrue()
            expectThat(file.readText()).isEqualTo("this is saved")
            expectThat(file.name).startsWith("memory-dump")
            expectThat(file.name).endsWith(".txt")
        } finally {
            file?.delete()
        }
    }

    @Test
    fun `Save to disk with optional parameters specified`() {
        var file: File? = null

        fun newFileNameSuffixFunction(p: String): String = "$p test suffix func"

        try {
            file = saveContentToDisk(fileName = "test-file-name", toSave = "to save data",
                                     fileNameSuffixFunction = ::newFileNameSuffixFunction,
                                     directory = Paths.get(System.getProperty("user.dir")),
                                     fileExtension = "doc")

            expectThat(file.readText()).isEqualTo("to save data")
            expectThat(file.name).isEqualTo("test-file-name test suffix func.doc")
        } finally {
            file?.delete()
        }
    }
}