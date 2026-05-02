# Phase 1: Project Foundation - Research

**Researched:** May 1, 2026
**Domain:** Android Architecture, Multi-module Hilt, Room, WorkManager, Material 3
**Confidence:** HIGH

## Summary

Phase 1 establishes the structural skeleton of the WashCleaner application. Research confirms that a **multi-module Gradle setup** using **Hilt** provides the best balance of build performance and architectural separation for a long-lived project [VERIFIED: developer.android.com]. The **monolithic Room database** approach simplifies local data management while the **WorkManager + Firestore sync** ensures reliable offline-first operation. The UI foundation uses **Material 3** with a custom brand theme to establish a clean, professional identity.

**Primary recommendation:** Use a standard multi-module structure (:core, :data, :domain, :ui) where `:domain` defines the repository interfaces and `:data` provides the implementations via Hilt, ensuring the UI layer only interacts with the Domain layer.

## Architectural Responsibility Map

| Capability | Primary Tier | Secondary Tier | Rationale |
|------------|-------------|----------------|-----------|
| Local Persistence | Database (Room) | — | Single source of truth for the UI. |
| Remote Sync | API (Firestore) | WorkManager | Background task to keep remote data consistent. |
| Business Logic | Domain (UseCases) | — | Pure Kotlin logic isolating laundry rules. |
| UI Rendering | Browser/Client (Compose) | — | Reactive UI based on Room data flows. |
| Authentication | Frontend Server (Firebase Auth) | — | Managed auth service. |

## Standard Stack

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Hilt | 2.59.2 | Dependency Injection | Industry standard for Android; compile-time safety [VERIFIED: dagger.dev]. |
| Room | 2.7.0 | Local Database | Official Jetpack library for SQLite abstraction [CITED: developer.android.com]. |
| WorkManager | 2.11.0 | Background Sync | Guarantees execution of sync tasks even if app is closed [CITED: developer.android.com]. |
| Material 3 | 1.3.1 | UI Components | Latest Material design guidelines with dynamic color support [VERIFIED: m3.material.io]. |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|--------------|
| Firestore | 34.12.0 (BOM) | Remote Storage | Real-time updates and seamless offline persistence [CITED: firebase.google.com]. |
| Kotlin Coroutines | 1.10.1 | Asynchrony | Non-blocking database and network operations. |

**Installation:**
```kotlin
// Multi-module setup requires Hilt in all modules
plugins {
    id("com.google.dagger.hilt.android") apply false
}

// In :data, :ui, :domain
dependencies {
    implementation(libs.google.hilt.android)
    ksp(libs.google.hilt.compiler)
}
```

## Architecture Patterns

### Recommended Project Structure
```
washcleaner/
├── app/              # Main entry point, NavHost, Hilt Application
├── core/             # Shared utils, Base classes, Common UI components
├── data/             # Room DB, DAOs, Repositories implementations, Firestore
├── domain/           # UseCases, Repository interfaces, Domain models (Entities)
└── ui/               # Compose screens, ViewModels, Theme
```

### Pattern 1: Multi-module Hilt Injection
**What:** Use `@Binds` in a Hilt Module within the `:data` layer to provide implementation for interfaces defined in `:domain`.
**When to use:** To maintain strict separation where `:domain` has no knowledge of `:data`.
**Example:**
```kotlin
// domain/src/main/.../OrderRepository.kt
interface OrderRepository {
    fun getOrders(): Flow<List<Order>>
}

// data/src/main/.../OrderRepositoryImpl.kt
class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao,
    private val firestore: FirebaseFirestore
) : OrderRepository { ... }

// data/src/main/.../RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository
}
```

