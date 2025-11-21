CREATE TABLE shift (
    id SERIAL PRIMARY KEY,
    shift_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    id_veterinary INT NOT NULL,

    FOREIGN KEY (id_veterinary) REFERENCES veterinary (id) ON DELETE CASCADE
);

-- PROCEDIMIENTOS ALMACENADOS TURNOS
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

--- INSERTAR TURNO:
INSERT INTO shift (shift_date, start_time, end_time, available, id_veterinary)
VALUES
('2025-11-05', '09:00:00', '13:00:00', TRUE, 1);