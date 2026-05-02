# Phase 2: Data & Domain Layer - Context

**Gathered:** 1 May 2026
**Status:** Ready for planning

<domain>
## Phase Boundary

Implement the complete Data and Domain layers for WashCleaner. This includes Domain models, Repository interfaces, Use Cases, Room entities/DAOs, Firestore data source, and Repository implementations. The goal is to have a fully functional "engine" for the app that handles CRUD, sync, and business logic before any UI is built.

</domain>

<decisions>
## Implementation Decisions

### Data Layer
- **D-01:** Offline-first strategy — Read from Room (Flow), write to Room first, then sync to Firestore.
- **D-02:** Firestore Snapshot Listeners — Use listeners in repositories to keep Room updated with remote changes.
- **D-03:** Result<T> for all operations — Use a consistent `Result` wrapper for repository and data source functions.
- **D-04:** Server Timestamps — Include `createdAt` and `updatedAt` in all Firestore writes.

### Domain Layer
- **D-05:** Pure Kotlin models — Data classes in `domain/model` must not have any Android or Firebase imports.
- **D-06:** UseCase separation — Business logic (validations, aggregations) lives in UseCases.

### Infrastructure
- **D-07:** DataStore Preferences — Use for session management (UID, name, role).
- **D-08:** Hilt DI — Modules for AppModule, Firebase, Database, and Repositories.

</decisions>

<canonical_refs>
## Canonical References
- `.planning/PROJECT.md`
- `.planning/REQUIREMENTS.md`
- `.planning/ROADMAP.md`
- `.planning/research/ARCHITECTURE.md`
- `.planning/research/STACK.md`

</canonical_refs>

<code_context>
## Existing Code Insights
- Foundation is established (Phase 1): Gradle, Hilt, Firebase, Room, and package skeleton exist.
- MainActivity and WashCleanerApp are set up.
- Placeholder files exist for all major components and need implementation.

</code_context>

---

*Phase: 02-data-domain-layer*
*Context gathered: 1 May 2026*