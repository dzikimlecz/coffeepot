package me.dzikimlecz.coffeepot.transit

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.security.MessageDigest
import java.util.zip.ZipInputStream

infix fun File.unzipTo(outputDir: File) {
    ZipInputStream(inputStream()).use { stream ->
        var entry = stream.nextEntry
        while (entry != null) {
            val entryFile = File(outputDir, entry.name)
            if (entry.isDirectory) {
                entryFile.mkdirs()
            } else {
                entryFile.parentFile?.mkdirs()
                entryFile.writeBytes(stream.readBytes())
            }
            stream.closeEntry()
            entry = stream.nextEntry
        }
    }
}

fun sha256(file: File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val buffer = ByteArray(4194304) // 4 MiB
    file.inputStream().use {stream ->
        var bytesRead = stream.read(buffer)
        do {
            digest.update(buffer, 0, bytesRead)
            bytesRead = stream.read(buffer)
        } while(bytesRead != -1)
    }
    return buildString {
        digest.digest().map {
            val hex = Integer.toHexString(0xFF and it.toInt())
            if (hex.length == 1) "0$hex" else hex
        }.forEach(this::append)
    }
}

infix fun File.writeTo(to: File) = inputStream() writeTo to

infix fun InputStream.writeTo(to: File) {
    val buffer = ByteArray(4194304) // 4 MiB
    to.writeBytes(ByteArray(0))
    FileOutputStream(to, true).use { out ->
        this.use { input ->
            var bytesRead = input.read(buffer)
            do {
                if (bytesRead == buffer.size) {
                    out.write(buffer)
                } else {
                    out.write(buffer.sliceArray(0 until bytesRead))
                }
                bytesRead = input.read(buffer)
            } while(bytesRead != -1)
        }
    }
}


