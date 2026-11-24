# AI prompt pack

This prompt pack is designed for a live-coding exercise that adds Green Scooter support while preserving the hexagonal architecture of the rental bounded context.

## Prerequisites
- Read `AGENTS.md` and `SETUP-AGENT.md` before editing.
- Keep the domain pure and the application ports explicit.
- Run the smallest relevant test slice after each change.

## Recommended workflow
1. Start with the kickoff prompt.
2. Explore the existing rental flow.
3. Plan changes without editing the supported provider flow.
4. Implement a small increment.
5. Verify the bounded test loop.
6. Review the architecture with the architecture review prompt.
7. Polish the conference demo.

## Expected Green Scooter additions
A live-coding agent is expected to add provider-specific integration files for Green Scooter, while leaving the core domain, application service, registry, and controller intact.

> Vibe coding does not mean architecture-free coding. Explicit architectural boundaries make AI-generated changes safer and easier to review.

## Review gates
- Domain remains free from adapters and provider SDKs.
- Application service and domain remain unchanged by provider additions.
- The new provider is isolated behind the `RentalProviderPort` abstraction.
- Tests remain honest and executable.
