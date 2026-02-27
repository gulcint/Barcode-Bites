# Phase 1 Backend + Shared Bootstrap Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** BarcodeBite Faz 1 için `backend` (Ktor) ve `shared` (KMP) modüllerini çalışır bir temel ile başlatmak.

**Architecture:** Çok modüllü Gradle yapısı kurulacak (`:backend`, `:shared`). `backend` Ktor HTTP API iskeleti ve temel endpointlerle başlayacak. `shared` tarafında domain model + repository sözleşmeleri + Ktor client katmanı ve DTO’lar tanımlanacak.

**Tech Stack:** Kotlin 2.0.x, Gradle Kotlin DSL, Ktor 2.3.x, Kotlinx Serialization, KMP (common + jvm), kotlin-test, JUnit 5

---

### Task 1: Build Altyapısı

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts`
- Create: `gradle.properties`

**Step 1: Write the failing test**
- Build dosyaları yokken Gradle görevleri çalıştırılamaz.

**Step 2: Run test to verify it fails**
- Run: `./gradlew tasks`
- Expected: FAIL (wrapper/build setup eksik)

**Step 3: Write minimal implementation**
- Çok modüllü proje ayarlarını ve ortak plugin/dependency yönetimini ekle.

**Step 4: Run test to verify it passes**
- Run: `./gradlew tasks`
- Expected: PASS

### Task 2: Backend İskeleti

**Files:**
- Create: `backend/build.gradle.kts`
- Create: `backend/src/main/kotlin/com/barcodebite/backend/Application.kt`
- Create: `backend/src/main/kotlin/com/barcodebite/backend/plugins/*.kt`
- Create: `backend/src/main/kotlin/com/barcodebite/backend/routes/*.kt`
- Test: `backend/src/test/kotlin/com/barcodebite/backend/ApplicationTest.kt`

**Step 1: Write the failing test**
- `/health`, `/v1/products/{barcode}` ve `/v1/analysis` endpoint testlerini yaz.

**Step 2: Run test to verify it fails**
- Run: `./gradlew :backend:test`
- Expected: FAIL (route’lar henüz yok)

**Step 3: Write minimal implementation**
- Ktor app + routing + JSON serialization + endpoint response modellerini ekle.

**Step 4: Run test to verify it passes**
- Run: `./gradlew :backend:test`
- Expected: PASS

### Task 3: Shared Modül Temeli

**Files:**
- Create: `shared/build.gradle.kts`
- Create: `shared/src/commonMain/kotlin/com/barcodebite/shared/domain/model/*.kt`
- Create: `shared/src/commonMain/kotlin/com/barcodebite/shared/domain/repository/*.kt`
- Create: `shared/src/commonMain/kotlin/com/barcodebite/shared/data/remote/dto/*.kt`
- Create: `shared/src/commonMain/kotlin/com/barcodebite/shared/data/remote/api/*.kt`
- Create: `shared/src/commonMain/kotlin/com/barcodebite/shared/data/remote/HttpClientFactory.kt`
- Test: `shared/src/commonTest/kotlin/com/barcodebite/shared/RemoteContractTest.kt`

**Step 1: Write the failing test**
- DTO serialization/deserialization ve API path üretim testlerini yaz.

**Step 2: Run test to verify it fails**
- Run: `./gradlew :shared:allTests`
- Expected: FAIL (DTO/client henüz yok)

**Step 3: Write minimal implementation**
- Domain modeller, repository interface’leri, DTO’lar, API client ve factory’yi ekle.

**Step 4: Run test to verify it passes**
- Run: `./gradlew :shared:allTests`
- Expected: PASS

### Task 4: Bütünleşik Doğrulama

**Files:**
- Modify: `README.md` (kurulum notları)

**Step 1: Write the failing test**
- `./gradlew :backend:test :shared:allTests` komutu başlangıçta fail olmalı.

**Step 2: Run test to verify it fails**
- Run: `./gradlew :backend:test :shared:allTests`

**Step 3: Write minimal implementation**
- Eksik yapılandırmaları tamamla.

**Step 4: Run test to verify it passes**
- Run: `./gradlew :backend:test :shared:allTests`
- Expected: PASS
