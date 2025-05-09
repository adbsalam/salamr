package play

import core.DirManager
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

class SnapshotManager(
    private val dirManager: DirManager = DirManager(),
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
    fun takeScreenshot(
        eventUUID: String,
        isIos: Boolean = false,
    ) {
        try {
            val screenshotFile = File(dirManager.snapshotDirectory, "$eventUUID.png")

            if (isIos) {
                // Use xcrun to take a screenshot from the iOS Simulator
                val process =
                    ProcessBuilder("xcrun", "simctl", "io", "booted", "screenshot", screenshotFile.absolutePath).start()
                process.waitFor()
            } else {
                // Use ADB to capture screenshot from Android device
                val process = ProcessBuilder("adb", "exec-out", "screencap", "-p").start()
                screenshotFile.outputStream().use { outputStream ->
                    process.inputStream.copyTo(outputStream)
                }
            }

            cropImageFixedHeight(screenshotFile, isIos)
        } catch (e: IOException) {
            println("Failed to take screenshot: ${e.message}")
        } catch (e: InterruptedException) {
            println("Screenshot process was interrupted: ${e.message}")
        }
    }

    /**
     * Remove status bar to avoid
     * We need to crop the status bar, otherwise simple things such as battery percentage change
     * Or time change from status bar will cause a comparison failure at verification level
     */
    private fun cropImageFixedHeight(
        imageFile: File,
        isIos: Boolean = false,
    ) {
        val image = ImageIO.read(imageFile)
        val croppedImage =
            if (topBarHeightInPxApprox < image.height) {
                image.getSubimage(0, getTopBarCropSize(isIos), image.width, image.height - getTopBarCropSize(isIos))
            } else {
                image
            }
        ImageIO.write(croppedImage, "png", imageFile)
    }

    fun getTopBarCropSize(isIos: Boolean): Int = if (!isIos) topBarHeightInPxApprox else topBarHeightInPxApprox + 20
}
