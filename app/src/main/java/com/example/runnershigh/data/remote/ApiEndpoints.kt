
package com.example.runnershigh.data.remote

import retrofit2.http.DELETE

object ApiEndpoints {

    // ───────── Auth & User 관련 ─────────

    //로그인 기능을 수행합니다.
    const val LOGIN_API: String =
        "https://example.com/login-api"

    //사용자 생성. 저희의 신장,체중,러닝목적,경험 여부를 묻고 이메일,비밀번호,이름까지 받아와서 이루어집니다.
    const val SIGNUP_API: String =
        "https://example.com/signup-api"

    //이름 중복 체크.
    const val CHECK_USERNAME_API: String =
        "https://example.com/check-username-api"

    //이메일 중복 체크
    const val CHECK_EMAIL_API = "https://example.com/check-email-api"

    //사용자 레벨 조회 기능을 수행합니다
    const val GET_USER_LEVEL_API: String =
        "https://example.com/get-user-level-api"

    //레벨 업데이트 기능을 수행합니다(exp증가 배지 흭득)
    const val UPDATE_USER_LEVEL_API: String =
        "https://example.com/update-user-level-api"


    //사용자의 신장·체중 저장 기능을 수행합니다
    const val UPDATE_BODY_API: String =
        "https://example.com/update-body-api"
    //사용자의 러닝 목적 저장 기능을 수행합니다
    const val UPDATE_PURPOSE_API: String =
        "https://example.com/update-purpose-api"

    //사용자의 러닝 경험 저장 기능을 수행합니다
    const val UPDATE_EXPERIENCE_API: String =
        "https://example.com/update-experience-api"

    //프로필 정보 조회 기능을 담당합니다
    const val GET_PROFILE_API: String =
        "https://example.com/get-profile-api"



    //MAIN HOME(runningScreen.kt)의 오늘의 플랜 조회 등 대시보드 정보를 가져오는 기능을 담당합니다
    //오늘의 플랜은 이제 여기서 가져옵니다.
    const val GET_HOME_DASHBOARD_API: String =
        "https://example.com/get-home-dashboard"




    // ───────── 러닝 세션 관련 ─────────
   //러닝 세션 시작 기능을 담당합니다
    const val START_RUNNING_API: String =
        "https://example.com/start-running"
    //러닝 세션 종료 및 러닝 종료 (GPS 정보 저장 포함) 기능을 담당합니다
    const val COMPLETE_RUNNING_API: String =
        "https://example.com/complete-running"
    //특정 세션의 러닝 결과 조회 및 러닝 기록 조회 기능을 담당합니다
    const val GET_SESSION_DETAIL_API: String =
        "https://example.com/get-session-detail-api"
    //목표와 현재 기록을 비교하는 api.
    const val GET_RUNNING_COMPARISON_API: String =
        "https://example.com/get-running-comparison-api"
    // 러닝 종료 후 피드백을 새로 생성/제출하는 기능을 담당합니다
    const val CREATE_FEEDBACK_API: String =
        "https://example.com/create-feedback-api"


    // ───────── 활동/피드백 관련. ─────────
    //월별, 연간, 전체 활동 요약을 비롯한 데이터 분석 API의 다양한 활동 통계 및 그래프 데이터 조회를 담당합니다
    const val GET_ACTIVITY_STATS_API: String =
        "https://example.com/get-activity-stats"
    //이미 제출된 피드백(코스 만족도, 난이도, 아픈 부위 등)을 조회하는 기능을 담당합니다
    const val GET_FEEDBACK_API: String =
        "https://example.com/get-feedback-api"





    // ───────── 배지 / 피드백 관련 ─────────
    //배지 자동 획득 검사 또는 강제 배지 지급 기능을 수행합니다
    const val ACQUIRE_BADGE_API: String =
        "https://example.com/acquire-badge-api"
    // 사용자 미션(다가오는 미션) 목록 조회 기능을 담당합니다
    const val GET_UPCOMING_MISSIONS: String =
        "https://example.com/get-upcoming-missions"
    // 사용자 배지 리스트 조회 기능을 담당합니다
    const val GET_BADGE_LIST_API: String =
        "https://example.com/get-badge-list-api"

    //컨디션 레벨 조회를 포함하여 컨디션 관련 세부 정보를 조회합니다.
    const val GET_CONDITION_DETAIL_API: String =
        "https://example.com/get-condition-detail"




    // ───────── 헬스 데이터 연동 ─────────(즉 헬스데이터에서 받아오는 심박수같은 데이터들은 여기서 받아와야 합니다.)
    const val SEND_HEALTH_DATA_API: String = "https://example.com/sync-health-data-api"

    //코스 크리에이터 연동.

    //내 코스 목록 조회 기능을 수행합니다. 이 API는 사용자가 생성하거나 저장한 러닝 코스 리스트를 (러닝 종료 후 세션 기록에서.)가져오는 데 사용됩니다
    const val GET_RUNNING_COURSES: String = "https://example.com/get-running-courses"

    //코스 등록 기능을 수행합니다. 사용자가 새로운 러닝 코스를 생성하여 서버에 저장할 때 호출됩니다
    const val CREATE_RUNNING_COURSE: String = "https://example.com/create-running-course"

    //코스 삭제 기능을 수행합니다.
    const val DELETE_COURSE: String = "https://example.com/delete-course"



}
