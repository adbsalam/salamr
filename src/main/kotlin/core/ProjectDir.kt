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

    fun validateTempDir() {
        if (!tempProjectDir.exists()) {
            tempProjectDir.mkdirs()
        }
    }

    fun writeToFile(text: String) {
        recordedJsonFile.writeText(text)
    }
}
