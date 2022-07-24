package com.example.roxiemobiletesttask.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.roxiemobiletesttask.R
import com.example.roxiemobiletesttask.adapters.OrdersAdapter
import com.example.roxiemobiletesttask.db.AppDatabase
import com.example.roxiemobiletesttask.models.Order
import com.example.roxiemobiletesttask.services.ClearCacheService
import com.example.roxiemobiletesttask.staticpackage.Constants
import com.example.roxiemobiletesttask.staticpackage.TextFunctions
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class MainActivity : AppCompatActivity()
{
    private lateinit var parentLayout: SwipeRefreshLayout
    private lateinit var nestedScrollView: NestedScrollView

    private lateinit var lastUpdateText: TextView
    private lateinit var ordersNotLoadedText: TextView

    private lateinit var ordersListView: RecyclerView
    private lateinit var ordersAdapter: OrdersAdapter
    private var orders = CopyOnWriteArrayList<Order>()

    private lateinit var loadingLayout: View
    private lateinit var downloadingBar: ProgressBar
    private lateinit var downloadingTitle: TextView

    private var firstLaunch = true

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    val ordersLoadCallback: (List<Order>) -> Unit = {
            list: List<Order> ->

        this.orders.clear()
        this.orders.addAll(list.sorted())

        this@MainActivity.runOnUiThread {
            this.ordersAdapter.notifyDataSetChanged()
            this.lastUpdateText.visibility = View.VISIBLE
            this.lastUpdateText.text = "${this@MainActivity.resources.getString(R.string.last_update_prefix)} ${Constants.LASTUPDATEFORMAT.format(Date())}"
            this.ordersNotLoadedText.visibility = View.GONE
        }

        TextFunctions.showText(this@MainActivity, "Orders info updated!")
    }

    @SuppressLint("NotifyDataSetChanged", "ResourceType")
    val ordersErrorCallback: () -> Unit = {
        this.orders.clear()
        this@MainActivity.runOnUiThread {
            this.ordersAdapter.notifyDataSetChanged()
            this.lastUpdateText.visibility = View.GONE
            this.lastUpdateText.text = ""
            this.ordersNotLoadedText.visibility = View.VISIBLE
            this.ordersNotLoadedText.text = this@MainActivity.resources.getString(R.string.orders_not_loaded)
        }

        TextFunctions.showText(this@MainActivity, "Error info updating!")
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

        initActions()
    }

    private fun initViews()
    {
        this.parentLayout         = findViewById(R.id.parent_layout)
        this.nestedScrollView     = findViewById(R.id.nested_scroll_view)

        this.lastUpdateText       = findViewById(R.id.last_update_text)
        this.ordersListView       = findViewById(R.id.orders_list_view)
        this.ordersNotLoadedText  = findViewById(R.id.orders_not_loaded_text)

        this.loadingLayout        = findViewById(R.id.loading_layout)
        this.downloadingBar       = findViewById(R.id.downloading_bar)
        this.downloadingTitle     = findViewById(R.id.downloading_title)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initActions()
    {
        Constants.getImageDirectory(this)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        this.supportActionBar?.hide()
        this.actionBar?.hide()

        this.ordersAdapter = OrdersAdapter(this.orders)

        this.ordersListView.layoutManager = LinearLayoutManager(this)
        this.ordersListView.adapter = this.ordersAdapter

        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.list_divider)!!)
        this.ordersListView.addItemDecoration(itemDecoration)

        this.parentLayout.setOnRefreshListener {
            Order.getOrdersFromServer(this@MainActivity, true, this.ordersLoadCallback, this.ordersErrorCallback)
            this.parentLayout.isRefreshing = false
        }

        this.parentLayout.viewTreeObserver.addOnGlobalLayoutListener {
            if (this.firstLaunch)
            {
                this.firstLaunch = false

                ClearCacheService.startAlarmManager(this@MainActivity)

                val database = AppDatabase.invoke(this@MainActivity).ordersDB()
                val count = database.getCount()

                if (count == 0)
                {
                    val defaultOrders =  Order.defaultOrders(this)
                    database.insertAll(defaultOrders)
                }

                val lastupdate = TextFunctions.loadText(Constants.LASTUPDATESHAREDPREFS, this@MainActivity)
                if (!TextFunctions.isLong(lastupdate) ||
                   (kotlin.math.abs(Date().time - Date(lastupdate.toLong()).time) > Constants.CONTENT_LIFETIME))
                    Order.getOrdersFromServer(this@MainActivity, true, this.ordersLoadCallback, this.ordersErrorCallback)
                else
                    this.ordersLoadCallback(database.getAll())

                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    //(ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)  != PackageManager.PERMISSION_GRANTED ||
                     //ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
                //{
                    /*if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        Dialogs.showPermissionsDescription(this@MainActivity)
                    else*/
                    //ActivityCompat.requestPermissions(
                        //this@MainActivity,
                        //arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        //Constants.MEMORY_PERMISSION)
                //}
                //else
                //{

                //}
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.MEMORY_PERMISSION) {
            var allPermissions = true
            for (i in permissions.indices)
                if ((permissions[i] == Manifest.permission.WRITE_EXTERNAL_STORAGE ||
                            permissions[i] == Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    grantResults[i] != PackageManager.PERMISSION_GRANTED)
                {
                    allPermissions = false
                    break
                }
            if (allPermissions)
            {

            }
            else
                TextFunctions.showText(this@MainActivity, "We can't update list without memory permission!")
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = this.currentFocus
            if (v is EditText) {
                v.clearFocus()
                hideKeyboard(v)
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun hideKeyboard(v: View?) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (v != null) {
            this@MainActivity.runOnUiThread {
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }
    }
}