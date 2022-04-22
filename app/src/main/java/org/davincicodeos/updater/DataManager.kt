package org.davincicodeos.updater

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import java.io.FileDescriptor
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.name

object DataManager {
    fun getFiles(context: Context): Stream<Path> {
        val filesDir = context.filesDir
        return Files.walk(filesDir.toPath()).filter { item -> Files.isRegularFile(item) }
            .filter { item -> item.name.endsWith(".zip") }
    }

    fun createFileAndCopyFromFd(context: Context, fileName: String, fd: FileDescriptor) {
        val newFile = context.openFileOutput(fileName, MODE_PRIVATE)
        val inputStream = FileInputStream(fd)

        Log.i("DataManager", "Copying from $fileName to $newFile")
        val written = inputStream.copyTo(newFile, DEFAULT_BUFFER_SIZE)
        Log.i("DataManager", "Wrote $written bytes")

        inputStream.close()
        newFile.close()
    }

    fun deleteFile(context: Context, fileName: String) {
        context.deleteFile(fileName)
    }
}