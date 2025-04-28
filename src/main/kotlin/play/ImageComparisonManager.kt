package play

import com.github.romankh3.image.comparison.ImageComparison
import com.github.romankh3.image.comparison.model.ImageComparisonResult
import com.github.romankh3.image.comparison.model.ImageComparisonState
import core.DirManager
import java.io.File
import javax.imageio.ImageIO

class ImageComparisonManager(
    private val snapshotManager: SnapshotManager = SnapshotManager(),
    private val dirManager: DirManager = DirManager(),
    private val snapshotReportGenerator: SnapshotReportGenerator = SnapshotReportGenerator()
) {

    /**
     * Step 1: Create a name with prefix _generated such as asd123.png will generate name as asd123_generated.png
     * This is to match the correct generated image with the golden image
     *
     * Step 2: Take a screenshot of the current screen
     * This is the latest screen that we want to compare with the golden image
     *
     * Step 3: Compare the golden image and the latest image
     *
     * Step 4: If the images are not matching
     * Move Golden image and diff image into reports dir so we can use them to generate HTML reports
     *
     * @param id The id of the image to compare
     * @return ReportFile object containing the paths to the golden and diff images
     */
    fun compareImage(id: String): ReportFile? {
        val generatedImageId = "${id}_generated"
        snapshotManager.takeScreenshot(generatedImageId) // capture latest
        Thread.sleep(1000)

        val goldenImageFileName = File(dirManager.snapshotDirectory, "$id.png")
        if (!goldenImageFileName.exists()) {
            println("Golden image not found for this event $goldenImageFileName")
        }
        val generatedImageFileName = File(dirManager.snapshotDirectory, "$generatedImageId.png")
        if (!generatedImageFileName.exists()) {
            println("Failed to generate snapshot for comparison $generatedImageFileName")
        }
        val golden = ImageIO.read(goldenImageFileName)
        val latest = ImageIO.read(generatedImageFileName)

        val comparison = ImageComparison(latest, golden)
            .setDifferenceRectangleFilling(true, 0.5)
            .setRectangleLineWidth(3)
            .setThreshold(5)

        val result = comparison.compareImages()
        val icon = if (result.imageComparisonState == ImageComparisonState.MATCH) "✔\uFE0F" else "❌"
        println("Comparison result: $icon ${result.imageComparisonState}")
        if (result.imageComparisonState == ImageComparisonState.MISMATCH) {
            val reportFile = handleMisMatchResult(
                generatedImageId = generatedImageId,
                generatedImageFileName = generatedImageFileName,
                result = result
            )
            return reportFile
        }
        return null
    }

    /**
     * This completes step 4 of compareImage() chain
     *
     * Step 4: If the images are not matching
     * Move Golden image and diff image into reports dir so we can use them to generate HTML reports
     *
     * @param generatedImageId The id of the generated image
     * @param generatedImageFileName The file name of the generated image
     * @param result The result of the image comparison
     *
     * @return ReportFile object containing the paths to the golden and diff images
     */
    private fun handleMisMatchResult(
        generatedImageId: String,
        generatedImageFileName: File,
        result: ImageComparisonResult
    ): ReportFile {
        val failedImageFile = File(dirManager.reportDir, "$generatedImageId-report.png")

        snapshotReportGenerator.copyFileToReportDirectory(
            sourceFile = generatedImageFileName,
            reportDirectory = dirManager.reportDir
        )

        ImageIO.write(result.result, "png", failedImageFile)
        return ReportFile(
            goldenImage = failedImageFile,
            diffFile = File(dirManager.reportDir, "$generatedImageId.png")
        )
    }
}