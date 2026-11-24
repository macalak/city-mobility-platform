# AGENTS.md

## Purpose
This repository is a conference-ready Java demo for a city mobility rental platform built with Spring Boot, hexagonal architecture, and Spring Modulith. The baseline intentionally supports City Bike and Canal Boat and intentionally rejects Green Scooter.

## Architecture rules
- Dependency direction: inbound adapters -> application -> domain; bootstrap composes adapters and application.
- Domain must remain free from Spring, Jackson, Jakarta Persistence, HTTP types, and provider-specific packages.
- Application owns the `RentalProviderPort` abstraction and must not depend on adapters.
- Providers are integration adapters: they translate provider protocols into application-owned types.
- New providers should be added at the edges without changing domain or use-case code.
- Do not add provider switch logic, reflection, or speculative abstraction.

## Required workflow
- Read project instructions before changing code.
- Preserve useful existing code and user edits.
- Prefer small, verifiable implementation steps.
- Keep the project runnable after each step.
- Run the smallest relevant tests before broader validation.
- Finish with the full verification workflow.

## Build and test commands
```bash
./gradlew test --tests '*RentalTest'
./gradlew test --tests '*RentalApplicationServiceTest'
./gradlew test --tests '*CityBike*'
./gradlew test --tests '*CanalBoat*'
./gradlew test --tests '*Architecture*'
./gradlew test --tests '*Modulith*'
./gradlew clean check
./gradlew bootRun
```

## Handling secrets and customer data
- Never add secrets, API keys, or provider credentials to source control.
- Keep simulated provider data local to the adapter layer.
- Do not expose customer data in logs or documentation.

## Definition of done
- The project builds with Java 21.
- The Gradle wrapper is standard and executable.
- Architecture tests and Spring Modulith checks pass.
- Integration tests cover supported providers and invalid requests.
- Green Scooter is intentionally excluded from the baseline.
