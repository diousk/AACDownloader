package com.example.downloader

import android.net.Uri
import okhttp3.ResponseBody
import retrofit2.adapter.rxjava2.Result
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

object FileUtils {
    lateinit var filesDirPath: String
    lateinit var cacheDirPath: String

    fun getSavePath(url: String, privateFile: Boolean = false) : String {
        val name = getNameFromUrl(url)
        return if (privateFile) {
            filesDirPath + File.separator + name
        } else {
            cacheDirPath + File.separator + name
        }
    }

    fun getNameFromUrl(url: String) : String? {
        return Uri.parse(url).lastPathSegment
    }

    fun isFileExists(filePath: String) : Boolean = File(filePath).exists()

    fun deleteFile(filePath: String) = File(filePath).delete()

    fun saveToFile(body: ResponseBody, filePath: String) {
        // process file
        if (isFileExists(filePath)) {
            Timber.w("isFileExists")
            deleteFile(filePath)
        }

        val buffer = ByteArray(DEFAULT_BUFFER)
        var downloadedBytes = 0
        val totalBytes = body.contentLength()
        Timber.d("totalBytes = $totalBytes")

        val inputStream: InputStream = body.byteStream()
        val outputStream: OutputStream = FileOutputStream(File(filePath))

        while (true) {
            val read = inputStream.read(buffer)
            if (read == -1) {
                break
            }
            outputStream.write(buffer, 0, read)  // write bytes into file

            downloadedBytes += read
            val progress = (downloadedBytes.toFloat() / totalBytes)
            Timber.d("progress = $progress, $downloadedBytes/$totalBytes")
        }

        body.close()
        outputStream.close()
        inputStream.close()
    }

    private const val DEFAULT_BUFFER = 8192
}