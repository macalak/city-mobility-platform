# Agent Task: Set Up the City Mobility Hexagonal Architecture Demo

## Mission

Set up this repository as a complete, conference-ready Java demonstration of:

- Domain-Driven Design
- Hexagonal Architecture, also known as Ports and Adapters
- Modular Monolith architecture
- Spring Boot
- Spring Modulith
- ArchUnit architecture verification
- Gradle Kotlin DSL
- AI-assisted development with a reusable prompt pack

The application domain is a **city mobility rental platform** supporting multiple mobility providers and transport types. The initial implementation must support:

- City Bike rental
- Canal Boat rental

The repository must be prepared for a live-coding exercise in which an AI coding agent adds:

- Green Scooter rental

The main architectural demonstration is that adding Green Scooter requires changes only at the integration boundary and its tests. The existing domain model, use case, application service, provider registry, and REST controller must not require provider-specific modifications.

---

## Working mode

Act as a senior Java architect and implementation agent.

Follow this workflow:

1. Inspect the current repository before editing.
2. Read `AGENTS.md` and `CLAUDE.md` when they exist.
3. Preserve useful existing files and user changes.
4. Produce a short implementation plan.
5. Implement the project in small, coherent increments.
6. Keep the project compilable after each increment.
7. Run focused tests after each major step.
8. Run the complete verification workflow before finishing.
9. Review the final Git diff for architectural violations and unrelated changes.
10. Report actual command results. Never claim that a test passed unless it was executed successfully.

Do not wait for confirmation between normal implementation steps. Stop only when a requirement conflicts with an existing architectural rule or when an external decision is genuinely required.

---

## Technology baseline

Use:

- Java 21
- Spring Boot
- Gradle Kotlin DSL
- Spring Modulith
- JUnit 5
- AssertJ
- ArchUnit
- Spring Boot Test
- JaCoCo

Use versions already defined in the repository when they are compatible. If the repository is empty, select mutually compatible stable versions and centralize them in Gradle configuration.

The project must be buildable with:

```bash
./gradlew clean check
```

The application must be runnable with:

```bash
./gradlew bootRun
```

If no Gradle wrapper exists and Gradle is available, generate and commit the standard wrapper:

```bash
gradle wrapper
```

Do not create placeholder wrapper scripts or a fake `gradle-wrapper.jar`.

---

## Architectural principles

### Dependency direction

Dependencies must point inward:

```text
inbound adapters  ─┐
                   ├──> application ──> domain
outbound adapters ─┘

bootstrap/composition root ──> application + adapters
```

### Domain

The domain must:

- contain pure Java business objects;
- contain aggregates, value objects, domain rules, and invariants;
- not depend on Spring;
- not depend on Jackson;
- not depend on HTTP types;
- not depend on Jakarta Persistence;
- not depend on provider SDKs, clients, or DTOs;
- not import adapter or bootstrap packages.

### Application

The application layer must:

- define inbound use-case ports;
- define outbound capability ports;
- orchestrate use cases;
- depend on the domain;
- not depend on adapters;
- not contain provider-specific conditional logic;
- own the abstraction represented by `RentalProviderPort`.

### Adapters

Adapters must:

- translate between external protocols and application-owned types;
- keep provider DTOs inside provider-specific packages;
- translate technical failures to application-owned exceptions;
- avoid implementing business invariants;
- implement ports declared by the application;
- remain replaceable without changing the domain.

### Composition root

Spring configuration must:

- live outside the domain;
- wire application services and outbound adapters;
- inject all `RentalProviderPort` implementations into the provider registry;
- avoid service-locator and provider-switch patterns.

---

## Required bounded context

Create a `rental` bounded context.

Use package structure equivalent to:

```text
com.example.mobility
├── MobilityApplication
├── bootstrap or root composition configuration
└── rental
    ├── domain
    ├── application
    │   ├── port
    │   │   ├── in
    │   │   └── out
    │   └── service
    └── adapter
        ├── in
        │   └── web
        └── out
            ├── persistence
            └── provider
                ├── citybike
                └── canalboat
```

