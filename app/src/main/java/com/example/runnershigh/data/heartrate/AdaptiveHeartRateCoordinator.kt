package com.example.runnershigh.data.heartrate

import android.content.Context
import com.example.runnershigh.data.health.HealthConnectManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

/**
 * 심박 통합 코디네이터.
 *
 * 목적:
 * - Wear OS(실시간성 우수)와 Health Connect(안정 폴백)를 합쳐
 *   UI에는 단일 심박 스트림만 제공.
 *
 * 정책:
 * 1) 웨어 데이터가 최근에 들어왔으면(신선) 웨어 우선
 * 2) 웨어 데이터가 오래됐거나 없으면 Health Connect 사용
 *
 * 부가 기능:
 * - 러닝 세션 동안 누적 평균 BPM 계산
 * - 현재 어떤 소스를 쓰는지(activeSource) 노출
 */
class AdaptiveHeartRateCoordinator(
    context: Context
) {

    /** UI와 연동되는 메인 스코프 */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    /** 폴백 소스(조회 기반) */
    private val healthSource = HealthConnectHeartRateSource(HealthConnectManager(context))

    /** 우선 소스(이벤트 기반) */
    private val wearSource = WearDataLayerHeartRateSource(context)

    /** 폰 -> 워치 측정 명령 송신 */
    private val commandSender = WearHeartRateCommandSender(context)

    /** 현재 화면에 표시할 BPM */
    private val _currentBpm = MutableStateFlow(0)
    val currentBpm: StateFlow<Int> = _currentBpm.asStateFlow()

    /** 세션 누적 평균 BPM */
    private val _averageBpm = MutableStateFlow(0)
    val averageBpm: StateFlow<Int> = _averageBpm.asStateFlow()

    /** 현재 사용 중인 데이터 소스 */
    private val _activeSource = MutableStateFlow(HeartRateActiveSource.NONE)
    val activeSource: StateFlow<HeartRateActiveSource> = _activeSource.asStateFlow()

    /** 워치 측정 시작/정지 명령 상태 */
    val commandState: StateFlow<HeartRateCommandState> = commandSender.commandState

    private var healthCollectJob: Job? = null
    private var wearCollectJob: Job? = null

    /** 평균 계산 누적치 */
    private var sampleCount = 0
    private var sampleSum = 0L

    /**
     * 코디네이터 시작.
     * - 각 소스 start
     * - 각 소스 flow 구독 시작
     */
    fun start(sessionStartTime: Instant? = null) {
        healthSource.updateReadWindowStartTime(sessionStartTime)
        wearSource.start()
        healthSource.start()

        // 웨어 데이터는 우선 반영
        wearCollectJob?.cancel()
        wearCollectJob = scope.launch {
            wearSource.bpm.collectLatest { bpm ->
                if (bpm > 0) {
                    applySample(bpm, HeartRateActiveSource.WEAR_OS)
                }
            }
        }

        // HC 데이터는 "웨어 데이터가 stale일 때만" 반영
        healthCollectJob?.cancel()
        healthCollectJob = scope.launch {
            healthSource.bpm.collectLatest { bpm ->
                if (bpm <= 0) return@collectLatest
                if (isWearDataFresh()) return@collectLatest
                applySample(bpm, HeartRateActiveSource.HEALTH_CONNECT)
            }
        }
    }

    /** 구독/소스 정지 */
    fun stop() {
        healthCollectJob?.cancel()
        wearCollectJob?.cancel()
        healthCollectJob = null
        wearCollectJob = null

        wearSource.stop()
        healthSource.stop()
    }

    /** 러닝 상태를 하위 소스에 전달(예: HC 폴링 주기 변경) */
    fun setRunning(isRunning: Boolean) {
        healthSource.updatePollingInterval(isRunning)
        if (isRunning) {
            commandSender.sendStart()
        } else {
            commandSender.sendStop()
        }
    }

    /** 새 세션 시작 시 평균값 초기화 */
    fun resetAverage() {
        sampleCount = 0
        sampleSum = 0L
        _currentBpm.update { 0 }
        _averageBpm.update { 0 }
        _activeSource.update { HeartRateActiveSource.NONE }
    }

    /** 완전 해제 */
    fun close() {
        healthSource.close()
        scope.cancel()
    }

    /**
     * 샘플 반영 공통 처리.
     * - 현재값 갱신
     * - 소스 표기 갱신
     * - 누적 평균 계산
     */
    private fun applySample(bpm: Int, source: HeartRateActiveSource) {
        _currentBpm.update { bpm }
        _activeSource.update { source }

        sampleCount += 1
        sampleSum += bpm
        _averageBpm.update { (sampleSum / sampleCount).toInt() }
    }

    /**
     * 웨어 데이터 신선도 판단.
     *
     * 45초 이내 업데이트가 있으면 fresh로 간주해
     * Health Connect 반영을 잠시 억제합니다.
     */
    private fun isWearDataFresh(): Boolean {
        val lastWear = wearSource.lastUpdateEpochMs.value
        if (lastWear <= 0L) return false

        val ageMs = System.currentTimeMillis() - lastWear
        return ageMs <= WEAR_FRESHNESS_WINDOW_MS
    }

    companion object {
        private const val WEAR_FRESHNESS_WINDOW_MS = 45_000L
    }
}

enum class HeartRateActiveSource {
    NONE,
    WEAR_OS,
    HEALTH_CONNECT
}
