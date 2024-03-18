package core

import java.io.File

class DirManager {
    val tempProjectDir: File
        get() {
            val homeDir = System.getProperty("user.home")
            return File(homeDir, ".salamr")
        }

    val dumpFileLocation: String
        get() = "$tempProjectDir/window_dump.xml"

    private val recordedJsonFile: File
        get() = File(tempProjectDir, "recorded.json")

    fun getRecordedJsonFileText(): String {
        return recordedJsonFile.readText()
    }

    fun getRecordedInputFileText(file: String): String {
        val recordedFile = File("$tempProjectDir/${file}.json")
        return if (recordedFile.exists()) {
            recordedFile.readText()
        } else ""
    }

    fun validateTempDir() {
        if (!tempProjectDir.exists()) {
            tempProjectDir.mkdirs()
        }
    }

    fun writeToFile(text: String, fileName: String?) {
        if (fileName != null) {
            val file = File(tempProjectDir, "$fileName.json")
            file.writeText(text)
        } else {
            recordedJsonFile.writeText(text)
        }
    }

    fun deleteTempFolder() {
        if (tempProjectDir.exists()) {
            tempProjectDir.listFiles()?.forEach {
                it.delete()
            }
        }
    }

    fun deleteMultiple(files: String) {
        files.split(",").forEach {
            Logger.log("deleting file $it")
            val file = File("${tempProjectDir}/$it.json")
            if (file.exists()) {
                file.delete()
            }
        }
    }
}
