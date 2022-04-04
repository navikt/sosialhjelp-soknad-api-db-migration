package no.nav.sosialhjelp.soknad.migration.utils

import java.io.StringReader
import java.io.StringWriter
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException
import javax.xml.bind.Marshaller
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

class JAXBHelper(vararg classes: Class<*>) {

    private var context: JAXBContext = try {
        JAXBContext.newInstance(*classes)
    } catch (e: JAXBException) {
        throw RuntimeException(e)
    }

    fun marshal(jaxbelement: Any?): String {
        return try {
            val writer = StringWriter()
            val marshaller = context.createMarshaller()
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true)
            marshaller.marshal(jaxbelement, StreamResult(writer))
            writer.toString()
        } catch (e: JAXBException) {
            throw RuntimeException(e)
        }
    }

    fun <T> unmarshal(melding: String, elementClass: Class<T>): T {
        return try {
            val value = context.createUnmarshaller().unmarshal(StreamSource(StringReader(melding)), elementClass)
            value.value
        } catch (e: JAXBException) {
            throw RuntimeException(e)
        }
    }
}
