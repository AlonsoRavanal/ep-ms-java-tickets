# EventPass — Microservicio de Tickets

Microservicio REST desarrollado en **Java 21** y **Spring Boot 4.1.1** para la **emisión, consulta, validación, anulación y eliminación de entradas** de EventPass. Utiliza **Spring Data JPA / Hibernate** y una base de datos relacional **H2 con persistencia en archivo**.

> **Alcance de esta versión:** el servicio administra tickets utilizando los identificadores de orden, evento y comprador recibidos en las solicitudes. En el código de este repositorio **no se implementan llamadas a otros microservicios** para verificar órdenes, compradores, eventos o cupos; tampoco se implementa autenticación/autorización para los endpoints.

## 1. Tecnologías y requisitos

| Componente | Versión / uso |
| --- | --- |
| Java | **JDK 21** |
| Spring Boot | **4.1.1** (según `pom.xml`) |
| Spring Web MVC | API REST |
| Spring Data JPA + Hibernate | Persistencia y repositorios |
| H2 Database | Base de datos relacional local en archivo |
| Jakarta Bean Validation | Validación de la solicitud de emisión |
| Maven / Maven Wrapper | Compilación, pruebas y empaquetado |
| Postman | Pruebas manuales de los endpoints (opcional) |

Se requiere tener **Java 21** configurado y acceso a Internet para descargar dependencias la primera vez que se utilice Maven Wrapper. Git permite clonar el código.

## 2. Estructura del proyecto

```text
ep-ms-java-tickets/
├── .mvn/wrapper/
├── mvnw
├── mvnw.cmd
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/alonsoravanalrubenvelazquez/ep_ms_java_tickets/
│   │   │   ├── EpMsJavaTicketsApplication.java
│   │   │   ├── controller/
│   │   │   │   └── TicketRestController.java
│   │   │   ├── model/
│   │   │   │   ├── Ticket.java
│   │   │   │   └── dto/EmitirTicketsRequest.java
│   │   │   ├── repository/
│   │   │   │   └── TicketRepository.java
│   │   │   └── service/
│   │   │       ├── TicketService.java
│   │   │       └── TicketServiceImpl.java
│   │   └── resources/application.properties
│   └── test/java/com/alonsoravanalrubenvelazquez/ep_ms_java_tickets/
│       └── EpMsJavaTicketsApplicationTests.java
└── README.md
```

La aplicación está organizada en capas:

- **Controller:** expone las rutas HTTP y devuelve las respuestas REST.
- **Service:** implementa la lógica de negocio y sus restricciones.
- **Repository:** accede a la base de datos utilizando `JpaRepository`, consultas JPA y bloqueos pesimistas en ciertas actualizaciones.
- **Model y DTO:** definen la entidad persistida y los datos de entrada para emitir tickets.

## 3. Modelo de datos y reglas de negocio

### Entidad `Ticket`

La clase `Ticket` está marcada con `@Entity` y se almacena en la tabla **`tickets`**.

| Campo | Tipo Java | Descripción |
| --- | --- | --- |
| `id` | `Long` | Identificador autogenerado del ticket |
| `ordenId` | `Long` | Identificador de la orden asociada |
| `eventoId` | `Long` | Identificador del evento asociado |
| `compradorId` | `Long` | Identificador del comprador |
| `numeroEntrada` | `Integer` | Número consecutivo del ticket dentro de la orden |
| `codigoAcceso` | `String` | UUID único generado al guardar el ticket |
| `estado` | `EstadoTicket` | `EMITIDO`, `UTILIZADO` o `ANULADO` |
| `fechaEmision` | `LocalDateTime` | Fecha y hora de emisión |
| `fechaUso` | `LocalDateTime` | Fecha y hora de validación; inicialmente nula |

Se establece una restricción de unicidad para la combinación **`orden_id` + `numero_entrada`**. Además, el código de acceso es único. Los campos `ordenId`, `eventoId` y `compradorId` son **identificadores numéricos**, no relaciones `@ManyToOne` con entidades de otros microservicios.

### Reglas implementadas

- **Emisión:** requiere `ordenId`, `eventoId`, `compradorId` y `cantidad` mayores que cero. Crea tantos tickets como indica `cantidad`, numerados desde 1, inicialmente `EMITIDO`.
- **Reemisión:** si ya existen tickets para la misma `ordenId`, la emisión se rechaza con **409 Conflict**.
- **Validación:** busca un ticket por `codigoAcceso`. Solo puede pasar de `EMITIDO` a `UTILIZADO`; registra `fechaUso`.
- **Anulación:** solo permite cambiar de `EMITIDO` a `ANULADO`.
- **Eliminación:** solo permite eliminar físicamente un ticket que siga `EMITIDO`.
- **Concurrencia:** las consultas utilizadas para validar, anular y eliminar tickets emplean `PESSIMISTIC_WRITE`. Esto no implica que exista una coordinación transaccional entre microservicios.

