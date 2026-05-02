<!-- GSD:project-start source:PROJECT.md -->
## Project

**WashCleaner**

WashCleaner is a laundry management application designed for a single laundry outlet owner with role-based multi-user support (Owner and Staff). It streamlines order processing, customer database management, and financial reporting.

**Core Value:** The single most important value is providing an efficient and reliable way to track laundry orders from receipt to delivery, ensuring accurate status updates and payment records.

### Constraints

- **Tech Stack**: Jetpack Compose, Hilt, Firebase, Room, Vico, WorkManager.
- **Platform**: Android Min SDK 30, Target SDK 36.
- **Geography**: Indonesia (Rupiah currency, WhatsApp-based receipts).
<!-- GSD:project-end -->

<!-- GSD:stack-start source:research/STACK.md -->
## Technology Stack

## Recommended Stack
### Core Framework
| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| Kotlin | 2.1.0 | Language | Standard for Android 16 (API 36). K2 compiler provides significant build performance improvements. |
| Jetpack Compose BOM | 2026.03.00 | UI Framework | Latest stable Bill of Materials for UI consistency and Material 3 support. |
| Android SDK | Target 36 | Platform | Aligns with Android 16 requirements, including mandatory Edge-to-Edge and 16KB page size support. |
| Gradle | 8.12 | Build Tool | Latest stable version for 2026, optimized for Kotlin 2.1. |
### Database & Storage
| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| Room | 2.7.0 | Local Database | Supports KMP and provides a robust offline-first source of truth. |
| Cloud Firestore | BOM 34.12.0 | Remote DB | Real-time sync, flexible schema for laundry orders, and excellent offline persistence. |
| DataStore | 1.1.1 | Preferences | Replacement for SharedPreferences for user settings and role-based state. |
### Infrastructure & Backend
| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| Firebase Auth | BOM 34.12.0 | Authentication | Simple implementation for Owner/Staff login. |
| Firebase Cloud Messaging | BOM 34.12.0 | Notifications | Status updates and reminders (optional for v1). |
| WorkManager | 2.11.0 | Background Sync | Reliable sync between Room and Firestore in unstable network conditions. |
### Supporting Libraries
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| Hilt | 2.59.2 | Dependency Injection | Standard for Android; simplifies dependency management across Clean Architecture layers. |
| Vico | 3.1.0 | Charts/Reporting | Powerful and extensible charting for financial revenue tracking. |
| Coil | 3.0.0 | Image Loading | Standard image loader for Compose; useful if adding photo proof for items. |
| Navigation Compose | 2.8.x+ | Navigation | Type-safe navigation (since 2.8.0) is the modern standard for Compose apps. |
| Kotlinx Serialization | 1.7.x | Serialization | Modern alternative to Gson/Moshi, fully compatible with Kotlin 2.1. |
## Alternatives Considered
| Category | Recommended | Alternative | Why Not |
|----------|-------------|-------------|---------|
| Database | Room + Firestore | Realm / PowerSync | Room is official; Firestore sync is easy for v1. PowerSync is powerful but adds external dependency complexity. |
| DI | Hilt | Koin | Hilt's compile-time safety and standard Android integration are preferred for enterprise-grade apps. |
| Charts | Vico | MPAndroidChart | MPAndroidChart is legacy; Vico is built specifically for Compose. |
## Installation
## Sources
- [Android 16 Release Notes](https://developer.android.com/about/versions/16)
- [Context7: Jetpack Compose BOM 2026.03.00]
- [Context7: Hilt 2.59.2]
- [Context7: Firebase 34.12.0]
- [Vico Documentation](https://github.com/patrykandpatrick/vico)
<!-- GSD:stack-end -->

<!-- GSD:conventions-start source:CONVENTIONS.md -->
## Conventions

Conventions not yet established. Will populate as patterns emerge during development.
<!-- GSD:conventions-end -->

<!-- GSD:architecture-start source:ARCHITECTURE.md -->
## Architecture

Architecture not yet mapped. Follow existing patterns found in the codebase.
<!-- GSD:architecture-end -->

<!-- GSD:skills-start source:skills/ -->
## Project Skills

No project skills found. Add skills to any of: `.claude/skills/`, `.agents/skills/`, `.cursor/skills/`, `.github/skills/`, or `.codex/skills/` with a `SKILL.md` index file.
<!-- GSD:skills-end -->

<!-- GSD:workflow-start source:GSD defaults -->
## GSD Workflow Enforcement

Before using Edit, Write, or other file-changing tools, start work through a GSD command so planning artifacts and execution context stay in sync.

Use these entry points:
- `/gsd-quick` for small fixes, doc updates, and ad-hoc tasks
- `/gsd-debug` for investigation and bug fixing
- `/gsd-execute-phase` for planned phase work

Do not make direct repo edits outside a GSD workflow unless the user explicitly asks to bypass it.
<!-- GSD:workflow-end -->



<!-- GSD:profile-start -->
## Developer Profile

> Profile not yet configured. Run `/gsd-profile-user` to generate your developer profile.
> This section is managed by `generate-claude-profile` -- do not edit manually.
<!-- GSD:profile-end -->
