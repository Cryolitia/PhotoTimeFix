package me.singleneuron.common.helper

import com.drew.imaging.ImageMetadataReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File

actual suspend fun getExif(path: String) = flow {
    try {
        val metadata = ImageMetadataReader.readMetadata(File(path))
        for (directory in metadata.directories) {
            try {
                for (tag in directory.tags) {
                    try {
                        emit(directory.name to "${tag.tagName} = ${tag.description}")
                    } catch (e: Exception) {
                        emit("Error" to e.toString())
                    }
                }
            } catch (e: Exception) {
                emit("Error" to e.toString())
            }
        }
    } catch (e: Exception) {
        emit("Error" to e.toString())
    }
}.flowOn(Dispatchers.IO)