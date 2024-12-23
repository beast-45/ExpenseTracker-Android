 package com.example.expensetracker

import android.content.ClipData.Item
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.arch.core.internal.SafeIterableMap.IteratorWithAdditions
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

 class MainActivity : AppCompatActivity() {
     private lateinit var deletedTransaction : Transaction
    private lateinit var transactions : List<Transaction>
    private lateinit var oldtransactions : List<Transaction>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var linearlayoutManager: LinearLayoutManager
    private lateinit var db : AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val rv = findViewById<RecyclerView>(R.id.recyclerview)

        transactions = arrayListOf()

        transactionAdapter = TransactionAdapter(transactions)
        linearlayoutManager = LinearLayoutManager(this)

        db =  Room.databaseBuilder(this ,
            AppDatabase::class.java ,
            "transactions").build()

        rv.apply{
            adapter = transactionAdapter
            layoutManager = linearlayoutManager
        }

        //swipe to remove
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0 , ItemTouchHelper.RIGHT)
        {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTransaction(transactions[viewHolder.adapterPosition])
            }
        }

        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(rv)


        val btnadd = findViewById<FloatingActionButton>(R.id.btnnewadd)

        btnadd.setOnClickListener()
        {
            val intent = Intent(this , AddTransaction::class.java)
            startActivity(intent)
        }

    }

     private fun fetchAll(){
         GlobalScope.launch {
             transactions = db.transactionDao().getAll()

             runOnUiThread{
                 updateDashboard()
                 transactionAdapter.setData(transactions)
             }
         }
     }

     private fun updateDashboard()
     {
         val totalAmount : Double = transactions.map{it.amount}.sum()
         val budgetAmount : Double = transactions.filter{it.amount>0}.map{it.amount}.sum()
         val expenseAmount = totalAmount - budgetAmount

         val bal = findViewById<TextView>(R.id.balance)
         val exp = findViewById<TextView>(R.id.expense)
         val bud = findViewById<TextView>(R.id.budget)

         bal.text = "₹%.2f".format(totalAmount)
         exp.text = "₹%.2f".format(expenseAmount)
         bud.text = "₹%.2f".format(budgetAmount)
         }

     private fun undoDelete(){
         GlobalScope.launch {
             db.transactionDao().insertall(deletedTransaction)
             transactions = oldtransactions
             runOnUiThread {
                 transactionAdapter.setData(transactions)
                 updateDashboard()
             }
         }
     }

     private fun showSnackbar()
     {
         val view = findViewById<View>(R.id.viewCoordinator)
         val snackbar = Snackbar.make(view , "Transcation Deleted." , Snackbar.LENGTH_LONG)
         snackbar.setAction("Undo"){
             undoDelete()
         }
             .setActionTextColor(ContextCompat.getColor(this , R.color.red))
             .setTextColor(ContextCompat.getColor(this , R.color.white))
             .show()
     }

     private fun deleteTransaction(transaction: Transaction)
     {
         deletedTransaction =  transaction
         oldtransactions = transactions

         GlobalScope.launch {
             db.transactionDao().delete(transaction)

             transactions = transactions.filter { it.id != transaction.id }
             runOnUiThread{
                 updateDashboard()
                 transactionAdapter.setData(transactions)
                 showSnackbar()
             }
         }
     }

     override fun onResume() {
         super.onResume()
         fetchAll()
     }
}