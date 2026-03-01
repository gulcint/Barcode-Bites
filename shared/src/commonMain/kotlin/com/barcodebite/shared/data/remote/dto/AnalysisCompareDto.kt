package com.barcodebite.shared.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnalysisCompareDto(
    val first: AnalysisDto,
    val second: AnalysisDto,
    val recommendation: String,
)
