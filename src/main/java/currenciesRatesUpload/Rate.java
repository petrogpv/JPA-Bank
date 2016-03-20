package currenciesRatesUpload;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "rate")
public class Rate {

    @XmlAttribute
    String id;
    @XmlElement
    String Name;
    @XmlElement
    double Rate;
    @XmlElement
    String Date;
    @XmlElement
    String Time;
    @XmlElement
    String Ask;
    @XmlElement
    String Bid;

    @Override
    public String toString() {
        return "Pair: " + id + "\nRate: " + Rate + "\nAsk: " + Ask + "\nBid: " + Bid;
    }

    public Rate(){}
}
