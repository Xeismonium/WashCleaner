# Phase 1: Project Foundation - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-05-01
**Phase:** 1-Project Foundation
**Areas discussed:** Room Database Strategy, Sync Infrastructure (WorkManager/Firestore), Clean Architecture Setup (Hilt), UI/UX Foundation (Compose)

---

## Room Database Strategy

| Option | Description | Selected |
|--------|-------------|----------|
| Monolithic Database | Single database with all entities. Easier to manage relations and shared data. | ✓ |
| Modular/Split Databases | Separate databases for Orders, Customers, and Settings. Better isolation, more complex. | |

**User's choice:** Monolithic Database (Recommended)
**Notes:** User also chose Auto-destructive migrations for development efficiency in v1.

---

## Sync Infrastructure (WorkManager/Firestore)

| Option | Description | Selected |
|--------|-------------|----------|
| Immediate + Background Retry | Sync starts immediately after a local change, with background retry. Fast feedback. | ✓ |
| Periodic/Scheduled Sync | Wait for periodic background job (e.g., every 15 min). Better battery life. | |

**User's choice:** Immediate + Background Retry (Recommended)
**Notes:** User also selected Batch/Queued Sync for better efficiency in background workers.

---

## Clean Architecture Setup (Hilt)

| Option | Description | Selected |
|--------|-------------|----------|
| Package-based | Keep all code in the 'app' module using package-based separation. Simpler for v1. | |
| Multi-module Architecture | Split code into multiple Gradle modules (e.g., :data, :domain, :ui). Better build times, higher complexity. | ✓ |

**User's choice:** Multi-module Architecture
**Notes:** User opted for UseCase-driven separation to enforce strict Clean Architecture.

---

## UI/UX Foundation (Compose)

| Option | Description | Selected |
|--------|-------------|----------|
| Navigation Compose (Standard) | Use Compose Navigation with 'app' module NavHost. Standard approach. | ✓ |
| Decoupled Navigation Module | Implement a more complex multi-module navigation strategy. | |

**User's choice:** Navigation Compose (Standard)
**Notes:** User also decided on a Custom Brand Theme (Cleaning/Laundry theme) over standard Material 3.

---

## Claude's Discretion

- Exact module naming and internal package structure.
- Specific implementation of the `SyncWorker`.
- Selection of brand-specific color hex codes.

## Deferred Ideas

- Expense tracking, QR/Barcode tagging, Priority logic, and Printer support (v2).
