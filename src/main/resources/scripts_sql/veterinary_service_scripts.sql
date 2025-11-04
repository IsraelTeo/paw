CREATE TABLE veterinary_service (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    price NUMERIC(10,2) NOT NULL,

    CONSTRAINT unique_name_veterinary_service UNIQUE (name),
);

-- PROCEDIMIENTOS ALMACENADOS  SERVICIO VETERINARIO
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