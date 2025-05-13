package com.example.corewallet.coresavings

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.corewallet.R


class CoreGoalDetail : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_goal_detail)
        window.statusBarColor = Color.parseColor("#0d5892")

    }


}