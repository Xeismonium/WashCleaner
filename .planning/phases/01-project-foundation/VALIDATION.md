---
phase: 1
slug: project-foundation
status: draft
nyquist_compliant: true
wave_0_complete: false
created: 2026-05-01
---

# Phase 1 — Validation Strategy

> Per-phase validation contract for feedback sampling during execution.

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | JUnit 4 + Robolectric |
| **Config file** | `app/build.gradle.kts` |
| **Quick run command** | `./gradlew test` |
| **Full suite command** | `./gradlew connectedCheck` |
| **Estimated runtime** | ~60 seconds |

---

## Sampling Rate

- **After every task commit:** Run `./gradlew test`
- **After every plan wave:** Run `./gradlew test`
- **Before `/gsd-verify-work`:** Full suite must be green
- **Max feedback latency:** 120 seconds

---

## Per-Task Verification Map

| Task ID | Plan | Wave | Requirement | Threat Ref | Secure Behavior | Test Type | Automated Command | File Exists | Status |
|---------|------|------|-------------|------------|-----------------|-----------|-------------------|-------------|--------|
| 01-00-01| 00 | 0 | INF-01 | — | N/A | unit | `./gradlew :data:test` | ⌛ W0 | ⬜ pending |
| 01-00-02| 00 | 0 | INF-02 | — | N/A | integration | `./gradlew :app:test` | ⌛ W0 | ⬜ pending |
| 01-01-01| 01 | 1 | D-05 | — | N/A | build | `./gradlew assembleDebug` | ✅ | ⬜ pending |
| 01-02-01| 02 | 2 | INF-01 | — | N/A | unit | `./gradlew :data:test` | ⌛ W0 | ⬜ pending |
| 01-03-01| 03 | 3 | INF-02 | — | N/A | integration | `./gradlew :app:test` | ⌛ W0 | ⬜ pending |

*Status: ⬜ pending · ✅ green · ❌ red · ⚠️ flaky*

---

## Wave 0 Requirements

- [ ] `data/src/test/java/com/washcleaner/data/RoomDatabaseTest.kt` — stubs for INF-01
- [ ] `app/src/test/java/com/washcleaner/worker/SyncWorkerTest.kt` — stubs for INF-02

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| Splash screen appearance | D-07 | Visual | Run app, verify splash appears and redirects to home. |

---

## Validation Sign-Off

- [x] All tasks have `<automated>` verify or Wave 0 dependencies
- [x] Sampling continuity: no 3 consecutive tasks without automated verify
- [x] Wave 0 covers all MISSING references
- [x] No watch-mode flags
- [x] Feedback latency < 120s
- [x] `nyquist_compliant: true` set in frontmatter

**Approval:** pending 2026-05-01
