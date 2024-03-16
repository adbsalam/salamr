package locator

import core.ADBProcess
import core.DirManager
import core.Logger
import core.Logger.log
import core.data.Coordinates
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Locator helps locate and tap on single element on screen
 */
class Locator(
    private val adbProcess: ADBProcess = ADBProcess(),
    private val dirManager: DirManager = DirManager()
) {

    /**
     * get adb screen dump from emulator
     * filter by text from the dump file
     * once item is found, collect bounds on screen
     * use these bounds to tap on element
     */
    fun run(element: String) {
        dirManager.validateTempDir()
        adbProcess.startScreenDumpProcess()
        log("finding node $element..")

        val windowDumpDoc = DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .parse(dirManager.dumpFileLocation)

        val nodeList: NodeList = windowDumpDoc.getElementsByTagName("node")

        for (i in 0 until nodeList.length) {
            val node = nodeList.item(i) as Element
            val text = node.getAttribute("text")
            if (text == element) {
                log("element found: $text\"")
                val bounds = node.getAttribute("bounds")
                val coordinates = parseBounds(bounds)
                coordinates?.let { adbProcess.adbTapProcess(it.x, it.y) }
                break
            }
        }
    }

    /**
     * Convert bound string into coordinates
     */
    private fun parseBounds(bounds: String): Coordinates? {
        val pattern = Regex("\\[(\\d+),(\\d+)\\]\\[(\\d+),(\\d+)\\]")
        val matchResult = pattern.find(bounds)
        val (x1, y1, x2, y2) = matchResult?.destructured ?: return null
        val x = (x1.toInt() + x2.toInt()) / 2
        val y = (y1.toInt() + y2.toInt()) / 2
        return Coordinates(x, y)
    }
}
