package currenciesRatesUpload;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement (name = "results")
public class Results {

    public Results(){}

    @XmlElement(name="rate")
    public List<Rate> rates = new ArrayList<Rate>();
    public int getQuantity (){
        return rates.size();
    }

}