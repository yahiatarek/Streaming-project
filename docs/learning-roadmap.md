# Streaming Project Learning Roadmap

This roadmap maps the course topics and additional backend concerns to concrete
features in this project. A topic is only marked as complete after implementation
and verification.

## Status legend

- `DONE`: implemented and verified
- `IN PROGRESS`: implementation exists but verification or related work remains
- `TODO`: not implemented yet
- `MODERNIZED`: course technology is obsolete and is practiced through its current replacement

## REST API and persistence

| Topic | Project exercise | Status |
|---|---|---|
| REST controllers and DTOs | Video API | DONE |
| Correct HTTP status and Location header | Create video returns `201 Created` | IN PROGRESS |
| GET, POST, PUT and DELETE | Video CRUD endpoints | IN PROGRESS |
| Bean Validation | Validate create and update requests | IN PROGRESS |
| Resource-specific 404 handling | `VideoNotFoundException` | IN PROGRESS |
| Global exception handling | Standard `ApiError` response | IN PROGRESS |
| OpenAPI and Swagger UI | Document the Video API | IN PROGRESS |
| XML content negotiation | Return selected resources as JSON or XML | TODO |
| Internationalization | Localized validation and API messages | TODO |
| API versioning | Implement and compare two versioning approaches | TODO |
| HATEOAS and HAL Explorer | Add links to video resources | TODO |
| Static and dynamic filtering | Protect internal video metadata | TODO |
| Actuator | Health and operational endpoints | IN PROGRESS |
| JPA and Hibernate | Persist videos in PostgreSQL | IN PROGRESS |
| Entity relationships | User, Video, Category and Comment relationships | TODO |
| Custom JPA queries | Search and filtering endpoints | TODO |
| H2 | Replaced by local PostgreSQL for the upload-service | MODERNIZED |
| Production database | PostgreSQL profile | IN PROGRESS |
| Database migrations | Flyway migrations instead of schema auto-update | TODO |

## Security

| Topic | Project exercise | Status |
|---|---|---|
| Basic Authentication | Course-compatible protected endpoint | TODO |
| Modern API authentication | OAuth2/JWT at the gateway | TODO |
| Authorization | User, uploader and administrator roles | TODO |
| Service-to-service security | Protect internal endpoints | TODO |
| Secrets | Docker/Kubernetes secrets; no credentials in Git | TODO |
| Security tests | Authentication and authorization scenarios | TODO |

## Spring Cloud and service communication

| Topic | Project exercise | Status |
|---|---|---|
| Centralized configuration | Config Server and environment profiles | DONE |
| Service discovery | Eureka registry | DONE |
| API Gateway | Spring Cloud Gateway discovery routes | DONE |
| Gateway filter | Log selected service instance | DONE |
| Client-side load balancing | Four upload-service instances | DONE |
| Feign | Implement one course-compatible internal client | TODO |
| HTTP Service Client | Implement the same call with the modern Spring API | TODO |
| Ribbon | Use Spring Cloud LoadBalancer instead | MODERNIZED |
| Zuul | Use Spring Cloud Gateway instead | MODERNIZED |

## Resilience and asynchronous processing

| Topic | Project exercise | Status |
|---|---|---|
| Retry | Retry a temporary processing-service failure | TODO |
| Circuit Breaker | Stop calls to an unhealthy processing-service | TODO |
| Fallback | Return or persist a meaningful degraded result | TODO |
| Rate Limiter | Protect upload-signature generation | TODO |
| Bulkhead | Isolate video-processing capacity | TODO |
| Time Limiter | Bound remote processing calls | TODO |
| Hystrix | Use Resilience4j instead | MODERNIZED |
| Domain events | Publish `VideoUploaded` after persistence | TODO |
| Event handlers | Trigger processing and notification handlers | TODO |
| RabbitMQ | Asynchronous inter-service event delivery | TODO |
| Idempotency | Prevent duplicate event processing | TODO |
| Transactional Outbox | Reliably connect database writes and events | TODO |
| Spring Cloud Bus | Refresh distributed configuration through RabbitMQ | TODO |

## Observability and quality

| Topic | Project exercise | Status |
|---|---|---|
| Distributed tracing | Micrometer Tracing and Zipkin | IN PROGRESS |
| Spring Cloud Sleuth | Use Micrometer Tracing instead | MODERNIZED |
| Correlated logs | Trace and service identifiers in logs | IN PROGRESS |
| Metrics | HTTP, JVM and business metrics | TODO |
| Unit tests | Service behavior with Mockito | TODO |
| MVC integration tests | Endpoint, validation and error tests | IN PROGRESS |
| Repository tests | JPA queries and mappings | TODO |
| End-to-end tests | Testcontainers-based service workflow | TODO |
| Contract tests | Verify service API expectations | TODO |

## Deployment

| Topic | Project exercise | Status |
|---|---|---|
| Dockerfiles | Container images for existing services | DONE |
| Multi-stage optimized builds | Review and optimize all Dockerfiles | TODO |
| Docker Compose | Local microservice environment | DONE |
| Kubernetes Deployments | Deploy each service declaratively | TODO |
| Kubernetes Services | Internal service discovery | TODO |
| ConfigMaps and Secrets | Externalized Kubernetes configuration | TODO |
| Readiness and liveness probes | Actuator-backed probes | TODO |
| ReplicaSets and scaling | Multiple upload and processing replicas | TODO |
| Horizontal Pod Autoscaler | Scale from CPU or application metrics | TODO |

## German-learning workflow

For each user message:

1. Provide a natural corrected version when there are meaningful errors.
2. Explain only the most useful grammar or vocabulary corrections.
3. Answer the technical request separately.
4. Reuse relevant technical vocabulary in correct German.
