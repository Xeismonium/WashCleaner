# Roadmap: WashCleaner

## Phases

- [x] **Phase 1: Project Foundation** - Complete tech stack setup, DI, theme, and package hierarchy.
- [ ] **Phase 2: Authentication & Core Settings** - Role-based access and store/staff configuration.
- [ ] **Phase 3: Customer & Order Management** - Core business logic for customers and order lifecycles.
- [ ] **Phase 4: Cashier & Payments** - Financial recording and WhatsApp receipts.
- [ ] **Phase 5: Analytics & Production Polish** - Reporting visualizations and final infrastructure enhancements.

## Phase Details

### Phase 1: Project Foundation
**Goal**: Establish a robust architecture, dependency management, and project skeleton.
**Depends on**: Nothing
**Requirements**: INF-04, INF-05, INF-06, INF-07
**Success Criteria**:
  1. Project compiles successfully with all dependencies in Version Catalog.
  2. Hilt is correctly initialized and MainActivity launches with a Material 3 theme.
  3. Status and Payment color constants are defined and used in the theme.
  4. Complete package hierarchy from core to ui is established with placeholder files.
**Plans**:
- [x] 01-01-PLAN.md — Gradle & Dependency Setup
- [x] 01-02-PLAN.md — UI Foundation & Application
- [x] 01-03-PLAN.md — Project Skeleton

### Phase 2: Authentication & Core Settings
**Goal**: Secure access and basic business configuration.
**Depends on**: Phase 1
**Requirements**: AUTH-01, AUTH-02, AUTH-03, AUTH-04, SET-01, SET-02, SET-03
**Success Criteria**:
  1. User can sign in via Firebase.
  2. Owner can configure service rates and manage staff accounts.
**Plans**:
- [ ] 02-01-PLAN.md — Authentication & Session Management
- [ ] 02-02-PLAN.md — Core Settings & Rates
- [ ] 02-03-PLAN.md — Security, Navigation & UI Masking

### Phase 3: Customer & Order Management
**Goal**: Track customers and the lifecycle of laundry orders.
**Depends on**: Phase 2
**Requirements**: CUST-01, CUST-02, CUST-03, ORD-01, ORD-02, ORD-03, ORD-04, ORD-05
**Success Criteria**:
  1. Orders can be created with auto-generated codes.
  2. Order status can be tracked from "Received" to "Done".
  3. Customer history is accessible and searchable.
**Plans**: TBD

### Phase 4: Cashier & Payments
**Goal**: Financial recording and customer communication.
**Depends on**: Phase 3
**Requirements**: PAY-01, PAY-02, PAY-03, PAY-04
**Success Criteria**:
  1. Partial and full payments are recorded accurately.
  2. Receipts are shared via WhatsApp with correct order details.
**Plans**: TBD

### Phase 5: Analytics & Production Polish
**Goal**: Business insights and operational reliability.
**Depends on**: Phase 4
**Requirements**: REP-01, REP-02, REP-03, INF-01, INF-02, INF-03
**Success Criteria**:
  1. Revenue trends are visualized with charts.
  2. Offline data syncs reliably with Firestore.
  3. Pickup reminders are triggered for overdue orders.
**Plans**: TBD

## Progress Table

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. Project Foundation | 3/3 | Completed | 1 May 2026 |
| 2. Authentication & Core Settings | 0/3 | In Progress | - |
| 3. Customer & Order Management | 0/0 | Not started | - |
| 4. Cashier & Payments | 0/0 | Not started | - |
| 5. Analytics & Production Polish | 0/0 | Not started | - |
