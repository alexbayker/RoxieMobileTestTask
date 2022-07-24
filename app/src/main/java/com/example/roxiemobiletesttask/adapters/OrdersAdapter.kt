package com.example.roxiemobiletesttask.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.roxiemobiletesttask.R
import com.example.roxiemobiletesttask.databinding.ListOrderBinding
import com.example.roxiemobiletesttask.models.Order

class OrdersAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrdersAdapter.MyViewHolder>()
{
    class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview)
    {
        var binding: ListOrderBinding? = null

        init
        {
            binding = DataBindingUtil.bind(itemview)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MyViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.list_order, parent, false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        //holder.setIsRecyclable(false)
        holder.binding?.order = orders[position]
    }

    override fun getItemCount() = orders.size

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) = position
}