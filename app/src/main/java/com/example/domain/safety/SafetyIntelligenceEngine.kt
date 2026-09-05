package com.example.domain.safety

import com.example.domain.model.RaahiRoute
import com.example.domain.model.RouteSafetyProfile

interface SafetyIntelligenceEngine {
    /**
     * Evaluates the contextual safety profile for a single route.
     */
    suspend fun evaluateRouteSafety(route: RaahiRoute): RouteSafetyProfile

    /**
     * Evaluates and compares multiple route alternatives, marking the route with
     * the strongest contextual safety profile as recommended.
     */
    suspend fun evaluateAndCompareRoutes(routes: List<RaahiRoute>): List<RaahiRoute>
}
