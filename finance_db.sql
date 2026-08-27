-- 1. Criação do Banco de Dados
CREATE DATABASE IF NOT EXISTS finance_db
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

USE finance_db;

-- 2. Tabela de Usuários (Entidade User.java)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    UNIQUE INDEX idx_user_email (email)
) ENGINE=InnoDB;

-- 3. Tabela de Metas (Entidade Goal.java)
CREATE TABLE IF NOT EXISTS goals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    targetAmount DOUBLE NOT NULL DEFAULT 0.0,
    currentAmount DOUBLE NOT NULL DEFAULT 0.0
) ENGINE=InnoDB;

-- 4. Tabela de Categorias (Entidade Category.java)
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    color VARCHAR(20), -- Armazena o Hex da cor (ex: #FF0000)
    type ENUM('INCOME', 'EXPENSE') NOT NULL,
    iconName VARCHAR(100),
    budgetLimit DOUBLE DEFAULT 0.0,
    CONSTRAINT fk_category_user FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. Tabela de Transações (Entidade Transaction.java)
CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    value DOUBLE NOT NULL,
    description VARCHAR(255),
    date BIGINT NOT NULL, -- Armazena o timestamp do Android (System.currentTimeMillis())
    category VARCHAR(100),
    type ENUM('INCOME', 'EXPENSE') NOT NULL,
    accountName VARCHAR(100),
    observation TEXT,
    linkedGoalId INT NULL,
    CONSTRAINT fk_transaction_goal FOREIGN KEY (linkedGoalId) REFERENCES goals(id) ON DELETE SET NULL
) ENGINE=InnoDB;