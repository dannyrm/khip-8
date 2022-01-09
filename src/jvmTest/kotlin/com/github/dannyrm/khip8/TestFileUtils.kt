package com.github.dannyrm.khip8

import java.io.File

object TestFileUtils {
    fun loadFile(fileName: String): File = File(loadResource(fileName))
    fun loadResource(name: String) = object {}.javaClass.classLoader.getResource(name).file
}