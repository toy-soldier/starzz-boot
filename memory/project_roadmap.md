---
name: Project Roadmap
description: Planned features and what is intentionally deferred in starzz-boot
type: project
---

Project is still in progress. Planned additions in order:

1. Controller tests (currently studying @WebMvcTest + MockMvc) — immediate next step
2. Spring Security with password hashing and JWT authentication
3. Externalize hardcoded DB credentials to environment variables
4. Pagination on list endpoints
5. Swagger/OpenAPI docs (after main features are done)

**Why:** No DB migrations is intentional. Hardcoded credentials in application.yaml are known and will be fixed later.
