# Upload Service

## Clean Layering

Layered Architecture
3-Layer Architecture
Controller-Service-Repository Pattern
MVC-style backend structure

Use separate classes for separate responsibilities:

```text
Entity      -> database model
DTO         -> API request/response shape
Repository  -> database access
Service     -> business logic
Controller  -> HTTP endpoints
```

Current structure:

```text
Entities/Video.java
DTO/VideoDto.java
Repositories/VideoRepository.java
Services/VideoService.java
Controllers/VideoController.java
```

## Why DTOs Matter

`Video` is the database entity. It can contain internal fields that the API should not expose, such as storage paths, internal bucket names, processing details, soft-delete flags, or audit data.

`VideoDto` is the public API shape. It includes only the fields that clients should see.

For example, the entity has:

```text
storagePath
updatedAt
```

The DTO intentionally does not return `storagePath`, because that is an internal server detail. If controllers returned `Video` directly, adding a new entity field later could automatically expose it in JSON responses.

## Practical Rule

Return DTOs from controllers. Keep entities inside the service/repository layer.

## Startup Seed Data

`Seeders/VideoDataSeeder.java` inserts a few sample `Video` rows when the app starts.

It only runs when the `videos` table is empty:

```text
if videoRepository.count() > 0, no seed rows are inserted
```

This keeps restarts from duplicating the sample videos.

## Persistence Stack

This service uses Spring Data JPA to save `Video` records in a database.

The full path looks like this:

```text
VideoService
  -> VideoRepository
    -> Spring Data JPA
      -> JPA
        -> Hibernate
          -> JDBC
            -> H2 database
```

### H2

H2 is the actual database engine used by this service during local development.

If the datasource URL uses `mem`, the database is temporary:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
```

Data stored in an in-memory H2 database disappears when the app stops.

To keep data after restarting the app, use file-based H2:

```properties
spring.datasource.url=jdbc:h2:file:./data/upload-service-db
```

This creates database files under `data/`, such as:

```text
data/upload-service-db.mv.db
```

### JDBC

JDBC is the low-level Java API used to communicate with databases.

With plain JDBC, code would manually open connections, write SQL, set parameters, and read results.

Spring Data JPA and Hibernate still use JDBC underneath, but the application code usually does not call JDBC directly.

### JPA

JPA is the Java standard for mapping Java objects to database tables.

For example:

```java
@Entity
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

`@Entity` means `Video` should be stored as a database table.

JPA is a specification. It defines the rules and annotations, but it needs an implementation to do the real work.

### Hibernate

Hibernate is the JPA implementation used by Spring Boot by default.

It reads the JPA annotations, tracks entity changes, creates SQL, inserts rows, updates rows, and loads data from the database.

When this code runs:

```java
videoRepository.save(video);
```

Hibernate turns that into SQL similar to:

```sql
insert into video (...) values (...);
```

### Spring Data JPA

Spring Data JPA is the Spring layer that makes JPA easier to use.

Instead of writing database access code manually, this service defines a repository interface:

```java
public interface VideoRepository extends JpaRepository<Video, Long> {
}
```

Spring automatically provides common methods:

```text
save(...)
findById(...)
findAll()
deleteById(...)
```

That is why `VideoService` can persist a video with:

```java
Video savedVideo = videoRepository.save(video);
```

### Schema Updates

For local development, this setting keeps the schema in sync without dropping existing data:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Avoid these settings when you want data to survive restarts:

```properties
spring.jpa.hibernate.ddl-auto=create
spring.jpa.hibernate.ddl-auto=create-drop
```

Those recreate or drop tables and can delete existing rows.
