package com.example.corewallet.coresavings

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.corewallet.R
import com.example.corewallet.models.Goal
import com.example.corewallet.models.GoalAdapter


class CoreGoal : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var GoalAdapter: GoalAdapter

    private fun showCustomPopup(anchorView: View, goal: Goal) {
        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.popup_menu_core_savings, null)
        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.elevation = 30f
        popupWindow.showAsDropDown(anchorView, -178, -5, Gravity.START)

        popupView.findViewById<View>(R.id.itemEdit).setOnClickListener {
            val data = HashMap<String, Any>()
            data["id_goal"] = goal.id_goal
            data["goalName"] = goal.goal_name
            data["targetAmount"] = "Rp. ${goal.target_amount}"
            data["progress"] = ((goal.saved_amount.toFloat() / goal.target_amount) * 100).toInt()

            // Kirim tanggal mulai dan akhir ke halaman edit
            data["deadline"] = goal.deadline

            navigateToCoreGoalEdit(this, CoreGoalEdit::class.java, data)
        }

        popupView.findViewById<View>(R.id.itemDelete).setOnClickListener {
            Toast.makeText(this, "Delete clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }
    }

    private fun navigateToCoreGoalEdit(activity: AppCompatActivity, nextActivityClass: Class<*>, data: HashMap<String, Any>) {
        // Membuat Intent untuk membuka aktivitas berikutnya
        val intent = Intent(activity, nextActivityClass)

        // Menambahkan data ke Intent
        for ((key, value) in data) {
            when (value) {
                is String -> intent.putExtra(key, value)
                is Int -> intent.putExtra(key, value)
                is Boolean -> intent.putExtra(key, value)
                is Float -> intent.putExtra(key, value)
                is Double -> intent.putExtra(key, value)
                else -> throw IllegalArgumentException("Unsupported data type for key: $key")
            }
        }
        activity.startActivity(intent)
    }

    private fun navigateToCoreGoalDetail(goal: Goal) {
        val intent = Intent(this, CoreGoalDetail::class.java)
        intent.putExtra("id_goal", goal.id_goal)
        intent.putExtra("goalName", goal.goal_name)
        intent.putExtra("targetAmount", goal.target_amount)
        intent.putExtra("savedAmount", goal.saved_amount)
        intent.putExtra("deadline", goal.deadline)
        startActivity(intent)
    }

    private fun getDummyBudgetGoals(): List<Goal> {
        return listOf(
            Goal(
                id_goal = 1,
                goal_name = "Vacation",
                target_amount = 15000000,
                saved_amount = 170000000,
                deadline = "2025-03-31",
                status = 0
            ),
            Goal(
                id_goal = 2,
                goal_name = "New Laptop",
                target_amount = 20000000,
                saved_amount = 10000000,
                deadline = "2025-06-30",
                status = 0
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_goal)
        window.statusBarColor = Color.parseColor("#0d5892")

        recyclerView = findViewById(R.id.recyclerViewCoreGoal)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Ambil data dummy
        val dummyList = getDummyBudgetGoals()

        // Inisialisasi adapter
        GoalAdapter = GoalAdapter(
            goalPlans = dummyList,
            onMoreOptionsClick = { view, goal ->
                showCustomPopup(view, goal)
            },
            onItemClicked = { goal ->
                navigateToCoreGoalDetail(goal)
            }
        )

        recyclerView.adapter = GoalAdapter

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressed() // Simulasikan tombol back
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        // Button Add Goal
        val btnCoreGoal: TextView = findViewById(R.id.btnAddGoal)
        btnCoreGoal.setOnClickListener {
            val moveIntent = Intent(this@CoreGoal, CoreGoalInput::class.java)
            startActivity(moveIntent)
        }
    }

}