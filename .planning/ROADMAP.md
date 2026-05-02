# Roadmap: WashCleaner

- [v1.0 - Project Foundation](milestones/v1.0-ROADMAP.md) (Completed 1 May 2026)
- [v1.1 - Data & Domain Layer](milestones/v1.1-ROADMAP.md) (Completed 1 May 2026)

## Milestone v1.2: Auth, Navigation & Core UI Components

- [ ] **Phase 3: Authentication & Core UI** - Implement Auth screens, NavGraph, and shared UI components.

## Phase Details

### Phase 3: Authentication & Core UI
**Goal**: Implement Authentication screens, Navigation Compose graph, and shared UI components.
**Depends on**: Phase 2 (Archived)
**Requirements**: ARCH-01..04, NAV-01..03, AUTH-UI-01..03, MAIN-UI-01..02, COMP-01..03
**Success Criteria**:
  1. `NavGraph` successfully navigates based on authentication and role state.
  2. `SplashScreen` and `LoginScreen` handle user authentication properly with Firestore `isActive` checks.
  3. `MainScreen` displays BottomNavigation with correct tabs based on user role.
  4. All screen files adhere strictly to the "EXACTLY 3 FUNCTIONS" rule.
  5. Core UI components are functional.
**Plans:** 3 plans
- [ ] 03-01-PLAN.md — Navigation Foundation & Core Components
- [x] 03-02-PLAN.md — Authentication Flow
- [ ] 03-03-PLAN.md — Main Scaffold & App Entry Point

### Phase 4: Customer & Order Management
**Goal**: Track customers and the lifecycle of laundry orders.
**Depends on**: Phase 3
**Success Criteria**:
  1. Orders can be created with auto-generated codes.
  2. Order status can be tracked from "Received" to "Done".
  3. Customer history is accessible and searchable.
**Plans**: TBD

### Phase 5: Cashier & Payments
**Goal**: Financial recording and customer communication.
**Depends on**: Phase 4
**Success Criteria**:
  1. Partial and full payments are recorded accurately.
  2. Receipts are shared via WhatsApp with correct order details.
**Plans**: TBD

### Phase 6: Analytics & Production Polish
**Goal**: Business insights and operational reliability.
**Depends on**: Phase 5
**Success Criteria**:
  1. Revenue trends are visualized with charts.
  2. Offline data syncs reliably with Firestore.
  3. Pickup reminders are triggered for overdue orders.
**Plans**: TBD

## Progress Table

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 3. Authentication & Core UI | 1/3 | In Progress | - |
| 4. Customer & Order Management | 0/0 | Not started | - |
| 5. Cashier & Payments | 0/0 | Not started | - |
| 6. Analytics & Production Polish | 0/0 | Not started | - |
