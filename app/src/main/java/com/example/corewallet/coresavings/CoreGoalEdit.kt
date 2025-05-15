package com.example.corewallet.coresavings

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.corewallet.R
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.util.Calendar

class CoreGoalEdit : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_goal_edit)
        window.statusBarColor = Color.parseColor("#0d5892")

        // Inisialisasi kalender untuk tanggal default
        val calendar = Calendar.getInstance()
        var selectedYear = calendar.get(Calendar.YEAR)
        var selectedMonth = calendar.get(Calendar.MONTH)
        var selectedDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Mengakses elemen UI
        val targetDate = findViewById<View>(R.id.buttonTargetEditDate)
        val tvDeadline = findViewById<TextView>(R.id.tvTargetEditDate)

        // Listener Start Date
        targetDate.setOnClickListener {
            // Tampilkan DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    // Simpan tanggal yang dipilih
                    selectedYear = year
                    selectedMonth = month
                    selectedDay = dayOfMonth

                    // Format tanggal ke string
                    val formattedDate = "$dayOfMonth/${month + 1}/$year"

                    // Tampilkan tanggal di TextView
                    tvDeadline.text = "Tanggal terpilih: $formattedDate"
                    tvDeadline.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                },
                selectedYear,
                selectedMonth,
                selectedDay
            )
            datePickerDialog.show()
        }

        // Terima data dari Server
        val id = intent.getIntExtra("id_goal", 0)
        val goalName = intent.getStringExtra("goalName")
        val targetAmount = intent.getStringExtra("targetAmount")
        val deadline = intent.getStringExtra("deadline")

        //Isi data ke dalam view
        val tvGoalName = findViewById<TextView>(R.id.etGoalPlanName)
        val tvTargetAmount = findViewById<TextView>(R.id.etGoalAmount)
        tvGoalName.text = goalName
        tvTargetAmount.text = targetAmount
        tvDeadline.text = deadline

        // Tombol back
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressed() // Simulasikan tombol back
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        //Tombol Delete
        val btnDelete = findViewById<Button>(R.id.btnDeleteGoal)
        btnDelete.setOnClickListener {
            MotionToast.createColorToast(
                this, "Hapus Berhasil!", "Data berhasil dihapus",
                MotionToastStyle.ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular)
            )

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, CoreBudget::class.java)
                startActivity(intent)
            }, 2000)
        }

        // Tombol Simpan
        val btnSave = findViewById<TextView>(R.id.btnConfirmGoal)
        btnSave.setOnClickListener {
            MotionToast.createColorToast(
                this, "Upload Berhasil!", "Data berhasil disimpan",
                MotionToastStyle.SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular)
            )

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, CoreBudget::class.java)
                startActivity(intent)
            }, 2000)
        }

    }

}