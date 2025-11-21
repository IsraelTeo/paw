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

-- PROCEDIMIENTOS ALMACENADOS VETERINARIO
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

-- INSERTAR VETERINARIO
INSERT INTO veterinary (first_name, last_name, birth_date, speciality, phone_number, email, dni)
VALUES
('Lucía', 'Fernández', '1990-03-12', 'Medicina general', '998877665', 'lucia.fernandez@vetclinic.com', '98765432');