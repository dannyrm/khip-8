package com.github.dannyrm.khip8.util

import com.github.dannyrm.khip8.test.utils.*
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.expect

class FileUtilsUnitTest {

    @Test
    fun `Save to disk writes data to disk`() {
        val file = TestFile("saveDiskUTPrefix", tempFile = true)

        try {
            val filePath = saveToDisk(file.getAbsolutePath(), "to save data")

            expect("to save data") { file.asString() }
            expect(file.getAbsolutePath()) { filePath }
        } finally {
            file.delete()
        }
    }

    @Test
    fun `Construct date time based file name`() {
        val fileName = constructDateTimeFileName("unitTestName")

        val fileNameRegex = Regex("unitTestName-\\d{4}-\\d{2}-\\d{2}T\\d{2}-\\d{2}-\\d{2}-\\d{9}")

        assertTrue { fileName.matches(fileNameRegex) }
    }

    @Test
    fun `Construct date time based file name with default time`() {
        val fileName = constructDateTimeFileName("unitTestName")
        assertTrue { fileName.startsWith("unitTestName-") }
    }

    @Test
    fun `Current directory`() {
        assertTrue { TestFile(currentDirectory()).isDirectory() }
    }

    @Test
    fun `Save memory dump`() {
        var file: TestFile? = null

        try {
            file = TestFile(memoryDump("this is saved"))

            assertTrue { file.isFile() }
            expect("this is saved") { file.asString() }
            assertTrue { file.getFileName().startsWith("memory-dump") }
            assertTrue { file.getFileName().endsWith(".txt") }
        } finally {
            file?.run { file.delete() }
        }
    }

    @Test
    fun `Save to disk with optional parameters specified`() {
        var file: TestFile? = null

        fun newFileNameSuffixFunction(p: String): String = "$p test suffix func"

        try {
            file = TestFile(
                saveContentToDisk(
                    fileName = "test-file-name", toSave = "to save data",
                    fileNameSuffixFunction = ::newFileNameSuffixFunction,
                    directory = currentDirectory(),
                    fileExtension = "doc"
                )
            )

            expect("to save data") { file.asString() }
            expect("test-file-name test suffix func.doc") { file.getFileName() }
        } finally {
            file?.run { file.delete() }
        }
    }
}