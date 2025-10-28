-- CREATE DATABASE --


-- TABLAS--
-- Tabla de clientes
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

-- Tabla de mascotas
CREATE TABLE pet (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    age INT,
    gender VARCHAR(10) NOT NULL,
    specie VARCHAR(50) NOT NULL,
    birth_date DATE,
    id_customer INT NOT NULL,

    FOREIGN KEY (id_customer) REFERENCES customer (id) ON DELETE CASCADE
);

-- Tabla de veterinarios
CREATE TABLE veterinary (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    birth_date  DATE NOT NULL,
    speciality VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL,

    CONSTRAINT unique_dni_doctor UNIQUE (dni),
    CONSTRAINT unique_email_doctor UNIQUE (email),
    CONSTRAINT unique_phone_number_doctor UNIQUE (phone_number)
);

-- Tabla de servicio veterinario
CREATE TABLE veterinary_service (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    price NUMERIC(10,2) NOT NULL,

    CONSTRAINT unique_name_veterinary_service UNIQUE (name),
);

-- Tabla de turnos
CREATE TABLE shift (
    id SERIAL PRIMARY KEY,
    shift_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    id_veterinary INT NOT NULL,

    FOREIGN KEY (id_veterinary) REFERENCES veterinary (id) ON DELETE CASCADE
);

-- Tabla de citas médicas
CREATE TABLE veterinary_appointment(
    id SERIAL PRIMARY KEY,
    observations TEXT,
    status VARCHAR(50) DEFAULT 'PENDIENTE' CHECK (state IN ('PENDIENTE', 'REALIZADA', 'CANCELADA')),
    register_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    id_pet INT NOT NULL,
    id_veterinary INT NOT NULL,
    id_veterinary_service INT NOT NULL,
    id_shift INT NOT NULL,

    FOREIGN KEY (id_pet) REFERENCES pet (id) ON DELETE CASCADE,,
    FOREIGN KEY (id_veterinary) REFERENCES veterinary (id),
    FOREIGN KEY (id_veterinary_service) REFERENCES veterinary_service (id),
    FOREIGN KEY (id_shift) REFERENCES shift (id) ON DELETE RESTRICT
);


-- PROCEDIMIENTOS ALMACENADOS QUE USARÁ EL SISTEMA ---------------------------------------------------------------------

------------------------------------------------------------------------------------------------------------------------
-- VETERINARIO
-- INSERTAR UN VETERINARIO
CREATE OR REPLACE PROCEDURE insert_veterinary(
    p_first_name VARCHAR,
    p_last_name VARCHAR,
    p_birth_date DATE,
    p_speciality VARCHAR,
    p_phone_number VARCHAR,
    p_email VARCHAR,
    p_dni VARCHAR,
    p_available_days VARCHAR -- días separados por coma, ejemplo: 'MONDAY,WEDNESDAY'
)
LANGUAGE plpgsql
AS $$
DECLARE
    vet_id BIGINT;
    day TEXT;
BEGIN
    -- Insertar el veterinario
    INSERT INTO veterinary(first_name, last_name, birth_date, speciality, phone_number, email, dni)
    VALUES (p_first_name, p_last_name, p_birth_date, p_speciality, p_phone_number, p_email, p_dni)
    RETURNING id INTO vet_id;

    -- Insertar los días disponibles
    FOREACH day IN ARRAY string_to_array(p_available_days, ',') LOOP
        INSERT INTO veterinary_available_days(veterinary_id, day_of_week)
        VALUES (vet_id, day);
    END LOOP;
END;
$$;

-- ELIMINAR UN VETERINARIO
CREATE OR REPLACE FUNCTION delete_veterinary(p_id INT)
RETURNS VOID AS $$
BEGIN
    DELETE FROM veterinary WHERE id = p_id;
END;
$$ LANGUAGE plpgsql;

