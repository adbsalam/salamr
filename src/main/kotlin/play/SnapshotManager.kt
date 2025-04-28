package play

import core.DirManager
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

class SnapshotManager(
    private val dirManager: DirManager = DirManager()
) {
    /**
     * Using 100 as approx height as we cannot get this from adb
     * This is because of edge to edge display, status bar becomes part of application area
     */
    private val topBarHeightInPxApprox = 100

    /**
     * Take snapshot for the current event
     * This is a snapshot of whole emulator screen that includes statusbar and bottom bar
     * We need to crop the status bar, otherwise simple things such as battery percentage change
     * Or time change from status bar will cause a comparison failure at verification level
     */
    fun takeScreenshot(eventUUID: String) {
        try {
            val process = ProcessBuilder("adb", "exec-out", "screencap", "-p").start()
            val screenshotFile = File(dirManager.snapshotDirectory, "$eventUUID.png")
            val outputStream = screenshotFile.outputStream()
            process.inputStream.copyTo(outputStream)
            outputStream.close()
            cropImageFixedHeight(screenshotFile)
        } catch (e: IOException) {
            println("Failed to take screenshot: ${e.message}")
        }
    }

    /**
     * Remove status bar to avoid
     * We need to crop the status bar, otherwise simple things such as battery percentage change
     * Or time change from status bar will cause a comparison failure at verification level
     */
    private fun cropImageFixedHeight(imageFile: File) {
        val image = ImageIO.read(imageFile)
        val croppedImage = if (topBarHeightInPxApprox < image.height) {
            image.getSubimage(0, topBarHeightInPxApprox, image.width, image.height - topBarHeightInPxApprox)
        } else {
            image
        }
        ImageIO.write(croppedImage, "png", imageFile)
    }
}