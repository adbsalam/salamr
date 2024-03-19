package delete

import core.DirManager
import core.Logger
import help.showHelp
import java.io.File
import kotlin.system.exitProcess

private const val deleteAll = "all"

class Delete(
    private val dirManager: DirManager = DirManager()
) {

    /**
     * Runs the deletion process based on the provided arguments.
     * @param args the arguments passed to the function.
     */
    fun run(args: String?) {
        if (args.isNullOrEmpty()) {
            showHelp("no args passed for -l, -l requires at least 1 arg, see below for usage")
            exitProcess(1)
        }

        if (args.trim() == deleteAll) {
            Logger.log("deleting all files")
            if (dirManager.tempProjectDir.exists()) {
                dirManager.tempProjectDir.listFiles()?.forEach {
                    it.delete()
                }
            }
            exitProcess(1)
        }

        args.split(",").forEach {
            Logger.log("deleting file $it")
            val file = File("${dirManager.tempProjectDir}/$it.json")
            if (file.exists()) {
                file.delete()
            }
        }
    }
}