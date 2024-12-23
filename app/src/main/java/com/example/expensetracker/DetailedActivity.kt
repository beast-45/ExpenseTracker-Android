package com.example.expensetracker

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailedActivity : AppCompatActivity() {
    private lateinit var transaction : Transaction
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detailed)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        transaction = intent.getSerializableExtra("transaction") as Transaction

        val roottouch = findViewById<View>(R.id.rootView)
        roottouch.setOnClickListener()
        {
            this.window.decorView.clearFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken , 0)
        }




        val tbtn = findViewById<Button>(R.id.updateTransactionBtn)
        val linput = findViewById<TextInputEditText>(R.id.labelInput)
        val ainput = findViewById<TextInputEditText>(R.id.amountInput)
        val dinput = findViewById<TextInputEditText>(R.id.decriptionInput)

        val llayout = findViewById<TextInputLayout>(R.id.labelLayoutdet)
        val alayout = findViewById<TextInputLayout>(R.id.amountLayout)

        val btnclose = findViewById<ImageButton>(R.id.closebtn)

        if (transaction != null) {
            linput.setText(transaction.label)
        }
        if (transaction != null) {
            ainput.setText(transaction.amount.toString())
        }

        if (transaction != null) {
            dinput.setText(transaction.description)
        }




        linput.addTextChangedListener()
        {
            tbtn.visibility = View.VISIBLE
            if (it!!.count() > 0)
                llayout.error = null
        }
        ainput.addTextChangedListener()
        {
            tbtn.visibility = View.VISIBLE
            if (it!!.count() > 0)
                alayout.error = null
        }
        dinput.addTextChangedListener()
        {
            tbtn.visibility = View.VISIBLE
            if (it!!.count() > 0)
                dinput.error = null
        }



        tbtn.setOnClickListener()
        {
            val label = linput.text.toString()
            val description = dinput.text.toString()
            val amount = ainput.text.toString().toDoubleOrNull()

            if (label.isEmpty())
                llayout.error = "Please Enter a Valid Label"
            else if (amount == null)
                alayout.error = "Please Enter a Valid Amount"
            else {
                val transaction = Transaction(transaction.id, label, amount, description)
                update(transaction)
            }
        }

        btnclose.setOnClickListener()
        {
            finish()
        }
    }

    private fun update(transaction: Transaction) {
        val db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "transactions"
        ).build()

        GlobalScope.launch {
            db.transactionDao().update(transaction)
            finish()
        }
    }
}