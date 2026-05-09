package com.example.runnershigh.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.naver.maps.map.MapView

/**
 * Compose 환경에서 MapView를 안전하게 사용하기 위한 헬퍼 함수.
 *
 * - 처음 호출될 때 MapView를 한 번 생성해서 remember에 저장하고
 * - 화면 생명주기(START, RESUME, PAUSE, STOP, DESTROY)에 맞춰
 *   MapView의 onStart/onResume/onPause/onStop/onDestroy를 자동으로 호출해 준다.
 */
@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current

    // 1) MapView를 remember로 한 번만 생성해서 재사용
    val mapView = remember {
        MapView(context).apply {
            // onCreate는 여기서 한 번만 호출 (Bundle은 null로)
            onCreate(null)
        }
    }

    // 2) 현재 Compose 화면의 LifecycleOwner 가져오기
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    // 3) DisposableEffect로 lifecycle이 변경될 때 MapView 수명주기 연동
    DisposableEffect(lifecycleOwner, mapView) {
        val lifecycle = lifecycleOwner.lifecycle

        // 라이프사이클 이벤트를 관찰하는 Observer
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> Unit
            }
        }

        // Observer 등록
        lifecycle.addObserver(observer)

        // Composable이 제거될 때 정리 작업
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    return mapView
}