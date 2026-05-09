package com.pack.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PolylineOverlay
import android.location.Location
import com.naver.maps.map.CameraAnimation
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // RunDataUploader 인스턴스 선언
    private val runDataUploader = RunDataUploader()

    // UI 요소
    private lateinit var fabStart: FloatingActionButton
    private lateinit var fabStop: FloatingActionButton
    private lateinit var fabPause: FloatingActionButton
    private lateinit var tvDistance: TextView

    // 상태 및 데이터
    private var isMapReady = false
    private var isTracking = false
    private var totalDistance = 0.0
    private var lastLocation: LatLng? = null // 이전 위치 (거리 계산용)

    private var startTimeMillis: Long = 0L // 추적 시작 시간 (밀리초)
    private var totalTimeMillis: Long = 0L // 최종 러닝 시간 (밀리초)

    // 러닝 경로 기록용
    private val pathPoints = mutableListOf<LatLng>() // 지도 Polyline용
    private val runningTrackPoints = mutableListOf<LocationPoint>() // GPX 기록용 (고도, 시간 포함)
    private var currentRunPolyline: PolylineOverlay? = null // 현재 러닝 경로 (녹색)
    private val coursePolylines = mutableListOf<PolylineOverlay>() // 코스 목록 Polyline (파란색)

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val TAG = "MainActivity"

        // 5초 간격으로 위치 업데이트 요청
        private val LOCATION_REQUEST_INTERVAL = TimeUnit.SECONDS.toMillis(5)
    }

    // 위치 업데이트 요청 객체
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = LOCATION_REQUEST_INTERVAL
        fastestInterval = LOCATION_REQUEST_INTERVAL / 2
        priority = Priority.PRIORITY_HIGH_ACCURACY
    }

    // 위치 콜백 (FusedLocationProviderClient 기반의 핵심 추적 로직)
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                val currentLatLng = LatLng(location.latitude, location.longitude)

                // 1. 지도 위치 업데이트 (Naver Location Overlay)
                naverMap.locationOverlay.run {
                    isVisible = true
                    position = currentLatLng
                }

                // 추적 중일 때만 경로 및 거리 계산
                if (isTracking) {
                    // 2. 러닝 경로 기록 및 거리 계산
                    updateRunTrackingData(currentLatLng, location)
                }

                // 5. 마지막 위치 업데이트 (거리 계산용)
                lastLocation = currentLatLng
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // UI 요소 초기화 및 리스너 설정
        fabStart = findViewById(R.id.fabStart)
        fabStop = findViewById(R.id.fabStop)
        fabPause = findViewById(R.id.fabPause)
        tvDistance = findViewById(R.id.tvDistance)

        setupButtonListeners()
        updateUIState(false) // 초기 상태: 시작 버튼만 보임

        // 권한 확인 및 위치 추적 시작 준비
        checkLocationPermission()
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        isMapReady = true

        naverMap.uiSettings.isZoomControlEnabled = true
        naverMap.uiSettings.isLocationButtonEnabled = true
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        checkLocationPermissionAndMoveCamera()
        naverMap.locationOverlay.run {
            anchor = android.graphics.PointF(0.5f, 0.5f)
        }

        // ⭐ 지도 준비 완료 시 서버 코스 목록을 불러와 그립니다.
        loadAndDrawCourseRecords()
    }

    // --- 코스 목록 다운로드 및 지도 표시 로직 ---

    /**
     * 서버에서 코스 목록을 불러와 지도에 Polyline으로 그립니다.
     */
    private fun loadAndDrawCourseRecords() {
        // 기존에 그려진 코스를 제거하여 중복 생성을 방지합니다.
        clearCoursePolylines()

        lifecycleScope.launch {
            Toast.makeText(this@MainActivity, "코스 목록을 불러오는 중...", Toast.LENGTH_SHORT).show()
            val courseList = runDataUploader.getCourseRecords()

            if (courseList != null) {
                if (courseList.isNotEmpty()) {
                    for (course in courseList) {
                        drawCoursePolyline(course)
                    }
                    Toast.makeText(this@MainActivity, "${courseList.size}개의 코스가 지도에 표시되었습니다.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@MainActivity, "서버에 저장된 코스가 없습니다.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this@MainActivity, "코스 목록 불러오기 실패", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * 개별 코스 데이터를 받아 지도에 파란색 Polyline으로 그립니다.
     */
    private fun drawCoursePolyline(courseData: CourseData) {
        // 1. LocationPoint 리스트를 LatLng 리스트로 변환
        // 필드 이름 수정: courseData.points -> courseData.pathPoints
        val latLngList = courseData.pathPoints.map { point ->
            LatLng(point.latitude, point.longitude)
        }

        // 2. 리스트 크기 접근 수정: latLngList.size -> latLngList.size
        if (latLngList.size < 2) {
            // 3. courseId 필드를 사용하려면 CourseData에 정의되어 있어야 함
            Log.w(TAG, "경로 지점 수가 부족하여 코스를 그릴 수 없습니다. (ID: ${courseData.courseId})")
            return
        }

        // 2. 새로운 PolylineOverlay 객체 생성
        val coursePolyline = PolylineOverlay().apply {
            coords = latLngList
            color = Color.BLUE // 코스는 파란색으로 표시
            width = 15
            map = naverMap // 지도에 추가
        }

        // 3. 관리 리스트에 추가 (나중에 제거/관리용)
        coursePolylines.add(coursePolyline)

        Log.d(TAG, "코스 #${courseData.courseId} (${courseData.name}) 지도에 파란색으로 그려짐.")
    }

    /**
     * 지도에 표시된 모든 코스 Polyline을 제거합니다.
     */
    private fun clearCoursePolylines() {
        coursePolylines.forEach { it.map = null }
        coursePolylines.clear()
    }

    // --- 기본 지도/위치 로직 ---

    private fun checkLocationPermissionAndMoveCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            moveCameraToCurrentLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun moveCameraToCurrentLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)

                        val cameraUpdate =
                            CameraUpdate.scrollTo(currentLatLng).animate(CameraAnimation.Easing)
                        naverMap.moveCamera(cameraUpdate)

                        naverMap.locationOverlay.apply {
                            isVisible = true
                            position = currentLatLng
                            bearing = location.bearing
                        }
                    } else {
                        Log.w("Location", "마지막 위치 정보를 가져올 수 없습니다. 임시 위치 유지.")
                    }
                }
        } catch (e: SecurityException) {
            Log.e("Location", "위치 정보를 가져오는 중 보안 예외 발생", e)
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun setupButtonListeners() {
        fabPause.setOnClickListener {
            Toast.makeText(this, "일시정지는 구현되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }

        fabStop.setOnClickListener {
            stopTracking()
        }

        fabStart.setOnClickListener {
            if (!isMapReady) {
                Toast.makeText(this, "지도를 초기화하는 중입니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            showCountdownDialog()
        }
    }

    /**
     * 위치 권한 확인 및 요청
     */
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // 권한이 있으면 위치 업데이트 리스너는 바로 등록 (지도 오버레이용)
            startLocationUpdates(false)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates(false) // 권한 획득 후 즉시 위치 업데이트 시작
            } else {
                Toast.makeText(this, "위치 권한이 거부되었습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * 위치 업데이트 요청 시작
     * @param isForTracking true일 경우에만 isTracking 상태를 true로 설정하고 토스트 메시지 표시
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(isForTracking: Boolean) {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        if (isForTracking) {
            isTracking = true
            Toast.makeText(this, "경로 추적을 시작합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 위치 업데이트 중지
     */
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // --- 러닝 추적 로직 ---

    /**
     * 러닝 경로 기록 및 거리 계산 로직
     */
    private fun updateRunTrackingData(currentLatLng: LatLng, location: Location) {
        // 1. 거리 계산
        lastLocation?.let { last ->
            // Android Location distanceTo 사용 (미터 단위 반환)
            val locationA = Location("last").apply {
                latitude = last.latitude
                longitude = last.longitude
            }
            val locationB = Location("current").apply {
                latitude = currentLatLng.latitude
                longitude = currentLatLng.longitude
            }

            val distance = locationA.distanceTo(locationB).toDouble()
            totalDistance += distance
            updateDistanceUI(totalDistance)
        }

        // 2. 경로 기록 (Polyline용)
        pathPoints.add(currentLatLng)
        if (pathPoints.size >= 2) {
            currentRunPolyline?.coords = pathPoints
        }

        // 3. GPX 기록용 데이터 추가
        val point = LocationPoint(
            latitude = currentLatLng.latitude,
            longitude = currentLatLng.longitude,
            elevation = location.altitude,
            time = GpxManager.getIso8601Time(location.time)
        )
        runningTrackPoints.add(point)
    }

    // --- UI 및 상태 관리 ---

    private fun updateDistanceUI(distance: Double) {
        runOnUiThread {
            val distanceKm = distance / 1000.0
            val distanceText = String.format("%.2f km", distanceKm)
            tvDistance.text = distanceText
        }
    }

    private fun updateUIState(tracking: Boolean) {
        if (tracking) {
            fabStart.visibility = View.INVISIBLE
            fabStop.visibility = View.VISIBLE
            fabPause.visibility = View.VISIBLE
        } else {
            fabStart.visibility = View.VISIBLE
            fabStop.visibility = View.INVISIBLE
            fabPause.visibility = View.INVISIBLE
        }
    }

    private fun prepareAndStartTracking() {
        // 이전 데이터 초기화
        currentRunPolyline?.map = null
        currentRunPolyline = null
        pathPoints.clear()
        runningTrackPoints.clear()
        totalDistance = 0.0
        lastLocation = null

        // 러닝 시간 초기화 및 시작 시간 기록
        totalTimeMillis = 0L
        startTimeMillis = System.currentTimeMillis()

        // 새로운 현재 러닝 경로 Polyline (녹색) 생성
        currentRunPolyline = PolylineOverlay().apply {
            color = Color.GREEN
            width = 20
            this.map = naverMap
        }

        updateDistanceUI(0.0)
        updateUIState(true) // UI 상태 변경 (시작 -> 중지/일시정지)
        startLocationUpdates(true) // 추적 상태로 위치 업데이트 시작
    }

    private fun stopTracking() {
        if (!isTracking) return

        isTracking = false
        stopLocationUpdates() // 위치 업데이트 중지
        updateUIState(false) // UI 상태 변경 (중지 -> 시작)

        // 1. 경로 저장 및 코스 표시
        if (pathPoints.isNotEmpty()) {
            // 총 러닝 시간 계산 및 totalTimeMillis에 저장
            val endTimeMillis = System.currentTimeMillis()
            totalTimeMillis = endTimeMillis - startTimeMillis
            startTimeMillis = 0L // 시작 시간 초기화

            // GPX 생성 및 백엔드 전송 호출
            sendGpxDataToServer(runningTrackPoints, totalDistance, totalTimeMillis)

            // 최종 거리 및 시간 확인 로그
            val distanceKm = totalDistance / 1000.0
            val timeSec = totalTimeMillis / 1000.0
            Log.d(TAG, "최종 거리: ${String.format("%.2f", distanceKm)} km, 최종 시간: ${String.format("%.1f", timeSec)} 초")
        } else {
            Toast.makeText(this, "기록된 위치 데이터가 없어 저장하지 않습니다.", Toast.LENGTH_SHORT).show()
        }

        // 2. 현재 러닝 데이터 및 UI 초기화
        updateDistanceUI(0.0)
        currentRunPolyline?.map = null // 현재 러닝 경로 (녹색) 제거
        currentRunPolyline = null

        // 3. 마커 제거 및 위치 초기화
        naverMap.locationOverlay.isVisible = false
        lastLocation = null // 다음 러닝을 위해 마지막 위치 초기화

        Toast.makeText(this, "경로 추적을 중지합니다.", Toast.LENGTH_SHORT).show()
    }

    /**
     * GPX 데이터를 생성하고 Base64로 인코딩하여 백엔드로 전송합니다.
     */
    private fun sendGpxDataToServer(points: List<LocationPoint>, distance: Double, timeMillis: Long) {
        lifecycleScope.launch {
            try {
                // GpxManager를 통해 GPX XML 콘텐츠 생성
                val gpxXmlContent = GpxManager.createGpxXmlString(points)
                // GpxManager를 통해 Base64 인코딩
                val base64GpxString = GpxManager.encodeToBase64(gpxXmlContent)

                // RunRecordRequest 생성
                val runUploadRequest = RunRecordRequest(
                    userId = "123",
                    name="강정보",
                    distanceMeters = distance,
                    totalTimeMillis = timeMillis, // 최종 러닝 시간 (밀리초)
                    gpxFileBase64 = base64GpxString
                )

                // Uploader를 통해 서버로 데이터 전송
                val uploadSuccess = runDataUploader.uploadRunData(runUploadRequest)

                if (uploadSuccess) {
                    Log.d(TAG, "✅ 러닝 데이터 서버 전송 성공.")
                    Toast.makeText(this@MainActivity, "러닝 기록 서버 전송 완료.", Toast.LENGTH_LONG).show()
                } else {
                    Log.e(TAG, "❌ 러닝 데이터 서버 전송 실패. (응답 오류)")
                    Toast.makeText(this@MainActivity, "러닝 기록 서버 전송 실패.", Toast.LENGTH_LONG).show()
                }

                Log.d(TAG, "GPX 데이터 생성 및 Base64 인코딩 완료. (총 거리: $distance m, 총 시간: ${timeMillis / 1000}초)")

            } catch (e: Exception) {
                Log.e(TAG, "GPX 생성 또는 전송 중 오류 발생", e)
                Toast.makeText(this@MainActivity, "데이터 전송 중 오류 발생: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    // --- Countdown Dialog ---

    private fun showCountdownDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.countdown, null)
        val countdownText = dialogView.findViewById<TextView>(R.id.countdown_text)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(false)
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        var count = 3
        val timer = Handler(Looper.getMainLooper())

        val countdownRunnable = object : Runnable {
            override fun run() {
                if (count > 0) {
                    countdownText.text = count.toString()
                    count--
                    timer.postDelayed(this, 1000)
                } else {
                    countdownText.text = "GO!"
                    timer.postDelayed({
                        dialog.dismiss()
                        onCountdownFinished()
                    }, 500)
                }
            }
        }
        timer.post(countdownRunnable)
    }

    private fun onCountdownFinished() {
        prepareAndStartTracking()
    }

    // --- Lifecycle Overrides ---

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        // 앱이 포그라운드로 돌아오면 위치 업데이트 재개
        if (!isTracking) {
            startLocationUpdates(false)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        // 앱이 배경으로 갈 때 위치 업데이트 중지
        stopLocationUpdates()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}