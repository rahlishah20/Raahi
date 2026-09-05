package com.example.domain.usecase

import com.example.domain.model.RaahiRoute
import com.example.domain.safety.SafetyIntelligenceEngine
import com.example.domain.safety.SafetyIntelligenceEngineImpl

class EvaluateRouteSafetyUseCase(
    private val safetyEngine: SafetyIntelligenceEngine = SafetyIntelligenceEngineImpl()
) {
    suspend operator fun invoke(routes: List<RaahiRoute>): Result<List<RaahiRoute>> {
        return try {
            if (routes.isEmpty()) {
                Result.success(emptyList())
            } else {
                val evaluatedRoutes = safetyEngine.evaluateAndCompareRoutes(routes)
                Result.success(evaluatedRoutes)
            }
        } catch (e: Exception) {
            try {
                android.util.Log.e("EvaluateRouteSafetyUseCase", "Safety evaluation failed, falling back to routes without safety profile", e)
            } catch (_: Throwable) {
                println("ERROR: [EvaluateRouteSafetyUseCase] Safety evaluation failed: ${e.message}")
            }
            // Gracefully fallback to original routes without safety profiles if engine fails
            Result.success(routes)
        }
    }
}
