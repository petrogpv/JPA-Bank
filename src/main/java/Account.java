import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "accounts")
@NamedQueries({
        @NamedQuery(name = "Accounts.findByNameAndCurrency", query = "SELECT DISTINCT a FROM Account a INNER JOIN a.client c WHERE  c.name = :name AND a.currency = :currency"),
        @NamedQuery(name = "Accounts.findByName", query = "SELECT DISTINCT a FROM Account a INNER JOIN a.client c WHERE  c.name = :name")
})
public class Account {

    public Account(){}

    public Account(long number, long balance, String currency) {
        this.number = number;
        this.balance = balance;
        this.currency = currency;
    }

    @Id
    @GeneratedValue
    private long id;


    @JoinColumn(name = "client_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    @Column(nullable = false)
    private long number;
    @Column(nullable = false)
    private double balance;
    @Column(nullable = false)
    private String currency;

    @OneToMany(mappedBy = "accountFrom", cascade = CascadeType.ALL)
    private List<Transaction> transactionFrom = new ArrayList<>();

    @OneToMany(mappedBy = "accountTo", cascade = CascadeType.ALL)
    private List<Transaction> transactionTo = new ArrayList<>();

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public long getNumber() {
        return number;
    }

    public double getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }
    public void setNumber(long number) {
        this.number = number;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
