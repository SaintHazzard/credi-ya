CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    names VARCHAR(100),
    lastname VARCHAR(100),
    birth_date DATE,
    address VARCHAR(200),
    phone VARCHAR(20),
    email VARCHAR(100) UNIQUE,
    salary_base NUMERIC(15,0),
    username VARCHAR(50) UNIQUE,
    password VARCHAR(100)
);

-- Insertar usuario de prueba (admin/admin) si no existe
INSERT INTO users (id, names, lastname, birth_date, address, phone, email, salary_base, username, password)
VALUES 
    ('1', 'Admin', 'User', '1990-01-01', 'Admin Address', '123456789', 'admin@example.com', 1000000, 'admin', '{encoded}admin')
ON CONFLICT (username) DO NOTHING;