Package visibility should be as restrictive as practical. Avoid making implementation classes public unless Spring composition or tests require it.

---

## Domain model

Implement the following domain concepts.

### Value objects

- `RentalId`
- `RenterId`
- `ProviderId`
- `VehicleId`

Validate mandatory values in their constructors. Prefer records where appropriate.

### Enumerations

`VehicleType`:

```text
BICYCLE
SCOOTER
BOAT
CAR
```

`RentalStatus`:

```text
ACTIVE
COMPLETED
```

### Rental aggregate

Implement `Rental` as the aggregate root.

It should contain at least:

- rental ID;
- renter ID;
- provider ID;
- external vehicle ID;
- vehicle type;
- external rental/session ID;
- start timestamp;
- status;
- optional completion timestamp.

Required behavior:

- rental starts in `ACTIVE` state;
- only an active rental can be completed;
- completion cannot precede start;
- completing the same rental twice fails;
- invalid mandatory values fail fast.

Use intention-revealing methods such as:

```java
Rental.start(...)
rental.complete(...)
```

Do not add Spring, persistence, or serialization annotations to the aggregate.

---

## Application ports

### Inbound port

Create:

```java
StartRentalUseCase
StartRentalCommand
```

The command must carry application and domain values, not HTTP or provider DTOs.

### Outbound ports

Create:

```java
RentalProviderPort
RentalRepository
TimeProvider
```

`RentalProviderPort` must:

- expose its `ProviderId`;
- start a provider rental;
- return an application-owned provider confirmation;
- not expose provider-specific DTOs.

The confirmation must contain at least:

- external rental/session ID;
- vehicle ID;
- vehicle type.

`RentalRepository` must support:

- checking whether a renter has an active rental;
- saving a rental;
- retrieving a rental by ID.

`TimeProvider` must make time deterministic in application tests.

---

## Application services

### RentalProviderRegistry

Implement a registry that:

- receives all `RentalProviderPort` implementations;
- indexes them by `ProviderId`;
- returns the corresponding provider;
- throws an application-owned exception for an unknown provider;
- rejects duplicate provider IDs rather than silently overwriting them.

Do not implement provider selection using:

- `switch`;
- `if/else` chains;
- reflection;
- class-name conventions;
- a custom plugin framework.

### RentalApplicationService

Implement `StartRentalUseCase`.

Required behavior:

1. Reject a request when the renter already has an active rental.
2. Resolve the selected provider through `RentalProviderRegistry`.
3. Start the rental through `RentalProviderPort`.
4. Create a `Rental` aggregate from the provider-neutral confirmation.
5. Persist the rental through `RentalRepository`.
6. Return the new `RentalId`.

The service must not know whether the provider rents bikes, boats, scooters, or cars.

### Application exceptions

Create application-owned exceptions equivalent to:

- `UnknownRentalProvider`
- `RenterAlreadyHasActiveRental`
- `ProviderIntegrationException`

Do not expose raw provider client exceptions through the inbound API.

---

## Initial outbound adapters

### In-memory persistence adapter

Implement `InMemoryRentalRepository` using a thread-safe collection.

It is deliberately used instead of JPA because the conference topic is architectural isolation and provider extensibility, not persistence technology.

### City Bike provider

Create a provider-specific package containing:

- client abstraction;
- provider request and response types;
- simulated client implementation;
- `CityBikeRentalAdapter`;
- Spring configuration.

Required mapping:

```text
Provider ID       -> city-bike
Vehicle type      -> BICYCLE
Renter ID         -> provider customer reference
Vehicle ID        -> provider bike identifier
Provider rental ID -> external rental ID
```

Translate provider client failures to `ProviderIntegrationException`.

### Canal Boat provider

Create an equivalent adapter for:

```text
Provider ID       -> canal-boat
Vehicle type      -> BOAT
```

Use a provider-specific vocabulary such as boat hire, vessel code, or passenger reference inside this adapter only. Translate those concepts to the application-owned port model.

