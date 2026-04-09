# 🛡️ PokéAPI Backend - Spring Boot & JWT

Este es el núcleo de la Pokédex. Una API RESTful construida con **Spring Boot** que gestiona la persistencia en MySQL y protege los recursos mediante **Spring Security y JWT**.

## 🚀 Tecnologías
* **Java 17** & **Spring Boot 3.x**
* **Spring Security** + **JWT (JJWT 0.12.x)**
* **Spring Data JPA** (Hibernate)
* **MySQL** (Persistencia)
* **Lombok** (Productividad)

## 🔑 Características Principales
* **Seguridad:** Autenticación Stateless mediante tokens JWT.
* **Relaciones Complejas:** Gestión de relación `@ManyToMany` entre Pokémon y Tipos con limpieza de colecciones para integridad de datos.
* **Validación:** Uso de `@Valid` y anotaciones de Jakarta para asegurar la calidad de los datos de entrada.
* **Arquitectura:** Patrón Controlador-Servicio-Repositorio.

## 📌 Endpoints Principales
| Método | Endpoint | Descripción | Acceso |
| :--- | :--- | :--- | :--- |
| POST | `/api/auth/login` | Obtener token JWT | Público |
| GET | `/api/pokemon` | Listar Pokémon (Paginado) | Privado (User/Admin) |
| GET | `/api/pokemon/{id}` | Obtener un Pokémon por ID | Privado (Admin) |
| POST | `/api/pokemon` | Crear nuevo Pokémon | Privado (Admin) |
| PUT | `/api/pokemon/{id}` | Actualizar Pokémon existente | Privado (Admin) |
| DELETE | `/api/pokemon/{id}` | Eliminar Pokémon | Privado (Admin) |
| GET | `/api/pokemon/tipos` | Obtener lista de tipos | Público |

## 🛠️ Configuración
1. Configura tu `application.properties` con las credenciales de tu DB.
2. Asegúrate de tener una clave secreta en `jwt.secret.key` (Base64).
3. Ejecuta con `./mvnw spring-boot:run`.