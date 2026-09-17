# ms-barriodigital-catalog

Microservicio de catálogo de BarrioDigital. Administra los tipos de trámite, requisitos, cupos diarios y disponibilidad.

## Stack

- Java 21
- Spring Boot 3.3.6
- Spring Web
- Spring Data JPA
- PostgreSQL
- Bean Validation
- Actuator
- H2 para pruebas automatizadas

## Flujo

```text
Frontend -> API Gateway -> BFF -> Catalog -> PostgreSQL
```

El servicio no aplica autenticación por sí mismo en esta entrega. La autorización se realiza en el BFF y Catalog permanece como servicio interno de Docker Compose.

## Variables de entorno

Crear `.env` dentro de `barriodigitalcatalog/` a partir de `.env.example`:

```env
SERVER_PORT=8082
DB_URL=jdbc:postgresql://YOUR_HOST:5432/YOUR_DATABASE
DB_USERNAME=YOUR_DATABASE_USERNAME
DB_PASSWORD=YOUR_DATABASE_PASSWORD
JPA_DDL_AUTO=update
```

El `.env` real no debe subirse al repositorio.

## Modelo de tipo de trámite

Cada procedimiento contiene:

```text
id
name
requirements
dailyQuota
available
createdAt
updatedAt
```

Los requisitos se guardan en la colección asociada `procedure_requirements`.

## Endpoints

### Listar catálogo

```http
GET /api/catalog/procedures
```

### Obtener procedimiento

```http
GET /api/catalog/procedures/{id}
```

### Crear procedimiento

```http
POST /api/catalog/procedures
```

Ejemplo:

```json
{
  "name": "Certificado de residencia",
  "requirements": ["Cédula de identidad"],
  "dailyQuota": 20,
  "available": true
}
```

Si `available` se omite al crear, el servicio lo deja en `true`.

### Actualizar procedimiento

```http
PUT /api/catalog/procedures/{id}
```

Para actualizar se deben enviar `name`, `requirements`, `dailyQuota` y `available`.

## Reglas y validaciones

- Nombre obligatorio y máximo 160 caracteres.
- Cupo diario obligatorio y mayor o igual a 0.
- Cada requisito tiene un máximo de 500 caracteres.
- No se permite crear dos procedimientos con el mismo nombre ignorando mayúsculas/minúsculas.
- Un procedimiento inexistente devuelve `404`.
- Un nombre duplicado devuelve `409`.

## Autorización aplicada por el BFF

| Operación | Roles permitidos |
| --- | --- |
| GET catálogo | Admin, Operador, Cliente |
| POST catálogo | Admin |
| PUT catálogo | Admin |

Cliente puede leer el catálogo porque necesita los tipos de trámite disponibles para crear una solicitud, aunque no tiene acceso a la pantalla administrativa `/catalog`.

## Estructura

```text
src/main/java/.../
├── controller/
├── dto/
├── exception/
├── model/
├── repository/
└── service/
```

## Ejecución local

```powershell
cd .\barriodigitalcatalog
.\mvnw.cmd spring-boot:run
```

Servicio: `http://localhost:8082`

Healthcheck:

```text
GET /actuator/health
```

## Tests

```powershell
cd .\barriodigitalcatalog
.\mvnw.cmd test
.\mvnw.cmd clean package
```

Las pruebas cubren listado, creación, edición, validaciones, nombres duplicados y persistencia de requisitos.
