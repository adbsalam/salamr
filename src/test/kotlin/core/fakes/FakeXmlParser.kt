package core.fakes

import locator.XmlParser
import org.w3c.dom.Document
import org.xml.sax.InputSource
import javax.xml.parsers.DocumentBuilderFactory

class FakeXmlParser : XmlParser {
    override fun createDocFromDumpFile(dir: String): Document {
        return createDocumentFromString(mockWindowDump)
    }

    private fun createDocumentFromString(xmlString: String): Document {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val inputSource = InputSource(xmlString.reader())
        return builder.parse(inputSource)
    }
}