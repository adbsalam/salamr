package play

import core.DirManager
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class SnapshotReportGenerator(
    private val dirManager: DirManager = DirManager(),
) {
    private fun String.escapeHtml(): String =
        this
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")

    /**
     * Generate HTML report for results
     * Success report of all snapshots were a match
     * Failure report if any snapshot was a mismatch
     * Reports generated as HTML files
     *
     * @param reportFiles List of ReportFile objects containing the paths to the golden and diff images
     */
    fun generateHtmlFromReportFiles(reportFiles: List<ReportFile>) {
        deleteAllGeneratedSnapshotsIfExist()
        val html =
            if (reportFiles.isEmpty()) {
                generateAllSnapshotsMatchedHtml()
            } else {
                generateFailedSnapshotsReport(reportFiles)
            }

        val reportFile = File(dirManager.reportDir, "report.html")
        reportFile.writeText(html)
        println(" \uD83D\uDCCA HTML report saved at: ${reportFile.absolutePath}")
        openReportInChrome()
    }

    /**
     * Generate HTML report for failed snapshots
     *
     * @param reportFiles List of ReportFile objects containing the paths to the golden and diff images
     */
    private fun generateFailedSnapshotsReport(reportFiles: List<ReportFile>): String {
        val rows =
            reportFiles.joinToString("\n") { report ->
                val goldenName = report.goldenImage.absolutePath.escapeHtml()
                val diffName = report.diffFile.absolutePath.escapeHtml()

                """
                <tr>
                    <td>
                        <div>$goldenName</div>
                        <img src="$goldenName" alt="Diff">
                    </td>
                    <td>
                        <div>$diffName</div>
                        <img src="$diffName" alt="Latest">
                    </td>
                </tr>
                """.trimIndent()
            }

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8" />
                <title>Image Diff Report</title>
                <style>
                    body { font-family: sans-serif; padding: 20px; }
                    table { width: 100%; border-collapse: collapse; }
                    th, td { border: 1px solid #ddd; padding: 10px; text-align: center; vertical-align: top; }
                    th { background-color: #f0f0f0; }
                    img { max-width: 100%; max-height: 400px; }
                    div { margin-bottom: 8px; font-size: 14px; font-weight: bold; }
                </style>
            </head>
            <body>
                <h1>🧪 Image Diff Report</h1>
                <table>
                    <thead>
                        <tr>
                            <th>Diff</th>
                            <th>Latest</th>
                        </tr>
                    </thead>
                    <tbody>
                        $rows
                    </tbody>
                </table>
            </body>
            </html>
            """.trimIndent()
    }

    /**
     * Generate HTML report indicating that all snapshots matched
     * This is a success report
     */
    private fun generateAllSnapshotsMatchedHtml(): String =
        """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8"/>
            <title>Snapshot Comparison</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    text-align: center;
                    padding: 50px;
                }
                h1 {
                    color: green;
                    font-size: 32px;
                }
            </style>
        </head>
        <body>
            <h1>✅ All Snapshots Matched</h1>
            <p>All the snapshots have been compared and are identical!</p>
        </body>
        </html>
        """.trimIndent()

    /**
     * Copy a file to the report directory
     * This is to keep golden or diff snapshots in reports folder so they can be added to report
     *
     * @param sourceFile The source file to copy
     * @param reportDirectory The destination directory where the file will be copied
     */
    fun copyFileToReportDirectory(
        sourceFile: File,
        reportDirectory: File,
    ) {
        if (!reportDirectory.exists()) {
            reportDirectory.mkdirs()
        }

        if (!sourceFile.isFile) {
            return
        }

        val destinationFile = File(reportDirectory, sourceFile.name)
        Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }

    /**
     * Delete all generated snapshots if they exist
     * This will be called when new test is started to clean up generated snapshots from snapshot directory
     */
    private fun deleteAllGeneratedSnapshotsIfExist() {
        dirManager.snapshotDirectory
            .listFiles { file ->
                file.isFile && file.name.matches(Regex(".*_generated.png"))
            }?.toList()
            ?.forEach {
                it.delete()
            }
    }

    private fun openReportInChrome() {
        try {
            val reportFile = File(System.getProperty("user.home"), ".salamr/report/report.html")
            ProcessBuilder("open", "-a", "Google Chrome", reportFile.absolutePath).start()
        } catch (e: Exception) {
            println("Failed to open report in Chrome: ${e.message}")
        }
    }
}

data class ReportFile(
    val goldenImage: File,
    val diffFile: File,
)
