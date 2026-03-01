package com.barcodebite.backend.service

import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

class RateLimitService(
    private val freeDailyLimit: Int = 10,
) {
    private val counters = ConcurrentHashMap<String, Int>()

    fun canConsume(key: String): Boolean {
        val todayKey = scopedKey(key)
        val count = counters[todayKey] ?: 0
        return count < freeDailyLimit
    }

    fun consume(key: String): Boolean {
        val todayKey = scopedKey(key)
        val updated = counters.merge(todayKey, 1, Int::plus) ?: 1
        return updated <= freeDailyLimit
    }

    private fun scopedKey(key: String): String = "${LocalDate.now()}:$key"
}
