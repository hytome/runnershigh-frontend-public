package com.pack.myapplication

// GpxManager.kt 파일


import android.util.Base64
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object GpxManager {

    /**
     * LocationPoint 리스트를 GPX 형식의 XML 문자열로 변환합니다.
     * @param points 기록된 모든 위치 데이터
     * @return GPX XML 문자열
     */
    fun createGpxXmlString(points: List<LocationPoint>): String {
        if (points.isEmpty()) return ""

        val sb = StringBuilder()

        // 1. <gpx> 시작 (최상위 요소)
        sb.append("""<?xml version="1.0" encoding="UTF-8"?>""")
        sb.append("""<gpx version="1.1" creator="MyRunningApp" 
                      xmlns="http://www.topografix.com/GPX/1/1" 
                      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                      xsi:schemaLocation="http://www.topografix.com/GPX/1/1 
                      http://www.topografix.com/GPX/1/1/gpx.xsd">""")

        // 2. <trk> 시작 (트랙)
        sb.append("<trk>")
        // 파일 이름에 사용할 시간 포맷 (첫 번째 포인트의 시간 사용)
        val startTime = try {
            points.first().time.substringBefore('T')
        } catch (e: Exception) {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        }
        sb.append("<name>Running Track $startTime</name>")

        // 3. <trkseg> 시작 (트랙 세그먼트)
        sb.append("<trkseg>")

        // 4. <trkpt> 데이터 삽입
        points.forEach { point ->
            sb.append("<trkpt lat=\"${point.latitude}\" lon=\"${point.longitude}\">")
            sb.append("<ele>${String.format(Locale.US, "%.2f", point.elevation)}</ele>") // 고도 소수점 처리
            sb.append("<time>${point.time}</time>")
            sb.append("</trkpt>")
        }

        // 닫는 태그 순서
        sb.append("</trkseg>")
        sb.append("</trk>")
        sb.append("</gpx>")

        return sb.toString()
    }

    /**
     * XML 문자열을 Base64로 인코딩합니다.
     * @param xmlContent GPX XML 문자열
     * @return Base64 인코딩된 문자열
     */
    fun encodeToBase64(xmlContent: String): String {
        // Base64.NO_WRAP: 줄바꿈 문자를 포함하지 않도록 설정
        return Base64.encodeToString(xmlContent.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }

    /**
     * 현재 시간을 ISO 8601 형식 (UTC)으로 포맷합니다.
     */
    fun getIso8601Time(timeMillis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(timeMillis))
    }
}