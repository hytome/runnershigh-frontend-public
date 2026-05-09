package com.example.runnershigh.wear

import androidx.wear.protolayout.ActionBuilders.AndroidActivity
import androidx.wear.protolayout.ActionBuilders.LaunchAction
import androidx.wear.protolayout.LayoutElementBuilders.Column
import androidx.wear.protolayout.LayoutElementBuilders.Layout
import androidx.wear.protolayout.LayoutElementBuilders.Text
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.material.Button
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.TileService
import androidx.wear.tiles.ResourceBuilders
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

class HealthTileService : TileService() {

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<Tile> {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val exerciseStart = prefs.getLong(KEY_EXERCISE_START_TIME, System.currentTimeMillis())
        val bpm = prefs.getInt(KEY_CURRENT_BPM, 0)
        val elapsedSeconds = ((System.currentTimeMillis() - exerciseStart) / 1000L).toInt().coerceAtLeast(0)

        val launchAction = LaunchAction.Builder()
            .setAndroidActivity(
                AndroidActivity.Builder()
                    .setPackageName(packageName)
                    .setClassName("$packageName.MainActivity")
                    .build()
            )
            .build()

        val content = Column.Builder()
            .addContent(
                Text.Builder()
                    .setText(getString(R.string.tile_heart_rate, if (bpm > 0) "$bpm bpm" else getString(R.string.tile_heart_rate_waiting)))
                    .build()
            )
            .addContent(
                Text.Builder()
                    .setText(getString(R.string.tile_exercise_time, formatSeconds(elapsedSeconds)))
                    .build()
            )
            .addContent(
                Button.Builder(
                    this,
                    ModifiersBuilders.Clickable.Builder()
                        .setId("open_app")
                        .setOnClick(launchAction)
                        .build()
                )
                    .setTextContent(getString(R.string.tile_open_app))
                    .build()
            )
            .build()

        val tile = Tile.Builder()
            .setResourcesVersion("1")
            .setFreshnessIntervalMillis(30_000)
            .setTileTimeline(
                TimelineBuilders.Timeline.Builder()
                    .addTimelineEntry(
                        TimelineBuilders.TimelineEntry.Builder()
                            .setLayout(
                                Layout.Builder()
                                    .setRoot(
                                        PrimaryLayout.Builder(requestParams.deviceConfiguration)
                                            .setContent(content)
                                            .build()
                                    )
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .build()

        return Futures.immediateFuture(tile)
    }

    override fun onResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<ResourceBuilders.Resources> {
        return Futures.immediateFuture(ResourceBuilders.Resources.Builder().setVersion("1").build())
    }

    private fun formatSeconds(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    companion object {
        const val PREFS_NAME = "wear_health_tile"
        const val KEY_CURRENT_BPM = "current_bpm"
        const val KEY_EXERCISE_START_TIME = "exercise_start_time"
    }
}
