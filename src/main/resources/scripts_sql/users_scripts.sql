CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  dni VARCHAR(8) NOT NULL,
  phone_number VARCHAR(9) NOT NULL,
  password TEXT NOT NULL,

  CONSTRAINT unique_email_user UNIQUE (email),
  CONSTRAINT unique_dni_user UNIQUE (dni),
  CONSTRAINT unique_phone_number_user UNIQUE (phone_number)
);

-- PROCEDIMIENTOS ALMACENADOS USUARIOS

--   0 = autenticación correcta
--   1 = email no existe
--   2 = contraseña incorrecta
CREATE OR REPLACE FUNCTION authenticate_user(p_email TEXT, p_password TEXT)
RETURNS INTEGER LANGUAGE plpgsql AS $$
DECLARE
  stored_pass TEXT;
BEGIN
  SELECT password INTO stored_pass FROM users WHERE email = p_email;
  IF NOT FOUND THEN
    RETURN 1; -- email no existe
  END IF;

  IF stored_pass IS NOT NULL AND crypt(p_password, stored_pass) = stored_pass THEN
    RETURN 0; -- success
  ELSE
    RETURN 2; -- wrong password
  END IF;
END;
$$;


CREATE OR REPLACE FUNCTION get_user_id_by_email(p_email TEXT)
RETURNS TABLE (
  id BIGINT,
  email TEXT
) LANGUAGE sql AS $$
  SELECT id, email
  FROM users
  WHERE email = p_email
  LIMIT 1;
$$;

--insertar usuario de prueba (password 'secret123')
CREATE OR REPLACE PROCEDURE save_user(
    p_email VARCHAR,
    p_dni VARCHAR,
    p_phone_number VARCHAR,
    p_password VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO users (email, dni, phone_number, password)
    VALUES (
        p_email,
        p_dni,
        p_phone_number,
        crypt(p_password, gen_salt('bf'))
    );

    RAISE NOTICE 'Usuario registrado correctamente: %', p_email;
END;
$$;


