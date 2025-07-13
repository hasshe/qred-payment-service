package com.qred.qredpaymentservice.utils.xml_processor.records;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "payments")
public class XmlPaymentList {

    private List<XmlPayment> payments;

    public XmlPaymentList() {
    }

    public XmlPaymentList(List<XmlPayment> payments) {
        this.payments = payments;
    }

    @XmlElement(name = "payment")
    public List<XmlPayment> getPayments() {
        return payments;
    }

    public void setPayments(List<XmlPayment> payments) {
        this.payments = payments;
    }
}