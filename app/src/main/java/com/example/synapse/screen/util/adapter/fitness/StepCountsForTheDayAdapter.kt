package com.example.synapse.screen.util.adapter.fitness

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.synapse.R
import com.example.synapse.screen.util.adapter.Steps

class StepCountsForTheDayAdapter(private val stepsList : ArrayList<Steps>) : RecyclerView.Adapter<StepCountsForTheDayAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.single_view_stepcounts_fortheday, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return stepsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val currentItem = stepsList[position]
        holder.itemTime.text = currentItem.time
        holder.itemSteps.text = currentItem.steps
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var itemTime: TextView
        var itemSteps: TextView

        init{
            itemTime = itemView.findViewById(R.id.tvTimeSteps)
            itemSteps = itemView.findViewById(R.id.tvSteps)
        }

    }
}