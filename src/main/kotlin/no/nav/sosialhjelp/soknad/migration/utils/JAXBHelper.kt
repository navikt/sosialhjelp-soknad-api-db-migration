package no.nav.sosialhjelp.soknad.migration.utils

import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.JAXBException
import jakarta.xml.bind.Marshaller
import jakarta.xml.bind.annotation.adapters.XmlAdapter
import java.io.StringReader
import java.io.StringWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.time.temporal.TemporalQuery
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

class LocalDateTimeXmlAdapter : XmlAdapter<String, LocalDateTime>() {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val temporalQuery: TemporalQuery<LocalDateTime> = TemporalQuery { temporal: TemporalAccessor -> LocalDateTime.from(temporal) }

    override fun unmarshal(stringValue: String): LocalDateTime {
        return formatter.parse(stringValue, temporalQuery)
    }

    override fun marshal(value: LocalDateTime): String {
        return formatter.format(value)
    }
}
