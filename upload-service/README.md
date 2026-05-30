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
