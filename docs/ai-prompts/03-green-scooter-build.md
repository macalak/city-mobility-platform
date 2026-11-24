Implement the Green Scooter support in small increments.

Keep the code constrained to the provider boundary and adapter tests. Do not create speculative abstractions. Ensure the provider-specific logic stays inside the adapter package and the application service remains provider-neutral.

Use a bounded test loop after each major step and stop if a change starts contaminating the domain layer.
