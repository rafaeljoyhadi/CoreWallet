package com.example.corewallet.view.coresavings.coregoal

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.example.corewallet.R
import com.example.corewallet.api.ApiClient
import com.example.corewallet.api.request.UpdateGoalRequest
import com.example.corewallet.view.coresavings.corebudget.CoreBudget
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.util.*

class CoreGoalEdit : AppCompatActivity() {

    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDay = 0

    private lateinit var etGoalName: EditText
    private lateinit var etTargetAmount: EditText
    private lateinit var tvDeadline: TextView
    private var goalId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_goal_edit)
        window.statusBarColor = Color.parseColor("#0d5892")

        initUIReferences()
        getIntentExtrasAndPopulate()
        setupDatePicker()
        setupBackButton()
        setupDeleteButton()
        setupSaveButton()
    }

    private fun initUIReferences() {
        etGoalName     = findViewById(R.id.etGoalPlanName)
        etTargetAmount = findViewById(R.id.etGoalAmount)
        tvDeadline     = findViewById(R.id.tvTargetEditDate)
    }

    private fun getIntentExtrasAndPopulate() {
        goalId        = intent.getIntExtra("id_goal", 0)
        val name      = intent.getStringExtra("goalName") ?: ""
        val target    = intent.getLongExtra("targetAmount", 0L)
        val deadline  = intent.getStringExtra("deadline") ?: ""

        etGoalName.setText(name)
        etTargetAmount.setText(target.toString())
        tvDeadline.text = formatDisplayDate(deadline)

        deadline.substringBefore("T").split("-").let {
            if (it.size == 3) {
                selectedYear = it[0].toInt()
                selectedMonth = it[1].toInt() - 1
                selectedDay = it[2].toInt()
            } else {
                val cal = Calendar.getInstance()
                selectedYear = cal.get(Calendar.YEAR)
                selectedMonth = cal.get(Calendar.MONTH)
                selectedDay = cal.get(Calendar.DAY_OF_MONTH)
            }
        }
    }

    private fun setupDatePicker() {
        val btnPickDate = findViewById<ImageButton>(R.id.buttonTargetEditDate)
        btnPickDate.setOnClickListener {
            DatePickerDialog(this, { _, y, m, d ->
                selectedYear = y
                selectedMonth = m
                selectedDay = d
                tvDeadline.text = formatDisplayDate(d, m, y)
                tvDeadline.setTextColor(resources.getColor(android.R.color.holo_green_dark))
            }, selectedYear, selectedMonth, selectedDay).show()
        }
    }

    private fun setupBackButton() {
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    private fun setupDeleteButton() {
        val btnDelete = findViewById<Button>(R.id.btnDeleteGoal)
        btnDelete.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                try {
                    val resp = ApiClient.apiService.deleteGoal(goalId)
                    if (resp.isSuccessful) {
                        showToast("Goal deleted")
                        Handler(Looper.getMainLooper()).postDelayed({
                            startActivity(Intent(this@CoreGoalEdit, CoreGoal::class.java))
                            finish()
                        }, 500)
                    } else {
                        showToast("Delete failed")
                    }
                } catch (e: Exception) {
                    showToast("Network error")
                }
            }
        }
    }

    private fun setupSaveButton() {
        val btnSave = findViewById<Button>(R.id.btnConfirmGoal)
        btnSave.setOnClickListener {
            val name = etGoalName.text.toString().trim()
            val target = etTargetAmount.text.toString().toLongOrNull() ?: 0L

            if (name.isBlank()) {
                showToast("Nama goal harus diisi")
                return@setOnClickListener
            }
            if (target <= 0L) {
                showToast("Target harus lebih dari 0")
                return@setOnClickListener
            }

            val deadlineIso = "%04d-%02d-%02d".format(selectedYear, selectedMonth + 1, selectedDay)
            val req = UpdateGoalRequest(name, target, deadlineIso)

            lifecycleScope.launchWhenStarted {
                try {
                    val resp = ApiClient.apiService.updateGoal(goalId, req)
                    if (resp.isSuccessful) {
                        showMotionToast("Berhasil!", "Goal baru dibuat", MotionToastStyle.SUCCESS)
                        Handler(Looper.getMainLooper()).postDelayed({
                            startActivity(Intent(this@CoreGoalEdit, CoreBudget::class.java))
                            finish()
                        }, 1500)
                    } else {
                        showMotionToast("Error!", "Update Failed!", MotionToastStyle.ERROR)
                    }
                } catch (e: Exception) {
                    showMotionToast("Error!", "Network Error!", MotionToastStyle.INFO)
                }
            }
        }
    }

    // Helpers
    private fun formatDisplayDate(iso: String): String = iso.substringBefore("T").split("-").let {
        if (it.size == 3) "%02d/%02d/%04d".format(it[2].toInt(), it[1].toInt(), it[0].toInt()) else iso
    }

    private fun formatDisplayDate(day: Int, month: Int, year: Int): String =
        "%02d/%02d/%04d".format(day, month + 1, year)

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showMotionToast(title: String, message: String, style: MotionToastStyle) {
        MotionToast.createColorToast(
            this,
            title,
            message,
            style,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular)
        )
    }
}
