package currenciesRatesUpload;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Query {

    @XmlAttribute(name = "yahoo:count")
    int yahooCount;

    @XmlAttribute(name = "yahoo:created")
    String yahooCreated;

    @XmlAttribute(name = "yahoo:lang")
    int yahooLang;

    @XmlElement(name="results")
    public Results results;


}
