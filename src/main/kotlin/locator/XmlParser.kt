package locator

import org.w3c.dom.Document
import javax.xml.parsers.DocumentBuilderFactory

class XmlParserImpl : XmlParser {
    override fun createDocFromDumpFile(dir: String): Document =
        DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .parse(dir)
}

interface XmlParser {
    fun createDocFromDumpFile(dir: String): Document
}