---

## REST inbound adapter

Create a REST endpoint for starting a rental.

Recommended route:

```http
POST /api/rentals
```

Example request:

```json
{
  "renterId": "anna",
  "providerId": "city-bike",
  "vehicleId": "bike-42"
}
```

Return:

- HTTP `201 Created` on success;
- a location header for the created rental;
- a response containing the rental ID and active status.

Add centralized error handling for:

- unknown provider;
- renter already having an active rental;
- provider integration failure;
- invalid request.

Use stable machine-readable error codes, for example:

```text
UNKNOWN_RENTAL_PROVIDER
ACTIVE_RENTAL_EXISTS
PROVIDER_UNAVAILABLE
INVALID_REQUEST
```

Keep REST request and response DTOs inside the inbound web adapter.

---

## Spring composition

Configure beans for:

- `RentalRepository`;
- `TimeProvider`;
- `RentalProviderRegistry`;
- `StartRentalUseCase`;
- City Bike client and adapter;
- Canal Boat client and adapter.

Spring must inject the collection of `RentalProviderPort` implementations into `RentalProviderRegistry`.

Do not annotate domain or application service classes merely to make component scanning convenient. Prefer explicit bean composition for the core.

---

## Spring Modulith

Mark `rental` as an application module using the repository's compatible Spring Modulith convention.

Create a test using:

```java
ApplicationModules.of(MobilityApplication.class).verify();
```

Create a documentation test that generates:

- a module-level PlantUML diagram;
- individual module diagrams;
- module canvases when supported by the selected Spring Modulith version.

Ensure the composition root is not accidentally interpreted as a business module that violates module boundaries.

---

## ArchUnit verification

Create executable architecture rules proving at least:

1. Domain does not depend on Spring.
2. Domain does not depend on Jackson.
3. Domain does not depend on persistence APIs.
4. Domain does not depend on adapters.
5. Application does not depend on adapters.
6. Adapters do not depend on the composition root.
7. Provider-specific packages are not referenced by domain or application packages.

Do not create rules that pass trivially because they scan the wrong package.

---

## Test suite

Implement tests at the correct architectural level.

### Domain tests

Test:

- starting a rental;
- completing an active rental;
- rejecting duplicate completion;
- rejecting completion before start;
- mandatory value validation.

These tests must not start Spring.

### Application tests

Test `RentalApplicationService` with:

- a stub `RentalProviderPort`;
- an in-memory repository;
- a fixed `TimeProvider`.

Verify:

- the provider is selected by ID;
- the provider-neutral confirmation creates a rental;
- an active rental blocks another rental;
- an unknown provider fails;
- provider-specific technology is absent from the test.

These tests must not start Spring.

### Provider adapter tests

Test City Bike and Canal Boat mappings.

Verify:

- provider ID;
- request mapping;
- response mapping;
- vehicle type;
- exception translation.

### REST integration tests

Start a real Spring test context with a random port.

Verify:

- City Bike rental returns `201 Created`;
- Canal Boat rental returns `201 Created`;
- unsupported `green-scooter` initially returns the unknown-provider response;
- validation errors return the defined API error contract.

Use unique renter IDs in integration tests because active rentals remain in the in-memory repository for the life of the context.

### Architecture tests

Run both:

- ArchUnit rules;
- Spring Modulith verification.

---

## Green Scooter live-coding preparation

Green Scooter must not be implemented in the baseline branch created by this setup task. The baseline must intentionally reject:

```text
green-scooter
```

Create:

```text
docs/live-coding-green-scooter.md
```

Document this external contract:

```http
POST /api/scooters/{scooterId}/activation
```

Request:

```json
{
  "customerReference": "anna"
}
```

Response:

```json
{
  "sessionId": "session-123",
  "scooterCode": "scooter-17"
}
```

Explain that the live exercise should add equivalents of:

```text
GreenScooterClient
ActivateScooterRequest
ActivateScooterResponse
SimulatedGreenScooterClient
GreenScooterRentalAdapter
GreenScooterConfiguration
GreenScooterRentalAdapterTest
```

