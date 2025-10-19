-- schema.sql (Run this in H2 console or via JDBC initialization)
CREATE TABLE IF NOT EXISTS Customers (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    firstName VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    employmentDetails VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS Accounts (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    accountNumber VARCHAR(255) UNIQUE NOT NULL,
    balance DOUBLE DEFAULT 0.0,
    branch VARCHAR(255) NOT NULL,
    openDate DATE NOT NULL,
    customerId INTEGER NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('SAVINGS', 'INVESTMENT', 'CHEQUE')),
    interestRate DOUBLE DEFAULT 0.0,
    initialDeposit DOUBLE DEFAULT 0.0,
    employerVerified BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (customerId) REFERENCES Customers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Transactions (
    id VARCHAR(255) PRIMARY KEY,
    amount DOUBLE NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAWAL', 'INTEREST')),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    accountId INTEGER NOT NULL,
    FOREIGN KEY (accountId) REFERENCES Accounts(id) ON DELETE CASCADE
);