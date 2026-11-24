Run a targeted test-and-fix loop before broader validation.

Examples:
- domain tests
- application service tests
- provider adapter tests
- REST integration tests
- architecture rules

If a failing test is caused by boundary drift, fix the design rather than weakening the tests. Never claim a test passed without executing it.
