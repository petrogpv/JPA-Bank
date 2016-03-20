package currenciesRatesUpload;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Currencies {

    public static Map<String,Double> getRates() throws JAXBException {

        String request = "http://query.yahooapis.com/v1/public/yql?format=xml&q=select" +
                "%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20" +
                "(\"USDUAH\",%20\"UAHUSD\",%20\"EURUAH\",%20\"UAHEUR\",%20\"EURUSD\",%20\"USDEUR\")" +
                "&env=store://datatables.org/alltableswithkeys";

        String answer = new String();

        try {
            answer = getXML(request);
//            System.out.println(answer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringReader sr = new StringReader(answer);
        JAXBContext jaxbContext = JAXBContext.newInstance(Query.class);

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        Query query = new Query();
        query = (Query) unmarshaller.unmarshal(sr);

        Map<String,Double> mapRates = new HashMap<String,Double>();

        for (Rate rate: query.results.rates) {
            mapRates.put(rate.Name,rate.Rate);
        }

        return mapRates;
    }
    public static String getXML(String urlToRead) throws Exception {

        StringBuilder result = new StringBuilder();

        URL url = new URL(urlToRead);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }
}

