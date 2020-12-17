package es.albaibs.ibscomanda.ventas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.R
import es.albaibs.ibscomanda.varios.ListaMesas

class MesasRvAdapter(var mesas: MutableList<ListaMesas>, val context: Context, var listener: OnItemClickListener): RecyclerView.Adapter<MesasRvAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mesas[position]
        holder.bind(item, context)

        holder.itemView.setOnClickListener {
            notifyDataSetChanged()
            listener.onClick(it, mesas[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        setOnItemClickListener(listener)
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_mesas_list, parent, false))
    }

    override fun getItemCount(): Int {
        return mesas.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onClick(view: View, data: ListaMesas)
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val numero = itemView.findViewById(R.id.btnNumeroMesa) as Button

        fun bind(mesa: ListaMesas, context: Context) {
            numero.text = mesa.mesaId.toString()
        }
    }

}