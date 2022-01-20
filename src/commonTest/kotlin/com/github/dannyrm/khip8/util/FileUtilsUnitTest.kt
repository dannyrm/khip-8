package com.github.dannyrm.khip8.util

import com.github.dannyrm.khip8.multiplatform.*
import com.github.dannyrm.khip8.test.utils.*
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.expect

class FileUtilsUnitTest {

    @Test
    fun `Save to disk writes data to disk`() {
        val fileAbsolutePath = createTempFile("saveDiskUTPrefix", "saveDiskUTSuffix")

        try {
            val filePath = saveToDisk(fileAbsolutePath, "to save data")

            expect("to save data") { readFileAsString(fileAbsolutePath) }
            expect(fileAbsolutePath) { filePath }
        } finally {
            deleteFile(fileAbsolutePath)
        }
    }

//    @Test
//    fun `Construct date time based file name`() {
//        val dateTime = LocalDateTime.of(2019, 10, 15, 16, 25, 45, 4)
//
//        val clock = Clock.fixed(dateTime.toInstant(ZoneOffset.UTC), ZoneId.systemDefault())
//
//        val fileName = constructDateTimeFileName("unitTestName", clock)
//        expectThat(fileName).isEqualTo("unitTestName-2019-10-15T17-25-45-000000004")
//    }

    @Test
    fun `Construct date time based file name with default time`() {
        val fileName = constructDateTimeFileName("unitTestName")
        assertTrue { fileName.startsWith("unitTestName-") }
    }

    @Test
    fun `Current directory`() {
        assertTrue { isDirectory(currentDirectory()) }
    }

    @Test
    fun `Save memory dump`() {
        var file: FileAbsolutePath? = null

        try {
            file = createFile(memoryDump("this is saved"))

            assertTrue { isFile(file) }
            expect("this is saved") { readFileAsString(file) }
            assertTrue { getFileName(file).startsWith("memory-dump") }
            assertTrue { getFileName(file).endsWith(".txt") }
        } finally {
            file?.run { deleteFile(file) }
        }
    }

    @Test
    fun `Save to disk with optional parameters specified`() {
        var file: FileAbsolutePath? = null

        fun newFileNameSuffixFunction(p: String): String = "$p test suffix func"

        try {
            file = createFile(
                saveContentToDisk(
                    fileName = "test-file-name", toSave = "to save data",
                    fileNameSuffixFunction = ::newFileNameSuffixFunction,
                    directory = currentDirectory(),
                    fileExtension = "doc"
                )
            )

            expect("to save data") { readFileAsString(file) }
            expect("test-file-name test suffix func.doc") { getFileName(file) }
        } finally {
            file?.run { deleteFile(file) }
        }
    }
}