# Roadmap: WashCleaner

- [v1.0 - Project Foundation](milestones/v1.0-ROADMAP.md) (Completed 1 May 2026)
- [v1.1 - Data & Domain Layer](milestones/v1.1-ROADMAP.md) (Completed 1 May 2026)
- [v1.2 - Auth, Navigation & Core UI Components](milestones/v1.2-ROADMAP.md) (Completed 1 May 2026)

## Milestone v1.3: Feature Screens

- [ ] **Phase 4: Feature Implementation** - Implement all business feature screens and ViewModels.

## Phase Details

### Phase 4: Feature Implementation
**Goal**: Implement the complete UI for Orders, Customers, Payments, Reports, and Settings.
**Depends on**: Phase 3 (Archived)
**Requirements**: ARCH-05..08, ORD-UI-*, CUST-UI-*, PAY-UI-*, REP-UI-*, SET-UI-*
**Success Criteria**:
  1. All feature screens are functional and strictly follow the 3-function architectural rule.
  2. Data is correctly displayed from Room/Firestore via ViewModels and UseCases.
  3. Form validation is implemented for customer creation and order processing.
  4. Role-based visibility is respected for Reports and Staff Management.
  5. WhatsApp receipts are generated and shared correctly.
**Plans**: 5 plans
- [ ] 04-01-PLAN.md — Order Management (ORD-UI)
- [ ] 04-02-PLAN.md — Customer Management (CUST-UI)
- [ ] 04-03-PLAN.md — Payment & Receipting (PAY-UI)
- [ ] 04-04-PLAN.md — Financial Reports (REP-UI)
- [ ] 04-05-PLAN.md — Settings & Staff Management (SET-UI)

### Phase 5: Final Polish & Infrastructure
**Goal**: Production readiness and infrastructure enhancements.
**Depends on**: Phase 4
**Success Criteria**:
  1. Offline sync is robust and verified.
  2. Pickup reminders are functional.
  3. App is ready for release.
**Plans**: TBD

## Progress Table

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 4. Feature Implementation | 0/5 | Not started | - |
| 5. Final Polish & Infrastructure | 0/0 | Not started | - |
