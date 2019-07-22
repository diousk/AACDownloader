package com.example.downloader

import android.net.Uri
import okhttp3.ResponseBody
import retrofit2.adapter.rxjava2.Result
import timber.log.Timber
import android.provider.SyncStateContract.Helpers.update
import java.io.*
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import android.text.TextUtils




object FileUtils {

    private const val DEFAULT_READ_BUFFER = 8192

    fun checkMD5(md5: String, updateFile: File?): Boolean {
        if (TextUtils.isEmpty(md5) || updateFile == null) {
            Timber.e("MD5 string empty or updateFile null")
            return false
        }

        val fileDigest = calculateMD5(updateFile)
        if (fileDigest == null) {
            Timber.e("calculatedDigest null")
            return false
        }

        Timber.d("file digest: $fileDigest")
        Timber.d("Provided digest: $md5")

        return fileDigest.equals(md5, ignoreCase = true)
    }

    fun calculateMD5(updateFile: File): String? {
        val digest: MessageDigest
        try {
            digest = MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            Timber.e("Exception while getting digest: $e")
            return null
        }

        val inputStream: InputStream
        try {
            inputStream = FileInputStream(updateFile)
        } catch (e: FileNotFoundException) {
            Timber.e("Exception while getting FileInputStream: $e")
            return null
        }

        val buffer = ByteArray(DEFAULT_READ_BUFFER)
        return inputStream.use { input ->
            while (true) {
                val read = input.read(buffer)
                if (read == -1) {
                    break
                }
                digest.update(buffer, 0, read)
            }
            val md5sum = digest.digest()
            val bigInt = BigInteger(1, md5sum)
            val output = bigInt.toString(16)
            // Fill to 32 chars
            String.format("%32s", output).replace(' ', '0')
        }
    }

    fun saveResponseBodyToFile(body: ResponseBody, filePath: String, isInterrupted: () -> Boolean): Boolean {
        val buffer = ByteArray(DEFAULT_READ_BUFFER)
        val totalBytes = body.contentLength()
        val inputStream: InputStream = body.byteStream()
        val outputStream: OutputStream = FileOutputStream(File(filePath))
        Timber.d("file totalBytes = $totalBytes")

        var success: Boolean
        try {
            var downloadedBytes = 0
            while (true) {
                if (isInterrupted()) {
                    Timber.w("file saving is interrupted")
                    break
                }

                val read = inputStream.read(buffer)
                if (read == -1) {
                    break
                }

                if (isInterrupted()) {
                    Timber.w("file saving is interrupted")
                    break
                }
                outputStream.write(buffer, 0, read)  // write bytes into file

                downloadedBytes += read
                if (totalBytes > 0) {
                    val progress = (downloadedBytes.toFloat() / totalBytes)
                    Timber.d("progress = $progress, $downloadedBytes/$totalBytes")
                }
            }
            success = !isInterrupted()
        } catch (e: Exception) {
            success = false
        } finally {
            try {
                body.close()
            } catch (ignore: Exception) {}
            try {
                outputStream.close()
            } catch (ignore: Exception) {}
            try {
                inputStream.close()
            } catch (ignore: Exception) {}
        }
        return success
    }


}