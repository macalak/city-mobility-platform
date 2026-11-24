Recovery prompt pack

If the agent over-engineers or violates boundaries, reset to the repository rules.

- Domain contamination: remove framework or provider dependencies from the domain and restore the pure model.
- Provider switch statements: replace the conditional logic with the `RentalProviderPort` abstraction and registry.
- Speculative abstractions: remove unused abstractions and keep the contract aligned with the current need.
- Disabled or weakened tests: restore the original assertions and fix the implementation to satisfy them.
- Completion claims without verification: only claim success after executing the relevant commands.
- Scope too large: split the work into the smallest provider-boundary change, verify it, and then proceed.
