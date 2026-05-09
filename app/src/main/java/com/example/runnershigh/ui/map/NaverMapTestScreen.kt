package com.example.runnershigh.ui.map  // ← 네 프로젝트 패키지에 맞게 확인!

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback

/**
 * 🔹 가장 기본적인 네이버 지도 테스트 화면
 *  - 일단 지도만 제대로 뜨는지 확인하기 위한 용도
 */
@Composable
fun NaverMapTestScreen() {
    // 아까 만든 유틸: Compose 수명주기와 MapView를 연결해줌
    val mapView = rememberMapViewWithLifecycle()

    // getMapAsync가 여러 번 호출되는 걸 막기 위한 플래그
    var isMapInitialized by remember { mutableStateOf(false) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { mapView },
        update = { view ->
            if (!isMapInitialized) {
                view.getMapAsync(object : OnMapReadyCallback {
                    override fun onMapReady(naverMap: NaverMap) {
                        isMapInitialized = true

                        // ⭐ 여기서 지도의 초기 상태를 설정
                        //    (일단은 서울 시청 기준으로 카메라 이동)
                        val seoulCityHall = LatLng(37.5665, 126.9780)
                        val cameraUpdate = CameraUpdate.scrollTo(seoulCityHall)
                        naverMap.moveCamera(cameraUpdate)

                        // 줌 버튼 / 현재 위치 버튼 표시
                        naverMap.uiSettings.isZoomControlEnabled = true
                        naverMap.uiSettings.isLocationButtonEnabled = true
                    }
                })
            }
        }
    )
}
