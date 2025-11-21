CREATE TABLE veterinary_appointment(
    id SERIAL PRIMARY KEY,
    observations TEXT,
    status VARCHAR(50) DEFAULT 'PENDIENTE' CHECK (state IN ('PENDIENTE', 'REALIZADA', 'CANCELADA')),
    register_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    id_pet INT NOT NULL,
    id_veterinary INT NOT NULL,
    id_veterinary_service INT NOT NULL,
    id_shift INT NOT NULL,

    FOREIGN KEY (id_pet) REFERENCES pet (id) ON DELETE CASCADE,
    FOREIGN KEY (id_veterinary) REFERENCES veterinary (id),
    FOREIGN KEY (id_veterinary_service) REFERENCES veterinary_service (id),
    FOREIGN KEY (id_shift) REFERENCES shift (id) ON DELETE RESTRICT
);

-- PROCEDIMIENTOS ALMACENADOS CITA VETERINARIO

-- PROCEDIMIENTO INSERTAR UNA CITA
CREATE OR REPLACE PROCEDURE insert_veterinary_appointment(
    IN p_observations TEXT,
    IN p_status VARCHAR,
    IN p_register_date DATE,
    IN p_id_pet INT,
    IN p_id_veterinary INT,
    IN p_id_veterinary_service INT,
    IN p_id_shift INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO veterinary_appointment (
        observations,
        status,
        register_date,
        id_pet,
        id_veterinary,
        id_veterinary_service,
        id_shift
    )
    VALUES (
        p_observations,
        p_status,
        p_register_date,
        p_id_pet,
        p_id_veterinary,
        p_id_veterinary_service,
        p_id_shift
    );
END;
$$;

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