## 4. Clonar y ejecutar localmente

### 4.1. Clonar el repositorio

En **CMD** o una terminal compatible:

```cmd
git clone https://github.com/AlonsoRavanal/ep-ms-java-tickets.git
cd ep-ms-java-tickets
```

> La rama que se clone debe contener la implementación completa y este README. Para la entrega final, integrar y comprobar los cambios en `main`.

### 4.2. Comprobar Java

```cmd
java -version
```

Debe utilizarse **JDK 21**. Verificar también que el directorio actual sea la raíz del proyecto, donde están `pom.xml` y `mvnw.cmd`.

### 4.3. Compilar y ejecutar pruebas automatizadas

```cmd
mvnw.cmd clean package
```

El comando compila, ejecuta las pruebas configuradas y genera el JAR cuando todas las fases terminan correctamente. También pueden ejecutarse las pruebas de forma independiente:

```cmd
mvnw.cmd test
```

> El repositorio incluye una prueba inicial `contextLoads()`. Las pruebas de negocio detalladas en la sección 7 son **manuales** y deben verificarse con Postman; no se afirma que estén automatizadas.

### 4.4. Iniciar la aplicación

```cmd
mvnw.cmd spring-boot:run
```

Una vez iniciada correctamente, la API estará disponible en:

```text
http://localhost:8084/api/tickets
```

Como alternativa, después de empaquetar la aplicación:

```cmd
java -jar target\ep-ms-java-tickets-0.0.1-SNAPSHOT.jar
```

El nombre mostrado corresponde al identificador y versión actuales del `pom.xml`; comprueba el nombre real del archivo en `target/` si cambias la versión.

## 5. Configuración de la base de datos H2

La configuración incluida en `src/main/resources/application.properties` es:

```properties
spring.application.name=ep-ms-java-tickets
server.port=8084

spring.datasource.url=jdbc:h2:file:./data/bdtickets
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

La base de datos H2 se guarda en el directorio `data/`, relativo a la carpeta desde la que ejecutas la aplicación. La opción `ddl-auto=update` permite actualizar el esquema durante el desarrollo.

### Consola H2

Mientras el microservicio esté iniciado, abre:

```text
http://localhost:8084/h2-console
```

Utiliza estos valores:

| Campo | Valor |
| --- | --- |
| JDBC URL | `jdbc:h2:file:./data/bdtickets` |
| User Name | `sa` |
| Password | *(en blanco)* |

Para observar los datos almacenados, en el editor SQL de H2 ejecuta:

```sql
SELECT * FROM tickets;
```

**Nota de seguridad:** estas credenciales, la consola H2 habilitada y la actualización automática del esquema corresponden al **entorno local de desarrollo**. No se recomienda exponer esta configuración a Internet ni subir `data/bdtickets.mv.db` al repositorio.

## 6. Endpoints REST

**URL base:** `http://localhost:8084/api/tickets`

| Método | Ruta | Función | Éxito |
| --- | --- | --- | --- |
| `GET` | `/api/tickets` | Listar todos los tickets | `200 OK` |
| `GET` | `/api/tickets/{id}` | Consultar un ticket por ID | `200 OK` |
| `GET` | `/api/tickets/comprador/{compradorId}` | Consultar tickets de un comprador | `200 OK` |
| `GET` | `/api/tickets/orden/{ordenId}` | Consultar tickets de una orden | `200 OK` |
| `POST` | `/api/tickets/emitir` | Emitir uno o varios tickets | `201 Created` |
| `PUT` | `/api/tickets/validar/{codigo}` | Validar un código de acceso | `200 OK` |
| `PUT` | `/api/tickets/{id}/anular` | Anular un ticket emitido | `200 OK` |
| `DELETE` | `/api/tickets/{id}` | Eliminar un ticket emitido | `204 No Content` |

No se dispone de un endpoint genérico para editar todos los atributos de un ticket. Las modificaciones permitidas se realizan mediante **validar** y **anular**.

## 7. Pruebas manuales con Postman

> Estas son **pruebas propuestas según el comportamiento del código**. Usa los IDs y códigos devueltos realmente por tu instalación; no asumas que serán iguales a los ejemplos.

En Postman, para peticiones con cuerpo, selecciona **Body → raw → JSON** y utiliza `Content-Type: application/json`.

### 7.1. Emitir tres tickets — caso válido

**POST** `http://localhost:8084/api/tickets/emitir`

```json
{
  "ordenId": 1001,
  "eventoId": 501,
  "compradorId": 1,
  "cantidad": 3
}
```

