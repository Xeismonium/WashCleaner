# Roadmap: WashCleaner

## Phases

- [ ] **Phase 1: Project Foundation** - Establish tech stack, DI, and offline-first infrastructure.
- [ ] **Phase 2: Authentication & Security** - Role-based access and session management.
- [ ] **Phase 3: Store & Staff Settings** - Configuration for rates, store info, and accounts.
- [ ] **Phase 4: Customer Management** - Profiles, search, and history tracking.
- [ ] **Phase 5: Order Processing** - Lifecycle management from receipt to completion.
- [ ] **Phase 6: Cashier & Receipts** - Payments, balance tracking, and WhatsApp sharing.
- [ ] **Phase 7: Analytics & Reports** - Financial visualization and summaries.
- [ ] **Phase 8: Infrastructure Enhancement** - Pickup reminders and final polish.

## Phase Details

### Phase 1: Project Foundation
**Goal**: Establish a robust architecture and data layer.
**Depends on**: Nothing
**Requirements**: INF-01, INF-02
**Success Criteria**:
  1. App launches with a splash screen and navigates to a placeholder home.
  2. Local database (Room) schema is initialized and verifiable.
  3. Background sync worker (WorkManager) is scheduled and logs attempts.
**Plans**: 4 plans
- [ ] 01-01-PLAN.md — Setup multi-module structure and Hilt DI.
- [ ] 01-02-PLAN.md — Implement Room database and core entities.
- [ ] 01-03-PLAN.md — Setup WorkManager and placeholder SyncWorker.
- [ ] 01-04-PLAN.md — Implement Splash Screen and NavHost.

### Phase 2: Authentication & Security
**Goal**: Users can securely access their role-appropriate features.
**Depends on**: Phase 1
**Requirements**: AUTH-01, AUTH-02, AUTH-03, AUTH-04
**Success Criteria**:
  1. User can sign in with Firebase credentials.
  2. Staff cannot access Owner-only settings screens.
  3. App prevents login if `isActive` flag is false in Firestore.
  4. Staff can open the app while offline and remain logged in to the current session.
**Plans**: TBD
**UI hint**: yes

### Phase 3: Store & Staff Settings
**Goal**: Owners can configure the business environment.
**Depends on**: Phase 2
**Requirements**: SET-01, SET-02, SET-03
**Success Criteria**:
  1. Owner can set laundry rates (e.g., Price per KG for "Kiloan").
  2. Store name and phone appear correctly in the profile settings.
  3. Owner can add a new staff account and toggle their active status.
**Plans**: TBD
**UI hint**: yes

### Phase 4: Customer Management
**Goal**: Maintain a searchable database of customers.
**Depends on**: Phase 3
**Requirements**: CUST-01, CUST-02, CUST-03
**Success Criteria**:
  1. User can add a new customer with name and phone number.
  2. User can find a customer instantly by searching their name or phone.
  3. User can view a list of previous orders for a specific customer.
**Plans**: TBD
**UI hint**: yes

### Phase 5: Order Processing
**Goal**: Track the lifecycle of laundry from receipt to completion.
**Depends on**: Phase 4
**Requirements**: ORD-01, ORD-02, ORD-03, ORD-04, ORD-05
**Success Criteria**:
  1. User can create an order for a customer (Weight-based or Item-based).
  2. Unique order code (WC-YYYYMMDD-XXX) is generated upon creation.
  3. Order status can be transitioned from "Received" through "Done".
  4. App automatically suggests a finish date based on store defaults.
**Plans**: TBD
**UI hint**: yes

### Phase 6: Cashier & Receipts
**Goal**: Record financial transactions and provide digital proof.
**Depends on**: Phase 5
**Requirements**: PAY-01, PAY-02, PAY-03, PAY-04
**Success Criteria**:
  1. User can mark an order as Paid, Unpaid, or Down Payment.
  2. Remaining balance is correctly calculated and shown for partial payments.
  3. Tapping "Share Receipt" opens WhatsApp with a summary of the order and payment.
**Plans**: TBD
**UI hint**: yes

### Phase 7: Analytics & Reports
**Goal**: Owner gains insights into business performance.
**Depends on**: Phase 6
**Requirements**: REP-01, REP-02, REP-03
**Success Criteria**:
  1. Owner sees a bar chart showing daily revenue trends for the current month.
  2. Total revenue for the day/week/month is displayed in summary cards.
  3. A "Debt List" shows all orders that have an outstanding balance.
**Plans**: TBD
**UI hint**: yes

### Phase 8: Infrastructure Enhancement
**Goal**: Improve operational reliability with notifications.
**Depends on**: Phase 7
**Requirements**: INF-03
**Success Criteria**:
  1. Device shows a local notification for orders marked "Ready" for more than 3 days.
**Plans**: TBD

## Progress Table

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. Project Foundation | 0/4 | Not started | - |
| 2. Authentication & Security | 0/0 | Not started | - |
| 3. Store & Staff Settings | 0/0 | Not started | - |
| 4. Customer Management | 0/0 | Not started | - |
| 5. Order Processing | 0/0 | Not started | - |
| 6. Cashier & Receipts | 0/0 | Not started | - |
| 7. Analytics & Reports | 0/0 | Not started | - |
| 8. Infrastructure Enhancement | 0/0 | Not started | - |
