# Phase 1: Project Foundation - Context

**Gathered:** 1 May 2026
**Status:** Ready for planning

<domain>
## Phase Boundary

Establish the foundational architecture and infrastructure for WashCleaner. This includes setting up Hilt for Dependency Injection (Multi-module), Room for the local data layer (Monolithic), and WorkManager for background synchronization with Firestore. The goal is to have a functional "Hello World" app that compiles with these core components integrated.

</domain>

<decisions>
## Implementation Decisions

### Room Database Strategy
- **D-01:** Monolithic Database approach — all entities (Orders, Customers, Services, Settings) reside in a single Room database to simplify relationships and management.
- **D-02:** Auto-destructive migrations — During v1 development, the database will be recreated on schema changes to speed up iteration (preserve data manually if needed).

### Sync Infrastructure (WorkManager/Firestore)
- **D-03:** Immediate + Background Retry — Sync to Firestore will be triggered immediately after local Room writes, with WorkManager handling retries and offline queuing.
- **D-04:** Batch/Queued Sync — The sync worker will handle batches of pending changes rather than spawning a separate worker for every single update.

### Clean Architecture Setup (Hilt)
- **D-05:** Multi-module Architecture — The project will be split into Gradle modules (e.g., `:core`, `:data`, `:domain`, `:ui`) to improve build times and enforce boundaries, despite being a v1.
- **D-06:** UseCase-driven separation — ViewModels will interact with domain UseCases rather than calling Repositories directly, ensuring strict Clean Architecture enforcement.

### UI/UX Foundation (Compose)
- **D-07:** Standard Navigation Compose — Even with multi-module, we will use the standard Jetpack Navigation Compose library within the main app module's NavHost.
- **D-08:** Custom Brand Theme — The Material 3 theme will be customized with a specific color palette reflecting the "WashCleaner" brand (e.g., cleaning-related blues/teals/whites).

### Claude's Discretion
- Exact module naming and internal package structure.
- Specific implementation of the `SyncWorker`.
- Selection of brand-specific color hex codes (within the Laundry/Cleaning theme).

</decisions>

<specifics>
## Specific Ideas
- Use dynamic color as a fallback, but primary theme should be the custom brand theme.
- Ensure the sync logic includes a "Last Updated" timestamp check to prevent overwriting newer data.

</specifics>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Requirements & Roadmap
- `.planning/PROJECT.md` — Core vision and context.
- `.planning/REQUIREMENTS.md` — v1 requirement list (INF-01, INF-02 targeted here).
- `.planning/ROADMAP.md` — Phase 1 goals and success criteria.

### Research
- `.planning/research/STACK.md` — Tech stack recommendations (Kotlin 2.1, Compose BOM 2026.03.00, etc.).
- `.planning/research/ARCHITECTURE.md` — Recommended Clean Architecture and sync patterns.
- `.planning/research/PITFALLS.md` — Sync conflict and role-based security warnings.

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- `ui/theme/` — Basic Theme.kt, Color.kt, and Type.kt already exist but will be customized for the brand theme.

### Established Patterns
- Material 3 and Compose are already enabled in `app/build.gradle.kts`.

### Integration Points
- `MainActivity.kt` — Entry point where NavHost and Hilt entry point will be established.
- `WashCleanerApp.kt` — Application class (to be created) for Hilt initialization.

</code_context>

<deferred>
## Deferred Ideas
- Expense tracking — v2 requirement.
- QR/Barcode tagging — v2 requirement.
- Priority/Express logic — v2 requirement.
- Bluetooth thermal printer support — v2 requirement.

</deferred>

---

*Phase: 01-project-foundation*
*Context gathered: 1 May 2026*