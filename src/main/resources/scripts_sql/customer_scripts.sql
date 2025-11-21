CREATE TABLE customer (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,

    CONSTRAINT unique_dni_customer UNIQUE (dni),
    CONSTRAINT unique_phone_number_customer UNIQUE (phone_number),
    CONSTRAINT unique_email_customer UNIQUE (email)
);

-- PROCEDIMIENTOS ALMACENADOS  CLIENTES
-- INSERTAR UN CUSTOMER
CREATE OR REPLACE FUNCTION insert_customer(
    p_first_name VARCHAR,
    p_last_name VARCHAR,
    p_dni VARCHAR,
    p_phone_number VARCHAR,
    p_email VARCHAR
)
RETURNS TABLE (
    id INT,
    first_name VARCHAR,
    last_name VARCHAR,
    dni VARCHAR,
    phone_number VARCHAR,
    email VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    INSERT INTO customer (
                            first_name,
                            last_name,
                            dni,
                            phone_number,
                            email
    )
    VALUES (
            p_first_name,
            p_last_name,
            p_dni,
            p_phone_number,
            p_email
    )
    RETURNING id,
            first_name,
            last_name,
            dni,
            phone_number,
            email;
END;
$$ LANGUAGE plpgsql;

-- ELIMINAR UN CUSTOMER
CREATE OR REPLACE FUNCTION delete_customer(p_id INT)
RETURNS VOID AS $$
BEGIN
    DELETE FROM customer WHERE id = p_id;
END;
$$ LANGUAGE plpgsql;

-- OBTENER CUSTOMER POR ID
CREATE OR REPLACE FUNCTION get_customer_by_id(p_id INT)
RETURNS TABLE(
    id INT,
    first_name VARCHAR,
    last_name VARCHAR,
    dni VARCHAR,
    phone_number VARCHAR,
    email VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT id,
            first_name,
            last_name,
            dni,
            phone_number,
            email
    FROM customer
    WHERE id = p_id
    LIMIT 1;
END;
$$ LANGUAGE plpgsql;

-- OBTENER TODOS LOS CUSTOMERS (CON PAGINACIÓN)
CREATE OR REPLACE FUNCTION get_all_customers(p_limit INT, p_offset INT)
RETURNS TABLE(
    id INT,
    first_name VARCHAR,
    last_name VARCHAR,
    dni VARCHAR,
    phone_number VARCHAR,
    email VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT id,
            first_name,
            last_name,
            dni,
            phone_number,
            email
    FROM customer
    ORDER BY id
    LIMIT p_limit OFFSET p_offset;
END;
$$ LANGUAGE plpgsql;

-- ACTUALIZAR CUSTOMER
CREATE OR REPLACE FUNCTION update_customer(
    p_id INT,
    p_first_name VARCHAR,
    p_last_name VARCHAR,
    p_dni VARCHAR,
    p_phone_number VARCHAR,
    p_email VARCHAR
)
RETURNS TABLE(
    id INT,
    first_name VARCHAR,
    last_name VARCHAR,
    dni VARCHAR,
    phone_number VARCHAR,
    email VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    UPDATE customer
    SET first_name = p_first_name,
        last_name = p_last_name,
        dni = p_dni,
        phone_number = p_phone_number,
        email = p_email
    WHERE id = p_id
    RETURNING
                id,
                first_name,
                last_name,
                dni,
                phone_number,
                email;
END;
$$ LANGUAGE plpgsql;

-- INSERTAR CLIENTE
INSERT INTO customer (first_name, last_name, dni, phone_number, email)
VALUES
('Carlos', 'Ramírez', '12345678', '987654321', 'carlos.ramirez@example.com');