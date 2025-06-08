package com.example.corewallet.view.coresavings.coregoal

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
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.corewallet.R
import com.example.corewallet.api.ApiClient
import com.example.corewallet.models.Goal
import com.example.corewallet.models.GoalAdapter
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle


class CoreGoal : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var goalAdapter: GoalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_goal)
        window.statusBarColor = Color.parseColor("#0d5892")

        recyclerView = findViewById(R.id.recyclerViewCoreGoal)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadGoals()

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        findViewById<TextView>(R.id.btnAddGoal).setOnClickListener {
            startActivity(Intent(this, CoreGoalInput::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadGoals()
    }

    private fun loadGoals() {
        lifecycleScope.launch {
            try {
                val resp = ApiClient.apiService.getGoalPlans()
                if (resp.isSuccessful) {
                    setupAdapter(resp.body() ?: emptyList())
                } else {
                    MotionToast.createColorToast(this@CoreGoal,
                        "Load Failed!",
                        "Failed to load budgets",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this@CoreGoal, www.sanju.motiontoast.R.font.helvetica_regular))
                }
            } catch (e: Exception) {
                MotionToast.createColorToast(this@CoreGoal,
                    "Network Issues",
                    "Network Error",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this@CoreGoal, www.sanju.motiontoast.R.font.helvetica_regular))
            }
        }
    }

    private fun setupAdapter(goals: List<Goal>) {
        goalAdapter = GoalAdapter(
            goalPlans = goals,
            onMoreOptionsClick = { view, goal -> showPopup(view, goal) },
            onItemClicked = { goal -> navigateToDetail(goal) }
        )
        recyclerView.adapter = goalAdapter
    }

    private fun showPopup(anchor: View, goal: Goal) {
        val popup = LayoutInflater.from(this)
            .inflate(R.layout.popup_menu_core_savings, null)
            .let { view ->
                PopupWindow(
                    view,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    true
                ).apply {
                    setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    elevation = 30f
                    showAsDropDown(anchor, -178, -5, Gravity.START)

                    view.findViewById<View>(R.id.itemEdit).setOnClickListener {
                        val data = hashMapOf<String, Any>(
                            "id_goal" to goal.id_goal,
                            "goalName" to goal.goal_name,
                            "targetAmount" to goal.target_amount,
                            "savedAmount" to goal.saved_amount,
                            "deadline" to goal.deadline
                        )
                        navigateToCoreGoalEdit(this@CoreGoal, CoreGoalEdit::class.java, data)
                    }

                    view.findViewById<View>(R.id.itemDelete).setOnClickListener {
                        deleteGoal(goal.id_goal)
                        dismiss()
                    }
                }
            }
    }

    private fun deleteGoal(id: Int) {
        lifecycleScope.launch {
            try {
                val resp = ApiClient.apiService.deleteGoal(id)
                if (resp.isSuccessful) {
                    MotionToast.createColorToast(this@CoreGoal,
                        "Task Completed!",
                        "Goal Deleted",
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this@CoreGoal, www.sanju.motiontoast.R.font.helvetica_regular))
                    loadGoals()
                } else {
                    MotionToast.createColorToast(this@CoreGoal,
                        "Task Failed",
                        "Failed to delete goal",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this@CoreGoal, www.sanju.motiontoast.R.font.helvetica_regular))
                }
            } catch (e: Exception) {
                Toast.makeText(this@CoreGoal, "Network error", Toast.LENGTH_SHORT).show()
                MotionToast.createColorToast(this@CoreGoal,
                    "Network Issue",
                    "Network Error",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this@CoreGoal, www.sanju.motiontoast.R.font.helvetica_regular))
            }
        }
    }

    private fun navigateToCoreGoalEdit(
        activity: AppCompatActivity,
        next: Class<*>,
        data: HashMap<String, Any>
    ) {
        Intent(activity, next).also { intent ->
            data.forEach { (k, v) ->
                when (v) {
                    is Int -> intent.putExtra(k, v)
                    is Long -> intent.putExtra(k, v)
                    is String -> intent.putExtra(k, v)
                    else -> throw IllegalArgumentException("Unsupported type for $k")
                }
            }
            startActivity(intent)
        }
    }

    private fun navigateToDetail(goal: Goal) {
        Intent(this, CoreGoalDetail::class.java).apply {
            putExtra("id_goal", goal.id_goal)
            putExtra("goalName", goal.goal_name)
            putExtra("targetAmount", goal.target_amount)
            putExtra("savedAmount", goal.saved_amount)
            putExtra("deadline", goal.deadline)
            startActivity(this)
        }
    }
}
