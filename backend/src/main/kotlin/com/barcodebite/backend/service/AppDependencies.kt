package com.barcodebite.backend.service

import com.barcodebite.backend.config.AppConfig
import com.barcodebite.backend.external.OpenFoodFactsClient
import com.barcodebite.backend.repository.ProductRepository
import com.barcodebite.backend.repository.UserRepository

data class AppDependencies(
    val authService: AuthService,
    val productService: ProductService,
    val analysisService: AnalysisService,
    val userRepository: UserRepository,
)

fun createDependencies(config: AppConfig): AppDependencies {
    val userRepository = UserRepository()
    val productRepository = ProductRepository()
    val jwtService = JwtService(config.jwt)
    val scoringService = NutritionScoringService()
    val openFoodFactsClient = OpenFoodFactsClient(config.openFoodFacts)

    return AppDependencies(
        authService = AuthService(userRepository, jwtService),
        productService = ProductService(productRepository, openFoodFactsClient),
        analysisService = AnalysisService(productRepository, scoringService),
        userRepository = userRepository,
    )
}
