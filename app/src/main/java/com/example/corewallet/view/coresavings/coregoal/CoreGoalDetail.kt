package com.example.corewallet.view.coresavings.coregoal

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType

import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.corewallet.R
import com.example.corewallet.api.request.AmountRequest
import com.example.corewallet.api.ApiClient
import com.example.corewallet.models.Goal
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class CoreGoalDetail : AppCompatActivity() {

    private var idGoal: Int = 0
    private var targetAmount: Long = 0L
    private var savedAmount: Long = 0L

    private lateinit var tvSaved: TextView
    private lateinit var progressBar: ProgressBar
    private val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_goal_detail)
        window.statusBarColor = Color.parseColor("#0d5892")

        // Receive intent data
        idGoal = intent.getIntExtra("id_goal", 0)
        val goalName = intent.getStringExtra("goalName").orEmpty()
        targetAmount = intent.getLongExtra("targetAmount", 0L)
        savedAmount = intent.getLongExtra("savedAmount", 0L)
        val deadline = intent.getStringExtra("deadline").orEmpty()

        // UI refs
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvDeadline = findViewById<TextView>(R.id.tvDeadline)
        tvSaved = findViewById(R.id.tvSavedAmount)
        val tvTarget = findViewById<TextView>(R.id.tvTargetAmount)
        progressBar = findViewById(R.id.progressBar)
        val btnDeposit = findViewById<Button>(R.id.btnAddMoney)
        val btnWithdraw = findViewById<Button>(R.id.btnWithdraw)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnEdit = findViewById<ImageView>(R.id.btnEditGoal)

        // Static UI
        tvTitle.text = goalName.uppercase(Locale.getDefault())
        tvDeadline.text = formatDate(deadline)
        tvTarget.text = "IDR ${formatter.format(targetAmount)}"

        // Back
        btnBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
        // Edit
        btnEdit.setOnClickListener {
            Intent(this, CoreGoalEdit::class.java).apply {
                putExtra("id_goal", idGoal)
                putExtra("goalName", goalName)
                putExtra("targetAmount", targetAmount)
                putExtra("deadline", deadline)
                startActivity(this)
            }
        }

        // Initial render
        renderUI()

        // Deposit
        btnDeposit.setOnClickListener {
            showAmountDialog("Enter deposit amount") { amt ->
                performTransaction(isDeposit = true, amount = amt)
            }
        }
        // Withdraw
        btnWithdraw.setOnClickListener {
            showAmountDialog("Enter withdrawal amount") { amt ->
                performTransaction(isDeposit = false, amount = amt)
            }
        }
    }

    private fun renderUI() {
        tvSaved.text = "IDR ${formatter.format(savedAmount)}"
        val progress = if (targetAmount == 0L) 0 else
            ((savedAmount.toDouble() / targetAmount.toDouble()) * 100).toInt().coerceAtMost(100)
        progressBar.progress = progress
        if (savedAmount >= targetAmount) {
            progressBar.progressDrawable = ContextCompat.getDrawable(this, R.drawable.progress_bar_full)
            tvSaved.setTextColor(Color.parseColor("#008000"))
        } else {
            progressBar.progressDrawable = ContextCompat.getDrawable(this, R.drawable.progress_bar)
            tvSaved.setTextColor(Color.BLACK)
        }
    }

    private fun performTransaction(isDeposit: Boolean, amount: Long) {
        if (amount <= 0L) return
        lifecycleScope.launch {
            try {
                val resp: Response<Goal> = if (isDeposit) {
                    ApiClient.apiService.depositToGoal(idGoal, AmountRequest(amount))
                } else {
                    ApiClient.apiService.withdrawFromGoal(idGoal, AmountRequest(amount))
                }
                if (resp.isSuccessful) {
                    // Fetch full updated goal to ensure consistency
                    fetchGoalDetail()
                }
            } catch (_: Exception) {}
        }
    }

    private fun fetchGoalDetail() {
        lifecycleScope.launch {
            try {
                val resp: Response<List<Goal>> = ApiClient.apiService.getGoalPlans()
                if (resp.isSuccessful) {
                    val goal = resp.body()?.firstOrNull { it.id_goal == idGoal }
                    goal?.let {
                        savedAmount = it.saved_amount
                        targetAmount = it.target_amount
                        runOnUiThread { renderUI() }
                    }
                }
            } catch (_: Exception) {}
        }
    }

    private fun showAmountDialog(title: String, onConfirm: (Long) -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            hint = "Amount"
        }
        builder.setView(input)
        builder.setPositiveButton("OK") { _, _ ->
            val amt = input.text.toString().toLongOrNull() ?: 0L
            onConfirm(amt)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun formatDate(date: String): String {
        val parts = date.split("-")
        return if (parts.size == 3) "${parts[2]} ${getMonthName(parts[1])} ${parts[0]}" else date
    }

    private fun getMonthName(month: String): String = when (month) {
        "01" -> "JANUARY"; "02" -> "FEBRUARY"; "03" -> "MARCH"; "04" -> "APRIL"
        "05" -> "MAY"; "06" -> "JUNE"; "07" -> "JULY"; "08" -> "AUGUST"
        "09" -> "SEPTEMBER"; "10" -> "OCTOBER"; "11" -> "NOVEMBER"; "12" -> "DECEMBER"
        else -> ""
    }
}


