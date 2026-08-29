# starzz-boot

![Java](https://img.shields.io/badge/java-17-blue)
![Spring Boot](https://img.shields.io/badge/springboot-3.4.5-brightgreen)
![Maven](https://img.shields.io/badge/maven-3.9.12-orange)
![MySQL](https://img.shields.io/badge/mysql-9.6-purple)

This project is a production-style REST API built with Spring Boot, designed to demonstrate clean backend architecture, maintainable service-layer logic, and real-world API design patterns.  It demonstrates a backend design using MVC architecture, layered services, DTO mapping, validation, exception handling, database integration with MySQL, and JWT-based authentication with role-based access control.

This project serves two purposes:

**Portfolio showcase**: A clear example of building a maintainable, production-ready Spring Boot API.

**Learning resource**: A guided walkthrough for developers familiar with Spring Boot, showing why each layer exists and how the components interact.

The API manages a database of fictional galaxies, constellations, and stars. You’ll see how entities, DTOs, mappers, services, and controllers work together to process requests and return structured responses.
Mermaid diagrams illustrate request flows, allowing readers to quickly grasp the architecture while chapter walkthroughs provide deeper insight.

## Features

- REST API built with Spring Boot
- MVC architecture with layered services (Controller → Service → Repository)
- DTO mapping between entities and responses
- Validation using Jakarta Bean Validation
- Global exception handling
- MySQL database with Spring Data JPA
- BCrypt password hashing
- JWT-based authentication with Spring Security
- Role-based access control (ADMIN and USER roles)
- Unit testing using JUnit 5 and Mockito
- Integration testing using H2
- Database migration using Flyway
- Containerization using Docker
- OpenAPI documentation viewable in Swagger UI
- Postman collection included
- Architecture diagrams using Mermaid

## Key Design Decisions

- **DTO over Entity exposure**  
  Prevents tight coupling between API and database schema.

- **Service layer abstraction**  
  Keeps controllers thin and business logic centralized.

- **Manual mapping instead of MapStruct**  
  Improves transparency for learning purposes.

- **No user deletion**  
  Preserves referential integrity in a legacy schema.

- **Lazy loading for relationships**
  Avoids unnecessary database queries.

- **`@Transactional` on mutating integration tests**
  Rolls back after each test to keep the seeded dataset intact, preventing test interference.

- **H2 with `MODE=MySQL`**
  Allows reuse of the existing `load.sql` seed script without modification, keeping a single source of truth for test data.

- **Public GET endpoints for astronomy data**
  Galaxies, constellations, and stars are non-sensitive read-only data — making their GET endpoints public reduces friction for consumers.  User endpoints remain protected because they return PII.

- **Config-driven admin list**
  Admin usernames are defined in `application.yaml` rather than a dedicated database role, avoiding schema changes to the legacy database.

- **Sentinel password for forced password reset**
  New users are assigned a BCrypt-encoded sentinel value as their password.  Login is blocked when the sentinel is detected, and the response includes the user's ID so the client can redirect them to `PATCH /users/{id}/change-password`.  The change-password endpoint is public to avoid a deadlock where a token is required to reset a password but a token cannot be obtained without resetting first.

- **Stateless session management**
  Spring Security is configured with `SessionCreationPolicy.STATELESS`.  No session is created or used; every request must carry a valid JWT.

- **Custom `AuthenticationEntryPoint` returning 401**
  Spring Security's default behavior returns HTTP 403 for unauthenticated requests, which is misleading.  A custom entry point returns HTTP 401, correctly indicating that the client has not authenticated.

## The Dataset

Here is a diagram to describe the tables and their relationships:

![Database schema](assets/schema.png)

Stars are located in constellations, which are in turn located in galaxies.

The `galaxies`, `constellations` and `stars` tables contain the additional
fields `added_by` and `verified_by` to indicate the id of the users who made
the finding and verified it, respectively.

The database was created in MySQL.  The scripts to create the tables and
load the dummy data are included in `assets` for reference.

Our assumption is that we are working with a legacy database that cannot easily be altered.

## Test Coverage

The application is covered by both unit tests and integration tests. Unit tests cover the service and controller layers in isolation using JUnit 5 and Mockito. Integration tests verify the full request lifecycle — from HTTP request through controller, service, repository, and H2 database — using `@SpringBootTest` and `MockMvc`. Overall line coverage is **99.7%**, with the only uncovered code being the single-line main method in `StarzzBootApplication.java`.

![Coverage](assets/coverage.png)

The above report was generated by IntelliJ's built-in coverage report generator.

## The Application

This project was created in IntelliJ IDEA Ultimate.  It uses Java, Spring Boot, Maven and MySQL.

First we create a new Spring Boot project.  Instead of manual setup from [https://start.spring.io],
we use IntelliJ:

![Project Setup 1](assets/project_setup_1.png)

We don't add any dependencies for now; we will add them as we go along.

![Project Setup 2](assets/project_setup_2.png)

> **Note:** The screenshot shows Spring Boot 4.0.2, which was the version used at project creation. This project has since been migrated to Spring Boot 3.4.5 for better ecosystem support and documentation availability.

After clicking **Create**, IntelliJ generates starter project files (the project also includes Maven):

![Project Setup 3](assets/project_setup_3.png)

All code committed at each chapter is available with the commit message of the chapter name.

### Chapter 1: Setting up the routes

Project dependencies added:

    Spring Web
    Lombok

#### Overview

This chapter sets up the foundation of the Spring Boot application and demonstrates the basic routing and controller structure. It shows how requests flow from the DispatcherServlet to controllers and how endpoints are defined using the MVC pattern. Hardcoded responses are used to verify routing, providing a clear view of how controllers handle HTTP requests. By the end, the project has a working skeleton API that illustrates the architecture and request flow.

```mermaid
sequenceDiagram
    autonumber

    actor User
    participant DispatcherServlet
    participant Controller

    User ->> DispatcherServlet: HTTP Request
    DispatcherServlet ->> Controller: HTTP Request
    Controller -->> DispatcherServlet: 200 OK
    DispatcherServlet -->> User: HTTP Response
 ```

#### Endpoints

| Endpoint                | Method | Description                                              | Response |
|-------------------------|--------|----------------------------------------------------------|----------|
| `/constellations`       | GET    | Returns *Successfully called getConstellationList()*     | 200 OK   |
| `/constellations/{id}`  | GET    | Returns *Successfully called getConstellation(id)*       | 200 OK   |
| `/constellations`       | POST   | Returns *Successfully called registerConstellation()*    | 200 OK   |
| `/constellations/{id}`  | PUT    | Returns *Successfully called updateConstellation(id)*    | 200 OK   |
| `/constellations/{id}`  | DELETE | Returns *Successfully called deleteConstellation(id)*    | 200 OK   |
| `/galaxies`             | GET    | Returns *Successfully called getGalaxyList()*            | 200 OK   |
| `/galaxies/{id}`        | GET    | Returns *Successfully called getGalaxy(id)*              | 200 OK   |
| `/galaxies`             | POST   | Returns *Successfully called registerGalaxy()*           | 200 OK   |
| `/galaxies/{id}`        | PUT    | Returns *Successfully called updateGalaxy(id)*           | 200 OK   |
| `/galaxies/{id}`        | DELETE | Returns *Successfully called deleteGalaxy(id)*           | 200 OK   |
| `/stars`                | GET    | Returns *Successfully called getStarList()*              | 200 OK   |
| `/stars/{id}`           | GET    | Returns *Successfully called getStar(id)*                | 200 OK   |
| `/stars`                | POST   | Returns *Successfully called registerStar()*             | 200 OK   |
| `/stars/{id}`           | PUT    | Returns *Successfully called updateStar(id)*             | 200 OK   |
| `/stars/{id}`           | DELETE | Returns *Successfully called deleteStar(id)*             | 200 OK   |
| `/users`                | GET    | Returns *Successfully called getUserList()*              | 200 OK   |
| `/users/{id}`           | GET    | Returns *Successfully called getUser(id)*                | 200 OK   |
| `/users`                | POST   | Returns *Successfully called registerUser()*             | 200 OK   |
| `/users/{id}`           | PUT    | Returns *Successfully called updateUser(id)*             | 200 OK   |
| `/users/{id}`           | DELETE | Returns *Successfully called deleteUser(id)*             | 200 OK   |

*A Postman collection for all routes is included in* `assets/starzz-boot.postman_collection.json`

#### Sample Responses

GET /constellations
```json
{
    "text": "Successfully called getConstellationList()"
}
```

GET /constellations/100

```json
{
    "text": "Successfully called getConstellation(100)"
}
```

GET /galaxies

```json
{
    "text": "Successfully called getGalaxyList()"
}
```

GET /galaxies/100

```json
{
    "text": "Successfully called getGalaxy(100)"
}
```

GET /stars

```json
{
    "text": "Successfully called getStarList()"
}
```

GET /stars/100

```json
{
    "text": "Successfully called getStar(100)"
}
```

GET /users

```json
{
    "text": "Successfully called getUserList()"
}
```

GET /users/100

```json
{
    "text": "Successfully called getUser(100)"
}
```

<details>

<summary>Chapter Walkthrough</summary>

A Spring Boot application follows the MVC pattern for web applications.  So to build our application,
we add the **Spring Web** dependency in `pom.xml`. When we add a dependency in `pom.xml`, Maven
resolves and downloads it automatically from the configured repositories.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

(We remove the version number so Spring Boot can manage versioning for us).

We also add **Lombok** for automatic generation of getters, setters, constructors, etc.:

```xml
<dependency>
    <artifactId>lombok</artifactId>
    <groupId>org.projectlombok</groupId>
    <scope>annotationProcessor</scope>
</dependency>
```

(We add the scope because Lombok is only needed during compilation and shouldn't exist at runtime)

> **Note:** Whenever you add a dependency to `pom.xml`, click the Load Maven Changes button (the elephant icon) in IntelliJ to download it.
  
For now, we will be referring to the `src/main/java` folder of our project.  We will talk about
`src/test/java` later.

One of the auto-generated files in our project is `StarzzBootApplication.java` in package
`com.sanjayrisbud.starzzboot`:

```java
    @SpringBootApplication
    public class StarzzBootApplication {

        public static void main(String[] args) {
            SpringApplication.run(StarzzBootApplication.class, args);
        }

    }
```

This file defines the class `StarzzBootApplication`, which serves as the entrypoint to our
application.  The `@SpringBootApplication` annotation enables component scanning, which instructs
Spring to discover and register application components as **beans** within the application context.

In Spring, a bean is an object whose lifecycle is managed by the Spring container. Classes
annotated with `@RestController`, `@Service`, or `@Component` are automatically detected as
beans and can be injected where needed.

We now define our application's endpoints.  First we define a class to hold plain responses to
requests to our API endpoints.  In package `com.sanjayrisbud.starzzboot` we add a new package,
`dtos`.  In this package we create a class `Message`:

```java
    @AllArgsConstructor
    @Getter
    @ToString
    public class Message {
        private String text;
    }
```

We annotate the class with `@AllArgsConstructor`, `@Getter` and `@ToString` so Lombok will
generate a constructor, getter and toString() method for us.

When an instance of this class is returned as a response to an HTTP request, Spring Boot takes
care of serializing the instance to JSON.  When an instance of the class is expected as a
parameter to a method, Spring Boot takes care of deserializing the supplied JSON argument to the
needed instance.

We now define classes that would handle HTTP requests to our different API endpoints and
respond accordingly.  In package `com.sanjayrisbud.starzzboot` we add a new package,
`controllers`.  This package will contain the classes that will handle the requests.

A sample class in the `controllers` package is `ConstellationController`:

```java
    @RestController
    @RequestMapping("/constellations")
    public class ConstellationController {
        @GetMapping
        public Message getConstellationList() {
            return new Message("Successfully called getConstellationList()");
        }
    
        @GetMapping("/{id}")
        public Message getConstellation(@PathVariable Long id) {
            return new Message("Successfully called getConstellation(" + id + ")");
        }
    
        @PostMapping
        public Message registerConstellation(@RequestBody Message request) {
            return new Message("Successfully called registerConstellation(" + request + ")");
        }
        // ...
    }
```

We annotate the class with `@RestController`, which marks the class as a controller where every
method returns a domain object instead of an HTML view.  The `@RequestMapping` annotation
specifies the prefix of the endpoints that the class handles, in this case, `/constellations`.

The mapping annotations (`@GetMapping`, `@PostMapping`, `@PutMapping` and `@DeleteMapping`)
specify the HTTP verb (*GET, POST, PUT* or *DELETE*) that the class method handles.  The
remainder of the handled URL, i.e. after the prefix, is passed as an argument to the annotation.

For the class methods that have parameters, the annotation `@PathVariable` indicates that the
argument is from the mapping annotation's argument (inside the curly braces).  The `@RequestBody`
annotation indicates that the argument is from the body of the request.

The other classes in the `controllers` package follow a similar logic.

So far, our controllers return hardcoded responses. While this is useful to verify that our
routing layer works correctly, real-world applications persist and retrieve data from a database.

In the next chapter, we introduce the persistence layer using Spring Data JPA and connect our
application to a MySQL database.

</details>

### Chapter 2: Setting up the database

Project dependencies added:

    Spring Data JPA
    MySQL Driver
    Jakarta Bean Validation

#### Overview

This chapter introduces the persistence layer and connects the application to a MySQL database. Entities, JPA annotations, and relationships are defined, along with DTOs for clean data transfer. The repository and service layers are implemented to manage database interactions, and controllers are connected to services to handle requests and responses. Validation, exception handling, and structured response patterns are also included, making the API fully functional with proper separation of concerns.

##### HTTP GET

```mermaid
sequenceDiagram
    autonumber

    actor User
    participant DispatcherServlet
    participant ExceptionHandler
    participant Controller
    participant Service
    participant Repository
    participant Database

    User ->> DispatcherServlet: HTTP GET Request
    DispatcherServlet ->> Controller: GET handler
    Controller ->> Service: fetch data
    Service ->> Repository: fetch data
    Repository ->> Database: SQL query
    Database -->> Repository: results
    Repository -->> Service: entity
    Service -->> Controller: DTO
    Controller -->> DispatcherServlet: ResponseEntity<DTO>
    DispatcherServlet -->> User: HTTP Response
```

##### HTTP POST/PUT

```mermaid
sequenceDiagram
    autonumber

    actor User
    participant DispatcherServlet
    participant ExceptionHandler
    participant Controller
    participant Service
    participant Repository
    participant Database

    User ->> DispatcherServlet: HTTP POST | HTTP PUT Request
    DispatcherServlet ->> Controller: POST/PUT handler
    alt validation failed
        Controller -->> ExceptionHandler: validation exception
        ExceptionHandler -->> DispatcherServlet: HTTP 400
    end
    Controller ->> Service: process request
    Service ->> Repository: fetch related entities
    Repository ->> Database: SQL query
    Database -->> Repository: results
    Repository -->> Service: entity
    alt not found
        Service -->> ExceptionHandler: not found exception
        ExceptionHandler -->> DispatcherServlet: HTTP 404
    end
    Service ->> Repository: save and fetch data
    Repository ->> Database: SQL query
    Database -->> Repository: results
    Repository -->> Service: entity
    Service -->> Controller: DTO
    Controller -->> DispatcherServlet: ResponseEntity<DTO>
    DispatcherServlet -->> User: HTTP Response
```

##### HTTP DELETE

```mermaid
sequenceDiagram
    autonumber

    actor User
    participant DispatcherServlet
    participant ExceptionHandler
    participant Controller
    participant Service
    participant Repository
    participant Database

    User ->> DispatcherServlet: HTTP DELETE Request
    DispatcherServlet ->> Controller: DELETE handler
    Controller ->> Service: process request
    Service ->> Repository: fetch entity
    Repository ->> Database: SQL query
    Database -->> Repository: results
    Repository -->> Service: entity
    alt not found
        Service -->> ExceptionHandler: not found exception
        ExceptionHandler -->> DispatcherServlet: HTTP 404
    end
    Service ->> Repository: delete data
    Repository ->> Database: SQL query
    Database -->> Repository: null
    Repository -->> Service: confirmation
    Service -->> Controller: Void
    Controller -->> DispatcherServlet: ResponseEntity<Void>
    DispatcherServlet -->> User: HTTP Response
```

#### Updated Endpoints

| Endpoint                | Method | Description                                              | Response       |
|-------------------------|--------|----------------------------------------------------------|----------------|
| `/constellations`       | GET    | Returns the list of constellations                       | 200 OK         |
| `/constellations/{id}`  | GET    | Returns the constellation with the ID of *id*            | 200 OK         |
| `/constellations`       | POST   | Creates a new constellation record                       | 201 Created    |
| `/constellations/{id}`  | PUT    | Updates the constellation with the ID of *id*            | 200 OK         |
| `/constellations/{id}`  | DELETE | Deletes the constellation with the ID of *id*            | 204 No Content |
| `/galaxies`             | GET    | Returns the list of galaxies                             | 200 OK         |
| `/galaxies/{id}`        | GET    | Returns the galaxy with the ID of *id*                   | 200 OK         |
| `/galaxies`             | POST   | Creates a new galaxy record                              | 201 Created    |
| `/galaxies/{id}`        | PUT    | Updates the galaxy with the ID of *id*                   | 200 OK         |
| `/galaxies/{id}`        | DELETE | Deletes the galaxy with the ID of *id*                   | 204 No Content |
| `/stars`                | GET    | Returns the list of stars                                | 200 OK         |
| `/stars/{id}`           | GET    | Returns the star with the ID of *id*                     | 200 OK         |
| `/stars`                | POST   | Creates a new star record                                | 201 Created    |
| `/stars/{id}`           | PUT    | Updates the star with the ID of *id*                     | 200 OK         |
| `/stars/{id}`           | DELETE | Deletes the star with the ID of *id*                     | 204 No Content |
| `/users`                | GET    | Returns the list of users                                | 200 OK         |
| `/users/{id}`           | GET    | Returns the user with the ID of *id*                     | 200 OK         |
| `/users`                | POST   | Creates a new user record                                | 201 Created    |
| `/users/{id}`           | PUT    | Updates the user with the ID of *id*                     | 200 OK         |

*The Postman collection in* `assets/starzz-boot.postman_collection.json` *has been updated to the
format of requests used in this chapter.*

*A Note on User Deletion* - The application **does not** provide a *DELETE* endpoint for users.
This is a deliberate design decision:

- Users are referenced by galaxies, constellations, and stars (`addedBy` and `verifiedBy`).
- Cascading delete through these relationships would risk data loss and long transactional operations.
- The existing database schema cannot be modified to add a soft-delete flag (`isActive`), so soft deletion is not possible.
- Therefore, user deletion is intentionally disabled to preserve historical data integrity.

#### Sample Responses

GET /constellations

```json
[
    {
        "constellationId": 1,
        "constellationName": "Pleiades"
    },
    {
        "constellationId": 2,
        "constellationName": "Orion"
    }
]
```

GET /constellations/100

```json
{
    "constellationId": 100,
    "constellationName": "Cassiopeia",
    "galaxy": {
        "galaxyId": 1,
        "galaxyName": "Andromeda"
    },
    "addedBy": {
        "userId": 10,
        "username": "mike14288"
    },
    "verifiedBy": {
        "userId": 37,
        "username": "centwhistle45939"
    }
}
```

GET /galaxies

```json
[
    {
        "galaxyId": 1,
        "galaxyName": "Andromeda"
    },
    {
        "galaxyId": 2,
        "galaxyName": "Poseidon"
    }
]
```

GET /galaxies/100

```json
{
    "galaxyId": 100,
    "galaxyName": "Valhalla",
    "galaxyType": "Barred Spiral",
    "distanceMly": 5096,
    "redshift": 4,
    "massSolar": 5434403,
    "diameterLy": 647254,
    "addedBy": {
        "userId": 15,
        "username": "rtavener27922"
    },
    "verifiedBy": {
        "userId": 62,
        "username": "kchatres44202"
    }
}
```

GET /stars

```json
[
    {
        "starId": 1,
        "starName": "Sol"
    },
    {
        "starId": 2,
        "starName": "Rigel"
    }
]
```

GET /stars/100

```json
{
    "starId": 100,
    "starName": "Centaur",
    "starType": "red dwarf star",
    "constellation": {
        "constellationId": 38,
        "constellationName": "Cans Minor"
    },
    "rightAscension": 10,
    "declination": 78,
    "apparentMagnitude": -12,
    "spectralType": "O",
    "addedBy": {
        "userId": 1,
        "username": "ebalducci29128"
    },
    "verifiedBy": {
        "userId": 89,
        "username": "ogarriock20720"
    }
}
```

GET /users

```json
[
    {
        "userId": 1,
        "username": "ebalducci29128"
    },
    {
        "userId": 2,
        "username": "ldilloway50275"
    }
]
```

GET /users/100

```json
{
    "userId": 100,
    "username": "tperazzo50392",
    "email": "torey.perazzo@yahoo.com",
    "firstName": "Torey",
    "lastName": "Perazzo",
    "dateOfBirth": "1998-05-26"
}
```

<details>

<summary>Chapter Walkthrough</summary>

To persist and retrieve data, we now introduce a persistence layer. In Spring Boot applications,
this is typically achieved using Spring Data JPA on top of a relational database.  In our case,
it is our **starzz** database on MySQL.

To allow our application to interact with the database, we would first need to add the
**MySQL driver** to allow our code to execute SQL via JDBC (JDBC is a low-level API
for accessing databases):

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

(We add the scope because the driver is only needed at runtime and not needed to compile the code)

The MySQL driver enables JDBC connectivity. We also add **Spring Data JPA** to provide
higher-level, object-oriented database interaction.  (We use Spring Data JPA to
interact with the database in an object-oriented way. Hibernate, the default JPA
implementation, handles the actual SQL generation and persistence management.)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

Since data that we add to the database comes from user input in the requests, we would
need to validate them before modifying the database.  We add **Jakarta Bean Validation** to
implement request validation:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

We add the snippets above to `pom.xml`.

With dependencies added, we now configure our data source so Spring Boot can connect to the MySQL
database.  In `src/main/resources/application.yaml` we add the application's data source:

```yaml
spring:
    datasource:
        url: jdbc:mysql://localhost:3306/starzz
        username: root
        password: <your database password>
    jpa:
        show-sql: true
```

(`username` and `password` are the credentials to our database server, accessible via `url`.
We also set `show-sql` under `jpa` to `true` so the application will log the SQL that
Hibernate generates.)

We now create our code to interact with our database.

Since Spring Data JPA allows us to work with database tables in an object-oriented way, we can
add classes to abstract tables and the operations on them.  In `com.sanjayrisbud.starzzboot`,
we add a new package, `models`, to contain the table abstractions.  An example class is
`Constellation`:

```java
    @Entity
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @Setter
    @Table(name = "constellations")
    public class Constellation {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "constellation_id")
        private Integer id;
    
        @Column(name = "constellation_name")
        private String name;
        // ...
    }
```

The `@Entity` annotation specifies that the class is an abstraction of a database table, and
`@Table` specifies the actual table name.  Each instance of this class represents one record
in the table.  `@AllArgsConstructor`, `@NoArgsConstructor`, `@Getter` and `@Setter` are for
Lombok to generate a constructor requiring all fields, a default constructor, getters and setters
for all fields, respectively (these Lombok-generated methods are needed by Spring Data JPA).
`@Builder` (also from Lombok) allows us to create an entity instance using the **Builder**
pattern i.e. one field at a time.  This improves readability by showing which values are
assigned to which fields.

We then have fields with `@Column` corresponding to the table columns.  `@Id` indicates the
primary key and `@GeneratedValue` indicates that the value is generated by the table.

We then define relationships with other entities.  Schema-wise, we have a many-to-one relationship
between `constellations` and `galaxies`; the foreign key is `galaxy_id`  We express that relationship
using:

```java
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "galaxy_id")
    private Galaxy galaxy;
```

Our entity for the `galaxies` table is `Galaxy`; we define a field `galaxy` to refer to it.  Since
this field uses a foreign key to refer to the parent `Galaxy` we annotate the field with
`@JoinColumn` to specify the foreign key.  `@ManyToOne` specifies the relationship i.e. many to one.
For these types of relationships, the default behavior of Spring Data JPA when it loads a particular entity into memory is to automatically fetch the related entity.  This is called eager loading.  In our
case, we indicate `fetch` to override this default and tell Spring Data JPA to fetch the related
entity only when we explicitly ask for it, which is called lazy loading.

In addition, this field has a symmetric field in the entity `Galaxy`:

```java
    @OneToMany(targetEntity = Constellation.class, mappedBy = "galaxy", cascade = CascadeType.REMOVE)
    private Set<Constellation> constellations = new HashSet<>();
```

This field does not correspond to a physical column in the `galaxies` table; it represents the
inverse side of the relationship.  It is a field for the set of its children `Constellation`
objects.  `@OneToMany` specifies  the relationship, `targetEntity` indicates the related entity,
and `mappedBy` indicates the symmetric field.  `cascade` specifies what operations on the parent
cascade to the children entities; in this case, deleting a galaxy also deletes its children
constellations.

Two other fields in `Constellation` are defined similarly:

```java
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by")
    private User addedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;
```

The symmetric fields in the `User` entity corresponding to the table `users`:

```java
    @OneToMany(targetEntity = Constellation.class, mappedBy = "addedBy")
    private Set<Constellation> constellationsAdded = new HashSet<>();

    @OneToMany(targetEntity = Constellation.class, mappedBy = "verifiedBy")
    private Set<Constellation> constellationsVerified = new HashSet<>();
```

The other classes in the `models` package follow a similar logic.

Now that we have defined our entities as object-oriented representations of database tables,
we introduce the repository layer.  While entities abstract the structure of our tables,
repositories abstract the operations performed on those entities. They provide a clean interface
for querying, saving, updating, and deleting data without requiring us to write SQL manually.

Our repositories extend the interface `JpaRepository`. Spring Data JPA automatically generates
implementations of these interfaces at runtime and registers them as beans in the application
context. This allows the repositories to be injected into services or controllers where needed.

In `com.sanjayrisbud.starzzboot`, we add a new package, `repositories`, to contain the repositories.
An example interface is`ConstellationRepository`:

```java
    public interface ConstellationRepository extends JpaRepository<Constellation, Integer> {
    }
```

The other interfaces in the `repositories` package follow a similar logic.

Whenever we send data as part of responses, we usually don't send raw entities from our database.
We create mappings from our entities to custom objects, and send those objects instead.  These
custom objects are known as data transfer objects.  We need to create DTOs for our entities.
In `com.sanjayrisbud.starzzboot.dtos`, we add our DTOs.  An example class is `ConstellationSummaryDto`:

```java
    public record ConstellationSummaryDto (
        Integer constellationId,
        String constellationName
    ) {}
```

Since we only need fields for the constellation's id and name, we define this class as a
`record` instead.  This guarantees immutability and clearly signifies its intent as a
data carrier.

Another example is `ConstellationDetailsDto`:

```java
    @Builder
    @Data
    @JsonPropertyOrder({
        "constellationId",
        "constellationName",
        "galaxy",
        "addedBy",
        "verifiedBy"
    })
    public class ConstellationDetailsDto {
        private Integer constellationId;
        private String constellationName;
        private GalaxySummaryDto galaxy;
        private UserSummaryDto addedBy;
        private UserSummaryDto verifiedBy;
    }
```

This DTO has additional fields to display the parent galaxy, and the adder and verifier.  These
fields are themselves DTOs.  `@JsonPropertyOrder` specifies the order of the fields in the DTO
serialization.  `@Data` tells Lombok to generate boilerplate code such as getters, setters, equals,
hashCode, and toString.  Although the class only carries data, we define it as a regular class instead
of a record because we construct instances using the builder pattern, which allows fields to be set
more flexibly.  We specify this using `@Builder`.

We also have a DTO to accept JSON input when processing requests to add or update constellations:

```java
    @Builder
    @Data
    public class ConstellationDto {
        @NotBlank private String constellationName;
        @NotNull private Integer galaxyId;
        @NotNull private Integer adderId;
        private Integer verifierId;
    }
```

We add validation annotations to the fields.  `@NotNull` specifies that the field must be present
and cannot be set to `null`.  `@NotBlank` specifies that the field must be present and cannot be
`null`, blank, or an empty string.  When all the validations pass, the JSON is deserialized to
a DTO instance.

The other classes in the `dtos` package follow a similar logic.

We would then need classes to map entities to DTOs.  The **MapStruct** dependency can help
generate mappings for us, but for the purposes of this application we will generate them manually.
In `com.sanjayrisbud.starzzboot`, we add a new package, `mappers`, to contain the classes for
our entity mappers.  An example class is `ConstellationMapper`:

```java
    @AllArgsConstructor
    @Component
    public class ConstellationMapper {
        private final GalaxyMapper galaxyMapper;
        private final UserMapper userMapper;
    
        public ConstellationSummaryDto toSummaryDto(Constellation constellation) {
            if (constellation == null)
                return null;
    
            return new ConstellationSummaryDto(constellation.getId(), constellation.getName());
        }

        public ConstellationDetailsDto toDetailsDto(Constellation constellation) {
            if (constellation == null)
                return null;
    
            var galaxy = galaxyMapper.toSummaryDto(constellation.getGalaxy());
            var addedBy = userMapper.toSummaryDto(constellation.getAddedBy());
            var verifiedBy = userMapper.toSummaryDto(constellation.getVerifiedBy());
    
            return ConstellationDetailsDto.builder()
                    .constellationId(constellation.getId())
                    .constellationName(constellation.getName())
                    .galaxy(galaxy)
                    .addedBy(addedBy)
                    .verifiedBy(verifiedBy)
                    .build();
        }
    }
```

`@Component` tags this class as a bean.  In `toSummaryDto()` we create a summary DTO using a regular
constructor.  In `toDetailsDto()` we first get summary DTOs of the related entities, then use the
builder pattern to create the details DTO one field at a time.

The other classes in the `mappers` package follow a similar logic.

Whenever our application processes requests, errors may occur, e.g. a request for a nonexistent
constellation.  Instead of letting the application fail silently, we throw exceptions to convey
information about these events.  We do this with exception handling.  In package
`com.sanjayrisbud.starzzboot.dtos` we add a new class, `ErrorResponseDto`, to contain the
error message:

```java
    public record ErrorResponseDto(
        String message,
        LocalDateTime timestamp
    ) {}
```

It has fields for the error message and the error's timestamp.

In `com.sanjayrisbud.starzzboot`, we add a new package, `exceptions`, to contain the classes
for our exception handling.  We create class `ResourceNotFoundException`:

```java
    @Getter
    public class ResourceNotFoundException extends RuntimeException {
    
        private final String resourceName;
        private final Integer resourceId;
    
        public ResourceNotFoundException(String resourceName, Integer resourceId) {
            super(resourceName + " with id " + resourceId + " not found.");
            this.resourceName = resourceName;
            this.resourceId = resourceId;
        }
    
    }
```

This will be the custom exception thrown whenever our application searches for nonexistent
resources.

In the same package, we create class `GlobalExceptionHandler`:

```java
    @RestControllerAdvice
    public class GlobalExceptionHandler {
    
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponseDto> handleNotFound(ResourceNotFoundException ex) {
    
            var errorResponse = new ErrorResponseDto(ex.getMessage(), LocalDateTime.now());
    
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
```

The `@RestControllerAdvice` annotation makes this class handle all our custom exceptions.
`@ExceptionHandler` specifies the method to handle specific exception classes.  In this case,
`handleNotFound()` is the handler for `ResourceNotFoundException`.  There are also handlers
for validation exceptions thrown by Spring Boot.

We now introduce a service layer to contain our application's business logic.  Introducing a
service layer ensures a clean separation of concerns; controllers can focus solely on HTTP
request handling and not need to worry about the business rules.

In `com.sanjayrisbud.starzzboot`, we add a new package, `services`, to contain the classes for our
service layer.  An example class is `ConstellationService`:

```java
    @AllArgsConstructor
    @Service
    public class ConstellationService {
        private final ConstellationRepository constellationRepository;
        private final ConstellationMapper constellationMapper;
        private final GalaxyService galaxyService;
        private final UserService userService;
    
        public List<ConstellationSummaryDto> getConstellationList() {
            return constellationRepository.findAll().stream()
                    .map(constellationMapper::toSummaryDto)
                    .toList();
        }
    
        public ConstellationDetailsDto getConstellation(Integer id) {
            return constellationMapper.toDetailsDto(findById(id));
        }
    
        public ConstellationDetailsDto registerConstellation(ConstellationDto request) {
            var constellation = Constellation.builder()
                    .name(request.getConstellationName())
                    .galaxy(galaxyService.getEntity(request.getGalaxyId(), null))
                    .addedBy(userService.getEntity(request.getAdderId(), null))
                    .verifiedBy(userService.getEntity(request.getVerifierId(), null))
                    .build();
    
            constellationRepository.save(constellation);
            return constellationMapper.toDetailsDto(constellation);
        }
        // ...
    }
```

`@Service` specifies that this class is a bean.  It has private fields for beans, which were
automatically injected by Spring.  We then define methods to implement CRUD operations.

The other classes in the `services` package follow a similar logic.

Finally, we rewrite our `ConstellationController`:

```java
    @AllArgsConstructor
    @RestController
    @RequestMapping("/constellations")
    public class ConstellationController {
    
        private final ConstellationService constellationService;
    
        @GetMapping
        public List<ConstellationSummaryDto> getConstellationList() {
            return constellationService.getConstellationList();
        }
    
        @GetMapping("/{id}")
        public ResponseEntity<ConstellationDetailsDto> getConstellation(@PathVariable Integer id) {
            return ResponseEntity.ok(constellationService.getConstellation(id));
        }
    
        @PostMapping
        public ResponseEntity<ConstellationDetailsDto> registerConstellation(
                @Valid @RequestBody ConstellationDto request,
                UriComponentsBuilder uriBuilder) {
            var newConstellation = constellationService.registerConstellation(request);
            var uri = uriBuilder.path("/constellations/{id}")
                    .buildAndExpand(newConstellation.getConstellationId()).toUri();
            return ResponseEntity.created(uri).body(newConstellation);
        }
        // ...
    }
```

We introduce a private field for the service bean.  When the endpoint handlers receive requests,
they now redirect those requests to different methods in the service bean, and then generate
appropriate responses based on the results of those methods.  Instead of returning DTOs directly,
the controller methods now return a `ResponseEntity<T>`, a generic wrapper that allows us to
specify the HTTP status code and response body.

The other classes in the `controllers` package follow a similar logic.

Now that we’ve finished setting up the database and our controllers, our app is starting to grow.
With more pieces working together, it’s easy to accidentally break something when we add new
features. That’s why it’s a good idea to start writing **unit tests** now — they help us make
sure each part works on its own and keep everything running smoothly as the project grows.

Before we write tests, we need to make a couple of enhancements to our persistence layer.  In the next chapter, we enable our application to retrieve secrets from a `.env` file instead of putting them directly in `application.yaml`.  We also set up **H2**, an in-memory database that our tests can use instead of our MySQL database.

</details>

### Chapter 3: Setting up persistence layer enhancements

Project dependencies added:

    Spring Dotenv
    H2 Database Engine

#### Overview

This chapter enhances the persistence layer in two ways. First, database credentials are moved out of `application.yaml` and into a `.env` file, keeping secrets out of version control. Second, an H2 in-memory database is configured for the test environment, ensuring that automated tests run in isolation without touching the production MySQL database.

```mermaid
  flowchart     
      A[Start Application] --> B{Production or Test?}
      B -- Test --> C[Load test application.yaml]
      B -- Production --> D[Load .env via spring-dotenv]
      D --> E[Load main application.yaml]
      C --> F[Connect to H2 database]
      E --> G[Connect to MySQL database]
```

<details>

<summary>Chapter Walkthrough</summary>

To interact with other systems, our application depends on secrets such as database credentials.  Currently, these secrets are stored in `application.yaml`.  This is not recommended.  For security, we should instead store secrets in a secrets repository and make `application.yaml` read the secrets from there.  For our project, we use a `.env` file as our secrets' repository.  To enable our application to read `.env` files, we need the **Spring Dotenv** dependency.  We add this to `pom.xml`:

```xml
<dependency>
    <groupId>me.paulschwarz</groupId>
    <artifactId>spring-dotenv</artifactId>
    <version>3.0.0</version>
</dependency>
```

(We specify the version number because this is a third party dependency and not managed by Spring Boot).

We create a file `.env` in the root of our project, containing:

```bash
DB_URL=jdbc:mysql://localhost:3306/starzz
DB_USERNAME=root
DB_PASSWORD=<your database password>
```

And modify `src/main/resources/application.yaml` to refer to `.env`:

```yaml
spring:
    application:
        name: starzz-boot
    datasource:
        url: ${DB_URL}
        username: ${DB_USERNAME}
        password: ${DB_PASSWORD}
    jpa:
        show-sql: true
```

We now set up our in-memory database.

Open `src/test/java/com/sanjayrisbud/starzzboot/StarzzBootApplicationTests.java`.  This is a starter test file automatically created by IntelliJ.  

```java
    @SpringBootTest
    class StarzzBootApplicationTests {

        @Test
        void contextLoads() {
        }

    }
```

Briefly, `@SpringBootTest` is used to create a test environment.  It looks for the main class (which has `@SpringBootApplication`) and uses it to start the application context.

When we execute the file, notice:

![Persistence 1](assets/persistence_1.png)

It simply loads the application and then exits.  However, reading through the logs, we see the *Database version: 9.6*.  We know that our underlying database is *MySQL 9.6*.  Since the logs don't say otherwise, we can assume that our test environment uses our MySQL database.  This is not ideal.  We want our automated tests to use a different database from production so our tests don't pollute the production database.  In addition, we prefer that our test database be located in memory rather than a physical database so our tests execute faster.

This is where H2 can help.  It is a lightweight and fast SQL database written in Java. It is particularly useful for testing and development because it allows you to create a temporary database that is automatically destroyed when the application stops.  To enable our application to use H2 for testing, we add the **H2 Database Engine** dependency to `pom.xml`:

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

We then create a new file `src/test/resources/application.yaml`:

```yaml
spring:
    datasource:
        url: jdbc:h2:mem:testdb
        username: sa
        password:
        driver-class-name: org.h2.Driver
    jpa:
        hibernate:
            ddl-auto: create-drop
        show-sql: true
    h2:
        console:
            enabled: true
```

Unlike `src/main/resources/application.yaml`, this file does not reference `.env` variables — H2 credentials are hardcoded since they are not real secrets. It also includes H2-specific settings: `driver-class-name` to specify the H2 driver, `ddl-auto: create-drop` to automatically create the schema at the start of each test run and drop it at the end, and `h2.console.enabled` to enable the H2 web console for inspection during development.

When we execute `src/test/java/com/sanjayrisbud/starzzboot/StarzzBootApplicationTests.java`, notice:

![Persistence 2](assets/persistence_2.png)

We now see several logs containing *SQL CREATE, ALTER* and *DROP*, implying that a blank copy of our database is being created (Hibernate is able to do this by inspecting our data repository beans).  The *H2 console...* log confirms that the test database is indeed the H2 database.

We have now completed setting up testing prerequisites.  In the next chapter, we’ll dive into **unit testing** using **JUnit 5** and **Mockito**.  We’ll cover how to test our services and controllers, mock dependencies, and build a solid foundation so future updates won’t surprise us.

</details>

### Chapter 4: Setting up unit tests

Project dependencies added:

    None

#### Overview

This chapter introduces unit testing using JUnit 5 and Mockito. Service layer tests are written using `@ExtendWith(MockitoExtension.class)`, with repositories and external services mocked and mappers used as real instances via `@Spy`. Controller tests use `@WebMvcTest` to test the web layer in isolation, with services replaced by Mockito mocks via `@MockitoBean`.

```mermaid
flowchart TD
    A[Test Class] --> B{Annotation?}
    B -- "@ExtendWith(MockitoExtension)" --> C[No Spring context loaded]
    B -- "@WebMvcTest" --> D[Web layer only]
    B -- "@SpringBootTest" --> E[Full application context]
    C --> F[Service tests\nFast, isolated]
    D --> G[Controller tests\nMedium speed]
    E --> H[Integration tests\nSlow, end-to-end]
```

<details>

<summary>Chapter Walkthrough</summary>

To test our application, we will use **JUnit 5** and **Mockito**. JUnit 5 provides the framework
to write and run unit tests, while Mockito allows us to create mocks of our dependencies so we
can test each layer in isolation. To use them in our project, we need the `spring-boot-starter-test`
dependency.  However, we don’t need to manually add this dependency to our `pom.xml`.  When we
created the project, the dependency had already been added to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

(The scope specifies that the dependency is only needed during testing and won't be included when
the application package is built.)

We will use **WebMvcTest** to test our controllers.  In Spring Boot 3, `@WebMvcTest` support is already included in `spring-boot-starter-test`, so no additional dependency is needed.

Up until now, we have been writing all our source code in the `src/main/java` folder of our project:

![Project Structure 1](assets/project_structure_1.png)

We will be writing our tests in the `src/test/java` folder of our project, also added upon project
creation:

![Project Structure 2](assets/project_structure_2.png)

This folder will have a similar package structure to `src/main/java`.  The testing class' name is
the same as the name of the class being tested, with the suffix `Test`.

We start with writing unit tests for our services.  In package `com.sanjayrisbud.starzzboot.services`
we create class `ConstellationServiceTest` to contain our unit tests for `ConstellationService`:

```java
    @ExtendWith(MockitoExtension.class)
    class ConstellationServiceTest {
        private final UserMapper userMapper = new UserMapper();
        private final GalaxyMapper galaxyMapper = new GalaxyMapper(userMapper);
        @Spy
        private final ConstellationMapper constellationMapper =
                new ConstellationMapper(galaxyMapper, userMapper);
    
        @Mock
        private ConstellationRepository constellationRepository;
        @Mock
        private GalaxyService galaxyService;
        @Mock
        private UserService userService;
        @InjectMocks
        private ConstellationService constellationService;
    
        @Test
        void getEntityGivenNewIdIsNullReturnsNull() {
            Constellation c1 = constellationService.getEntity(null, null);
            assertNull(c1);
    
            Constellation c2 = constellationService.getEntity(null,
                    Constellation.builder().build());
            assertNull(c2);
        }
    
        @Test
        void getEntityGivenNullCurrentConstellationReturnsConstellationFromRepository() {
            var existingConstellation = Constellation.builder().build();
            when(constellationRepository.findById(1))
                    .thenReturn(Optional.of(existingConstellation));
    
            Constellation c = constellationService.getEntity(1, null);
    
            assertEquals(existingConstellation, c);
        }
        // ...
    }
```

To test `ConstellationService`, we create an instance of it.  Since it depends on other classes,
we would also need instances of those dependencies.

In unit tests, we mock dependencies so the class being tested runs in isolation.  Instead of
calling real implementations like repositories or other services, mocks return predefined
results.  This keeps tests fast, predictable, and focused only on the logic of the unit under
test rather than external systems like databases.

We use `@Mock` to create mock instances of `ConstellationRepository`, `GalaxyService` and
`UserService`.  `ConstellationMapper` is a dependency, but since it only contains lightweight,
deterministic mapping logic, we can use a real instance (annotated with `@Spy`) instead of
mocking it. This keeps tests simple and readable.

We then use `@InjectMocks` to inject these dependencies into our `ConstellationService`
instance.  Finally, `@ExtendWith(MockitoExtension.class)` enables Mockito's functionality.

`@Test` specifies that the method is a test.  Both methods above are tests for `getEntity()` in
`ConstellationService`.  There are actually 5 tests for `getEntity()` alone.  Multiple tests for
the same method are beneficial.  This ensures that the method is thoroughly tested.

Private methods contain logic that affects the behavior of public methods.  We verify that logic
indirectly by testing the public methods that use them, rather than testing private methods
directly.  This allows tests to verify behavior without exposing internal implementation details.

Notice also the `assertNull()` and `when()` methods.  They are static methods defined in the
classes *Assertions* and *Mockito*, respectively.  For common testing helpers, we can statically
import them:

```java
    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.Mockito.*;
```

This allows us to call the static methods without having to prefix them with the class name.
This is purely for readability.

The other classes in the `services` package follow a similar logic.

We now write unit tests for our controllers.  In package `com.sanjayrisbud.starzzboot.controllers`
we create class `ConstellationControllerTest` to contain our unit tests for `ConstellationController`:

```java
    @WebMvcTest(ConstellationController.class)
    class ConstellationControllerTest {
        @Autowired
        private MockMvc mockMvc;
    
        @Autowired
        private ObjectMapper objectMapper;
    
        @MockitoBean
        private ConstellationService constellationService;
    
        @Test
        void getConstellationListReturns200WithEmptyList() throws Exception {
            when(constellationService.getConstellationList()).thenReturn(List.of());
    
            mockMvc.perform(get("/constellations"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    
        @Test
        void getConstellationGivenExistingIdReturns200AndConstellation() throws Exception {
            Constellation c = buildConstellation();
            ConstellationDetailsDto dto = buildConstellationDetailsDto(c);
    
            when(constellationService.getConstellation(c.getId())).thenReturn(dto);
    
            mockMvc.perform(get("/constellations/" + c.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.constellationId").value(c.getId()));
        }
    
        @Test
        void getConstellationGivenNonExistentIdReturns404AndError() throws Exception {
            Integer nonExistentId = 999;
    
            when(constellationService.getConstellation(nonExistentId))
                    .thenThrow(new ResourceNotFoundException("Constellation", nonExistentId));
    
            mockMvc.perform(get("/constellations/" + nonExistentId))
                    .andExpect(status().isNotFound());
        }
        // ...
    }
```

In the previous chapter, we briefly encountered `@SpringBootTest` and mentioned that it loads the full application context. For controller tests, we don't need the full context — we only care about the web layer.  `@WebMvcTest` loads the web layer — controllers, exception handlers, filters, and other web-related beans — without starting a real server or connecting to a database.  We specify the controller class (in this case, `ConstellationController`) so that only that controller is loaded, rather than all controllers in the application.  However, service dependencies are not part of the web layer and are not autoloaded, so we must provide mock instances of these dependencies.

Since `ConstellationController` has a dependency on `ConstellationService`, we declare a mock instance of it using `@MockitoBean`.  `MockMvc` is the actual class that allows us to generate mock HTTP calls.  `ObjectMapper` serializes Java objects to JSON strings for use as request bodies in POST and PUT tests — this is necessary because MockMvc's `.content()` method expects a `String`, not a Java object.  We declare instances of both classes as beans and annotate them with `@Autowired`.  When a `ConstellationControllerTest` instance is later initialized for executing tests, all three beans are automatically injected into the instance.

> **Note:** In service tests, Mockito injects dependencies via constructor injection. In controller tests, Spring manages the beans and are injected via field injection. Different test types, different frameworks for wiring.

Our tests all have the `throws Exception` clause.  This is because when we call `mockMvc.perform()` it may throw an exception.

The other classes in the `controllers` package follow a similar logic.

In the next chapter, we introduce integration tests. Unlike the unit tests in this chapter, integration tests load the full application context and interact with a database. This allows us to verify that all layers of the application work correctly together.

</details>

### Chapter 5: Setting up integration tests

Project dependencies added:

    None

#### Overview

This chapter introduces integration tests.  Integration tests verify that all layers of the application work correctly together — controller, service, repository, and database — using a real Spring context and an in-memory H2 database.

```mermaid
sequenceDiagram
    autonumber

    participant MockMvc
    participant DispatcherServlet
    participant ExceptionHandler
    participant Controller
    participant Service
    participant Repository
    participant H2

    MockMvc ->> DispatcherServlet: HTTP Request
    DispatcherServlet ->> Controller: request handler
    alt validation failed
        Controller -->> ExceptionHandler: validation exception
        ExceptionHandler -->> DispatcherServlet: HTTP 400
    end
    Controller ->> Service: process request
    Service ->> Repository: fetch related entities
    Repository ->> H2: SQL query
    H2 -->> Repository: results
    Repository -->> Service: entity
    alt not found
        Service -->> ExceptionHandler: not found exception
        ExceptionHandler -->> DispatcherServlet: HTTP 404
    end
    Service ->> Repository: save and fetch data
    Repository ->> H2: SQL query
    H2 -->> Repository: results
    Repository -->> Service: entity
    Service -->> Controller: DTO
    Controller -->> DispatcherServlet: ResponseEntity<DTO>
    DispatcherServlet -->> MockMvc: HTTP Response
```

<details>

<summary>Chapter Walkthrough</summary>

We use integration tests to check that all the components that we have coded work together as expected.  Refer to the diagram in the *Overview*.  Notice that it is similar to the diagrams in the *Overview* of **Chapter 2**, i.e. replace **User** with **MockMvc** as we did in Chapter 3, and **Database** with the **H2** in-memory database we configured in Chapter 3.  Essentially, this is what integration testing is all about; testing the whole application flow while mocking the ends. 

We revisit `src/test/java/com/sanjayrisbud/starzzboot/StarzzBootApplicationTests.java`:

```java
    @SpringBootTest
    class StarzzBootApplicationTests {

        @Test
        void contextLoads() {
        }

    }
```

`@SpringBootTest` is used to create a test environment.  `@SpringBootTest` loads everything that `@SpringBootApplication` does i.e. a fully-fledged application context. It even loads the `application.yaml` file. It also enables beans to inject using `@Autowired`.

We learned from Chapter 4 that `MockMvc` allows us to generate HTTP requests, thus we also need to inject an instance of it in our test class.  However unlike with `@WebMvcTest`, `@SpringBootTest` won't configure `MockMvc` automatically.  We need to specify this behavior with `@AutoConfigureMockMvc`.

Instead of having one class test all our endpoints, we can make each controller (`Galaxy`, `Constellation`, `Star`, `User`) have a dedicated integration test class. The structure mirrors the controller tests from Chapter 4. These 4 classes would each need `@SpringBootTest`; Spring will cache 1 application context to be used by these classes. There are no mocks — all beans are real, and requests flow through the full stack.

We will need to seed our database with some test data.  We can do this in our test files with `@BeforeAll` methods that insert seed data.  However, a cleaner approach would be to leverage our load script in `assets/load.sql`.  We configure Spring Boot to auto-execute the script once when the H2 database initializes. This keeps test classes free of setup boilerplate and ensures all tests share a consistent, realistic dataset (100 records per table).

In unit tests, we mock operations on the database.  In integration tests, we don't mock database calls.  Tests that modify data (*POST, PUT, DELETE*) might affect other tests.  To avoid this, these tests are annotated with `@Transactional`. Spring rolls back the transaction after each test, leaving the seeded data intact for other tests.  This allows tests to run independently without interference.

We now create our integration tests.

We first need to update `src/test/resources/application.yaml`:

```yaml
spring:
    datasource:
        url: jdbc:h2:mem:testdb;MODE=MySQL
        username: sa
        password:
        driver-class-name: org.h2.Driver
    jpa:
        hibernate:
            ddl-auto: create-drop
        show-sql: true
        defer-datasource-initialization: true
    sql:
        init:
            mode: always
            data-locations: file:assets/load.sql
    h2:
        console:
            enabled: true
```

`MODE=MySQL` is required because `load.sql` uses MySQL backtick quoting (`` `table_name` ``). Without it, H2 rejects the syntax.

`defer-datasource-initialization: true` is required when using `ddl-auto: create-drop`. By default, Spring Boot runs SQL initialization scripts before Hibernate creates the schema, causing the inserts to fail against non-existent tables. Deferring initialization ensures Hibernate runs first.

In `sql.init` we specify that the seed script should always run. `mode: always` is required because Spring Boot disables SQL initialization by default when using JPA — without it, the script is silently skipped. `data-locations` points to the script using a `file:` prefix, which resolves relative to the project root. This allows us to reuse the existing `assets/load.sql` directly without copying it into the test resources, keeping a single source of truth for the seed data.

We create package `com.sanjayrisbud.integration` to contain our integration tests.  We create class `ConstellationControllerIT` to contain the integration tests for `ConstellationController`:

```java
    @SpringBootTest(classes = StarzzBootApplication.class)
    @AutoConfigureMockMvc
    class ConstellationControllerIT {
    
        @Autowired
        private MockMvc mockMvc;
    
        @Autowired
        private ObjectMapper objectMapper;
    
        @Test
        void getConstellationListReturns200AndList() throws Exception {
            mockMvc.perform(get("/constellations"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(100));
        }
    
        @Test
        void getConstellationGivenExistingIdReturns200AndConstellation() throws Exception {
            mockMvc.perform(get("/constellations/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.constellationId").value(1))
                    .andExpect(jsonPath("$.constellationName").value("CON-YKkuk"));
        }
    
        @Test
        void getConstellationGivenNonExistentIdReturns404AndError() throws Exception {
            mockMvc.perform(get("/constellations/9999"))
                    .andExpect(status().isNotFound());
        }
        // ...
    }
```

As discussed, `@SpringBootTest` is used to load the application context.  This annotation tells Spring Boot to search for the main class in the current package and in parent packages.  However since the main class is in `com.sanjayrisbud.starzzboot` i.e. a sibling package, Spring Boot won't be able to find it automatically so we add `(classes = StarzzBootApplication.class)` to specify what the main class is.

The other classes in the `integration` package follow a similar logic.

For security of our system, only registered users should be allowed to make any changes to our data.  We enforce this by requiring users to log in to our system using their username and password.  Currently though, user's passwords are saved as-is in our database.  This is not desirable.  In the next chapter, we create a new endpoint that would allow users to change their passwords and save their passwords more securely.

</details>

### Chapter 6: Setting up password hashing

Project dependencies added:

    Spring Security Crypto

#### Overview

In this chapter, we introduce password hashing using **Spring Security Crypto**.  Passwords are now stored as BCrypt hashes instead of plaintext.  When a user is registered, the sentinel value is hashed before being saved to the database.  A new endpoint, `PATCH /users/{id}/change-password`, allows users to update their password by supplying their existing password and a new one.  The same endpoint handles both the initial password reset (where the existing password is the sentinel) and a regular password change.

#### User Resets Password

```mermaid
sequenceDiagram
    autonumber

    actor User
    participant Application
    participant Database

    User ->> Application: **PATCH** /users/<id>/change-password<br/>{"existingPassword": "resetRequired",<br/>"newPassword": <new password>}
    Application ->> Database: Query user data with<br/>**userId**=<id>
    Database ->> Application: User data
    Application ->> Application: Verify that **password**=hash("resetRequired")
    Application ->> Database: Update user data with<br/>**password**=hash(<new password>)
    Database ->> Application: Result
    Application ->> User: HTTP Response
```

#### User Changes Password

```mermaid
sequenceDiagram
    autonumber

    actor User
    participant Application
    participant Database

    User ->> Application: **PATCH** /users/<id>/change-password<br/>{"existingPassword": <existing password>,<br/>"newPassword": <new password>}
    Application ->> Database: Query user data with<br/>**userId**=<id>
    Database ->> Application: User data
    Application ->> Application: Verify that **password**=hash(<existing password>)
    Application ->> Database: Update user data with<br/>**password**=hash(<new password>)
    Database ->> Application: Result
    Application ->> User: HTTP Response
```

#### New Endpoint

| Endpoint                        | Method | Description                                              | Response       |
|---------------------------------|--------|----------------------------------------------------------|----------------|
| `/users/{id}/change-password`   | PATCH  | Updates the user's password                              | 204 No Content |

*The Postman collection in* `assets/starzz-boot.postman_collection.json` *has been updated to include this new endpoint.*

<details>

<summary>Chapter Walkthrough</summary>

Whenever users are registered to our application, they are assigned the default password **resetRequired**; we will henceforth be referring to this value as the *sentinel*.
The sentinel is a reminder that before a user can fully use our application, he has to change his password first.  We thus need to provide the user with some way to change his password without needing to manually update the database.

Another use case for the "change password" functionality is to allow users to periodically update their passwords, which is a good security practice.  

The above use cases can be satisfied by creating a new endpoint, `/users/{id}/change-password`; the user can then provide his existing password and nominate a new one.  The application then verifies that the user's existing password is the same as the one that the user just supplied, and if so, proceeds to update the user's password to the new password.  If the user is instead requesting a password reset, the user explicitly supplies the sentinel as the existing password.

Note however that this solution is not secure.  Our application saves passwords to the database as-is i.e. if a user's password is *abcd1234*, it is written to the database as *abcd1234*.  Anyone with access to the database can see all the users' passwords.  To prevent this, we would need some way to save passwords in encrypted form.

This is where **Spring Security Crypto** comes in.  With it, we can save hashes of passwords instead of actual passwords.  A hash is the result of passing some input through a hash function.  A hash function is a one-way function i.e. given the result of the function, we cannot figure out what the input to the function was.  This can be used for passwords.

This is how hashing will help us.  Any password that is to be saved to the database is first passed to a hash function.  The hash, not the actual password, is the user's password saved to the database.  The hash is effectively the encrypted form of the password.  Even if someone manages to read this value, there is no way to decrypt the value and derive the original password.

There are several hashing algorithms; BCrypt is the recommended algorithm, and this is what we will use for our application.  BCrypt is a salted hash function, meaning the same input produces a different hash each time.  A password's hashes taken at different times will be different.  

So to determine whether a password change should proceed, we use BCrypt's `matches()` method, which internally handles the salt comparison.  Since we cannot simply hash the supplied password and compare it directly against the saved hash, we use `matches()` to extract the salt from the saved hash.  The method then uses the extracted salt to verify the supplied password.  If the verification succeeds, we can say that the supplied password matches the user's actual password, and we allow the change operation to continue.

To use **Spring Security Crypto** we need to add the dependency to our project:

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
```

Right now, the sentinel value is hardcoded in `com.sanjayrisbud.starzzboot.services.UserService`.  We may want to use this value elsewhere.  We can hard code the value in those places too, but a better approach would be to configure the value in `application.yaml` so that it is defined in one place and can be changed without modifying code.  In the places that need the value, code can just refer to the configuration.

We add these lines to `src/main/resources/application.yaml`:

```yaml
app:
  security:
    password-reset-sentinel: resetRequired
```

We then modify code in `com.sanjayrisbud.starzzboot.services.UserService` to read the sentinel value:

```java
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Value("${app.security.password-reset-sentinel}")
    private String passwordResetSentinel;
```

Here, Spring uses field injection on `passwordResetSentinel` so we cannot set it as `final`.

We modify `registerUser()`:

```java
    public UserDetailsDto registerUser(UserDto request) {
        var user = User.builder()
                .name(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .password(passwordResetSentinel)
                .build();
        userRepository.save(user);
        return userMapper.toDetailsDto(user);
    }
```

Instead of hardcoding the sentinel value for the password, we use `passwordResetSentinel`.

When we execute our tests, we see that they have broken.  There are 3 issues:

- Unit tests don't load application context and so cannot read from a configuration file.  Tests that call instances of `com.sanjayrisbud.starzzboot.services.UserService` must set `passwordResetSentinel`.  However, this field is `private` so we have to set the field using Java's Reflection API.  In `src/test/java/com/sanjayrisbud/starzzboot/services/UserServiceTest.java`, we add this before our tests:

```java
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "passwordResetSentinel", "resetRequired");
    }
```

`@BeforeEach` specifies that the method should run before each test in the class executes.

- Integration tests load the full application context.  So when `com.sanjayrisbud.starzzboot.services.UserService` is tested, it will read the configuration file for the sentinel value.  Since tests use `src/test/resources/application.yaml`, we need to add these lines too:

```yaml
app:
  security:
    password-reset-sentinel: resetRequired
```

- Also in integration tests, when the application context is loaded, `com.sanjayrisbud.starzzboot.services.UserService` cannot be properly instantiated (this will also occur in production; fortunately our test caught it).  It is because of `@AllArgsConstructor`.  Lombok generated a constructor with all fields as parameters. So the generated constructor looks like:

```java
    public UserService(UserRepository userRepository, UserMapper userMapper, String passwordResetSentinel) { 
        // ...
    }
```

Spring sees this constructor and tries to autowire all three parameters. For `userRepository` and `userMapper` it found matching beans. But for `passwordResetSentinel`, it couldn't find a bean of type `String` and failed.  Even though `@Value` was on the field, Spring isn't able to process it.  Spring does constructor injection before field injection.

We can fix this by removing `@AllArgsConstructor` and creating our own constructor.  This constructor would still ask Spring to inject `userRepository` and `userMapper` but instead of injecting a bean for `passwordResetSentinel`, we inject a property from the configuration file, which Spring had already read.  We change the injection code we wrote earlier to:

```java
    @Service
    public class UserService {
        private final UserRepository userRepository;
        private final UserMapper userMapper;
        private final String passwordResetSentinel;

        public UserService(UserRepository userRepository, UserMapper userMapper,
                        @Value("${app.security.password-reset-sentinel}") String passwordResetSentinel) {
            this.userRepository = userRepository;
            this.userMapper = userMapper;
            this.passwordResetSentinel = passwordResetSentinel;
        }
        // ...
    }
```

The code is getting the property `app.security.password-reset-sentinel` from `application.yaml`.  Now we are able to set `passwordResetSentinel` as `final`.

We now write the code to hash passwords.

We create a new package `com.sanjayrisbud.starzzboot.config` to contain classes related to application configuration.  In this package, we create a class `SecurityConfig`:

```java
    @Configuration
    public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
```

`@Configuration`  tells Spring that this class contains bean definitions. Spring scans for it at startup and processes all `@Bean` methods inside it, registering the returned objects as beans in the application context.

We modify `com.sanjayrisbud.starzzboot.services.UserService`.

```java
    @Service
    public class UserService {
        private final UserRepository userRepository;
        private final UserMapper userMapper;
        private final PasswordEncoder passwordEncoder;
        private final String passwordResetSentinel;
        // ...
    }
```

We add a field for `passwordEncoder` to hold the hashing bean.

```java
    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder,
                       @Value("${app.security.password-reset-sentinel}") String passwordResetSentinel) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetSentinel = passwordResetSentinel;
    }
```

In the constructor, we ask Spring to inject the bean of type `PasswordEncoder` into the new field.

```java
    public UserDetailsDto registerUser(UserDto request) {
        var user = User.builder()
                .name(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .password(passwordEncoder.encode(passwordResetSentinel))
                .build();
        userRepository.save(user);
        return userMapper.toDetailsDto(user);
    }
```

We modify `registerUser()` so that `passwordResetSentinel` is hashed using `passwordEncoder.encode()` before setting it as the password.

We run our tests and discover that `UserServiceTest` broke again.  This is because we had added a new bean in `UserService`, and in `UserServiceTest` **Mockito** cannot instantiate `UserService` anymore. We simply modify `src/test/java/com/sanjayrisbud/starzzboot/services/UserServiceTest.java`:

```java
    @ExtendWith(MockitoExtension.class)
    class UserServiceTest {
        @Spy
        private final UserMapper userMapper = new UserMapper();
    
        @Mock
        private UserRepository userRepository;
        @Mock
        private PasswordEncoder passwordEncoder;
        @InjectMocks
        private UserService userService;
        // ...
    }
```

We add another `@Mock` for `passwordEncoder`.

It's also a good idea to create a test to verify that the password of the registered user is the hash of the sentinel value.  We modify `src/test/java/com/sanjayrisbud/integration/UserControllerIT.java`:

```java
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Value("${app.security.password-reset-sentinel}")
    private String passwordResetSentinel;
```

We ask Spring to inject beans of type `PasswordEncoder` and `UserRepository` and the value `app.security.password-reset-sentinel` from `application.yaml`.

```java
    @Test
    @Transactional
    void registerUserGivenValidDataReturns201AndUser() throws Exception {
        String newUsername = "testuser12345";
        UserDto request = UserDto.builder()
                .username(newUsername)
                .email("test@email.com")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.username").value(newUsername));

        User u = userRepository.findByName(newUsername);
        Assertions.assertTrue(passwordEncoder.matches(
                passwordResetSentinel, u.getPassword()));
    }
```

We then test the password.  We add code to retrieve the user that was just added to the `users` table and assert that the hashing bean successfully matches the sentinel value and the user's password.

We also need to define `findByName()`.  In `com.sanjayrisbud.starzzboot.repositories.UserRepository`.  we have:

```java
    public interface UserRepository extends JpaRepository<User, Integer> {
        User findByName(String s);
    }
```

The field `name` in the `User` entity contains the username, so we can just declare `findByName()` in the repository interface and Spring Data JPA can generate the method automatically.  

We create our change password endpoint.  In package `com.sanjayrisbud.starzzboot.dtos`, we create `ChangePasswordDto`:

```java
    @Builder
    @Data
    public class ChangePasswordDto {
        @NotBlank private String existingPassword;
        @NotBlank private String newPassword;
    }
```

This DTO will hold the user's inputs for their existing and new passwords.

We add a new method to `com.sanjayrisbud.starzzboot.services.UserService`:

```java
    public void changePassword(Integer id, ChangePasswordDto request) {
        var currentUser = findById(id);
        if (!passwordEncoder.matches(request.getExistingPassword(),
                currentUser.getPassword())) {
            throw new InputMismatchException("Passwords don't match.");
        }
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }
```

This method will handle change password requests.  It tries to match the existing password supplied
by the user with his existing password.  If successful, the change proceeds.  Otherwise, an `InputMismatchException`
is thrown.  We add a new method to `com.sanjayrisbud.starzzboot.exceptions.GlobalExceptionHandler`

```java
    @ExceptionHandler(InputMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleInputMismatch(InputMismatchException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                ex.getMessage(), 
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }
```

We add a new handler for `InputMismatchException`.

We add new methods to `src/test/java/com/sanjayrisbud/starzzboot/services/UserServiceTest.java`:

```java
    @Test
    void changePasswordGivenMatchingExistingPasswordUpdatesPassword() {
        User u = buildUser();
        ChangePasswordDto request = ChangePasswordDto.builder()
                .existingPassword("existingPassword")
                .newPassword("newPassword")
                .build();

        when(userRepository.findById(u.getId()))
                .thenReturn(Optional.of(u));
        when(passwordEncoder.matches(request.getExistingPassword(), u.getPassword()))
                .thenReturn(true);

        userService.changePassword(u.getId(), request);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePasswordGivenNonMatchingExistingPasswordThrowsInputMismatchException() {
        User u = buildUser();
        ChangePasswordDto request = ChangePasswordDto.builder()
                .existingPassword("existingPassword")
                .newPassword("newPassword")
                .build();

        when(userRepository.findById(u.getId()))
                .thenReturn(Optional.of(u));
        when(passwordEncoder.matches(request.getExistingPassword(), u.getPassword()))
                .thenReturn(false);

        InputMismatchException ex = assertThrows(InputMismatchException.class,
                () -> userService.changePassword(u.getId(), request));

        assertEquals("Passwords don't match.", ex.getMessage());

        verifyNoMoreInteractions(userRepository);
    }
```

These are unit tests for our new service method.

We modify `com.sanjayrisbud.starzzboot.controllers.UserController`:

```java
    @PatchMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Integer id, @Valid @RequestBody  ChangePasswordDto request) {
        userService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }
```

We add a method to listen for change password requests.

We add new methods to `src/test/java/com/sanjayrisbud/starzzboot/controllers/UserControllerTest.java`:

```java
    @Test
    void changePasswordGivenMatchingExistingPasswordReturns204() throws Exception {
        User u = buildUser();
        ChangePasswordDto request = ChangePasswordDto.builder()
                .existingPassword("existingPassword")
                .newPassword("newPassword")
                .build();

        doNothing().when(userService).changePassword(u.getId(), request);

        mockMvc.perform(patch("/users/" + u.getId() + "/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void changePasswordGivenNonMatchingExistingPasswordReturns400() throws Exception {
        User u = buildUser();
        ChangePasswordDto request = ChangePasswordDto.builder()
                .existingPassword("existingPassword")
                .newPassword("newPassword")
                .build();

        doThrow(new InputMismatchException("Passwords don't match."))
                .when(userService).changePassword(u.getId(), request);

        mockMvc.perform(patch("/users/" + u.getId() + "/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
```

These, in turn, are controller unit tests for our new endpoint.

It's also a good idea to add integration tests for our new endpoint.  We add new methods to `src/test/java/com/sanjayrisbud/integration/UserControllerIT.java`:

```java
    @Test
    @Transactional
    void changePasswordGivenMatchingPasswordReturns204() throws Exception {
        String newUserPassword = "passwordsMatch";
        String updatedUserPassword = "passwordUpdated";
        User u = User.builder()
                .name("testuser12345").email("testuser12345@email.com")
                .password(passwordEncoder.encode(newUserPassword))
                .build();
        userRepository.save(u);

        ChangePasswordDto request = ChangePasswordDto.builder()
                .existingPassword(newUserPassword)
                .newPassword(updatedUserPassword)
                .build();

        mockMvc.perform(patch("/users/" + u.getId() + "/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        assertTrue(passwordEncoder.matches(updatedUserPassword, u.getPassword()));
    }

    @Test
    @Transactional
    void changePasswordGivenNonMatchingPasswordReturns400() throws Exception {
        String newUserPassword = "passwordsDontMatch";
        User u = User.builder()
                .name("testuser12345").email("testuser12345@email.com")
                .password(passwordEncoder.encode(newUserPassword))
                .build();
        userRepository.save(u);

        ChangePasswordDto request = ChangePasswordDto.builder()
                .existingPassword("wrongPassword")
                .newPassword("newPassword")
                .build();

        mockMvc.perform(patch("/users/" + u.getId() + "/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        assertTrue(passwordEncoder.matches(newUserPassword, u.getPassword()));
    }

    @Test
    void changePasswordGivenNonExistentIdReturns404() throws Exception {
        ChangePasswordDto request = ChangePasswordDto.builder()
                .existingPassword("existingPassword")
                .newPassword("newPassword")
                .build();

        mockMvc.perform(patch("/users/9999/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
```

We have made meaningful progress in securing our users' passwords — they are now stored as BCrypt
hashes, making the original values practically unrecoverable even if the database is compromised.
However, securing passwords alone is not enough.  At the moment, anyone who knows the URL of an
endpoint can call it freely.  We need a way to ensure that only authenticated users can access
our API.  In the next chapter, we integrate **Spring Security** and implement **JWT (JSON Web Token)**
authentication.

</details>

### Chapter 7: Setting up JWT

Project dependencies added:

    Spring Security

#### Overview

In this chapter, we secure our application.  We first define our application's security rules.
We then implement simple role based access control and authentication using JWT (JSON Web Token).
It is the standard approach for securing REST APIs.

#### Login

```mermaid
sequenceDiagram
    autonumber

    actor User
    participant Database
    participant Application
    participant ConfigFile

    User ->> Application: **POST** /login<br/>{"username": <username>,<br/>"password": <password>}
    Application ->> Database: Query user with<br/>**username**=<username>
    Database ->> Application: User data
    Application ->> Application: Verify that **password** matches hash(<password>) and that <password> is not "resetRequired"
    Application ->> ConfigFile: Check if user has admin privileges
    ConfigFile ->> Application: Result
    Application ->> User: Token
```

#### Admin Registers User

```mermaid
sequenceDiagram
    autonumber

    actor Admin
    participant Application
    participant Database

    Admin ->> Application: **POST** /users<br/>{"username": "john1234",<br/> "email": "john@acme.com"}
    Application ->> Database: save user data with<br/>**username**=*john1234*,<br/>**password**=hash("*resetRequired*")
    Database ->> Application: New user data
    Application ->> Admin: HTTP Response
```

#### New Endpoints

| Endpoint                | Method | Description                                                  | Response |
|-------------------------|--------|--------------------------------------------------------------|----------|
| `/login`                | POST   | Returns a JWT to be used to authenticate subsequent requests | 200 OK   |

*The Postman collection in* `assets/starzz-boot.postman_collection.json` *has been updated to include this new endpoint.  Requests needing Authorization headers have also been updated.*

#### Sample Responses

POST /login
```json
{
    "token": "qwrtyuiop1234asdfghjkl"
}
```

<details>

<summary>Chapter Walkthrough</summary>

For our application, we want users to log in using a new **POST** `/login`endpoint that we
will create.  Although users can log in normally using their credentials i.e. they don't have
to encode their password, their passwords are saved in the database as BCrypt-encoded strings,
which we set up in the previous chapter.

When a user logs in with valid credentials, the application issues a signed token. The user
then includes this token in subsequent requests, and the application verifies it before granting
access.  This is called token-based authentication as opposed to session-based authentication.

In regard to access control, we want to restrict the **POST** `/users` endpoint to admins so only
admins can create new users.  We would also like only authenticated users to have access to all
*POST, PUT, PATCH* and *DELETE* endpoints.  The *GET* endpoints of galaxies, constellations and stars are accessible to anybody.
We still restrict the *GET* endpoints of users to be accessible only to authenticated users.

To satisfy our first access requirement, we need to identify our admins.  This is usually done
by tagging them in our `users` table with a field like `isAdmin`.  However, our assumption is that
we cannot easily alter our database schema.  As a workaround, we can define our admins in `application.yaml`.
The downside of this is that if we ever need to modify this list, we would need to redeploy the
application.

We create new users `admin1`, `admin2` and `admin3`, reset their passwords, and in `src/main/resources/application.yaml`,
we add a new `app.admins` after `app.security`:

```yaml
app:
  security:
    password-reset-sentinel: resetRequired
  admins:
    - admin1
    - admin2
    - admin3
```

We also add `app.admins` in `src/test/resources/application.yaml` for our tests.

To implement security, we will use **Spring Security**.  In the previous chapter, we added
**Spring Security Crypto** to our project.  It is already included in **Spring Security** so we
can remove the earlier dependency.  In `pom.xml`, replace:

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
```

with:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

**JJWT** is a Java library providing end-to-end JSON Web Token creation and verification.
We need to add **JJWT** to our project:

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
```
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

We add the above snippets to `pom.xml`.

Adding `spring-boot-starter-security` to the project immediately activates Spring Security with
its default behavior: all endpoints are locked down, a default in-memory user is created with
a randomly generated password, and form login is enabled. 

Since we have our own security rules, we need to disable this default behavior.  We define our
rules by adding a `SecurityFilterChain` bean.  A *filter chain* is literally several filters chained together — each one processes the request in sequence and passes it to the next.  

Spring Security's filter chain contains filters responsible for authentication and authorization — things like reading credentials, validating tokens, and enforcing access rules.  By defining a `SecurityFilterChain` bean, we take control of which of those filters are active and how they behave.

We add this to the security configuration class we created earlier, in `com.sanjayrisbud.starzzboot.config.SecurityConfig`:

```java
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    
        return http.build();
    }
```

The object returned by this method is our `SecurityFilterChain` bean.  It accepts as a parameter
an `HttpSecurity` object, which is a builder object.  We chain configuration methods on it, with each method requiring a lambda to set a specific configuration:

- `.csrf()`: *CSRF* is disabled — CSRF attacks exploit browser-based session cookies which we are not using so we can disable it.
- `sessionManagement()`: *Session management* is set to `STATELESS` — our application will not create or use HTTP sessions; authentication state is carried entirely in the JWT.  Effectively we are also disabling it.
- `.formLogin()` and `.httpBasic()`: *Form login* and *HTTP Basic Authentication* are disabled — we do not want Spring Security's built-in login mechanisms; we will handle authentication ourselves via `POST /login`.
- `.authorizeHttpRequests()`: *All requests are permitted for now* — we will lock down specific endpoints once our JWT filter is in place.

On our application's startup, Spring calls `securityFilterChain()`.  The method configures and builds a `SecurityFilterChain` bean.  Spring Security picks up that bean and evaluates every HTTP request against the rules we defined.

Next, we implement the `POST /login` endpoint.

We need a DTO to be sent as the body of the request to the endpoint, to hold the user's credentials.
In package `com.sanjayrisbud.starzzboot.dtos`, we create class `LoginDto`:

```java
    @Data
    public class LoginDto {
        @NotBlank private String username;
        @NotBlank private String password;
    }
```

If the login attempt was successful we respond with a token so we need a DTO for that. In the
same package, we create record `TokenDto`:

```java
    public record TokenDto(
            String token
    ) {}
```

If the attempt was unsuccessful, we instead throw an exception.  An attempt can fail in one of three ways:

- The supplied username doesn't exist in the database.
- The supplied username exists but the supplied password doesn't match his password saved in the database.
- The supplied username and password matches a record in the database but the password equals the sentinel.

We handle the first two cases by simply throwing `BadCredentialsException`.  We deliberately use the same exception for both cases to avoid revealing whether a given username exists.  For the third case, we can be a bit more helpful because the user attempting to log in has given the correct credentials; it's just a password reset that is lacking.

Also in package `com.sanjayrisbud.starzzboot.dtos`, we create class `PasswordResetRequiredDto`:

```java
    public record PasswordResetRequiredDto(
            String message,
            LocalDateTime timestamp,
            Integer userId
    ) {}
```

Aside from the message and timestamp, this DTO contains the user's id.

In `com.sanjayrisbud.starzzboot.exceptions` we create a new `PasswordResetRequiredException`:

```java
    @Getter
    public class PasswordResetRequiredException extends RuntimeException {

        private final Integer userId;

        public PasswordResetRequiredException(Integer userId) {
            super("Password reset required before logging in.");
            this.userId = userId;
        }
    }
```

This exception is thrown for Case 3.

As with our other exceptions, we add handlers for these exceptions in our exception handler.  We modify  `com.sanjayrisbud.starzzboot.exceptions.GlobalExceptionHandler`:

```java
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentials(BadCredentialsException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(PasswordResetRequiredException.class)
    public ResponseEntity<PasswordResetRequiredDto> handlePasswordResetRequired(PasswordResetRequiredException ex) {
        var response = new PasswordResetRequiredDto(
                ex.getMessage(),
                LocalDateTime.now(),
                ex.getUserId()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
```

When `PasswordResetRequiredException` is thrown, the user can take the id indicated in the response and then call the *Change Password* endpoint.

To generate JWTs, we need a JWT secret to sign our tokens.  The secret is our application's way to check whether tokens included with requests havee been tampered with.  This is sensitive information, so we store it in `.env`:

```bash
DB_URL=jdbc:mysql://localhost:3306/starzz
DB_USERNAME=root
DB_PASSWORD=<your-database-password>
JWT_SECRET=<your-secret-key>
```

And reference it in `application.yaml`.  We also add `jwt-expiration` to specify a token expiration value in milliseconds:

```yaml
app:
  security:
    password-reset-sentinel: resetRequired
    jwt-secret: ${JWT_SECRET}
    jwt-expiration: 86400000
```

Here we specify that tokens should be invalidated after 24h.

We will also include these configurations in our tests so we also include them in `src/test/resources/application.yaml`.

We then create a service to handle token generation.  In  package `com.sanjayrisbud.starzzboot.services`, we create `JwtService`:

```java
    @Service
    public class JwtService {
        private final String jwtSecret;
        private final long jwtExpiration;
    
        public JwtService(@Value("${app.security.jwt-secret}") String jwtSecret,
                          @Value("${app.security.jwt-expiration}") long jwtExpiration) {
            this.jwtSecret = jwtSecret;
            this.jwtExpiration = jwtExpiration;
        }

        public String generateToken(Integer id, String username, String role) {
            return Jwts.builder()
                    .subject(id.toString())
                    .claim("username", username)    
                    .claim("role", role)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(getSigningKey())
                    .compact();
        }

        private SecretKey getSigningKey() {
            byte[] keyBytes = jwtSecret.getBytes();
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }
```

The token generation also follows a builder pattern.  It includes the user's id as the subject, and their username and role (*ADMIN* or *USER*) as a claim.  It is signed using the secret key decoded from `JWT_SECRET`.  The token expires after a certain period.

To determine whether the user is an admin or user, we check if his username is in our admin list from `application.yaml`.  It is straightforward to read `app.security.jwt-secret` and `app.security.jwt-expiration` because Spring's `@Value` annotation works well for simple scalar values like strings and numbers, but it cannot bind a YAML list to a `List<String>`.  For that, we need `@ConfigurationProperties`.

In `com.sanjayrisbud.starzzboot.config`, we create `AdminProperties`:

```java
    @Data
    @Component
    @ConfigurationProperties(prefix = "app")
    public class AdminProperties {
        private List<String> admins;
    }
```

The `@ConfigurationProperties(prefix = "app")` annotation tells Spring to bind properties under `app` to the fields of this class.  Since `app.admins` is a YAML list, Spring will bind it to the `List<String> admins` field automatically.

In `com.sanjayrisbud.starzzboot.services` we then create `AuthService`:

```java
    @Service
    public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final String sentinel;
        private final AdminProperties adminProperties;

        public AuthService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        @Value("${app.security.password-reset-sentinel}") String sentinel,
                        AdminProperties adminProperties) {
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
            this.jwtService = jwtService;
            this.sentinel = sentinel;
            this.adminProperties = adminProperties;
        }

        public String login(LoginDto request) {
            var user = userRepository.findByName(request.getUsername());
            if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Invalid credentials.");
            }
            if (passwordEncoder.matches(sentinel, user.getPassword())) {
                throw new PasswordResetRequiredException(user.getId());
            }
            String role = adminProperties.getAdmins().contains(user.getName()) ? "ADMIN" : "USER";
            return jwtService.generateToken(user.getId(), user.getName(), role);
        }
    }
```

This class handles our login logic.  It proceeds as follows.  We first look up the user by username.  If the user does not exist, or if the supplied password does not match the stored BCrypt hash, we throw a `BadCredentialsException`.  Next, we check whether the stored password is a hash of the sentinel — if so, the user has not yet reset their password and we throw a `PasswordResetRequiredException`.  Finally, we determine the user's role by checking the admin list via `AdminProperties`, and issue a signed JWT.

In `com.sanjayrisbud.starzzboot.controllers` we create `AuthController`:

```java
    @AllArgsConstructor
    @RestController
    public class AuthController {

        private final AuthService authService;

        @PostMapping("/login")
        public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto request) {
            String token = authService.login(request);
            return ResponseEntity.ok(new TokenDto(token));
        }
    }
```

This class listens for requests to `/login`.

With the login endpoint in place, we now need to validate the JWT on every subsequent request and lock down the endpoints.  To do this, we create a filter that intercepts every incoming request, extracts the token from the `Authorization` header, validates it, and sets the authenticated user in Spring Security's `SecurityContext`.  

To validate tokens and set the user, we add two parsing methods to `com.sanjayrisbud.starzzboot.services.JwtService`:

```java
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }
```

`extractAllClaims()` parses the signed JWT and returns the claims.  `parseSignedClaims()` checks whether the token has been tampered with or has expired, and if so, throws a `JwtException`.  The exception propagates up to the caller's catch block.  `extractRole()` is a convenience method that pulls the `role` from the claims.

We create a new package `com.sanjayrisbud.starzzboot.filters` to contain classes related to our request filters.  In this package, we create a class `JwtAuthFilter`:

```java
    @AllArgsConstructor
    @Component
    public class JwtAuthFilter extends OncePerRequestFilter {

        private final JwtService jwtService;

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain)
                throws ServletException, IOException {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    String role = jwtService.extractRole(token);
                    var authority = new SimpleGrantedAuthority("ROLE_" + role);
                    var auth = new UsernamePasswordAuthenticationToken(null, null, List.of(authority));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (Exception ignored) {
                // Parsing failed — unauthenticated. Spring Security enforces access rules downstream.
                }
            }

            filterChain.doFilter(request, response);
        }
    }
```

We extend `OncePerRequestFilter` to guarantee the filter runs exactly once per request.  If the `Authorization` header is present and starts with `Bearer `, we strip the prefix and pass the token to `JwtService` to extract the role.  If we are able to extract the role from the token, we are already sure that the request is authenticated; we just need to inform the downstream filters.

For that we build a `UsernamePasswordAuthenticationToken`.  This is Spring Security's standard representation of an authenticated principal.  The constructor of `UsernamePasswordAuthenticationToken` can take 3 parameters: the principal's username, the principal's password and the principal's roles.  The first 2, we can set as `null` because these info won't be necessary for later filters.  We need to pass the third parameter thhough because the role will be referenced later.  We set the returned object on the `SecurityContextHolder`.

If the token is missing, expired, or malformed, the `SecurityContext` stays empty.

With the filter in place, we update our security filter chain in `com.sanjayrisbud.starzzboot.config.SecurityConfig`:

```java
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/users/*/change-password").permitAll()
                .requestMatchers(HttpMethod.GET, "/galaxies/**", "/constellations/**", "/stars/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, authEx) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            );

        return http.build();
    }
```

We update `authorizeHttpRequests()` with our access rules:

- `POST /login` is public — anyone can attempt to log in.
- `PATCH /users/*/change-password` is public — a user whose password is the sentinel cannot get a JWT because login is blocked with a 403.  They need to call this endpoint first, so requiring a JWT here would create a deadlock.  The `existingPassword` requirement in the request body is the only gate needed.
- `GET /galaxies/**`, `GET /constellations/**`, `GET /stars/**` are public — this is read-only, non-sensitive data.  Making these endpoints public is a deliberate design decision based on data sensitivity, not HTTP method.  User endpoints are kept protected because they return PII (email, name, date of birth).
- `POST /users` requires the `ADMIN` role — only admins can create new users.
- All other endpoints require any authenticated user.

Earlier, we mentioned that since we had our own security rules, we wanted to define our own `SecurityFilterChain` bean.  We used to have a basic chain, but now our filter chain is more complex:

- `.addFilterBefore()`: We use `UsernamePasswordAuthenticationFilter` as a reference point to position our `JwtAuthFilter` in the chain.  Our filter populates the `SecurityContext` from the JWT (note that `UsernamePasswordAuthenticationFilter` is inactive because we disabled form login).

- `.exceptionHandling()`: Spring Security's default behavior is to return HTTP 403 for unauthenticated requests.  This is misleading; HTTP 403 means the user is known but lacks permission.  We thus configure `ExceptionTranslationFilter` to use a custom `AuthenticationEntryPoint` that returns HTTP 401, which means the user has not authenticated.

- `authorizeHttpRequests()`: Spring Security checks the `SecurityContext` to enforce our access rules.  The rules set in this method are enforced by `AuthorizationFilter`.

We have finished writing our application's code.  We now proceed to adding new and fixing existing tests.

When we run our existing tests, we discover that all our controller and integration tests have failed.

Spring Security is now a dependency of our project so it gets loaded.  It is interfering with our existing tests.

Our controller tests use `@WebMvcTest`, which loads a limited application context containing only the web layer.  Spring Security's auto-configuration is part of that web layer, so it gets loaded too.  The failures in our controller tests are due to this auto-configuration (CSRF protection and access rule blocking).  However, controller tests are only concerned with controller logic — routing, validation, and response format — and not with security rules.  Thus the fix is to tell MockMvc not to apply filters when processing requests, using `@AutoConfigureMockMvc(addFilters = false)`:

```java
    @WebMvcTest(ConstellationController.class)
    @AutoConfigureMockMvc(addFilters = false)
    class ConstellationControllerTest {
        // ...
    }
```

We apply this to all four controller test classes.

Recall that it is `MockMvc` that allows us to mock HTTP requests and that `@WebMvcTest` already configures it for us.  `@AutoConfigureMockMvc(addFilters = false)` overrides this default configuration to instruct MockMvc not to apply filters when processing requests.  Note that the filters are still instantiated as beans in the context — they are just not applied to requests.

Recall also that `@WebMvcTest` loads the web layer, including filters.  This means that our custom filter `com.sanjayrisbud.starzzboot.filters.JwtAuthFilter` also gets picked up.  Our filter depends on `JwtService`, which is not part of the web layer and is therefore not loaded by `@WebMvcTest`.  So we manually load this dependency with a mock:

```java
    @MockitoBean
    private JwtService jwtService;
```

This is added to all four controller test classes.  Note that this mock is not used in our controller test; we only load it to satisfy the dependency in our filter.

Our integration tests use `@SpringBootTest`, which loads the full application context including the real `SecurityFilterChain`.  This means all access rules are enforced.  We use `@WithMockUser` from `spring-security-test` to place a pre-authenticated user in the `SecurityContext` for tests that hit protected endpoints.  This avoids the need to perform a real login for every test.

For endpoints that require any authenticated user (POST, PUT, DELETE on galaxies, constellations, and stars; GET, PUT on users), we annotate the test method with `@WithMockUser`.  For the `POST /users` endpoint which requires the `ADMIN` role, we use `@WithMockUser(roles = "ADMIN")`.  Tests for public endpoints (`GET /galaxies/**`, `GET /constellations/**`, `GET /stars/**`, and `PATCH /users/*/change-password`) are left unannotated.

We also add new tests to verify that our security rules are enforced correctly.  In each of `GalaxyControllerIT`, `ConstellationControllerIT`, and `StarControllerIT`, we add a test that calls the POST endpoint without a token and expects an HTTP 401:

```java
    @Test
    void registerConstellationWithoutAuthReturns401() throws Exception {
        mockMvc.perform(post("/constellations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
```

In `UserControllerIT`, we add the same HTTP 401 test for `POST /users`.  We also add an HTTP 403 test to verify that an authenticated user without the `ADMIN` role cannot create a new user:

```java
    @Test
    @WithMockUser
    void registerUserWithoutAdminRoleReturns403() throws Exception {
        UserDto request = UserDto.builder()
                .username("testuser12345")
                .email("test@email.com")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
```

We now turn to unit tests for the new classes introduced in this chapter.  We start with `JwtService`.

`JwtService` uses `jwtSecret` and `jwtExpiration`, which are injected via constructor parameters annotated with `@Value`.  Each test simply instantiates `JwtService`.

In `src/test/java/com/sanjayrisbud/starzzboot/services` we create `JwtServiceTest`.

Our first test verifies that a generated token contains the correct claims — subject (user ID), username, and role — by parsing it back through `extractAllClaims()` and `extractRole()`:

```java
    @Test
    void generateTokenReturnsTokenWithCorrectClaims() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION);
        String token = jwtService.generateToken(1, "john", "USER");
        assertEquals("1", jwtService.extractAllClaims(token).getSubject());
        assertEquals("john", jwtService.extractAllClaims(token).get("username", String.class));
        assertEquals("USER", jwtService.extractRole(token));
    }
```

Our second test verifies that tokens with a different role are generated correctly:

```java
    @Test
    void generateTokenReturnsTokenWithAdminRole() {
        JwtService jwtService = new JwtService(SECRET, EXPIRATION);
        String token = jwtService.generateToken(1, "admin1", "ADMIN");
        assertEquals("ADMIN", jwtService.extractRole(token));
    }
```

Our third test verifies that parsing an expired token throws a `JwtException`.  We pass `1L` as the expiration, generate a token, sleep briefly to let it expire, then assert that `extractRole()` throws:

```java
    @Test
    void extractRoleGivenExpiredTokenThrowsJwtException() throws InterruptedException {
        JwtService jwtService = new JwtService(SECRET, 1L);
        String token = jwtService.generateToken(1, "john", "USER");
        Thread.sleep(10);
        assertThrows(JwtException.class, () -> jwtService.extractRole(token));
    }
```

Also in `src/test/java/com/sanjayrisbud/starzzboot/services` we create `AuthServiceTest`.  The class setup follows the same pattern as our other service tests — `@Mock` for repositories and external services, `@InjectMocks` for the class under test — with two additions:

`AuthService` has a `sentinel` field injected via `@Value` through its constructor.  Mockito's `@InjectMocks` does not resolve `@Value`; it passes `null` for that parameter.  We correct this in `@BeforeEach` using `ReflectionTestUtils.setField()`.

`AdminProperties` is a plain `@Data` class; we can instantiate it directly and annotate it with `@Spy` so Mockito includes it in constructor injection.  We populate the admins list also in `@BeforeEach`:

```java
    @Spy
    private AdminProperties adminProperties = new AdminProperties();

    @BeforeEach
    void setUp() {
        adminProperties.setAdmins(List.of("admin1"));
        ReflectionTestUtils.setField(authService, "sentinel", SENTINEL);
    }
```

We cover five scenarios in `AuthServiceTest`:

**User not found** — `userRepository.findByName()` returns `null`, so the null check fires and `BadCredentialsException` is thrown:

```java
    @Test
    void loginGivenNonExistentUserThrowsBadCredentialsException() {
        // ...
        when(userRepository.findByName(request.getUsername())).thenReturn(null);
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
```

**Wrong password** — the user is found but `passwordEncoder.matches()` returns `false`:

```java
    @Test
    void loginGivenWrongPasswordThrowsBadCredentialsException() {
        // ...
        when(userRepository.findByName(request.getUsername())).thenReturn(user);
        when(passwordEncoder.matches(request.getPassword(), HASHED_PASSWORD)).thenReturn(false);
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
```

**Sentinel password** — password matches but the stored hash is the sentinel, so `PasswordResetRequiredException` is thrown.  We also assert that the exception carries the correct `userId` so the client knows which user needs to reset their password:

```java
    @Test
    void loginGivenSentinelPasswordThrowsPasswordResetRequiredException() {
        // ...
        when(passwordEncoder.matches(request.getPassword(), HASHED_PASSWORD)).thenReturn(true);
        when(passwordEncoder.matches(SENTINEL, HASHED_PASSWORD)).thenReturn(true);
        PasswordResetRequiredException ex = assertThrows(
                PasswordResetRequiredException.class, () -> authService.login(request));
        assertEquals(1, ex.getUserId());
    }
```

**Admin login** — the username appears in `AdminProperties.admins`, so `generateToken()` is called with `"ADMIN"`:

```java
    @Test
    void loginGivenAdminUserReturnsTokenWithAdminRole() {
        // ...
        when(jwtService.generateToken(1, "admin1", "ADMIN")).thenReturn("admin-token");
        assertEquals("admin-token", authService.login(request));
    }
```

**Regular user login** — the username is not in the admins list, so `generateToken()` is called with `"USER"`:

```java
    @Test
    void loginGivenRegularUserReturnsTokenWithUserRole() {
        // ...
        when(jwtService.generateToken(1, "john", "USER")).thenReturn("user-token");
        assertEquals("user-token", authService.login(request));
    }
```

In `src/test/java/com/sanjayrisbud/starzzboot/controllers` we create `AuthControllerTest` using the same `@WebMvcTest` + `@AutoConfigureMockMvc(addFilters = false)` pattern as the other controller tests.  `AuthController` depends only on `AuthService`, but we still need `@MockitoBean JwtService` to satisfy `JwtAuthFilter`'s dependency.

`AuthControllerTest` covers five scenarios:

**Valid credentials** — `authService.login()` returns a token string, and the response body contains it:

```java
    @Test
    void loginGivenValidCredentialsReturns200WithToken() throws Exception {
        // ...
        when(authService.login(request)).thenReturn("jwt-token");
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }
```

**Invalid credentials** — `authService.login()` throws `BadCredentialsException`, which `GlobalExceptionHandler` maps to HTTP 401:

```java
    @Test
    void loginGivenInvalidCredentialsReturns401() throws Exception {
        // ...
        when(authService.login(request))
                .thenThrow(new BadCredentialsException("Invalid credentials."));
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
```

**Sentinel password** — `authService.login()` throws `PasswordResetRequiredException`, which `GlobalExceptionHandler` maps to HTTP 403.  We also assert that the response body contains the `userId` so the client knows which user needs to reset their password:

```java
    @Test
    void loginGivenSentinelPasswordReturns403WithUserId() throws Exception {
        // ...
        when(authService.login(request))
                .thenThrow(new PasswordResetRequiredException(1));
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.userId").value(1));
    }
```

**Invalid request data** and **malformed JSON** — both return HTTP 400, consistent with the other controller tests.

Finally, In `src/test/java/com/sanjayrisbud/integration` we create `AuthControllerIT`.  Unlike the other integration tests, `AuthControllerIT` does not use `@WithMockUser` at all — its purpose is to verify the full login-to-access flow using real JWTs.  Each test creates its own user inline with a BCrypt-encoded password, similar to the approach in `UserControllerIT`.

We also update `src/test/resources/application.yaml` to replace `${JWT_SECRET}` with a hardcoded test value.  The production YAML reads the secret from an environment variable.  For tests, a hardcoded value in the test config is fine and actually better since it removes that dependency.

`AuthControllerIT` covers six scenarios:

**Login returns token** — a user is created, login is called with the correct password, and the response contains a non-empty token:

```java
    @Test
    @Transactional
    void loginGivenValidCredentialsReturns200WithToken() throws Exception {
        // create user, then:
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
```

**Token grants access to protected endpoint** — we extract the token from the login response and attach it as a `Bearer` header on `GET /users`, which expects an authenticated user.  This is the key test that exercises `JwtAuthFilter` end-to-end:

```java
    @Test
    @Transactional
    void loginGivenValidTokenAllowsAccessToProtectedEndpoint() throws Exception {
        // login, extract token, then:
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
```

**Wrong password returns 401** — `AuthService` throws `BadCredentialsException`, mapped to HTTP 401.

**User role cannot create user** — a regular user (not in the admins list) logs in and receives a `USER`-role token.  Using that token on `POST /users` returns HTTP 403, verifying that the `hasRole("ADMIN")` rule is enforced with a real JWT:

```java
    @Test
    @Transactional
    void loginGivenUserRoleTokenAttemptingToCreateUserReturns403() throws Exception {
        // login as regular user, extract token, then:
        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());
    }
```

**Sentinel password returns 403 with userId** — a user whose password is the BCrypt-encoded sentinel attempts to log in.  The response is HTTP 403 and the body contains the user's ID.

**Invalid token and missing token both return 401** — two tests verify `JwtAuthFilter`'s handling of a malformed `Bearer` token and a completely absent `Authorization` header, both leaving the `SecurityContext` empty.

We have now finished coding our app's functionality.

In subsequent chapters, we add some enhancements to apply some production best practices.

</details>

### Chapter 8: Enhancing our app with Flyway migrations

Project dependencies added:

    Flyway Core
    Flyway MySQL

#### Overview

In this chapter, we replace the database's implicit initialization with **Flyway** database migrations.  The database schema and initial data are managed by versioned SQL files that are executed automatically when the application starts.

The same migrations are used for both the production MySQL database and the H2 database used by the integration tests. This keeps the two environments aligned and ensures that the tests run against the same schema definition as the application.

```mermaid
sequenceDiagram
    autonumber

    participant Application
    participant Flyway
    participant Database
    participant Hibernate

    Application ->> Flyway: Start application context
    Flyway ->> Database: Check flyway_schema_history
    Flyway ->> Database: Apply pending migrations
    Flyway -->> Application: Database is initialized
    Application ->> Hibernate: Build JPA context
    Hibernate ->> Database: Validate entity mappings
    Database -->> Hibernate: Schema matches
    Hibernate -->> Application: Application is ready
```

<details>

<summary>Chapter Walkthrough</summary>

Previously, the application relied on database setup scripts outside the normal application startup process.  The assumption is that the database is legacy and the schema will likely not change.  However, this approach is not suitable for schemas that evolve over time because it makes the installed schema version difficult to track and requires manual coordination.  Production teams rely on a different approach to manage schema migrations.

One approach is to use **Flyway**.  With Flyway, we track each migration as part of the application's versioned source code.  To keep track of which migrations have already been applied, when they were applied, and whether they succeeded, Flyway adds a schema history table named `flyway_schema_history` by default. This table also stores migration checksums, descriptions, execution times, and other metadata.

Flyway performs the following steps:

1. It checks the configured database schema for `flyway_schema_history`. If the table does not exist, Flyway creates it.
2. It scans the configured migration locations, such as the application classpath, for available migrations.
3. It compares the available migrations with the entries in the schema history table. Previously applied migrations are validated using their checksums. If an applied migration has been modified, validation fails.
4. New migrations are marked as pending. Versioned migrations are sorted by version number and applied in order.
5. After each migration is applied, Flyway records the result in `flyway_schema_history`.

To use Flyway, we need **Flyway Core**:

```xml
<dependency>
    <artifactId>flyway-core</artifactId>
    <groupId>org.flywaydb</groupId>
</dependency>
```

Since our database is MySQL, we also need **Flyway MySQL**:

```xml
<dependency>
    <artifactId>flyway-mysql</artifactId>
    <groupId>org.flywaydb</groupId>
</dependency>
```

We add the above snippets in `pom.xml`.

We then create the directory `src/main/resources/db/migration` to contain our migration scripts.  In this directory, we create `V1__create_schema.sql` that contains the same scripts as `assets/create.sql`, and `V2__seed_initial_data.sql` that contains the same scripts as `assets/load.sql`.  The execution order is now explicit: the schema is created by version 1, then the data is loaded by version 2.

Flyway does not execute the SQL files in `assets`; the migration copies under `src/main/resources/db/migration` are the files used by the application.  We can technically delete them, but we keep the original files as reference copies.

To enable our app to use Flyway, we need to modify our production and test configuration files.  In `src/main/resources/application.yaml` we add new keys `spring.jpa.hibernate.ddl-auto` and `spring.flyway.enabled`:

```yaml
spring:
  application:
    name: starzz-boot
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
```

Hibernate is configured with `ddl-auto: validate`, so it checks that the JPA entities match the schema without attempting to create, update, or delete database tables.

The production datasource still obtains its connection details from environment variables. The
migrations therefore initialize the configured MySQL database without placing database credentials
in the repository.

For a fresh development database, we can drop the manually created tables before starting the application so Flyway can create them through V1. We should not drop an existing production database. An existing database must instead be baselined or migrated carefully before Flyway is introduced.

In `src/test/resources/application.yaml` we also add new key `spring.flyway.enabled` and `spring.jpa.hibernate.ddl-auto` to `validate`.  Since we now intend to use Flyway for our H2 database's creation and seeding, we can delete the keys `spring.jpa.defer-datasource-initialization` and `spring.sql`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=MySQL
    username: sa
    password:
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
  flyway:
    enabled: true
  h2:
    console:
      enabled: true
```

For our production and test databases, Spring Boot now has the following workflow: On startup, Flyway runs first and establishes the schema. Hibernate then checks the tables and columns against the entity mappings. If the schema and mappings do not match, startup fails rather than silently changing the database.

When a future schema change is needed, we add another migration such as `V3__add_new_column.sql`, run the application, and allow Flyway to apply it. The migration history then documents how the database evolved and gives production and test environments the same upgrade path.

</details>

### Chapter 9: Enhancing our app with Docker containerization

Project dependencies added:

    None

#### Overview

In this chapter, we package the Spring Boot application and its MySQL database as a reproducible Docker Compose stack.  The application and database run in separate Docker containers, allowing each service to be managed and configured independently.

The database container is derived from the official MySQL 9.6 Docker image, while the application container is built from a custom Dockerfile that packages the Spring Boot application into a lightweight Java runtime image.  A Compose file brings both containers together, configuring their environment variables, networking, database connection, and port mappings so the complete application stack can be started consistently with a single command.

```mermaid
flowchart LR
    User --> Compose
    Compose --> App[Spring Boot app\ncontainer]
    Compose --> MySQL[MySQL\ncontainer]
    App -->|jdbc:mysql://mysql:3306/starzz| MySQL
    MySQL --> Volume[(mysql-data\nvolume)]
```

<details>

<summary>Chapter Walkthrough</summary>

Containerization is a technology that packages an application and its dependencies into isolated, portable environments called **containers**, allowing the application to run consistently across different environments.  One popular containerization technology is **Docker**.  It is widely adopted, well supported by the Java and Spring ecosystem, and provides a straightforward way to package and run applications along with their dependencies.

In this project, we will create separate Docker images for each of the application's components.  We will use the official MySQL 9.6 Docker image for the database and build a custom image containing our Spring Boot application.  These images serve as the blueprints from which our containers are created.  The containers are the actual running instances of those images.

Because our application and database will run in separate containers, we also need a way to configure and manage how those containers work together.  This is where **Docker Compose** comes in.  Docker provides the containerization technology itself, while Docker Compose provides a convenient way to define, configure, network, and run multiple containers as a single application stack.

*The Docker images*

Since we will be using the MySQL 9.6 Docker image, we need to provide several environment variables to configure the MySQL container:

    MYSQL_DATABASE
    MYSQL_USER
    MYSQL_PASSWORD
    MYSQL_ROOT_PASSWORD

Rather than hardcoding these sensitive values later in our Compose configuration, we store them in our `.env` file:

    DOCKER_MYSQL_USER=<corresponds to MYSQL_USER>
    DOCKER_MYSQL_PASSWORD=<corresponds to MYSQL_PASSWORD>
    DOCKER_MYSQL_ROOT_PASSWORD=<corresponds to MYSQL_ROOT_PASSWORD>

We also define `DOCKER_MYSQL_URL`, which contains the JDBC URL that the application will use to connect to the MySQL container.  We keep this separate from `DB_URL`, which contains the URL used when connecting to our local MySQL instance.

Make sure that `DOCKER_MYSQL_URL` sets the flag `createDatabaseIfNotExist` to `true`:

    jdbc:mysql://mysql:3306/starzz?...&createDatabaseIfNotExist=true

`createDatabaseIfNotExist=true` creates the database, while Flyway creates the tables and seed data.

The `.env.example` file serves as a template for creating `.env` files:

    JWT_SECRET=<your_jwt_secret>

    DB_URL=<your_local_mysql_url>
    DB_USERNAME=<your_local_mysql_user>
    DB_PASSWORD=<your_local_mysql_password>

    DOCKER_MYSQL_URL=<your_docker_mysql_url>
    DOCKER_MYSQL_USER=<your_docker_mysql_user>
    DOCKER_MYSQL_PASSWORD=<your_docker_mysql_password>
    DOCKER_MYSQL_ROOT_PASSWORD=<your_docker_mysql_root_password>

We use this template to create `.env` files containing the values specific to each developer's local environment. These files remain on the developer's machine and are not committed to the repository.

For the application image, we create a `Dockerfile` that uses a two-stage build. The first stage compiles the project and creates the application's JAR using a Maven/JDK 17 base image. The second stage copies the JAR into a JRE 17 base image, giving us a smaller runtime image without Maven or the JDK.

The first stage copies the project files into the build image. Since the project contains files and directories that are not required to compile the application, we use `.dockerignore` to prevent unnecessary files from being included in the Docker build context.

*The Compose configuration*

We use `compose.yaml` to define and configure the two containers that make up our application stack: the `mysql` service for our MySQL database and the `app` service for our Spring Boot application.

The `mysql` service uses the official `mysql:9.6.0` image.  Its environment variables are populated from our `.env` file.  We map port `3306` on the host to port `3306` in the container and use a named volume, `mysql-data`, to persist the database data when the container is stopped or recreated.

The MySQL service also has a **health check** that periodically uses `mysqladmin` to verify that the database is ready to accept connections.  This is important because starting the MySQL container does not necessarily mean that MySQL is immediately ready for the application to use.

The `app` service is built from our `Dockerfile`.  Its environment variables configure the database connection and JWT secret using values from `.env`.  Port `8080` is mapped from the container to the host so that we can access the Spring Boot application from our local machine.

Finally, `depends_on` establishes the relationship between the two services.  Rather than simply starting the application after the MySQL container starts, we configure Compose to wait until the MySQL health check succeeds before starting the application.  This prevents the Spring Boot application from attempting to connect to a database that is still initializing.  This is particularly important because Flyway remains responsible for creating and seeding the database schema when the application starts.

*Running the Stack*

After creating the `.env` file, open a console window from the repository root and execute:

```bash
docker compose up --build
```

Compose starts the `mysql` service by downloading the MySQL image if it is not already available locally, performing the necessary configuration, and creating a container from the image.

For the `app` service, Compose builds an image using our `Dockerfile` and performs the necessary configuration. It waits until the `mysql` service passes its health check before starting the application container. When the application starts, Flyway applies any required database migrations and seeds the database as configured. The API is then available at `http://localhost:8080`.

To verify that Flyway migration worked, open another console window in the repository root and check for the Flyway logs:

```bash
docker compose logs app | grep -i flyway
```

There should be a log similar to

    Creating Schema History table `starzz`.`flyway_schema_history` ...

To stop the containers while retaining the database volume, execute:

```bash
docker compose down
```

This removes the containers and Compose network but leaves the `mysql-data` volume intact. The existing database contents therefore remain available when the stack is started again. As a result, the Flyway logs should not show any `Creating...` messages for objects that have already been created.

To remove the database volume and create a fresh database on the next startup, use the explicitly destructive volume option:

```bash
docker compose down --volumes
```

When the stack is restarted, the Flyway logs should now show `Creating...` messages.

</details>

### Chapter 10: Enhancing our app with Swagger UI

Project dependencies added:

    Springdoc OpenAPI Starter WebMVC UI

#### Overview

In this chapter, we add generated OpenAPI documentation and an interactive Swagger UI to the application. We first configure Swagger UI to document and provide access to our public endpoints.  We then configure JWT bearer authentication in the UI so that our protected endpoints can also be explored and tested.

The OpenAPI documentation endpoints are made publicly accessible through explicit permit-all rules in the existing security
configuration.  Protected API endpoints continue to require their existing JWT authentication.

```mermaid
flowchart LR
    Controllers[Existing controllers] --> Docs["/v3/api-docs"]
    Browser --> Swagger[Swagger UI]
    Swagger --> Docs

    Browser --> API[Application API]
    API --> Security[Existing JWT security]
```

<details>

<summary>Chapter walkthrough</summary>

OpenAPI provides a standardized way to describe a REST API, including its endpoints, parameters, request bodies, and responses.  Swagger UI presents the OpenAPI specification through an interactive web interface, making it easier for developers to explore and understand the API.  In short, OpenAPI defines the specification that describes the API, while Swagger UI provides an interface for viewing and testing that specification.

The API documentation in this chapter is generated automatically from the existing Spring Boot application.  More detailed documentation could be added by annotating our controllers, methods, and model classes with additional OpenAPI metadata.  However, the objective of this chapter is to demonstrate how API documentation can be added to an existing application with minimal modification.  We therefore rely primarily on information that can be inferred from the application's existing code rather than modifying the existing controllers solely for documentation purposes.

To auto-generate the documentation, we need **Springdoc OpenAPI Starter WebMVC UI** (or **Springdoc** for short):

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.13</version>
</dependency>
```

We configure the documentation paths in `application.yaml`:

```yaml
springdoc:
  api-docs:
    enabled: true
    path: /v3/api-docs
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
```

Adding OpenAPI documentation and Swagger UI introduces new URL paths.  Recall that our application has JWT-protected endpoints and that we created a custom security configuration at
`src/main/java/com/sanjayrisbud/starzzboot/config/SecurityConfig.java`.  This code:

```java
...
.authorizeHttpRequests(auth -> auth
    .requestMatchers(HttpMethod.POST, "/login").permitAll()
    .requestMatchers(HttpMethod.PATCH, "/users/*/change-password").permitAll()
    .requestMatchers(HttpMethod.GET, "/galaxies/**", "/constellations/**", "/stars/**").permitAll()
    .requestMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
    .anyRequest().authenticated()
...
```

requires all paths that are not explicitly excluded to have a JWT. Our new documentation URLs will therefore also require JWTs unless we explicitly allow them.

Our objective is to make the documentation publicly available, so we add rules that exclude the OpenAPI and Swagger UI paths from the JWT requirement:

```java
...
.authorizeHttpRequests(auth -> auth
    .requestMatchers(HttpMethod.POST, "/login").permitAll()
    .requestMatchers(HttpMethod.PATCH, "/users/*/change-password").permitAll()
    .requestMatchers(HttpMethod.GET, "/galaxies/**", "/constellations/**", "/stars/**").permitAll()
    .requestMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
    // These rules are for Swagger UI and OpenAPI documentation.
    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
    .anyRequest().authenticated()
...
```

To let Swagger UI call protected endpoints, we define the JWT Bearer scheme in a separate OpenAPI configuration. This describes the authentication mechanism in the generated document; it does not change the application's existing security rules:

```java
@Bean
public OpenAPI starzzOpenAPI() {
    return new OpenAPI()
        .components(new Components()
            .addSecuritySchemes("bearerAuth", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
}
```

Once the application is running, Springdoc inspects the existing Spring MVC controllers and generates an OpenAPI 3 specification.  Swagger UI presents that specification as an interactive web page, allowing users to inspect routes, request parameters, request bodies, response types, and other information that can be inferred from the application.

The generated specification is available at `http://localhost:8080/v3/api-docs`, and Swagger UI is available at
`http://localhost:8080/swagger-ui.html`:

![Swagger UI 1](assets/swagger_ui_1.png)

Swagger UI can display the API and invoke public endpoints such as `GET /constellations` without a JWT:

![Swagger UI 2](assets/swagger_ui_2.png)

Note the open padlock icon, which signifies that the request is unprotected.

Without a JWT, calls to protected endpoints such as `GET /users/1` return **HTTP 401**:

![Swagger UI 3](assets/swagger_ui_3.png)

Again note the open padlock icon.

To access the protected endpoints, first login using the `/login` endpoint and copy the returned token:

![Swagger UI Login](assets/swagger_ui_login.png)

Swagger UI has an **Authorize** button at the top:

![Swagger UI 4](assets/swagger_ui_4.png)

Click it and in the resulting dialog box, input the token you had just copied and click **Authorize**:

![Swagger UI Input Token](assets/swagger_ui_input_token.png)

It should now say "Authorized".  Close the dialog box and retry the protected endpoint.  It should now return **HTTP 200**:

![Swagger UI 5](assets/swagger_ui_5.png)

Note the closed padlock icon, which signifies that the request is protected.

To aid in demos, a dummy user is inserted in the database.  Refer to `src/main/resources/db/migration/V3__insert_admin_user_for_demos.sql` for details.

</details>

## Conclusion

**starzz-boot** demonstrates a complete, production-style Spring Boot REST API built incrementally — from setting up routes and wiring in a MySQL database, to layering in DTO mapping, validation, and global exception handling, to covering the application with unit and integration tests, to securing passwords with BCrypt hashing, locking down the API with Spring Security, JWT-based authentication, and role-based access control. We then enhance our application with production best practices, specifically implementing database migrations with Flyway, packaging the application with Docker, and documenting the API with OpenAPI docs/Swagger UI. Each chapter builds on the last, reflecting how a real backend evolves in practice.
