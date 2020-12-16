package es.albaibs.ibscomanda.ventas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.albaibs.ibscomanda.R
import es.albaibs.ibscomanda.varios.ListaSalas

class SalasRvAdapter(var salas: MutableList<ListaSalas>, val context: Context, var listener: OnItemClickListener): RecyclerView.Adapter<SalasRvAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = salas[position]
        holder.bind(item, context)

        holder.itemView.setOnClickListener {
            notifyDataSetChanged()
            listener.onClick(it, salas[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        setOnItemClickListener(listener)
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_salas_list, parent, false))
    }

    override fun getItemCount(): Int {
        return salas.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onClick(view: View, data: ListaSalas)
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val nombre = itemView.findViewById(R.id.tvNombreSala) as TextView

        fun bind(sala: ListaSalas, context: Context) {
            nombre.text = sala.nombre
        }
    }
}