package com.example.runnershigh.ui.screen.level

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runnershigh.MainActivity
import com.example.runnershigh.R
import com.example.runnershigh.data.remote.ApiClient
import com.example.runnershigh.data.remote.dto.AcquireBadgeRequest
import com.example.runnershigh.data.remote.dto.AcquiredBadge
import com.example.runnershigh.data.remote.dto.Badge
import com.example.runnershigh.data.remote.dto.BadgeSessionRecord
import com.example.runnershigh.data.remote.dto.RecentActivity
import com.example.runnershigh.data.remote.dto.UserIdRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class BadgeActivity : ComponentActivity(), CoroutineScope {

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    // 잠금 배지용 RecyclerView
    private lateinit var lockedBadgesRecyclerView: RecyclerView
    private lateinit var lockedBadgeAdapter: LockedBadgeAdapter

    // 획득 배지용 RecyclerView
    private lateinit var acquiredBadgesRecyclerView: RecyclerView
    private lateinit var acquiredBadgeAdapter: AcquiredBadgeAdapter

    // 획득 배지 개수 텍스트
    private lateinit var badgeCountText: TextView
    private lateinit var badgeErrorText: TextView
    // 획득 배지 없을 때 보여줄 레이아웃
    private lateinit var noBadgeLayout: View

    private data class SessionStats(
        val records: List<BadgeSessionRecord>
    ) {
        val totalDistanceKm: Double = records.sumOf { it.distanceKm }
        val sessionCount: Int = records.size
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()

        setContentView(R.layout.activity_badge)
        window.statusBarColor = Color.parseColor("#CCFF00")

        // ✅ 반드시 가장 먼저 RecyclerView / View 들 초기화
        initRecyclerView()

        // 뒤로가기 버튼
        val backButton = findViewById<ImageView>(R.id.iv_back)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
            }
            startActivity(intent)
            finish()
        }

    }

    private fun initRecyclerView() {
        // 획득 배지 리스트
        acquiredBadgesRecyclerView = findViewById(R.id.acquired_badges_recycler_view)
        acquiredBadgesRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        acquiredBadgeAdapter = AcquiredBadgeAdapter(emptyList())
        acquiredBadgesRecyclerView.adapter = acquiredBadgeAdapter

        // 잠금 배지 리스트
        lockedBadgesRecyclerView = findViewById(R.id.locked_badges_recycler_view)
        lockedBadgesRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        lockedBadgeAdapter = LockedBadgeAdapter(emptyList())
        lockedBadgesRecyclerView.adapter = lockedBadgeAdapter
        lockedBadgesRecyclerView.isNestedScrollingEnabled = false

        // 배지 개수 / 빈 상태 뷰
        badgeCountText = findViewById(R.id.badge_count_text)
        badgeErrorText = findViewById(R.id.badge_error_text)
        noBadgeLayout = findViewById(R.id.badge_complete_4)
    }

    // 🔹 실제 서버에서 전체 배지 목록 조회 + 획득/미획득 분리
    private suspend fun fetchAllBadges(userUuid: String, sessionStats: SessionStats?) {
        withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.userService.getAllBadges(
                    UserIdRequest(user_uuid = userUuid)
                )
                if (response.isSuccessful) {
                    val badges = response.body().orEmpty()
                    Log.d("API_CALL", "전체 배지 데이터 성공: $badges")

                    val (acquiredBadges, lockedBadges) = partitionBadgesWithSessions(badges, sessionStats)

                    withContext(Dispatchers.Main) {
                        hideBadgeError()
                        updateBadgeUI(lockedBadges)
                        updateAcquiredBadgeUI(acquiredBadges)
                    }
                } else {
                    Log.e(
                        "API_CALL",
                        "배지 API 호출 실패: HTTP ${response.code()} ${response.errorBody()?.string() ?: ""}"
                    )
                    withContext(Dispatchers.Main) {
                        showBadgeError(getString(R.string.badge_error_message))
                        updateBadgeUI(emptyList())
                        updateAcquiredBadgeUI(emptyList())
                    }
                }
            } catch (e: Exception) {
                Log.e("API_CALL", "배지 네트워크 오류 발생", e)
                withContext(Dispatchers.Main) {
                    showBadgeError(getString(R.string.badge_error_message))
                    updateBadgeUI(emptyList())
                    updateAcquiredBadgeUI(emptyList())
                }
            }
        }
    }

    private fun partitionBadgesWithSessions(
        badges: List<Badge>,
        sessionStats: SessionStats?
    ): Pair<List<AcquiredBadge>, List<Badge>> {
        if (badges.isEmpty()) return emptyList<AcquiredBadge>() to emptyList()

        val (acquired, locked) = badges.partition { badge ->
            isBadgeCompleted(badge, sessionStats)
        }

        val acquiredBadges = acquired.map { badge ->
            AcquiredBadge(
                missionName = badge.missionName,
                missionDescription = badge.missionDescription,
                acquiredDate = badge.progressStatus.ifBlank { "Completed" }
            )
        }

        return acquiredBadges to locked
    }

    private fun isBadgeCompleted(badge: Badge, sessionStats: SessionStats?): Boolean {
        val status = badge.progressStatus.trim().lowercase()
        if (status == "completed" || status == "complete" || badge.gaugeRatio >= 100) return true
        if (sessionStats == null) return false

        val combinedText = listOf(
            badge.missionDetail,
            badge.missionDescription,
            badge.missionName
        ).joinToString(" ").lowercase()

        val distanceRegex = Regex("(\\d+(?:\\.\\d+)?)\\s*km")
        val distanceRequirement = distanceRegex.find(combinedText)?.groupValues?.get(1)?.toDoubleOrNull()

        distanceRequirement?.let { requiredDistance ->
            if (sessionStats.totalDistanceKm >= requiredDistance) return true
        }

        if (combinedText.contains("마라톤") && sessionStats.totalDistanceKm >= 42.195) return true
        if (combinedText.contains("첫") || combinedText.contains("first")) {
            return sessionStats.sessionCount > 0
        }

        return false
    }


    // 잠금 배지 UI 업데이트
    private fun updateBadgeUI(badges: List<Badge>?) {
        if (badges.isNullOrEmpty()) {
            Log.w("BADGE_UI", "잠금 배지 데이터가 없습니다.")
            lockedBadgesRecyclerView.visibility = View.GONE
            return
        }
        lockedBadgesRecyclerView.visibility = View.VISIBLE
        lockedBadgeAdapter.updateBadges(badges)
    }

    // 획득 배지 UI 업데이트
    private fun updateAcquiredBadgeUI(acquiredBadges: List<AcquiredBadge>?) {
        if (acquiredBadges.isNullOrEmpty()) {
            noBadgeLayout.visibility = View.VISIBLE
            acquiredBadgesRecyclerView.visibility = View.GONE
            badgeCountText.text = "0"
        } else {
            noBadgeLayout.visibility = View.GONE
            acquiredBadgesRecyclerView.visibility = View.VISIBLE
            badgeCountText.text = acquiredBadges.size.toString()
            acquiredBadgeAdapter.updateBadges(acquiredBadges)
        }
    }

    private fun showBadgeError(message: String) {
        badgeErrorText.text = message
        badgeErrorText.visibility = View.VISIBLE
    }

    private fun hideBadgeError() {
        badgeErrorText.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        val userUuid = intent.getStringExtra("userUuid")

        launch {
            if (userUuid.isNullOrBlank()) {
                Log.e("API_CALL", "사용자 UUID가 전달되지 않았습니다.")
                return@launch
            }
            val sessionStats = fetchSessionStats(userUuid)
            acquireBadgesFromRecentSessions(userUuid, sessionStats?.records.orEmpty())
            fetchAllBadges(userUuid, sessionStats)
        }
    }

    private suspend fun fetchSessionStats(userUuid: String): SessionStats? {
        return withContext(Dispatchers.IO) {
            runCatching { ApiClient.activityApi.getRecentActivities(userUuid, limit = 100) }
                .onFailure { e -> Log.e("API_CALL", "최근 러닝 기록 조회 실패", e) }
                .getOrNull()
                ?.recentActivities
                ?.toBadgeSessionRecords()
                ?.takeIf { it.isNotEmpty() }
                ?.let { SessionStats(it) }
        }
    }

    private suspend fun acquireBadgesFromRecentSessions(
        userUuid: String,
        sessionRecords: List<BadgeSessionRecord>
    ) {
        if (sessionRecords.isEmpty()) return

        withContext(Dispatchers.IO) {
            runCatching {
                ApiClient.userService.acquireBadges(
                    AcquireBadgeRequest(
                        userUuid = userUuid,
                        sessions = sessionRecords
                    )
                )
            }
                .onFailure { e -> Log.e("API_CALL", "배지 자동 획득 호출 실패", e) }
        }
    }

    private fun List<RecentActivity>.toBadgeSessionRecords(): List<BadgeSessionRecord> = mapNotNull { activity ->
        val sessionId = activity.sessionId
        if (sessionId.isBlank()) return@mapNotNull null

        BadgeSessionRecord(
            sessionId = sessionId,
            distanceKm = activity.distance,
            durationSec = activity.durationSeconds,
            date = activity.date
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
