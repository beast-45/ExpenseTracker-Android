package com.example.expensetracker

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddTransaction : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_transaction)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val tbtn = findViewById<Button>(R.id.addTransactionBtn)
        val linput = findViewById<TextInputEditText>(R.id.labelInput)
        val ainput = findViewById<TextInputEditText>(R.id.amountInput)
        val dinput = findViewById<TextInputEditText>(R.id.decriptionInput)

        val llayout = findViewById<TextInputLayout>(R.id.labelLayout)
        val alayout = findViewById<TextInputLayout>(R.id.amountLayout)
        val dlayout = findViewById<TextInputLayout>(R.id.descriptionLayout)

        val btnclose = findViewById<ImageButton>(R.id.closebtn)

        linput.addTextChangedListener()
        {
            if(it!!.count()>0)
                llayout.error = null
        }
        ainput.addTextChangedListener()
        {
            if(it!!.count()>0)
                alayout.error = null
        }



        tbtn.setOnClickListener()
        {
            val label = linput .text.toString()
            val description = dinput.text.toString()
            val amount = ainput .text.toString().toDoubleOrNull()

            if(label.isEmpty())
                llayout.error = "Please Enter a Valid Label"

            else if(amount == null)
                alayout.error = "Please Enter a Valid Amount"
            else{
                val transaction =  Transaction(0 , label , amount , description)
                insert(transaction)
            }
        }

        btnclose.setOnClickListener()
        {
            finish()
        }
    }

    private fun insert(transaction: Transaction){
        val db =  Room.databaseBuilder(this ,
            AppDatabase::class.java ,
            "transactions").build()

        GlobalScope.launch {
            db.transactionDao().insertall(transaction)
            finish()
        }

    }
}