package com.example.runnershigh.ui.screen.level
//import com.example.runnershigh.data.remote.dto.UserLevel
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import com.example.runnershigh.data.remote.ApiClient
import com.example.runnershigh.ui.screen.level.BadgeActivity // BadgeActivity 경로
import com.example.runnershigh.data.remote.dto.UserIdRequest

import com.example.runnershigh.data.remote.dto.UserLevel
import com.example.runnershigh.data.remote.dto.Mission
import com.example.runnershigh.data.remote.dto.UserCondition
import com.example.runnershigh.R // 리소스 ID 경로
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class LevelActivity : AppCompatActivity(), CoroutineScope { // 파일명과 클래스명 변경됨

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    // 🌟 레벨 UI 요소
    private lateinit var progressBar: ProgressBar
    private lateinit var expText: TextView
    private lateinit var expRemainingText: TextView
    private lateinit var levelTitle: TextView // 레벨 숫자 표시를 위한 TextView (XML에 있다고 가정)

    // 🌟 컨디션 UI 요소
    private lateinit var conditionLevel: TextView

    // 🌟 미션 UI 요소들 (3개 묶음)
    // 미션 1
    private lateinit var lvTitle1: TextView
    private lateinit var lvDescription1: TextView
    private lateinit var lvExp1: TextView
    private lateinit var lvProgress1: TextView
    private lateinit var missionCategory1: TextView
    private lateinit var lvRate1: ProgressBar
    private lateinit var missionLayout1: View
    private lateinit var icCheckImage1: ImageView

    // 미션 2
    private lateinit var lvTitle2: TextView
    private lateinit var lvDescription2: TextView
    private lateinit var lvExp2: TextView
    private lateinit var lvProgress2: TextView
    private lateinit var missionCategory2: TextView
    private lateinit var lvRate2: ProgressBar
    private lateinit var missionLayout2: View
    private lateinit var icCheckImage2: ImageView

    // 미션 3
    private lateinit var lvTitle3: TextView
    private lateinit var lvDescription3: TextView
    private lateinit var lvExp3: TextView
    private lateinit var lvProgress3: TextView
    private lateinit var missionCategory3: TextView
    private lateinit var lvRate3: ProgressBar
    private lateinit var missionLayout3: View
    private lateinit var icCheckImage3: ImageView

    // 레벨 기본값
    private val EXP_PER_LEVEL = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()

        setContentView(R.layout.activity_main) // XML 레이아웃 ID는 그대로 사용
        supportActionBar?.hide()

        window.statusBarColor = Color.parseColor("#CCFF00")

        initViews()
        initMissionViews()

        // 레벨 프로그레스바 최대값 설정
        progressBar.max = EXP_PER_LEVEL

        // Level 카드 클릭 리스너 (BadgeActivity로 이동)
        val clickableLayout = findViewById<LinearLayout>(R.id.linear_layout_clickable)
        clickableLayout.setOnClickListener {
            val badgeIntent = Intent(this, BadgeActivity::class.java)
            badgeIntent.putExtra("userUuid", intent.getStringExtra("userUuid"))
            startActivity(badgeIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        val userUuid = intent.getStringExtra("userUuid")

        if (userUuid.isNullOrBlank()) {
            Log.e("API_CALL", "사용자 UUID가 전달되지 않았습니다.")
            return
        }

        fetchUserLevel(userUuid)
        fetchUserMissions(userUuid)
        fetchUserCondition(userUuid)
    }

    private fun initViews() {
        // 레벨/EXP 카드
        levelTitle = findViewById(R.id.tv_level) // XML에 있는 레벨 텍스트뷰 ID
        progressBar = findViewById(R.id.level_progress_bar)
        expText = findViewById(R.id.level_progress)
        expRemainingText = findViewById(R.id.tv_exp_remaining)

        // 뒤로가기 버튼
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 컨디션 UI 초기화
        conditionLevel = findViewById(R.id.condition_level)
    }

    private fun initMissionViews() {
        // 미션 1 UI 요소 연결 (ID는 XML에 맞게 수정 필요)
        missionLayout1 = findViewById(R.id.lv_title_1)
        lvTitle1 = findViewById(R.id.lv_title_1)
        lvDescription1 = findViewById(R.id.lv_description_1)
        lvExp1 = findViewById(R.id.lv_exp_1)
        lvProgress1 = findViewById(R.id.lv_progress_1)
        lvRate1 = findViewById(R.id.lv_rate_1)
        icCheckImage1 = findViewById(R.id.ic_check_image1)


        // 미션 2 UI 요소 연결 (ID는 XML에 맞게 수정 필요)
        missionLayout2 = findViewById(R.id.lv_title_2)
        lvTitle2 = findViewById(R.id.lv_title_2)
        lvDescription2 = findViewById(R.id.lv_description_2)
        lvExp2 = findViewById(R.id.lv_exp_2)
        lvProgress2 = findViewById(R.id.lv_progress_2)
        lvRate2 = findViewById(R.id.lv_rate_2)
        icCheckImage2 = findViewById(R.id.ic_check_image2)


        // 미션 3 UI 요소 연결 (ID는 XML에 맞게 수정 필요)
        missionLayout3 = findViewById(R.id.lv_title_3)
        lvTitle3 = findViewById(R.id.lv_title_3)
        lvDescription3 = findViewById(R.id.lv_description_3)
        lvExp3 = findViewById(R.id.lv_exp_3)
        lvProgress3 = findViewById(R.id.lv_progress_3)
        lvRate3 = findViewById(R.id.lv_rate_3)
        icCheckImage3 = findViewById(R.id.ic_check_image3)

    }

    // 🔹 API: 유저 레벨 조회
    private fun fetchUserLevel(userUuid: String){
        launch(Dispatchers.IO) {
            try {
                val response =  ApiClient.userService.getUserLevel(UserIdRequest(user_uuid = userUuid))

                if (response.isSuccessful) {
                    val userLevel = response.body()
                    Log.d("API_CALL", "유저 레벨 데이터 성공: $userLevel")

                    withContext(Dispatchers.Main) {
                        updateLevelUI(userLevel)
                    }
                } else {
                    Log.e("API_CALL", "레벨 API 호출 실패: HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_CALL", "레벨 네트워크 오류 발생", e)
            }
        }
    }

    // 🔹 API: 미션 목록 조회
    private fun fetchUserMissions(userUuid: String) {
        launch(Dispatchers.IO) {
            try {
                val response =  ApiClient.userService.getUserMissions(UserIdRequest(user_uuid = userUuid))

                if (response.isSuccessful) {
                    val missions = response.body()
                    Log.d("API_CALL", "미션 데이터 성공: $missions")

                    withContext(Dispatchers.Main) {
                        updateMissionUI(missions)
                    }
                } else {
                    Log.e("API_CALL", "미션 API 호출 실패: HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_CALL", "미션 네트워크 오류 발생", e)
            }
        }
    }

    // 🔹 API: 컨디션 조회
    private fun fetchUserCondition(userUuid: String) {
        launch(Dispatchers.IO) {
            try {
                val response =  ApiClient.userService.getUserCondition(UserIdRequest(user_uuid = userUuid))

                if (response.isSuccessful) {
                    val condition = response.body()
                    Log.d("API_CALL", "컨디션 데이터 성공: $condition")

                    withContext(Dispatchers.Main) {
                        updateConditionUI(condition)
                    }
                } else {
                    Log.e("API_CALL", "컨디션 API 호출 실패: HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_CALL", "컨디션 네트워크 오류 발생", e)
            }
        }
    }

    // 🖼️ UI 업데이트: 레벨
    private fun updateLevelUI(userLevel: UserLevel?) {
        if (userLevel == null) return

        val maxExp = if (userLevel.nextLevelXp > 0) userLevel.nextLevelXp else EXP_PER_LEVEL
        val currentExp = userLevel.currentXp.coerceIn(0, maxExp)
        val remainingExp = (maxExp - currentExp).coerceAtLeast(0)

        progressBar.max = maxExp
        progressBar.progress = currentExp

        levelTitle.text = "Level ${userLevel.level}" // Level 숫자 업데이트
        expText.text = "${currentExp}/${maxExp}"
        expRemainingText.text = remainingExp.toString()
    }

    // 🖼️ UI 업데이트: 미션
    private fun updateMissionUI(missions: List<Mission>?) {
        if (missions.isNullOrEmpty()) {
            Log.w("MISSION_UI", "미션 데이터가 없습니다.")
            // 모든 미션 레이아웃 숨기기 처리
            missionLayout1.visibility = View.GONE
            missionLayout2.visibility = View.GONE
            missionLayout3.visibility = View.GONE
            return
        }

        val missionViews = listOf(
            Triple(lvTitle1, lvDescription1, lvExp1),
            Triple(lvTitle2, lvDescription2, lvExp2),
            Triple(lvTitle3, lvDescription3, lvExp3)
        )

        val progressViews = listOf(
            Triple(lvProgress1, lvRate1, icCheckImage1),
            Triple(lvProgress2, lvRate2, icCheckImage2),
            Triple(lvProgress3, lvRate3, icCheckImage3)
        )

        val missionLayouts = listOf(missionLayout1, missionLayout2, missionLayout3)

        missions.forEachIndexed { index, mission ->
            if (index < 3) { // 최대 3개의 미션만 처리
                val (title, description, exp) = missionViews[index]
                val (progressText, progressBar, checkImage) = progressViews[index]
                val layout = missionLayouts[index]

                title.text = mission.displayTitle()
                description.text = mission.displayDescription()
                progressText.text = mission.progressStatus ?: "${mission.gaugeRatio}%"

                progressBar.max = 100
                progressBar.progress = mission.gaugeRatio
                layout.visibility = View.VISIBLE

                if (mission.isCompleted()) {
                    progressBar.visibility = View.GONE
                    checkImage.visibility = View.VISIBLE
                    exp.text = ""
                } else {
                    progressBar.visibility = View.VISIBLE
                    checkImage.visibility = View.GONE
                    exp.text = "+${mission.rewardXp()}"
                }
            }
        }
        // 나머지 미션 레이아웃 숨기기 (데이터가 3개 미만일 경우)
        for (i in missions.size until 3) {
            if (i < missionLayouts.size) {
                missionLayouts[i].visibility = View.GONE
            }
        }
    }

    // 🖼️ UI 업데이트: 컨디션
    private fun updateConditionUI(condition: UserCondition?) {
        if (condition == null) {
            Log.w("CONDITION_UI", "컨디션 데이터가 없습니다.")
            return
        }
        // TODO: 컨디션 레벨에 따라 색상 변경 로직 추가 가능
        val statusText = condition.todayStatus ?: condition.conditionLevel ?: "컨디션 정보를 불러올 수 없습니다."
        conditionLevel.text = statusText
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}

private fun Mission.displayTitle(): String =
    missionName
        ?: title
        ?: planId
        ?: goalType
        ?: missionDescription
        ?: description
        ?: "오늘의 미션"

private fun Mission.displayDescription(): String {
    val descriptionParts = listOfNotNull(
        missionDescription?.takeIf(String::isNotBlank),
        missionDetail?.takeIf(String::isNotBlank)
    )

    if (descriptionParts.isNotEmpty()) {
        return descriptionParts.joinToString("\n")
    }

    return description
        ?.takeIf(String::isNotBlank)
        ?: "오늘은 표시할 미션이 없습니다."
}

private fun Mission.rewardXp(): Int = xpReward ?: expPoints

private fun Mission.isCompleted(): Boolean = gaugeRatio >= 100 ||
        (progressStatus?.contains("100", ignoreCase = true) == true)
