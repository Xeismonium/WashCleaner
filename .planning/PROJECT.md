# WashCleaner

## What This Is
WashCleaner is a laundry management application designed for a single laundry outlet owner with role-based multi-user support (Owner and Staff). It streamlines order processing, customer database management, and financial reporting.

## Core Value
The single most important value is providing an efficient and reliable way to track laundry orders from receipt to delivery, ensuring accurate status updates and payment records.

## Current State
- **v1.0 - Project Foundation** (Shipped 1 May 2026): Core architecture, dependency management, and package skeleton established.
- **v1.1 - Data & Domain Layer** (Shipped 1 May 2026): Complete Domain models, Repositories, Room database, and Firestore synchronization.

## Active Milestone: [TBD]
- (In planning)

## Requirements
- [x] Milestone: v1.0 - Project Foundation
    - [x] Set up complete project foundation (Gradle, Hilt, Firebase, Room, etc.)
    - [x] Establish full package hierarchy with placeholder files.
    - [x] Implement base theme and single entry point (MainActivity).
- [x] Milestone: v1.1 - Data & Domain Layer
    - [x] Implement Domain models (User, Order, Customer, Service, StoreSettings).
    - [x] Implement Repository interfaces and implementations.
    - [x] Setup Room entities, DAOs, and database.
    - [x] Implement Firestore data source and sync logic.
    - [x] Configure Hilt DI modules for data and domain layers.
- [ ] Role-based access control (Owner vs Staff)
...
- [ ] Order management (CRUD, status tracking, filtering)
- [ ] Customer database (CRUD, search, history)
- [ ] Cashier and Payment (Payment tracking, WhatsApp receipts)
- [ ] Financial Reports (Daily/Weekly/Monthly, revenue tracking)
- [ ] Store and Staff management
- [ ] Offline-first capability with Room + Firestore sync

### Out of Scope
- [ ] Payment Gateway Integration — Manual recording and WhatsApp receipts are sufficient for v1.
- [ ] Multi-outlet support — Initial version is focused on a single outlet.

## Context
- Android Application using Kotlin and Jetpack Compose.
- MVVM + Clean Architecture.
- Firebase for Auth and Firestore.
- Room for local caching.

## Constraints
- **Tech Stack**: Jetpack Compose, Hilt, Firebase, Room, Vico, WorkManager.
- **Platform**: Android Min SDK 30, Target SDK 36.
- **Geography**: Indonesia (Rupiah currency, WhatsApp-based receipts).

## Key Decisions
| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Jetpack Compose | Modern UI framework for faster development and Material 3 support. | — Pending |
| MVVM + Clean Architecture | Ensures maintainability and scalability of the app. | — Pending |
| Offline-first (Room + Firestore) | Reliable performance in areas with unstable internet. | — Pending |
| Manual Payment Recording | Simplifies v1 by avoiding complex payment gateway integrations. | — Pending |

---
*Last updated: 1 May 2026 after initialization*