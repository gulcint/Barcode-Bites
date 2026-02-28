# Additive Database Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Add a seeded additive catalog with 500+ entries and return enriched additive metadata from backend to shared and Android UI.

**Architecture:** Seed a local backend resource catalog, load it through an additive catalog service, enrich stored additive codes into structured payloads on product responses, and render those additive details on the Android result screen.

**Tech Stack:** Kotlin, Ktor, JVM resources, Kotlinx Serialization, Jetpack Compose.

---

### Task 1: Add failing tests for additive catalog and enriched product responses

**Files:**
- Modify: `backend/src/test/kotlin/com/barcodebite/backend/ApplicationTest.kt`
- Create: `backend/src/test/kotlin/com/barcodebite/backend/service/AdditiveCatalogServiceTest.kt`

1. Assert `/v1/products/{barcode}` returns additive objects with code/name/risk metadata.
2. Assert the additive catalog loads 500+ entries and resolves `e621` / `e950`.
3. Run focused backend tests and verify failure.

### Task 2: Implement backend additive catalog and product enrichment

**Files:**
- Create: `backend/src/main/kotlin/com/barcodebite/backend/service/AdditiveCatalogService.kt`
- Create: `backend/src/main/resources/data/additives.csv`
- Modify: `backend/src/main/kotlin/com/barcodebite/backend/service/AppDependencies.kt`
- Modify: `backend/src/main/kotlin/com/barcodebite/backend/service/ProductService.kt`
- Modify: `backend/src/main/kotlin/com/barcodebite/backend/routes/ProductRoutes.kt`

1. Load CSV catalog with 500+ entries into a lookup map.
2. Enrich product additive code list into structured response payloads.
3. Keep request/persistence format unchanged (`List<String>` codes).
4. Re-run focused backend tests to green.

### Task 3: Expose additive metadata to shared and Android result UI

**Files:**
- Create: `shared/src/commonMain/kotlin/com/barcodebite/shared/data/remote/dto/AdditiveDto.kt`
- Modify: `shared/src/commonMain/kotlin/com/barcodebite/shared/data/remote/dto/ProductDto.kt`
- Create: `androidApp/src/main/kotlin/com/barcodebite/android/ui/screen/result/AdditivesList.kt`
- Modify: `androidApp/src/main/kotlin/com/barcodebite/android/ui/screen/result/ProductResultScreen.kt`

1. Map additive DTOs into shared `Additive` domain models.
2. Render additives with risk labels in Android result screen.
3. Run full verification command and keep commit history split by backend/test/ui.
