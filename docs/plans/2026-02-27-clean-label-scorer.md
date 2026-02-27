# Clean Label Scorer Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Add a DB-backed clean label score to product analysis and expose it to shared + Android result UI.

**Architecture:** Extend product data model with ingredients and additive codes, compute clean label score in backend service, and return the new fields from `/v1/analysis`. Keep API backward-compatible by adding defaults in shared DTO parsing and update Android UI to render the clean label verdict.

**Tech Stack:** Kotlin, Ktor, Exposed, H2/PostgreSQL, Kotlinx Serialization, Jetpack Compose.

---

### Task 1: Add failing backend tests for clean label analysis response

**Files:**
- Modify: `backend/src/test/kotlin/com/barcodebite/backend/ApplicationTest.kt`

**Step 1: Write the failing test**
- Extend `productAndAnalysisEndpointsUsePersistedData` to assert `cleanLabelScore` and `cleanLabelVerdict` in `/v1/analysis` response.
- Add a scenario with additive-rich product payload and verify clean label score is lower.

**Step 2: Run test to verify it fails**
- Run: `./gradlew --no-daemon :backend:test --tests com.barcodebite.backend.ApplicationTest.productAndAnalysisEndpointsUsePersistedData`
- Expected: fail due to missing fields/logic.

**Step 3: Write minimal implementation**
- Add new fields to backend models, service calculation, and route serialization.

**Step 4: Run test to verify it passes**
- Re-run the same focused test.

**Step 5: Commit**
- `git commit -m "feat(backend): add clean label analysis scoring"`

### Task 2: Feed scoring with persisted product ingredient/additive data

**Files:**
- Modify: `backend/src/main/kotlin/com/barcodebite/backend/model/DomainModels.kt`
- Modify: `backend/src/main/kotlin/com/barcodebite/backend/persistence/table/Tables.kt`
- Modify: `backend/src/main/kotlin/com/barcodebite/backend/repository/ProductRepository.kt`
- Modify: `backend/src/main/kotlin/com/barcodebite/backend/routes/ProductRoutes.kt`
- Modify: `backend/src/main/kotlin/com/barcodebite/backend/external/OpenFoodFactsClient.kt`
- Modify: `backend/src/test/kotlin/com/barcodebite/backend/external/OpenFoodFactsClientTest.kt`

**Step 1: Write failing tests**
- Extend OFF mapping test to assert additive tags and ingredients extraction.

**Step 2: Run test to verify it fails**
- Run: `./gradlew --no-daemon :backend:test --tests com.barcodebite.backend.external.OpenFoodFactsClientTest`

**Step 3: Write minimal implementation**
- Add `ingredients` and `additives` to `ProductRecord`.
- Persist/load them in `products` table (text + csv).
- Parse OFF payload additive tags and ingredients text.

**Step 4: Run test to verify it passes**
- Re-run focused test.

**Step 5: Commit**
- `git commit -m "feat(backend): persist ingredients and additives for clean label scoring"`

### Task 3: Expose clean label fields to shared and Android UI

**Files:**
- Modify: `shared/src/commonMain/kotlin/com/barcodebite/shared/data/remote/dto/AnalysisDto.kt`
- Modify: `shared/src/commonMain/kotlin/com/barcodebite/shared/domain/model/NutritionScore.kt`
- Modify: `androidApp/src/main/kotlin/com/barcodebite/android/ui/screen/result/ProductResultScreen.kt`

**Step 1: Write/adjust failing compile expectations**
- Update model constructor usages to include clean label fields.

**Step 2: Run compile to verify failures are resolved**
- Run: `./gradlew --no-daemon :shared:allTests :androidApp:assembleDebug`

**Step 3: Implement UI render**
- Show clean label score + verdict beneath nutrition score.

**Step 4: Run full verification**
- Run: `ANDROID_SDK_ROOT=/Users/gulcintas/Library/Android/sdk ./gradlew --no-daemon :androidApp:assembleDebug :backend:test :shared:allTests`

**Step 5: Commit**
- `git commit -m "feat(android): show clean label scoring on result screen"`
