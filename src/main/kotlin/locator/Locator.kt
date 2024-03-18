package locator

import actionExecutor.ActionExecutor
import actionExecutor.ActionExecutorImpl
import actionExecutor.KeyEvent
import core.*
import core.Logger.log
import core.data.Coordinates
import help.showHelp
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.system.exitProcess

private const val TF = "TF"

class Locator(
    private val dirManager: DirManager = DirManager(),
    private val actionExecutor: ActionExecutor = ActionExecutorImpl(),
    private val xmlParser: XmlParser = XmlParserImpl()
) {
    /**
     * Runs the specified action based on the given element.
     * @param element the element to perform the action on.
     */
    fun run(element: String?) {
        if (element.isNullOrEmpty()) {
            showHelp("no args passed for -l, -l requires at least 1 arg, see below for usage")
            exitWithError("no args passed for -l")
        }

        dirManager.validateTempDir()
        actionExecutor.createScreenDump()
        log("finding node $element..")

        val windowDumpDoc = xmlParser.createDocFromDumpFile(dirManager.dumpFileLocation)

        val nodeList: NodeList = windowDumpDoc.getElementsByTagName("node")

        if (element.startsWith(TF)) {
            handleTextField(nodeList, element)
        } else {
            handleTapElement(nodeList, element)
        }
    }

    /**
     * Handles the action for text field elements.
     * @param nodes the list of XML nodes representing UI elements.
     * @param element the text field element.
     */
    private fun handleTextField(nodes: NodeList, element: String) {
        val (index, text) = parseTextField(element)
        findNodeByClassName(nodes, "android.widget.EditText", index)?.let { node ->
            val bounds = node.getAttribute("bounds")
            val coordinates = parseBounds(bounds)
            coordinates?.let { actionExecutor.tap(it.x, it.y) }
            actionExecutor.sendText(text, Duration(1.0))
            actionExecutor.sendKeyEvent(KeyEvent.ForwardKey)
        } ?: exitWithError("cannot find element $element")
    }

    /**
     * Handles the action for tap elements.
     * @param nodes the list of XML nodes representing UI elements.
     * @param element the tap element.
     */
    private fun handleTapElement(nodes: NodeList, element: String) {
        val (index, item) = parseElement(element)

        findNodeByText(nodes, item, index)?.let { node ->
            log("element found: $item")
            val bounds = node.getAttribute("bounds")
            val coordinates = parseBounds(bounds)
            coordinates?.let { actionExecutor.tap(it.x, it.y) }
        } ?: exitWithError("node not found at index")
    }

    /**
     * Parses the text field element.
     * @param element the text field element string.
     * @return a pair containing index and text.
     */
    private fun parseTextField(element: String): Pair<Int, String> {
        val regex = Regex("TF\\[(\\d+)](?:\\((.*?)\\))?")
        val matchResult = regex.find(element) ?: exitWithError("invalid usage of TF")
        val index = matchResult.groupValues[1].toIntOrNull() ?: exitWithError("invalid usage of TF")
        val text = matchResult.groupValues[2].ifEmpty { exitWithError("invalid usage of TF") }
        return index to text
    }

    /**
     * Parses the tap element.
     * @param element the tap element string.
     * @return a pair containing index and item.
     */
    private fun parseElement(element: String): Pair<Int, String> {
        val regex = Regex("^(.+?)(?:\\[(\\d+)]|)$")
        val matchResult = regex.find(element) ?: exitWithError("invalid element format")
        val item = matchResult.groupValues[1]
        val index = matchResult.groupValues[2].toIntOrNull() ?: 0
        return index to item
    }

    /**
     * Finds a node by its class name.
     * @param nodes the list of XML nodes representing UI elements.
     * @param className the class name to search for.
     * @param index the index of the element.
     * @return the found XML node element.
     */
    private fun findNodeByClassName(nodes: NodeList, className: String, index: Int): Element? {
        var textFieldCount = 0

        for (i in 0 until nodes.length) {
            val node = nodes.item(i) as Element
            if (node.getAttribute("class") == className) {
                if (textFieldCount == index) {
                    return node
                }
                textFieldCount++
            }
        }
        return null
    }

    /**
     * Finds a node by its text.
     * @param nodes the list of XML nodes representing UI elements.
     * @param text the text to search for.
     * @param index the index of the element.
     * @return the found XML node element.
     */
    private fun findNodeByText(nodes: NodeList, text: String, index: Int): Element? {
        var textFieldCount = 0

        for (i in 0 until nodes.length) {
            val node = nodes.item(i) as Element
            if (node.getAttribute("text") == text) {
                if (textFieldCount == index) {
                    return node
                }
                textFieldCount++
            }
        }
        return null
    }

    /**
     * Exits the application with an error message.
     * @param message the error message to display.
     */
    private fun exitWithError(message: String): Nothing {
        actionExecutor.systemExit.exitWithHelp(message)
    }

    /**
     * Parses the bounds string into coordinates.
     * @param bounds the bounds string.
     * @return the parsed coordinates.
     */
    private fun parseBounds(bounds: String): Coordinates? {
        val pattern = Regex("\\[(\\d+),(\\d+)]\\[(\\d+),(\\d+)]")
        val matchResult = pattern.find(bounds)
        val (x1, y1, x2, y2) = matchResult?.destructured ?: return null
        val x = (x1.toInt() + x2.toInt()) / 2
        val y = (y1.toInt() + y2.toInt()) / 2
        return Coordinates(x, y)
    }
}
