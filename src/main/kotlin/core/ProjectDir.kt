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

    val coordinatesFile: String
        get() = "$tempProjectDir/recorded.json"


    fun validateTempDir() {
        if (!tempProjectDir.exists()) {
            tempProjectDir.mkdirs()
        }
    }
}
