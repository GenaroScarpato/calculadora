package com.genaro.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DestinoAdapter(private val destinos: List<String>) :
    RecyclerView.Adapter<DestinoAdapter.DestinoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_destino, parent, false)
        return DestinoViewHolder(view)
    }

    override fun onBindViewHolder(holder: DestinoViewHolder, position: Int) {
        holder.bind(destinos[position])
    }

    override fun getItemCount(): Int = destinos.size

    inner class DestinoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val destinoTextView: TextView = itemView.findViewById(R.id.destinoTextView)

        fun bind(destino: String) {
            destinoTextView.text = destino
        }
    }
}
