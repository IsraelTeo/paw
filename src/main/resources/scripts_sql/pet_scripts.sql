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

-- PROCEDIMIENTOS ALMACENADOS MASCOTAS
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

-- INSERTAR MASCOTA
INSERT INTO pet (first_name, last_name, age, gender, specie, birth_date, id_customer)
VALUES
('Firulais', 'Ramírez', 3, 'MALE', 'Perro', '2022-05-10', 1);