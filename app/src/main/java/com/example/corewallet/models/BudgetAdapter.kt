package com.example.corewallet.models

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import Budget
import com.example.corewallet.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class BudgetAdapter(
    private val budgetPlans: List<Budget>,
    private val onMoreOptionsClick: (View, Budget) -> Unit,
    private val onItemClicked: (Budget) -> Unit
) : RecyclerView.Adapter<BudgetAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.ItemtvMonthSpendingsTitle1)
        val endDate: TextView = itemView.findViewById(R.id.ItemtvEndDate1)
        val category: TextView = itemView.findViewById(R.id.ItemtvBudgetCategoryName1)
        val amount: TextView = itemView.findViewById(R.id.ItemtvBudgetAmount1)
        val progressBar: ProgressBar = itemView.findViewById(R.id.ItemprogressBar1)
        val btnMoreOptions: View = itemView.findViewById(R.id.btnOverflow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_core_budget, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = budgetPlans.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val plan = budgetPlans[position]
        holder.title.text = plan.budget_name.uppercase(Locale.getDefault())
        holder.endDate.text = run {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            try {
                val date = inputFormat.parse(plan.end_date)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                ""
            }
        }

        holder.category.text = plan.category_name
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val spent = plan.spent_amount
        val limit = plan.amount_limit
        holder.amount.text = "${formatter.format(spent)} / ${formatter.format(limit)}"
        val progress = if (limit == 0L) 0 else ((spent * 100) / limit).toInt().coerceAtMost(100)
        holder.progressBar.progress = progress

        if (spent > limit) {
            holder.progressBar.progressDrawable = ContextCompat.getDrawable(
                holder.itemView.context, R.drawable.progress_bar_full
            )
            holder.amount.setTextColor(Color.RED)
            holder.amount.setTypeface(null, Typeface.BOLD)
        } else {
            holder.amount.setTextColor(Color.BLACK)
            holder.amount.setTypeface(null, Typeface.NORMAL)
        }

        holder.itemView.setOnClickListener { onItemClicked(plan) }
        holder.btnMoreOptions.setOnClickListener { onMoreOptionsClick(it, plan) }
    }
}
