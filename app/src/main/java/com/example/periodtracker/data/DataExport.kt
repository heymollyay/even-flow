package com.example.periodtracker.data

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.periodtracker.EncryptionManager
import java.io.File
import java.io.FileWriter

object DataExport {

    fun exportToCSV(context: Context, entries: List<CycleData>): Boolean {
        return try {
            val csvContent = buildCSV(entries)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ use MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, "evenflow_export.csv")
                    put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    resolver.openOutputStream(it)?.use { stream ->
                        stream.write(csvContent.toByteArray())
                    }
                    contentValues.clear()
                    contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                    resolver.update(it, contentValues, null, null)
                }
                true
            } else {
                // Android 9 and below
                val downloadsDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                )
                val file = File(downloadsDir, "evenflow_export.csv")
                FileWriter(file).use { it.write(csvContent) }
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun buildCSV(entries: List<CycleData>): String {
        val sb = StringBuilder()
        sb.appendLine("Date,Flow,Notes")
        entries.forEach { entry ->
            val date = try { EncryptionManager.decrypt(entry.encryptedDate) } catch (e: Exception) { "" }
            val flow = try { EncryptionManager.decrypt(entry.encryptedFlow) } catch (e: Exception) { "" }
            val notes = try { EncryptionManager.decrypt(entry.encryptedNotes) } catch (e: Exception) { "" }
            // wrap notes in quotes to handle commas in text
            sb.appendLine("$date,$flow,\"$notes\"")
        }
        return sb.toString()
    }
}