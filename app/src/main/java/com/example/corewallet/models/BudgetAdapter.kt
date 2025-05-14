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
import java.util.*

class BudgetAdapter(
    private val budgetPlans: List<Budget>,
    private val onMoreOptionsClick: (View, Budget) -> Unit, // callback untuk popup
    private val onItemClicked: (Budget) -> Unit // callback untuk navigasi ke detail
) : RecyclerView.Adapter<BudgetAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(budget: Budget)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.ItemtvMonthSpendingsTitle1)
        val endDate = itemView.findViewById<TextView>(R.id.ItemtvEndDate1)
        val category = itemView.findViewById<TextView>(R.id.ItemtvBudgetCategoryName1)
        val amount = itemView.findViewById<TextView>(R.id.ItemtvBudgetAmount1)
        val progressBar = itemView.findViewById<ProgressBar>(R.id.ItemprogressBar1)
        val btnMoreOptions = itemView.findViewById<View>(R.id.btnOverflow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_core_budget, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = budgetPlans.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val plan = budgetPlans[position]
        holder.title.text = plan.plan_name.uppercase()
        holder.endDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            .format(SimpleDateFormat("yyyy-MM-dd").parse(plan.end_date)!!)

        val categories = listOf(
            "Top-Up",
            "Food",
            "Shopping",
            "Entertainment",
            "Bills",
            "Transfer",
            "Other/Miscellaneous"
        )

        val categoryIndex = plan.category_number
        holder.category.text = if (categoryIndex in categories.indices) {
            categories[categoryIndex]
        } else {
            "Unknown"
        }

        // Format jumlah uang
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        holder.amount.text = "${formatter.format(plan.spent_amount)} / ${formatter.format(plan.amount_limit)}"

        // Progress bar logic
        val progress = if (plan.amount_limit == 0L) 0 else
            ((plan.spent_amount.toDouble() / plan.amount_limit.toDouble()) * 100).toInt()
        holder.progressBar.progress = progress.coerceAtMost(100)

        // Ganti warna progress bar jika melebihi target
        if (plan.spent_amount > plan.amount_limit) {
            holder.progressBar.progressDrawable =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.progress_bar_full)
            holder.amount.setTextColor(Color.parseColor("#FF0000"))
            holder.amount.setTypeface(null, Typeface.BOLD)
        } else {
            holder.amount.setTextColor(Color.BLACK)
            holder.amount.setTypeface(null, Typeface.NORMAL)
        }

        //Trigger tombol per list
        holder.itemView.setOnClickListener {
            onItemClicked(plan)
        }

        // Trigger tombol popup
        holder.btnMoreOptions.setOnClickListener {
            onMoreOptionsClick(it, plan)
        }
    }
}
