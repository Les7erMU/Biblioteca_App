# App Biblioteca Bv
Aplicación de gestión de biblioteca desarrollada con Java with Maven

## Tecnologías usadas:
- Java +17
- Ant
- MySql(phpmyadmin)
- JDBC

## Estructura del proyecto
src/
 └── com/biblioteca/
      ├── model/
      │    ├── Libro.java
      │    ├── Usuario.java
      │    └── Prestamo.java
      │
      ├── dao/
      │    ├── Conexion.java      # Conexión JDBC a MySQL
      │    ├── LibroDAO.java      # Acceso a tabla libros
      │    ├── UsuarioDAO.java
      │    └── PrestamoDAO.java
      │
      ├── service/
      │    ├── ApiClient.java     # Cliente HTTP para consumir API PHP
      │    └── JsonParser.java    # Si necesitas procesar JSON
      │
      └── ui/
           ├── Main.java          # Menú o JFrame principal
           └── Formularios/*.java



## Configuración de la base de datos
#Para crear la base de datos
CREATE DATABASE App_Biblioteca;
#Para la tabla libro
CREATE TABLE `libro`(
	`id` int AUTO_INCREMENT PRIMARY KEY,
    `codigo` varchar(20) UNIQUE NOT NULL,
    `titulo` varchar(50) NOT NULL,
    `autor` varchar(50),
    `genero` varchar(30),
    `estado` ENUM('disponible', 'alquilado', 'perdido', 'baja') DEFAULT 'disponible'
);

#Para la tabla cliente
CREATE TABLE `cliente`(
	`id` int AUTO_INCREMENT PRIMARY KEY,
    `codigo` varchar(20) UNIQUE NOT NULL,
    `nombre` varchar(50) NOT NULL,
    `telefono` varchar(20) NOT NULL,
    `email` varchar(20)
);

#Para la tabla alquiler
CREATE TABLE `alquiler`(
	`id` int AUTO_INCREMENT PRIMARY KEY,
    `id_libro` int NOT NULL,
    `id_cliente` int NOT NULL,
    `fecha_de_inicio` date NOT NULL,
    `fecha_fin` date,
    `devuelto` ENUM('si', 'no', 'pendiente') DEFAULT 'pendiente',
    FOREIGN KEY (`id_libro`) REFERENCES `libro`(`id`),
    FOREIGN KEY (`id_cliente`) REFERENCES `cliente`(`id`)
);