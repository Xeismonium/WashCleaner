# Phase 4: Feature Implementation - Validation

**Status:** Implementation Ready
**Target Milestone:** v1.3

## Phase Goal
Implement the complete functional UI for all business features, ensuring strict adherence to the 3-function architectural rule and robust integration with the data layer.

## Validation Scenarios

### 1. Order Management Lifecycle (ORD-UI)
- **Scenario:** Create a new order, search for it in the list, and update its status.
- **Expectation:** The order is created with a sequential code, appears in the list (optionally filtered), and the detail screen shows the correct status in the stepper.
- **Verification:** UI automated test or manual E2E check.

### 2. Customer Management (CUST-UI)
- **Scenario:** Add a new customer and edit their details.
- **Expectation:** Form validation prevents invalid phone numbers. Details are persisted and correctly displayed in the list and detail screens.
- **Verification:** Unit test for `CustomerViewModel` validation logic.

### 3. Payment & Receipt (PAY-UI)
- **Scenario:** Process a "Down Payment" and share the receipt.
- **Expectation:** The balance is calculated correctly. Clicking share opens an external intent with a pre-formatted WhatsApp message containing order details.
- **Verification:** Manual verification of Intent generation.

### 4. Role-Based Reporting (REP-UI, SET-UI)
- **Scenario:** Access Reports and Staff Management as a `staff` user.
- **Expectation:** These features are hidden from the UI (NavigationBar and Settings buttons).
- **Verification:** UI test mocking `UserRole.STAFF`.

### 5. Architectural Compliance (ARCH-05)
- **Scenario:** Inspecting screen files.
- **Expectation:** Every file has exactly 3 functions (`Screen`, `Content`, `Preview`). Helper components are in `components/` subfolders.
- **Verification:** Automated structural code check.

## Automated Verification Gates

| Command | Objective |
|---------|-----------|
| `./gradlew compileDebugKotlin` | Verify all feature screens and ViewModels compile |
| `./gradlew test` | Run all ViewModel unit tests (state transitions, validation) |
| `./gradlew assembleDebug` | Final full build check |

---
*Phase: 04-feature-implementation*
*Validation defined: 1 May 2026*