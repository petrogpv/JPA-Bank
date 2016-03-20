import javax.persistence.*;

@Entity
@Table(name = "currency_rate")
@NamedQuery(name="CurrencyRate.findByName", query = "SELECT c FROM CurrencyRate c WHERE c.name = :name")
public class CurrencyRate {
    public CurrencyRate(){}

    public CurrencyRate(String name, double rate) {
        this.name = name;
        this.rate = rate;
    }

    @Id
    @GeneratedValue
    long id;
    @Column(nullable = false)
    String name;
    @Column(nullable = false)
    double rate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
    public static double getCurrencyRate (String currencyExchangeType, EntityManager em) {
        try {
            Query query = em.createNamedQuery("CurrencyRate.findByName", CurrencyRate.class);
            query.setParameter("name", currencyExchangeType);
            CurrencyRate rate = (CurrencyRate) query.getSingleResult();

            return rate.getRate();

        } catch (NoResultException ex) {
            System.out.println("CurrencyRate not found!");
            return 0;
        } catch (NonUniqueResultException ex) {
            System.out.println("Non unique CurrencyRate found!");
            return 0;
        }

    }
}
