package com.example.corewallet.view.coresavings.coregoal

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.example.corewallet.R
import com.example.corewallet.api.ApiClient
import com.example.corewallet.api.request.NewGoalRequest
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.util.Calendar


class CoreGoalInput : AppCompatActivity() {

    private var year = 0
    private var month = 0
    private var day = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_goal_input)
        window.statusBarColor = Color.parseColor("#0d5892")

        // initialize to today
        val cal = Calendar.getInstance()
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH)
        day = cal.get(Calendar.DAY_OF_MONTH)

        // UI refs
        val etName     = findViewById<EditText>(R.id.etGoalPlanName)
        val etTarget   = findViewById<EditText>(R.id.etGoalAmount)
        val tvDeadline = findViewById<TextView>(R.id.tvTargetInputDate)
        val btnPick    = findViewById<View>(R.id.buttonTargetInputDate)
        val btnSave    = findViewById<Button>(R.id.btnSave)
        val btnBack    = findViewById<ImageView>(R.id.btnBack)

        // show initial date
        tvDeadline.text = "%02d/%02d/%04d".format(day, month+1, year)

        btnPick.setOnClickListener {
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    year = y; month = m; day = d
                    tvDeadline.text = "%02d/%02d/%04d".format(d, m+1, y)
                },
                year, month, day
            ).show()
        }

        btnBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        btnSave.setOnClickListener {
            val name   = etName.text.toString().trim()
            val target = etTarget.text.toString().toLongOrNull() ?: 0L

            if (name.isBlank()) {
                MotionToast.createColorToast(
                    this, "Error", "Nama goal harus diisi",
                    MotionToastStyle.ERROR, MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular)
                )
                return@setOnClickListener
            }
            if (target <= 0L) {
                MotionToast.createColorToast(
                    this, "Error", "Target harus > 0",
                    MotionToastStyle.ERROR, MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular)
                )
                return@setOnClickListener
            }

            val isoDeadline = "%04d-%02d-%02d".format(year, month+1, day)
            val req = NewGoalRequest(
                goalName = name,
                targetAmount = target,
                deadline = isoDeadline
            )

            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.createGoal(req)
                    if (response.isSuccessful) {
                        MotionToast.createColorToast(
                            this@CoreGoalInput,
                            "Success", "Goal berhasil dibuat",
                            MotionToastStyle.SUCCESS, MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this@CoreGoalInput, www.sanju.motiontoast.R.font.helvetica_regular)
                        )
                        Handler(Looper.getMainLooper()).postDelayed({
                            startActivity(Intent(this@CoreGoalInput, CoreGoal::class.java))
                            finish()
                        }, 1000)
                    } else {
                        MotionToast.createColorToast(
                            this@CoreGoalInput,
                            "Error", "Server error",
                            MotionToastStyle.ERROR, MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this@CoreGoalInput, www.sanju.motiontoast.R.font.helvetica_regular)
                        )
                    }
                } catch (e: Exception) {
                    MotionToast.createColorToast(
                        this@CoreGoalInput,
                        "Error", "Network error: ${e.message}",
                        MotionToastStyle.ERROR, MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this@CoreGoalInput, www.sanju.motiontoast.R.font.helvetica_regular)
                    )
                }
            }

        }
    }
}