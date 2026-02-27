# Junk Food Detector Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Add a backend-driven junk food detector and surface detection result/reasons in shared and Android result screen.

**Architecture:** Extend analysis result contract with `isJunkFood` and `junkFoodReasons`, compute these from nutrition + clean label metrics in `NutritionScoringService`, and render warning details in Android result UI.

**Tech Stack:** Kotlin, Ktor, Kotlinx Serialization, Jetpack Compose.

---

### Task 1: Add failing integration assertions for junk food detection

**Files:**
- Modify: `backend/src/test/kotlin/com/barcodebite/backend/ApplicationTest.kt`

1. Add assertions for unhealthy sample: `isJunkFood = true` and non-empty `junkFoodReasons`.
2. Add assertions for healthy sample: `isJunkFood = false` and empty `junkFoodReasons`.
3. Run focused backend test and confirm failure.

### Task 2: Implement backend detector and API response fields

**Files:**
- Modify: `backend/src/main/kotlin/com/barcodebite/backend/service/NutritionScoringService.kt`
- Modify: `backend/src/main/kotlin/com/barcodebite/backend/service/AnalysisService.kt`
- Modify: `backend/src/main/kotlin/com/barcodebite/backend/model/DomainModels.kt`
- Modify: `backend/src/main/kotlin/com/barcodebite/backend/routes/AnalysisRoutes.kt`

1. Add junk food detection function returning boolean + reasons.
2. Include detector output in `AnalysisResult` and `/v1/analysis` response.
3. Run focused backend tests and confirm pass.

### Task 3: Expose and render junk food info in shared + Android

**Files:**
- Modify: `shared/src/commonMain/kotlin/com/barcodebite/shared/data/remote/dto/AnalysisDto.kt`
- Modify: `shared/src/commonMain/kotlin/com/barcodebite/shared/domain/model/NutritionScore.kt`
- Modify: `androidApp/src/main/kotlin/com/barcodebite/android/ui/screen/result/ProductResultScreen.kt`

1. Add new fields with safe defaults to shared DTO/domain.
2. Show junk food warning/reasons on result screen when flagged.
3. Run full verification command.
