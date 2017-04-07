package io.textback.azure.storage.blob.util;

import io.textback.azure.storage.blob.model.ErrorResponse;
import io.textback.azure.storage.blob.model.filter.NamespaceFilter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;
import java.io.ByteArrayInputStream;

public class XmlUtil {

    public static SAXSource convertToSaxSource(byte[] payload) throws SAXException {
        final XMLReader reader = XMLReaderFactory.createXMLReader();

        final NamespaceFilter inFilter = new NamespaceFilter(null, false);
        inFilter.setParent(reader);

        final InputSource inputSource = new InputSource(new ByteArrayInputStream(payload));

        return new SAXSource(inFilter, inputSource);
    }


    private static JAXBContext createJAXBContext() {
        try {
            return JAXBContext.newInstance(
                    ErrorResponse.class
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Marshaller createJaxbMarshaller() {
        try {
            final JAXBContext jaxbContext = createJAXBContext();
            final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            return jaxbMarshaller;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Unmarshaller createJaxbUnmarshaller() {
        try {
            final JAXBContext jaxbContext = createJAXBContext();
            return jaxbContext.createUnmarshaller();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
