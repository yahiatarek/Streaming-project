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

This service uses Spring Data JPA and Hibernate to save `Video` records in PostgreSQL.

The full path looks like this:

```text
VideoService
  -> VideoRepository
    -> Spring Data JPA
      -> JPA
        -> Hibernate
          -> JDBC
            -> PostgreSQL
```

### PostgreSQL

PostgreSQL is the database engine used by this service during local development.
Run it through Docker Compose:

```bash
docker compose up -d postgres
```

The local development URI is:

```text
jdbc:postgresql://localhost:5432/upload_service
```

Inside Docker, services use the internal Docker hostname:

```text
jdbc:postgresql://postgres:5432/upload_service
```

### JPA entity

```java
@Entity
@Table(name = "videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

`@Entity` means `Video` should be stored in a relational table.
`@GeneratedValue(strategy = GenerationType.IDENTITY)` lets PostgreSQL generate numeric ids.

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

### Schema updates

For local development, Hibernate keeps the PostgreSQL schema in sync with the entity model:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Avoid destructive schema modes when you want data to survive restarts:

```properties
spring.jpa.hibernate.ddl-auto=create
spring.jpa.hibernate.ddl-auto=create-drop
```

Those recreate or drop tables and can delete existing rows.
