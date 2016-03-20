import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
public class Client {
    public Client(){}
    public Client(String name, String address){
        this.name = name;
        this.address = address;
    }


    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    String name;
    String address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Account> accounts = new ArrayList<>();

    public void setName (String name){
        this.name = address;
    }
    public String getName(){
        return this.name;
    }
    public void setAddress (String address){
        this.address = name;
    }
    public String getAddress(){
        return this.address;
    }

    public void addAccount(Account account) {
        account.setClient(this);
        accounts.add(account);
    }

}
