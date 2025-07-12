CREATE TABLE clients (
    id VARCHAR(50) primary key,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE contracts (
    contract_number VARCHAR(50) primary key,
    client_id VARCHAR(50) NOT NULL,
    start_date DATE,
    end_date DATE,
    status VARCHAR(50),
    CONSTRAINT fk_contracts_client FOREIGN KEY (client_id) REFERENCES clients(id)
);

CREATE TABLE payments (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    payment_date DATE DEFAULT CURRENT_DATE,
    amount DECIMAL(15, 2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    contract_number VARCHAR(50) NOT NULL,
    client_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_client
                      FOREIGN KEY (client_id)
                      REFERENCES clients(id),
    CONSTRAINT fk_payments_contract
                      FOREIGN KEY (contract_number)
                      REFERENCES contracts(contract_number)
)