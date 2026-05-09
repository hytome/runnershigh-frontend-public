package com.example.runnershigh.data.heartrate

import kotlinx.coroutines.flow.StateFlow

/**
 * 심박수 제공자(Provider) 공통 인터페이스.
 *
 * 백엔드/앱 공통 관점에서 보면 "심박 데이터를 어디서 가져오든"
 * UI/도메인 계층은 동일한 계약(Contract)만 바라보면 됩니다.
 *
 * - Wear OS Data Layer
 * - Health Connect 조회
 * - (추후) BLE 심박밴드
 *
 * 위 소스들이 모두 이 인터페이스를 구현하면,
 * 상위 계층은 소스 변경 없이 `StateFlow<Int>`만 구독하면 됩니다.
 */
interface HeartRateSource {

    /**
     * 현재 심박수(BPM) 상태 스트림.
     * - 0: 유효 데이터 없음(권한 없음/센서 미연결/초기 상태)
     * - 1 이상: 유효 BPM
     */
    val bpm: StateFlow<Int>

    /** 소스 시작(리스너 등록/폴링 시작 등) */
    fun start()

    /** 소스 중지(리스너 해제/폴링 중단 등) */
    fun stop()
}