### Pattern 2: Immediate Batch Sync
**What:** Triggering a `UniqueWorkRequest` with `ExistingWorkPolicy.KEEP` immediately after a Room write.
**When to use:** To ensure data reaches Firestore as soon as possible without creating redundant worker instances.
**Example:**
```kotlin
// data/src/main/.../SyncWorker.kt
class SyncWorker(...) : CoroutineWorker(...) {
    override suspend fun doWork(): Result {
        val pendingOrders = orderDao.getUnsyncedOrders()
        // Batch upload to Firestore
        return Result.success()
    }
}
```

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Offline Queuing | Custom SQLite queue | WorkManager | Handles network constraints and OS-level retries automatically. |
| DI Lifecycle | Manual Factory/ServiceLoc | Hilt | Scoped components (Singleton, Activity, ViewModel) are error-prone to manage manually. |
| Sync Conflicts | Manual timestamping | Firestore + Sync Flag | Firestore handles field-level merges; simple sync flags prevent infinite loops. |

## Common Pitfalls

### Pitfall 1: Multi-module Dependency Visibility
**What goes wrong:** UI module trying to access Room DAOs directly.
**Why it happens:** Incorrectly adding `:data` as an `api` dependency instead of `implementation` to `:ui`.
**How to avoid:** `:ui` should only depend on `:domain`. Use Hilt to inject UseCases into ViewModels.

### Pitfall 2: Infinite Sync Loops
**What goes wrong:** Firestore update triggers Room change -> Room change triggers SyncWorker -> SyncWorker updates Firestore.
**Why it happens:** Not checking if the local change was actually a "new" update vs a "synced" update.
**How to avoid:** Add an `isSynced` flag or `lastUpdatedRemote` timestamp to Room entities. Only trigger SyncWorker for local edits where `isSynced` is false.

## Code Examples

### Custom Material 3 Brand Theme (Clean/Laundry Style)
```kotlin
// ui/src/main/.../theme/Color.kt
val PrimaryBlue = Color(0xFF0061A4)
val SecondaryGray = Color(0xFF535F70)
val TertiaryTeal = Color(0xFF006B5D)

// ui/src/main/.../theme/Theme.kt
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryGray,
    tertiary = TertiaryTeal,
    background = Color(0xFFFDFBFF),
    surface = Color(0xFFFDFBFF),
)

@Composable
fun WashCleanerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
```

## Environment Availability

| Dependency | Required By | Available | Version | Fallback |
|------------|------------|-----------|---------|----------|
| Node.js | Development | ✓ | 25.8.1 | — |
| Git | Version Control | ✓ | 2.53.0 | — |
| Android SDK | App Development | ✓ | API 36 | — |

## Validation Architecture

### Test Framework
| Property | Value |
|----------|-------|
| Framework | JUnit 4 + Robolectric |
| Config file | `app/build.gradle.kts` |
| Quick run command | `./gradlew test` |
| Full suite command | `./gradlew connectedCheck` |

### Phase Requirements → Test Map
| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| INF-01 | Room DB persists multiple entities | Unit Test | `./gradlew :data:test` | ❌ Wave 0 |
| INF-02 | SyncWorker triggers on write | Integration | `./gradlew :app:test` | ❌ Wave 0 |
| D-05 | Hilt modules compile | Build | `./gradlew assembleDebug` | ❌ Wave 0 |

## Security Domain

### Applicable ASVS Categories

| ASVS Category | Applies | Standard Control |
|---------------|---------|-----------------|
| V5 Input Validation | yes | Room `@TypeConverters` and Domain validation logic. |
| V6 Cryptography | no | Firestore handles encryption at rest. |

### Known Threat Patterns

| Pattern | STRIDE | Standard Mitigation |
|---------|--------|---------------------|
| Data Exposure | Information Disclosure | Firestore Security Rules (enforced in Phase 2). |
| Unauthorized Access | Elevation of Privilege | Hilt scoped dependencies to prevent leaky state. |

## Sources

### Primary (HIGH confidence)
- [dagger_dev_hilt] - Hilt multi-module and entry point documentation.
- [androidx_compose_material3] - Material 3 custom theming.
- [developer.android.com] - WorkManager and Room best practices.

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH - Latest Jetpack versions used.
- Architecture: HIGH - Standard Clean Architecture mapped to modules.
- Pitfalls: MEDIUM - Sync conflict strategies are hypothesis-based until implementation.

**Research date:** May 1, 2026
**Valid until:** June 1, 2026