The live exercise must keep these existing elements unchanged unless the current repository proves a correction is necessary:

```text
Rental
StartRentalUseCase
StartRentalCommand
RentalApplicationService
RentalProviderRegistry
RentalController
```

---

## AI agent instructions

Create `AGENTS.md` in the repository root.

It must define:

- project purpose;
- dependency direction;
- domain isolation rules;
- rules for inbound and outbound ports;
- provider adapter conventions;
- restrictions against provider-specific branches;
- restriction against speculative abstractions;
- required build and test commands;
- safe handling of secrets and customer data;
- definition of done.

Also create `CLAUDE.md` that tells compatible Claude-based agents to follow `AGENTS.md` as the authoritative repository guidance.

---

## Vibe-coding prompt pack

Create:

```text
docs/ai-prompts/
├── README.md
├── 00-session-kickoff.md
├── 01-explore-and-explain.md
├── 02-green-scooter-plan.md
├── 03-green-scooter-build.md
├── 04-test-and-fix-loop.md
├── 05-architecture-review.md
├── 06-demo-polish.md
└── 07-recovery-prompts.md
```

### Prompt pack rules

The prompt pack must encourage fast, conversational implementation while keeping architecture explicit.

The sequence must cover:

1. Reading project instructions.
2. Exploring the existing Start Rental flow.
3. Planning Green Scooter without editing.
4. Implementing in small increments.
5. Running a bounded test-and-fix loop.
6. Reviewing the architecture as a separate role.
7. Polishing the result for the conference.
8. Recovering when the agent over-engineers or violates boundaries.

Include recovery prompts for:

- domain contamination;
- provider switch statements;
- speculative abstractions;
- disabled or weakened tests;
- completion claims without executed verification;
- scope becoming too large for live coding.

### Prompt pack README

The prompt pack README must explain:

- prerequisites;
- how to use the prompts in IntelliJ IDEA;
- recommended prompt order;
- plan-review-implement-verify workflow;
- expected Green Scooter file changes;
- human review gates;
- fallback and recovery strategy;
- safe AI usage;
- presenter commentary.

Use this core message:

> Vibe coding does not mean architecture-free coding. Explicit architectural boundaries make AI-generated changes safer and easier to review.

---

## Project README

Create or update the repository `README.md`.

It must include:

- project purpose;
- architecture overview;
- Mermaid architecture diagram;
- package structure;
- technology stack;
- prerequisites;
- Gradle commands;
- example REST calls for City Bike and Canal Boat;
- the expected unknown-provider response for Green Scooter in the baseline;
- testing and architecture-verification commands;
- generated Spring Modulith documentation location;
- AI prompt pack usage;
- live-coding preparation notes;
- design trade-offs and intentional simplifications.

State clearly that the in-memory repository and simulated provider clients are intentional conference-demo choices.

Do not describe the baseline as production-ready in operational terms. It is production-quality in structure and testing, but intentionally omits production infrastructure such as durable persistence, authentication, retries, circuit breakers, observability backends, and real provider HTTP clients.

---

## Build configuration

Configure Gradle with:

- Java toolchain 21;
- Maven Central;
- Spring Boot plugin;
- dependency management or platform alignment;
- Spring Modulith dependencies;
- JUnit and Spring Boot test dependencies;
- ArchUnit;
- JaCoCo;
- JUnit Platform;
- reproducible build behavior where practical.

Add a clear verification task or ensure `check` runs all required tests.

If dedicated test tasks such as `architectureTest` are created, avoid executing the same architecture tests twice unless duplication is intentional and documented.

Add `.gitignore` for:

- Gradle state;
- build output;
- IntelliJ IDEA files;
- OS metadata;
- generated temporary files.

Do not ignore `AGENTS.md`, prompt files, generated source documentation committed by design, or Gradle wrapper files.

---

## Optional CI

If the repository already uses GitHub Actions, add or update a workflow that:

