import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transactions")

public class Transaction {

    public Transaction(){}

    public Transaction(Account accountFrom, Account accountTo, double valueFrom, EntityManager em) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.valueFrom = valueFrom;
        this.setFields(em);
    }

    @Id
    @GeneratedValue
    long id;

    @JoinColumn(name = "fromAccount_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Account accountFrom;

    @JoinColumn(name = "toAccount_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Account accountTo;

    @Column(nullable = false)
    private double valueFrom;
    @Column(nullable = false)

    private double valueTo;
    private String currencyExchangeType;
    private double currencyRate;
    private Date date;

    public Account getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(Account fromAccount) {
        this.accountFrom = fromAccount;
    }

    public Account getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(Account toAccount) {
        this.accountTo = toAccount;
    }

    public double getValueFrom() {
        return valueFrom;
    }

    public void setValueFrom(double valueFrom) {
        this.valueFrom = valueFrom;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    private void setFields(EntityManager em){
        String currencyFrom = accountFrom.getCurrency();
        String currencyTo = accountTo.getCurrency();
        if(!currencyFrom.equals(currencyTo)){
            currencyExchangeType = currencyFrom +"/"+currencyTo;
            currencyRate = CurrencyRate.getCurrencyRate(currencyExchangeType, em);
        }
    }

    public void commit(EntityManager em){
        if(currencyExchangeType==null){
            valueTo = valueFrom;
        }else{
            valueTo = valueFrom*currencyRate;
        }

        try{
            em.getTransaction().begin();
            accountFrom.setBalance(accountFrom.getBalance()-valueFrom);
            em.merge(accountFrom);
            accountTo.setBalance(accountTo.getBalance()+valueTo);
            em.merge(accountTo);
            this.setDate(new Date());

            em.persist(this);
            em.getTransaction().commit();

        } catch (Exception ex) {
        em.getTransaction().rollback();
        return;
        }
        System.out.println("---------SUCCESSFUL---------");
    }
}
