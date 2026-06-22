CREATE DATABASE IF NOT EXISTS valenbicibd
CHARACTER SET utf8mb4 COLLATE utf8mb4_es_0900_as_cs;

CREATE TABLE historico ( 
    id INT AUTO_INCREMENT PRIMARY KEY, 
    estacion_id INT NOT NULL, 
    direccion VARCHAR(255), 
    bicis_disponibles INT NOT NULL, 
    anclajes_libres INT NOT NULL, 
    estado_operativo BOOLEAN NOT NULL, 
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    ubicación POINT 
   ); 
   
 ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'draco21';
FLUSH PRIVILEGES;
ALTER TABLE historico MODIFY COLUMN estacion_id INT AUTO_INCREMENT;
ALTER TABLE historico MODIFY estacion_id INT NULL;

ALTER TABLE historico MODIFY estacion_id INT NULL;
ALTER TABLE historico MODIFY bicis_disponibles INT NULL;
ALTER TABLE historico MODIFY anclajes_libres INT NULL;
ALTER TABLE historico MODIFY estado_operativo BOOLEAN NULL;

USE valenbicibd;

ALTER TABLE historico MODIFY bicis_disponibles INT NULL;
ALTER TABLE historico MODIFY anclajes_libres INT NULL;
ALTER TABLE historico MODIFY estado_operativo BOOLEAN NULL;