- checks out the repository;
- configures Java 21;
- validates the Gradle wrapper when the validation action is available;
- runs `./gradlew clean check`;
- does not require secrets.

If another CI platform is already present, follow the existing platform rather than introducing a second one.

---

## Verification workflow

Run the narrowest useful checks first, then the complete suite.

Expected sequence:

```bash
./gradlew test --tests '*RentalTest'
./gradlew test --tests '*RentalApplicationServiceTest'
./gradlew test --tests '*CityBike*'
./gradlew test --tests '*CanalBoat*'
./gradlew test --tests '*Architecture*'
./gradlew test --tests '*Modulith*'
./gradlew clean check
```

Adjust test patterns to the actual class names.

Also inspect:

```bash
git status --short
git diff --stat
git diff
```

Do not:

- disable failing tests;
- weaken architecture rules;
- exclude failing classes merely to obtain a green build;
- suppress compiler warnings without understanding them;
- claim that the Gradle wrapper works without executing it;
- introduce external service requirements for the test suite.

---

## Definition of done

The task is complete only when all applicable conditions are satisfied:

### Build

- [ ] The standard Gradle wrapper is present and executable.
- [ ] `./gradlew clean check` succeeds.
- [ ] Java 21 toolchain configuration is correct.
- [ ] The application starts through `./gradlew bootRun` or a documented equivalent smoke test.

### Architecture

- [ ] Domain is free of framework and provider dependencies.
- [ ] Application does not depend on adapters.
- [ ] Providers implement the application-owned port.
- [ ] No provider switch is present in the use case.
- [ ] Spring Modulith verification passes.
- [ ] ArchUnit verification passes.

### Functionality

- [ ] City Bike rental succeeds.
- [ ] Canal Boat rental succeeds.
- [ ] Unknown provider is handled consistently.
- [ ] Green Scooter is intentionally unsupported in the baseline.
- [ ] Active-rental invariant is enforced.

### Tests

- [ ] Domain tests pass without Spring.
- [ ] Application tests pass without Spring.
- [ ] Adapter mapping and failure tests pass.
- [ ] REST integration tests pass.
- [ ] Full verification passes.

### AI support

- [ ] `AGENTS.md` exists and is accurate.
- [ ] `CLAUDE.md` points to the authoritative instructions.
- [ ] The complete prompt pack exists.
- [ ] Prompt-pack README explains the workflow.
- [ ] Recovery prompts are included.

### Documentation

- [ ] Root README explains architecture and usage.
- [ ] Green Scooter live-coding scenario is documented.
- [ ] Example commands match the actual project.
- [ ] Intentional demo simplifications are explicit.

---

## Final response format

When setup is complete, respond with:

### Summary

A concise description of what was created.

### Architecture

The implemented dependency direction, module boundary, and extension mechanism.

### Files changed

Group files by:

- build and configuration;
- domain;
- application;
- adapters;
- tests;
- architecture verification;
- AI instructions and prompts;
- documentation.

### Verification evidence

List every executed command and its actual outcome.

### Green Scooter readiness

Explain:

- why Green Scooter fails in the baseline;
- which files the live-coding agent is expected to add;
- which core files should remain unchanged.

### Limitations

State any unexecuted verification, environment problem, incompatible version, or remaining assumption. Do not hide incomplete work.

### Suggested commit

Provide a concise commit message, for example:

```text
feat: add hexagonal city mobility conference demo
```

---

## Presenter-oriented architecture messages

Preserve these messages in the documentation and generated prompt pack:

> DDD tells us what belongs inside the model. Hexagonal architecture protects that model from technology and external systems.

> An inbound port expresses what the application offers. An outbound port expresses what the application needs.

> The application owns the port. The provider adapter translates the provider protocol into the application's language.

> Spring is the composition mechanism at the edge. The domain is not a Spring application.

> A new integration should primarily change integration code. A new business invariant should change the domain.

> AI accelerates implementation, but architecture rules, tests, and human review govern the result.

> Vibe coding does not mean architecture-free coding.
