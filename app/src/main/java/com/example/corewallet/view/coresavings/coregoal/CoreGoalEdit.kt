package com.example.corewallet.view.coresavings.coregoal

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.example.corewallet.R
import com.example.corewallet.api.ApiClient
import com.example.corewallet.api.request.UpdateGoalRequest
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.util.Calendar

class CoreGoalEdit : AppCompatActivity() {

    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDay = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_goal_edit)
        window.statusBarColor = Color.parseColor("#0d5892")

        // UI refs
        val etGoalName     = findViewById<EditText>(R.id.etGoalPlanName)
        val etTargetAmount = findViewById<EditText>(R.id.etGoalAmount)
        val tvDeadline     = findViewById<TextView>(R.id.tvTargetEditDate)
        val btnPickDate    = findViewById<ImageButton>(R.id.buttonTargetEditDate)
        val btnSave        = findViewById<Button>(R.id.btnConfirmGoal)
        val btnDelete      = findViewById<Button>(R.id.btnDeleteGoal)
        val btnBack        = findViewById<ImageView>(R.id.btnBack)

        // Receive data from intent
        val goalId        = intent.getIntExtra("id_goal", 0)
        val initialName   = intent.getStringExtra("goalName") ?: ""
        val initialTarget = intent.getLongExtra("targetAmount", 0L)
        val initialDeadlineIso = intent.getStringExtra("deadline") ?: ""

        // Populate initial values
        etGoalName.setText(initialName)
        etTargetAmount.setText(initialTarget.toString())
        tvDeadline.text = formatDisplayDate(initialDeadlineIso)

        // Parse initial deadline
        initialDeadlineIso.substringBefore("T").split("-").let {
            if (it.size == 3) {
                selectedYear  = it[0].toInt()
                selectedMonth = it[1].toInt() - 1
                selectedDay   = it[2].toInt()
            } else {
                val cal = Calendar.getInstance()
                selectedYear  = cal.get(Calendar.YEAR)
                selectedMonth = cal.get(Calendar.MONTH)
                selectedDay   = cal.get(Calendar.DAY_OF_MONTH)
            }
        }

        // Date picker for deadline
        btnPickDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    selectedYear = y
                    selectedMonth = m
                    selectedDay = d
                    tvDeadline.text = formatDisplayDate(d, m, y)
                },
                selectedYear,
                selectedMonth,
                selectedDay
            ).show()
        }

        // Back
        btnBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        // Delete
        btnDelete.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                try {
                    val resp = ApiClient.apiService.deleteGoal(goalId)
                    if (resp.isSuccessful) {
                        Toast.makeText(this@CoreGoalEdit, "Goal deleted", Toast.LENGTH_SHORT).show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            startActivity(Intent(this@CoreGoalEdit, CoreGoal::class.java))
                            finish()
                        }, 500)
                    } else {
                        Toast.makeText(this@CoreGoalEdit, "Delete failed", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@CoreGoalEdit, "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Save updates
        btnSave.setOnClickListener {
            val name = etGoalName.text.toString().trim()
            val target = etTargetAmount.text.toString().toLongOrNull() ?: 0L

            if (name.isBlank()) {
                Toast.makeText(this, "Nama goal harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (target <= 0L) {
                Toast.makeText(this, "Target harus lebih dari 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val deadlineIso = "%04d-%02d-%02d".format(selectedYear, selectedMonth + 1, selectedDay)

            val req = UpdateGoalRequest(
                goalName = name,
                targetAmount = target,
                deadline = deadlineIso
            )

            lifecycleScope.launchWhenStarted {
                try {
                    val resp = ApiClient.apiService.updateGoal(goalId, req)
                    if (resp.isSuccessful) {
                        MotionToast.createColorToast(
                            this@CoreGoalEdit,
                            "Berhasil!",
                            "Goal baru dibuat",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(
                                this@CoreGoalEdit,
                                www.sanju.motiontoast.R.font.helvetica_regular
                            )
                        )
                        Handler(Looper.getMainLooper()).postDelayed({
                            startActivity(Intent(this@CoreGoalEdit, CoreGoal
                            ::class.java))
                            finish()
                        }, 1500)
                    } else {
                        MotionToast.createColorToast(
                            this@CoreGoalEdit,
                            "Error!",
                            "Update Failed!",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(
                                this@CoreGoalEdit,
                                www.sanju.motiontoast.R.font.helvetica_regular
                            )
                        )
                    }
                } catch (e: Exception) {
                    MotionToast.createColorToast(
                        this@CoreGoalEdit,
                        "Error!",
                        "Network Error!",
                        MotionToastStyle.INFO,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(
                            this@CoreGoalEdit,
                            www.sanju.motiontoast.R.font.helvetica_regular
                        )
                    )
                }
            }
        }
    }

    // Helpers
    private fun formatDisplayDate(iso: String): String = iso.substringBefore("T").split("-").let {
        if (it.size == 3) "%02d/%02d/%04d".format(it[2].toInt(), it[1].toInt(), it[0].toInt()) else iso
    }
    private fun formatDisplayDate(day: Int, month: Int, year: Int): String = "%02d/%02d/%04d".format(day, month+1, year)
}
