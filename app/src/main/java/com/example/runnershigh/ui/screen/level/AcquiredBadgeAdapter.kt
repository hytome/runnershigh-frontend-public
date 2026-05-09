package com.example.runnershigh.ui.screen.level

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.runnershigh.R   // 🔹 우리 앱의 R 사용
import com.example.runnershigh.data.remote.dto.*

class AcquiredBadgeAdapter(
    private var badges: List<AcquiredBadge>
) : RecyclerView.Adapter<AcquiredBadgeAdapter.BadgeViewHolder>() {

    inner class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val badgeTitle: TextView = itemView.findViewById(R.id.badge_title_8)
        val badgeDescription: TextView = itemView.findViewById(R.id.badge_description_8)
        val badgeDate: TextView = itemView.findViewById(R.id.badge_date_8)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.badge_8, parent, false)   // 🔹 badge_8.xml 레이아웃
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badges[position]
        holder.badgeTitle.text = badge.displayName
        holder.badgeDescription.text = badge.displayDescription
        holder.badgeDate.text = badge.displayAcquiredDate
    }

    override fun getItemCount(): Int = badges.size

    fun updateBadges(newBadges: List<AcquiredBadge>) {
        badges = newBadges
        notifyDataSetChanged()
    }
}
