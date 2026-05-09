package com.example.runnershigh.data.heartrate

import com.example.runnershigh.data.health.HealthConnectManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import kotlin.math.roundToInt

/**
 * Health Connect 기반 심박 소스.
 *
 * 역할:
 * 1) Health Connect 저장소에서 심박 기록을 주기적으로 읽고
 * 2) 가장 최신 레코드의 샘플 평균값을 BPM으로 방출합니다.
 *
 * 특성:
 * - 실시간 스트리밍이 아닌 "조회 기반 폴링" 방식
 * - 웨어 데이터가 없을 때 안전한 폴백 소스로 동작
 */
class HealthConnectHeartRateSource(
    private val healthConnectManager: HealthConnectManager
) : HeartRateSource {

    /** UI 생명주기와 분리된 I/O 스코프 (조회 작업용) */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** 중복 시작 방지를 위한 폴링 Job 핸들 */
    private var pollingJob: Job? = null

    /** 외부에 노출할 현재 BPM 상태 */
    private val _bpm = MutableStateFlow(0)
    override val bpm: StateFlow<Int> = _bpm.asStateFlow()

    /**
     * 폴링 주기(유동 정책)
     * - 러닝 중: 30초
     * - 일시정지: 60초
     */
    private val pollingIntervalMs = MutableStateFlow(DEFAULT_ACTIVE_INTERVAL_MS)
    private var readWindowStartTime: Instant? = null

    override fun start() {
        // 이미 시작되어 있으면 재시작하지 않음
        if (pollingJob?.isActive == true) return

        pollingJob = scope.launch {
            while (isActive) {
                val latestBpm = readLatestHeartRateSafely()
                _bpm.update { latestBpm }
                delay(pollingIntervalMs.value)
            }
        }
    }

    override fun stop() {
        pollingJob?.cancel()
        pollingJob = null
    }

    /**
     * 러닝 상태에 따라 폴링 주기를 동적으로 조정.
     *
     * 백엔드 팀 관점:
     * 이 값은 클라이언트에서 심박 수집 빈도에 직접 영향이 있으며,
     * 장기적으로는 실험 파라미터(AB 테스트)화하기 좋은 지점입니다.
     */
    fun updatePollingInterval(isRunning: Boolean) {
        pollingIntervalMs.value = if (isRunning) {
            DEFAULT_ACTIVE_INTERVAL_MS
        } else {
            DEFAULT_PAUSED_INTERVAL_MS
        }
    }

    fun updateReadWindowStartTime(startTime: Instant?) {
        readWindowStartTime = startTime
    }

    /** 인스턴스 완전 종료(스코프 해제) */
    fun close() {
        scope.cancel()
    }

    /**
     * Health Connect 조회 실패를 앱 크래시로 연결하지 않기 위해 안전 조회.
     *
     * 처리 방식:
     * - 오늘 데이터 조회
     * - 가장 최신 레코드 선택
     * - 해당 레코드 내부 샘플 평균을 Int BPM으로 변환
     * - 예외 시 0 반환
     */
    private suspend fun readLatestHeartRateSafely(): Int {
        return try {
            val heartRates = readWindowStartTime?.let { startTime ->
                healthConnectManager.readHeartRates(startTime, Instant.now())
            } ?: healthConnectManager.readHeartRates()
            val latestRecord = heartRates.maxByOrNull { it.startTime } ?: return 0
            val average = latestRecord.samples.map { it.beatsPerMinute }.average()
            if (average.isFinite()) average.roundToInt() else 0
        } catch (_: Exception) {
            0
        }
    }

    companion object {
        private const val DEFAULT_ACTIVE_INTERVAL_MS = 30_000L
        private const val DEFAULT_PAUSED_INTERVAL_MS = 60_000L
    }
}
