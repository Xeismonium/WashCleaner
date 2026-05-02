# Milestone v1.2 Requirements: Auth, Navigation & Core UI Components

## Architectural Rules (ARCH)
- **ARCH-01**: Every Screen file must contain EXACTLY 3 functions: `[Name]Screen`, `[Name]Content`, and `[Name]Preview`.
- **ARCH-02**: `[Name]Screen` handles injection (`hiltViewModel`), side effects, and navigation. No layout code.
- **ARCH-03**: `[Name]Content` receives only state and callbacks. No ViewModel, NavController, or Coroutines.
- **ARCH-04**: `[Name]Preview` calls `Content` with dummy data.

## Navigation (NAV)
- **NAV-01**: `Route.kt` containing all destination routes and helper functions for path building.
- **NAV-02**: `NavGraph.kt` with `SplashScreen` as start destination and role-based routing (e.g., `REPORT` requires "owner" role).
- **NAV-03**: `MainActivity.kt` updated to collect role from `SessionManager` as State and host `NavGraph` with `SnackbarHostState`.

## Authentication Flow (AUTH-UI)
- **AUTH-UI-01**: `AuthViewModel` with `UiState`, handling `login()`, `logout()`, `checkLoginStatus()`, and checking `isActive` flag.
- **AUTH-UI-02**: `SplashScreen` with auto-login navigation or routing to `LOGIN`.
- **AUTH-UI-03**: `LoginScreen` with email/password inputs, loading state, inline error messages, and snackbar integration.

## Main Scaffold (MAIN-UI)
- **MAIN-UI-01**: `MainScreen` hosting bottom navigation destinations.
- **MAIN-UI-02**: NavigationBar with "Orders", "Customers", "Reports" (owner only), "Settings".

## Core Components (COMP)
- **COMP-01**: `EmptyState` composable (icon, title, description, optional action).
- **COMP-02**: `ConfirmDialog` composable (Material 3 AlertDialog with destructive action support).
- **COMP-03**: `ShimmerEffect` composables (`ShimmerBox`, `ShimmerOrderCard`, `ShimmerCustomerCard`) using `rememberInfiniteTransition()`.

---

## Traceability

| Requirement | Phase | Status |
|-------------|-------|--------|
| ARCH-01..04 | Phase 3 | In Progress |
| NAV-01..03  | Phase 3 | Pending |
| AUTH-UI-01..03 | Phase 3 | Completed |
| MAIN-UI-01..02 | Phase 3 | Pending |
| COMP-01..03 | Phase 3 | Pending |
