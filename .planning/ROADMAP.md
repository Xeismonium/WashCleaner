# Roadmap: WashCleaner

- [v1.0 - Project Foundation](milestones/v1.0-ROADMAP.md) (Completed 1 May 2026)
- [v1.1 - Data & Domain Layer](milestones/v1.1-ROADMAP.md) (Completed 1 May 2026)
- [v1.2 - Auth, Navigation & Core UI Components](milestones/v1.2-ROADMAP.md) (Completed 1 May 2026)
- [v1.3 - Feature Screens](milestones/v1.3-ROADMAP.md) (Completed 1 May 2026)
- [v1.4 - Hardening & Quality](milestones/v1.4-ROADMAP.md) (Completed 1 May 2026)

## Milestone v1.5: Auth Implementation

- [ ] **Phase 6: Auth Implementation** - Connect navigation and finalize auth features.

## Phase Details

### Phase 6: Auth Implementation
**Goal**: Replace placeholders with real screens and implement full authentication features (Registration, Forgot Password, RBAC).
**Depends on**: Phase 5 (Hardening & Quality)
**Requirements**: NAV-01..03, AUTH-01..04, USER-01
**Success Criteria**:
  1. `NavGraph` uses real screens instead of placeholders.
  2. `SplashScreen` correctly directs users based on auth state.
  3. Users can register and reset their passwords.
  4. Access to Reports and Staff Management is restricted to Owner role.
  5. Session persists across app restarts.
**Plans**:
- [ ] 06-01-PLAN.md — Navigation & Splash Logic
- [ ] 06-02-PLAN.md — Registration & Password Reset
- [ ] 06-03-PLAN.md — Role-Based Access & Staff Management

## Progress Table

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 5. Hardening & Quality | 3/3 | Completed | 1 May 2026 |
| 6. Auth Implementation | 0/3 | Active | - |
