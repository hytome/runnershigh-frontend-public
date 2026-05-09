package com.example.runnershigh.ui.screen.course

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runnershigh.data.remote.ApiClient
import com.example.runnershigh.data.remote.api.ActivityApi
import com.example.runnershigh.data.remote.dto.RecentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

private const val DEFAULT_RECENT_LIMIT = 3

data class RecentCourseUiState(
    val recentActivities: List<RecentActivity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class RecentCourseViewModel(
    private val activityApi: ActivityApi = ApiClient.activityApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecentCourseUiState())
    val uiState: StateFlow<RecentCourseUiState> = _uiState

    fun loadRecentActivities(userUuid: String, limit: Int = DEFAULT_RECENT_LIMIT) {
        if (userUuid.isBlank()) return
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            runCatching { activityApi.getRecentActivities(userUuid, limit) }
                .onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        recentActivities = response.recentActivities,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                .onFailure { e ->
                    Log.e("RecentCourseViewModel", "Failed to load recent activities", e)
                    val message = when (e) {
                        is HttpException -> {
                            val code = e.code()
                            if (code >= 500) {
                                "서버 오류로 최근 기록을 불러오지 못했습니다. 잠시 후 다시 시도해주세요."
                            } else {
                                "최근 기록을 불러오지 못했습니다 (오류 코드: $code)."
                            }
                        }

                        else -> e.localizedMessage ?: "알 수 없는 오류가 발생했습니다."
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = message
                    )
                }
        }
    }
}
