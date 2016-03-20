import currenciesRatesUpload.Currencies;

import javax.persistence.*;
import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class App {
    private static final int TRANSFERING = 0;
    private static final int DEPOSITING = 1;
    private static final int WITHDRAWAL = 2;

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPA");
    private static final EntityManager em = emf.createEntityManager();


    public static void main (String [] args) throws JAXBException {

//        createDefaultDatabase(em);  // uncomment for first launch
        updateCurrencyRate();

        start();

        em.close();
        emf.close();


    }
    public static void updateCurrencyRate() throws JAXBException {


        Map<String, Double> ratesMap = Currencies.getRates();
        em.getTransaction().begin();
        try{
            CurrencyRate rate;
            for (String s:ratesMap.keySet()) {
                try {
                    Query query = em.createNamedQuery("CurrencyRate.findByName", CurrencyRate.class);
                    query.setParameter("name", s);
                    rate = (CurrencyRate) query.getSingleResult();
                    rate.setRate(ratesMap.get(s));
                    em.merge(rate);

                } catch (NoResultException ex) {
                    System.out.println("CurrencyRate not found!");
                    rate = new CurrencyRate(s,ratesMap.get(s));
                    em.persist(rate);
                } catch (NonUniqueResultException ex) {
                    System.out.println("Non unique CurrencyRate found!");
                }
            }

            em.getTransaction().commit();

        } catch (Exception ex) {
            em.getTransaction().rollback();
            return;
        }

    }
    private static void createDefaultDatabase() throws JAXBException {
        try{
            em.getTransaction().begin();

            Client bank = new Client("BANK","Gallactic");
            Account b1 = new Account(11,100_000_000,"UAH");
            Account b2 = new Account(22,100_000_000,"USD");
            Account b3 = new Account(33,100_000_000,"EUR");
            bank.addAccount(b1);
            bank.addAccount(b2);
            bank.addAccount(b3);
            em.persist(bank);

            Client client1 = new Client("Ivan", "Kiev");
            Account acc1 = new Account(1,1000,"UAH");
            Account acc2 = new Account(2,5000,"USD");
            client1.addAccount(acc1);
            client1.addAccount(acc2);
            em.persist(client1);

            Client client2 = new Client("Petro", "Kiev");
            Account acc3 = new Account(3,10000,"EUR");
            Account acc4 = new Account(4,20000,"USD");
            client2.addAccount(acc3);
            client2.addAccount(acc4);
            em.persist(client2);

            Client client3 = new Client("Jack", "New-York");
            Account acc5 = new Account(5,10000,"EUR");
            Account acc6 = new Account(6,15000,"USD");
            Account acc7 = new Account(7,1000,"UAH");
            client3.addAccount(acc5);
            client3.addAccount(acc6);
            client3.addAccount(acc7);
            em.persist(client3);

            em.getTransaction().commit();

        } catch (Exception ex) {
            em.getTransaction().rollback();
            return;
        }
    }
    private static void start (){
        Scanner sc = new Scanner(System.in);
        boolean ch = true;

     while (ch) {
        System.out.println("\t\t----Menu----");
        System.out.println  ("\t1 - depositing money\n" +
                "\t2 - withdrawal money\n" +
                "\t3 - money transfer\n" +
                "\t4 - view and convert all monies of client in one currency\n" +
                "\t5 - exit!");
        String choice = sc.nextLine();
        if(choice!=null&&choice.length()==1){
            switch (Integer.parseInt(choice)){
                case 1:
                    transfering(DEPOSITING,sc);
                    start();
                    break;
                case 2:
                    transfering(WITHDRAWAL,sc);
                    start();
                case 3:
                    transfering(TRANSFERING,sc);
                    start();
                    break;
                case 4:
                    doAllInOne(sc);
                    break;
                case 5:
                    ch = false;
                    sc.close();
                    break;
            }
            System.out.println("Wrong input! Try again!");
        }

    }
}
    private static void doAllInOne(Scanner sc) {
        String name;
        String currency;
        String currencyExchangeType;
        double rate;
        List <Account> accounts;
        double sum = 0;

        System.out.println("\t----View all money in one currency----");

        while(true) {
            try {

                System.out.print("Enter client name: ");
                name= sc.nextLine();


                System.out.print("Enter currency: ");
                currency = sc.nextLine();

                if (!currency.equals("UAH") && !currency.equals("USD") && !currency.equals("EUR")) {
                    System.out.println("Wrong currency input! Currencies: UAH,USD,EUR. Try again!");
                    continue;
                }

            } catch (NumberFormatException e) {
                System.out.println("Wrong input! Try again!");
                continue;
            }

            accounts = getAccounts(name);

            if(accounts==null){
                System.out.println("Error! " + name + " doesn't have any accounts or " + name + " doesn't exists");
                continue;
            }
            break;
        }
        for (Account a:accounts) {
            if(a.getCurrency().equals(currency)){
                sum+=a.getBalance();
            }
            else{
                currencyExchangeType = a.getCurrency() + "/" + currency;
                rate = CurrencyRate.getCurrencyRate(currencyExchangeType,em);
                sum+=a.getBalance()*rate;
            }
        }
        System.out.println("\nAll money of " + name + " in " + currency + " : " + sum);

        while (true) {
            System.out.println("Would you like to convert all money to " + currency + " account? (y/n)");
            String answer = sc.nextLine();
            if (!answer.equals("y") && !answer.equals("n")) {
                System.out.println("Wrong input! Try again!");
                continue;
            } else if (answer.equals("n")) {
                return;
            } else {
                System.out.println("Are you sure? (y/n)");
                answer = sc.nextLine();
                if (!answer.equals("y") && !answer.equals("n")) {
                    System.out.println("Wrong input! Try again!");
                    continue;
                } else if (answer.equals("n")) {
                    return;
                }
                break;
            }
        }
        Account account = getAccount(name,currency);
        if(account == null){
            System.out.println("Client " + name + " doesn't have the account in " + currency + ".Create account first!");
            return;
        }

        for (Account a:accounts) {
            if(a.getCurrency().equals(account.getCurrency())){
                continue;
            }
            Transaction transaction = new Transaction(a,account,a.getBalance(),em);
            transaction.commit(em);
        }

    }

    private static List <Account> getAccounts(String name){
        Query query = em.createNamedQuery("Accounts.findByName", Account.class);
        List <Account> accounts;
        try{
            query.setParameter("name", name);
            accounts = query.getResultList();
        } catch (NoResultException ex) {
            return null;
        }
        return accounts;
    }
    private static Account getAccount(String name, String currency){
        Query query = em.createNamedQuery("Accounts.findByNameAndCurrency", Account.class);
        Account account;
        try{
            query.setParameter("name", name);
            query.setParameter("currency", currency);
            account = (Account)query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
        return account;
    }


    private static void transfering(int flag, Scanner sc) {
        String nameFrom = null;
        String nameTo = null;
        double sum;
        String currencyFrom;
        String currencyTo;
        Account accountTo;
        Account accountFrom;
            switch (flag){
                case TRANSFERING:
                    System.out.println("\t----Transfering money----");
                    break;
                case DEPOSITING:
                    System.out.println("\t----Depositing money----");
                    nameFrom = "BANK";
                    break;
                case WITHDRAWAL:
                    System.out.println("\t----Withdrawal money----");
                    nameTo = "BANK";
                    break;
            }

        if (flag==TRANSFERING) {
            System.out.println("\t----Transfering money----");
        }
        else {

        }

        while(true) {
            try {
                if (flag != DEPOSITING) {
                    System.out.print("Enter client from name: ");
                    nameFrom = sc.nextLine();
                }

                System.out.print("Enter currency from: ");
                currencyFrom = sc.nextLine();

                if (!currencyFrom.equals("UAH") && !currencyFrom.equals("USD") && !currencyFrom.equals("EUR")) {
                    System.out.println("Wrong currency input! Currencies: UAH,USD,EUR. Try again!");
                    continue;
                }

                accountFrom = getAccount(nameFrom, currencyFrom);
                if (accountFrom == null) {
                    System.out.println("Error! " + nameFrom + " doesn't have the account in " + currencyFrom + "or " + nameFrom + " doesn't exists");
                    continue;
                }

                System.out.print("Enter sum: ");
                sum = Double.parseDouble(sc.nextLine());

                if (accountFrom.getBalance() < sum) {
                    System.out.println("Not enough money in the account. Available: " + accountFrom.getBalance()+""+accountFrom.getCurrency());
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Wrong input! Try again!");
                continue;
            }
                break;
        }
        while(true) {
            try {
                if (flag != WITHDRAWAL) {
                    System.out.print("Enter client to name: ");
                    nameTo = sc.nextLine();
                }
                System.out.print("Enter currency to: ");
                currencyTo = sc.nextLine();

                if(!currencyTo.equals("UAH")&&!currencyTo.equals("USD")&&!currencyTo.equals("EUR")){
                    System.out.println("Wrong currency input! Currencies: UAH,USD,EUR. Try again!");
                    continue;
                }

                accountTo = getAccount(nameTo, currencyTo);
                if(accountTo==null){
                    System.out.println("Error! "+nameTo+" doesn't have an account in "+currencyTo+ "or "+nameTo+" doesn't exists");
                    continue;
                }

            } catch (NumberFormatException e) {
                System.out.println("Wrong input! Try again!");
                continue;
            }
            break;
        }
        Transaction transaction = new Transaction(accountFrom,accountTo,sum, em);
        transaction.commit(em);

    }

}