-- OBTENER POR ID
CREATE OR REPLACE FUNCTION get_veterinary_by_id(p_id INT)
RETURNS TABLE(
    id INT,
    first_name VARCHAR,
    last_name VARCHAR,
    birth_date DATE,
    speciality VARCHAR,
    phone_number VARCHAR,
    email VARCHAR,
    dni VARCHAR
) AS $$
BEGIN
    RETURN QUERY
        SELECT  id,
                first_name,
                last_name,
                birth_date,
                speciality,
                phone_number,
                email,
                dni
        FROM veterinary
        WHERE id = p_id
        LIMIT 1;
END;
$$ LANGUAGE plpgsql;

-- OBTENER TODOS (CON PAGINACIÓN)
CREATE OR REPLACE FUNCTION get_all_veterinaries(p_limit INT, p_offset INT)
RETURNS TABLE(
    id INT,
    first_name VARCHAR,
    last_name VARCHAR,
    speciality VARCHAR,
    phone_number VARCHAR,
    email VARCHAR,
    dni VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT  id,
            first_name,
            last_name,
            speciality
    FROM veterinary
    ORDER BY id
    LIMIT p_limit OFFSET p_offset;
END;
$$ LANGUAGE plpgsql;

-- ACTUALIZAR UN VETERINARIO
CREATE OR REPLACE FUNCTION update_veterinary(
    p_id INT,
    p_first_name VARCHAR,
    p_last_name VARCHAR,
    p_birth_date DATE,
    p_speciality VARCHAR,
    p_phone_number VARCHAR,
    p_email VARCHAR,
    p_dni VARCHAR
)
RETURNS TABLE(
    id INT,
    first_name VARCHAR,
    last_name VARCHAR,
    birth_date DATE,
    speciality VARCHAR,
    phone_number VARCHAR,
    email VARCHAR,
    dni VARCHAR
) AS $$
BEGIN
    UPDATE veterinary
    SET first_name = p_first_name,
        last_name = p_last_name,
        birth_date = p_birth_date,
        speciality = p_speciality,
        phone_number = p_phone_number,
        email = p_email,
        dni = p_dni
    WHERE id = p_id;
END;
$$ LANGUAGE plpgsql;

------------------------------------------------------------------------------------------------------------------------
-- MASCOTAS
-- INSERTAR UNA MASCOTA
CREATE OR REPLACE FUNCTION insert_pet(
    p_first_name VARCHAR,
    p_last_name VARCHAR,
    p_age INT,
    p_gender VARCHAR,
    p_specie VARCHAR,
    p_birth_date DATE,
    p_id_customer INT
)
RETURNS TABLE (
    id INT,
    first_name VARCHAR,
    last_name VARCHAR,
    age INT,
    gender VARCHAR,
    specie VARCHAR,
    birth_date DATE,
    id_customer INT
)
AS $$
BEGIN
    RETURN QUERY
    INSERT INTO pet (
                        first_name,
                        last_name,
                        age,
                        gender,
                        specie,
                        birth_date,
                        id_customer
    )
    VALUES (
                p_first_name,
                p_last_name,
                p_age,
                p_gender,
                p_specie,
                p_birth_date,
                p_id_customer
    )
    RETURNING id, first_name, last_name, age, gender, specie, birth_date, id_customer;
END;
$$ LANGUAGE plpgsql;

-- ELIMINAR UNA MASCOTA
CREATE OR REPLACE FUNCTION delete_pet(p_id INT)
RETURNS VOID
AS $$
BEGIN
    DELETE FROM pet WHERE id = p_id;
END;
$$ LANGUAGE plpgsql;

-- OBTENER MASCOTA POR ID
CREATE OR REPLACE FUNCTION get_pet_by_id(p_id INT)
RETURNS TABLE(
    id INT,
    first_name VARCHAR,
    last_name VARCHAR,
    age INT,
    gender VARCHAR,
    specie VARCHAR,
    birth_date DATE,
    id_customer INT
)
AS $$
BEGIN
    RETURN QUERY
    SELECT  id,
            first_name,
            last_name,
            age,
            gender,
            specie,
            birth_date,
            id_customer
    FROM pet WHERE id = p_id
    LIMIT 1;
END;
$$ LANGUAGE plpgsql;

-- OBTENER TODAS LAS MASCOTAS (CON PAGINACIÓN)
CREATE OR REPLACE FUNCTION get_all_pets(p_limit INT, p_offset INT)
RETURNS TABLE(
    id INT,
    first_name VARCHAR,
    last_name VARCHAR,
    gender VARCHAR,
    specie VARCHAR
)
AS $$
BEGIN
    RETURN QUERY
    SELECT
            id,
            first_name,
            last_name,
            gender,
            specie
    FROM pet
    ORDER BY id
    LIMIT p_limit OFFSET p_offset;
END;
$$ LANGUAGE plpgsql;

-- ACTUALIZAR UNA MASCOTA
CREATE OR REPLACE FUNCTION update_pet(
    p_id INT,
    p_first_name VARCHAR,
    p_last_name VARCHAR,
    p_age INT,
    p_gender VARCHAR,
    p_specie VARCHAR,
    p_birth_date DATE,
    p_id_customer INT
)
RETURNS TABLE(
    id INT,
    first_name VARCHAR,
    last_name VARCHAR,
    age INT,
    gender VARCHAR,
    specie VARCHAR,
    birth_date DATE,
    id_customer INT
) AS $$
BEGIN
    UPDATE pet
    SET first_name = p_first_name,
        last_name = p_last_name,
        age = p_age,
        gender = p_gender,
        specie = p_specie,
        birth_date = p_birth_date,
        id_customer = p_id_customer
    WHERE id = p_id;
END;
$$ LANGUAGE plpgsql;

------------------------------------------------------------------------------------------------------------------------
-- CLIENTES
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

------------------------------------------------------------------------------------------------------------------------
-- TURNOS
-- INSERTAR UN SHIFT
CREATE OR REPLACE FUNCTION insert_shift(
    p_shift_date DATE,
    p_time_start TIME,
    p_time_end TIME,
    p_available BOOLEAN,
    p_id_veterinary
)
RETURNS TABLE(
    id INT,
    shift_date DATE,
    time_start TIME,
    time_end TIME,
    available BOOLEAN,
    id_veterinary
) AS $$
BEGIN
    RETURN QUERY
    INSERT INTO shift (
                        shift_date,
                        time_start,
                        time_end,
                        available,
                        id_veterinary
    )
    VALUES (
            p_shift_date,
            p_time_start,
            p_time_end,
            p_available,
            p_id_veterinary
    )
    RETURNING   id,
                shift_date,
                time_start,
                time_end,
                available,
                id_veterinary;
END;
$$ LANGUAGE plpgsql;

-- ELIMINAR UN SHIFT
CREATE OR REPLACE FUNCTION delete_shift(p_id INT)
RETURNS VOID AS $$
BEGIN
    DELETE FROM shift WHERE id = p_id;
END;
$$ LANGUAGE plpgsql;

-- OBTENER SHIFT POR ID
CREATE OR REPLACE FUNCTION get_shift_by_id(p_id INT)
RETURNS TABLE(
    id INT,
    shift_date DATE,
    time_start TIME,
    time_end TIME,
    available BOOLEAN,
    id_veterinary
) AS $$
BEGIN
    RETURN QUERY
    SELECT  id,
            shift_date,
            time_start,
            time_end,
            available,
            id_veterinary
    FROM shift
    WHERE id = p_id
    LIMIT 1;
END;
$$ LANGUAGE plpgsql;

-- OBTENER TODOS LOS SHIFTS (CON PAGINACIÓN)
CREATE OR REPLACE FUNCTION get_all_shifts(p_limit INT, p_offset INT)
RETURNS TABLE(
    id INT,
    shift_date DATE,
    time_start TIME,
    time_end TIME,
    available BOOLEAN,
    id_veterinary
) AS $$
BEGIN
    RETURN QUERY
    SELECT  id,
            shift_date,
            time_start,
            time_end,
            available,
            id_veterinary
    FROM shift
    ORDER BY id
    LIMIT p_limit OFFSET p_offset;
END;
$$ LANGUAGE plpgsql;

-- ACTUALIZAR SHIFT
CREATE OR REPLACE FUNCTION update_shift(
    p_id INT,
    p_shift_date DATE,
    p_time_start TIME,
    p_time_end TIME,
    p_available BOOLEAN,
    p_id_veterinary
)
RETURNS TABLE(
    id INT,
    shift_date DATE,
    time_start TIME,
    time_end TIME,
    available BOOLEAN,
    id_veterinary
) AS $$
BEGIN
    RETURN QUERY
    UPDATE shift
    SET shift_date = p_shift_date,
        time_start = p_time_start,
        time_end = p_time_end,
        available = p_available,
        id_veterinary = p_id_veterinary
    WHERE id = p_id
    RETURNING id, shift_date, time_start, time_end, available, id_veterinary;
END;
$$ LANGUAGE plpgsql;

------------------------------------------------------------------------------------------------------------------------
-- SERVICIO VETERINARIO
-- INSERTAR UN SERVICIO VETERINARIO
CREATE OR REPLACE FUNCTION insert_veterinary_service(
    p_name VARCHAR,
    p_description TEXT,
    p_price NUMERIC(10,2)
)
RETURNS TABLE(
    id INT,
    name VARCHAR,
    description TEXT,
    price NUMERIC(10,2)
) AS $$
BEGIN
    RETURN QUERY
    INSERT INTO veterinary_service (
                                    name,
                                    description,
                                    price
    )
    VALUES (
            p_name,
            p_description,
            p_price
    )
    RETURNING id,
            name,
            description,
            price;
END;
$$ LANGUAGE plpgsql;

-- ELIMINAR UN SERVICIO VETERINARIO
CREATE OR REPLACE FUNCTION delete_veterinary_service(p_id INT)
RETURNS VOID AS $$
BEGIN
    DELETE FROM veterinary_service WHERE id = p_id;
END;
$$ LANGUAGE plpgsql;

-- OBTENER SERVICIO POR ID
CREATE OR REPLACE FUNCTION get_veterinary_service_by_id(p_id INT)
RETURNS TABLE(
    id INT,
    name VARCHAR,
    description TEXT,
    price NUMERIC(10,2)
) AS $$
BEGIN
    RETURN QUERY
    SELECT id, name, description, price
    FROM veterinary_service
    WHERE id = p_id
    LIMIT 1;
END;
$$ LANGUAGE plpgsql;

-- OBTENER TODOS LOS SERVICIOS (CON PAGINACIÓN)
CREATE OR REPLACE FUNCTION get_all_veterinary_services(p_limit INT, p_offset INT)
RETURNS TABLE(
    id INT,
    name VARCHAR,
    description TEXT,
    price NUMERIC(10,2)
) AS $$
BEGIN
    RETURN QUERY
    SELECT id,
            name,
            description,
            price
    FROM veterinary_service
    ORDER BY id
    LIMIT p_limit OFFSET p_offset;
END;
$$ LANGUAGE plpgsql;

-- ACTUALIZAR UN SERVICIO VETERINARIO
CREATE OR REPLACE FUNCTION update_veterinary_service(
    p_id INT,
    p_name VARCHAR,
    p_description TEXT,
    p_price NUMERIC(10,2)
)
RETURNS TABLE(
    id INT,
    name VARCHAR,
    description TEXT,
    price NUMERIC(10,2)
) AS $$
BEGIN
    RETURN QUERY
    UPDATE veterinary_service
    SET name = p_name,
        description = p_description,
        price = p_price
    WHERE id = p_id
    RETURNING id, name, description, price;
END;
$$ LANGUAGE plpgsql;

------------------------------------------------------------------------------------------------------------------------
-- CITA VETERINARIO
-- INSERTAR UNA CITA
CREATE OR REPLACE FUNCTION insert_veterinary_appointment(
    p_state VARCHAR,
    p_observations TEXT,
    p_id_pet INT,
    p_id_veterinary INT,
    p_id_veterinary_service INT,
    p_id_shift INT
)
RETURNS TABLE(
    id INT,
    state VARCHAR,
    observations TEXT,
    register_date DATE,
    id_pet INT,
    id_veterinary INT,
    id_veterinary_service INT,
    id_shift INT,
) AS $$
BEGIN
    INSERT INTO veterinary_appointment (
        state,
        observations,
        id_pet, id_veterinary,
        id_veterinary_service,
        id_shift
    )
    VALUES (
            p_state,
            p_observations,
            p_id_pet,
            p_id_veterinary,
            p_id_veterinary_service,
            p_id_shift
    );
END;
$$ LANGUAGE plpgsql;

-- ELIMINAR UNA CITA
CREATE OR REPLACE FUNCTION delete_veterinary_appointment(p_id INT)
RETURNS VOID AS $$
BEGIN
    DELETE FROM veterinary_appointment WHERE id = p_id;
END;
$$ LANGUAGE plpgsql;

-- OBTENER CITA POR ID
CREATE OR REPLACE FUNCTION get_veterinary_appointment_by_id(p_id INT)
RETURNS TABLE(
    id INT,
    register_date TIMESTAMP,
    observations TEXT,
    id_pet INT,
    id_veterinary INT,
    id_veterinary_service INT,
    id_shift INT,
    state VARCHAR
) AS $$
BEGIN
    RETURN QUERY
        SELECT
                id,
                register_date,
                observations,
                id_pet,
                id_veterinary,
                id_veterinary_service,
                id_shift,
                state
        FROM veterinary_appointment
        WHERE id = p_id
        LIMIT 1;
END;
$$ LANGUAGE plpgsql;

-- OBTENER TODAS LAS CITAS (CON PAGINACIÓN)
CREATE OR REPLACE FUNCTION get_all_veterinary_appointments(p_limit INT, p_offset INT)
RETURNS TABLE(
    id INT,
    register_date TIMESTAMP,
    observations TEXT,
    id_pet INT,
    id_veterinary INT,
    id_veterinary_service INT,
    id_shift INT,
    state VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT
            id,
            register_date,
            observations,
            state
    FROM veterinary_appointment
    ORDER BY register_date DESC
    LIMIT p_limit OFFSET p_offset;
END;
$$ LANGUAGE plpgsql;

-- ACTUALIZAR UNA CITA
CREATE OR REPLACE FUNCTION update_veterinary_appointment(
    p_id INT,
    p_state VARCHAR,
    p_observations TEXT,
    p_id_pet INT,
    p_id_veterinary INT,
    p_id_veterinary_service INT,
    p_id_shift INT
)
RETURNS TABLE(
    id INT,
    register_date TIMESTAMP,
    observations TEXT,
    register_date DATE,
    id_pet INT,
    id_veterinary INT,
    id_veterinary_service INT,
    id_shift INT,
    state VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    UPDATE veterinary_appointment
    SET observations = p_observations,
        id_pet = p_id_pet,
        id_veterinary = p_id_veterinary,
        id_veterinary_service = p_id_veterinary_service,
        id_shift = p_id_shift,
        state = p_state
    WHERE id = p_id
    RETURNING
                id,
                state,
                observations,
                register_date,
                id_pet,
                id_veterinary,
                id_veterinary_service,
                id_shift;
END;
$$ LANGUAGE plpgsql;