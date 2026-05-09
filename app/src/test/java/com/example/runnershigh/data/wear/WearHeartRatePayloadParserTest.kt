package com.example.runnershigh.data.wear

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WearHeartRatePayloadParserTest {

    @Test
    fun `normalize returns null when bpm is not positive`() {
        val result = WearHeartRatePayloadParser.normalize(
            rawBpm = 0,
            rawTimestampMs = 123L,
            nowMs = 999L,
            schemaVersion = 1,
            source = null
        )

        assertNull(result)
    }

    @Test
    fun `normalize uses now when timestamp is missing`() {
        val result = WearHeartRatePayloadParser.normalize(
            rawBpm = 154,
            rawTimestampMs = 0L,
            nowMs = 1_000L,
            schemaVersion = 1,
            source = WearHeartRateContract.SOURCE_EXERCISE_CLIENT
        )

        requireNotNull(result)
        assertEquals(154, result.bpm)
        assertEquals(1_000L, result.timestampMs)
        assertEquals(1, result.schemaVersion)
        assertEquals(WearHeartRateContract.SOURCE_EXERCISE_CLIENT, result.source)
    }

    @Test
    fun `normalize keeps valid timestamp`() {
        val result = WearHeartRatePayloadParser.normalize(
            rawBpm = 132,
            rawTimestampMs = 55_000L,
            nowMs = 99_000L,
            schemaVersion = 2,
            source = WearHeartRateContract.SOURCE_SENSOR_MANAGER
        )

        requireNotNull(result)
        assertEquals(132, result.bpm)
        assertEquals(55_000L, result.timestampMs)
        assertEquals(2, result.schemaVersion)
        assertEquals(WearHeartRateContract.SOURCE_SENSOR_MANAGER, result.source)
    }
}
