package ios

import core.DirManager
import core.Logger.log
import core.SnapshotArgs
import ios.actionExecutor.IOSActionExecutor
import ios.actionExecutor.IOSActionExecutorImpl
import ios.core.IOSEventType
import play.ImageComparisonManager
import play.ReportFile
import play.SnapshotManager
import play.SnapshotReportGenerator

class IOSPlay(
    private val simulatorCoordinateConverter: SimulatorCoordinateConverter = SimulatorCoordinateConverter(),
    private val dirManager: DirManager = DirManager(),
    private val snapshotManager: SnapshotManager = SnapshotManager(),
    private val imageComparisonManager: ImageComparisonManager = ImageComparisonManager(),
    private val snapshotReportGenerator: SnapshotReportGenerator = SnapshotReportGenerator(),
    private val iosActionExecutor: IOSActionExecutor = IOSActionExecutorImpl(),
) {
    private var failedSnapshots = mutableListOf<ReportFile>()

    fun run(snapshotArgs: SnapshotArgs?) {
        val events = getRecordedIOSEvents()
        println("\uD83C\uDFAC playing recorded inputs")
        val processedEvents = simulatorCoordinateConverter.mapCoordinatesToSimulatorOffset(events)
        executeCliClickCommands(processedEvents, snapshotArgs)
    }

    private fun getRecordedIOSEvents(): List<IOSEventType> {
        val json = dirManager.iosLogsFile.readText()
        val recordedEvents = IOSEventType.recordedEventAdapter.fromJson(json)
        if (recordedEvents.isNullOrEmpty()) {
            println("No recorded events found, did you forget to record?")
        }
        return recordedEvents ?: emptyList()
    }

    private fun executeCliClickCommands(
        events: List<IOSEventType>,
        snapshotArg: SnapshotArgs?,
    ) {
        var index = 1
        for (event in events) {
            index++
            iosActionExecutor.executeSimulatorEvent(event)
            handleSnapshotArgs(snapshotArg, event)
        }

        when (snapshotArg) {
            SnapshotArgs.Record -> log("snapshots recorded \uD83D\uDCF7")
            SnapshotArgs.Verify ->
                snapshotReportGenerator.generateHtmlFromReportFiles(failedSnapshots)

            else -> {}
        }
    }

    private fun handleSnapshotArgs(
        snapshotArg: SnapshotArgs?,
        event: IOSEventType,
    ) {
        when (snapshotArg) {
            SnapshotArgs.Record -> snapshotManager.takeScreenshot(event.uuid, isIos = true)
            SnapshotArgs.Verify ->
                try {
                    imageComparisonManager.compareImage(event.uuid, isIos = true)?.let {
                        failedSnapshots.add(it)
                    }
                } catch (e: Exception) {
                    println("Something went wrong while image comparison, did you forget to record snapshots?")
                }

            else -> {}
        }
    }
}
