CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password TEXT NOT NULL, -- almacenaremos el hash con crypt()

  CONSTRAINT unique_email_user UNIQUE (email),
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

-- 4. (Opcional) función para obtener id/ email si ya autenticó:
--    devuelve id del usuario o NULL si no existe
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

 insertar usuario de prueba (password 'secret123')
INSERT INTO users (email, password)
VALUES (
  'test@example.com',
  crypt('secret123', gen_salt('bf'))
);