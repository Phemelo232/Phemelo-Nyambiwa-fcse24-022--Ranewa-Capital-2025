package service;

// CustomerService.java - Business logic layer (separate from GUI/boundary)
import com.ooad.ranewacapital.dao.AccountDAO;
import com.ooad.ranewacapital.dao.CustomerDAO;
import com.ooad.ranewacapital.model.Account;
import com.ooad.ranewacapital.model.Customer;
import com.ooad.ranewacapital.model.DatabaseManager;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerService {
    private final CustomerDAO customerDAO;
    private final AccountDAO accountDAO;
    private final DatabaseManager dbManager;

    public CustomerService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.customerDAO = new CustomerDAO(dbManager);
        this.accountDAO = new AccountDAO(dbManager);
    }

    // Simple login validation (email-based for demo; add password field to Customer in real app)
    public Customer validateLogin(String email) throws SQLException {
        // In real app, query by email and check hashed password
        List<Customer> customers = customerDAO.listAll();
        return customers.stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public List<String> getAccountSummaries(Customer customer) throws SQLException {
        List<Account> accounts = accountDAO.listByCustomer(customer.getId());
        return accounts.stream()
                .map(acc -> acc.getAccountNumber() + ": $" + String.format("%.2f", acc.getBalance()))
                .collect(Collectors.toList());
    }

    public double getBalance(String accountNumber) throws SQLException {
        // Fetch account by number (add method to AccountDAO if needed)
        // For demo: Assume first matching account
        List<Account> allAccounts = accountDAO.listByCustomer(/* current customer id */ 1); // Stub; use session
        return allAccounts.stream()
                .filter(a -> a.getAccountNumber().equals(accountNumber))
                .findFirst()
                .map(Account::getBalance)
                .orElse(0.0);
    }

    public void deposit(String accountNumber, double amount) throws SQLException {
        // Fetch and update account (add findByNumber to AccountDAO)
        Account account = null /* accountDAO.findByNumber(accountNumber) */;
        account.deposit(amount, "User deposit");
        accountDAO.update(account);
        // Create transaction via TransactionDAO
    }

    public void withdraw(String accountNumber, double amount) throws SQLException {
        // Similar to deposit
        Account account = null /* fetch */;
        account.withdraw(amount);
        accountDAO.update(account);
    }

    public double calculateInterest(String accountNumber) throws SQLException {
        Account account = null /* fetch */;
        return account.calculateInterest();
        // Optionally credit to balance and log transaction
    }

    // Getter for current customer (use session/thread-local in real app)
    public Customer getCurrentCustomer() {
        // Stub: Return logged-in customer
        return null;
    }
}
