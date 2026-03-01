# 🍫 BarcodeBite — Proje Planlama Dökümanı
> Bilinçli tüketim için akıllı barkod analiz uygulaması  
> **Stack:** Kotlin Multiplatform (Android önce) + Ktor Backend + Clean Architecture + MVVM

---

## 📋 İÇİNDEKİLER
1. [Proje Özeti](#proje-özeti)
2. [Özellikler & Sürümler](#özellikler--sürümler)
3. [Teknik Mimari](#teknik-mimari)
4. [Proje Yapısı](#proje-yapısı)
5. [Veri Modelleri](#veri-modelleri)
6. [API Tasarımı](#api-tasarımı)
7. [Ekranlar & UI/UX](#ekranlar--uiux)
8. [Animasyon Rehberi](#animasyon-rehberi)
9. [Puanlama & Analiz Sistemi](#puanlama--analiz-sistemi)
10. [Veritabanı Şeması](#veritabanı-şeması)
11. [Bağımlılıklar](#bağımlılıklar)
12. [Geliştirme Yol Haritası](#geliştirme-yol-haritası)
13. [Ortam Kurulumu](#ortam-kurulumu)

---

## 🎯 Proje Özeti

**BarcodeBite**, paketli gıdaların barkodunu okuyarak içerik analizi yapan, kullanıcıların daha bilinçli tüketici olmasını sağlayan bir mobil uygulamadır.

### Temel Değer Önerisi
- Barkod tarat → Anında besin analizi gör
- Katkı maddelerini tespit et
- "Gerçek gıda mı, junk food mu?" sorusuna bilimsel cevap al
- Temiz içerik (clean label) skorlaması
- Geçmiş taramalarını takip et

### Hedef Kitle
Markette alışveriş yapan, ne yediğini bilemek isteyen herkes: ebeveynler, sporcular, sağlık bilincine sahip bireyler.

---

## ⭐ Özellikler & Sürümler

### 🆓 Ücretsiz (Free)
- Günlük **10 barkod tarama**
- Temel besin değerleri (kalori, protein, karbonhidrat, yağ, şeker, tuz)
- Basit skor gösterimi (A/B/C/D/E — Nutriscore benzeri)
- Tarama geçmişi (son 20 ürün)
- Katkı maddesi uyarıları (sadece kritik olanlar)

### 💎 Premium (Pro)
- **Sınırsız** tarama
- Detaylı besin profili (vitaminler, mineraller, amino asitler)
- Katkı maddesi tam veritabanı + açıklamalar
- **"Temiz İçerik" Skoru** (0-100)
- Junk food dedektörü (algoritma tabanlı)
- Ürün karşılaştırma (2 ürünü yan yana)
- Haftalık/aylık tüketim raporu
- Alerjen takibi (kişisel profil)
- Barcode geçmişi sınırsız + arama
- CSV/PDF rapor export
- Reklamsız deneyim

### 💰 Fiyatlandırma
```
Premium Aylık:   ₺49.99 / ay
Premium Yıllık:  ₺399.99 / yıl (~₺33/ay)
```

---

## 🏗️ Teknik Mimari

### Genel Bakış
```
┌─────────────────────────────────────────────────┐
│               KMP Shared Module                  │
│  domain/ · data/ · presentation/viewmodels       │
├────────────────┬────────────────────────────────┤
│  Android App   │     iOS App (gelecek faz)       │
│  Compose UI    │     SwiftUI                     │
└────────────────┴────────────────────────────────┘
                         │
                    HTTPS / REST
                         │
┌─────────────────────────────────────────────────┐
│              Ktor Backend (JVM)                  │
│  Auth · Product · Analysis · User · Subscription│
├─────────────────────────────────────────────────┤
│  PostgreSQL  │  Redis Cache  │  S3 (görseller)  │
└─────────────────────────────────────────────────┘
                         │
              ┌──────────┴──────────┐
              │  Open Food Facts    │
              │  OpenFoodRepo API   │
              │  Edamam Nutrition   │
              └─────────────────────┘
```

### Katmanlar (Clean Architecture)

```
Presentation Layer  →  ViewModel (MVVM) → UI State
Domain Layer        →  UseCases → Repository Interfaces
Data Layer          →  Repository Impl → Remote/Local DataSource
```

---

## 📁 Proje Yapısı

```
BarcodeBite/
├── androidApp/
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── kotlin/com/barcodebite/android/
│           ├── MainActivity.kt
│           ├── BarcodeBiteApp.kt
│           └── ui/
│               ├── theme/
│               │   ├── Theme.kt
│               │   ├── Color.kt
│               │   ├── Typography.kt
│               │   └── Shape.kt
│               ├── navigation/
│               │   └── AppNavigation.kt
│               └── screens/
│                   ├── splash/SplashScreen.kt
│                   ├── onboarding/OnboardingScreen.kt
│                   ├── home/HomeScreen.kt
│                   ├── scanner/
│                   │   ├── ScannerScreen.kt
│                   │   └── CameraPreview.kt
│                   ├── result/
│                   │   ├── ProductResultScreen.kt
│                   │   ├── NutritionChart.kt
│                   │   ├── AdditivesList.kt
│                   │   └── ScoreCard.kt
│                   ├── history/HistoryScreen.kt
│                   ├── compare/CompareScreen.kt
│                   ├── profile/ProfileScreen.kt
│                   └── paywall/PaywallScreen.kt
│
├── shared/
│   └── src/
│       ├── commonMain/kotlin/com/barcodebite/shared/
│       │   ├── domain/
│       │   │   ├── model/
│       │   │   │   ├── Product.kt
│       │   │   │   ├── Nutrition.kt
│       │   │   │   ├── Additive.kt
│       │   │   │   ├── NutritionScore.kt
│       │   │   │   └── ScanResult.kt
│       │   │   ├── repository/
│       │   │   │   ├── ProductRepository.kt
│       │   │   │   ├── UserRepository.kt
│       │   │   │   └── ScanHistoryRepository.kt
│       │   │   └── usecase/
│       │   │       ├── ScanBarcodeUseCase.kt
│       │   │       ├── GetProductByBarcodeUseCase.kt
│       │   │       ├── AnalyzeProductUseCase.kt
│       │   │       ├── GetScanHistoryUseCase.kt
│       │   │       ├── CompareProductsUseCase.kt
│       │   │       └── CheckPremiumUseCase.kt
│       │   ├── data/
│       │   │   ├── remote/
│       │   │   │   ├── api/
│       │   │   │   │   ├── BarcodeBiteApiService.kt
│       │   │   │   │   └── OpenFoodFactsService.kt
│       │   │   │   └── dto/
│       │   │   │       ├── ProductDto.kt
│       │   │   │       └── NutritionDto.kt
│       │   │   ├── local/
│       │   │   │   ├── database/AppDatabase.kt
│       │   │   │   └── entity/
│       │   │   │       ├── ProductEntity.kt
│       │   │   │       └── ScanHistoryEntity.kt
│       │   │   └── repository/
│       │   │       ├── ProductRepositoryImpl.kt
│       │   │       └── ScanHistoryRepositoryImpl.kt
│       │   └── presentation/
│       │       └── viewmodel/
│       │           ├── ScannerViewModel.kt
│       │           ├── ProductResultViewModel.kt
│       │           ├── HistoryViewModel.kt
│       │           └── ProfileViewModel.kt
│       ├── androidMain/
│       └── iosMain/
│
└── backend/
    └── src/main/kotlin/com/barcodebite/backend/
        ├── Application.kt
        ├── plugins/
        │   ├── Routing.kt
        │   ├── Security.kt
        │   ├── Serialization.kt
        │   ├── Database.kt
        │   ├── RateLimit.kt
        │   └── CORS.kt
        ├── routes/
        │   ├── AuthRoutes.kt
        │   ├── ProductRoutes.kt
        │   ├── AnalysisRoutes.kt
        │   ├── UserRoutes.kt
        │   └── SubscriptionRoutes.kt
        ├── service/
        │   ├── ProductService.kt
        │   ├── AnalysisService.kt
        │   ├── NutritionScoringService.kt
        │   ├── AdditiveService.kt
        │   └── SubscriptionService.kt
        ├── model/
        │   ├── Product.kt
        │   ├── User.kt
        │   └── Subscription.kt
        └── external/
            ├── OpenFoodFactsClient.kt
            └── EdamamClient.kt
```

---

## 📊 Veri Modelleri

### Product.kt (Shared Domain)
```kotlin
data class Product(
    val id: String,
    val barcode: String,
    val name: String,
    val brand: String,
    val imageUrl: String?,
    val quantity: String?,
    val categories: List<String>,
    val nutrition: Nutrition,
    val additives: List<Additive>,
    val ingredients: String,
    val allergens: List<String>,
    val labels: List<String>,          // "organic", "vegan", etc.
    val score: NutritionScore,
    val cleanLabelScore: Int,          // 0-100
    val isJunkFood: Boolean,
    val junkFoodReasons: List<String>,
    val analysisNotes: List<AnalysisNote>
)
```

### Nutrition.kt
```kotlin
data class Nutrition(
    // Per 100g
    val energyKcal: Double,
    val proteins: Double,
    val carbohydrates: Double,
    val sugars: Double,
    val fat: Double,
    val saturatedFat: Double,
    val fiber: Double,
    val salt: Double,
    val sodium: Double,
    // Premium fields
    val vitaminC: Double? = null,
    val calcium: Double? = null,
    val iron: Double? = null,
    // Calculated
    val proteinCalorieRatio: Double,    // protein * 4 / totalKcal * 100
    val sugarCalorieRatio: Double,
    val fatCalorieRatio: Double
)
```

### Additive.kt
```kotlin
data class Additive(
    val eNumber: String,               // E621, E951 etc.
    val name: String,
    val category: AdditiveCategory,
    val riskLevel: RiskLevel,
    val description: String,
    val effects: List<String>,
    val isBanned: Boolean,             // bazı ülkelerde yasaklı mı
    val isPremiumDetail: Boolean
)

enum class AdditiveCategory {
    PRESERVATIVE, COLORANT, SWEETENER, FLAVOR_ENHANCER,
    EMULSIFIER, STABILIZER, THICKENER, ANTIOXIDANT, OTHER
}

enum class RiskLevel { LOW, MODERATE, HIGH, VERY_HIGH }
```

### NutritionScore.kt
```kotlin
data class NutritionScore(
    val grade: NutritionGrade,         // A, B, C, D, E
    val score: Int,                    // -15 to +40 (Nutriscore algorithm)
    val positivePoints: Int,
    val negativePoints: Int,
    val colorHex: String               // #038141, #85BB2F, #FECB02, #EE8100, #E63E11
)

enum class NutritionGrade { A, B, C, D, E }
```

### NutritionAnalysis.kt (Analiz Notları)
```kotlin
data class AnalysisNote(
    val type: NoteType,
    val title: String,
    val description: String,
    val severity: Severity
)

enum class NoteType {
    HIGH_SUGAR, HIGH_SALT, HIGH_SATURATED_FAT,
    LOW_PROTEIN, HIGH_ADDITIVES, MISLEADING_LABEL,
    JUNK_FOOD_INDICATOR, CLEAN_LABEL, HIGH_FIBER,
    ARTIFICIAL_SWEETENER, MSG_PRESENT
}
```

---

## 🌐 API Tasarımı

### Base URL
```
Production: https://api.barcodebite.app/v1
Development: http://localhost:8080/v1
```

### Endpoints

#### Auth
```http
POST   /auth/register          # Email + password kayıt
POST   /auth/login             # JWT token al
POST   /auth/refresh           # Token yenile
POST   /auth/google            # Google OAuth
DELETE /auth/logout
```

#### Products
```http
GET    /products/{barcode}     # Barkod ile ürün getir
GET    /products/search?q=     # İsim ile ara (Premium)
POST   /products/report        # Hatalı bilgi bildir
```

#### Analysis
```http
GET    /analysis/{barcode}     # Tam analiz raporu
POST   /analysis/compare       # İki ürün karşılaştır (Premium)
GET    /analysis/{barcode}/score  # Sadece skor
```

#### Scan History
```http
GET    /history                # Tarama geçmişi
POST   /history                # Taramayı kaydet
DELETE /history/{id}           # Tarama sil
DELETE /history                # Tüm geçmişi temizle
```

#### User
```http
GET    /user/profile           # Profil bilgileri
PUT    /user/profile           # Profil güncelle
PUT    /user/allergens         # Alerjen tercihleri
GET    /user/stats             # Kullanım istatistikleri
```

#### Subscription
```http
GET    /subscription/status    # Premium durumu
POST   /subscription/verify    # Satın alma doğrula (Google Play)
POST   /subscription/restore   # Satın alma geri yükle
```

### Response Format
```json
{
  "success": true,
  "data": { ... },
  "error": null,
  "meta": {
    "timestamp": "2025-01-15T10:30:00Z",
    "version": "1.0.0"
  }
}
```

### Hata Response
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "PRODUCT_NOT_FOUND",
    "message": "Bu barkoda ait ürün bulunamadı",
    "details": null
  }
}
```

---

## 📱 Ekranlar & UI/UX

### Renk Paleti
```kotlin
// Light Theme
val Primary = Color(0xFF1A1A2E)          // Derin lacivert
val Secondary = Color(0xFF16213E)
val Accent = Color(0xFF0F3460)
val GreenHealthy = Color(0xFF4CAF50)     // Sağlıklı
val OrangeModerate = Color(0xFFFF9800)   // Orta
val RedUnhealthy = Color(0xFFF44336)     // Sağlıksız
val Background = Color(0xFFF8F9FA)
val Surface = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF1A1A2E)
val TextSecondary = Color(0xFF6C757D)

// Nutriscore Renkleri
val ScoreA = Color(0xFF038141)
val ScoreB = Color(0xFF85BB2F)
val ScoreC = Color(0xFFFECB02)
val ScoreD = Color(0xFFEE8100)
val ScoreE = Color(0xFFE63E11)
```

### Tipografi
```kotlin
// Modern, okunabilir font stack
// Başlıklar: Inter Bold / Poppins SemiBold
// Gövde: Inter Regular
// Sayısal değerler: Roboto Mono
```

### Ekran Listesi

#### 1. Splash Screen
- Logo animasyonu (ölçek + fade)
- Gradient arka plan
- Versiyon kontrolü

#### 2. Onboarding (3 sayfa)
- Sayfa 1: "Barkodu Tara" — scanner animasyonu
- Sayfa 2: "İçeriği Öğren" — chart animasyonu  
- Sayfa 3: "Bilinçli Seç" — skor kartı animasyonu
- Lottie animasyonları
- Skip butonu

#### 3. Home Screen
- Üstte arama çubuğu
- Büyük "Tara" FAB (pulse animasyonu)
- Son tarananlar (horizontal scroll)
- Günlük kota göstergesi (Free kullanıcı)
- Premium banner (Free kullanıcı)

#### 4. Scanner Screen
- Tam ekran kamera önizleme
- Animasyonlu tarama çerçevesi (köşeler kayıyor)
- Alt panel: "Barkodu çerçeve içine alın"
- Manuel barkod giriş seçeneği
- Torch (fener) butonu

#### 5. Product Result Screen ⭐ (Ana ekran)
```
┌─────────────────────────────────┐
│  ← Geri    [Ürün Adı]   Paylaş │
├─────────────────────────────────┤
│  [Ürün Görseli]                 │
│  Marka • Miktar                 │
│                                 │
│  ┌──────────────────────────┐   │
│  │   NUTRİ SCORE    [A] ✓   │   │  ← Animasyonlu skor kartı
│  └──────────────────────────┘   │
│                                 │
│  BESIN DEĞERLERİ (100g)         │
│  ┌──────────────────────────┐   │
│  │  Donut Chart (makrolar)  │   │  ← Animasyonlu
│  │  Protein/Karb/Yağ        │   │
│  └──────────────────────────┘   │
│                                 │
│  [Kalori Bar]  245 kcal         │
│  Protein  ██████░░  12g         │
│  Karbonhidrat ████████ 28g      │
│  Yağ  ████░░░░  8g              │
│  Şeker ██░░░░░░  4g             │
│  Tuz  █░░░░░░░  0.3g            │
│                                 │
│  TEMİZ İÇERİK SKORU             │
│  ████████████░░ 78/100 🌿       │  ← Animasyonlu progress
│                                 │
│  KATKI MADDELERİ (3)            │
│  ⚠️ E621 - Glutamat  [YÜKSEK]   │
│  ℹ️ E471 - Emülgatör [DÜŞÜK]    │
│  🔒 Detaylar için Premium...     │
│                                 │
│  ANALİZ NOTLARI                 │
│  🔴 Yüksek şeker içeriği        │
│  🟡 Düşük protein oranı         │
│  🔴 Junk Food göstergeleri var   │
└─────────────────────────────────┘
```

#### 6. History Screen
- Tarama listesi (tarih gruplu)
- Skor badge'leri renkli
- Arama / filtreleme
- Sürükle-sil

#### 7. Compare Screen (Premium)
- İki ürün yan yana
- Radar chart karşılaştırma
- Kazanan vurgulaması

#### 8. Profile Screen
- Kullanım istatistikleri
- Alerjen tercihleri
- Premium durumu
- Ayarlar

#### 9. Paywall Screen
- Animasyonlu Premium özellikleri
- Fiyat seçimi (aylık/yıllık)
- Güven rozetleri

---

## 🎬 Animasyon Rehberi

### Kullanılacak Kütüphaneler
- **Lottie Compose** — Onboarding, boş durum animasyonları
- **Compose Animations** — Geçişler, micro-animations
- **MPAndroidChart / Compose Charts** — Besin grafikleri

### Animasyon Listesi

```kotlin
// 1. Scanner Frame Animasyonu
// Köşe braketleri sürekli pulse + scan line yukarı-aşağı

// 2. Score Card Reveal
// Kart flip animasyonu + renk geçişi
// A harfi scale-in + bounce

// 3. Nutrition Bars
// Staggered bar fill animasyonu (0'dan değere)
// Gecikme: her bar 100ms sonra başlar

// 4. Donut Chart
// Sweep animasyonu (0 açıdan tam açıya)
// Her segment sırayla dolsun

// 5. Clean Label Score
// Circular progress animasyonu
// Renk: kırmızıdan yeşile geçiş (score'a göre)

// 6. Additive Risk Badge
// Shake animasyonu (YÜKSEK risk için)

// 7. Screen Transitions
// Shared element transition (scanner → result)
// Slide + fade

// 8. Pull to Refresh
// Custom lottie animasyonu
```

### Animasyon Kodları (Compose)
```kotlin
// Staggered List Animation
@Composable
fun AnimatedNutritionBar(
    label: String,
    value: Double,
    maxValue: Double,
    color: Color,
    animationDelay: Int
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (animationPlayed) (value / maxValue).toFloat() else 0f,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = animationDelay,
            easing = FastOutSlowInEasing
        )
    )
    LaunchedEffect(Unit) { animationPlayed = true }
    // ... UI
}
```

---

## 🔬 Puanlama & Analiz Sistemi

### 1. Nutriscore Algoritması (FSA/Santé Publique France)

```kotlin
object NutriscoreCalculator {
    
    fun calculate(nutrition: Nutrition, isFood: Boolean = true): NutritionScore {
        val negativePoints = calculateNegativePoints(nutrition)
        val positivePoints = calculatePositivePoints(nutrition)
        
        val finalScore = if (negativePoints >= 11 && nutrition.fiber >= 0.9 && !isCheese) {
            negativePoints - nutrition.fiber.toInt() - (if (nutrition.proteins >= 8) 1 else 0)
        } else {
            negativePoints - positivePoints
        }
        
        val grade = when {
            finalScore <= -1 -> NutritionGrade.A
            finalScore <= 2  -> NutritionGrade.B
            finalScore <= 10 -> NutritionGrade.C
            finalScore <= 18 -> NutritionGrade.D
            else             -> NutritionGrade.E
        }
        
        return NutritionScore(grade, finalScore, positivePoints, negativePoints, grade.colorHex)
    }
    
    private fun calculateNegativePoints(n: Nutrition): Int {
        // Enerji (kcal/100g)
        val energyPts = when {
            n.energyKcal <= 335 -> 0; n.energyKcal <= 670 -> 1
            n.energyKcal <= 1005 -> 2; n.energyKcal <= 1340 -> 3
            n.energyKcal <= 1675 -> 4; n.energyKcal <= 2010 -> 5
            n.energyKcal <= 2345 -> 6; n.energyKcal <= 2680 -> 7
            n.energyKcal <= 3015 -> 8; n.energyKcal <= 3350 -> 9
            else -> 10
        }
        // Şeker, doymuş yağ, sodyum benzer tablolar...
        return energyPts + sugarPts + satFatPts + sodiumPts
    }
    
    private fun calculatePositivePoints(n: Nutrition): Int {
        // Lif, protein, meyve/sebze/kuruyemiş içeriği
        return fiberPts + proteinPts + fruitsVeggiesPts
    }
}
```

### 2. Temiz İçerik (Clean Label) Skoru

```kotlin
object CleanLabelScorer {
    
    fun calculate(product: Product): Int {
        var score = 100
        
        // Katkı maddesi cezaları
        score -= product.additives.count { it.riskLevel == RiskLevel.VERY_HIGH } * 20
        score -= product.additives.count { it.riskLevel == RiskLevel.HIGH } * 10
        score -= product.additives.count { it.riskLevel == RiskLevel.MODERATE } * 5
        score -= product.additives.count { it.riskLevel == RiskLevel.LOW } * 2
        
        // Yapay tatlandırıcı cezası
        if (product.additives.any { it.category == AdditiveCategory.SWEETENER }) score -= 15
        
        // Malzeme sayısı cezası (>10 malzeme şüpheli)
        val ingredientCount = product.ingredients.split(",").size
        if (ingredientCount > 20) score -= 20
        else if (ingredientCount > 10) score -= 10
        
        // Bonus: organik/doğal etiketler
        if ("organic" in product.labels) score += 10
        if ("no-additives" in product.labels) score += 15
        
        return score.coerceIn(0, 100)
    }
}
```

### 3. Junk Food Dedektörü

```kotlin
object JunkFoodDetector {
    
    data class JunkFoodAnalysis(
        val isJunkFood: Boolean,
        val confidence: Float,      // 0.0 - 1.0
        val reasons: List<String>
    )
    
    fun analyze(product: Product): JunkFoodAnalysis {
        val reasons = mutableListOf<String>()
        var junkScore = 0
        val nutrition = product.nutrition
        
        // Kural 1: Protein kalori oranı < %10 → junk
        if (nutrition.proteinCalorieRatio < 10.0) {
            reasons.add("Protein oranı kalori değerine göre çok düşük (%${nutrition.proteinCalorieRatio.toInt()})")
            junkScore += 25
        }
        
        // Kural 2: Şeker kalori oranı > %30 → junk
        if (nutrition.sugarCalorieRatio > 30.0) {
            reasons.add("Kalorilerin %${nutrition.sugarCalorieRatio.toInt()}'i şekerden geliyor")
            junkScore += 20
        }
        
        // Kural 3: 100g'da 10g+ şeker → uyarı
        if (nutrition.sugars > 10.0) {
            reasons.add("100g'da ${nutrition.sugars}g yüksek şeker içeriği")
            junkScore += 15
        }
        
        // Kural 4: Yapay tatlandırıcı varsa
        if (product.additives.any { it.category == AdditiveCategory.SWEETENER }) {
            reasons.add("Yapay tatlandırıcı içeriyor")
            junkScore += 10
        }
        
        // Kural 5: MSG veya lezzet arttırıcı
        if (product.additives.any { it.eNumber == "E621" || it.category == AdditiveCategory.FLAVOR_ENHANCER }) {
            reasons.add("Lezzet arttırıcı (MSG veya benzeri) içeriyor")
            junkScore += 15
        }
        
        // Kural 6: Nutriscore D veya E
        if (product.score.grade in listOf(NutritionGrade.D, NutritionGrade.E)) {
            reasons.add("Nutriscore ${product.score.grade} — düşük besin kalitesi")
            junkScore += 15
        }
        
        // Kural 7: Temiz içerik skoru < 40
        if (product.cleanLabelScore < 40) {
            reasons.add("Temiz içerik skoru düşük (${product.cleanLabelScore}/100)")
            junkScore += 10
        }
        
        val isJunk = junkScore >= 40
        val confidence = (junkScore / 100f).coerceIn(0f, 1f)
        
        return JunkFoodAnalysis(isJunk, confidence, reasons)
    }
}
```

### 4. Yanıltıcı Etiket Tespiti

```kotlin
object MisleadingLabelDetector {
    
    fun detect(product: Product): List<AnalysisNote> {
        val notes = mutableListOf<AnalysisNote>()
        
        // "Proteinli" ama protein < 5g/100g
        if (product.name.contains("protein", ignoreCase = true) &&
            product.nutrition.proteins < 5.0) {
            notes.add(AnalysisNote(
                type = NoteType.MISLEADING_LABEL,
                title = "Yanıltıcı 'Proteinli' etiketi",
                description = "Ürün 'proteinli' olarak pazarlanıyor ancak 100g'da yalnızca ${product.nutrition.proteins}g protein içeriyor.",
                severity = Severity.HIGH
            ))
        }
        
        // "Light" ama kalori farkı az
        if (product.name.contains("light", ignoreCase = true) &&
            product.nutrition.energyKcal > 250) {
            notes.add(AnalysisNote(
                type = NoteType.MISLEADING_LABEL,
                title = "Light ürün yüksek kalori",
                description = "Light olarak etiketlenmesine rağmen 100g'da ${product.nutrition.energyKcal.toInt()} kcal içeriyor.",
                severity = Severity.MODERATE
            ))
        }
        
        // "Doğal" ama E no'lu katkı maddesi çok
        if (product.labels.contains("natural") && product.additives.size > 3) {
            notes.add(AnalysisNote(
                type = NoteType.MISLEADING_LABEL,
                title = "'Doğal' ama katkı maddesi fazla",
                description = "${product.additives.size} farklı katkı maddesi içeriyor.",
                severity = Severity.MODERATE
            ))
        }
        
        return notes
    }
}
```

---

## 🗄️ Veritabanı Şeması

### PostgreSQL (Backend)

```sql
-- Kullanıcılar
CREATE TABLE users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email       VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    name        VARCHAR(255),
    google_id   VARCHAR(255),
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP DEFAULT NOW()
);

-- Abonelikler
CREATE TABLE subscriptions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID REFERENCES users(id),
    plan            VARCHAR(50) NOT NULL,   -- 'free', 'premium_monthly', 'premium_yearly'
    status          VARCHAR(50) NOT NULL,   -- 'active', 'expired', 'cancelled'
    purchase_token  VARCHAR(500),
    expires_at      TIMESTAMP,
    created_at      TIMESTAMP DEFAULT NOW()
);

-- Ürün önbelleği
CREATE TABLE products (
    barcode         VARCHAR(50) PRIMARY KEY,
    name            VARCHAR(500) NOT NULL,
    brand           VARCHAR(255),
    image_url       TEXT,
    ingredients     TEXT,
    nutrition_json  JSONB NOT NULL,
    additives_json  JSONB DEFAULT '[]',
    score_json      JSONB NOT NULL,
    clean_label_score INT,
    is_junk_food    BOOLEAN DEFAULT FALSE,
    analysis_json   JSONB DEFAULT '[]',
    source          VARCHAR(50),  -- 'openfoodfacts', 'manual'
    last_updated    TIMESTAMP DEFAULT NOW(),
    created_at      TIMESTAMP DEFAULT NOW()
);

-- Katkı Maddesi Veritabanı
CREATE TABLE additives (
    e_number        VARCHAR(20) PRIMARY KEY,  -- E621
    name            VARCHAR(255) NOT NULL,
    category        VARCHAR(100) NOT NULL,
    risk_level      VARCHAR(50) NOT NULL,
    description     TEXT,
    effects         TEXT[],
    is_banned       BOOLEAN DEFAULT FALSE,
    banned_in       TEXT[]
);

-- Tarama Geçmişi
CREATE TABLE scan_history (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID REFERENCES users(id),
    barcode     VARCHAR(50) NOT NULL,
    product_name VARCHAR(500),
    score_grade VARCHAR(1),
    scanned_at  TIMESTAMP DEFAULT NOW()
);

-- Günlük tarama kotası (Free)
CREATE TABLE daily_scan_quota (
    user_id     UUID REFERENCES users(id),
    date        DATE NOT NULL,
    count       INT DEFAULT 0,
    PRIMARY KEY (user_id, date)
);

-- İndeksler
CREATE INDEX idx_scan_history_user_id ON scan_history(user_id);
CREATE INDEX idx_scan_history_scanned_at ON scan_history(scanned_at);
CREATE INDEX idx_products_barcode ON products(barcode);
```

### SQLDelight (Local - KMP)

```sql
-- ScanHistory.sq
CREATE TABLE ScanHistoryEntity (
    id          TEXT NOT NULL PRIMARY KEY,
    barcode     TEXT NOT NULL,
    productName TEXT NOT NULL,
    brand       TEXT NOT NULL,
    scoreGrade  TEXT NOT NULL,
    imageUrl    TEXT,
    scannedAt   INTEGER NOT NULL  -- epoch millis
);

selectAll:
SELECT * FROM ScanHistoryEntity ORDER BY scannedAt DESC;

selectRecent:
SELECT * FROM ScanHistoryEntity ORDER BY scannedAt DESC LIMIT :limit;

insert:
INSERT OR REPLACE INTO ScanHistoryEntity VALUES ?;

deleteById:
DELETE FROM ScanHistoryEntity WHERE id = :id;

deleteAll:
DELETE FROM ScanHistoryEntity;

-- CachedProduct.sq
CREATE TABLE CachedProductEntity (
    barcode     TEXT NOT NULL PRIMARY KEY,
    dataJson    TEXT NOT NULL,
    cachedAt    INTEGER NOT NULL
);
```

---

## 📦 Bağımlılıklar

### Android App (build.gradle.kts)

```kotlin
dependencies {
    // KMP Shared
    implementation(projects.shared)
    
    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.3")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.4")
    
    // Koin DI
    implementation("io.insert-koin:koin-android:3.5.6")
    implementation("io.insert-koin:koin-androidx-compose:3.5.6")
    
    // Camera / Barcode
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
    implementation("androidx.camera:camera-camera2:1.4.0")
    implementation("androidx.camera:camera-lifecycle:1.4.0")
    implementation("androidx.camera:camera-view:1.4.0")
    
    // Lottie
    implementation("com.airbnb.android:lottie-compose:6.6.0")
    
    // Coil (image loading)
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    
    // Charts
    implementation("io.github.ehsannarmani:compose-charts:0.0.18")
    
    // DataStore (preferences)
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    
    // Billing (Google Play)
    implementation("com.android.billingclient:billing-ktx:7.1.1")
    
    // Splash
    implementation("androidx.core:core-splashscreen:1.0.1")
}
```

### Shared Module (build.gradle.kts)

```kotlin
kotlin {
    androidTarget()
    iosX64(); iosArm64(); iosSimulatorArm64()
    
    sourceSets {
        commonMain.dependencies {
            // Ktor Client
            implementation("io.ktor:ktor-client-core:2.3.12")
            implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
            implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")
            implementation("io.ktor:ktor-client-logging:2.3.12")
            implementation("io.ktor:ktor-client-auth:2.3.12")
            
            // Coroutines
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
            
            // Serialization
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            
            // SQLDelight
            implementation("app.cash.sqldelight:runtime:2.0.2")
            implementation("app.cash.sqldelight:coroutines-extensions:2.0.2")
            
            // Koin
            implementation("io.insert-koin:koin-core:3.5.6")
            
            // DateTime
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
            
            // Settings (DataStore alternative for KMP)
            implementation("com.russhwolf:multiplatform-settings:1.2.0")
        }
        
        androidMain.dependencies {
            implementation("io.ktor:ktor-client-okhttp:2.3.12")
            implementation("app.cash.sqldelight:android-driver:2.0.2")
        }
        
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:2.3.12")
            implementation("app.cash.sqldelight:native-driver:2.0.2")
        }
    }
}
```

### Backend (build.gradle.kts)

```kotlin
dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core:2.3.12")
    implementation("io.ktor:ktor-server-netty:2.3.12")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.12")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")
    implementation("io.ktor:ktor-server-auth:2.3.12")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.12")
    implementation("io.ktor:ktor-server-cors:2.3.12")
    implementation("io.ktor:ktor-server-rate-limit:2.3.12")
    implementation("io.ktor:ktor-server-call-logging:2.3.12")
    implementation("io.ktor:ktor-server-status-pages:2.3.12")
    
    // Ktor Client (external API calls)
    implementation("io.ktor:ktor-client-core:2.3.12")
    implementation("io.ktor:ktor-client-okhttp:2.3.12")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
    
    // Database
    implementation("org.jetbrains.exposed:exposed-core:0.55.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.55.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.55.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.55.0")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("com.zaxxer:HikariCP:6.0.0")
    
    // Redis Cache
    implementation("io.github.crackthecodeabhi:kreds:0.9.1")
    
    // Koin
    implementation("io.insert-koin:koin-ktor:3.5.6")
    
    // Security
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("org.mindrot:jbcrypt:0.4")
    
    // Config
    implementation("com.sksamuel.hoplite:hoplite-core:2.8.2")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.8.2")
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.12")
    
    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
}
```

---

## 🗺️ Geliştirme Yol Haritası

### Faz 1 — MVP Android (Hafta 1-3)
- [x] Proje kurulumu (KMP + Android + Backend)
- [x] Backend: PostgreSQL şema, temel Ktor kurulumu
- [x] Backend: Auth (JWT), Product endpoint, Analysis endpoint
- [x] Open Food Facts entegrasyonu
- [x] Shared: Domain modeller, Repository interfaces
- [x] Shared: Ktor client setup, DTO'lar
- [x] Android: Theme, Navigation setup
- [x] Android: Scanner ekranı (CameraX + ML Kit)
- [x] Android: Ürün sonuç ekranı (temel)
- [x] Nutriscore algoritması implementasyonu

### Faz 2 — Temel Özellikler (Hafta 4-5)
- [x] Animasyonlu besin grafikleri (Donut chart, bar'lar)
- [x] Clean Label skorer
- [x] Junk Food dedektörü
- [x] Katkı maddesi veritabanı (500+ madde)
- [x] Tarama geçmişi (lokal SQLDelight)
- [x] Home ekranı, History ekranı
- [x] Profil ekranı

### Faz 3 — Premium & Polish (Hafta 6-7)
- [x] Google Play Billing entegrasyonu
- [x] Paywall ekranı
- [x] Premium özellikler (karşılaştırma, detaylı analiz)
- [x] Onboarding animasyonları (Lottie)
- [x] Splash screen
- [x] Micro-animasyonlar
- [x] Hata durumları, boş durumlar

### Faz 4 — Yayın Hazırlığı (Hafta 8)
- [x] Rate limiting (Free: 10/gün)
- [x] Crash reporting (Firebase Crashlytics)
- [x] Analytics (Firebase Analytics)
- [x] Backend deployment (Railway / Render / VPS)
- [x] Google Play Store hazırlığı
- [x] Privacy Policy, Terms of Service
- [x] Beta test

### Faz 5 — Production Operasyon (Hafta 9)
- [x] Production credential checklist
- [x] Play Store release checklist
- [x] Launch monitoring runbook
- [x] On-call ownership mapping
- [x] Backend production env preflight script

### Faz 6 — CI/CD ve Kalite Kapilari (Hafta 10)
- [x] GitHub Actions CI pipeline (backend/shared/android gates)
- [x] PR merge gate dokumantasyonu
- [x] Dependabot ile haftalik bagimlilik guncellemesi

---

## ⚙️ Ortam Kurulumu

### Gereksinimler
```
Android Studio Ladybug (2024.2.1+)
JDK 17+
Kotlin 2.0.21+
Gradle 8.9+
Docker (backend için)
```

### Ortam Değişkenleri (.env)
```env
# Backend
DATABASE_URL=postgresql://localhost:5432/barcodebite
DATABASE_USER=barcodebite
DATABASE_PASSWORD=secret
REDIS_URL=redis://localhost:6379
JWT_SECRET=your-super-secret-jwt-key-here
JWT_ISSUER=barcodebite.app
JWT_AUDIENCE=barcodebite-users

# External APIs
OPEN_FOOD_FACTS_URL=https://world.openfoodfacts.org/api/v2
EDAMAM_APP_ID=your-edamam-app-id
EDAMAM_APP_KEY=your-edamam-app-key

# Google Play
GOOGLE_PLAY_SERVICE_ACCOUNT_JSON=path/to/service-account.json
```

### local.properties (Android)
```properties
BARCODEBITE_API_BASE_URL=http://10.0.2.2:8080/v1
BARCODEBITE_API_KEY=dev-api-key
```

### Docker Compose (Backend geliştirme)
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: barcodebite
      POSTGRES_USER: barcodebite
      POSTGRES_PASSWORD: secret
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

volumes:
  postgres_data:
```

---

## 🔗 Dış API'lar

### Open Food Facts (Ücretsiz, birincil kaynak)
```
GET https://world.openfoodfacts.org/api/v2/product/{barcode}.json
```
- Dünya geneli en büyük açık gıda veritabanı
- 3M+ ürün
- Nutriscore dahil

### Edamam Food Database (Yedek / zenginleştirme)
```
GET https://api.edamam.com/api/food-database/v2/parser?upc={barcode}
```
- Detaylı vitamin/mineral bilgisi
- Freemium (günlük 1000 istek ücretsiz)

---

## 💡 Codex İçin Önemli Notlar

1. **Her zaman Clean Architecture katmanlarına uy** — ViewModel'den direkt API çağırma, UseCase kullan
2. **Shared modüldeki tüm iş mantığı platform bağımsız olmalı** — expect/actual sadece platform spesifik için
3. **Compose UI'da State hoisting** — State'i mümkün olduğunca yukarı taşı
4. **Ktor client'ta interceptor** — JWT token otomatik ekleme + refresh
5. **SQLDelight sorguları** — Doğrudan SQL yaz, tip güvenli kotlin üretilir
6. **Koin DI** — Module'ları shared, android, backend olarak ayır
7. **Error handling** — `Result<T>` kullan, exception fırlatma
8. **Animasyonlar** — `remember { Animatable() }` ile LaunchedEffect içinde tetikle
9. **Premium check** — Her premium özellik öncesi `CheckPremiumUseCase` çağır
10. **Barcode scanning** — ML Kit önce dene, başarısız olursa ZXing fallback

---

*BarcodeBite v1.0 — Proje Planı*  
*Oluşturulma: 2025 | Teknoloji: KMP + Ktor + Compose*
