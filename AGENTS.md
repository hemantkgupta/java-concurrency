# Agent Instructions

## Project
- Java (Maven). Build with `mvn -q -e -DskipTests package` or `./build.sh` when needed.
- Tests live under `src/test/java`. Prefer `mvn -q -Dtest=ClassName test` for focused runs.

## Conventions
- Keep code and examples in Java 8+ compatible syntax unless otherwise requested.
- Prefer clear, educational explanations in docs (this is a learning repo).

## Repo Hygiene
- Do not delete or modify files under `target/` in commits (ignore generated files).
- Keep `.gitignore` up to date if new tools are introduced.
