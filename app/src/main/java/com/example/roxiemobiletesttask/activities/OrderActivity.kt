package com.example.roxiemobiletesttask.activities

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.example.roxiemobiletesttask.R
import com.example.roxiemobiletesttask.models.Order
import com.example.roxiemobiletesttask.staticpackage.TextFunctions
import com.example.roxiemobiletesttask.databinding.ActivityOrderBinding

class OrderActivity : AppCompatActivity()
{
    private lateinit var parentLayout: ConstraintLayout

    private lateinit var binding: ActivityOrderBinding
    private lateinit var order: Order

    private lateinit var orderLayout: View
    private lateinit var vehicleLayout: View

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_order)
        val view = this.binding.root
        setContentView(view)


        if (loadOrder())//(::order.isInitialized)
        {
            println("THIS IS ORDER: ${this.order}")
            initViews()

            initActions()
        }
        else
        {
            TextFunctions.showText(this, "Order not found!")
            finish()
        }
    }

    private fun loadOrder(): Boolean
    {
        if (intent.hasExtra("order"))
        {
            val temporder = intent.getParcelableExtra<Order>("order")
            if (temporder != null)
                this.order = temporder
            this.binding.order = this.order
            return true
        }
        return false
    }


    private fun initViews() {
        this.parentLayout = findViewById(R.id.parent_layout)

        this.orderLayout = findViewById(R.id.order_layout)
        this.vehicleLayout = findViewById(R.id.vehicle_layout)
    }

    private fun initActions()
    {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        this.supportActionBar?.hide()
        this.actionBar?.hide()

        this.parentLayout.viewTreeObserver.addOnGlobalLayoutListener {

        }

    }

}