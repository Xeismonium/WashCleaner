# Roadmap: WashCleaner

- [v1.0 - Project Foundation](milestones/v1.0-ROADMAP.md) (Completed 1 May 2026)

## Milestone v1.1: Data & Domain Layer

- [ ] **Phase 2: Data & Domain Layer** - Implement core business logic, persistence, and remote synchronization.

## Phase Details

### Phase 2: Data & Domain Layer
**Goal**: Implement core business logic, persistence, and remote synchronization.
**Depends on**: Phase 1 (Archived)
**Requirements**: DOM-MOD-01, DOM-REP-01, DOM-UC-01, LOC-DATA-01, REM-DATA-01, SYNC-01, INF-08, INF-09
**Success Criteria**:
  1. All Domain models and Repository interfaces are implemented.
  2. Room database is fully functional with entities and DAOs.
  3. FirestoreDataSource handles all remote operations with Result<T>.
  4. Repository implementations handle offline-first logic and sync.
  5. Hilt modules are configured for all layers.
  6. Project compiles successfully.
**Plans**: 5 plans
- [ ] 02-01-PLAN.md — Domain Layer - Models & Interfaces
- [ ] 02-02-PLAN.md — Local Data Layer - Room Setup
- [ ] 02-03-PLAN.md — Remote Data Layer & Utilities
- [ ] 02-04-PLAN.md — Repository Implementations & Session Persistence
- [ ] 02-05-PLAN.md — Use Cases & Dependency Injection

### Phase 3: Authentication & Core UI
**Goal**: Secure access and basic business configuration.
**Depends on**: Phase 2
**Requirements**: AUTH-01, AUTH-02, AUTH-03, AUTH-04, SET-01, SET-02, SET-03
**Success Criteria**:
  1. User can sign in via Firebase.
  2. Owner can configure service rates and manage staff accounts.
**Plans**: TBD

### Phase 4: Customer & Order Management
**Goal**: Track customers and the lifecycle of laundry orders.
**Depends on**: Phase 3
**Requirements**: CUST-01, CUST-02, CUST-03, ORD-01, ORD-02, ORD-03, ORD-04, ORD-05
**Success Criteria**:
  1. Orders can be created with auto-generated codes.
  2. Order status can be tracked from "Received" to "Done".
  3. Customer history is accessible and searchable.
**Plans**: TBD

### Phase 5: Cashier & Payments
**Goal**: Financial recording and customer communication.
**Depends on**: Phase 4
**Requirements**: PAY-01, PAY-02, PAY-03, PAY-04
**Success Criteria**:
  1. Partial and full payments are recorded accurately.
  2. Receipts are shared via WhatsApp with correct order details.
**Plans**: TBD

### Phase 6: Analytics & Production Polish
**Goal**: Business insights and operational reliability.
**Depends on**: Phase 5
**Requirements**: REP-01, REP-02, REP-03, INF-01, INF-02, INF-03
**Success Criteria**:
  1. Revenue trends are visualized with charts.
  2. Offline data syncs reliably with Firestore.
  3. Pickup reminders are triggered for overdue orders.
**Plans**: TBD

## Progress Table

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 2. Data & Domain Layer | 0/5 | Not started | - |
| 3. Authentication & Core UI | 0/0 | Not started | - |
| 4. Customer & Order Management | 0/0 | Not started | - |
| 5. Cashier & Payments | 0/0 | Not started | - |
| 6. Analytics & Production Polish | 0/0 | Not started | - |
