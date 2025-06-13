package com.example.corewallet.models

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.corewallet.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class GoalAdapter(
    private val goalPlans: List<Goal>,
    private val onMoreOptionsClick: (View, Goal) -> Unit,
    private val onItemClicked: (Goal) -> Unit
) : RecyclerView.Adapter<GoalAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.ItemtvTitle1)
        val targetDate = itemView.findViewById<TextView>(R.id.ItemtvTargetDate1)
        val amount = itemView.findViewById<TextView>(R.id.goalAmount1)
        val progressBar = itemView.findViewById<ProgressBar>(R.id.progressBar1)
        val btnMoreOptions = itemView.findViewById<ImageButton>(R.id.btnOverflow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_core_goal, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = goalPlans.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val goal = goalPlans[position]
        holder.title.text = goal.goal_name.uppercase()
        holder.targetDate.text = formatDate(goal.deadline)
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        holder.amount.text = "${formatter.format(goal.saved_amount)} / ${formatter.format(goal.target_amount)}"
        val progress = if (goal.target_amount == 0L) 0 else
            ((goal.saved_amount.toDouble() / goal.target_amount.toDouble()) * 100).toInt()
        holder.progressBar.progress = progress.coerceAtMost(100)

        if (goal.status == 1 || goal.saved_amount >= goal.target_amount) {
            holder.progressBar.progressDrawable =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.progress_bar)
            holder.amount.setTextColor(Color.parseColor("#008000")) // Hijau untuk selesai
            holder.amount.setTypeface(null, Typeface.BOLD)
        } else {
            holder.amount.setTextColor(Color.BLACK)
            holder.amount.setTypeface(null, Typeface.NORMAL)
        }

        holder.itemView.setOnClickListener {
            onItemClicked(goal)
        }
        holder.btnMoreOptions.setOnClickListener {
            onMoreOptionsClick(it, goal)
        }
    }

    private fun formatDate(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return outputFormat.format(inputFormat.parse(date)!!)
    }
}