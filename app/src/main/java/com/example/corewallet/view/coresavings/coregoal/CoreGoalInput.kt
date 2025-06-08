package com.example.corewallet.view.coresavings.coregoal

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.example.corewallet.R
import com.example.corewallet.api.ApiClient
import com.example.corewallet.api.request.NewGoalRequest
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.util.*

class CoreGoalInput : AppCompatActivity() {

    private var year = 0
    private var month = 0
    private var day = 0

    private lateinit var etName: EditText
    private lateinit var etTarget: EditText
    private lateinit var tvDeadline: TextView
    private lateinit var btnPick: View
    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_goal_input)
        window.statusBarColor = Color.parseColor("#0d5892")

        initViews()
        initDate()
        setupDatePicker()
        setupBackButton()
        setupSaveButton()
    }

    private fun initViews() {
        etName = findViewById(R.id.etGoalPlanName)
        etTarget = findViewById(R.id.etGoalAmount)
        tvDeadline = findViewById(R.id.tvTargetInputDate)
        btnPick = findViewById(R.id.buttonTargetInputDate)
        btnSave = findViewById(R.id.btnSave)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun initDate() {
        val cal = Calendar.getInstance()
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH)
        day = cal.get(Calendar.DAY_OF_MONTH)

        tvDeadline.text = "%02d/%02d/%04d".format(day, month + 1, year)
    }

    private fun setupDatePicker() {
        btnPick.setOnClickListener {
            DatePickerDialog(this, { _, y, m, d ->
                year = y; month = m; day = d
                tvDeadline.text = "%02d/%02d/%04d".format(d, m + 1, y)
            }, year, month, day).show()
        }
    }

    private fun setupBackButton() {
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    private fun setupSaveButton() {
        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val target = etTarget.text.toString().toLongOrNull() ?: 0L

            if (!validateInputs(name, target)) return@setOnClickListener

            val isoDeadline = "%04d-%02d-%02d".format(year, month + 1, day)
            val req = NewGoalRequest(goalName = name, targetAmount = target, deadline = isoDeadline)

            sendCreateGoalRequest(req)
        }
    }

    private fun validateInputs(name: String, target: Long): Boolean {
        val font = ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular)
        if (name.isBlank()) {
            showToast("Error", "Nama goal harus diisi", MotionToastStyle.ERROR, font)
            return false
        }
        if (target <= 0L) {
            showToast("Error", "Target harus > 0", MotionToastStyle.ERROR, font)
            return false
        }
        return true
    }

    private fun sendCreateGoalRequest(req: NewGoalRequest) {
        val font = ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular)
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.createGoal(req)
                if (response.isSuccessful) {
                    showToast("Success", "Goal berhasil dibuat", MotionToastStyle.SUCCESS, font)
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this@CoreGoalInput, CoreGoal::class.java))
                        finish()
                    }, 1000)
                } else {
                    showToast("Error", "Server error", MotionToastStyle.ERROR, font)
                }
            } catch (e: Exception) {
                showToast("Error", "Network error: ${e.message}", MotionToastStyle.ERROR, font)
            }
        }
    }

    private fun showToast(title: String, message: String, style: MotionToastStyle, font: android.graphics.Typeface?) {
        MotionToast.createColorToast(
            this,
            title, message,
            style, MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            font
        )
    }
}
