package com.example.synapse.screen.util.adapter.fitness

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.synapse.R
import com.example.synapse.screen.util.adapter.HeartRates
import com.example.synapse.screen.util.adapter.Steps

class HeartRateForTheDayAdapter(private val stepsList : ArrayList<HeartRates>) : RecyclerView.Adapter<HeartRateForTheDayAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.single_view_heartrates_fortheday, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return stepsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val currentItem = stepsList[position]
        holder.itemTime.text = currentItem.time
        holder.itemHearts.text = currentItem.hearts
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var itemTime: TextView
        var itemHearts: TextView

        init{
            itemTime = itemView.findViewById(R.id.tvTimehearts)
            itemHearts = itemView.findViewById(R.id.tvHearts)
        }

    }
}