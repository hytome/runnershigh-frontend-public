package com.example.runnershigh.util

import android.util.Base64
import com.example.runnershigh.domain.model.GpxLocationPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * GPX 파일 생성 및 Base64 인코딩을 담당한다.
 * 기존 gpxapi 모듈의 로직을 그대로 옮겨와, 실제 측정된 시간/고도를 포함한 GPX를 만든다.
 */
object GpxManager {

    /**
     * 기록된 위치 리스트를 GPX XML 문자열로 변환한다.
     */
    fun createGpxXmlString(
        points: List<GpxLocationPoint>,
        courseName: String
    ): String {
        if (points.isEmpty()) return ""

        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        val creatorName = GpxManager::class.java.name
        sb.append(
            """
            <gpx version="1.1" creator="${creatorName}"
                xmlns="http://www.topografix.com/GPX/1/1"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://www.topografix.com/GPX/1/1
                http://www.topografix.com/GPX/1/1/gpx.xsd">
            """.trimIndent()
        )

        sb.append("<trk>")

        if (courseName.isNotBlank()) {
            sb.append("<name>${courseName}</name>")
        }
        sb.append("<trkseg>")

        points.forEach { point ->
            sb.append("<trkpt lat=\"${point.latitude}\" lon=\"${point.longitude}\">")
            sb.append("<ele>${String.format(Locale.US, "%.2f", point.elevation)}</ele>")
            sb.append("<time>${point.timestampIsoUtc}</time>")
            sb.append("</trkpt>")
        }

        sb.append("</trkseg>")
        sb.append("</trk>")
        sb.append("</gpx>")

        return sb.toString()
    }

    /**
     * XML 문자열을 Base64 로 인코딩한다.
     */
    fun encodeToBase64(xmlContent: String): String {
        return Base64.encodeToString(xmlContent.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }

    /**
     * 현재 시간을 ISO 8601(UTC) 문자열로 반환한다.
     */
    fun getIso8601Time(timeMillis: Long = System.currentTimeMillis()): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(timeMillis))
    }
}
