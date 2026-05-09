package com.example.runnershigh.ui.screen.level

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.runnershigh.R
import com.example.runnershigh.data.remote.dto.*
class LockedBadgeAdapter(
    private var badges: List<Badge>
) : RecyclerView.Adapter<LockedBadgeAdapter.BadgeViewHolder>() {

    class BadgeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.badge_title_1)
        val description: TextView = view.findViewById(R.id.badge_description_1)
        val detail: TextView = view.findViewById(R.id.badge_detail_1)
        val progressText: TextView = view.findViewById(R.id.badge_progress_1)
        val progressBar: ProgressBar = view.findViewById(R.id.badge_progress_bar_1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.badge_1, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badges[position]
        holder.title.text = badge.missionName
        holder.description.text = badge.missionDescription
        holder.detail.text = badge.missionDetail
        holder.progressText.text = badge.progressStatus

        holder.progressBar.max = 100
        holder.progressBar.progress = badge.gaugeRatio
    }

    override fun getItemCount(): Int = badges.size

    fun updateBadges(newBadges: List<Badge>) {
        this.badges = newBadges
        notifyDataSetChanged()
    }
}