**Resultado esperado:** `201 Created` con una lista JSON de **tres tickets**, con `numeroEntrada` 1, 2 y 3; sus estados iniciales deben ser `EMITIDO`. Los identificadores, UUID y fechas se generan al persistir los registros.

**Importante:** estos números son datos de ejemplo. El microservicio **no verifica** por sí mismo que las órdenes, eventos o compradores existan en otros servicios.

### 7.2. Consultar las entradas

**GET** `http://localhost:8084/api/tickets`

**GET** `http://localhost:8084/api/tickets/orden/1001`

**GET** `http://localhost:8084/api/tickets/comprador/1`

**GET** `http://localhost:8084/api/tickets/1`

Para la consulta por ID, sustituye `1` por un ID existente de la respuesta de emisión. Las consultas de listas retornan un arreglo JSON (posiblemente vacío); la consulta de un ID inexistente debe devolver **404 Not Found**.

### 7.3. Validar el acceso de un ticket

Copia el valor real de `codigoAcceso` devuelto al emitir un ticket.

**PUT** `http://localhost:8084/api/tickets/validar/{codigoAcceso}`

Sustituye `{codigoAcceso}` por ese UUID. No se necesita body.

**Resultado esperado:** `200 OK`, con `estado` igual a `UTILIZADO` y `fechaUso` registrada.

**Repite la misma solicitud:** debe devolver **409 Conflict**, porque ese ticket ya fue utilizado.

### 7.4. Anular otro ticket

Selecciona un ticket distinto que siga `EMITIDO`.

**PUT** `http://localhost:8084/api/tickets/{id}/anular`

Sustituye `{id}` por el ID real. No se necesita body.

**Resultado esperado:** `200 OK` y `estado` igual a `ANULADO`.

**Repetir la anulación:** debe producir **409 Conflict**.

### 7.5. Eliminar otro ticket

Selecciona otro ticket que todavía esté `EMITIDO`.

**DELETE** `http://localhost:8084/api/tickets/{id}`

**Resultado esperado:** `204 No Content`. Un `GET` posterior del mismo ID debe devolver **404 Not Found**.

Intentar borrar un ticket `UTILIZADO` o `ANULADO` debe devolver **409 Conflict**.

### 7.6. Validar errores de emisión

**Caso A: cantidad negativa — POST** `/api/tickets/emitir`

```json
{
  "ordenId": 1002,
  "eventoId": 501,
  "compradorId": 1,
  "cantidad": -2
}
```

**Resultado esperado:** `400 Bad Request` por la validación `@Positive` de `cantidad`.

**Caso B: cantidad nula — POST** `/api/tickets/emitir`

```json
{
  "ordenId": 1003,
  "eventoId": 501,
  "compradorId": 1,
  "cantidad": null
}
```

**Resultado esperado:** `400 Bad Request` por `@NotNull`.

**Caso C: repetir una orden que ya tiene tickets — POST** `/api/tickets/emitir`

Envía nuevamente el JSON de la sección 7.1, con `ordenId` 1001, siempre que todavía exista algún ticket de esa orden.

**Resultado esperado:** `409 Conflict` por la protección frente a reemisiones de la misma orden.

**Caso D: código inexistente — PUT** `/api/tickets/validar/codigo-inexistente`

**Resultado esperado:** `404 Not Found`.

### 7.7. Comprobar persistencia

Abre la consola H2, ejecuta `SELECT * FROM tickets;` y comprueba que los registros creados, modificados o eliminados por Postman se reflejen en la base de datos.

## 8. Códigos de respuesta y manejo de errores

| Código HTTP | Casos previstos |
| --- | --- |
| `200 OK` | Consultas y operaciones de validar/anular exitosas |
| `201 Created` | Emisión exitosa |
| `204 No Content` | Eliminación exitosa |
| `400 Bad Request` | Campos de emisión nulos, no positivos o solicitud inválida |
| `404 Not Found` | ID o código de acceso inexistente |
| `409 Conflict` | Orden ya emitida, código ya utilizado/anulado o cambio de estado no permitido |

Los mensajes exactos y el formato JSON de error pueden variar según la configuración de Spring Boot. El código emplea `ResponseStatusException` para diversas reglas de negocio y `@Valid` para los campos del DTO de emisión.

## 9. Git y control de versiones

Para la evaluación se utiliza una estructura de ramas como:

- **`main`:** versión estable destinada a la entrega.
- **`develop`:** integración de los cambios antes de la versión estable.
- **`feature/*`:** desarrollo de componentes específicos, mediante commits descriptivos y Pull Requests.

Ejemplos de mensajes de commit: `feat: implementar emision de tickets`, `fix: corregir pom.xml de tickets`, `docs: agregar README de tickets`.

---

**Proyecto académico EventPass — Microservicio de Órdenes**  
**Asignatura:** Java: Diseño y Construcción de Soluciones Nativas en Nube (JVY0101).
