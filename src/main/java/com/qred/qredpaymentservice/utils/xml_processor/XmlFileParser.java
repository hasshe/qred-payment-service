package com.qred.qredpaymentservice.utils.xml_processor;

import com.qred.qredpaymentservice.service.domain.DomainPayment;
import com.qred.qredpaymentservice.utils.xml_processor.records.XmlPaymentList;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class XmlFileParser {

    public static List<DomainPayment> processXmlFile(MultipartFile file, String clientId) throws IOException, JAXBException {
        String xmlContent = new String(file.getBytes());

        JAXBContext jaxbContext = JAXBContext.newInstance(XmlPaymentList.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        XmlPaymentList paymentList = (XmlPaymentList) unmarshaller.unmarshal(new StringReader(xmlContent));

        var xmlPayments = paymentList.getPayments();
        return xmlPayments.stream().map(xmlPayment -> DomainPayment.toDomain(xmlPayment, clientId)).toList();
    }
}
