/*package com.pack.myapplication.model

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import kotlinx.coroutines.*
import java.io.IOException
import com.pack.myapplication.model.Geocoding
import com.pack.myapplication.model.Address
import com.pack.myapplication.model.Status
// ViewBinding을 사용한다고 가정하고, UI 요소는 간소화했습니다.
class SetCoordsActivity : AppCompatActivity(), OnMapReadyCallback, NaverMap.OnCameraChangeListener, NaverMap.OnCameraIdleListener {

    private lateinit var naverMap: NaverMap

    // UI 요소 (Swift의 textField 대체)
    private lateinit var searchTextField: EditText

    // Swift의 map 대체
    private lateinit var mapView: MapView

    // Swift의 centerMarker 대체
    private val centerMarker = Marker()

    // Swift의 startPointCoords 대체 (지도의 중심 좌표)
    private var startPointCoords: String = ""

    // Swift의 targetPointCoords 대체 (검색으로 얻은 목표 좌표)
    private var targetPointCoords: String = ""

    private val coroutineScope = MainScope() // 코루틴 스코프

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 레이아웃 XML 파일을 설정하는 부분 (R.layout.activity_set_coords 가정)
        // setContentView(R.layout.activity_set_coords)

        // 뷰 객체 설정
        // searchTextField = findViewById<EditText>(R.id.search_text_field)
        // searchButton = findViewById<Button>(R.id.search_button)
        // searchButton.setOnClickListener { searchButtonTapped() }

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this) // 지도 객체 비동기 호출 시작
    }

    // NaverMap 객체가 준비되면 호출되는 콜백
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        // Swift의 addCameraDelegate(delegate: self) 대체
        naverMap.addOnCameraChangeListener(this)
        naverMap.addOnCameraIdleListener(this)

        // 센터 마크 설정 (지도 중앙에 마커 표시)
        centerMarker.iconTintColor = android.graphics.Color.RED // 마커 색상 설정
        centerMarker.map = naverMap // 마커를 지도에 추가

        // 초기 중심 좌표 설정
        centerMarker.position = naverMap.cameraPosition.target
        mapViewCameraIdle(naverMap) // 초기 좌표 저장
    }

    // Swift의 func geoCallRequest(query: String, completion: @escaping (Data) -> Void) 대체
    private suspend fun geoCallRequest(query: String): Result<String> {
        // 이 부분은 이전 답변에서 변환한 OkHttp 코드를 사용합니다.
        // Geocoding API 호출에 맞게 엔드포인트와 파라미터를 수정해야 합니다.
        // 현재는 예시로 더미 Result를 반환합니다.

        // 실제 Geocoding API 엔드포인트: map-geocode/v2/geocode
        return Result.success("""
            {"status": {"code": 200, "name": "OK"}, "addresses": [{"roadAddress": "...", "x": "127.000", "y": "37.000"}]}
        """.trimIndent())
    }

    // Swift의 @objc func searchButtonTapped() 대체
    fun searchButtonTapped() {
        val query = searchTextField.text?.toString()
        if (query.isNullOrEmpty()) {
            Toast.makeText(this, "Query is empty", Toast.LENGTH_SHORT).show()
            return
        }

        // 비동기 Coroutine 실행 (Swift의 클로저를 대체)
        coroutineScope.launch {
            try {
                // 1. 네트워크 호출 (geoCallRequest 함수는 suspend 함수여야 함)
                val result = geoCallRequest(query)

                result.onSuccess { dataString ->
                    // 2. JSON 파싱
                    val decodedData = parseGeocodingJson(dataString) // JSON 파싱 함수 필요

                    val lastData = decodedData.addresses.firstOrNull()

                    if (lastData != null) {
                        // 3. targetPointCoords 업데이트
                        targetPointCoords = "${lastData.x},${lastData.y}"

                        // 4. 지도 카메라 이동 (필요하다면)
                        val targetLatLng = LatLng(lastData.y.toDouble(), lastData.x.toDouble())
                        val cameraUpdate = CameraUpdate.scrollTo(targetLatLng).animate(CameraAnimation.Easing)
                        naverMap.moveCamera(cameraUpdate)

                        println("Target Coords updated: $targetPointCoords")
                    } else {
                        Toast.makeText(this@SetCoordsActivity, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }.onFailure { error ->
                    // 네트워크 실패 처리
                    println("Network Error: $error")
                }
            } catch (e: Exception) {
                println("Decoding Error: $e")
            }
        }
    }

    // MARK: - Naver Map Camera Delegate Methods (카메라 델리게이트 대체)

    // Swift의 mapView(_:cameraIsChangingByReason:) 대체
    override fun onCameraChange(reason: Int, animated: Boolean) {
        centerMarker.position = naverMap.cameraPosition.target
    }

    // Swift의 mapView(_:cameraDidChangeByReason:animated:) 대체 (Kotlin SDK에는 이와 동일한 역할의 콜백은 자주 사용되지 않으며, onCameraChange로 처리 가능)
    // Naver Map Android SDK에서는 onCameraChange가 이동 중 변경 사항을 처리합니다.

    // Swift의 mapViewCameraIdle(_:) 대체
    override fun onCameraIdle() {
        println("Map Center: ${naverMap.cameraPosition.target}")
        val target = naverMap.cameraPosition.target
        // 경도(lng, x) 먼저, 위도(lat, y) 나중에 저장 (API 형식에 맞춤)
        startPointCoords = "${target.longitude},${target.latitude}"
        println("Start Coords updated: $startPointCoords")
    }

    // 액티비티 생명주기 메서드 오버라이드 (지도 뷰 필수)
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }
    // ... onResume, onPause, onStop, onDestroy, onLowMemory, onSaveInstanceState 모두 오버라이드 필요

    // JSON 문자열을 Geocoding 객체로 파싱하는 임시 함수 (실제로는 Gson/Moshi 사용)
    private fun parseGeocodingJson(jsonString: String): Geocoding {
        // 실제 프로젝트에서는 Gson, Moshi 또는 kotlinx.serialization을 사용하여 안전하게 디코딩해야 합니다.
        // 현재는 더미 데이터를 반환한다고 가정합니다.
        return Geocoding(
            status = Status(200, "OK", ""),
            addresses = listOf(
                Address(
                    roadAddress = "테스트 주소",
                    jibunAddress = "테스트 지번",
                    x = "127.123456", // 임의 경도
                    y = "37.123456" // 임의 위도
                )
            )
        )
    }
}
// SetCoordsActivity.kt 파일 내용 (가장 아랫부분에 추가)

// ... (SetCoordsActivity 클래스 정의 끝) ...

// MARK: - Geocoding Data Models (JSON 파싱을 위한 사용자 정의 모델)

// Geocoding API 응답 구조
data class Geocoding(
    val status: Status,
    val addresses: List<Address>
)

data class Status(
    val code: Int,
    val name: String,
    val message: String
)

data class Address(
    val roadAddress: String,
    val jibunAddress: String,
    val x: String, // 경도 (Longitude)
    val y: String  // 위도 (Latitude)
)*/