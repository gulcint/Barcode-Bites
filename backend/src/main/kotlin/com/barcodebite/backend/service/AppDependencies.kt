package com.barcodebite.backend.service

import com.barcodebite.backend.config.AppConfig
import com.barcodebite.backend.external.OpenFoodFactsClient
import com.barcodebite.backend.repository.ProductRepository
import com.barcodebite.backend.repository.SubscriptionRepository
import com.barcodebite.backend.repository.UserRepository

data class AppDependencies(
    val authService: AuthService,
    val productService: ProductService,
    val analysisService: AnalysisService,
    val rateLimitService: RateLimitService,
    val userRepository: UserRepository,
    val subscriptionRepository: SubscriptionRepository,
)

fun createDependencies(config: AppConfig): AppDependencies {
    val userRepository = UserRepository()
    val subscriptionRepository = SubscriptionRepository()
    val productRepository = ProductRepository()
    val jwtService = JwtService(config.jwt)
    val scoringService = NutritionScoringService()
    val rateLimitService = RateLimitService(freeDailyLimit = 10)
    val openFoodFactsClient = OpenFoodFactsClient(config.openFoodFacts)
    val additiveCatalogService = AdditiveCatalogService()

    return AppDependencies(
        authService = AuthService(userRepository, jwtService),
        productService = ProductService(productRepository, openFoodFactsClient, additiveCatalogService),
        analysisService = AnalysisService(productRepository, scoringService),
        rateLimitService = rateLimitService,
        userRepository = userRepository,
        subscriptionRepository = subscriptionRepository,
    )
}
