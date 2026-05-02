# Phase 4: Feature Implementation - Context

**Gathered:** 1 May 2026
**Status:** Ready for planning

<domain>
## Phase Boundary

Implement the complete set of business features for WashCleaner. This phase transitions the app from a structural skeleton to a fully functional tool. We will build five primary features: Order Management, Customer Management, Payments, Reporting, and Settings. All screens must adhere to the strict 3-function architectural rule.

</domain>

<decisions>
## Implementation Decisions

### Strict Architecture
- **D-01 (ARCH-05):** **STRICT:** Every Screen file must contain EXACTLY 3 functions: `[Name]Screen`, `[Name]Content`, and `[Name]Preview`.
- **D-02 (ARCH-06):** Component Isolation: Composable helpers for specific screens must be in `components/` sub-folders.
- **D-03 (ARCH-07):** Unified ViewModel State: Use the `UiState` sealed class pattern for all feature ViewModels.

### Feature UX Patterns
- **D-04:** Autocomplete: Order creation must use `ExposedDropdownMenuBox` for customer search (starts at 2+ chars).
- **D-05:** Price Calculation: Use `derivedStateOf` in `CreateOrderScreen` for real-time total price updates.
- **D-06:** Navigation Logic: Use `launchSingleTop = true` and `restoreState = true` for bottom navigation transitions in `MainScreen`.
- **D-07:** Receipt Integration: WhatsApp receipts will be shared using a standard `Intent.ACTION_VIEW` with a `wa.me` URL and encoded text.
- **D-08:** Reporting: Use Vico `ColumnChart` for daily/weekly/monthly revenue trends.

</decisions>

<canonical_refs>
## Canonical References
- `.planning/PROJECT.md`
- `.planning/REQUIREMENTS.md`
- `.planning/ROADMAP.md`
- `.planning/phases/04-feature-implementation/04-RESEARCH.md`

</canonical_refs>

<code_context>
## Existing Code Insights
- Data Engine (Phase 2) is complete: All Repositories, UseCases, Room DAOs, and Firestore sync are ready.
- UI Foundation (Phase 3) is complete: `Route.kt`, `NavGraph.kt`, and `MainScreen` scaffold are ready.
- Auth flow is functional.
- Placeholder files for all feature screens and ViewModels exist (from Phase 1 skeleton) and are ready to be fully implemented.

</code_context>

---

*Phase: 04-feature-implementation*
*Context gathered: 1 May 2026*